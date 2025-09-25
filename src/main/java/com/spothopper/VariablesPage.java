package com.spothopper;

import org.json.JSONArray;
import java.util.*;
import com.spothopper.*;

public class VariablesPage extends AbstractClass {
    public static final String EMAIL = getSecret("GOOGLE_EMAIL_VANJA");
    public static final String GOOGLE_PASSWORD = getSecret("GOOGLE_PASSWORD_VANJA");
    public static final String GOOGLE_TOTPIN = getSecret("GOOGLE_TOTPIN_VANJA");
    public static final String HUBSPOT_TOTPIN = getSecret("HUBSPOT_TOTPIN_VANJA");
    public static final String SPREADSHEET_ID = "1Pxgp3zUZ6khudOVD--5aI3oFd8NQzmkqigCj1ZlScfY";
    public static final String RANGE_LAST_MONTH = "last_month!A2";
    public static final String RANGE_THIS_MONTH = "this_month!A2";

    public static final JSONArray CLOSE_RATES_THIS_MONTH = new JSONArray();

    public static final Map<String, List<String>> REPORT_MAP_THIS_MONTH = new HashMap<>();

     // ******************** This Month ***************
        // PMB By Sales Reps - This Month
     public static final  List<String> pmbReportUrlsThisMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15295898/132144905",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820555/113078679",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821140/113082895",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931736/109173589",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821612/113086364",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829866/113147378",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829954/113148099",
            "https://app.hubspot.com/reports-dashboard/587184/view/16817768/144354606",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008070/129970525",
            "https://app.hubspot.com/reports-dashboard/587184/view/12822090/113090236",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916712/120523319",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484127/149758998",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484838/149764809"

        );

        // Calls - This Month
     public static final   List<String> callsReportUrlsThisMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15295898/134269782",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820555/134270401",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821140/134270978",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931736/128525790",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821612/134271184",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829866/134271756",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829954/134271975",
            "https://app.hubspot.com/reports-dashboard/587184/view/16817768/144354613",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008070/134272816",
            "https://app.hubspot.com/reports-dashboard/587184/view/12822090/134271380",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916712/134272428",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484127/149759004",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484838/149764816"

        );

        // Meetings Booked By BDRs - This Month
     public static final   List<String> BdrMbReportUrlsThisMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15295898/132144907",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820555/113078681",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821140/113082897",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931736/109313508",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821612/113086366",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829866/113147380",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829954/113148101",
            "https://app.hubspot.com/reports-dashboard/587184/view/16817768/144354608",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008070/129970527",
            "https://app.hubspot.com/reports-dashboard/587184/view/12822090/113090238",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916712/120523321",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484127/149758999",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484838/149764811"

        );

        // Sales Rep Held w. Owner Meetings - This Month
        public static final List<String> RepSetNomhReportUrlsThisMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15295898/132144906",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820555/113078680",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821140/113082896",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931736/109173898",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821612/113086365",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829866/113147379",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829954/113148100",
            "https://app.hubspot.com/reports-dashboard/587184/view/16817768/144354607",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008070/129970526",
            "https://app.hubspot.com/reports-dashboard/587184/view/12822090/113090237",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916712/120523320",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484127/149758997",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484838/149764810"

        );

        // BDR Held w. Owner Meetings - This Month
        public static final List<String> BdrNomhReportUrlsThisMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15295898/132144908",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820555/113078682",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821140/113082898",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931736/109313842",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821612/113086367",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829866/113147381",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829954/113148102",
            "https://app.hubspot.com/reports-dashboard/587184/view/16817768/144354609",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008070/129970528",
            "https://app.hubspot.com/reports-dashboard/587184/view/12822090/113090239",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916712/120523322",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484127/149759000",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484838/149764812"

        );

        // Closed Won Deals Booked by BDRs - This Month
        public static final List<String> BdrSetSalesReportUrlsThisMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15295898/147245990",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820555/147255166",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821140/147255486",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931736/147144379",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821612/147271750",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829866/147274826",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829954/147275428",
            "https://app.hubspot.com/reports-dashboard/587184/view/16817768/147278682",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008070/147277594",
            "https://app.hubspot.com/reports-dashboard/587184/view/12822090/147274303",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916712/147277118",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484127/149758996",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484838/149764808"

        );

        // Closed Won Deals Booked by Sales Reps - This Month
        public static final List<String> RepSetSalesReportUrlsThisMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/15295898/147245942",
            "https://app.hubspot.com/reports-dashboard/587184/view/12820555/147255142",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821140/147255471",
            "https://app.hubspot.com/reports-dashboard/587184/view/11931736/147144347",
            "https://app.hubspot.com/reports-dashboard/587184/view/12821612/147271748",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829866/147274820",
            "https://app.hubspot.com/reports-dashboard/587184/view/12829954/147275408",
            "https://app.hubspot.com/reports-dashboard/587184/view/16817768/147278656",
            "https://app.hubspot.com/reports-dashboard/587184/view/15008070/147277575",
            "https://app.hubspot.com/reports-dashboard/587184/view/12822090/147274293",
            "https://app.hubspot.com/reports-dashboard/587184/view/13916712/147277106",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484127/149758995",
            "https://app.hubspot.com/reports-dashboard/587184/view/17484838/149764807"

        );

        // ************************ Last Month **************

        // PMB By Sales Reps - Last Month
        public static final List<String> pmbReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/17541126/150192135"
        );

        // Calls - Last Month
        public static final List<String> callsReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/17541126/150193451"
        );

        // Meetings Booked By BDRs - Last Month
        public static final List<String> BdrMbReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/17541126/150193811"
        );

        // Sales Rep Held w. Owner Meetings - Last Month
        public static final List<String> RepSetNomhReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/17541126/150194434"

        );

        // BDR Held w. Owner Meetings - Last Month
        public static final List<String> BdrNomhReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/17541126/150194838"

        );

        // Closed Won Deals Booked by BDRs - Last Month
        public static final List<String> BdrSetSalesReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/17541126/150196031"


        );

        // Closed Won Deals Booked by Sales Reps - Last Month
        public static final List<String> RepSetSalesReportUrlsLastMonth = List.of(
            "https://app.hubspot.com/reports-dashboard/587184/view/17541126/150196395"

        );




}
