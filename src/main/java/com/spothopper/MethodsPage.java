package com.spothopper;

// Java I/O and utility
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.security.GeneralSecurityException;

// JSON
import org.json.JSONArray;
import org.json.JSONObject;

// Selenium WebDriver
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

// OTP for 2FA


// OTP for 2FA
import org.jboss.aerogear.security.otp.Totp;


public class MethodsPage extends AbstractClass {
    // Variables
    private WebDriver driver;

    public MethodsPage() {
        System.out.println("Constructor activated!");
    }

    // Methods


    public void updateCloseRatesThisMonth(JSONArray closeRates, String spreadsheetId,String rangeForCounts) {
        if (closeRates == null || closeRates.isEmpty()) {
            System.out.println("No close rate data to update! Skipping sheet update.");
            return;
        }
        try {
            Sheets sheetsService = getSheetsService();
            List<List<Object>> rows = new ArrayList<>();
            for (int i = 0; i < closeRates.length(); i++) {
                JSONObject entry = closeRates.getJSONObject(i);
                rows.add(
                        Arrays.asList(
                            entry.optString("SalesRep", ""),
                            entry.optInt("Calls", 0),
                            entry.optInt("Pmb", 0),
                            entry.optDouble("TotalMrr", 0.0),
                            entry.optInt("BdrMb", 0),
                            entry.optInt("BdrNomh", 0),
                            entry.optInt("NumberOfMissingOutcomes", 0),
                            entry.optInt("RepSetNomh", 0)
                        )
                );
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


    public void updateCloseRatesLastMonth(JSONArray closeRates, String spreadsheetId,String rangeForCounts) {
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
                    entry.optString("SalesRep", ""),
                    entry.optInt("Pmb", 0),
                    entry.optInt("Calls", 0),
                    entry.optInt("BdrMb", 0),
                    entry.optInt("RepSetNomh", 0),
                    entry.optInt("RepSetNomhWithGm", 0),
                    entry.optInt("BdrNomh", 0),
                    entry.optInt("BdrNomhWithGm", 0),
                    entry.optInt("BdrSetSales", 0),
                    entry.optInt("RepSetSales", 0),
                    entry.optInt("TotalDeals", 0),
                    entry.optInt("TotalLocations", 0),
                    entry.optDouble ("TotalMrr", 0.0),
                    entry.optInt("GmMeetingsHeld", 0)
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
        GoogleCredentials credentials;
        String rawJson = getSecret("GOOGLE_CREDENTIALS_JSON");
        if (rawJson != null && !rawJson.isEmpty()) {
            InputStream stream = new ByteArrayInputStream(rawJson.getBytes(StandardCharsets.UTF_8));
            credentials = GoogleCredentials.fromStream(stream);
        } else {
            String credentialsPath = getSecret("GOOGLE_CREDENTIALS_PATH");
            if (credentialsPath == null || credentialsPath.isEmpty()) {
                credentialsPath = "credentials.json";
            }
            File credentialsFile = new File(credentialsPath);
            if (!credentialsFile.exists()) {
                throw new IOException("Missing credentials file at: " + credentialsPath);
            }
            credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsFile));
        }

        credentials = credentials.createScoped(List.of(SheetsScopes.SPREADSHEETS));
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Close Rates Sync")
                .build();
    }

    public static JSONArray buildSalesRepActivityJsonThisMonth(List<String> listOfCsvFiles, List<String> jsonFields) {
        Map<String, JSONObject> salesRepMap = new HashMap<>();
        for (int i = 0; i < listOfCsvFiles.size(); i++) {
            String fileName = listOfCsvFiles.get(i);
            String fieldName = jsonFields.get(i);
            Set<String> seenKeys = new HashSet<>();
            try (CSVReader reader = new CSVReader(new FileReader("resources/this_month/" + fileName))) {
                String[] columns;
                boolean isHeader = true;
                while ((columns = reader.readNext()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }
                    String salesRep = columns[0].trim();
                    JSONObject entry = salesRepMap.get(salesRep);
                    if (entry == null) {
                        entry = new JSONObject();
                        entry.put("SalesRep", salesRep);
                    }
                    for (String field : jsonFields) {
                        if (!entry.has(field)) {
                            entry.put(field, "0");
                        }
                    }
                    // Fields from reports This month
                    if (fileName.equals("sales-reps-calls-this-month.csv") ) {
                        String companyRecordId = columns[1].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("pmb-by-sales-reps-this-month.csv")) {
                        String companyRecordId = columns[5].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("total-mrr-this-month-s-sales.csv")) {
                        String mrrValueStr = columns[1].trim();
                        double mrrValue = 0.0;
                        try {
                            mrrValue = Double.parseDouble(mrrValueStr);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid MRR value for sales rep " + salesRep + ": " + mrrValueStr);
                        }

                        double currentMRR = 0.0;
                        try {
                            currentMRR = Double.parseDouble(entry.getString(fieldName));
                        } catch (NumberFormatException e) {
                            currentMRR = 0.0;
                        }
                        entry.put(fieldName, String.valueOf(currentMRR + mrrValue));
                    }

                    else if (fileName.equals("meetings-booked-by-bdrs-this.csv")) {
                        String companyRecordId = columns[6].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("bdr-held-w-owner-meetings-th.csv") ) {
                        String companyRecordId = columns[6].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("meetings-with-no-outcome-this.csv") ) {
                        String companyRecordId = columns[3].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("sales-rep-held-w-owner-meeting.csv") ) {
                        String companyRecordId = columns[4].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else {
                        int currentCount = Integer.parseInt(entry.getString(fieldName));
                        entry.put(fieldName, String.valueOf(currentCount + 1));
                    }

                    salesRepMap.put(salesRep, entry);
                }
            } catch (IOException | CsvValidationException e) {
                System.err.println("Error reading file: " + fileName);
                e.printStackTrace();
            }
        }
        return new JSONArray(salesRepMap.values());
    }

    public static JSONArray buildSalesRepActivityJsonLastMonth(List<String> listOfCsvFiles, List<String> jsonFields) {
        Map<String, JSONObject> salesRepMap = new HashMap<>();

        for (int i = 0; i < listOfCsvFiles.size(); i++) {
            String fileName = listOfCsvFiles.get(i);
            String fieldName = jsonFields.get(i);
            Set<String> seenKeys = new HashSet<>();

            try (CSVReader reader = new CSVReader(new FileReader("resources/last_month/" + fileName))) {
                String[] columns;
                boolean isHeader = true;
                while ((columns = reader.readNext()) != null) {
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }
                    String salesRep = columns[0].trim();
                    JSONObject entry = salesRepMap.get(salesRep);
                    if (entry == null) {
                        entry = new JSONObject();
                        entry.put("SalesRep", salesRep);
                    }
                    for (String field : jsonFields) {
                        if (!entry.has(field)) {
                            entry.put(field, "0");
                        }
                    }
                    // Fields from reports Last month
                    if (fileName.equals("pmb-by-sales-reps-last-month.csv") && columns.length >= 6) {
                        String companyRecordId = columns[5].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("sales-reps-calls-last-month.csv") && columns.length >= 2) {
                        String companyRecordId = columns[1].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("meetings-booked-by-bdrs-last.csv") && columns.length >= 7) {
                        String companyRecordId = columns[6].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("sales-rep-held-w-owner-meeting.csv") ) {
                        String companyRecordId = columns[4].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("rep-set-nomh-w-gms-last.csv") ) {
                        String companyRecordId = columns[4].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("bdr-held-w-owner-meetings-la.csv") ) {
                        String companyRecordId = columns[6].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("bdr-set-nomh-w-gms-last.csv") ) {
                        String companyRecordId = columns[6].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("closed-won-deals-booked-by-bdrs.csv") ) {
                        String companyRecordId = columns[6].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }
                    else if (fileName.equals("closed-won-deals-booked-by-sale.csv") ) {
                        String companyRecordId = columns[6].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("sales-rep-total-deals-last-mo.csv") ) {
                        String companyRecordId = columns[2].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }
                    else if (fileName.equals("sales-rep-total-locations-las.csv") ) {
                        String companyRecordId = columns[1].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else if (fileName.equals("total-mrr-last-month-s-sales.csv")) {
                        String mrrValueStr = columns[1].trim();
                        double mrrValue = 0.0;
                        try {
                            mrrValue = Double.parseDouble(mrrValueStr);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid MRR value for sales rep " + salesRep + ": " + mrrValueStr);
                        }

                        double currentMRR = 0.0;
                        try {
                            currentMRR = Double.parseDouble(entry.getString(fieldName));
                        } catch (NumberFormatException e) {
                            currentMRR = 0.0;
                        }
                        entry.put(fieldName, String.valueOf(currentMRR + mrrValue));
                    }

                    else if (fileName.equals("gm-meetings-held-last-month.csv") ) {
                        String companyRecordId = columns[3].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName, seenKeys);
                    }

                    else {
                        int currentCount = Integer.parseInt(entry.getString(fieldName));
                        entry.put(fieldName, String.valueOf(currentCount + 1));
                    }

                    salesRepMap.put(salesRep, entry);
                }
            } catch (IOException | CsvValidationException e) {
                System.err.println("Error reading file: " + fileName);
                e.printStackTrace();
            }
        }
        return new JSONArray(salesRepMap.values());
    }

    private static boolean isUniqueAndCount(JSONObject entry, String uniqueKey, String fieldName, Set<String> seenKeys) {
        if (!seenKeys.contains(uniqueKey)) {
            seenKeys.add(uniqueKey);
            int currentCount = Integer.parseInt(entry.getString(fieldName));
            entry.put(fieldName, String.valueOf(currentCount + 1));
            return true;
        }
        return false;
    }




} // class
