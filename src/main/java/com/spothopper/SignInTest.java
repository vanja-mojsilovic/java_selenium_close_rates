package com.spothopper;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @FindBy(xpath = "//div[@data-test-id='report-loaded']//table//tr/td[1]")
    List<WebElement> tableWithOwnerNames;

    private static final By SALES_REP = By.xpath("//div[@data-test-id='report-loaded']//table//tr/td[1]");

    private static final By COUNT_OF_COMPANIES = By.xpath("//div[@data-test-id='report-loaded']//table//tr/td[2]");

    //By rowsPerPageDropDown = By.xpath("//span[contains(text(),'10 rows per page')]");
    @FindBy(xpath = "//span[contains(text(),'10 rows per page')]")
    List<WebElement> tenRowsPerPageDropDown;

    @FindBy(xpath = "//span[contains(text(),'100 rows per page')]")
    WebElement hundredRowsPerPageDropDown;

    @FindBy(xpath = "//div[@data-test-id='report-loaded']//table//tr/td[2]")
    List<WebElement> tableWithCompanyCounts;


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
            List<WebElement> dropdownElements = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.xpath("//span[contains(text(),'10 rows per page')]")
                ));
            if (!dropdownElements.isEmpty()) {
                dropdownElements.get(0).click();
                sleep(1000);
                WebElement visibleHundredRows = waitForVisibilityOfElement(hundredRowsPerPageDropDown, 10);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", hundredRowsPerPageDropDown);
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
        sleep(10000);
        try {
            WebElement element = waitForVisibilityOfElement(tableWithOwnersAndCounts, 10);
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

    public void getData(JSONArray closeRates,String fieldName) {
        try {
            List<WebElement> nameElements = driver.findElements(SALES_REP);
            List<WebElement> countElements = driver.findElements(COUNT_OF_COMPANIES);
            if (nameElements == null || countElements == null ||
                nameElements.isEmpty() || countElements.isEmpty()) {
                System.out.println("No data found — skipping.");
                return;
            }
            nameElements = waitForVisibilityOfElements(nameElements, 15);
            countElements = waitForVisibilityOfElements(countElements, 15);
            int size = Math.min(nameElements.size(), countElements.size());
            int updated = 0, added = 0;
            for (int i = 0; i < size; i++) {
                String salesRep = nameElements.get(i).getText().trim();
                String countOfCompanies = countElements.get(i).getText().trim();
                boolean found = false;
                for (int j = 0; j < closeRates.length(); j++) {
                    JSONObject existing = closeRates.getJSONObject(j);
                    if (existing.optString("SalesRep").equalsIgnoreCase(salesRep)) {
                        existing.put(fieldName, countOfCompanies);
                        updated++;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    JSONObject entry = new JSONObject();
                    entry.put("SalesRep", salesRep);
                    entry.put("Pmb", "");
                    entry.put("Calls", "");
                    entry.put("BdrMb", "");
                    entry.put("RepSetNomh", "");
                    entry.put("BdrNomh", "");
                    entry.put("BDRSetSales", "");
                    entry.put("RepSetSales", "");
                    entry.put(fieldName, countOfCompanies);
                    closeRates.put(entry);
                    added++;
                }
            }
            System.out.println(size + " entries processed: " + updated + " updated, " + added + " added.");
        } catch (Exception e) {
            System.err.println("Error while extracting close rate data: " + e.getMessage());
            e.printStackTrace();
        }
    }





    public void updateCloseRates(JSONArray closeRates, String spreadsheetId) {
        if (closeRates == null || closeRates.isEmpty()) {
            System.out.println("No close rate data to append — skipping sheet update.");
            return;
        }
        try {
            Sheets sheetsService = getSheetsService();
            String range = "pmb!A1"; // Appends below existing data
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
                    entry.optString("BDRSetSales", ""),
                    entry.optString("RepSetSales", "")
                ));
            }
            ValueRange body = new ValueRange().setValues(rows);
            AppendValuesResponse response = sheetsService.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
            System.out.println("Appended " + response.getUpdates().getUpdatedRows() + " rows to 'pmb' sheet.");
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

        List<String> pmbReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15296315/132148069",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820743/113080121",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821545/113085819",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931971/109173462",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821874/113088373",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830013/113148524",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830092/113149066",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830518/113151980",
            "https://app.hubspot.com/reports-dashboard/587184/view/16817770/144354630",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008226/129971621",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916924/120524801",
            "https://app.hubspot.com/reports-dashboard/587184/view/12822410/113092772"
        );

        List<String> callsReportUrlsThisMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15295898/134269782"
            // add later
        );

        List<String> callsReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15296315/132148075",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820743/128527614",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821545/128528016",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931971/128526818",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821874/113088373",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830013/128530561",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830092/128531073",
            "https://app.hubspot.com/reports-dashboard/587184/view/12830518/128531381",
            "https://app.hubspot.com/reports-dashboard/587184/view/16817770/144354636",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008226/129971627",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916924/128532303",
            "https://app.hubspot.com/reports-dashboard/587184/view/12822410/128530012"
        );

        JSONArray closeRates = new JSONArray();

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

            // Calls
            for (String url : callsReportUrlsLastMonth) {
                boolean hasTable = signInTest.scrollIntoViewTable(driver, url);
                sleep(2000);
                if (hasTable) {
                    signInTest.clickDropDown();
                    sleep(1000);
                    signInTest.getData(closeRates,"Calls");
                }
            }

            // PMB
            for (String url : pmbReportUrlsLastMonth) {
                boolean hasTable = signInTest.scrollIntoViewTable(driver, url);
                sleep(2000);
                if (hasTable) {
                    signInTest.clickDropDown();
                    sleep(1000);
                    signInTest.getData(closeRates,"Pmb");
                }
            }





            signInTest.updateCloseRates(closeRates, spreadsheetId);

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

}
