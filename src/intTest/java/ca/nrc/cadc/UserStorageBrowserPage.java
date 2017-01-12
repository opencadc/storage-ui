/*
 ************************************************************************
 *******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
 **************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
 *
 *  (c) 2016.                            (c) 2016.
 *  Government of Canada                 Gouvernement du Canada
 *  National Research Council            Conseil national de recherches
 *  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 *  All rights reserved                  Tous droits réservés
 *
 *  NRC disclaims any warranties,        Le CNRC dénie toute garantie
 *  expressed, implied, or               énoncée, implicite ou légale,
 *  statutory, of any kind with          de quelque nature que ce
 *  respect to the software,             soit, concernant le logiciel,
 *  including without limitation         y compris sans restriction
 *  any warranty of merchantability      toute garantie de valeur
 *  or fitness for a particular          marchande ou de pertinence
 *  purpose. NRC shall not be            pour un usage particulier.
 *  liable in any event for any          Le CNRC ne pourra en aucun cas
 *  damages, whether direct or           être tenu responsable de tout
 *  indirect, special or general,        dommage, direct ou indirect,
 *  consequential or incidental,         particulier ou général,
 *  arising from the use of the          accessoire ou fortuit, résultant
 *  software.  Neither the name          de l'utilisation du logiciel. Ni
 *  of the National Research             le nom du Conseil National de
 *  Council of Canada nor the            Recherches du Canada ni les noms
 *  names of its contributors may        de ses  participants ne peuvent
 *  be used to endorse or promote        être utilisés pour approuver ou
 *  products derived from this           promouvoir les produits dérivés
 *  software without specific prior      de ce logiciel sans autorisation
 *  written permission.                  préalable et particulière
 *                                       par écrit.
 *
 *  This file is part of the             Ce fichier fait partie du projet
 *  OpenCADC project.                    OpenCADC.
 *
 *  OpenCADC is free software:           OpenCADC est un logiciel libre ;
 *  you can redistribute it and/or       vous pouvez le redistribuer ou le
 *  modify it under the terms of         modifier suivant les termes de
 *  the GNU Affero General Public        la “GNU Affero General Public
 *  License as published by the          License” telle que publiée
 *  Free Software Foundation,            par la Free Software Foundation
 *  either version 3 of the              : soit la version 3 de cette
 *  License, or (at your option)         licence, soit (à votre gré)
 *  any later version.                   toute version ultérieure.
 *
 *  OpenCADC is distributed in the       OpenCADC est distribué
 *  hope that it will be useful,         dans l’espoir qu’il vous
 *  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
 *  without even the implied             GARANTIE : sans même la garantie
 *  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
 *  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
 *  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
 *  General Public License for           Générale Publique GNU Affero
 *  more details.                        pour plus de détails.
 *
 *  You should have received             Vous devriez avoir reçu une
 *  a copy of the GNU Affero             copie de la Licence Générale
 *  General Public License along         Publique GNU Affero avec
 *  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
 *  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
 *                                  best ide for debugging gradle projects     <http://www.gnu.org/licenses/>.
 *
 *
 ************************************************************************
 */

package ca.nrc.cadc;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import ca.nrc.cadc.web.selenium.AbstractTestWebPage;
import org.openqa.selenium.NoSuchElementException;


import java.util.List;


public class UserStorageBrowserPage extends AbstractTestWebPage
{
    // Define in here what elements are mode indicators

    // Elements always on the page
    @FindBy(id = "beacon_filter")
    private WebElement searchFilter;

    @FindBy(id = "beacon")
    private WebElement beaconTable;

    // element 'Showing x to y of z entries' line
    @FindBy(id = "beacon_info")
    private WebElement statusMessage;

    @FindBy(className="beacon-progress")
    private WebElement progressBar;

    // header displaying name of current folder
    @FindBy(xpath="//h2[@property='name']")
    private WebElement folderNameHeader;


    // Elements present once user has navigated away from ROOT folder
    // Toobar buttons
    @FindBy(id="level-up")
    private WebElement leveUpButton;

    @FindBy(id="root")
    private WebElement rootButton;

    // class has 'disabled' in it for base case.
    @FindBy(id="newdropdown")
    private WebElement newdropdownButton;

    @FindBy(id="download")
    private WebElement downloadButton;

    @FindBy(id="delete")
    private WebElement deleteButton;

    @FindBy(id="more_details")
    private WebElement moredetailsButton;


    // Login form elements
    // TODO: put this in it's own pojo so it can be made more generic
//    LoginFormPageObject loginForm;
    // May be issues with leveraging PageFactory with @FindBy in the subclass?
    // Login Form elements
    @FindBy(id="username")
    private WebElement loginUsername;

    @FindBy(id="password")
    private WebElement loginPassword;

    @FindBy(id="submitLogin")
    private WebElement submitLoginButton;

    @FindBy(id = "logout")
    private WebElement logoutButton;



    public UserStorageBrowserPage(final WebDriver driver) throws Exception
    {
        super(driver);

        // The beacon-progress bar displays "Transferring Data" while it's loading
        // the page. Firefox doesn't display whole list until the bar is green, and
        // that text is gone. Could be this test isn't sufficient but it works
        // to have intTestFirefox not fail.
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.textToBe(By.className("beacon-progress"), ""));

        PageFactory.initElements(driver, this);

        // Don't think this is the best way to do it but it'll work for now.
//        loginForm = new LoginFormPageObject(driver);
    }


    // Transition functions
    public void enterSearch(final String searchString) throws Exception {
    	sendKeys(searchFilter, searchString);
    }

    public void doLogin(String username, String password) throws Exception {
        sendKeys(loginUsername, username);
        sendKeys(loginPassword, password);
        click(submitLoginButton);
        waitForElementPresent(By.id("logout"));
    }

    public void doLogout() throws Exception {
        click(logoutButton);
    }

    public void selectFolder(String folderName)
    {
        //*[@id="beacon"]/tbody/tr[17]/td[2]/a
        WebElement folder = beaconTable.findElement(By.xpath("//*[@id=\"beacon\"]/tbody/tr/td/a[text()[contains(.,'" + folderName  + "')]]"));
        System.out.println("Folder to be clicked: " + folder.getText());
        folder.click();
    }
    


    // Inspection functions
    public WebElement getProgressBar() throws Exception {
        System.out.println(progressBar.getText());
        return progressBar;
    }

    int getTableRowCount() throws Exception
    {
        List<WebElement> tableRows = beaconTable.findElements(By.tagName("tr"));
        return tableRows.size();
    }

    boolean verifyFolderName(int rowNum, String expectedValue) throws Exception
    {
        List<WebElement> tableRows = beaconTable.findElements(By.tagName("tr"));
        WebElement selectedRow = tableRows.get(rowNum);
        WebElement namecolumn = selectedRow.findElement(By.cssSelector("a:nth-of-type(1)"));
        System.out.println(namecolumn.getText());
        return expectedValue.equals(namecolumn.getText());

    }

    String getFolderName(int rowNum) throws Exception
    {
        List<WebElement> tableRows = beaconTable.findElements(By.tagName("tr"));
        WebElement selectedRow = tableRows.get(rowNum);
        WebElement namecolumn = selectedRow.findElement(By.cssSelector("a:nth-of-type(1)"));
        System.out.println("Foldername to be returned: " + namecolumn.getText());
        return namecolumn.getText();
    }

    String getHeaderText() throws Exception
    {
        System.out.println("Header text: " + folderNameHeader.getText());
        return folderNameHeader.getText();
    }

//    boolean isLoggedIn(String username) throws Exception
//    {
//        return loginForm.isLoggedIn();
//    }

    boolean isLoggedIn() {
        try {
            logoutButton.isDisplayed();
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

}