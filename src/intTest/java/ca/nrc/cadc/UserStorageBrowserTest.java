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

    	// TODO: First landing -  check:
    	// - basic elements are on page
		// - not logged in
    	// - Name is primary sort
		// Test default sort on Name column
		verifyTrue(userStoragePage.isDefaultSort());


    	// Scenario 1:
    	// enter search(filter) value
    	// check that rows of table are shorted correctly
    	// verify entry is correct

    	userStoragePage.enterSearch(testFolderName);
    	int rowCount = userStoragePage.getTableRowCount();
//    	System.out.println("Rowcount: " + rowCount);
    	verifyTrue(rowCount < 3);
    	verifyTrue(userStoragePage.verifyFolderName(rowCount-1, testFolderName));


		// Verify page permissions prior to logging in
		// click through to CADCtest folder
		userStoragePage.clickFolder(testFolderName);
		// Verify sub folder page state
		verifyTrue(userStoragePage.isSubFolder(testFolderName));

		// Check permissions on page
		verifyTrue(userStoragePage.isReadAccess());


    	// Scenario 2: Login test - credentials should be in the gradle build file.
		userStoragePage.doLogin("CADCtest","sywymUL4");
		verifyTrue(userStoragePage.isLoggedIn());
		System.out.println("logged in");

		rowCount = userStoragePage.getTableRowCount();

		System.out.println("Rowcount: " + rowCount);
		verifyTrue(rowCount > 2);

		// Check access to page: should be write accessible
		verifyFalse(userStoragePage.isReadAccess());


		// Scenario 3: Test navigation buttons
		// Test state is currently in a subfolder: Start at Root
		System.out.println("navigating to root...");
		userStoragePage.navToRoot();
		// Verify in Root Folder
		verifyTrue(userStoragePage.isRootFolder());

		int startRow = 1;

		System.out.println("Starting navigation tests");
		// click through to first folder
		int firstPageRowClicked = userStoragePage.getNextAvailabileFolderRow(startRow);
		String subFolder1 = userStoragePage.getFolderName(firstPageRowClicked);
		userStoragePage.clickFolderForRow(firstPageRowClicked);
		verifyTrue(userStoragePage.isSubFolder(subFolder1));

		// Go down one more level
		int secondPageRowCilcked = userStoragePage.getNextAvailabileFolderRow(startRow);
		String subFolder2 = userStoragePage.getFolderName(secondPageRowCilcked);
		userStoragePage.clickFolderForRow(secondPageRowCilcked);
		verifyTrue(userStoragePage.isSubFolder(subFolder2));

		// Navigate up one level (should be up one level)
		userStoragePage.navUpLevel();
		verifyTrue(userStoragePage.isSubFolder(subFolder1));

		// Go back down one folder
		userStoragePage.clickFolderForRow(secondPageRowCilcked);
		verifyTrue(userStoragePage.isSubFolder(subFolder2));

		// Go up to root
		userStoragePage.navToRoot();
		// Verify in Root Folder
		verifyTrue(userStoragePage.isRootFolder());


		// Scenario 4: test file actions
		System.out.println("testing file actions");
		userStoragePage.clickFolderForRow(firstPageRowClicked);
		userStoragePage.clickCheckboxForRow(startRow);
		verifyTrue(userStoragePage.isFileSelectedMode(startRow));

		userStoragePage.clickCheckboxForRow(startRow);
		verifyFalse(userStoragePage.isFileSelectedMode(startRow));

		// Go up to root
		userStoragePage.navToRoot();
		verifyTrue(userStoragePage.isRootFolder());
		// click through to CADCtest folder
		userStoragePage.clickFolder(testFolderName);
		// Verify sub folder page state
		verifyTrue(userStoragePage.isSubFolder(testFolderName));

		// navigate to automated test folder
		String testFolder = "automated_test";
		userStoragePage.clickFolder(testFolder);

		// Create second test folder
		String tempTestFolder = "vosui_automated_test";
		userStoragePage.createNewFolder(tempTestFolder);

		userStoragePage.enterSearch(tempTestFolder);

		// Change Public attribute
		String publicValue = userStoragePage.getValueForRowCol(1,6);
		System.out.println("public permission: " + publicValue);
		userStoragePage.togglePublicAttributeForRow(1);

		// short list displayed in page again
		userStoragePage.enterSearch(tempTestFolder);
		String publicValue2 = userStoragePage.getValueForRowCol(1,6);
		// verify that value has changed
		System.out.println("public permission: " + publicValue2);

		verifyFalse(publicValue2.equals(publicValue));

		// Delete folder just created
		userStoragePage.clickCheckboxForRow(1);
		userStoragePage.deleteFolder(tempTestFolder);

		// verify the folder is no longer there
		userStoragePage.enterSearch(tempTestFolder);
		verifyTrue(userStoragePage.isTableEmpty());

		// Scenario 5: logout
		System.out.println("Test logout");
		userStoragePage.doLogout();
		verifyFalse(userStoragePage.isLoggedIn());
   
    	System.out.println("UserStorageBrowserTest completed");

		// Scenario TODO:
		// downloading a file
    	
    }
}