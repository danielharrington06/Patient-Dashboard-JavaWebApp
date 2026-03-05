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

    public String getValue(String columnName, int row) {
        int i = getColumnNames().indexOf(columnName);
        return columns.get(i).getRowValue(row);
    }

    public void putValue(String columnName, int row, String value) {
        int i = getColumnNames().indexOf(columnName);
        columns.get(i).setRowValue(row, value);
    }

    public void addValue(String columnName, String value) {
        int i = getColumnNames().indexOf(columnName);
        columns.get(i).addRowValue(value);
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
            default -> throw new IllegalArgumentException("Unknown field: " + columnName);
        }
    }
}