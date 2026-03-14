package uk.ac.ucl.model;

import java.util.ArrayList;

public class Column {
    
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
    
    // Out of bounds access throws IndexOutOfBoundsException from ArrayList
    // indicates a programming error in the calling code

    public String getRowValue(int index) {
        return rows.get(index);
    }

    public void addRowValue(String value) {
        rows.add(value);
    }

    public void setRowValue(int index, String value) {
        rows.set(index, value);
    }

    public void removeRowValue(int row) {
        rows.remove(row);
    }
}
