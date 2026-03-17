package uk.ac.ucl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ucl.util.Formatter;

class PatientQuery {
    
    private final DataFrame dataFrame;

    public PatientQuery(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    public List<String> searchPatients(String searchString) {
        String[] lowerStrings = searchString.toLowerCase().split(" ");
        List<String> searchResultIDs = new ArrayList<>();

        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            boolean valid = true;
            for (String term : lowerStrings) {
                boolean found = false;
                for (String column : dataFrame.getColumnNames()) {
                    String value = dataFrame.getValue(column, row);
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
                searchResultIDs.add(dataFrame.getValue("ID", row));
            }
        }
        return searchResultIDs;
    }

    public Map<String, List<String>> filterPatients(Map<String, List<String>> data, String gender, String alive, String marital, List<String> races, List<String> ethnicities) {

        Map<String, List<String>> results = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            String id = entry.getKey();
            int row = dataFrame.getRowNumFromId(id);

            // gender filter
            if (gender != null && !gender.isEmpty()) {
                if (!gender.equalsIgnoreCase(dataFrame.getValue("GENDER", row))) continue;
            }

            // alive filter - check if DEATHDATE is empty
            if (alive != null && !alive.isEmpty()) {
                String deathDate = dataFrame.getValue("DEATHDATE", row);
                boolean isAlive = (deathDate == null || deathDate.isEmpty());
                if ("true".equals(alive) && !isAlive) continue;
                if ("false".equals(alive) && isAlive) continue;
            }

            // marital filter
            if (marital != null && !marital.isEmpty()) {
                String val = dataFrame.getValue("MARITAL", row);
                if ("-".equals(marital)) {
                    // filter for unknown/blank
                    if (val != null && !val.isEmpty()) continue;
                } else {
                    if (!marital.equalsIgnoreCase(val)) continue;
                }
            }
            if (races != null && !races.isEmpty()) {
                if (!races.contains(dataFrame.getValue("RACE", row))) continue;
            }
            if (ethnicities != null && !ethnicities.isEmpty()) {
                if (!ethnicities.contains(dataFrame.getValue("ETHNICITY", row))) continue;
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
                    int rowA = dataFrame.getRowNumFromId(a.getKey());
                    int rowB = dataFrame.getRowNumFromId(b.getKey());
                    valA = dataFrame.getValue(col, rowA);
                    valB = dataFrame.getValue(col, rowB);
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
}
