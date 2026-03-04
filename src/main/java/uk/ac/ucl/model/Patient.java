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

    public String getId() { return id; }
    public String getBirthDate() { return birthDate; }
    public String getDeathDate() { return deathDate; }
    public String getSsn() { return ssn; }
    public String getDrivers() { return drivers; }
    public String getPassport() { return passport; }
    public String getPrefix() { return prefix; }
    public String getFirst() { return first; }
    public String getLast() { return last; }
    public String getSuffix() { return suffix; }
    public String getMaiden() { return maiden; }
    public String getMarital() { return marital; }
    public String getRace() { return race; }
    public String getEthnicity() { return ethnicity; }
    public String getGender() { return gender; }
    public String getBirthPlace() { return birthPlace; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZip() { return zip; }
}
