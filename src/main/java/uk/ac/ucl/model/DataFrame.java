package uk.ac.ucl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class DataFrame {
    
    private final ArrayList<Column> columns;
    private Map<String, Integer> idToRowIndex = new HashMap<>();

    public DataFrame() {
        this.columns = new ArrayList<>();
    }

    /* -- Indexing -- */

    public final void rebuildIndex() {
        idToRowIndex = new HashMap<>();
        for (int row = 0; row < getRowCount(); row++) {
            idToRowIndex.put(getValue("ID", row), row);
        }
    }
    
    public int getRowNumFromId(String id) {
        Integer row = idToRowIndex.get(id);
        if (row == null) throw new IllegalArgumentException("Invalid id: " + id);
        return row;
    }

    public boolean idExists(String id) {
        return idToRowIndex.containsKey(id);
    }

    public String generateUUID() {
        // unlikely for a UUID to clash with an existing one, but worth ensuring it cannot happen
        String newId;
        do {
            newId = java.util.UUID.randomUUID().toString();
        } while (idExists(newId));
        return newId;
    }

    /** -- Get and Set -- */

    public void addColumn(Column column) {
        columns.add(column);
    }

    public ArrayList<String> getColumnNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Column column : columns) { 
            names.add(column.getName()); 
        }
        return names;
    }

    public int getRowCount() {
        if (columns.isEmpty()) return 0;
        return columns.get(0).getSize();
    }

    public String getValue(String columnName, int row) {
        int i = getColumnNames().indexOf(columnName);
        return columns.get(i).getRowValue(row);
    }

    public void putValue(int row, String columnName, String value) {
        int i = getColumnNames().indexOf(columnName);
        columns.get(i).setRowValue(row, value);
    }

    public void addValue(String columnName, String value) {
        int i = getColumnNames().indexOf(columnName);
        columns.get(i).addRowValue(value);
    }

    public void removeRow(int row) {
        for (Column column : columns) {
            column.removeRowValue(row);
        }
    }

    /**
     * @param values - map of column name to value — unspecified columns default to empty string
     */
    public void addRow(Map<String, String> values) {
        for (Column column : columns) {
            String val = values.getOrDefault(column.getName(), "");
            column.addRowValue(val);
        }
    }

    /**
     * @param values - map of column name to value - including edited attributes only
     */
    public void editRow(int row, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            putValue(row, entry.getKey(), entry.getValue());
        }
    }
}