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

package net.canfar;

import org.apache.log4j.Logger;
import org.junit.Test;


public class UserStoragePermissionsTest extends UserStorageBaseTest {
    private static final Logger log =
        Logger.getLogger(UserStoragePermissionsTest.class);

    public UserStoragePermissionsTest() throws Exception {
        super();
    }


    @Test
    public void runTest() throws Exception {
        try {
            browseUserStorage();
        } catch (Exception e) {
            captureScreenShot(UserStoragePermissionsTest.class.getName() + ".browseUserStorage");
            throw e;
        }
    }

    private void browseUserStorage() throws Exception {
        System.out.println("Visiting: " + webURL);

        // Go to root test directory
        final String workingDirectoryName = UserStoragePermissionsTest.class.getSimpleName() + "_" + generateAlphaNumeric();
        UserStorageBrowserPage userStoragePage = goTo(testDirectory, null, UserStorageBrowserPage.class);

        if (userStoragePage.isMainPage()) {
            userStoragePage.waitForStorageLoad();
        }

        // Login test - credentials should be in the gradle build file.
        userStoragePage = loginTest(userStoragePage);

        // Create a folder, and run tests in there
        // This will be deleted at the end of this test suite
        userStoragePage = userStoragePage.createNewFolder(workingDirectoryName);

        // Narrow field to just this folder
        userStoragePage.enterSearch(workingDirectoryName);

        // verify edit icons are now present - as this is the user's own folder
        verifyTrue(userStoragePage.isRowItemPermissionsEditable(1));

        // Get permissions of this uppermost temporary test folder
        String parentWriteGroup = "";
        String parentReadGroup = "";

        parentWriteGroup = userStoragePage.getValueForRowCol(1, 5);
        parentReadGroup = userStoragePage.getValueForRowCol(1, 6);

        /// Nav into the folder
        userStoragePage = userStoragePage.clickFolder(workingDirectoryName);

        // Create second test folder
        String tempTestFolder = "UserStoragePermissionsTest_subfolder1_tobedeleted";
        userStoragePage = userStoragePage.createNewFolder(tempTestFolder);

        // get current directory permissions
        String readGroupName = "cadcsw";
        String writeGroupName = "cadc-dev";
        String invalidGroupName = "invalid-group";

        log.debug("doing permissions testing");
        final boolean isPublic = parentReadGroup.equals("Public");

        // Test that permissions are same as the parent to start
        // should only be 1 row displayed
        verifyTrue(userStoragePage.isPermissionDataForRow(1, parentWriteGroup, parentReadGroup, isPublic));

        // Edit permissions on the form
        String currentReadGroup = userStoragePage.getValueForRowCol(1, 6);

        // Clearly only works for English test suite. :/
        // Toggle the Public attribute to get the underlying read group (if any)
        if (currentReadGroup.equals("Public")) {
            userStoragePage = userStoragePage.togglePublicAttributeForRow();
        }

        // Don't change anything, verify that the correct message is displayed
        userStoragePage.clickEditIconForFirstRow();
        userStoragePage = userStoragePage.clickButtonAndWait(UserStorageBrowserPage.SAVE);
        userStoragePage = userStoragePage.clickButtonAndWait(UserStorageBrowserPage.CANCEL);
        userStoragePage = userStoragePage.setReadGroup("", true);

        verifyTrue(userStoragePage.isPermissionDataForRow(1, parentWriteGroup, "", false));
        userStoragePage.waitForPromptFinish();

        // Set read group to selected group
        userStoragePage = userStoragePage.setReadGroup(readGroupName, true);
        verifyTrue(userStoragePage.isPermissionDataForRow(1, parentWriteGroup, readGroupName, false));

        userStoragePage = userStoragePage.setWriteGroup("", true);
        verifyTrue(userStoragePage.isPermissionDataForRow(1, "", readGroupName, false));
        userStoragePage = userStoragePage.setWriteGroup(writeGroupName, true);
        verifyTrue(userStoragePage.isPermissionDataForRow(1, writeGroupName, readGroupName, false));

        // Test response to invalid autocomplete selection
        userStoragePage.clickEditIconForFirstRow();
        // last parameter says 'don't confirm anything'
        userStoragePage = userStoragePage.setGroupOnly(UserStorageBrowserPage.READ_GROUP_INPUT, invalidGroupName,
            false);
        verifyTrue(userStoragePage.isGroupError(UserStorageBrowserPage.READ_GROUP_DIV));

        // Enter correct one in order to close the prompt
        userStoragePage = userStoragePage.setGroupOnly(UserStorageBrowserPage.READ_GROUP_INPUT,
            readGroupName, true);
        verifyTrue(userStoragePage.isPermissionDataForRow(1, writeGroupName, readGroupName, false));

        // Test response to invalid autocomplete selection
        userStoragePage.clickEditIconForFirstRow();
        // second parameter says 'don't confirm anything'
        userStoragePage = userStoragePage.setGroupOnly(UserStorageBrowserPage.WRITE_GROUP_INPUT,
            invalidGroupName, false);
        verifyTrue(userStoragePage.isGroupError(UserStorageBrowserPage.WRITE_GROUP_DIV));

            // Enter correct one in order to close the prompt
        userStoragePage = userStoragePage.setGroupOnly(UserStorageBrowserPage.WRITE_GROUP_INPUT,
            writeGroupName, true);
        verifyTrue(userStoragePage.isPermissionDataForRow(1, writeGroupName, readGroupName, false));

        // Toggle public permissions to set them
        // Group name displayed in table should read "Public"
        userStoragePage = userStoragePage.togglePublicAttributeForRow();

            verifyTrue(userStoragePage.isPermissionDataForRow(1, writeGroupName, "Public", true));
            System.out.println("Set read group to public");

        // Toggle public permission to unset
        userStoragePage = userStoragePage.togglePublicAttributeForRow();
        verifyTrue(userStoragePage.isPermissionDataForRow(1, writeGroupName, readGroupName, false));

        userStoragePage.enterSearch(tempTestFolder);
        userStoragePage = userStoragePage.clickFolder(tempTestFolder);

        String recursiveTestFolder = "recursive_tobedeleted";
        userStoragePage = userStoragePage.createNewFolder(recursiveTestFolder);

        userStoragePage = userStoragePage.navUpLevel();

        userStoragePage = userStoragePage.applyRecursivePermissions(UserStorageBrowserPage.WRITE_GROUP_INPUT,
            "cadcsw");
        verifyTrue(userStoragePage.isPermissionDataForRow(1, "cadcsw", readGroupName, false));

        userStoragePage = userStoragePage.clickFolder(tempTestFolder);
        verifyTrue(userStoragePage.isPermissionDataForRow(1, "cadcsw", readGroupName, false));

        // go up to temp test folder
        userStoragePage = userStoragePage.navUpLevel();

        // go up to testDirectory folder
        userStoragePage = userStoragePage.navUpLevel();

        userStoragePage = cleanup(userStoragePage, workingDirectoryName);

        userStoragePage.doLogout();

        log.info("UserStoragePermissionsTest completed");
    }

}
