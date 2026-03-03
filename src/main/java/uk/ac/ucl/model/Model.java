package uk.ac.ucl.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public List<String> getPatientNames() {
        List<String> names = new ArrayList<>();
        for (int row = 0; row < getRowCount(); row++) {
            String first = dataFrame.getValue("FIRST", row);
            String last = dataFrame.getValue("LAST", row);
            names.add(first + " " + last);
        }
        return names;
    }

    /* public List<String> getPatientNames()
    {
        return readFile("data/patients100.csv");
    }
    */

    // This also returns dummy data. The real version should use the keyword parameter to search
    // the data and return a list of matching items.
    public List<String> searchFor(String keyword)
    {
        return List.of("Search keyword is: "+ keyword, "result1", "result2", "result3");
    }
}
