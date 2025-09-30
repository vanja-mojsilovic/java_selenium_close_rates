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
    public static List<String> jsonFields = Arrays.asList(
            "Pmb", "Calls", "BdrMb",  "BdrNomh","RepSetNomh", "BdrSetSales", "RepSetSales","TotalLocations","TotalMrr","GmMeetingsHeld"
    );

    public static List<String> listOfCsvFiles = Arrays.asList(
            "pmb-by-sales-reps-last-month.csv",
            "sales-reps-calls-last-month.csv",
            "meetings-booked-by-bdrs-last.csv",
            "bdr-held-w-owner-meetings-la.csv",
            "sales-rep-held-w-owner-meeting.csv",
            "closed-won-deals-booked-by-bdrs.csv",
            "closed-won-deals-booked-by-sale.csv",
            "sales-rep-total-locations-las.csv",
            "total-mrr-last-month-s-sales.csv",
            "gm-meetings-held-last-month.csv"
    );






}
