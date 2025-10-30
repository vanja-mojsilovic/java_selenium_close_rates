package com.spothopper;

import org.json.JSONArray;
import java.util.*;
import com.spothopper.*;

public class VariablesPage extends AbstractClass {
    public static final String EMAIL = getSecret("GOOGLE_EMAIL_VANJA");
    public static final String SPREADSHEET_ID = "1Pxgp3zUZ6khudOVD--5aI3oFd8NQzmkqigCj1ZlScfY";
    public static final String RANGE_LAST_MONTH = "last_month!A2";
    public static final JSONArray CLOSE_RATES_THIS_MONTH = new JSONArray();
    public static final Map<String, List<String>> REPORT_MAP_THIS_MONTH = new HashMap<>();
    public static String spreadsheetId = "1Pxgp3zUZ6khudOVD--5aI3oFd8NQzmkqigCj1ZlScfY";
    public static String rangeForCountsLastMonth = "last_month!A2";
    public static String rangeForCountsThisMonth = "this_month!A2";
    public static List<String> jsonFieldsLastMonth = Arrays.asList(
            "Pmb", "Calls", "BdrMb", "RepSetNomh","RepSetNomhWithGm", "BdrNomh","BdrNomhWithGm", "BdrSetSales", "RepSetSales","TotalDeals","TotalLocations","TotalMrr","GmMeetingsHeld");
    public static List<String> jsonFieldsThisMonth = Arrays.asList(
           "Calls", "Pmb", "TotalMrr", "BdrMb", "BdrNomh","NumberOfMissingOutcomes","RepSetNomh");



    public static List<String> listOfCsvFilesLastMonth = Arrays.asList(
            // ordered as in sheets document
            "pmb-by-sales-reps-last-month.csv",
            "sales-reps-calls-last-month.csv",
            "meetings-booked-by-bdrs-last.csv",
            "sales-rep-held-w-owner-meeting.csv",
            "rep-set-nomh-w-gms-last.csv",
            "bdr-held-w-owner-meetings-la.csv",
            "bdr-set-nomh-w-gms-last.csv",
            "closed-won-deals-booked-by-bdrs.csv",
            "closed-won-deals-booked-by-sale.csv",
            "sales-rep-total-deals-last-mo.csv",
            "sales-rep-total-locations-las.csv",
            "total-mrr-last-month-s-sales.csv",
            "gm-meetings-held-last-month.csv"
    );

    public static List<String> listOfCsvFilesThisMonth = Arrays.asList(
            // ordered as in sheets document
            "sales-reps-calls-this-month.csv",
            "pmb-by-sales-reps-this-month.csv",
            "total-mrr-this-month-s-sales.csv",
            "meetings-booked-by-bdrs-this.csv",
            "bdr-held-w-owner-meetings-th.csv",
            "meetings-with-no-outcome-this.csv",
            "sales-rep-held-w-owner-meeting.csv"
    );

}
