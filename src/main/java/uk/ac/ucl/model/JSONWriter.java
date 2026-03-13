package uk.ac.ucl.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONWriter {
    public JSONWriter() {

    }

    public void write(DataFrame dataFrame, String jsonFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> rows = new ArrayList<>();

        List<String> columnNames = dataFrame.getColumnNames();

        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            Map<String, String> row = new LinkedHashMap<>();
            for (String col : columnNames) {
                row.put(col, dataFrame.getValue(col, i));
            }
            rows.add(row);
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(jsonFilePath), rows);
    }

    // Helper method in StatsServlet
    public static String[] mapChartToJson(Map<String, Integer> map) {
        StringBuilder labels = new StringBuilder("[");
        StringBuilder values = new StringBuilder("[");
        boolean first = true;
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            if (!first) { labels.append(","); values.append(","); }
            labels.append("\"").append(e.getKey().replace("\"", "\\\"")).append("\"");
            values.append(e.getValue());
            first = false;
        }
        labels.append("]"); values.append("]");
        return new String[]{labels.toString(), values.toString()};
    }

    public static String buildChartDataJson(String[] gender, String[] marital, String[] ethnicity, String[] race) {
        return "{"
            + "\"genderLabels\":"    + gender[0]    + ","
            + "\"genderValues\":"    + gender[1]    + ","
            + "\"maritalLabels\":"   + marital[0]   + ","
            + "\"maritalValues\":"   + marital[1]   + ","
            + "\"ethnicityLabels\":" + ethnicity[0] + ","
            + "\"ethnicityValues\":" + ethnicity[1] + ","
            + "\"raceLabels\":"      + race[0]      + ","
            + "\"raceValues\":"      + race[1]
            + "}";
    }

    public static String buildChartDataJson(String[] gender, String[] marital,
                                            String[] ethnicity, String[] race,
                                            String[] living, String[] city,
                                            String[] ageHistogram) {
        return "{"
            + "\"genderLabels\":"       + gender[0]       + ","
            + "\"genderValues\":"       + gender[1]       + ","
            + "\"maritalLabels\":"      + marital[0]      + ","
            + "\"maritalValues\":"      + marital[1]      + ","
            + "\"ethnicityLabels\":"    + ethnicity[0]    + ","
            + "\"ethnicityValues\":"    + ethnicity[1]    + ","
            + "\"raceLabels\":"         + race[0]         + ","
            + "\"raceValues\":"         + race[1]         + ","
            + "\"livingLabels\":"       + living[0]       + ","
            + "\"livingValues\":"       + living[1]       + ","
            + "\"cityLabels\":"         + city[0]         + ","
            + "\"cityValues\":"         + city[1]         + ","
            + "\"ageHistogramLabels\":" + ageHistogram[0] + ","
            + "\"ageHistogramValues\":" + ageHistogram[1]
            + "}";
    }
}
