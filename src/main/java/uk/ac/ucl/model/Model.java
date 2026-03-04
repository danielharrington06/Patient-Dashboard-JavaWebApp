package uk.ac.ucl.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model
{
    DataFrame dataFrame;

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

    public Patient getPatientRecord(String id) {
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
    }

    public Map<String, List<String>> getPatientSummaries() {
        // gets a hash map of id to contents: [fullName, dob, gender, city, state]
        Map<String, List<String>> patients = new HashMap<>();
        for (int row = 0; row < getRowCount(); row++) {
            String id = dataFrame.getValue("ID", row);
            List<String> info = new ArrayList<>();
            info.add(dataFrame.getValue("FIRST", row) + " " + dataFrame.getValue("LAST", row));
            info.add(dataFrame.getValue("BIRTHDATE", row));
            info.add(dataFrame.getValue("GENDER", row));
            info.add(dataFrame.getValue("CITY", row));
            info.add(dataFrame.getValue("STATE", row));
            patients.put(id, info);
        }
        return patients;
    }

    public Map<String, List<String>> searchPatientSummaries(String keyword) {
        String lower = keyword.toLowerCase();
        Map<String, List<String>> results = new HashMap<>();

        for (int row = 0; row < getRowCount(); row++) {
            for (String column : getColumnNames()) {
                String value = getValue(column, row);
                if (value != null && value.toLowerCase().contains(lower)) {
                    String id = getValue("ID", row);
                    List<String> info = new ArrayList<>();
                    info.add(getValue("FIRST", row) + " " + getValue("LAST", row));
                    info.add(getValue("BIRTHDATE", row));
                    info.add(getValue("GENDER", row));
                    info.add(getValue("CITY", row));
                    info.add(getValue("STATE", row));
                    results.put(id, info);
                    break;
                }
            }
        }
        return results;
    }

    // This also returns dummy data. The real version should use the keyword parameter to search
    // the data and return a list of matching items.
    public List<String> searchFor(String keyword)
    {
        return List.of("Search keyword is: "+ keyword, "result1", "result2", "result3");
    }
}
