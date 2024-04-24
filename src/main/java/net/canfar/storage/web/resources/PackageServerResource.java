/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2022.                            (c) 2022.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                       <http://www.gnu.org/licenses/>.
 *
 *
 ************************************************************************
 */

package net.canfar.storage.web.resources;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.auth.RunnableAction;
import ca.nrc.cadc.net.FileContent;
import ca.nrc.cadc.net.HttpGet;
import ca.nrc.cadc.net.HttpPost;
import ca.nrc.cadc.reg.Standards;
import net.canfar.storage.web.config.VOSpaceServiceConfig;
import org.opencadc.vospace.client.VOSpaceClient;
import org.opencadc.vospace.VOS;
import org.opencadc.vospace.View;

import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.opencadc.vospace.transfer.Direction;
import org.opencadc.vospace.transfer.Protocol;
import org.opencadc.vospace.transfer.Transfer;
import org.opencadc.vospace.transfer.TransferWriter;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import javax.security.auth.Subject;


public class PackageServerResource extends StorageItemServerResource {
    private static final Logger LOGGER = Logger.getLogger(PackageServerResource.class);

    /**
     * Empty constructor needed for Restlet to manage it.
     */
    public PackageServerResource() {
    }

    PackageServerResource(final VOSpaceClient voSpaceClient, final VOSpaceServiceConfig serviceConfig) {
        super(voSpaceClient, serviceConfig);
    }

    @Get("json")
    public Representation notSupported() {
        throw new UnsupportedOperationException("GET not supported.");
    }

    @Post
    public Representation getPackage(final Representation payload) throws Exception {
        final Form form = new Form(payload);
        final String responseFormat = determineContentType(form);
        final String[] uris = form.getValuesArray("uri", true);

        if (uris == null || uris.length == 0) {
            throw new IllegalArgumentException("Nothing specified to download.");
        } else {
            LOGGER.debug("Determined content type of " + responseFormat);
            // build target list to add to transfer
            final List<URI> targetList = Arrays.stream(uris).map(URI::create).collect(Collectors.toList());

            if (LOGGER.isDebugEnabled()) {
                targetList.forEach(target -> LOGGER.debug("Sending target " + target));
            }

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

            final Subject currentSubject = getCurrentSubject();
            final URL dataURL = getRedirect(currentSubject, sw.toString().getBytes());

            if (dataURL == null) {
                throw new IllegalArgumentException("No package endpoint available.");
            } else {
                return new OutputRepresentation(new MediaType(responseFormat)) {
                    @Override
                    public void write(OutputStream outputStream) {
                        final HttpGet httpGet = new HttpGet(dataURL, outputStream);
                        Subject.doAs(currentSubject, new RunnableAction(httpGet));
                    }
                };
            }
        }
    }

    private URL getRedirect(final Subject currentSubject, final byte[] payload) {
        // POST the transfer to synctrans
        final FileContent fileContent = new FileContent(payload, "text/xml");
        final URL synctransServiceURL = lookupEndpoint(this.currentService.getResourceID(), Standards.VOSPACE_SYNC_21,
                                                       AuthMethod.ANON);
        final HttpPost post = new HttpPost(synctransServiceURL, fileContent, false);

        Subject.doAs(currentSubject, new RunnableAction(post));
        return post.getRedirectURL();
    }

    private String determineContentType(final Form form) {
        final String packageType = form.getFirst("method").getValue();
        return PackageTypes.fromLabel(packageType).contentType;
    }

    private enum PackageTypes {
        ZIP("ZIP Package", "application/zip"),
        TAR("TAR Package", "application/x-tar");

        final String label;
        final String contentType;

        PackageTypes(String label, String contentType) {
            this.label = label;
            this.contentType = contentType;
        }

        static PackageTypes fromLabel(final String label) {
            return Arrays.stream(values()).filter(packageType -> packageType.label.equals(label))
                         .findFirst()
                         .orElseThrow(() -> new NoSuchElementException("No Package with label " + label));
        }
    }
}
