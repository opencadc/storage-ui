/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2021.                            (c) 2021.
 * National Research Council            Conseil national de recherches
 * Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 * All rights reserved                  Tous droits reserves
 *
 * NRC disclaims any warranties         Le CNRC denie toute garantie
 * expressed, implied, or statu-        enoncee, implicite ou legale,
 * tory, of any kind with respect       de quelque nature que se soit,
 * to the software, including           concernant le logiciel, y com-
 * without limitation any war-          pris sans restriction toute
 * ranty of merchantability or          garantie de valeur marchande
 * fitness for a particular pur-        ou de pertinence pour un usage
 * pose.  NRC shall not be liable       particulier.  Le CNRC ne
 * in any event for any damages,        pourra en aucun cas etre tenu
 * whether direct or indirect,          responsable de tout dommage,
 * special or general, consequen-       direct ou indirect, particul-
 * tial or incidental, arising          ier ou general, accessoire ou
 * from the use of the software.        fortuit, resultant de l'utili-
 *                                      sation du logiciel.
 *
 *
 *
 *
 *
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package net.canfar;

import ca.nrc.cadc.net.ResourceAlreadyExistsException;
import ca.nrc.cadc.web.selenium.AbstractWebApplicationIntegrationTest;
import java.net.URI;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import javax.security.auth.Subject;
import org.apache.log4j.Logger;
import org.opencadc.storage.util.StorageUtil;
import org.opencadc.vospace.ContainerNode;
import org.opencadc.vospace.NodeProperty;
import org.opencadc.vospace.VOS;
import org.opencadc.vospace.VOSURI;
import org.opencadc.vospace.client.VOSpaceClient;

public class UserStorageBaseTest extends AbstractWebApplicationIntegrationTest {
    private static final Logger LOGGER = Logger.getLogger(UserStorageBaseTest.class);

    private static final char[] SEED_CHARS;
    protected String testDirectoryPath;
    protected String defaultVOSpaceSvc;
    protected String altVOSpaceSvc;
    protected String altHomeDir;

    protected URI nodeResourceURI;

    static {
        final StringBuilder chars = new StringBuilder(128);

        for (char c = 'a'; c <= 'z'; c++) {
            chars.append(c);
        }

        for (char c = 'A'; c <= 'Z'; c++) {
            chars.append(c);
        }

        for (char c = '0'; c <= '9'; c++) {
            chars.append(c);
        }

        chars.append("_-()=+!,:@*$.");

        SEED_CHARS = chars.toString().toCharArray();
    }

    public UserStorageBaseTest() {
        super();
        final Properties systemProperties = System.getProperties();
        final String resourceIDString = systemProperties.getProperty("resource.id");
        final URI resourceID = URI.create(resourceIDString);
        final String homeRoot = systemProperties.getProperty("home.root", "");

        nodeResourceURI = URI.create(
                "vos://" + resourceID.getHost() + "~" + resourceID.getPath().split("/")[1]);
        LOGGER.info("Using Node URI Prefix: " + nodeResourceURI);

        defaultVOSpaceSvc = systemProperties.getProperty("test.default_vospace");
        altVOSpaceSvc = systemProperties.getProperty("test.alt_vospace");
        altHomeDir = systemProperties.contains("test.alt_home_directory")
                ? systemProperties.getProperty("test.alt_home_directory")
                : String.format(homeRoot + "/%s", AbstractWebApplicationIntegrationTest.username);

        final VOSpaceClient voSpaceClient = new VOSpaceClient(resourceID, false);
        final ContainerNode containerNode = new ContainerNode(StorageUtil.getTestDirectoryName());
        containerNode.getProperties().add(new NodeProperty(VOS.PROPERTY_URI_ISPUBLIC, Boolean.toString(true)));
        containerNode.isPublic = true;

        testDirectoryPath =
                homeRoot + "/" + AbstractWebApplicationIntegrationTest.username + "/" + containerNode.getName();
        LOGGER.info("Attempting to create " + containerNode.getName());
        try {
            final Subject subject = StorageUtil.getCurrentUser(resourceID, nodeResourceURI);
            try {
                Subject.doAs(subject, (PrivilegedExceptionAction<Void>) () -> {
                    final ContainerNode userSpace = new ContainerNode(AbstractWebApplicationIntegrationTest.username);
                    userSpace
                            .getProperties()
                            .add(new NodeProperty(VOS.PROPERTY_URI_QUOTA, Long.toString(1024 * 1024 * 1024)));
                    voSpaceClient.createNode(
                            new VOSURI(StorageUtil.createTestDirectoryURI(
                                    nodeResourceURI, homeRoot + "/" + AbstractWebApplicationIntegrationTest.username)),
                            userSpace);
                    return null;
                });
            } catch (PrivilegedActionException privilegedActionException) {
                if (privilegedActionException.getException() instanceof ResourceAlreadyExistsException) {
                    LOGGER.info("Test folder " + homeRoot + "/" + AbstractWebApplicationIntegrationTest.username
                            + " already exists.");
                } else {
                    throw privilegedActionException;
                }
            }

            Subject.doAs(subject, (PrivilegedExceptionAction<Void>) () -> {
                voSpaceClient.createNode(
                        new VOSURI(StorageUtil.createTestDirectoryURI(nodeResourceURI, testDirectoryPath)),
                        containerNode);
                return null;
            });
        } catch (PrivilegedActionException privilegedActionException) {
            final Exception cause = privilegedActionException.getException();
            throw new RuntimeException(cause.getMessage(), cause);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    /**
     * Log in and return new page instance.
     *
     * @param userPage The current page.
     * @return UserStorageBrowserPage instance with user logged in
     * @throws Exception if login fails
     */
    protected FolderPage login(final FolderPage userPage) throws Exception {
        // Assumption: username and password have been sanely populated during test initialization
        final FolderPage authPage = userPage.doLogin(username, password);

        // Possibly unnecessary, also possibly prudent.
        waitFor(3);
        verifyTrue(authPage.isLoggedIn());
        System.out.println("logged in");

        return authPage;
    }
}
