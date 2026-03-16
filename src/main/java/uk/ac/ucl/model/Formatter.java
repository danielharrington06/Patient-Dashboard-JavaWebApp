package uk.ac.ucl.model;

import java.time.format.DateTimeFormatter;

public class Formatter {
    // private constructor prevents accidentally instanstiating it
    public Formatter() {} 

    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static String formatColumnName(String columnName) {
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
            default -> { return columnName; }
        }
    }

    public static String formatValue(String columnName, String value) {
        if (value == null || value.isEmpty()) return "—";
        
        switch (columnName.toUpperCase()) {
            case "GENDER" -> {
                return switch (value.toUpperCase()) {
                    case "M" -> "Male";
                    case "F" -> "Female";
                    default -> value;
                };
            }
            case "MARITAL" -> {
                return switch (value.toUpperCase()) {
                    case "M" -> "Married";
                    case "S" -> "Single";
                    default -> value;
                };
            }
            case "ETHNICITY", "RACE" -> {
                // convert underscores to spaces and capitalise each word
                String[] words = value.replace("_", " ").split(" ");
                StringBuilder sb = new StringBuilder();
                for (String word : words) {
                    if (!word.isEmpty()) {
                        sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
                    }
                }
                return sb.toString().trim();
            }
            case "BIRTHDATE", "DEATHDATE" -> {
                // convert 1979-08-24 to 24 Aug 1979
                try {
                    String[] parts = value.split("-");
                    String[] months = {"Jan","Feb","Mar","Apr","May","Jun",
                                    "Jul","Aug","Sep","Oct","Nov","Dec"};
                    int month = Integer.parseInt(parts[1]) - 1;
                    return parts[2] + " " + months[month] + " " + parts[0];
                } catch (NumberFormatException e) {
                    return value;
                }
            }
            default -> { return value; }
        }
    }
}
