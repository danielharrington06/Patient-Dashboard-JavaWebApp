package uk.ac.ucl.model;

import java.util.ArrayList;

public class Column {
    // need to decide consistent way to deal with out of bounds
    
    private String name;
    private final ArrayList<String> rows;

    public Column(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Column name cannot be null nor blank");
        }
        this.name = name;
        this.rows = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return rows.size();
    }

    public String getRowValue(int index) {
        return rows.get(index);
    }

    public void setRowValue(int index, String value) {
        rows.set(index, value);
    }

    public void addRowValue(String value) {
        rows.add(value);
    }

}
