package uk.ac.ucl.model;

public class Patient {
    private final String id;
    private final String birthDate;
    private final String deathDate;
    private final String ssn;
    private final String drivers;
    private final String passport;
    private final String prefix;
    private final String first;
    private final String last;
    private final String suffix;
    private final String maiden;
    private final String marital;
    private final String race;
    private final String ethnicity;
    private final String gender;
    private final String birthPlace;
    private final String address;
    private final String city;
    private final String state;
    private final String zip;

    public Patient(
            String id,
            String birthDate,
            String deathDate,
            String ssn,
            String drivers,
            String passport,
            String prefix,
            String first,
            String last,
            String suffix,
            String maiden,
            String marital,
            String race,
            String ethnicity,
            String gender,
            String birthPlace,
            String address,
            String city,
            String state,
            String zip) {

        this.id = id;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.ssn = ssn;
        this.drivers = drivers;
        this.passport = passport;
        this.prefix = prefix;
        this.first = first;
        this.last = last;
        this.suffix = suffix;
        this.maiden = maiden;
        this.marital = marital;
        this.race = race;
        this.ethnicity = ethnicity;
        this.gender = gender;
        this.birthPlace = birthPlace;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    // Getters (required for JSP EL)

    public String get(String field) {
        switch (field.toUpperCase()) {
            case "ID" -> {return id;}
            case "BIRTHDATE" -> {return birthDate;}
            case "DEATHDATE" -> {return deathDate;}
            case "SSN" -> {return ssn;}
            case "DRIVERS" -> {return drivers;}
            case "PASSPORT" -> {return passport;}
            case "PREFIX" -> {return prefix;}
            case "FIRST" -> {return first;}
            case "LAST" -> {return last;}
            case "SUFFIX" -> {return suffix;}
            case "MAIDEN" -> {return maiden;}
            case "MARITAL" -> {return marital;}
            case "RACE" -> {return race;}
            case "ETHNICITY" -> {return ethnicity;}
            case "GENDER" -> {return gender;}
            case "BIRTHPLACE" -> {return birthPlace;}
            case "ADDRESS" -> {return address;}
            case "CITY" -> {return city;}
            case "STATE" -> {return state;}
            case "ZIP" -> {return zip;}
            default -> throw new IllegalArgumentException("Unknown field: " + field);
        }
    }
}
