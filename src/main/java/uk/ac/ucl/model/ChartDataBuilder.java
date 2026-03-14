package uk.ac.ucl.model;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

// Utility class for building JSON data structures consumed by Chart.js on the statistics page
public class ChartDataBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String[] mapToChartArrays(Map<String, Integer> map) {
        ArrayNode labels = mapper.createArrayNode();
        ArrayNode values = mapper.createArrayNode();
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            labels.add(e.getKey());
            values.add(e.getValue());
        }
        return new String[]{labels.toString(), values.toString()};
    }

    public static String buildChartDataJson(
        String[] gender, 
        String[] marital,
        String[] ethnicity,
        String[] living,
        String[] ageHistogram) {
            
        try {
            ObjectNode root = mapper.createObjectNode();
            root.set("genderLabels",       mapper.readTree(gender[0]));
            root.set("genderValues",       mapper.readTree(gender[1]));
            root.set("maritalLabels",      mapper.readTree(marital[0]));
            root.set("maritalValues",      mapper.readTree(marital[1]));
            root.set("ethnicityLabels",    mapper.readTree(ethnicity[0]));
            root.set("ethnicityValues",    mapper.readTree(ethnicity[1]));
            root.set("livingLabels",       mapper.readTree(living[0]));
            root.set("livingValues",       mapper.readTree(living[1]));
            root.set("ageHistogramLabels", mapper.readTree(ageHistogram[0]));
            root.set("ageHistogramValues", mapper.readTree(ageHistogram[1]));
            return mapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to build chart data JSON", e);
        }
    }
}