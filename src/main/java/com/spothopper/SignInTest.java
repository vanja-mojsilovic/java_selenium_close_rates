package com.spothopper;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jboss.aerogear.security.otp.Totp;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;



// Google Sheets API imports
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;



public class SignInTest {
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




    // Constructor *********************
    public SignInTest(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Methods
    public static WebDriver createWebDriver() {
        ChromeOptions options = new ChromeOptions();
        if (isCIEnvironment()) {
            options.addArguments("--headless=new"); // Use new headless mode for Chrome 109+
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        } else {
            options.addArguments("--start-maximized");
        }
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver(options);
    }

    public void clickElementWithScroll(WebElement enteredElement) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", enteredElement);
        enteredElement.click();
    }


    public static boolean isCIEnvironment() {
        String ci = System.getenv("CI");
        return ci != null && ci.equalsIgnoreCase("true");
    }


    public static String getSecret(String key) {
    String value = System.getenv(key);
    if (value == null || value.isEmpty()) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        value = dotenv.get(key);
    }
    return value;
}

    public WebElement waitForVisibilityOfElement(WebElement element, int numOfSec) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(numOfSec));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public List<WebElement> waitForVisibilityOfElements(List<WebElement> elements, int numOfSec) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(numOfSec));
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }


    public static void sleep(int numOfMilliseconds) {
        try {
            Thread.sleep(numOfMilliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
            e.printStackTrace();
        }
    }

    public void enterEmail(String email) {
        WebElement element = waitForVisibilityOfElement(emailField, 10);
        element.clear();
        element.sendKeys(email);
    }

    public void clickNextButton() {
        WebElement element = waitForVisibilityOfElement(nextButton, 10);
        element.click();
    }

    public void clickSignInWithGoogletButton() {
        WebElement element = waitForVisibilityOfElement(signInWithGoogleButton, 10);
        element.click();
    }

    public void enterEmailInIdentifier(String email) {
        WebElement element = waitForVisibilityOfElement(googleIdentifierField, 10);
        element.clear();
        element.sendKeys(email);
    }

    public void clickNextIdentifierButton() {
        WebElement element = waitForVisibilityOfElement(identifierNextButton, 10);
        element.click();
    }

    public void enterGooglePassword(String email) {
        WebElement element = waitForVisibilityOfElement(googlePasswordField, 10);
        element.clear();
        element.sendKeys(email);
    }

    public void clickNextGooglePasswordButton() {
        WebElement element = waitForVisibilityOfElement(googlePasswordNextButton, 10);
        element.click();
    }

    public void enterGoogleTotPin(String email) {
        WebElement element = waitForVisibilityOfElement(googleTotPinField, 10);
        element.clear();
        element.sendKeys(email);
    }

    public void clickGoogleTotPinNextButton(){
        WebElement element = waitForVisibilityOfElement(googleTotPinNextButton, 10);
        element.click();
    }

    public void enterHubspotTotPin(String email) {
        WebElement element = waitForVisibilityOfElement(hubspotTotPinField, 10);
        element.clear();
        element.sendKeys(email);
    }

    public void clickHubspotTotPinLoginButton(){
        WebElement element = waitForVisibilityOfElement(logInHubspotButton, 10);
        element.click();
    }

    public void clickRememberMeFalseButton(){
        WebElement element = waitForVisibilityOfElement(rememberMeFalseButton, 10);
        element.click();
    }

    public void clickHubspotAccountElement(){
        WebElement element = waitForVisibilityOfElement(hubspotAccountElement, 10);
        element.click();
    }

    public void clickDropDown(){
        try{
            List<WebElement> dropdownElements = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.xpath("//span[contains(text(),'10 rows per page')]")
                ));
            if (!dropdownElements.isEmpty()) {
                dropdownElements.get(0).click();
                sleep(1000);
                WebElement visibleHundredRows = waitForVisibilityOfElement(hundredRowsPerPageDropDown, 10);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", hundredRowsPerPageDropDown);
                sleep(1000);
                visibleHundredRows.click();
            }
        }catch(Exception e){
            System.out.println("Dropdown expansion failed: " + e.getMessage());
        }


    }

    public boolean scrollIntoViewTable(WebDriver driver, String url) {
        boolean result = true;
        driver.get(url);
        System.out.println("Navigating to: " + url);
        sleep(8000);
        try {
            WebElement element = waitForVisibilityOfElement(reportTotal, 60);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        } catch (TimeoutException e) {
            result = false;
            System.out.println("Dropdown for '10 rows per page' not found continuing without expanding rows.");
        } catch (Exception e) {
            result = false;
            System.out.println("Unexpected error while handling rows-per-page dropdown: " + e.getMessage());
        }
        return result;
    }

    public void getSource(JSONArray sourceReports, String fieldName) {
        try {
            WebElement reportNameElement = waitForVisibilityOfElement(reportNameLocator, 15);
            String reportName = reportNameElement.getText();

            Set<String> allSalesReps = new LinkedHashSet<>();

            // Step 1: Scrape first page
            List<WebElement> nameElements = waitForVisibilityOfElements(driver.findElements(SALES_REP), 15);
            if (nameElements == null || nameElements.isEmpty()) {
                System.out.println("No sales rep data found on first page — skipping.");
                return;
            }
            for (WebElement nameElement : nameElements) {
                allSalesReps.add(nameElement.getText().trim());
            }

            // Step 2: Try navigating to second page
            try {
                WebElement firstPageButton = waitForVisibilityOfElement(navigateToFirstPageLocator, 15);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", firstPageButton);
                WebElement nextPageButton = waitForVisibilityOfElement(navigateToSecondPageLocator, 5);
                if (nextPageButton != null && nextPageButton.isDisplayed()) {
                    nextPageButton.click();
                    Thread.sleep(1500); // Wait for second page to load

                    List<WebElement> secondPageElements = waitForVisibilityOfElements(driver.findElements(SALES_REP), 15);
                    for (WebElement nameElement : secondPageElements) {
                        allSalesReps.add(nameElement.getText().trim());
                    }
                }
            } catch (Exception e) {
                System.out.println("Second page not found or not clickable — continuing with first page only.");
            }

            // Step 3: Enrich sourceReports
            int updated = 0, added = 0;
            for (String salesRep : allSalesReps) {
                boolean found = false;
                for (int j = 0; j < sourceReports.length(); j++) {
                    JSONObject existing = sourceReports.getJSONObject(j);
                    if (existing.optString("SalesRep").equalsIgnoreCase(salesRep)) {
                        existing.put(fieldName, reportName);
                        updated++;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    JSONObject newEntry = new JSONObject();
                    newEntry.put("SalesRep", salesRep);
                    newEntry.put("Pmb", "");
                    newEntry.put("Calls", "");
                    newEntry.put("BdrMb", "");
                    newEntry.put("RepSetNomh", "");
                    newEntry.put("BdrNomh", "");
                    newEntry.put("BdrSetSales", "");
                    newEntry.put("RepSetSales", "");
                    newEntry.put(fieldName, reportName);
                    sourceReports.put(newEntry);
                    added++;
                }
            }

            System.out.println(allSalesReps.size() + " report names processed: " +
                               updated + " updated, " + added + " added.");
        } catch (Exception e) {
            System.err.println("Error while extracting source report data: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public void getData(JSONArray closeRates, String fieldName) {
        //try {
            WebElement reportNameElement = waitForVisibilityOfElement(reportNameLocator, 15);
            String reportName = reportNameElement.getText();
            int startIndex = 0;
            int endIndex = reportName.indexOf("-");
            String team = reportName.substring(startIndex, endIndex).trim();

            Map<String, Integer> aggregatedCounts = new LinkedHashMap<>();

            // Fetch data from first page
            aggregatePageData(aggregatedCounts);

            // Try navigating to second page
            try {
                WebElement firstPageButton = waitForVisibilityOfElement(navigateToFirstPageLocator, 15);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", firstPageButton);
                WebElement nextPageButton = waitForVisibilityOfElement(navigateToSecondPageLocator, 5);
                if (nextPageButton != null && nextPageButton.isDisplayed()) {
                    nextPageButton.click();
                    System.out.println("Next Page Button Clicked!");
                    Thread.sleep(1500);
                    aggregatePageData(aggregatedCounts);
                }
            } catch (Exception e) {
                System.out.println("Second page not found or not clickable — continuing with first page only.");
            }

            // Step 2: Insert into closeRates
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
                    newEntry.put("Pmb", "");
                    newEntry.put("Calls", "");
                    newEntry.put("BdrMb", "");
                    newEntry.put("RepSetNomh", "");
                    newEntry.put("BdrNomh", "");
                    newEntry.put("BdrSetSales", "");
                    newEntry.put("RepSetSales", "");
                    newEntry.put("Team", team);
                    newEntry.put(fieldName, totalCount);
                    closeRates.put(newEntry);
                    added++;
                }
            }
            System.out.println(aggregatedCounts.size() + " unique reps processed: " +
                               updated + " updated, " + added + " added.");
        /*
        } catch (Exception e) {
            System.err.println("Error while extracting close rate data: " + e.getMessage());
            e.printStackTrace();
        }
        */
    }


    private void aggregatePageData(Map<String, Integer> aggregatedCounts) {
        List<WebElement> nameElements = waitForVisibilityOfElements(driver.findElements(SALES_REP), 15);
        List<WebElement> countElements = waitForVisibilityOfElements(driver.findElements(COUNT_OF_COMPANIES), 15);

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


    public void updateCloseRates(JSONArray closeRates,JSONArray sourceReports, String spreadsheetId) {
        if (closeRates == null || closeRates.isEmpty()) {
            System.out.println("No close rate data to update — skipping sheet update.");
            return;
        }
        try {
            // Close rates
            String rangeForCounts = "last_month!A2";
            Sheets sheetsService = getSheetsService();
            List<List<Object>> rows = new ArrayList<>();
            for (int i = 0; i < closeRates.length(); i++) {
                JSONObject entry = closeRates.getJSONObject(i);
                rows.add(Arrays.asList(
                    entry.optString("SalesRep", ""),
                    entry.optString("Pmb", ""),
                    entry.optString("Calls", ""),
                    entry.optString("BdrMb", ""),
                    entry.optString("RepSetNomh", ""),
                    entry.optString("BdrNomh", ""),
                    entry.optString("BdrSetSales", ""),
                    entry.optString("RepSetSales", ""),
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
            /*
            // Source reports
            String rangeForSources = "sources!A2";
            Sheets sourceSheetsService = getSheetsService();
            List<List<Object>> sourceRows = new ArrayList<>();
            for (int i = 0; i < sourceReports.length(); i++) {
                JSONObject entry = sourceReports.getJSONObject(i);
                sourceRows.add(Arrays.asList(
                    entry.optString("SalesRep", ""),
                    entry.optString("Pmb", ""),
                    entry.optString("Calls", ""),
                    entry.optString("BdrMb", ""),
                    entry.optString("RepSetNomh", ""),
                    entry.optString("BdrNomh", ""),
                    entry.optString("BdrSetSales", ""),
                    entry.optString("RepSetSales", "")

                ));
            }
            ValueRange sourceBody = new ValueRange().setValues(sourceRows);
            UpdateValuesResponse SourceResponse = sourceSheetsService.spreadsheets().values()
                .update(spreadsheetId, rangeForSources, sourceBody)
                .setValueInputOption("RAW")
                .execute();
            System.out.println("Updated range: " + SourceResponse.getUpdatedRange());
            System.out.println("Replaced " + sourceRows.size() + " rows in 'close_rates_result' sheet.");
            */

        } catch (IOException | GeneralSecurityException e) {
            System.err.println("Failed to update Google Sheet: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private static Sheets getSheetsService() throws IOException, GeneralSecurityException {
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
                                    JSONArray sourceReports,
                                    String spreadsheetId) {
        for (String url : reportUrls) {
            boolean hasTable = scrollIntoViewTable(driver, url);
            sleep(2000);
            if (hasTable) {
                clickDropDown();
                sleep(1000);
                getData(closeRates,reportType);
                //getSource(sourceReports,reportType);
            }
            updateCloseRates(closeRates,sourceReports,spreadsheetId);
        }
    }




   

    // Main method *****************
    public static void main(String[] args) {
        String email = getSecret("GOOGLE_EMAIL_VANJA");
        String googlePassword = getSecret("GOOGLE_PASSWORD_VANJA");
        String googleTotPin = getSecret("GOOGLE_TOTPIN_VANJA");
        String hubspotTotPin = getSecret("HUBSPOT_TOTPIN_VANJA");
        String spreadsheetId = "1Pxgp3zUZ6khudOVD--5aI3oFd8NQzmkqigCj1ZlScfY";

        List<String> pmbReportUrlsThisMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15295898/132144905",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820555/113078679",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821140/113082895",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931736/109173589",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821612/113086364",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829866/113147378",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829954/113148099",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830325/113150823",
            "https://app.hubspot.com/reports-dashboard/587184/view/16817768/144354606",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008070/129970525",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916712/120523319",
            "https://app.hubspot.com/reports-dashboard/587184/view/12822090/113090236"
        );

        List<String> callsReportUrlsThisMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15295898/134269782"
            // add later
        );

        List<String> pmbReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15296315/132148069",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820743/113080121",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821545/113085819",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931971/109173462",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821874/113088373",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830013/113148524",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830092/113149066",

            "https://app.hubspot.com/reports-dashboard/587184/view/16817770/144354630",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008226/129971621",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916924/120524801",

            "https://app.hubspot.com/reports-dashboard/587184/view/17484591/149762742",
            "https://app.hubspot.com/reports-dashboard/587184/view/17485043/149766513"
        );



        List<String> callsReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15296315/132148075",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820743/128527614",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821545/128528016",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931971/128526818",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821874/128529603",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830013/128530561",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830092/128531073",

            "https://app.hubspot.com/reports-dashboard/587184/view/16817770/144354636",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008226/129971627",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916924/128532303",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484591/149762748",
            "https://app.hubspot.com/reports-dashboard/587184/view/17485043/149766519"
        );

        List<String> BdrMbReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15296315/132148071",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820743/113080123",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821545/113085821",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931971/109313705",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821874/113088375",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830013/113148526",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830092/113149068",

            "https://app.hubspot.com/reports-dashboard/587184/view/16817770/144354632",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008226/129971623",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916924/120524803",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484591/149762744",
            "https://app.hubspot.com/reports-dashboard/587184/view/17485043/149766515"
        );

        List<String> RepSetNomhReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15296315/132148070",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820743/113080122",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821545/113085820",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931971/109174100",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821874/113088374",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830013/113148525",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830092/113149067",

            "https://app.hubspot.com/reports-dashboard/587184/view/16817770/144354631",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008226/129971622",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916924/120524802",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484591/149762743",
            "https://app.hubspot.com/reports-dashboard/587184/view/17485043/149766514"

        );

        List<String> BdrNomhReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15296315/132148072",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820743/113080124",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821545/113085822",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931971/109314151",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821874/113088376",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830013/113148527",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830092/113149069",

            "https://app.hubspot.com/reports-dashboard/587184/view/16817770/144354633",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008226/129971624",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916924/120524804",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484591/149762745",
            "https://app.hubspot.com/reports-dashboard/587184/view/17485043/149766516"

        );

        List<String> BdrSetSalesReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15296315/147246052",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820743/147255130",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821545/147255435",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931971/147144069",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821874/147271739",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830013/147274767",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830092/147275351",

            "https://app.hubspot.com/reports-dashboard/587184/view/16817770/147278340",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008226/147277385",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916924/147277063",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484591/149762741",
            "https://app.hubspot.com/reports-dashboard/587184/view/17485043/149766512"


        );

        List<String> RepSetSalesReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15296315/147246035",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820743/147255096",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821545/147255432",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931971/147144021",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821874/147271727",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830013/147274760",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830092/147275278",

            "https://app.hubspot.com/reports-dashboard/587184/view/16817770/147278332",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008226/147277362",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916924/147277060",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484591/149762740",
            "https://app.hubspot.com/reports-dashboard/587184/view/17485043/149766511"

        );



        //

        JSONArray closeRates = new JSONArray();
        JSONArray sourceReports = new JSONArray();

        WebDriver driver = null;
        try {
            WebDriverManager.chromedriver().setup();
            driver = createWebDriver();
            driver.get("https://app.hubspot.com");
            sleep(2000);

            SignInTest signInTest = new SignInTest(driver);
            signInTest.enterEmail(email);
            signInTest.clickNextButton();
            sleep(2000);
            signInTest.clickSignInWithGoogletButton();
            signInTest.enterEmailInIdentifier(email);
            signInTest.clickNextIdentifierButton();
            sleep(2000);
            signInTest.enterGooglePassword(googlePassword);
            signInTest.clickNextGooglePasswordButton();
            sleep(2000);
            signInTest.enterGoogleTotPin(new Totp(googleTotPin).now());
            signInTest.clickGoogleTotPinNextButton();
            sleep(2000);
            signInTest.enterHubspotTotPin(new Totp(hubspotTotPin).now());
            signInTest.clickHubspotTotPinLoginButton();
            sleep(2000);
            signInTest.clickRememberMeFalseButton();
            sleep(4000);
            driver.get("https://app.hubspot.com");
            signInTest.clickHubspotAccountElement();
            sleep(2000);

            signInTest.processReportList(pmbReportUrlsLastMonth,"Pmb",driver,closeRates,sourceReports,spreadsheetId);
            signInTest.processReportList(callsReportUrlsLastMonth,"Calls",driver,closeRates,sourceReports,spreadsheetId);
            signInTest.processReportList(BdrMbReportUrlsLastMonth,"BdrMb",driver,closeRates,sourceReports,spreadsheetId);
            signInTest.processReportList(RepSetNomhReportUrlsLastMonth,"RepSetNomh",driver,closeRates,sourceReports,spreadsheetId);
            signInTest.processReportList(BdrNomhReportUrlsLastMonth,"BdrNomh",driver,closeRates,sourceReports,spreadsheetId);
            signInTest.processReportList(BdrSetSalesReportUrlsLastMonth,"BdrSetSales",driver,closeRates,sourceReports,spreadsheetId);
            signInTest.processReportList(RepSetSalesReportUrlsLastMonth,"RepSetSales",driver,closeRates,sourceReports,spreadsheetId);

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

}
