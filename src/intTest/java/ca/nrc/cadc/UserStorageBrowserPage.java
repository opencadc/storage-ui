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

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ca.nrc.cadc.web.selenium.AbstractTestWebPage;

import ca.nrc.cadc.util.StringUtil;

import java.util.List;

import static org.openqa.selenium.By.xpath;


public class UserStorageBrowserPage extends AbstractTestWebPage
{
    private static final String ROOT_FOLDER_NAME = "ROOT";

    // Strings for matching against prompt messages and buttons
    private static final String DELETE_CONFIRMATION_TEXT = "Are you sure you wish to delete the selected items?";
    private static final String SUCCESSFUL = "successful";
    private static final String ALREADY_EXISTS = "already exists";
    public static final String MODIFIED = "modified";
    public static final String NOT_MODIFIED = "not modified";
    private static final String CONFIRMATION_MSG = "New folder added successfully";
    private static final String OK = "Ok";
    private static final String YES = "Yes";
    private static final String CLOSE = "Close";
    public static final String SAVE = "Save";
    private static final By NAVBAR_ELEMENTS_BY =
            xpath("//*[@id=\"navbar-functions\"]/ul");
    private static final By NEW_FOLDER_BY = By.id("newfolder");

    // Define in here what elements are mode indicators

    public static final String READ_GROUP_DIV = "readGroupDiv";
    public static final String WRITE_GROUP_DIV = "writeGroupDiv";
    public static final String READ_GROUP_INPUT = "readGroup";
    public static final String WRITE_GROUP_INPUT = "writeGroup";

    // Elements always on the page
    @FindBy(id = "beacon_filter")
    private WebElement searchFilter;

    @FindBy(id = "beacon")
    private WebElement beaconTable;

    // element 'Showing x to y of z entries' line
    @FindBy(id = "beacon_info")
    private WebElement statusMessage;

    @FindBy(className = "beacon-progress")
    private WebElement progressBar;

    // header displaying name of current folder
    @FindBy(xpath = "//h2[@property='name']")
    private WebElement folderNameHeader;

    @FindBy(xpath = "//*[@id=\"navbar-functions\"]/ul")
    private WebElement navbarButtonList;

    @FindBy(xpath = "//*[@id=\"beacon\"]/tbody/tr[1]")
    private WebElement firstTableRow;

    // Elements present once user has navigated away from ROOT folder
    // Toobar buttons
    @FindBy(id = "level-up")
    private WebElement leveUpButton;

    @FindBy(id = "root")
    private WebElement rootButton;

    // class has 'disabled' in it for base case.
    @FindBy(id = "newdropdown")
    private WebElement newdropdownButton;

    // element of the list under newdropdown
    @FindBy(id = "newfolder")
    private WebElement newFolder;


    @FindBy(id = "download")
    private WebElement downloadButton;

    @FindBy(id = "delete")
    private WebElement deleteButton;

    @FindBy(id = "more_details")
    private WebElement moredetailsButton;


    // Login Form elements
    @FindBy(id = "username")
    private WebElement loginUsername;

    @FindBy(id = "password")
    private WebElement loginPassword;

    @FindBy(id = "submitLogin")
    private WebElement submitLoginButton;

    @FindBy(id = "logout")
    private WebElement logoutButton;

    private WebDriver driver = null;


    public UserStorageBrowserPage(final WebDriver driver) throws Exception
    {
        super(driver);
        this.driver = driver;

        waitForStorageLoad();

        PageFactory.initElements(driver, this);
    }


    // Transition functions
    public void clickButton(String promptText) throws Exception
    {
        WebElement button = waitUntil(ExpectedConditions.elementToBeClickable(
                xpath("//button[contains(text(),\"" + promptText + "\")]")));
        click(button);
    }

    public void clickButtonWithClass(String promptText, String className) throws
                                                                          Exception
    {
        WebElement button = waitUntil(ExpectedConditions.elementToBeClickable(
                xpath("//button[contains(@class, '" + className
                + "') and contains(text(),'" + promptText + "')]")));
        click(button);
    }

    public void enterSearch(final String searchString) throws Exception
    {
        sendKeys(searchFilter, searchString);
        waitForStorageLoad();
    }

    public UserStorageBrowserPage doLogin(String username, String password) throws Exception
    {
        sendKeys(loginUsername, username);
        sendKeys(loginPassword, password);
        click(submitLoginButton);
        waitForElementPresent(By.id("logout"));
        return new UserStorageBrowserPage(driver);
    }

    public UserStorageBrowserPage doLogout() throws Exception
    {
        click(logoutButton);
        return new UserStorageBrowserPage(driver);
    }

    // Folder Related Transition functions
    public UserStorageBrowserPage clickFolder(String folderName)
        throws Exception
    {
        WebElement folder = waitUntil(ExpectedConditions.elementToBeClickable(
                xpath("//*[@id=\"beacon\"]/tbody/tr/td/a[text()[contains(.,'"
                + folderName + "')]]")));

        System.out.println("Folder to be clicked: " + folder.getText());
        click(folder);

        return new UserStorageBrowserPage(driver);
    }

    public void clickFolderForRow(int rowNum) throws Exception
    {
        WebElement firstCheckbox = waitUntil(ExpectedConditions.elementToBeClickable(
                xpath("//*[@id=\"beacon\"]/tbody/tr[" + rowNum + "]/td[2]/a")));
        click(firstCheckbox);
    }


    protected int getNextAvailabileFolderRow(final int startRow)
            throws Exception
    {
        //   not all folders are clickable, go down the rows to find one
        boolean found = false;
        int rowNum = startRow;

        while (!found)
        {
            // This method throws an exception if the element is not found
            try
            {
                beaconTable.findElement(xpath("//*[@id=\"beacon\"]/tbody/tr["
                                              + rowNum + "]/td[2]/a"));
            }
            catch (Exception e)
            {
                rowNum++;
                continue;
            }
            found = true;
        }
        return rowNum;
    }

    protected void confirmSubItem(final String itemName) throws Exception
    {
        waitUntil(ExpectedConditions.elementToBeClickable(
                xpath("//*[@id=\"beacon\"]/tbody/tr/td/a[text()[contains(.,'"
                + itemName + "')]]")));
    }

    // CRUD for folders
    protected UserStorageBrowserPage createNewFolder(final String foldername)
            throws Exception
    {
        final WebElement newdropdownButton = find(By.id("newdropdown"));
        click(newdropdownButton);

        if (newdropdownButton.getAttribute("class").contains("disabled"))
        {
            try
            {
                final WebElement logout = find(By.id("logout"));
                if (logout == null)
                {
                    throw new RuntimeException("You are not logged in.");
                }
                else
                {
                    throw new RuntimeException("You are logged in, but " +
                    "something else is keeping this functionality disabled.");
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException("Can't create new items.  That " +
                "functionality is disabled.  Did you remember to login?");
            }
        }
        else
        {
            System.out.println("Everything is kosher > > "
                               + newdropdownButton.getAttribute("class"));
        }

        waitForElementVisible(NEW_FOLDER_BY);
        click(NEW_FOLDER_BY);
        WebElement newfolderInput =
                waitUntil(ExpectedConditions.elementToBeClickable(
                                By.id("fname")));

        sendKeys(newfolderInput, foldername);

        WebElement createFolderButton =
            find(xpath("//button[contains(text(),\"Create Folder\")]"));
        click(createFolderButton);

        try
        {
            confirmJqiMsg(CONFIRMATION_MSG);

            return new UserStorageBrowserPage(driver);
        }
        catch (Exception e)
        {
            throw new Exception("Could not create folder " + foldername);
        }
    }

    public void deleteFolder(String foldername) throws Exception
    {
        if (!isDisabled(deleteButton))
        {
            deleteButton.click();
        }

        // locate folder, select checkbox, select delete button
        if (isJqiMsgShowing(DELETE_CONFIRMATION_TEXT))
        {
            clickButtonWithClass(YES, "btn-danger");
        }
        else
        {
            throw new Exception("Could not delete folder " + foldername);
        }

        // confirm folder delete
        if (isJqiColourMsgShowing(SUCCESSFUL))
        {
            clickButton(CLOSE);
        }
        else
        {
            throw new Exception("Folder delete not successful: " + foldername);
        }
    }


    // Permissions functions
    public void clickEditIconForFirstRow() throws Exception
    {

        WebElement editIcon = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(
                        xpath("//span[contains(@class, 'glyphicon-pencil')]")));
        editIcon.click();
    }

    protected UserStorageBrowserPage setGroup(final String idToFind,
                                           final String newGroup,
                                            final boolean isModifyNode)
            throws Exception
    {
        clickEditIconForFirstRow();
        WebElement groupInput = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(
                        By.id(idToFind)));

        // Send group name
        sendKeys(groupInput, newGroup);

        waitForAjaxFinished();

        clickButton(SAVE);

        String confirmationBoxMsg = SUCCESSFUL;
        if (isModifyNode == false)
        {
            confirmationBoxMsg = MODIFIED;
        }
        confirmJqiMsg(confirmationBoxMsg);

        return new UserStorageBrowserPage(driver);

    }

    /**
     * Convenience function to click through most of the impromptu .prompt
     * confirmation patterns.
     *
     * @param messageType
     * @throws Exception
     */
    public void confirmJqiMsg(String messageType) throws Exception
    {
        if (isJqiMsgShowing(messageType))
        {
            clickButton(OK);
        }
        else
        {
            throw new Exception("Could not confirm JqiMsg");
        }
    }

    protected UserStorageBrowserPage setGroupOnly(final String idToFind,
                                                  final String newGroup,
                                                  final boolean confirm)
            throws Exception
    {
        final WebElement permissionCheckbox = waitUntil(
                ExpectedConditions.elementToBeClickable(
                        By.id("publicPermission")));
        final WebElement groupInput = find(By.id(idToFind));
        final String currentPermission =
                permissionCheckbox.getAttribute("checked");

        if (currentPermission != null)
        {
            // clear checkbox
            click(permissionCheckbox);
        }

        sendKeys(groupInput, newGroup);

        clickButton(SAVE);

        // read/writeGroupDiv should have 'has-error' class
        // confirm here is conditional because it won't
        // show up if an invalid group has been sent in.
        if (confirm)
        {
            confirmJqiMsg(SUCCESSFUL);
        }

        return new UserStorageBrowserPage(driver);
    }


    protected UserStorageBrowserPage togglePublicAttributeForRow()
            throws Exception
    {
        clickEditIconForFirstRow();
        WebElement permissionCheckbox = waitUntil(
                ExpectedConditions.elementToBeClickable(
                        By.id("publicPermission")));

//        WebElement permissionCheckbox = (new WebDriverWait(driver, 10))
//                .until(ExpectedConditions.elementToBeClickable(
//                        By.id("publicPermission")));

        click(permissionCheckbox);

        waitForAjaxFinished();

        clickButton(SAVE);

        confirmJqiMsg(SUCCESSFUL);

        return new UserStorageBrowserPage(driver);
    }


    /**
     * Gets row number for next row that has an edit icon after the row passed in.
     * Using this kind of function instead of a specific row tends to leave the
     * functions working even when the underlying data changes.
     *
     * @param startRow
     * @return int: row of first edit icon
     * @throws Exception
     */
    public int getNextAvailableEditIconRow(int startRow) throws Exception
    {
        //   not all folders have editable data for currently logged in user
        boolean found = false;
        int rowNum = startRow;

        while (!found)
        {
            // This method throws an exception if the element is not found
            try
            {
                beaconTable.findElement(
                        xpath("//*[@id='beacon']/tbody/tr[" + rowNum
                              + "]/td[2]/span[contains(@class, 'glyphicon-pencil']"));

            }
            catch (Exception e)
            {
                rowNum++;
                continue;
            }
            found = true;
        }

        return rowNum;
    }

    // Row Checkbox related
    public void clickCheckboxForRow(int rowNum) throws Exception
    {
        WebElement firstCheckbox = waitUntil(ExpectedConditions.elementToBeClickable(
                xpath("//*[@id=\"beacon\"]/tbody/tr[" + rowNum + "]/td[1]")));
        click(firstCheckbox);
    }

    public void clickCheckboxForFolder(String folderName) throws Exception
    {
        WebElement folder = find(
                xpath("//*[@id=\"beacon\"]/tbody/tr/td/a[contains(text()," + folderName + ")]"));
        WebElement folderParent = folder.findElement(xpath(".."));
        WebElement folderCheckbox = folderParent
                .findElement(xpath("//div[contains(@class=\"select-checkbox\")]"));
        folderCheckbox.click();
    }


    // Navigation functions
    public UserStorageBrowserPage navToRoot() throws Exception
    {
        // opting for sendKeys because chromedriver
        // doesn't work for click() function for some reason. :(
//        rootButton.sendKeys(Keys.ENTER);
        click(By.id("root"));
        return new UserStorageBrowserPage(driver);
    }

    public UserStorageBrowserPage navUpLevel() throws Exception
    {
        click(leveUpButton);
        return new UserStorageBrowserPage(driver);
    }


    // Inspection functions
    public WebElement getProgressBar() throws Exception
    {
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
        WebElement namecolumn = selectedRow
                .findElement(By.cssSelector("a:nth-of-type(1)"));
        System.out.println(namecolumn.getText());
        return expectedValue.equals(namecolumn.getText());
    }

    boolean verifyFolderSize(int rowNum) throws Exception
    {
        List<WebElement> tableRows = beaconTable.findElements(By.tagName("tr"));
        WebElement selectedRow = tableRows.get(rowNum);
        List<WebElement> columns = selectedRow.findElements(By.tagName("td"));
        String sizeString = columns.get(2).getText();
        return sizeString != null;
    }

    public String getFolderName(int rowNum) throws Exception
    {
        List<WebElement> tableRows = beaconTable.findElements(By.tagName("tr"));
        WebElement selectedRow = tableRows.get(rowNum);
        WebElement namecolumn = selectedRow
                .findElement(By.cssSelector("a:nth-of-type(1)"));
        System.out
                .println("Foldername to be returned: " + namecolumn.getText());
        return namecolumn.getText();
    }

    public String getHeaderText() throws Exception
    {
        System.out.println("Header text: " + folderNameHeader.getText());
        return folderNameHeader.getText();
    }

    public String getValueForRowCol(int rowNum, int colNum)
    {
        String val;

        try
        {
            WebElement el = find(xpath("//*[@id='beacon']/tbody/tr["
                                       + rowNum + "]/td[" + colNum + "]"));
            val = el.getText();
        }
        catch (Exception e)
        {
            // element not found, return empty string
            val = "";
        }
        return val;
    }

    // Permissions Data
    public PermissionsFormData getValuesFromEditIcon() throws Exception
    {
        WebElement editIcon = find(xpath("//span[contains(@class, 'glyphicon-pencil')]/a"));

        PermissionsFormData formData = new PermissionsFormData
                (
                        editIcon.getAttribute("readGroup"),
                        editIcon.getAttribute("writeGroup"),
                        editIcon.getAttribute("publicPermissions")
                );

        return formData;
    }

    boolean isLoggedIn() {

        try
        {
            logoutButton.isDisplayed();
            return true;
        }
        catch (NoSuchElementException e)
        {
            return false;
        }
    }


    private boolean isDisabled(WebElement webEl)
    {
        return webEl.getAttribute("class").contains("disabled");
    }

    public boolean isReadAccess()
    {
        // need to check class of these buttons, look for 'disabled' in there
        return isDisabled(downloadButton) &&
               isDisabled(newdropdownButton) &&
               !isDisabled(searchFilter) &&
               !isDisabled(leveUpButton) &&
               !isDisabled(rootButton);
    }

    public boolean isSubFolder(String folderName) throws Exception
    {
        // Check number of elements in button bar
        // Check state of buttons
        final List<WebElement> navbarElements = navbarButtonList
                .findElements(By.xpath("*"));

        System.out.println(String.format("Navbar has %d elements.",
                                         navbarElements.size()));

        return getHeaderText().contains(folderName) &&
               (navbarElements.size() == 6) &&
               leveUpButton.isDisplayed() &&
               deleteButton.isDisplayed() &&
               rootButton.isDisplayed() &&
               newdropdownButton.isDisplayed() &&
               moredetailsButton.isDisplayed();
    }

    public boolean isRootFolder() throws Exception
    {
        // navigation buttons are NOT displayed in root
        // folder. This will change as functionality is added
        // Currently the navbar only has one child, and it's ID is

        waitForElementPresent(NAVBAR_ELEMENTS_BY);

        return getHeaderText().contains(ROOT_FOLDER_NAME)
               && navbarButtonList.findElements(
                xpath("//*[@id=\"navbar-functions\"]/ul")).size() == 1;
    }

    public boolean isFileSelectedMode(int rowNumber) throws Exception
    {
        // Class of selected row is different:
        // visually it will be different, but for now the change
        // in css class is enough to check
        //*[@id="beacon"]/tbody/tr[1]
        WebElement selectedRow = beaconTable.findElement(
                xpath("//*[@id=\"beacon\"]/tbody/tr[" + rowNumber + "]"));

        if (!selectedRow.getAttribute("class").contains("selected"))
        {
            return false;
        }

        // Behaviour is different if person is logged in or not
        if (isLoggedIn())
        {
            if (!(isDisabled(deleteButton) && isDisabled(downloadButton)))
            {
                return true;
            }
        }
        else
        {   // There will need to be a check for publicly available for download or not?
            if (isDisabled(deleteButton) && !isDisabled(downloadButton))
            {
                return true;
            }
        }

        return false;
    }


    public boolean isDefaultSort()
    {
        // Name column asc is default sort when page loads
        WebElement nameColHeader = beaconTable.findElement(
                xpath("//*[@id=\"beacon_wrapper\"]/div[2]/div/div[1]/div[1]/div/table/thead/tr/th[2]"));
        return nameColHeader.getAttribute("class").equals("sorting_asc");
    }


    // Impromptu convenience functions
    public boolean isJqiMsgShowing(String message)
    {
        try
        {
            waitUntil(ExpectedConditions.elementToBeClickable(
                    xpath("//div[contains(@class, \"jqimessage\") and contains(text(), \""
                          + message + "\")]")));
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public boolean isJqiColourMsgShowing(String message)
    {
        try
        {
            WebElement jqiMsg = (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.elementToBeClickable(
                            xpath("//div[contains(@class, 'jqimessage')]/span[contains(text(), '"
                                  + message + "')]")));
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }


    public boolean isTableEmpty()
    {
        //*[@id="beacon"]/tbody/tr[6]
        List<WebElement> rowList = beaconTable
                .findElements(By.xpath("//*/tbody/tr"));

        if (rowList.size() > 0)
        {
            // Value is different if the entire table is empty, as compared to a list shorted to a particular value
            if (rowList.get(0).findElement(By.xpath("//*/td"))
                    .getAttribute("class").equals("dataTables_empty"))
            {
                return true;
            }
            return false;
        }
        else
        {
            return true;
        }
    }


    /**
     * Verify that the given row has the values passed in
     *
     * @param readGroup
     * @param isPublic
     * @return
     * @throws Exception
     */
    public boolean isPermissionDataForRow(int row, String writeGroup, String readGroup, boolean isPublic) throws
                                                                                                          Exception
    {
        // readGroup is the last column (#5)
        // isPublic defines what might be in that row: text should say 'Public' if isPublic is true
        String rowReadGroup = getValueForRowCol(row, 6);
        String rowWriteGroup = getValueForRowCol(row, 5);
        boolean isPermissionSetCorrect = false;

        if (isPublic)
        {
            if (rowReadGroup.equals("Public") && rowWriteGroup
                    .equals(writeGroup))
            {
                isPermissionSetCorrect = true;
            }
        }
        else if (rowReadGroup.equals(readGroup) && rowWriteGroup
                .equals(writeGroup))
        {
            isPermissionSetCorrect = true;
        }

        return isPermissionSetCorrect;
    }


    public boolean isGroupError(String idToFind) throws Exception
    {
        WebElement readGroupDiv =
                waitUntil(ExpectedConditions.elementToBeClickable(By.id(idToFind)));

        return readGroupDiv.getAttribute("class").contains("has-error");
    }


    public boolean quotaIsDisplayed()
    {
        boolean isDisplayed = false;

        try
        {
            WebElement quota = find(xpath("//div[contains(@class, 'quota')]"));
            isDisplayed = StringUtil.hasText(quota.getText());
        }
        catch (Exception e)
        {
            isDisplayed = false;
        }

        return isDisplayed;
    }

    // --------- Page state wait methods

    public void waitForAjaxFinished() throws Exception
    {
        waitUntil(new ExpectedCondition<Boolean>()
        {
            public Boolean apply(WebDriver driver)
            {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                return (Boolean) js.executeScript("return jQuery.active == 0");
            }
        });
    }


    public void waitForStorageLoad() throws Exception
    {
        // The beacon-progress bar state changes while it's loading
        // the page. Firefox doesn't display whole list until the bar is green
        // instead of striped. Could be this test isn't sufficient but it works
        // to have intTestFirefox not fail.

        waitUntil(ExpectedConditions.attributeContains(
                By.className("beacon-progress"), "class", "progress-bar-success"));
        waitForElementPresent(NAVBAR_ELEMENTS_BY);
    }


    public void waitForPromptFinish()
    {
        // jqifade modal div will be up sometimes for longer than it takes
        // for the scripts to click through. Wait for it to go away
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("jqifade")));
    }


}


class PermissionsFormData {
    private String readGroup;
    private String writeGroup;
    private String publicPermissions;

    public PermissionsFormData(String readGroup, String writeGroup, String publicPerms)
    {
        this.readGroup = readGroup;
        this.writeGroup = writeGroup;
        this.publicPermissions = publicPerms;
    }

    public String getReadGroup() {
        return readGroup;
    }

    public void setReadGroup(String readGroup) {
        this.readGroup = readGroup;
    }


    public String getWriteGroup() {
        return writeGroup;
    }

    public void setWriteGroup(String writeGroup) {
        this.writeGroup = writeGroup;
    }

    public String getPublicPermissions() {
        return publicPermissions;
    }

    public void setPublicPermissions(String publicPermissions) {
        this.publicPermissions = publicPermissions;
    }
}

