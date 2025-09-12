package com.spothopper;

import com.spothopper.*;
import com.spothopper.MethodsPage;

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
import org.openqa.selenium.remote.RemoteWebDriver;

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



public class SignInTest extends AbstractClass {
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







    // ****************** Main method *****************
    public static void main(String[] args) {



        //WebDriver driver = AbstractClass.createWebDriver();

        String googlePassword = getSecret("GOOGLE_PASSWORD_VANJA");
        String googleTotPin = getSecret("GOOGLE_TOTPIN_VANJA");
        String hubspotTotPin = getSecret("HUBSPOT_TOTPIN_VANJA");
        String spreadsheetId = "1Pxgp3zUZ6khudOVD--5aI3oFd8NQzmkqigCj1ZlScfY";
        String rangeForCountsLastMonth = "last_month!A2";
        String rangeForCountsThisMonth = "this_month!A2";
        JSONArray closeRates = new JSONArray();
        Map<String, List<String>> reportMapLastMonth = new HashMap<>();
       reportMapLastMonth.put("Pmb", VariablesPage.pmbReportUrlsLastMonth);
        reportMapLastMonth.put("Calls", VariablesPage.callsReportUrlsLastMonth);
        reportMapLastMonth.put("BdrMb", VariablesPage.BdrMbReportUrlsLastMonth);
        reportMapLastMonth.put("RepSetNomh", VariablesPage.RepSetNomhReportUrlsLastMonth);
        reportMapLastMonth.put("BdrNomh", VariablesPage.BdrNomhReportUrlsLastMonth);
       reportMapLastMonth.put("BdrSetSales", VariablesPage.BdrSetSalesReportUrlsLastMonth);
        reportMapLastMonth.put("RepSetSales", VariablesPage.RepSetSalesReportUrlsLastMonth);
        for (Map.Entry<String, List<String>> entry : reportMapLastMonth.entrySet()) {

            String columnName = entry.getKey();
            List<String> reportUrls = entry.getValue();
            WebDriver driver = null;
            try {
                WebDriverManager.chromedriver().setup();
                driver = AbstractClass.createWebDriver();
                MethodsPage methodsPage = new MethodsPage(driver);
                SignInTest signInTest = new SignInTest(driver);
                methodsPage.performLogin(driver, VariablesPage.EMAIL, googlePassword, googleTotPin, hubspotTotPin);
                methodsPage.processReportList(reportUrls,
                                             columnName,
                                             driver,
                                             closeRates,
                                             rangeForCountsLastMonth,
                                             spreadsheetId,
                                             VariablesPage.EMAIL,
                                             googlePassword,
                                             googleTotPin,
                                             hubspotTotPin);
            } catch (Exception e) {
                System.out.println("Fatal error while processing last month: " + columnName);
                e.printStackTrace();
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        }

        //  Process This Month Reports
        JSONArray closeRatesThisMonth = new JSONArray();
        Map<String, List<String>> reportMapThisMonth = new HashMap<>();
        reportMapThisMonth.put("Pmb", VariablesPage.pmbReportUrlsThisMonth);
        reportMapThisMonth.put("Calls", VariablesPage.callsReportUrlsThisMonth);
        reportMapThisMonth.put("BdrMb", VariablesPage.BdrMbReportUrlsThisMonth);
        reportMapThisMonth.put("RepSetNomh", VariablesPage.RepSetNomhReportUrlsThisMonth);
        reportMapThisMonth.put("BdrNomh", VariablesPage.BdrNomhReportUrlsThisMonth);
        reportMapThisMonth.put("BdrSetSales", VariablesPage.BdrSetSalesReportUrlsThisMonth);
        reportMapThisMonth.put("RepSetSales", VariablesPage.RepSetSalesReportUrlsThisMonth);
        for (Map.Entry<String, List<String>> entry : reportMapThisMonth.entrySet()) {

            String columnName = entry.getKey();
            List<String> reportUrls = entry.getValue();
            WebDriver driver = null;
            try {
                WebDriverManager.chromedriver().setup();
                driver = AbstractClass.createWebDriver();
                MethodsPage methodsPage = new MethodsPage(driver);
                SignInTest signInTest = new SignInTest(driver);
                methodsPage.performLogin(driver, VariablesPage.EMAIL, googlePassword, googleTotPin, hubspotTotPin);
                methodsPage.processReportList(reportUrls,
                                             columnName,
                                             driver,
                                             closeRatesThisMonth,
                                             rangeForCountsThisMonth,
                                             spreadsheetId,
                                             VariablesPage.EMAIL,
                                             googlePassword,
                                             googleTotPin,
                                             hubspotTotPin);
            } catch (Exception e) {
                System.out.println("Fatal error while processing this month: " + columnName);
                e.printStackTrace();
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        }
    }
}
