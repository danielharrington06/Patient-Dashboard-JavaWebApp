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
    public static final int DEFAULT_PAGE_SIZE = 40;
    public static final String DATA_FILE = "data/patients100.csv";

    public Model() {
        DataLoader loader = new DataLoader();
        try {
            this.dataFrame = loader.load(DATA_FILE);
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

    public LinkedHashMap<String, String> getFormattedPatientRecord(String id) {
        LinkedHashMap<String, String> formatted = new LinkedHashMap<>();
        for (String column : getColumnNames()) {
            int row = getRowNumFromId(id);
            formatted.put(formatColumnName(column), formatValue(column, getValue(column, row)));
        }
        return formatted;
    }

    public List<String> getSummaryColumnDisplayNames() {
        List<String> summaryColumns = List.of("FIRST", "LAST", "BIRTHDATE", "DEATHDATE", "GENDER", "MARITAL", "RACE", "ETHNICITY", "CITY", "STATE");
        List<String> displayNames = new ArrayList<>();
        for (String col : summaryColumns) {
            displayNames.add(formatColumnName(col));
        }
        return displayNames;
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
        List<String> distinct = new ArrayList<>();
        for (int row = 0; row < getRowCount(); row++) {
            String value = getValue(columnName, row);
            if (value != null && !value.isEmpty() && !distinct.contains(value)) {
                distinct.add(value);
            }
        }
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
        DataWriter writer = new DataWriter();
        writer.save(dataFrame, DATA_FILE);
    }

    public void deletePatient(String id) throws IOException {
        int row = getRowNumFromId(id);
        dataFrame.removeRow(row);
        saveToCSV();
    }
}
