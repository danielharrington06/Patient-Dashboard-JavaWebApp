package uk.ac.ucl.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ucl.util.Formatter;

public class Model
{
    private final Statistics statistics;

    private DataFrame dataFrame;
    private Map<String, Integer> idToRowIndex = new HashMap<>();
    
    private String currentDataFile;
    public static final String DEFAULT_FILE = Paths.get("data", "patients100.csv").toString();

    public static final int DEFAULT_PAGE_SIZE = 50;

    public static final int ETHNICITY_CHART_TOP_N = 8;

    public Model() {
        try {
            reloadData(DEFAULT_FILE);
        } catch (IOException e) {
            System.err.println("Error loading CSV file: " + e.getMessage());
            this.dataFrame = new DataFrame(); // fallback to empty frame
        }
        finally {
            statistics = new Statistics(dataFrame);

        }
    }

    /* -- Data Loading -- */

    public final void reloadData(String filename) throws IOException {
        CSVLoader loader = new CSVLoader();
        this.dataFrame = loader.load(filename);
        this.currentDataFile = filename;
        buildIndex();
    }

    public String getCurrentDataFile() {
        return currentDataFile;
    }

    /* -- Indexing -- */

    private void buildIndex() {
        idToRowIndex = new HashMap<>();
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            idToRowIndex.put(dataFrame.getValue("ID", row), row);
        }
    }
    
    public int getRowNumFromId(String id) {
        Integer row = idToRowIndex.get(id);
        if (row == null) throw new IllegalArgumentException("Invalid id: " + id);
        return row;
    }

    public boolean idExists(String id) {
        return idToRowIndex.containsKey(id);
    }

    public String generateUUID() {
        // unlikely for a UUID to clash with an existing one, but worth ensuring it cannot happen
        String newId;
        do {
            newId = java.util.UUID.randomUUID().toString();
        } while (idExists(newId));
        return newId;
    }

    /* -- Data Access -- */

    public List<String> getColumnNames() {
        return dataFrame.getColumnNames();
    }

    public int getNumPatients() { return dataFrame.getRowCount(); }

    public String getValue(String columnName, int row) {
        return dataFrame.getValue(columnName, row);
    }

    public List<String> getDistinctValues(String columnName) {
        Set<String> seen = new LinkedHashSet<>();
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String value = getValue(columnName, row);
            if (value != null && !value.isEmpty()) {
                seen.add(value);
            }
        }
        List<String> distinct = new ArrayList<>(seen);
        return distinct;
    }

    public LinkedHashMap<String, String> getDistinctValuesWithLabels(String columnName) {
        List<String> distinct = getDistinctValues(columnName);
        distinct.sort(String::compareToIgnoreCase);
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        for (String value : distinct) {
            result.put(value, Formatter.formatValue(columnName, value));
        }
        return result;
    }
    
    /* -- Patient Records -- */

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
            formatted.put(column, Formatter.formatValue(column, getValue(column, row)));
        }
        return formatted;
    }

    public List<String> getSummaryColumnNamesFormatted() {
        List<String> summaryColumns = List.of("FIRST", "LAST", "BIRTHDATE", "DEATHDATE", "GENDER", "MARITAL", "RACE", "ETHNICITY", "CITY");
        List<String> displayNames = new ArrayList<>();
        for (String col : summaryColumns) {
            displayNames.add(Formatter.formatColumnName(col));
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
        info.add(Formatter.formatValue("FIRST", dataFrame.getValue("FIRST", row)));  // index 0: firstname
        info.add(Formatter.formatValue("LAST", dataFrame.getValue("LAST", row)));    // index 1: lastname
        info.add(Formatter.formatValue("BIRTHDATE", dataFrame.getValue("BIRTHDATE", row)));
        info.add(Formatter.formatValue("DEATHDATE", dataFrame.getValue("DEATHDATE", row)));
        info.add(Formatter.formatValue("GENDER", dataFrame.getValue("GENDER", row)));
        info.add(Formatter.formatValue("MARITAL", dataFrame.getValue("MARITAL", row)));
        info.add(Formatter.formatValue("RACE", dataFrame.getValue("RACE", row)));
        info.add(Formatter.formatValue("ETHNICITY", dataFrame.getValue("ETHNICITY", row)));
        info.add(Formatter.formatValue("CITY", dataFrame.getValue("CITY", row)));
        return info;
    }

    public Map<String, List<String>> getPatientSummaries() {
        // gets a hash map of id to contents: [fullName, dob, gender, city, state]
        Map<String, List<String>> patients = new LinkedHashMap<>();
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String id = dataFrame.getValue("ID", row);
            List<String> info = packagePatientSummaryInfo(row);
            patients.put(id, info);
        }
        return patients;
    }

    /* -- Search, Filter, Sort -- */

    public Map<String, List<String>> searchPatientSummaries(String searchString) {
        String[] lowerStrings = searchString.toLowerCase().split(" ");
        Map<String, List<String>> results = new HashMap<>();

        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            boolean valid = true;
            for (String term : lowerStrings) {
                boolean found = false;
                for (String column : getColumnNames()) {
                    String value = getValue(column, row);
                    if (value != null && Formatter.formatValue(column, value).toLowerCase().contains(term)) {
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

    /* -- Pagination -- */

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

    /* -- CRUD (Create, Read, Update Delete) Operations -- */

    public void deletePatient(String id) throws IOException {
        int row = getRowNumFromId(id);
        dataFrame.removeRow(row);
        saveToCSV();
    }

    public void addPatient(Map<String, String> values) throws IOException {
        dataFrame.addRow(values);
        saveToCSV();
    }

    public void editPatient(String id, Map<String, String> values) throws IOException {
        int row = getRowNumFromId(id);
        dataFrame.editRow(row, values);
        saveToCSV();
    }

    public void saveToCSV() throws IOException {
        CSVWriter writer = new CSVWriter();
        writer.save(dataFrame, currentDataFile);
    }

    public void exportToJSON() throws IOException {
        JSONWriter writer = new JSONWriter();
        Path outputPath = Paths.get("data", "patients.json");
        writer.write(dataFrame, outputPath.toString());
    }

    /* -- Statistics -- */

    public int getOldestAliveAge() { return statistics.getOldestAliveAge(); }
    
    public int getYoungestAliveAge() { return statistics.getYoungestAliveAge(); }
    
    public double getAverageAliveAge() { return statistics.getAverageAliveAge(); }
    
    public double getAverageAgeAtDeath() { return statistics.getAverageAgeAtDeath(); }
    
    public String[] getMostCommonCity() { return statistics.getMostCommonCity(); }
    
    public String getMostCommonEthnicity() { return statistics.getMostCommonEthnicity(); }

    public String getMostCommonRace() { return statistics.getMostCommonRace(); }

    public int getLivingCount() { return statistics.getLivingCount(); }
    
    public Map<String, Integer> getGenderCounts() { return statistics.getGenderCounts(); }
    
    public Map<String, Integer> getMaritalCounts() { return statistics.getMaritalCounts(); }

    public Map<String, Integer> getLivingDeceasedCounts() { return statistics.getLivingDeceasedCounts(); }
    
    public Map<String, Integer> getEthnicityCounts(int topN) { return statistics.getEthnicityCounts(topN); }

    public Map<String, Integer> getAliveAgeHistogram() { return statistics.getAliveAgeHistogram(); }
}