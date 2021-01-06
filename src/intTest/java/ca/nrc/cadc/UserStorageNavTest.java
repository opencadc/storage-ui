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
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */

package ca.nrc.cadc;

import ca.nrc.cadc.util.Log4jInit;
import ca.nrc.cadc.util.StringUtil;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;


public class UserStorageNavTest extends UserStorageBaseTest {
    private static final Logger log =
        Logger.getLogger(UserStorageNavTest.class);

    static {
        Log4jInit.setLevel("ca.nrc.cadc", Level.DEBUG);
    }

    public UserStorageNavTest() throws Exception {
        super();
    }


    @Test
    public void runTest() throws Exception {
        try {
            navUserStorage();
        } catch (Exception e) {
            captureScreenShot(UserStorageNavTest.class.getName() + ".navUserStorage");
            throw e;
        }
    }

    private void navUserStorage() throws Exception {
        log.info("Visiting: " + webURL + testDirectory);

        final String workingDirectoryName = UserStorageNavTest.class.getSimpleName() + "_" + generateAlphaNumeric();
        UserStorageBrowserPage userStoragePage = goTo(testDirectory, null, UserStorageBrowserPage.class);

        String[] testDirPath = parseTestDirPath(testDirectory);

        if (userStoragePage.isMainPage()) {
            userStoragePage.waitForStorageLoad();
        }

        // Not sure if this is useful - TODO
        verifyTrue(userStoragePage.isDefaultSort());

        // Login test - credentials should be in the gradle build file.
        userStoragePage = loginTest(userStoragePage);

        // Test adding a directory & deleting it
        int rowCount = userStoragePage.getTableRowCount();

        // Create a temp test folder
        userStoragePage = userStoragePage.createNewFolder(workingDirectoryName);

        // Should have at least 1 directory (some may be left from older failing tests)
        // so short the list to the newly created dir to make sure it's there
        userStoragePage.enterSearch(workingDirectoryName);
        // possibly need to wait for search to finish.
        waitFor(3);

        // Should be 1 more entry in the table - could be cruft left from failed prior tests
        // table header has one row. This and the new directory is a total of 2
        verifyTrue(userStoragePage.getTableRowCount() == 2);

        int startRow = 1;

        // Test selecting checkbox
        System.out.println("testing selecting checkbox");
        userStoragePage.clickCheckboxForRow(startRow);
        verifyTrue(userStoragePage.isFileSelectedMode(startRow));

        userStoragePage.clickCheckboxForRow(startRow);
        verifyFalse(userStoragePage.isFileSelectedMode(startRow));

        // Go into the working directory
        userStoragePage = userStoragePage.clickFolder(workingDirectoryName);

        // Nav up one level
        userStoragePage = userStoragePage.navUpLevel();
        log.debug("gone up one level");

        // Nav back into working dir
        userStoragePage.enterSearch(workingDirectoryName);
        userStoragePage = userStoragePage.clickFolder(workingDirectoryName);

        userStoragePage = userStoragePage.navToRoot();

        // Should be able to click through the path back to the test dir
        for (String s: testDirPath) {
            if (StringUtil.hasLength(s)) {
                log.debug("nav to this folder: " + s);
                userStoragePage.enterSearch(s);
                waitFor(2);
                userStoragePage = userStoragePage.clickFolder(s);
            }
        }

        userStoragePage.enterSearch(workingDirectoryName);
        // Nav back into working dir
        userStoragePage = userStoragePage.clickFolder(workingDirectoryName);
        log.debug("gone back into working directory");

        // Go back to home directory where test was started.

        // Nav up one level to start cleanup
        userStoragePage = userStoragePage.navToHome();
        log.debug("gone to home directory");

        userStoragePage = cleanup(userStoragePage, workingDirectoryName);

        userStoragePage.doLogout();

        log.debug("UserStorageNavTest completed");
    }

}
