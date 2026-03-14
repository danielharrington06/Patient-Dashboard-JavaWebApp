package uk.ac.ucl.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONWriter {
    public JSONWriter() {

    }

    public void write(DataFrame dataFrame, String jsonFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> rows = new ArrayList<>();

        List<String> columnNames = dataFrame.getColumnNames();

        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            Map<String, String> row = new LinkedHashMap<>();
            for (String col : columnNames) {
                row.put(col, dataFrame.getValue(col, i));
            }
            rows.add(row);
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(jsonFilePath), rows);
    }
}
