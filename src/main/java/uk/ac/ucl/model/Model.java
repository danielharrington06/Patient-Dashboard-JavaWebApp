package uk.ac.ucl.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Model
{
    DataFrame dataFrame;
    public static final int DEFAULT_PAGE_SIZE = 25;

    public Model() {
        DataLoader loader = new DataLoader();
        try {
            this.dataFrame = loader.load("data/patients100.csv");
        } catch (IOException e) {
            System.err.println("Error loading CSV file: " + e.getMessage());
            this.dataFrame = new DataFrame(); // fallback to empty frame
        }
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
        for (int row = 0; row < getRowCount(); row++) {
            if (id.equals(getValue("ID", row))) {
                return row;
            }
        }
        // not found, so invalid ID and incorrect code
        throw new IllegalArgumentException("Invalid id: " + id);
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

    public LinkedHashMap<String, String> getFormattedPatientRecord(String id) {
        LinkedHashMap<String, String> formatted = new LinkedHashMap<>();
        for (String column : getColumnNames()) {
            int row = getRowNumFromId(id);
            formatted.put(dataFrame.formatColumnName(column), formatValue(column, getValue(column, row)));
        }
        return formatted;
    }

    /* public Patient getPatientRecord(String id) {
        int row = getRowNumFromId(id);
        List<String> columns = getColumnNames();
        List<String> rowContents = new ArrayList<>();
        for (String column : columns) {
            rowContents.add(getValue(column, row));
        }
        return new Patient(
            rowContents.get(0),   // ID
            rowContents.get(1),   // BIRTHDATE
            rowContents.get(2),   // DEATHDATE
            rowContents.get(3),   // SSN
            rowContents.get(4),   // DRIVERS
            rowContents.get(5),   // PASSPORT
            rowContents.get(6),   // PREFIX
            rowContents.get(7),   // FIRST
            rowContents.get(8),   // LAST
            rowContents.get(9),   // SUFFIX
            rowContents.get(10),  // MAIDEN
            rowContents.get(11),  // MARITAL
            rowContents.get(12),  // RACE
            rowContents.get(13),  // ETHNICITY
            rowContents.get(14),  // GENDER
            rowContents.get(15),  // BIRTHPLACE
            rowContents.get(16),  // ADDRESS
            rowContents.get(17),  // CITY
            rowContents.get(18),  // STATE
            rowContents.get(19)   // ZIP
        );
    } */

    public List<String> getSummaryColumnDisplayNames() {
        List<String> summaryColumns = List.of("FIRST", "BIRTHDATE", "DEATHDATE", "GENDER", "MARITAL", "RACE", "ETHNICITY", "CITY", "STATE");
        List<String> displayNames = new ArrayList<>();
        for (String col : summaryColumns) {
            displayNames.add(dataFrame.formatColumnName(col));
        }
        return displayNames;
    }

    public List<String> packagePatientSummaryInfo(int row) {
        List<String> info = new ArrayList<>();
        info.add(formatValue("FIRST", dataFrame.getValue("FIRST", row) + " " + dataFrame.getValue("LAST", row)));
        info.add(formatValue("BIRTHDATE", dataFrame.getValue("BIRTHDATE", row)));
        info.add(formatValue("DEATHDATE", dataFrame.getValue("DEATHDATE", row)));
        info.add(formatValue("GENDER", dataFrame.getValue("GENDER", row)));
        info.add(formatValue("MARITAL", dataFrame.getValue("MARITAL", row)));
        info.add(formatValue("RACE", dataFrame.getValue("RACE", row)));
        info.add(formatValue("ETHNICITY", dataFrame.getValue("ETHNICITY", row)));
        info.add(formatValue("CITY", dataFrame.getValue("CITY", row)));
        info.add(formatValue("STATE", dataFrame.getValue("STATE", row)));
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
}
