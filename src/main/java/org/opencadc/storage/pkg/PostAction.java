package org.opencadc.storage.pkg;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.auth.RunnableAction;
import ca.nrc.cadc.net.FileContent;
import ca.nrc.cadc.net.HttpGet;
import ca.nrc.cadc.net.HttpPost;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.rest.InlineContentHandler;
import ca.nrc.cadc.rest.SyncInput;
import ca.nrc.cadc.rest.SyncOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletResponse;
import net.canfar.storage.web.config.StorageConfiguration;
import net.canfar.storage.web.config.VOSpaceServiceConfig;
import net.canfar.storage.web.config.VOSpaceServiceConfigManager;
import org.apache.log4j.Logger;
import org.opencadc.storage.StorageAction;
import org.opencadc.vospace.VOS;
import org.opencadc.vospace.View;
import org.opencadc.vospace.transfer.Direction;
import org.opencadc.vospace.transfer.Protocol;
import org.opencadc.vospace.transfer.Transfer;
import org.opencadc.vospace.transfer.TransferWriter;

public class PostAction extends StorageAction {
    private static final Logger LOGGER = Logger.getLogger(PostAction.class);

    public PostAction() {
        super();
    }

    PostAction(
            final StorageConfiguration storageConfiguration,
            final VOSpaceServiceConfigManager voSpaceServiceConfigManager,
            final SyncInput syncInput,
            final SyncOutput syncOutput) {
        super(storageConfiguration, voSpaceServiceConfigManager);
        this.syncInput = syncInput;
        this.syncOutput = syncOutput;
    }

    @Override
    protected InlineContentHandler getInlineContentHandler() {
        return null;
    }

    @Override
    public void doAction() throws Exception {
        final List<URI> targetList = getTargetURIs();
        final String responseFormat = determineContentType();
        LOGGER.debug("Determined content type of " + responseFormat);

        if (LOGGER.isDebugEnabled()) {
            targetList.forEach(target -> LOGGER.debug("Sending target " + target));
        }
        final Subject currentSubject = getCurrentSubject(
                lookupEndpoint(getCurrentService().getResourceID(), Standards.VOSPACE_NODES_20, AuthMethod.TOKEN));
        final URL transferRunURL = getTransferRunURL(currentSubject, getTransferRunPayload(targetList, responseFormat));

        processDownload(transferRunURL, currentSubject);
    }

    void processDownload(final URL transferRunURL, final Subject currentSubject) throws Exception {
        if (this.getCurrentService().supportsDirectDownload()) {
            LOGGER.debug("Direct download supported, redirecting to " + transferRunURL);
            redirect(transferRunURL);
        } else {
            LOGGER.debug("Direct download not supported, proxying from " + transferRunURL);
            proxyDownload(transferRunURL, currentSubject);
        }
    }

    void redirect(final URL redirectURL) {
        this.syncOutput.setCode(HttpServletResponse.SC_MOVED_TEMPORARILY);
        this.syncOutput.addHeader("location", redirectURL.toExternalForm());
    }

    void proxyDownload(final URL transferRunURL, final Subject currentSubject) throws Exception {
        final HttpGet httpGet = new HttpGet(transferRunURL, true);
        Subject.doAs(currentSubject, (PrivilegedExceptionAction<Void>) () -> {
            httpGet.prepare();
            return null;
        });

        this.syncOutput.addHeader("content-disposition", httpGet.getResponseHeader("content-disposition"));
        this.syncOutput.addHeader("content-length", httpGet.getContentLength());
        this.syncOutput.addHeader("content-type", httpGet.getContentType());

        final byte[] buffer = new byte[8192];
        final InputStream inputStream = httpGet.getInputStream();
        final OutputStream outputStream = this.syncOutput.getOutputStream();
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.flush();
    }

    List<URI> getTargetURIs() {
        final List<String> requestedURIs = this.syncInput.getParameters("uri");

        if (requestedURIs == null || requestedURIs.isEmpty()) {
            throw new IllegalArgumentException("Nothing specified to download.");
        } else {
            return requestedURIs.stream().map(URI::create).collect(Collectors.toList());
        }
    }

    byte[] getTransferRunPayload(final List<URI> targetList, final String responseFormat) throws IOException {
        // Create the Transfer.
        final Transfer transfer = new Transfer(Direction.pullFromVoSpace);
        transfer.getTargets().addAll(targetList);
        transfer.version = VOS.VOSPACE_21;

        final Protocol protocol = new Protocol(VOS.PROTOCOL_HTTPS_GET);
        protocol.setSecurityMethod(Standards.SECURITY_METHOD_COOKIE);
        transfer.getProtocols().add(protocol);

        // Add package view to request using responseFormat provided
        final View packageView = new View(Standards.PKG_10);
        packageView.getParameters().add(new View.Parameter(VOS.PROPERTY_URI_FORMAT, responseFormat));
        transfer.setView(packageView);

        final TransferWriter transferWriter = new TransferWriter();
        final StringWriter sw = new StringWriter();
        transferWriter.write(transfer, sw);
        LOGGER.debug("transfer XML: " + sw);

        return sw.toString().getBytes();
    }

    private URL getTransferRunURL(final Subject currentSubject, final byte[] payload) {
        // POST the transfer to synctrans
        final VOSpaceServiceConfig currentService = getCurrentService();
        final FileContent fileContent = new FileContent(payload, "text/xml");
        final URL synctransServiceURL =
                lookupEndpoint(currentService.getResourceID(), Standards.VOSPACE_SYNC_21, AuthMethod.TOKEN);
        final HttpPost post = new HttpPost(synctransServiceURL, fileContent, false);

        Subject.doAs(currentSubject, new RunnableAction(post));
        return post.getRedirectURL();
    }

    private String determineContentType() {
        final String packageType = this.syncInput.getParameter("method");
        return PackageTypes.fromLabel(packageType).contentType;
    }

    enum PackageTypes {
        ZIP("ZIP Package", "application/zip", "zip"),
        TAR("TAR Package", "application/x-tar", "tar");

        final String label;
        final String contentType;
        final String fileExtension;

        PackageTypes(String label, String contentType, String fileExtension) {
            this.label = label;
            this.contentType = contentType;
            this.fileExtension = fileExtension;
        }

        static PackageTypes fromLabel(final String label) {
            return Arrays.stream(PackageTypes.values())
                    .filter(packageType -> packageType.label.equals(label))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No Package with label " + label));
        }
    }
}
