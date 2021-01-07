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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;


public class UserStorageQuotaDisplayTest extends UserStorageBaseTest {
    private static final Logger log =
        Logger.getLogger(UserStorageQuotaDisplayTest.class);

    static {
        Log4jInit.setLevel("ca.nrc.cadc", Level.INFO);
    }

    public UserStorageQuotaDisplayTest() throws Exception {
        super();
    }

    @Test
    public void runTest() throws Exception {
        try {
            quotaDisplayTest();
        } catch (Exception e) {
            captureScreenShot(UserStorageQuotaDisplayTest.class.getName() + ".quotaDisplayTest");
            throw e;
        }
    }

    private void quotaDisplayTest() throws Exception {
        log.info("Visiting: " + webURL +  testDirectory);

        // Go to test directory
        UserStorageBrowserPage userStoragePage = goTo(testDirectory, null, UserStorageBrowserPage.class);

        // Will be located in whatever has been designated the test directory in build.gradle
        // quota should be displayed here, but not in root directory.
        verifyTrue(userStoragePage.quotaIsDisplayed());

        log.debug("navigating to root...");
        userStoragePage = userStoragePage.navToRoot();

        log.debug("done nav to root");
        verifyFalse(userStoragePage.quotaIsDisplayed());

        log.info("UserStorageQuotaDisplayTest completed");
    }

}
