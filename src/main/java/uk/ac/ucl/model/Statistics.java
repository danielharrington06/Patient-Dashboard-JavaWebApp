package uk.ac.ucl.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ucl.util.Formatter;

class Statistics {

    private final DataFrame dataFrame;

    public Statistics(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return LocalDate.parse(raw.trim(), Formatter.DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private int ageAt(LocalDate birth, LocalDate end) {
        return Period.between(birth, end).getYears();
    }

    public int getOldestAliveAge() {
        LocalDate today = LocalDate.now();
        int max = -1;
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String death = dataFrame.getValue("DEATHDATE", row);
            if (death != null && !death.isBlank()) continue; // deceased
            LocalDate birth = parseDate(dataFrame.getValue("BIRTHDATE", row));
            if (birth == null) continue;
            int age = ageAt(birth, today);
            if (age > max) max = age;
        }
        return max;
    }

    public int getYoungestAliveAge() {
        LocalDate today = LocalDate.now();
        int min = Integer.MAX_VALUE;
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String death = dataFrame.getValue("DEATHDATE", row);
            if (death != null && !death.isBlank()) continue;
            LocalDate birth = parseDate(dataFrame.getValue("BIRTHDATE", row));
            if (birth == null) continue;
            int age = ageAt(birth, today);
            if (age < min) min = age;
        }
        return min == Integer.MAX_VALUE ? -1 : min;
    }

    public double getAverageAliveAge() {
        LocalDate today = LocalDate.now();
        long total = 0;
        int count = 0;
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String death = dataFrame.getValue("DEATHDATE", row);
            if (death != null && !death.isBlank()) continue;
            LocalDate birth = parseDate(dataFrame.getValue("BIRTHDATE", row));
            if (birth == null) continue;
            total += ageAt(birth, today);
            count++;
        }
        return count == 0 ? 0 : (double) total / count;
    }

    public double getAverageAgeAtDeath() {
        long total = 0;
        int count = 0;
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            LocalDate death = parseDate(dataFrame.getValue("DEATHDATE", row));
            if (death == null) continue;
            LocalDate birth = parseDate(dataFrame.getValue("BIRTHDATE", row));
            if (birth == null) continue;
            total += ageAt(birth, death);
            count++;
        }
        return count == 0 ? 0 : (double) total / count;
    }

    public String[] getMostCommonCity() {
        // Returns [cityName, count]
        Map<String, Integer> counts = new HashMap<>();
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String city = dataFrame.getValue("CITY", row);
            if (city != null && !city.isBlank()) {
                counts.merge(city, 1, Integer::sum);
            }
        }
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new String[]{e.getKey(), String.valueOf(e.getValue())})
                .orElse(new String[]{"N/A", "0"});
    }

    public String getMostCommonEthnicity() {
        Map<String, Integer> counts = new HashMap<>();
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String val = dataFrame.getValue("ETHNICITY", row);
            if (val != null && !val.isBlank()) counts.merge(val, 1, Integer::sum);
        }
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> Formatter.formatValue("ETHNICITY", e.getKey()))
                .orElse("N/A");
    }

    public String getMostCommonRace() {
        Map<String, Integer> counts = new HashMap<>();
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String val = dataFrame.getValue("RACE", row);
            if (val != null && !val.isBlank()) counts.merge(val, 1, Integer::sum);
        }
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> Formatter.formatValue("RACE", e.getKey()))
                .orElse("N/A");
    }

    public int getLivingCount() {
        int count = 0;
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String death = dataFrame.getValue("DEATHDATE", row);
            if (death == null || death.isBlank()) count++;
        }
        return count;
    }

    public Map<String, Integer> getGenderCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("Male", 0);
        counts.put("Female", 0);
        counts.put("Unknown", 0);
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String g = dataFrame.getValue("GENDER", row);
            if ("M".equalsIgnoreCase(g)) counts.merge("Male", 1, Integer::sum);
            else if ("F".equalsIgnoreCase(g)) counts.merge("Female", 1, Integer::sum);
            else counts.merge("Unknown", 1, Integer::sum);
        }
        counts.values().removeIf(v -> v == 0);
        return counts;
    }

    public Map<String, Integer> getMaritalCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("Married", 0);
        counts.put("Single", 0);
        counts.put("Unknown", 0);
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String m = dataFrame.getValue("MARITAL", row);
            if ("M".equalsIgnoreCase(m)) counts.merge("Married", 1, Integer::sum);
            else if ("S".equalsIgnoreCase(m)) counts.merge("Single", 1, Integer::sum);
            else counts.merge("Unknown", 1, Integer::sum);
        }
        return counts;
    }

    public Map<String, Integer> getLivingDeceasedCounts() {
        int living = getLivingCount();
        int deceased = dataFrame.getRowCount() - living;
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("Living", living);
        counts.put("Deceased", deceased);
        return counts;
    }

    public Map<String, Integer> getEthnicityCounts(int topN) {
        // Returns top N ethnicities + "Other" bucket
        Map<String, Integer> raw = new HashMap<>();
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String val = dataFrame.getValue("ETHNICITY", row);
            if (val != null && !val.isBlank()) raw.merge(val, 1, Integer::sum);
        }
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(raw.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        Map<String, Integer> result = new LinkedHashMap<>();
        int otherCount = 0;
        for (int i = 0; i < sorted.size(); i++) {
            if (i < topN) {
                result.put(Formatter.formatValue("ETHNICITY", sorted.get(i).getKey()), sorted.get(i).getValue());
            } else {
                otherCount += sorted.get(i).getValue();
            }
        }
        if (otherCount > 0) result.put("Other", otherCount);
        return result;
    }

    public Map<String, Integer> getAliveAgeHistogram() {
        LocalDate today = LocalDate.now();

        String[] bucketLabels = {"0-9", "10-19", "20-29", "30-39", "40-49", "50-59", "60-69", "70-79", "80-89", "90+"};
        Map<String, Integer> histogram = new LinkedHashMap<>();
        for (String label : bucketLabels) histogram.put(label, 0);

        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String death = dataFrame.getValue("DEATHDATE", row);
            if (death != null && !death.isBlank()) continue;
            LocalDate birth = parseDate(dataFrame.getValue("BIRTHDATE", row));
            if (birth == null) continue;
            int age = ageAt(birth, today);
            int bucketIndex = Math.min(age / 10, 9); // cap at 90+
            histogram.merge(bucketLabels[bucketIndex], 1, Integer::sum);
        }
        return histogram;
    }
}