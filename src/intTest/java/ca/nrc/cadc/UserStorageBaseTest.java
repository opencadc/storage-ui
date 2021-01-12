/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2020.                            (c) 2020.
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

package ca.nrc.cadc;

import ca.nrc.cadc.web.selenium.AbstractWebApplicationIntegrationTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;


public class UserStorageBaseTest extends AbstractWebApplicationIntegrationTest {
    private static final Logger log =
        Logger.getLogger(UserStorageBaseTest.class);

    private static final char[] SEED_CHARS;
    protected static String testDirectory;

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

    public UserStorageBaseTest() throws Exception {
        super();
        testDirectory = System.getProperty("test.directory");
    }


    /**
     * Generate an ASCII string, replacing the '\' and '+' characters with
     * underscores to keep them URL friendly.
     *
     * @return An ASCII string of 16.
     */
    protected String generateAlphaNumeric() {
        return generateAlphaNumeric(16);
    }

    /**
     * Generate an ASCII string, replacing the '\' and '+' characters with
     * underscores to keep them URL friendly.
     *
     * @param length The desired length of the generated string.
     * @return An ASCII string of the given length.
     */
    protected String generateAlphaNumeric(final int length) {
        return RandomStringUtils.random(length, 0, SEED_CHARS.length, false,
                                        false, SEED_CHARS);
    }

    /**
     * Log in and return new page instance.
     * @param userPage
     * @return UserStorageBrowserPage instance with user logged in
     * @throws Exception if login fails
     */
    protected UserStorageBrowserPage loginTest(final UserStorageBrowserPage userPage) throws Exception {
        // Assumption: username and password have been sanely populated during test initialization
        final UserStorageBrowserPage authPage = userPage.doLogin(username, password);

        // Possibly unnecessary, also possibly prudent.
        waitFor(3);
        verifyTrue(authPage.isLoggedIn());
        System.out.println("logged in");

        return authPage;
    }

    protected UserStorageBrowserPage cleanup(final UserStorageBrowserPage userPage, final String workingDir) throws Exception {
        // Nav up one level & delete working folder as well

        userPage.enterSearch(workingDir);
        userPage.clickCheckboxForRow(1);
        UserStorageBrowserPage newPage = userPage.deleteFolder();

        // verify the folder is no longer there
        newPage.enterSearch(workingDir);
        verifyTrue(newPage.isTableEmpty());
        return newPage;
    }

    protected String[] parseTestDirPath(String testDir) throws Exception {
        String[] parsedPath = testDir.split("/");

        for (String s: parsedPath) {
            log.debug("path:" + s);
        }
        return parsedPath;
    }
}
