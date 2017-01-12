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

		userStoragePage.selectFolder(testFolderName);
		verifyTrue(userStoragePage.getHeaderText().contains(testFolderName));
		// click through to CADCtest page
		// check h2 with property 'name' matches folder name (although there's alot of whitespace in there!

		// look for Up and Root buttons?
		// Check status of other buttons (depending on whether you have write access to the page or not
		// determine write access via the 'more_details' button (if present)

    	
    	// Scenario 2:
		// login test - credentials should be in the gradle build file.
		userStoragePage.doLogin("CADCtest","sywymUL4");
		verifyTrue(userStoragePage.isLoggedIn());
		System.out.println("logged in");

		// search should be removed after login (page is reset to default, although
		// you would remain in the same subdirectory)
		rowCount = userStoragePage.getTableRowCount();

		System.out.println("Rowcount: " + rowCount);
		verifyTrue(rowCount > 2);

		// Scenario 3: logout
		userStoragePage.doLogout();
		verifyFalse(userStoragePage.isLoggedIn());


    	
    	// Scenario 4: 
    	// downloading a file?

   
    	System.out.println("UserStorageBrowserTest completed");
    	
    }
}