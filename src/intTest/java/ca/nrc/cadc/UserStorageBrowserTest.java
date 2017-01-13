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


import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import ca.nrc.cadc.web.selenium.AbstractWebApplicationIntegrationTest;


public class UserStorageBrowserTest extends AbstractWebApplicationIntegrationTest
{
	static final String STORAGE_ENDPOINT = "storage/list";

    @Test
    public void browseUserStorage() throws Exception
    {

    	UserStorageBrowserPage userStoragePage =
                goTo(STORAGE_ENDPOINT, null, UserStorageBrowserPage.class);

		String testFolderName = "CADCtest";

    	// First landing:
    	// expected number of entries?
    	// check basic elements are on page
    	// check login/not logged in state

    	// Scenario 1:
    	// enter search value
    	// check that rows of table are shorted correctly
    	// verify entry is correct

    	// Should short to one entry, verify the name and quit.

    	userStoragePage.enterSearch(testFolderName);
    	
    	// find that entry and click on Name/link
    	int rowCount = userStoragePage.getTableRowCount();

    	System.out.println("Rowcount: " + rowCount);
    	verifyTrue(rowCount < 3);
    	String folderName = userStoragePage.getFolderName(rowCount-1);
    	
    	verifyTrue(userStoragePage.verifyFolderName(rowCount-1, testFolderName));

		// Next tests to run after this (as part of page clickthrough test)
		// click on the row entry

		// click through to CADCtest folder
		userStoragePage.selectFolder(testFolderName);
		// check h2 with property 'name' matches folder name
		// (although there's alot of whitespace in there!
		verifyTrue(userStoragePage.getHeaderText().contains(testFolderName));
		verifyTrue(userStoragePage.inSubFolderMode());

		// look for Up and Root buttons?
		// Will only be present in sub folders?



		// Check access to page
		// Check status of other buttons (depending on whether you have write access to the page or not
		// determine write access via the 'more_details' button (if present)
		// TODO: best way to determine write access is???
		verifyTrue(userStoragePage.inReadAccessMode());
    	
    	// Scenario 2:
		// login test - credentials should be in the gradle build file.
		userStoragePage.doLogin("CADCtest","sywymUL4");
		verifyTrue(userStoragePage.inLoggedInMode());
		System.out.println("logged in");

		// search should be removed after login (page is reset to default, although
		// you would remain in the same subdirectory)
		rowCount = userStoragePage.getTableRowCount();

		System.out.println("Rowcount: " + rowCount);
		verifyTrue(rowCount > 2);

		// Check access to page
		verifyTrue(userStoragePage.inReadAccessMode());


		// Scenario 3: Test navigation buttons

		// Navigate back up to root
		userStoragePage.navToRoot();
		// Verify header
		verifyTrue(userStoragePage.getHeaderText().contains("ROOT"));

		// click through to CADCtest folder again
		userStoragePage.selectFolder(testFolderName);
		// check h2 with property 'name' matches folder name
		// (although there's alot of whitespace in there!
		verifyTrue(userStoragePage.getHeaderText().contains(testFolderName));

		// Navigate up one level (should be back to root)
		userStoragePage.navUpLevel();
		// Verify header
		verifyTrue(userStoragePage.getHeaderText().contains("ROOT"));

		// TODO: better test here is to have two levels to navigate through,
		// make sure root goes to root, up level goes up one only.



		// Scenario 4: logout
		userStoragePage.doLogout();
		verifyFalse(userStoragePage.inLoggedInMode());


    	
    	// Scenario X:
    	// downloading a file?

   
    	System.out.println("UserStorageBrowserTest completed");
    	
    }
}