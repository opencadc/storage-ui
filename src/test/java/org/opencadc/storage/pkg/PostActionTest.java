package org.opencadc.storage.pkg;

import ca.nrc.cadc.rest.SyncInput;
import ca.nrc.cadc.rest.SyncOutput;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import javax.security.auth.Subject;
import net.canfar.storage.web.config.VOSpaceServiceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.xmlunit.assertj.XmlAssert;

public class PostActionTest {
    @Test
    public void testTransferRunXMLZIP() throws Exception {
        final PostAction postAction = new PostAction(null, null, null, null);

        final String responseFormat = PostAction.PackageTypes.ZIP.contentType;
        final URI[] targets = new URI[] {URI.create("vos://test1"), URI.create("vos://test2")};

        final String xmlOutput = new String(postAction.getTransferRunPayload(Arrays.asList(targets), responseFormat));
        final String expectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<vos:transfer xmlns:vos=\"http://www.ivoa.net/xml/VOSpace/v2.0\" version=\"2.1\">\n"
                + "  <vos:target>vos://test1</vos:target>\n"
                + "  <vos:target>vos://test2</vos:target>\n"
                + "  <vos:direction>pullFromVoSpace</vos:direction>\n"
                + "  <vos:view uri=\"vos://cadc.nrc.ca~vospace/CADC/std/Pkg-1.0\">\n"
                + "    <vos:param uri=\"ivo://ivoa.net/vospace/core#format\">application/zip</vos:param>\n"
                + "  </vos:view>\n"
                + "  <vos:protocol uri=\"ivo://ivoa.net/vospace/core#httpsget\">\n"
                + "    <vos:securityMethod uri=\"ivo://ivoa.net/sso#cookie\" />\n"
                + "  </vos:protocol>\n"
                + "  <vos:keepBytes>true</vos:keepBytes>\n"
                + "</vos:transfer>\n";

        XmlAssert.assertThat(xmlOutput).and(expectedXML).ignoreWhitespace().areSimilar();
    }

    @Test
    public void testTransferRunXMLTAR() throws Exception {
        final PostAction postAction = new PostAction(null, null, null, null);

        final String responseFormat = PostAction.PackageTypes.TAR.contentType;
        final URI[] targets = new URI[] {URI.create("vos://test/tar/1"), URI.create("vos://test/tar/2")};

        final String xmlOutput = new String(postAction.getTransferRunPayload(Arrays.asList(targets), responseFormat));
        final String expectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<vos:transfer xmlns:vos=\"http://www.ivoa.net/xml/VOSpace/v2.0\" version=\"2.1\">\n"
                + "  <vos:target>vos://test/tar/1</vos:target>\n"
                + "  <vos:target>vos://test/tar/2</vos:target>\n"
                + "  <vos:direction>pullFromVoSpace</vos:direction>\n"
                + "  <vos:view uri=\"vos://cadc.nrc.ca~vospace/CADC/std/Pkg-1.0\">\n"
                + "    <vos:param uri=\"ivo://ivoa.net/vospace/core#format\">application/x-tar</vos:param>\n"
                + "  </vos:view>\n"
                + "  <vos:protocol uri=\"ivo://ivoa.net/vospace/core#httpsget\">\n"
                + "    <vos:securityMethod uri=\"ivo://ivoa.net/sso#cookie\" />\n"
                + "  </vos:protocol>\n"
                + "  <vos:keepBytes>true</vos:keepBytes>\n"
                + "</vos:transfer>\n";

        XmlAssert.assertThat(xmlOutput).and(expectedXML).ignoreWhitespace().areSimilar();
    }

    @Test
    public void testGetTargetURIs() {
        final SyncInput mockSyncInput = Mockito.mock(SyncInput.class);
        final SyncOutput mockSyncOutput = Mockito.mock(SyncOutput.class);
        final PostAction postAction = new PostAction(null, null, mockSyncInput, mockSyncOutput);

        Mockito.when(mockSyncInput.getParameters("uri")).thenReturn(Arrays.asList("vos://test/1", "vos://test/2"));

        final URI[] actualURIs = postAction.getTargetURIs().toArray(new URI[0]);
        final URI[] expectedURIs = new URI[] {URI.create("vos://test/1"), URI.create("vos://test/2")};

        Assert.assertArrayEquals("Wrong URIs.", expectedURIs, actualURIs);
        Mockito.verify(mockSyncInput, Mockito.times(1)).getParameters("uri");
    }

    @Test
    public void testDirectDownload() throws Exception {
        final VOSpaceServiceConfig mockService = Mockito.mock(VOSpaceServiceConfig.class);

        Mockito.when(mockService.supportsDirectDownload()).thenReturn(true);
        final boolean[] expectedCalls = new boolean[] {false};

        final PostAction postAction = new PostAction(null, null, null, null) {
            @Override
            protected VOSpaceServiceConfig getCurrentService() {
                return mockService;
            }

            @Override
            void proxyDownload(URL transferRunURL, Subject currentSubject) {
                Assert.fail("Should not be called");
            }

            @Override
            void redirect(URL redirectURL) {
                expectedCalls[0] = true;
            }
        };

        postAction.processDownload(null, null);
        Assert.assertTrue("Should call redirect.", expectedCalls[0]);

        Mockito.verify(mockService, Mockito.times(1)).supportsDirectDownload();
    }

    @Test
    public void testProxyDownload() throws Exception {
        final VOSpaceServiceConfig mockService = Mockito.mock(VOSpaceServiceConfig.class);

        Mockito.when(mockService.supportsDirectDownload()).thenReturn(false);
        final boolean[] expectedCalls = new boolean[] {false};

        final PostAction postAction = new PostAction(null, null, null, null) {
            @Override
            protected VOSpaceServiceConfig getCurrentService() {
                return mockService;
            }

            @Override
            void proxyDownload(URL transferRunURL, Subject currentSubject) throws Exception {
                expectedCalls[0] = true;
            }

            @Override
            void redirect(URL redirectURL) {
                Assert.fail("Should not be called");
            }
        };

        postAction.processDownload(null, null);
        Assert.assertTrue("Should call proxyDownload.", expectedCalls[0]);

        Mockito.verify(mockService, Mockito.times(1)).supportsDirectDownload();
    }
}
