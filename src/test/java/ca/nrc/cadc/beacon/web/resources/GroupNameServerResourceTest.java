package ca.nrc.cadc.beacon.web.resources;

import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.vos.ContainerNode;
import ca.nrc.cadc.vos.LinkNode;
import ca.nrc.cadc.vos.VOSURI;
import org.json.JSONObject;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.verify;

/**
 * Created by hjeeves on 2017-02-07.
 */
public class GroupNameServerResourceTest
        extends AbstractServerResourceTest<GroupNameServerResource> {
    @Test
    public void getGroupNames() throws Exception {

        expect(mockServletContext.getContextPath()).andReturn("/teststorage").once();

//        mockResponse.redirectTemporary(
//                "/servletpath/list/other/dir/my/dir");
//        expectLastCall().once();

//
//        final JSONObject sourceJSON = new JSONObject("{\"link_name\":\"MY_LINK\","
//                + "\"link_url\":\"http://gohere.com/to/see\"}");

//        final JsonRepresentation payload = new JsonRepresentation(sourceJSON);

        final Map<String, Object> attributes = new HashMap<>();

//        attributes.put(VOSpaceApplication.GMS_SERVICE_PROPERTY_KEY, "curr/dir/MY_LINK");

//        mockResponse.setStatus(Status.SUCCESS_CREATED);
//        expectLastCall().once();

//        replay(mockVOSpaceClient, mockServletContext, mockResponse);

        testSubject = new GroupNameServerResource() {
            @Override
            ServletContext getServletContext() {
                return mockServletContext;
            }

            @Override
            RegistryClient getRegistryClient() {
                return mockRegistryClient;
            }

            /**
             * Returns the request attributes.
             *
             * @return The request attributes.
             * @see Request#getAttributes()
             */
            @Override
            public Map<String, Object> getRequestAttributes()
            {
                return attributes;
            }

            /**
             * Returns the handled response.
             *
             * @return The handled response.
             */
            @Override
            public Response getResponse() {
                return mockResponse;
            }

        };

//        Representation groupNames = testSubject.getGroupNames();

//        verify(mockVOSpaceClient, mockServletContext, mockResponse);
    };
}

