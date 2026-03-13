package uk.ac.ucl.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Model
{
    DataFrame dataFrame;
    public static final String DEFAULT_FILE = Paths.get("data", "patients100.csv").toString();
    public String currentDataFile;
    private Map<String, Integer> idToRowIndex = new HashMap<>();

    public static final int DEFAULT_PAGE_SIZE = 40;
    public static final int ETHNICITY_CHART_TOP_N = 8;

    public Model() {
        try {
            reloadData(DEFAULT_FILE);
        } catch (IOException e) {
            System.err.println("Error loading CSV file: " + e.getMessage());
            this.dataFrame = new DataFrame(); // fallback to empty frame
        }
    }

    public final void reloadData(String filename) throws IOException {
        CSVLoader loader = new CSVLoader();
        this.dataFrame = loader.load(filename);
        this.currentDataFile = filename;
        buildIndex();
    }

    private void buildIndex() {
        idToRowIndex = new HashMap<>();
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            idToRowIndex.put(dataFrame.getValue("ID", row), row);
        }
    }

    public String getCurrentDataFile() {
        return currentDataFile;
    }

    public List<String> getColumnNames() {
        return dataFrame.getColumnNames();
    }

    public int getRowCount() {
        return dataFrame.getRowCount();
    }

    public String getValue(String columnName, int row) {
        return dataFrame.getValue(columnName, row);
    }

    public int getRowNumFromId(String id) {
        Integer row = idToRowIndex.get(id);
        if (row == null) throw new IllegalArgumentException("Invalid id: " + id);
        return row;
    }

    public boolean idExists(String id) {
        return idToRowIndex.containsKey(id);
    }

    public String formatColumnName(String columnName) {
        switch (columnName.toUpperCase()) {
            case "ID" -> {return "Patient ID";}
            case "BIRTHDATE" -> {return "Date of Birth";}
            case "DEATHDATE" -> {return "Date of Death";}
            case "SSN" -> {return "SSN";}
            case "DRIVERS" -> {return "Driver's Licence Number";}
            case "PASSPORT" -> {return "Passport Number";}
            case "PREFIX" -> {return "Prefix";}
            case "FIRST" -> {return "Firstname";}
            case "LAST" -> {return "Lastname";}
            case "SUFFIX" -> {return "Suffix";}
            case "MAIDEN" -> {return "Maiden Name";}
            case "MARITAL" -> {return "Marital Status";}
            case "RACE" -> {return "Race";}
            case "ETHNICITY" -> {return "Ethnicity";}
            case "GENDER" -> {return "Gender";}
            case "BIRTHPLACE" -> {return "Birthplace";}
            case "ADDRESS" -> {return "Street Address";}
            case "CITY" -> {return "City";}
            case "STATE" -> {return "State";}
            case "ZIP" -> {return "Zip Code";}
            default -> { return columnName; }
        }
    }

    public String formatValue(String columnName, String value) {
        if (value == null || value.isEmpty()) return "—";
        
        switch (columnName.toUpperCase()) {
            case "GENDER" -> {
                return switch (value.toUpperCase()) {
                    case "M" -> "Male";
                    case "F" -> "Female";
                    default -> value;
                };
            }
            case "MARITAL" -> {
                return switch (value.toUpperCase()) {
                    case "M" -> "Married";
                    case "S" -> "Single";
                    default -> value;
                };
            }
            case "ETHNICITY", "RACE" -> {
                // convert underscores to spaces and capitalise each word
                String[] words = value.replace("_", " ").split(" ");
                StringBuilder sb = new StringBuilder();
                for (String word : words) {
                    if (!word.isEmpty()) {
                        sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
                    }
                }
                return sb.toString().trim();
            }
            case "BIRTHDATE", "DEATHDATE" -> {
                // convert 1979-08-24 to 24 Aug 1979
                try {
                    String[] parts = value.split("-");
                    String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                                    "Jul","Aug","Sep","Oct","Nov","Dec"};
                    int month = Integer.parseInt(parts[1]) - 1;
                    return parts[2] + " " + months[month] + " " + parts[0];
                } catch (NumberFormatException e) {
                    return value;
                }
            }
            default -> { return value; }
        }
    }

    public LinkedHashMap<String, String> getRawPatientRecord(String id) {
        LinkedHashMap<String, String> record = new LinkedHashMap<>();
        int row = getRowNumFromId(id);
        for (String column : getColumnNames()) {
            record.put(column, getValue(column, row));
        }
        return record;
    }

    public Map<String, String> getFormattedPatientRecord(String id) {
        Map<String, String> formatted = new LinkedHashMap<>();
        int row = getRowNumFromId(id);
        for (String column : getColumnNames()) {
            formatted.put(column, formatValue(column, getValue(column, row)));
        }
        return formatted;
    }

    public List<String> getSummaryColumnNamesFormatted() {
        List<String> summaryColumns = List.of("FIRST", "LAST", "BIRTHDATE", "DEATHDATE", "GENDER", "MARITAL", "RACE", "ETHNICITY", "CITY");
        List<String> displayNames = new ArrayList<>();
        for (String col : summaryColumns) {
            displayNames.add(formatColumnName(col));
        }
        return displayNames;
    }

    public Map<String, String> getAllColumnNamesFormatted() {
        Map<String, String> columnLabels = new LinkedHashMap<>();
        columnLabels.put("ID",         "Patient ID");
        columnLabels.put("PREFIX",     "Prefix");
        columnLabels.put("FIRST",      "First Name");
        columnLabels.put("LAST",       "Last Name");
        columnLabels.put("SUFFIX",     "Suffix");
        columnLabels.put("MAIDEN",     "Maiden Name");
        columnLabels.put("GENDER",     "Gender");
        columnLabels.put("BIRTHDATE",  "Date of Birth");
        columnLabels.put("DEATHDATE",  "Date of Death");
        columnLabels.put("MARITAL",    "Marital Status");
        columnLabels.put("RACE",       "Race");
        columnLabels.put("ETHNICITY",  "Ethnicity");
        columnLabels.put("BIRTHPLACE", "Birthplace");
        columnLabels.put("SSN",        "SSN");
        columnLabels.put("DRIVERS",    "Driver's Licence");
        columnLabels.put("PASSPORT",   "Passport");
        columnLabels.put("ADDRESS",    "Address");
        columnLabels.put("CITY",       "City");
        columnLabels.put("STATE",      "State");
        columnLabels.put("ZIP",        "ZIP Code");
        return columnLabels;
    }

    public List<String> packagePatientSummaryInfo(int row) {
        List<String> info = new ArrayList<>();
        info.add(formatValue("FIRST", dataFrame.getValue("FIRST", row)));  // index 0: firstname
        info.add(formatValue("LAST", dataFrame.getValue("LAST", row)));    // index 1: lastname
        info.add(formatValue("BIRTHDATE", dataFrame.getValue("BIRTHDATE", row)));
        info.add(formatValue("DEATHDATE", dataFrame.getValue("DEATHDATE", row)));
        info.add(formatValue("GENDER", dataFrame.getValue("GENDER", row)));
        info.add(formatValue("MARITAL", dataFrame.getValue("MARITAL", row)));
        info.add(formatValue("RACE", dataFrame.getValue("RACE", row)));
        info.add(formatValue("ETHNICITY", dataFrame.getValue("ETHNICITY", row)));
        info.add(formatValue("CITY", dataFrame.getValue("CITY", row)));
        return info;
    }

    public Map<String, List<String>> getPatientSummaries() {
        // gets a hash map of id to contents: [fullName, dob, gender, city, state]
        Map<String, List<String>> patients = new LinkedHashMap<>();
        for (int row = 0; row < getRowCount(); row++) {
            String id = dataFrame.getValue("ID", row);
            List<String> info = packagePatientSummaryInfo(row);
            patients.put(id, info);
        }
        return patients;
    }

    public Map<String, List<String>> searchPatientSummaries(String searchString) {
        String[] lowerStrings = searchString.toLowerCase().split(" ");
        Map<String, List<String>> results = new HashMap<>();

        for (int row = 0; row < getRowCount(); row++) {
            boolean valid = true;
            for (String term : lowerStrings) {
                boolean found = false;
                for (String column : getColumnNames()) {
                    String value = getValue(column, row);
                    if (value != null && formatValue(column, value).toLowerCase().contains(term)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    valid = false;
                    break;  // No need to check remaining terms
                }
            }
            if (valid) {
                String id = getValue("ID", row);
                List<String> info = packagePatientSummaryInfo(row);
                results.put(id, info);
            }
        }
        return results;
    }

    public Map<String, List<String>> getPage(Map<String, List<String>> data, int page, int pageSize) {
        List<String> keys = new ArrayList<>(data.keySet());
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, keys.size());
        if (start >= keys.size()) return new HashMap<>();

        Map<String, List<String>> pageData = new LinkedHashMap<>();
        for (int i = start; i < end; i++) {
            String key = keys.get(i);
            pageData.put(key, data.get(key));
        }
        return pageData;
    }

    public Map<String, List<String>> sortPatientSummaries(Map<String, List<String>> data, String sortKey, boolean ascending) {
        // column indices in the summary list
        Map<String, Integer> sortIndices = Map.of(
            "firstname",  0,
            "lastname",   1,
            "birthdate",  2,
            "deathdate",  3,
            "city",       8
        );

        if (sortKey == null || !sortIndices.containsKey(sortKey)) return data;

        int index = sortIndices.get(sortKey);

        List<Map.Entry<String, List<String>>> entries = new ArrayList<>(data.entrySet());

        entries.sort((a, b) -> {
            String valA = a.getValue().get(index);
            String valB = b.getValue().get(index);

            // handle "—" (empty) values - always sort to bottom
            if (valA.equals("—") && valB.equals("—")) return 0;
            if (valA.equals("—")) return 1;
            if (valB.equals("—")) return -1;

            // date fields - compare as raw strings (yyyy-MM-dd sorts correctly)
            if (sortKey.equals("birthdate") || sortKey.equals("deathdate")) {
                String col = sortKey.equals("birthdate") ? "BIRTHDATE" : "DEATHDATE";
                // find the row by id and get raw value
                try {
                    int rowA = getRowNumFromId(a.getKey());
                    int rowB = getRowNumFromId(b.getKey());
                    valA = getValue(col, rowA);
                    valB = getValue(col, rowB);
                    if (valA == null || valA.isEmpty()) valA = "—";
                    if (valB == null || valB.isEmpty()) valB = "—";
                    if (valA.equals("—") && valB.equals("—")) return 0;
                    if (valA.equals("—")) return 1;
                    if (valB.equals("—")) return -1;
                } catch (IllegalArgumentException e) {
                    return 0;
                }
            }

            int result = valA.compareToIgnoreCase(valB);
            return ascending ? result : -result;
        });

        LinkedHashMap<String, List<String>> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : entries) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        return sorted;
    }

    public Map<String, List<String>> filterPatients(Map<String, List<String>> data, String gender, String alive, String marital, List<String> races, List<String> ethnicities) {

        Map<String, List<String>> results = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            String id = entry.getKey();
            int row = getRowNumFromId(id);

            // gender filter
            if (gender != null && !gender.isEmpty()) {
                if (!gender.equalsIgnoreCase(getValue("GENDER", row))) continue;
            }

            // alive filter - check if DEATHDATE is empty
            if (alive != null && !alive.isEmpty()) {
                String deathDate = getValue("DEATHDATE", row);
                boolean isAlive = (deathDate == null || deathDate.isEmpty());
                if ("true".equals(alive) && !isAlive) continue;
                if ("false".equals(alive) && isAlive) continue;
            }

            // marital filter
            if (marital != null && !marital.isEmpty()) {
                String val = getValue("MARITAL", row);
                if ("-".equals(marital)) {
                    // filter for unknown/blank
                    if (val != null && !val.isEmpty()) continue;
                } else {
                    if (!marital.equalsIgnoreCase(val)) continue;
                }
            }
            if (races != null && !races.isEmpty()) {
                if (!races.contains(getValue("RACE", row))) continue;
            }
            if (ethnicities != null && !ethnicities.isEmpty()) {
                if (!ethnicities.contains(getValue("ETHNICITY", row))) continue;
            }

            results.put(id, entry.getValue());
        }
        return results;
    }

    public List<String> getDistinctValues(String columnName) {
        Set<String> seen = new LinkedHashSet<>();
        for (int row = 0; row < getRowCount(); row++) {
            String value = getValue(columnName, row);
            if (value != null && !value.isEmpty()) {
                seen.add(value);
            }
        }
        List<String> distinct = new ArrayList<>(seen);
        distinct.sort(String::compareToIgnoreCase);
        return distinct;
    }

    public LinkedHashMap<String, String> getDistinctValuesWithLabels(String columnName) {
        List<String> distinct = getDistinctValues(columnName);
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (String value : distinct) {
            result.put(value, formatValue(columnName, value));
        }
        return result;
    }

    public void saveToCSV() throws IOException {
        CSVWriter writer = new CSVWriter();
        writer.save(dataFrame, currentDataFile);
    }

    public void deletePatient(String id) throws IOException {
        int row = getRowNumFromId(id);
        dataFrame.removeRow(row);
        saveToCSV();
    }

    public void addPatient(Map<String, String> values) throws IOException {
        dataFrame.addRow(values);
        saveToCSV();
    }

    public String generateUUID() {
        // unlikely for a UUID to clash with an existing one, but worth ensuring it cannot happen
        String newId;
        do {
            newId = java.util.UUID.randomUUID().toString();
        } while (idExists(newId));
        return newId;
    }

    public void editPatient(String id, Map<String, String> values) throws IOException {
        int row = getRowNumFromId(id);
        dataFrame.editRow(row, values);
        saveToCSV();
    }

    public void exportToJSON() throws IOException {
        JSONWriter writer = new JSONWriter();
        Path outputPath = Paths.get("data", "patients.json");
        writer.write(dataFrame, outputPath.toString());
    }

    public Map<String, String> buildResetUrls(String search, String gender, String alive, String marital, List<String> race, List<String> ethnicity) {
        Map<String, String> urls = new HashMap<>();
        urls.put("gender", buildUrl(search, null, alive, marital, race, ethnicity));
        urls.put("alive",  buildUrl(search, gender, null, marital, race, ethnicity));
        urls.put("marital",buildUrl(search, gender, alive, null, race, ethnicity));
        urls.put("race",   buildUrl(search, gender, alive, marital, null, ethnicity));
        urls.put("ethnicity", buildUrl(search, gender, alive, marital, race, null));
        return urls;
    }

    private String buildUrl(String search, String gender, String alive, String marital, List<String> race, List<String> ethnicity) {
        StringBuilder sb = new StringBuilder("/runsearch?");
        if (search != null && !search.isEmpty()) sb.append("searchstring=").append(search).append("&");
        if (gender != null && !gender.isEmpty()) sb.append("gender=").append(gender).append("&");
        if (alive != null && !alive.isEmpty()) sb.append("alive=").append(alive).append("&");
        if (marital != null && !marital.isEmpty()) sb.append("marital=").append(marital).append("&");
        if (race != null) race.forEach(r -> sb.append("race=").append(r).append("&"));
        if (ethnicity != null) ethnicity.forEach(e -> sb.append("ethnicity=").append(e).append("&"));
        return sb.toString();
    }

    public String buildPaginationBaseUrl(String search, String gender, String alive, String marital, List<String> race, List<String> ethnicity, String sortKey, String sortDir) {
        StringBuilder sb = new StringBuilder(
            (search != null && !search.isEmpty()) ? "/runsearch?" : "/patientList?"
        );
        if (search != null && !search.isEmpty()) sb.append("searchstring=").append(search).append("&");
        if (gender != null && !gender.isEmpty()) sb.append("gender=").append(gender).append("&");
        if (alive != null && !alive.isEmpty()) sb.append("alive=").append(alive).append("&");
        if (marital != null && !marital.isEmpty()) sb.append("marital=").append(marital).append("&");
        if (race != null) race.forEach(r -> sb.append("race=").append(r).append("&"));
        if (ethnicity != null) ethnicity.forEach(e -> sb.append("ethnicity=").append(e).append("&"));
        if (sortKey != null && !sortKey.isEmpty()) sb.append("sort=").append(sortKey).append("&dir=").append(sortDir != null ? sortDir : "asc").append("&");
        sb.append("page=");
        return sb.toString();
    }

    /* STATISTICS */ 
 
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return LocalDate.parse(raw.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    private int ageAt(LocalDate birth, LocalDate end) {
        return Period.between(birth, end).getYears();
    }
    
    public int getTotalPatients() {
        return dataFrame.getRowCount();
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
                .map(e -> formatValue("ETHNICITY", e.getKey()))
                .orElse("N/A");
    }
    
    // ── Stats: chart data ────────────────────────────────────────
    
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
                result.put(formatValue("ETHNICITY", sorted.get(i).getKey()), sorted.get(i).getValue());
            } else {
                otherCount += sorted.get(i).getValue();
            }
        }
        if (otherCount > 0) result.put("Other", otherCount);
        return result;
    }
    
    public Map<String, Integer> getRaceCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String val = dataFrame.getValue("RACE", row);
            if (val != null && !val.isBlank()) {
                String label = formatValue("RACE", val);
                counts.merge(label, 1, Integer::sum);
            }
        }
        // Sort by count descending
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());
        Map<String, Integer> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : entries) sorted.put(e.getKey(), e.getValue());
        return sorted;
    }
}
