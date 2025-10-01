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
import org.jboss.aerogear.security.otp.Totp;



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
                    entry.optString("RepSetSales", "0"),
                    entry.optString("TotalLocations", "0"),
                    entry.optString("TotalMrr", "0"),
                    entry.optString("GmMeetingsHeld", "0")
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
        GoogleCredentials credentials;

        // Try environment-based injection first (CI mode)
        String rawJson = getSecret("GOOGLE_CREDENTIALS_JSON");

        if (rawJson != null && !rawJson.isEmpty()) {
            InputStream stream = new ByteArrayInputStream(rawJson.getBytes(StandardCharsets.UTF_8));
            credentials = GoogleCredentials.fromStream(stream);
        } else {
            // Fallback to file-based credentials (local mode)
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




    // important
    public static JSONArray buildSalesRepActivityJSON(List<String> listOfCsvFiles, List<String> jsonFields) {
        Map<String, JSONObject> salesRepMap = new HashMap<>();

        for (int i = 0; i < listOfCsvFiles.size(); i++) {
            String fileName = listOfCsvFiles.get(i);
            String fieldName = jsonFields.get(i);

            try (CSVReader reader = new CSVReader(new FileReader("resources/" + fileName))) {
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

                    if (fileName.equals("total-mrr-last-month-s-sales.csv")) {
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
                    else if (fileName.equals("pmb-by-sales-reps-last-month.csv") && columns.length >= 6) {
                        String companyRecordId = columns[5].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName);
                    }
                    else if (fileName.equals("sales-reps-calls-last-month.csv") && columns.length >= 2) {
                        String companyRecordId = columns[1].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName);
                    }
                    else if (fileName.equals("meetings-booked-by-bdrs-last.csv") && columns.length >= 7) {
                        String companyRecordId = columns[6].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName);
                    }
                    /*
                    else if (fileName.equals("sales-rep-held-w-owner-meeting.csv")) {
                        if (columns.length < 6) {
                            System.err.println("Skipping malformed row (too few columns): " + Arrays.toString(columns));
                            continue;
                        }
                        //System.out.println("Processing row for sales rep: " + salesRep);
                        //System.out.println("Company ID: " + columns[4]);
                        String companyRecordId = columns[4].trim();
                        String uniqueKey = salesRep + "," + companyRecordId;
                        isUniqueAndCount(entry, uniqueKey, fieldName);
                        System.out.println("Current count for " + fieldName + ": " + entry.getString(fieldName));
                    }

                     */
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

        for (JSONObject obj : salesRepMap.values()) {
            obj.remove("uniqueRows");
        }

        return new JSONArray(salesRepMap.values());
    }


    private static boolean isUniqueAndCount(JSONObject entry, String uniqueKey, String fieldName) {
        JSONArray arr = entry.optJSONArray("uniqueRows");
        Set<String> uniqueRows = new HashSet<>();
        if (arr != null) {
            for (int j = 0; j < arr.length(); j++) {
                uniqueRows.add(arr.getString(j));
            }
        }
        if (!uniqueRows.contains(uniqueKey)) {
            uniqueRows.add(uniqueKey);
            int currentCount = Integer.parseInt(entry.getString(fieldName));
            entry.put(fieldName, String.valueOf(currentCount + 1));
            entry.put("uniqueRows", new JSONArray(uniqueRows));
            return true;
        }
        return false;
    }



} // class
