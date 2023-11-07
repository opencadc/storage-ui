/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2016.                            (c) 2016.
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


import net.canfar.storage.web.RegexFileValidator;
import net.canfar.storage.web.UploadOutputStreamWrapper;
import net.canfar.storage.web.UploadVerifier;
import net.canfar.storage.web.config.VOSpaceServiceConfigMgr;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.vos.*;

import java.util.concurrent.ConcurrentMap;

import org.apache.commons.fileupload.FileItemStream;
import org.mockito.Mockito;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import java.security.MessageDigest;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.restlet.representation.EmptyRepresentation;

import static org.junit.Assert.*;


public class FileItemServerResourceTest extends AbstractServerResourceTest<FileItemServerResource> {
    @Test
    public void uploadFileItem() throws Exception {
        final Map<String, Object> requestAttributes = new HashMap<>();
        final VOSURI parentURI = new VOSURI(URI.create("vos://cadc.nrc.ca!vault/parent/sub"));
        final VOSURI expectedURI = new VOSURI(URI.create("vos://cadc.nrc.ca!vault/parent/sub/MYUPLOADFILE.txt"));
        final DataNode expectedDataNode = new DataNode(expectedURI);
        final String data = "MYUPLOADDATA";
        final byte[] dataBytes = data.getBytes();
        final InputStream inputStream = new ByteArrayInputStream(dataBytes);

        final List<NodeProperty> propertyList = new ArrayList<>();

        propertyList.add(new NodeProperty("ivo://ivoa.net/vospace/core#length", "" + dataBytes.length));
        propertyList.add(new NodeProperty("ivo://ivoa.net/vospace/core#MD5",
                                          new String(MessageDigest.getInstance("MD5").digest(dataBytes))));

        expectedDataNode.setProperties(propertyList);

        requestAttributes.put("path", "my/file.txt");
        final ConcurrentMap<String, Object> mockContextAttributes =
                new ConcurrentHashMap<>();

        mockContextAttributes.put(VOSpaceServiceConfigMgr.VOSPACE_SERVICE_NAME_KEY, "vault");
        mockContextAttributes.put(VOSpaceServiceConfigMgr.KEY_BASE + ".vault" + VOSpaceServiceConfigMgr.NODE_URI_KEY,
                                  "vault");

        Mockito.when(mockContext.getAttributes()).thenReturn(mockContextAttributes);
        Mockito.when(mockRequest.getEntity()).thenReturn(new EmptyRepresentation());
        Mockito.when(mockServletContext.getContextPath()).thenReturn("/teststorage");

        testSubject = new FileItemServerResource(mockVOSpaceClient, new UploadVerifier(), new RegexFileValidator()) {
            @Override
            public Response getResponse() {
                return mockResponse;
            }

            /**
             * Returns the current context.
             *
             * @return The current context.
             */
            @Override
            public Context getContext() {
                return mockContext;
            }

            @Override
            ServletContext getServletContext() {
                return mockServletContext;
            }

            @Override
            RegistryClient getRegistryClient() {
                return mockRegistryClient;
            }

            @Override
            public Request getRequest() {
                return mockRequest;
            }

            @Override
            public String getVospaceNodeUriPrefix() {
                return VOSPACE_NODE_URI_PREFIX;
            }

            /**
             * Returns the request attributes.
             *
             * @return The request attributes.
             * @see Request#getAttributes()
             */
            @Override
            public Map<String, Object> getRequestAttributes() {
                return requestAttributes;
            }

            @Override
            VOSURI getCurrentItemURI() {
                return parentURI;
            }


            /**
             * Abstract away the Transfer stuff.  It's cumbersome.
             *
             * @param outputStreamWrapper The OutputStream wrapper.
             * @param dataNode            The node to upload.
             */
            @Override
            void upload(UploadOutputStreamWrapper outputStreamWrapper, DataNode dataNode) {
                // Do nothing.
            }

            @Override
            <T> T executeSecurely(PrivilegedExceptionAction<T> runnable) throws Exception {
                return runnable.run();
            }
        };

        final FileItemStream mockFileItemStream = Mockito.mock(FileItemStream.class);

        Mockito.when(mockVOSpaceClient.getNode("/parent/sub/MYUPLOADFILE.txt", "limit=0&detail=min"))
                .thenThrow(new NodeNotFoundException("No such node."));
        Mockito.when(mockVOSpaceClient.createNode(expectedDataNode, false)).thenReturn(
                expectedDataNode);
        Mockito.when(mockFileItemStream.getName()).thenReturn("MYUPLOADFILE.txt");
        Mockito.when(mockFileItemStream.openStream()).thenReturn(inputStream);
        Mockito.when(mockFileItemStream.getContentType()).thenReturn("text/plain");

        final VOSURI resultURI = testSubject.upload(mockFileItemStream);

        Mockito.verify(mockContext, Mockito.times(2)).getAttributes();
        Mockito.verify(mockRequest, Mockito.atMostOnce()).getEntity();
        Mockito.verify(mockServletContext, Mockito.atMostOnce()).getContextPath();

        Mockito.verify(mockVOSpaceClient, Mockito.atMostOnce()).getNode("/parent/sub/MYUPLOADFILE.txt",
                                                                        "limit=0&detail=min");
        Mockito.verify(mockVOSpaceClient, Mockito.atMostOnce()).createNode(expectedDataNode, false);
        Mockito.verify(mockFileItemStream, Mockito.atMostOnce()).getName();
        Mockito.verify(mockFileItemStream, Mockito.atMostOnce()).openStream();
        Mockito.verify(mockFileItemStream, Mockito.atMostOnce()).getContentType();

        assertEquals("End URI is wrong.", expectedURI, resultURI);
    }
}
