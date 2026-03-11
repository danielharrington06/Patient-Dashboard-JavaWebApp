package uk.ac.ucl.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataWriter {
    public DataWriter() {
        
    }
    
    public void save(DataFrame dataFrame, String csvFilePath) throws IOException {
        File file = new File(csvFilePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            List<String> columnNames = dataFrame.getColumnNames();
            
            // write header
            writer.write(String.join(",", columnNames));
            writer.newLine();

            // write each row
            for (int row = 0; row < dataFrame.getRowCount(); row++) {
                List<String> values = new ArrayList<>();
                for (String col : columnNames) {
                    String val = dataFrame.getValue(col, row);
                    if (val == null) val = "";
                    // wrap in quotes if value contains a comma
                    if (val.contains(",")) val = "\"" + val + "\"";
                    values.add(val);
                }
                writer.write(String.join(",", values));
                writer.newLine();
            }
        }
    }
}
