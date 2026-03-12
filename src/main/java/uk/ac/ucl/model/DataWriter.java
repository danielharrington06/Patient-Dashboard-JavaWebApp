package uk.ac.ucl.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class DataWriter {

    public void save(DataFrame dataFrame, String csvFilePath) throws IOException {
        List<String> columnNames = dataFrame.getColumnNames();
        String[] headers = columnNames.toArray(String[]::new);

        CSVFormat format = CSVFormat.DEFAULT.withHeader(headers);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(csvFilePath)));
             CSVPrinter printer = new CSVPrinter(writer, format)) {

            for (int row = 0; row < dataFrame.getRowCount(); row++) {
                List<String> values = new ArrayList<>();
                for (String col : columnNames) {
                    String val = dataFrame.getValue(col, row);
                    values.add(val != null ? val : "");
                }
                printer.printRecord(values);
            }
        }
    }
}