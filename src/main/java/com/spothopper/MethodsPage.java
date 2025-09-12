package com.spothopper;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;

import org.json.JSONArray;
import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.*;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import org.jboss.aerogear.security.otp.Totp;
import org.openqa.selenium.remote.RemoteWebDriver;


public class MethodsPage extends AbstractClass {
    // Variables
    private WebDriver driver;


    // Locators

    @FindBy(xpath = "//input[@data-test-id='email-input-field']")
    WebElement emailField;

    @FindBy(xpath = "//button[@data-test-id='password-login-button']")
    WebElement nextButton;

    @FindBy(xpath = "//button[@data-test-id='google-sign-in']")
    WebElement signInWithGoogleButton;

    @FindBy(xpath = "//input[@id='identifierId']")
    WebElement googleIdentifierField;


    @FindBy(xpath = "//div[@id='identifierNext']//button")
    WebElement identifierNextButton;

    @FindBy(xpath = "//div[@id='password']//input")
    WebElement googlePasswordField;

    @FindBy(xpath = "//div[@id='passwordNext']//button")
    WebElement googlePasswordNextButton;

    @FindBy(xpath = "//input[@id='totpPin']")
    WebElement googleTotPinField;

    @FindBy(xpath = "//div[@id='totpNext']//button")
    WebElement googleTotPinNextButton;

    @FindBy(xpath = "//input[@id='code']")
    WebElement hubspotTotPinField;

    @FindBy(xpath = "//button[@data-test-id='confirm-to-login-submit']")
    WebElement logInHubspotButton;

    @FindBy(xpath = "//button[@data-2fa-rememberme='false']")
    WebElement rememberMeFalseButton;

    @FindBy(xpath = "//small[contains(text(),'587184')]")
    WebElement hubspotAccountElement;

    @FindBy(xpath = "//div[@data-test-id='report-loaded']//table")
    WebElement tableWithOwnersAndCounts;

    @FindBy(xpath = "//p[text()='Report Total']")
    WebElement reportTotal;

    @FindBy(xpath = "//div[@data-test-id='report-loaded']//table//tr/td[1]")
    List<WebElement> tableWithOwnerNames;

    private static final By SALES_REP = By.xpath("//div[@data-test-id='report-loaded']//table//tr/td[1]");

    private static final By COUNT_OF_COMPANIES = By.xpath("//div[@data-test-id='report-loaded']//table//tr/td[2]");

    @FindBy(xpath = "//span[contains(text(),'10 rows per page')]")
    List<WebElement> tenRowsPerPageDropDown;

    @FindBy(xpath = "//span[contains(text(),'100 rows per page')]")
    WebElement hundredRowsPerPageDropDown;

    @FindBy(xpath = "//div[@data-test-id='report-loaded']//table//tr/td[2]")
    List<WebElement> tableWithCompanyCounts;

    @FindBy(xpath = "//div[@data-test-id = 'srv-report-name']")
    WebElement reportNameLocator;

    @FindBy(xpath = "(//*[local-name()='g' and contains(@class,'highcharts-xaxis-labels')])[1]/*[local-name()='text']")
    private List<WebElement> xAxisLabels;

    @FindBy(xpath = "(//*[local-name()='g' and contains(@class,'highcharts-label highcharts-stack-labels')])[1]/*[local-name()='text']")
    private List<WebElement> yAxisLabels;

    @FindBy(xpath = "//button[@data-page-number = '1']")
    WebElement navigateToFirstPageLocator;

    @FindBy(xpath = "//button[@data-page-number = '2']")
    WebElement navigateToSecondPageLocator;

    // Constructor
    public MethodsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Methods


    public void enterEmail(WebDriver driver, String email) {
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,emailField, 10);
        element.clear();
        element.sendKeys(email);
    }
     public void clickElementWithScroll(WebElement enteredElement) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", enteredElement);
        enteredElement.click();
    }

    public void clickNextButton() {
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,nextButton, 10);
        element.click();
    }

    public void clickSignInWithGoogletButton() {
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,signInWithGoogleButton, 10);
        element.click();
    }

    public void enterEmailInIdentifier(String email) {
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,googleIdentifierField, 10);
        element.clear();
        element.sendKeys(email);
    }

    public void clickNextIdentifierButton() {
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,identifierNextButton, 10);
        element.click();
    }

    public void enterGooglePassword(String email) {
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,googlePasswordField, 10);
        element.clear();
        element.sendKeys(email);
    }

    public void clickNextGooglePasswordButton() {
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,googlePasswordNextButton, 10);
        element.click();
    }

    public void enterGoogleTotPin(String email) {
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,googleTotPinField, 10);
        element.clear();
        element.sendKeys(email);
    }

    public void clickGoogleTotPinNextButton(){
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,googleTotPinNextButton, 10);
        element.click();
    }

    public void enterHubspotTotPin(String email) {
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,hubspotTotPinField, 10);
        element.clear();
        element.sendKeys(email);
    }

    public void clickHubspotTotPinLoginButton(){
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,logInHubspotButton, 10);
        element.click();
    }

    public void clickRememberMeFalseButton(){
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,rememberMeFalseButton, 10);
        element.click();
    }

    public void clickHubspotAccountElement(){
        WebElement element = AbstractClass.waitForVisibilityOfElement(driver,hubspotAccountElement, 10);
        element.click();
    }

    public void clickDropDown(){
        try{
            List<WebElement> dropdownElements = new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.xpath("//span[contains(text(),'10 rows per page')]")
                ));
            if (!dropdownElements.isEmpty()) {
                dropdownElements.get(0).click();
                AbstractClass.sleep(1000);
                WebElement visibleHundredRows = AbstractClass.waitForVisibilityOfElement(driver,hundredRowsPerPageDropDown, 10);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", hundredRowsPerPageDropDown);
                AbstractClass.sleep(1000);
                visibleHundredRows.click();
            }
        }catch(Exception e){
            System.out.println("Dropdown - 10 rows per page - expansion failed: " + e.getMessage());
        }


    }

    public boolean scrollIntoViewTable(WebDriver driver, String url) {
        System.out.println("Navigating to: " + url);
        boolean result = true;
        try {
            driver.get(url);
            new WebDriverWait(driver, Duration.ofSeconds(60)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete")
            );
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(60))
                .until(ExpectedConditions.visibilityOf(reportTotal));
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element
            );
        } catch (TimeoutException e) {
            result = false;
            System.out.println("Timeout: 'Report Total' element not found — continuing without expanding rows.");
        } catch (Exception e) {
            result = false;
            System.out.println("Unexpected error while scrolling to 'Report Total': " + e.getMessage());
        }
        return result;
    }



    public void getData(JSONArray closeRates, String fieldName) {
        WebElement reportNameElement = AbstractClass.waitForVisibilityOfElement(driver,reportNameLocator, 60);
        String reportName = reportNameElement.getText();
        int startIndex = 0;
        int endIndex = reportName.indexOf("-");
        String team = reportName.substring(startIndex, endIndex).trim();
        System.out.println(team);
        Map<String, Integer> aggregatedCounts = new LinkedHashMap<>();
        // Fetch data from first page
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // preserve interrupt status
            e.printStackTrace(); // or log it
        }
        aggregatePageData(aggregatedCounts);
        // Try navigating to second page
        try {
            WebElement firstPageButton = AbstractClass.waitForVisibilityOfElement(driver,navigateToFirstPageLocator, 15);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", firstPageButton);
            WebElement nextPageButton = AbstractClass.waitForVisibilityOfElement(driver,navigateToSecondPageLocator, 5);
            if (nextPageButton != null && nextPageButton.isDisplayed()) {
                nextPageButton.click();
                System.out.println("Next Page Button Clicked!");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // preserve interrupt status
                    e.printStackTrace(); // or log it
                }
                aggregatePageData(aggregatedCounts);
            }
        } catch (Exception e) {
            System.out.println("Second page not found or not clickable — continuing with first page only.");
        }

        // Step 2: Insert into JSON Array closeRates
        int updated = 0, added = 0;
        for (Map.Entry<String, Integer> entry : aggregatedCounts.entrySet()) {
            String salesRep = entry.getKey();
            int totalCount = entry.getValue();
            boolean found = false;
            for (int j = 0; j < closeRates.length(); j++) {
                JSONObject existing = closeRates.getJSONObject(j);
                if (existing.optString("SalesRep").equalsIgnoreCase(salesRep)) {
                    existing.put(fieldName, totalCount);
                    updated++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                JSONObject newEntry = new JSONObject();
                newEntry.put("SalesRep", salesRep);
                newEntry.put("Pmb", "0");
                newEntry.put("Calls", "0");
                newEntry.put("BdrMb", "0");
                newEntry.put("RepSetNomh", "0");
                newEntry.put("BdrNomh", "0");
                newEntry.put("BdrSetSales", "0");
                newEntry.put("RepSetSales", "0");
                newEntry.put("Team", team);
                newEntry.put(fieldName, totalCount);
                closeRates.put(newEntry);
                added++;
            }
        }
        System.out.println(aggregatedCounts.size() + " unique reps processed: " +
                           updated + " updated, " + added + " added.");

    }


    private void aggregatePageData(Map<String, Integer> aggregatedCounts) {
        List<WebElement> nameElements = AbstractClass.waitForVisibilityOfElements(driver,driver.findElements(SALES_REP), 15);
        List<WebElement> countElements = AbstractClass.waitForVisibilityOfElements(driver,driver.findElements(COUNT_OF_COMPANIES), 15);

        if (nameElements == null || countElements == null ||
            nameElements.isEmpty() || countElements.isEmpty()) {
            System.out.println("No data found on this page — skipping.");
            return;
        }

        int size = Math.min(nameElements.size(), countElements.size());
        for (int i = 0; i < size; i++) {
            String salesRep = nameElements.get(i).getText().trim();
            String countText = countElements.get(i).getText().trim();
            int count = countText.isEmpty() ? 0 : Integer.parseInt(countText);
            aggregatedCounts.merge(salesRep, count, Integer::sum);
        }
    }


    public void updateCloseRates(JSONArray closeRates, String spreadsheetId,String rangeForCounts) {
        if (closeRates == null || closeRates.isEmpty()) {
            System.out.println("No close rate data to update — skipping sheet update.");
            return;
        }
        try {
            Sheets sheetsService = getSheetsService();
            List<List<Object>> rows = new ArrayList<>();
            for (int i = 0; i < closeRates.length(); i++) {
                JSONObject entry = closeRates.getJSONObject(i);
                rows.add(Arrays.asList(
                    entry.optString("SalesRep", "0"),
                    entry.optString("Pmb", "0"),
                    entry.optString("Calls", "0"),
                    entry.optString("BdrMb", "0"),
                    entry.optString("RepSetNomh", "0"),
                    entry.optString("BdrNomh", "0"),
                    entry.optString("BdrSetSales", "0"),
                    entry.optString("RepSetSales", "0"),
                    entry.optString("Team", "")
                ));
            }
            ValueRange body = new ValueRange().setValues(rows);
            UpdateValuesResponse response = sheetsService.spreadsheets().values()
                .update(spreadsheetId, rangeForCounts, body)
                .setValueInputOption("RAW")
                .execute();
            System.out.println("Updated range: " + response.getUpdatedRange());
            System.out.println("Replaced " + rows.size() + " rows in 'close_rates_result' sheet.");


        } catch (IOException | GeneralSecurityException e) {
            System.err.println("Failed to update Google Sheet: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Dotenv dotenv = Dotenv.load();
        String credentialsPath = dotenv.get("GOOGLE_CREDENTIALS_PATH");
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("credentials.json"))
            .createScoped(List.of(SheetsScopes.SPREADSHEETS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), requestInitializer)
            .setApplicationName("Close Rates Sync")
            .build();
    }

    public void processReportList(List<String> reportUrls,
                              String reportType,
                              WebDriver driver,
                              JSONArray closeRates,
                              String rangeForCounts,
                              String spreadsheetId,
                              String email,
                              String googlePassword,
                              String googleTotPin,
                              String hubspotTotPin) {

        SignInTest signInTest = new SignInTest(driver); // initial binding

        for (String url : reportUrls) {
            try {

                if (!isSessionAlive(driver)) {
                    System.out.println("Session expired — restarting driver.");
                    driver.quit();
                    driver = createWebDriver();
                    performLogin(driver, email, googlePassword, googleTotPin, hubspotTotPin);
                    signInTest = new SignInTest(driver);
                }
                boolean hasTable = scrollIntoViewTable(driver, url);
                if (hasTable) {
                    clickDropDown();
                    AbstractClass.sleep(1000);
                    getData(closeRates, reportType);
                } else {
                    System.out.println("Skipping data extraction — table not found.");
                }
                updateCloseRates(closeRates, spreadsheetId, rangeForCounts);
            } catch (Exception e) {
                System.out.println("Error processing report: " + url);
                e.printStackTrace();
            }
        }
    }


    public void performLogin(WebDriver driver,
                                String email,
                                String googlePassword,
                                String googleTotPin,
                                String hubspotTotPin) {
        SignInTest signInTest = new SignInTest(driver);
        driver.get("https://app.hubspot.com");
        AbstractClass.sleep(2000);
        enterEmail(driver,email);
        clickNextButton();
        AbstractClass.sleep(2000);
        clickSignInWithGoogletButton();
        AbstractClass.sleep(2000);
        enterEmailInIdentifier(email);
        clickNextIdentifierButton();
        AbstractClass.sleep(2000);
        enterGooglePassword(googlePassword);
        clickNextGooglePasswordButton();
        AbstractClass.sleep(2000);
        enterGoogleTotPin(new Totp(googleTotPin).now());
        clickGoogleTotPinNextButton();
        AbstractClass.sleep(2000);
        enterHubspotTotPin(new Totp(hubspotTotPin).now());
        clickHubspotTotPinLoginButton();
        AbstractClass.sleep(2000);
        clickRememberMeFalseButton();
        AbstractClass.sleep(2000);
        driver.get("https://app.hubspot.com");
        clickHubspotAccountElement();
        AbstractClass.sleep(2000);
    }




    public boolean isSessionAlive(WebDriver driver) {
        try {
            return ((RemoteWebDriver) driver).getSessionId() != null;
        } catch (Exception e) {
            return false;
        }
    }







}
