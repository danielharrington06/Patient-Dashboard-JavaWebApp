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
    private Statistics statistics;
    private PatientQuery patientQuery;

    private DataFrame dataFrame;
    
    private String currentDataFile;
    public static final String DEFAULT_FILE = Paths.get("data", "patients100.csv").toString();

    public static final int DEFAULT_PAGE_SIZE = 50;

    public static final int ETHNICITY_CHART_TOP_N = 8;

    public static final List<String> ORDERED_COLUMNS = List.of("ID", "PREFIX", "FIRST", "LAST", "SUFFIX", "MAIDEN", "GENDER", "BIRTHDATE", "DEATHDATE", "MARITAL", "RACE", "ETHNICITY", "BIRTHPLACE", "SSN", "DRIVERS", "PASSPORT", "ADDRESS", "CITY", "STATE", "ZIP");

    private static final List<String> SUMMARY_COLUMNS = List.of("FIRST", "LAST", "BIRTHDATE", "DEATHDATE", "GENDER", "MARITAL", "RACE", "ETHNICITY", "CITY");

    public Model() {

        try {
            reloadData(DEFAULT_FILE);
        } catch (IOException e) {
            System.err.println("Error loading CSV file: " + e.getMessage());
            this.dataFrame = new DataFrame();
            this.statistics = new Statistics(dataFrame);
            this.patientQuery = new PatientQuery(dataFrame);
        }
    }

    public final void reloadData(String filename) throws IOException {
        CSVLoader loader = new CSVLoader();
        this.dataFrame = loader.load(filename);
        this.currentDataFile = filename;
        this.statistics = new Statistics(dataFrame);
        this.patientQuery = new PatientQuery(dataFrame);
    }

    public String getCurrentDataFile() {
        return currentDataFile;
    }

    /* -- Data Access -- */

    /** Only call this method from servlets
     * 
     * @return
     */
    public List<String> getColumnNames() {
        return dataFrame.getColumnNames();
    }

    public int getNumPatients() { 
        return dataFrame.getRowCount(); 
    }

    public List<String> getDistinctValues(String columnName) {
        Set<String> seen = new LinkedHashSet<>();
        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            String value = dataFrame.getValue(columnName, row);
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
        int row = dataFrame.getRowNumFromId(id);
        for (String column : dataFrame.getColumnNames()) {
            record.put(column, dataFrame.getValue(column, row));
        }
        return record;
    }

    public Map<String, String> getFormattedPatientRecord(String id) {
        Map<String, String> formatted = new LinkedHashMap<>();
        int row = dataFrame.getRowNumFromId(id);
        for (String column : dataFrame.getColumnNames()) {
            formatted.put(column, Formatter.formatValue(column, dataFrame.getValue(column, row)));
        }
        return formatted;
    }

    public List<String> getSummaryColumnNamesFormatted() {
        List<String> displayNames = new ArrayList<>();
        for (String col : SUMMARY_COLUMNS) {
            displayNames.add(Formatter.formatColumnName(col));
        }
        return displayNames;
    }

    public Map<String, String> getAllColumnNamesFormatted() {
        Map<String, String> columnLabels = new LinkedHashMap<>();
        for (String column : ORDERED_COLUMNS) {
            columnLabels.put(column, Formatter.formatColumnName(column));
        }
        return columnLabels;
    }

    public List<String> packagePatientSummaryInfo(int row) {
    List<String> info = new ArrayList<>();

        for (String column : SUMMARY_COLUMNS) {
            info.add(Formatter.formatValue(column, dataFrame.getValue(column, row)));
        }
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

    /* Patient Query - search, filter, sort, paginate */

    public Map<String, List<String>> searchPatientSummaries(String searchString) {
        List<String> searchResultIDs = patientQuery.searchPatients(searchString);
        Map<String, List<String>> results = new HashMap<>();
        for (String id : searchResultIDs) {
            List<String> info = packagePatientSummaryInfo(dataFrame.getRowNumFromId(id));
            results.put(id, info);
        }
        return results;
    }

    public Map<String, List<String>> filterPatients(Map<String, List<String>> data, String gender, String alive, String marital, List<String> races, List<String> ethnicities) {
        return patientQuery.filterPatients(data, gender, alive, marital, races, ethnicities);
    }

    public Map<String, List<String>> sortPatientSummaries(Map<String, List<String>> data, String sortKey, boolean ascending) {
        return patientQuery.sortPatientSummaries(data, sortKey, ascending);
    }

    public Map<String, List<String>> getPage(Map<String, List<String>> data, int page, int pageSize) {
        return patientQuery.getPage(data, page, pageSize);
    }

    /* -- CRUD (Create, Read, Update Delete) Operations -- */

    public String generateUUID() {
        return dataFrame.generateUUID();
    }

    public void deletePatient(String id) throws IOException {
        int row = dataFrame.getRowNumFromId(id);
        dataFrame.removeRow(row);
        dataFrame.rebuildIndex();
        saveToCSV();
    }

    public void addPatient(Map<String, String> values) throws IOException {
        dataFrame.addRow(values);
        dataFrame.rebuildIndex();
        saveToCSV();
    }

    public void editPatient(String id, Map<String, String> values) throws IOException {
        int row = dataFrame.getRowNumFromId(id);
        dataFrame.editRow(row, values);
        dataFrame.rebuildIndex();
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