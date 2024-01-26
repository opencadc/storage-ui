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

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.auth.SSOCookieCredential;
import ca.nrc.cadc.date.DateUtil;
import ca.nrc.cadc.net.HttpPost;
import ca.nrc.cadc.net.NetUtil;
import ca.nrc.cadc.net.ResourceAlreadyExistsException;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.web.selenium.AbstractWebApplicationIntegrationTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.opencadc.vospace.ContainerNode;
import org.opencadc.vospace.NodeProperty;
import org.opencadc.vospace.VOS;
import org.opencadc.vospace.VOSURI;
import org.opencadc.vospace.client.VOSpaceClient;

import javax.security.auth.Subject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


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
        nodeResourceURI = URI.create("vos://" + resourceID.getHost() + "~" + resourceID.getPath().split("/")[1]);
        LOGGER.info("Using Node URI Prefix: " + nodeResourceURI);

        defaultVOSpaceSvc = systemProperties.getProperty("test.default_vospace");
        altVOSpaceSvc = systemProperties.getProperty("test.alt_vospace");
        altHomeDir = systemProperties.contains("test.alt_home_directory")
                     ? systemProperties.getProperty("test.alt_home_directory")
                     : String.format("/home/%s", AbstractWebApplicationIntegrationTest.username);

        final VOSpaceClient voSpaceClient = new VOSpaceClient(resourceID, false);
        final ContainerNode containerNode = new ContainerNode(UserStorageBaseTest.getTestDirectoryName());
        testDirectoryPath = "/" + AbstractWebApplicationIntegrationTest.username + "/" + containerNode.getName();
        LOGGER.info("Attempting to create " + containerNode.getName());
        try {
            final Subject subject = UserStorageBaseTest.getCookieSubject(resourceID);
            try {
                Subject.doAs(subject, (PrivilegedExceptionAction<Void>) () -> {
                    final ContainerNode userSpace = new ContainerNode(AbstractWebApplicationIntegrationTest.username);
                    userSpace.getProperties().add(new NodeProperty(VOS.PROPERTY_URI_QUOTA,
                                                                   Long.toString(1024 * 1024 * 1024)));
                    voSpaceClient.createNode(
                            new VOSURI(UserStorageBaseTest.createTestDirectoryURI(
                                    nodeResourceURI, "/" + AbstractWebApplicationIntegrationTest.username)),
                            userSpace);
                    return null;
                });
            } catch (PrivilegedActionException privilegedActionException) {
                if (privilegedActionException.getException() instanceof ResourceAlreadyExistsException) {
                    LOGGER.info("Test folder " + "/" + AbstractWebApplicationIntegrationTest.username
                                + " already exists.");
                } else {
                    throw privilegedActionException;
                }
            }

            Subject.doAs(subject, (PrivilegedExceptionAction<Void>) () -> {
                voSpaceClient.createNode(
                        new VOSURI(UserStorageBaseTest.createTestDirectoryURI(nodeResourceURI, testDirectoryPath)),
                        containerNode);
                return null;
            });
        } catch (PrivilegedActionException privilegedActionException) {
            final Exception cause = privilegedActionException.getException();
            throw new RuntimeException(cause.getMessage(), cause);
        }
    }

    private static String getTestDirectoryName() {
        final Properties systemProperties = System.getProperties();
        if (systemProperties.containsKey("test.directory.name")) {
            return systemProperties.getProperty("test.directory.name");
        } else {
            return UserStorageBaseTest.generateAlphaNumeric();
        }
    }

    private static URI createTestDirectoryURI(final URI nodeResourceID, final String containerNodePath) {
        return URI.create(nodeResourceID + containerNodePath);
    }

    private static Subject getCookieSubject(final URI resourceID) {
        final RegistryClient registryClient = new RegistryClient();
        LOGGER.info("Looking up VOSpace URL with " + resourceID);
        final URL vospaceURL = registryClient.getServiceURL(resourceID, Standards.VOSPACE_NODES_20, AuthMethod.CERT);
        LOGGER.info("Looking up VOSpace URL with " + resourceID + ": Done -> (" + vospaceURL + ")");
        final URL oldLoginServiceURL = registryClient.getServiceURL(URI.create("ivo://cadc.nrc.ca/gms"),
                                                                    Standards.UMS_LOGIN_01, AuthMethod.ANON);
        final URL newLoginServiceURL = registryClient.getServiceURL(URI.create("ivo://cadc.nrc.ca/gms"),
                                                                    Standards.UMS_LOGIN_10, AuthMethod.ANON);
        final URL loginServiceURL = newLoginServiceURL == null ? oldLoginServiceURL : newLoginServiceURL;

        final String cookieValue = UserStorageBaseTest.getCookieValue(loginServiceURL);
        final Calendar expiryCalendar = Calendar.getInstance(DateUtil.UTC);
        expiryCalendar.add(Calendar.MINUTE, 10);

        try {
            final SSOCookieCredential cookieCredential =
                    new SSOCookieCredential(cookieValue, NetUtil.getDomainName(vospaceURL), expiryCalendar.getTime());
            final Subject subject = new Subject();
            subject.getPublicCredentials().add(cookieCredential);
            subject.getPublicCredentials().add(AuthMethod.COOKIE);

            return subject;
        } catch (IOException ioException) {
            throw new RuntimeException(ioException.getMessage(), ioException);
        }
    }

    private static String getCookieValue(URL loginServiceURL) {
        final Map<String, Object> payload = new HashMap<>();
        payload.put("username", AbstractWebApplicationIntegrationTest.username);
        payload.put("password", AbstractWebApplicationIntegrationTest.password);

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final HttpPost httpPost = new HttpPost(loginServiceURL, payload, byteArrayOutputStream);
        httpPost.run();

        return new String(byteArrayOutputStream.toByteArray(), StandardCharsets.ISO_8859_1);
    }

    /**
     * Generate an ASCII string, replacing the '\' and '+' characters with
     * underscores to keep them URL friendly.
     *
     * @return An ASCII string of 16.
     */
    protected static String generateAlphaNumeric() {
        return generateAlphaNumeric(16);
    }

    /**
     * Generate an ASCII string, replacing the '\' and '+' characters with
     * underscores to keep them URL friendly.
     *
     * @param length The desired length of the generated string.
     * @return An ASCII string of the given length.
     */
    protected static String generateAlphaNumeric(final int length) {
        return RandomStringUtils.random(length, 0, SEED_CHARS.length, false,
                                        false, SEED_CHARS);
    }

    /**
     * Log in and return new page instance.
     *
     * @param userPage  The current page.
     * @return UserStorageBrowserPage instance with user logged in
     * @throws Exception if login fails
     */
    protected FolderPage loginTest(final FolderPage userPage) throws Exception {
        // Assumption: username and password have been sanely populated during test initialization
        final FolderPage authPage = userPage.doLogin(username, password);

        // Possibly unnecessary, also possibly prudent.
        waitFor(3);
        verifyTrue(authPage.isLoggedIn());
        System.out.println("logged in");

        return authPage;
    }

    protected static String[] parseTestDirPath(String testDir) {
        String[] parsedPath = testDir.split("/");

        for (String s : parsedPath) {
            LOGGER.debug("path:" + s);
        }
        return parsedPath;
    }
}
