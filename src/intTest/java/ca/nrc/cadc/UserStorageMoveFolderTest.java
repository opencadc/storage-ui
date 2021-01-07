/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2014.                         (c) 2014.
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
 * @author jenkinsd
 * 15/05/14 - 1:19 PM
 *
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


public class UserStorageMoveFolderTest extends UserStorageBaseTest {
    private static final Logger log =
        Logger.getLogger(UserStorageMoveFolderTest.class);

    static {
        Log4jInit.setLevel("ca.nrc.cadc", Level.DEBUG);
    }

    public UserStorageMoveFolderTest() throws Exception {
        super();
    }

    @Test
    public void runTest() throws Exception {
        try {
            moveFolderTest();
        } catch (Exception e) {
            captureScreenShot(UserStorageMoveFolderTest.class.getName() + ".moveFolderTest");
            throw e;
        }
    }

    private void moveFolderTest() throws Exception {
        log.info("Visiting: " + webURL + testDirectory);

        final String workingDirectoryName = UserStorageMoveFolderTest.class.getSimpleName() + "_" + generateAlphaNumeric();
        UserStorageBrowserPage userStoragePage = goTo(testDirectory, null, UserStorageBrowserPage.class);
        String[] testPath = parseTestDirPath(testDirectory);

        // Login test - credentials should be in the gradle build file.
        userStoragePage = loginTest(userStoragePage);

        // Create a temp test folder, and run tests in there
        // This will be deleted at the end of this test suite
        userStoragePage = userStoragePage.createNewFolder(workingDirectoryName);

        // Should have at least 1 directory (some may be left from older failing tests)
        // so short the list to the newly created dir to make sure it's there
        userStoragePage.enterSearch(workingDirectoryName);
        // possibly need to wait for search to finish.
        waitFor(3);

        // Nav into the new folder
        userStoragePage = userStoragePage.clickFolder(workingDirectoryName);

        // Test Move folder
        String sourceFolder = "moveTest_source_toBeDeleted";
        userStoragePage = userStoragePage.createNewFolder(sourceFolder);
        log.debug("move source folder: " + sourceFolder);

        // Second folder to move first into
        // Test Move folder
        String targetFolder = "moveTest_target_toBeDeleted";
        userStoragePage = userStoragePage.createNewFolder(targetFolder);
        log.debug("move target folder: " + targetFolder);

        // Select folder to be moved
        userStoragePage.enterSearch(sourceFolder);
        userStoragePage.clickCheckboxForRow(1);

        // Kick off first ajax call to populate tree
        userStoragePage = userStoragePage.startMove();

        // Navigate through to the target node
        for (String pathEl: testPath) {
            if (StringUtil.hasLength(pathEl)) {
                log.debug("navigating to path element: " + pathEl);
                userStoragePage.selectFolderFromTree(pathEl);
            }
        }
        userStoragePage.selectFolderFromTree(workingDirectoryName);
        userStoragePage.selectFolderFromTree(targetFolder);

        // Open move dialog
        userStoragePage = userStoragePage.doMove();

        // Nav into target folder to find source folder
        /// Nav into the folder
        // the apparent '+1' in the table row count below is because the header row has
        // one 'tr' in it, so everything needs to be upped by 1.
        userStoragePage = userStoragePage.clickFolder(targetFolder);
        userStoragePage.enterSearch(sourceFolder);
        verifyTrue(userStoragePage.getTableRowCount() == 2);

        // Make sure nothing is left one level up
        userStoragePage = userStoragePage.navUpLevel();
        userStoragePage.enterSearch(targetFolder);
        log.debug("table row count: " + userStoragePage.getTableRowCount());
        verifyTrue(userStoragePage.getTableRowCount() == 2);

        userStoragePage = userStoragePage.navUpLevel();
        cleanup(userStoragePage, workingDirectoryName);

        log.debug("Logout");
        userStoragePage.doLogout();

        System.out.println("MoveFolderTest completed");
    }

}
