package net.canfar.storage.web.resources;

import static org.mockito.Mockito.*;

import ca.nrc.cadc.reg.client.RegistryClient;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.Subject;
import net.canfar.storage.web.StorageItemFactory;
import net.canfar.storage.web.config.VOSpaceServiceConfig;
import org.junit.Test;
import org.opencadc.vospace.*;
import org.opencadc.vospace.client.async.RecursiveDeleteNode;
import org.restlet.Context;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

public class StorageItemServerResourceTest extends AbstractServerResourceTest<StorageItemServerResource> {

    private static final VOSpaceServiceConfig TEST_SERVICE_CONFIG = new VOSpaceServiceConfig(
            "vault",
            URI.create("ivo://example.org/vault"),
            URI.create("vos://example.org~vault"),
            new VOSpaceServiceConfig.Features(),
            URI.create("https://example.com/groups"));

    @Test
    public void deleteDataNode() throws Exception {
        final VOSURI nodeURI = new VOSURI(URI.create(VOSPACE_NODE_URI_PREFIX + "/my/file.txt"));
        final DataNode dataNode = new DataNode("file.txt");

        final Map<String, Object> requestAttributes = new HashMap<>();
        requestAttributes.put("path", nodeURI.getPath());

        testSubject =
                new StorageItemServerResource(
                        null,
                        null,
                        new StorageItemFactory("/teststorage", TEST_SERVICE_CONFIG),
                        mockVOSpaceClient,
                        TEST_SERVICE_CONFIG) {
                    @Override
                    Subject getVOSpaceCallingSubject() {
                        return new Subject();
                    }

                    @Override
                    public Map<String, Object> getRequestAttributes() {
                        return requestAttributes;
                    }

                    @Override
                    public Context getContext() {
                        return mockContext;
                    }

                    @Override
                    public Response getResponse() {
                        return mockResponse;
                    }

                    @Override
                    RegistryClient getRegistryClient() {
                        return mockRegistryClient;
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    <T extends Node> T getNode(final Path nodePath, final VOS.Detail detail, final Integer limit)
                            throws ResourceException {
                        return (T) dataNode;
                    }
                };

        testSubject.deleteNode();

        verify(mockVOSpaceClient, times(1)).deleteNode(nodeURI.getPath());
    }

    @Test
    public void deleteEmptyContainerNode() throws Exception {
        final VOSURI nodeURI = new VOSURI(URI.create(VOSPACE_NODE_URI_PREFIX + "/my/empty_folder"));
        final ContainerNode emptyContainer = new ContainerNode("empty_folder");

        final Map<String, Object> requestAttributes = new HashMap<>();
        requestAttributes.put("path", nodeURI.getPath());

        testSubject =
                new StorageItemServerResource(
                        null,
                        null,
                        new StorageItemFactory("/teststorage", TEST_SERVICE_CONFIG),
                        mockVOSpaceClient,
                        TEST_SERVICE_CONFIG) {
                    @Override
                    Subject getVOSpaceCallingSubject() {
                        return new Subject();
                    }

                    @Override
                    public Map<String, Object> getRequestAttributes() {
                        return requestAttributes;
                    }

                    @Override
                    public Context getContext() {
                        return mockContext;
                    }

                    @Override
                    public Response getResponse() {
                        return mockResponse;
                    }

                    @Override
                    RegistryClient getRegistryClient() {
                        return mockRegistryClient;
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    <T extends Node> T getNode(final Path nodePath, final VOS.Detail detail, final Integer limit)
                            throws ResourceException {
                        return (T) emptyContainer;
                    }
                };

        testSubject.deleteNode();

        verify(mockVOSpaceClient, times(1)).deleteNode(nodeURI.getPath());
    }

    @Test
    public void deleteNonEmptyContainerNode() throws Exception {
        final VOSURI nodeURI = new VOSURI(URI.create(VOSPACE_NODE_URI_PREFIX + "/my/full_folder"));
        final ContainerNode nonEmptyContainer = new ContainerNode("full_folder");
        nonEmptyContainer.getNodes().add(new DataNode("child.txt"));

        final RecursiveDeleteNode mockRecursiveDelete = mock(RecursiveDeleteNode.class);
        when(mockVOSpaceClient.createRecursiveDelete(nodeURI)).thenReturn(mockRecursiveDelete);

        final Map<String, Object> requestAttributes = new HashMap<>();
        requestAttributes.put("path", nodeURI.getPath());

        testSubject =
                new StorageItemServerResource(
                        null,
                        null,
                        new StorageItemFactory("/teststorage", TEST_SERVICE_CONFIG),
                        mockVOSpaceClient,
                        TEST_SERVICE_CONFIG) {
                    @Override
                    Subject getVOSpaceCallingSubject() {
                        return new Subject();
                    }

                    @Override
                    public Map<String, Object> getRequestAttributes() {
                        return requestAttributes;
                    }

                    @Override
                    VOSURI getCurrentItemURI() {
                        return nodeURI;
                    }

                    @Override
                    public Context getContext() {
                        return mockContext;
                    }

                    @Override
                    public Response getResponse() {
                        return mockResponse;
                    }

                    @Override
                    RegistryClient getRegistryClient() {
                        return mockRegistryClient;
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    <T extends Node> T getNode(final Path nodePath, final VOS.Detail detail, final Integer limit)
                            throws ResourceException {
                        return (T) nonEmptyContainer;
                    }
                };

        testSubject.deleteNode();

        verify(mockVOSpaceClient, never()).deleteNode(anyString());
        verify(mockVOSpaceClient, times(1)).createRecursiveDelete(nodeURI);
        verify(mockRecursiveDelete, times(1)).setMonitor(false);
        verify(mockRecursiveDelete, times(1)).run();
    }
}
