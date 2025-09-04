package com.spothopper;

import java.time.Duration;

import org.jboss.aerogear.security.otp.Totp;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;

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

    // Main method *****************
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        String email = getSecret("GOOGLE_EMAIL_VANJA");
        String googlePassword = getSecret("GOOGLE_PASSWORD_VANJA");
        String googleTotPin = getSecret("GOOGLE_TOTPIN_VANJA");
        String hubspotTotPin = getSecret("HUBSPOT_TOTPIN_VANJA");
        WebDriverManager.chromedriver().setup();
        //WebDriver driver = new ChromeDriver();
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
        sleep(2000);
        driver.get("https://app.hubspot.com");
        signInTest.clickHubspotAccountElement();
        driver.get("https://app.hubspot.com/reports-dashboard/587184/view/12820555");
        

        driver.quit(); 
    }
}
