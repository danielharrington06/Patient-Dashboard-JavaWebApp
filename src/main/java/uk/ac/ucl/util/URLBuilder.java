package uk.ac.ucl.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URLBuilder {
    
    public static Map<String, String> buildResetUrls(String search, String gender, String alive, String marital, List<String> race, List<String> ethnicity) {
        // by building all 5 in one function, they can easily and cleanly be accessed by the reset button that is pressed on the page
        Map<String, String> urls = new HashMap<>();
        urls.put("gender", buildUrl(search, null, alive, marital, race, ethnicity));
        urls.put("alive",  buildUrl(search, gender, null, marital, race, ethnicity));
        urls.put("marital",buildUrl(search, gender, alive, null, race, ethnicity));
        urls.put("race",   buildUrl(search, gender, alive, marital, null, ethnicity));
        urls.put("ethnicity", buildUrl(search, gender, alive, marital, race, null));
        return urls;
    }

    private static String buildUrl(String search, String gender, String alive, String marital, List<String> race, List<String> ethnicity) {
        StringBuilder sb = new StringBuilder("/runsearch?");
        if (search != null && !search.isEmpty()) sb.append("searchstring=").append(search).append("&");
        if (gender != null && !gender.isEmpty()) sb.append("gender=").append(gender).append("&");
        if (alive != null && !alive.isEmpty()) sb.append("alive=").append(alive).append("&");
        if (marital != null && !marital.isEmpty()) sb.append("marital=").append(marital).append("&");
        if (race != null) race.forEach(r -> sb.append("race=").append(r).append("&"));
        if (ethnicity != null) ethnicity.forEach(e -> sb.append("ethnicity=").append(e).append("&"));
        return sb.toString();
    }

    public static String buildPaginationBaseUrl(String search, String gender, String alive, String marital, List<String> race, List<String> ethnicity, String sortKey, String sortDir) {
        StringBuilder sb = new StringBuilder(
            (search != null && !search.isEmpty()) ? "/runsearch?" : "/patientList?"
        );
        if (search != null && !search.isEmpty()) sb.append("searchstring=").append(search).append("&amp;");
        if (gender != null && !gender.isEmpty()) sb.append("gender=").append(gender).append("&amp;");
        if (alive != null && !alive.isEmpty()) sb.append("alive=").append(alive).append("&amp;");
        if (marital != null && !marital.isEmpty()) sb.append("marital=").append(marital).append("&amp;");
        if (race != null) race.forEach(r -> sb.append("race=").append(r).append("&amp;"));
        if (ethnicity != null) ethnicity.forEach(e -> sb.append("ethnicity=").append(e).append("&amp;"));
        if (sortKey != null && !sortKey.isEmpty()) sb.append("sort=").append(sortKey).append("&amp;dir=").append(sortDir != null ? sortDir : "asc").append("&amp;");
        sb.append("page=");
        return sb.toString();
    }
}
