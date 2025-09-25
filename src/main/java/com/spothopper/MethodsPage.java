package com.spothopper;

// Java I/O and utility imports
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.*;

// JSON handling
import org.json.JSONArray;
import org.json.JSONObject;

// Web scraping
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// Selenium WebDriver
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;

// WebDriver manager and environment variables
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;

// Google Sheets API
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

// OTP for 2FA
import org.jboss.aerogear.security.otp.Totp;


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

    @FindBy(xpath = "(//div[@data-test-id='report-loaded']//table)[5]")
    WebElement tableWithOwnersAndCounts;

    @FindBy(xpath = "//div[@data-test-id='report-loaded']//table//p[text()='Report Total']")
    List<WebElement> reportTotalLocator;

    private static final By SALES_REP = By.xpath("(//div[@data-test-id='report-loaded']//table)[5]//tr/td[1]");

    private static final By COUNT_OF_COMPANIES = By.xpath("(//div[@data-test-id='report-loaded']//table)[5]//tr/td[2]");

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

    public MethodsPage() {
        System.out.println("Constructor without driver!");
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

    public void scrollIntoViewTable(WebDriver driver, String url) {
        System.out.println("Navigating to: " + url);
        try {
            driver.get(url);
            System.out.println("Wait 20 seconds...");
            AbstractClass.sleep(20000);
            new WebDriverWait(driver, Duration.ofSeconds(60)).until(
                    webDriver -> ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState").equals("complete")
            );
            System.out.println("The page is completely loaded!");

            List<WebElement> reportTotalElements = waitForVisibilityOfElements(driver, reportTotalLocator, 20);

            if (reportTotalElements == null || reportTotalElements.isEmpty()) {
                System.out.println("Report Total elements not found via XPath.");
            } else {
                WebElement fifthElement = reportTotalElements.size() >= 5 ? reportTotalElements.get(4) : null;
                if (fifthElement != null) {
                    System.out.println("Fifth 'Report Total' element found and ready.");
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior:'smooth',block:'center'});", fifthElement);
                } else {
                    System.out.println("Fewer than 5 'Report Total' elements found.");
                }
            }


        } catch (TimeoutException e) {
            System.out.println("Timeout: 'Report Total' element not found. Skipping scroll.");
        } catch (Exception e) {
            System.out.println("Unexpected error while scrolling to 'Report Total': " + e.getMessage());
        }
    }




    public void getData(JSONArray closeRates, String fieldName) {
        WebElement reportNameElement = AbstractClass.waitForVisibilityOfElement(driver,reportNameLocator, 60);
        String reportName = reportNameElement.getText();
        System.out.println(reportName);
        Map<String, Integer> aggregatedCounts = new LinkedHashMap<>();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        sleep(1000);
        aggregatePageData(aggregatedCounts);

        // Insert into JSON Array closeRates
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
                newEntry.put(fieldName, totalCount);
                closeRates.put(newEntry);
                added++;
            }
        }
        System.out.println(aggregatedCounts.size() + " unique reps processed: " +
                           updated + " updated, " + added + " added.");

    }


    private void aggregatePageData(Map<String, Integer> aggregatedCounts) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        List<WebElement> nameElements = (List<WebElement>) js.executeScript(
                "return Array.from(document.querySelectorAll(" +
                        "'div[data-test-id=\"report-loaded\"] table tr td:first-child'" +
                        "));"
        );

        List<WebElement> countElements = (List<WebElement>) js.executeScript(
                "return Array.from(document.querySelectorAll(" +
                        "'div[data-test-id=\"report-loaded\"] table tr td:nth-child(2)'" +
                        "));"
        );

        System.out.println("Sales reps found via JS: " + nameElements.size());
        System.out.println("Company counts found via JS: " + countElements.size());

        if (nameElements == null || countElements == null ||
                nameElements.isEmpty() || countElements.isEmpty()) {
            System.out.println("No data found on this page! Skipping.");
            return;
        }

        int size = Math.min(nameElements.size(), countElements.size());

        // Phase 1: Count how many times "Report Total" appears
        int reportTotalCount = 0;
        for (int i = 0; i < size; i++) {
            String salesRep = nameElements.get(i).getText().trim().toLowerCase();
            String countOfActivities = countElements.get(i).getText().trim().toLowerCase();
            System.out.println(salesRep + " " + countOfActivities);
            if (salesRep.contains("report total")) {
                reportTotalCount++;
            }
        }

        System.out.println("'Report Total' appears " + reportTotalCount + " times.");

        // Phase 2: Aggregate rows after (n - 1)th "Report Total"
        int reportTotalSeen = 0;
        boolean startAggregation = false;

        for (int i = 0; i < size; i++) {
            String salesRep = nameElements.get(i).getText().trim();
            String countText = countElements.get(i).getText().trim();

            if (salesRep.toLowerCase().contains("report total")) {
                reportTotalSeen++;
                System.out.println("Report Total #" + reportTotalSeen + ": " + countText);
                if (reportTotalSeen == reportTotalCount - 1) {
                    System.out.println("Starting aggregation after Report Total #" + reportTotalSeen);
                    startAggregation = true;
                }
                continue;
            }

            if (!startAggregation) {
                continue;
            }

            int count = 0;
            if (!countText.isEmpty()) {
                try {
                    String sanitized = countText.replaceAll("[^\\d]", "");
                    count = Integer.parseInt(sanitized);
                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse count for sales rep '" + salesRep + "' from text: '" + countText + "'");
                    e.printStackTrace();
                    continue;
                }
            }

            aggregatedCounts.merge(salesRep, count, Integer::sum);
        }
    }




    public void updateCloseRates(JSONArray closeRates, String spreadsheetId,String rangeForCounts) {
        if (closeRates == null || closeRates.isEmpty()) {
            System.out.println("No close rate data to update! Skipping sheet update.");
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
                    entry.optString("RepSetSales", "0")
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

        SignInTest signInTest = new SignInTest(driver);

        for (String url : reportUrls) {
            try {

                if (!isSessionAlive(driver)) {
                    System.out.println("Session expired — restarting driver.");
                    driver.quit();
                    driver = createWebDriver();
                    performLogin(driver, email, googlePassword, googleTotPin, hubspotTotPin);
                    signInTest = new SignInTest(driver);
                }
                scrollIntoViewTable(driver, url);
                AbstractClass.sleep(1000);
                getData(closeRates, reportType);
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

    public static JSONArray buildSalesRepActivityJSON(List<String> listOfCsvFiles, List<String> jsonFields) {
        Map<String, JSONObject> salesRepMap = new HashMap<>();

        for (int i = 0; i < listOfCsvFiles.size(); i++) {
            String fileName = listOfCsvFiles.get(i);
            String fieldName = jsonFields.get(i);

            try (BufferedReader reader = new BufferedReader(new FileReader("resources/" + fileName))) {
                String line;
                boolean isHeader = true;

                while ((line = reader.readLine()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }

                    String[] columns = line.split("\",\"");
                    String salesRep = columns[0].replace("\"", "").trim();

                    // Get or create JSON entry
                    JSONObject entry = salesRepMap.getOrDefault(salesRep, new JSONObject());
                    entry.put("SalesRep", salesRep);

                    // Initialize all fields if new
                    for (String field : jsonFields) {
                        if (!entry.has(field)) {
                            entry.put(field, "0");
                        }
                    }

                    // Increment the current field
                    int currentCount = Integer.parseInt(entry.getString(fieldName));
                    entry.put(fieldName, String.valueOf(currentCount + 1));

                    salesRepMap.put(salesRep, entry);
                }

            } catch (IOException e) {
                System.err.println("Error reading file: " + fileName);
                e.printStackTrace();
            }
        }

        // Convert map to JSONArray
        return new JSONArray(salesRepMap.values());
    }






}
