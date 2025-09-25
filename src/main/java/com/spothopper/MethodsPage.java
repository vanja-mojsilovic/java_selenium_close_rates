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

    public MethodsPage() {
        System.out.println("Constructor activated!");
    }

    // Methods

    // important
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


    // important
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

    // important
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
                    JSONObject entry = salesRepMap.getOrDefault(salesRep, new JSONObject());
                    entry.put("SalesRep", salesRep);
                    for (String field : jsonFields) {
                        if (!entry.has(field)) {
                            entry.put(field, "0");
                        }
                    }
                    int currentCount = Integer.parseInt(entry.getString(fieldName));
                    entry.put(fieldName, String.valueOf(currentCount + 1));

                    salesRepMap.put(salesRep, entry);
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + fileName);
                e.printStackTrace();
            }
        }
        return new JSONArray(salesRepMap.values());
    }


} // class
