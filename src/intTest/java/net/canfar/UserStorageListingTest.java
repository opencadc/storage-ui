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

package net.canfar;

import ca.nrc.cadc.util.Log4jInit;
import ca.nrc.cadc.util.StringUtil;
import ca.nrc.cadc.web.selenium.AbstractWebApplicationIntegrationTest;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.opencadc.storage.util.StorageUtil;

public class UserStorageListingTest extends UserStorageBaseTest {
    private static final Logger log = Logger.getLogger(UserStorageListingTest.class);

    static {
        Log4jInit.setLevel("ca.nrc.cadc", Level.DEBUG);
    }

    public UserStorageListingTest() {
        super();
    }

    @Test
    public void navUserStorage() throws Exception {
        log.info("Visiting: " + webURL + testDirectoryPath);

        FolderPage userStoragePage = goTo("/", null, FolderPage.class);

        if (StringUtil.hasText(AbstractWebApplicationIntegrationTest.password)) {
            log.debug("Logging in as " + AbstractWebApplicationIntegrationTest.username);

            // Need to do this to have access to Home button
            login(userStoragePage);
        } else {
            log.debug("NOT logging in as no password provided");
        }

        userStoragePage = goTo(testDirectoryPath, null, FolderPage.class);

        if (userStoragePage.isMainPage()) {
            userStoragePage.waitForStorageLoad();
        }

        // Not sure if this is useful - TODO
        verifyTrue(userStoragePage.isDefaultSort());

        // Test adding a directory & deleting it
        userStoragePage.getTableRowCount();

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

        // Nav up one level
        userStoragePage = userStoragePage.navUpLevel();
        log.debug("gone up one level");

        userStoragePage = userStoragePage.navToRoot();

        final String[] testDirPath = StorageUtil.parseTestDirPath(testDirectoryPath);
        // Should be able to click through the path back to the test dir
        for (final String s : testDirPath) {
            if (StringUtil.hasLength(s)) {
                log.debug("nav to this folder: " + s);
                userStoragePage.enterSearch(s);
                waitFor(2);
                userStoragePage = userStoragePage.clickFolder(s);
            }
        }

        // Go back to home directory where test was started.

        // Nav up one level to start cleanup
        userStoragePage = goTo(testDirectoryPath, null, FolderPage.class);

        final StoragePage storagePage = userStoragePage.doLogout(false);

        if (storagePage.isError()) {
            log.warn("Found error on page: " + storagePage.getErrorMessage());
        }

        log.debug("UserStorageListingTest completed");
    }
}
