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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class UserStorageVOSpaceServiceTest extends UserStorageBaseTest {
    private static final Logger log = Logger.getLogger(UserStorageVOSpaceServiceTest.class);

    static {
        Log4jInit.setLevel("ca.nrc.cadc", Level.DEBUG);
    }

    public UserStorageVOSpaceServiceTest() {
        super();
    }

    @Test
    public void voSpaceServiceSwitch() throws Exception {
        FolderPage userStoragePage = goTo("/", null, FolderPage.class);

        // Need to do this to have access to Home button
        userStoragePage = login(userStoragePage);

        // Nav to home - should be using vault home dir by default
        userStoragePage.navToHome();

        userStoragePage = goTo(testDirectoryPath, null, FolderPage.class);

        // check folder name
        userStoragePage.waitForHeaderText(testDirectoryPath);

        // go to root
        userStoragePage = userStoragePage.navToRoot();

        if (StringUtil.hasText(altVOSpaceSvc)) {
            // switch to alternate VOSpace Service (defined in build.gradle)
            userStoragePage = userStoragePage.switchVOSpaceService(altVOSpaceSvc);

            // Nav to home - should be using vault home dir by default
            userStoragePage = userStoragePage.navToHome();

            // check folder name (defined in build.gradle)
            log.info(altHomeDir);
            userStoragePage.waitForHeaderText(altHomeDir);

            log.debug("testVOSpaceServiceSwitch completed");
        } else {
            log.debug("testVOSpaceServiceSwitch completed without switching (no alternate set).");
        }
    }
}
