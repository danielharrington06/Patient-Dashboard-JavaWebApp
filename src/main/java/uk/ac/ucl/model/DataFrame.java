package uk.ac.ucl.model;

import java.util.ArrayList;

class DataFrame {
    // need to decide a consistent way to deal with out of range

    private final ArrayList<Column> columns;

    public DataFrame() {
        this.columns = new ArrayList<>();
    }

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
        return columns.get(0).getSize();
    }

    public String getValue(String field, int row) {
        int i = getColumnNames().indexOf(field);
        return columns.get(i).getRowValue(row);
    }

    public void putValue(String field, int row, String value) {
        int i = getColumnNames().indexOf(field);
        columns.get(i).setRowValue(row, value);
    }

    public void addValue(String field, String value) {
        int i = getColumnNames().indexOf(field);
        columns.get(i).addRowValue(value);
    }
}