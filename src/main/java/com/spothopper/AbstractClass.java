package com.spothopper;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.cdimascio.dotenv.Dotenv;

import org.json.JSONArray;
import org.json.JSONObject;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.*;



public abstract class AbstractClass {


    public static boolean isCIEnvironment() {
        String ci = System.getenv("CI");
        return ci != null && ci.equalsIgnoreCase("true");
    }

    public static String getSecret(String key) {
        String value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            System.out.println("[ENV] Found secret for key: " + key);
            return value;
        }

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        value = dotenv.get(key);
        if (value != null && !value.isEmpty()) {
            System.out.println("[LOCAL] Found secret for key: " + key + " in .env file");
        } else {
            System.out.println("[WARN] Secret not found for key: " + key);
        }
        return value;
    }



    public static WebElement waitForVisibilityOfElement(WebDriver driver, WebElement element, int numOfSec) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(numOfSec));
        WebElement visibleElement = wait.until(ExpectedConditions.visibilityOf(element));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", visibleElement);
        return visibleElement;
    }


    public static List<WebElement> waitForVisibilityOfElements(WebDriver driver,List<WebElement> elements, int numOfSec) {
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


}
