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

    private static final By OWNER_NAME_CELLS = By.xpath("//div[@data-test-id='report-loaded']//table//tr/td[1]");

    private static final By COMPANY_COUNTS_CELLS = By.xpath("//div[@data-test-id='report-loaded']//table//tr/td[2]");

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

    public void scrollIntoViewTable(WebDriver driver, String url) {
        driver.get(url);
        System.out.println("Navigating to: " + url);
        sleep(8000);
        WebElement element = waitForVisibilityOfElement(tableWithOwnersAndCounts, 10);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        try {
            List<WebElement> dropdownElements = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.xpath("//span[contains(text(),'10 rows per page')]")
                ));
            if (!dropdownElements.isEmpty()) {
                dropdownElements.get(0).click();
                WebElement visibleHundredRows = waitForVisibilityOfElement(hundredRowsPerPageDropDown, 10);
                visibleHundredRows.click();
            }
        } catch (TimeoutException e) {
            System.out.println("Dropdown for '10 rows per page' not found continuing without expanding rows.");
        } catch (Exception e) {
            System.out.println("Unexpected error while handling rows-per-page dropdown: " + e.getMessage());
        }
    }




    public void getOwnerNames(List<String> result) {
        List<WebElement> elements = driver.findElements(OWNER_NAME_CELLS);
        elements = waitForVisibilityOfElements(elements, 15);
        if(elements != null){
            int numOfElements = elements.size();
            for (int i=0;i<numOfElements;i++) {
                String name = elements.get(i).getText().trim();
                result.add(name);
            }
            System.out.println(numOfElements + " names found!");
        }

    }

    public void getCompanyCounts(List<String> result) {
        List<WebElement> elements = driver.findElements(COMPANY_COUNTS_CELLS);
        elements = waitForVisibilityOfElements(elements, 15);
        if(elements != null){
            int numOfElements = elements.size();
            for (int i=0;i<numOfElements;i++) {
                String name = elements.get(i).getText().trim();
                result.add(name);
            }
        }
    }

    public void updateCloseRates(List<String> names, List<String> counts) {
        try {
            Sheets sheetsService = getSheetsService();
            String spreadsheetId = "1Pxgp3zUZ6khudOVD--5aI3oFd8NQzmkqigCj1ZlScfY";
            String range = "pmb!A1"; // Appends below existing data
            List<List<Object>> rows = new ArrayList<>();
            for (int i = 0; i < names.size(); i++) {
                rows.add(Arrays.asList(names.get(i), counts.get(i)));
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
        String pmbBySalesReps = "https://app.hubspot.com/reports-dashboard/587184/view/";
        String pmbBySalesRepsLaurenBryan = pmbBySalesReps + "15295898/132144905";
        String pmbBySalesRepsDanielWyss = pmbBySalesReps + "12820555/113078679";
        String pmbBySalesRepsJillianKelly = pmbBySalesReps + "12821140/113082895";
        String pmbBySalesRepsKellyONeill = pmbBySalesReps + "11931736/109173589";
        String pmbBySalesRepsThomasBarrow = pmbBySalesReps + "12821612/113086364";
        String pmbBySalesRepsHarrisonMuller = pmbBySalesReps + "12829866/113147378";
        String pmbBySalesRepsUmutEngin = pmbBySalesReps + "12829954/113148099";
        String pmbBySalesRepsTylerBottenhagen = pmbBySalesReps + "12830325/113150823";
        String pmbBySalesRepsTristanMeyerInside = pmbBySalesReps + "16817768/144354606";
        String pmbBySalesRepsTristanMeyerOutside = pmbBySalesReps + "15008070/129970525";
        String pmbBySalesRepsNatalieTowne = pmbBySalesReps + "13916712/120523319";
        String pmbBySalesRepsGraceYeager = pmbBySalesReps + "12822090/113090236";





        WebDriverManager.chromedriver().setup();
        WebDriver driver = createWebDriver();
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
        Totp totpGoogle = new Totp(googleTotPin);
        String googleAuthCode = totpGoogle.now();
        signInTest.enterGoogleTotPin(googleAuthCode);
        signInTest.clickGoogleTotPinNextButton();
        sleep(2000);
        Totp totpHubspot = new Totp(hubspotTotPin);
        String hubspotAuthCode = totpHubspot.now();
        signInTest.enterHubspotTotPin(hubspotAuthCode);
        signInTest.clickHubspotTotPinLoginButton();
        sleep(2000);
        signInTest.clickRememberMeFalseButton();
        sleep(4000);
        driver.get("https://app.hubspot.com");
        signInTest.clickHubspotAccountElement();
        sleep(2000);
        List<String> ownersNames = new ArrayList<>();
        List<String> companyCounts = new ArrayList<>();

        // Lauren Bryan
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsLaurenBryan);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);
        // Daniel Wyss
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsDanielWyss);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);
        // Jillian Kelly
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsJillianKelly);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);
        // Kelly O'Neill
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsKellyONeill);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);
        // Thomas Barrow
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsThomasBarrow);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);
        // Harrison Muller
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsHarrisonMuller);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);
        // Umut Engin
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsUmutEngin);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);
        // Tyler Bottenhagen
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsTylerBottenhagen);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);
        // Tristan Meyer Inside
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsTristanMeyerInside);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);
        // Tristan Meyer Outside
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsTristanMeyerOutside);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);
        // Natalie Towne
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsNatalieTowne);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);
        // Grace Yeager
        signInTest.scrollIntoViewTable(driver,pmbBySalesRepsGraceYeager);
        sleep(1000);
        signInTest.getOwnerNames(ownersNames);
        signInTest.getCompanyCounts(companyCounts);

            // error zero names update on every step

        // Appending data in Google sheets document
        signInTest.updateCloseRates(ownersNames,companyCounts);


        driver.quit(); 
    }
}
