package uk.ac.ucl.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

class DataLoader {

    public DataLoader() {
        
    }

    public DataFrame load(String csvFilePath) throws IOException {
        DataFrame dataFrame = new DataFrame();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
         CSVParser csvParser = new CSVParser(reader,
                 CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {
            
            // create columns
            for (String header : csvParser.getHeaderNames()) {
                dataFrame.addColumn(new Column(header));
            }

            // read rows
            for (CSVRecord record : csvParser) {
                for (String header : csvParser.getHeaderNames()) {
                    String value = record.get(header);
                    dataFrame.addValue(header, value);
                }
            }
        }
        return dataFrame;
    }
}