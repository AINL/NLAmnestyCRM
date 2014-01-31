/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.config.DateFormat;
import nl.amnesty.crm.util.DateUtil;

/**
 *
 * @author ed
 */
public class Person {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    public final static int STATUS_MATCHED_ID = 3;
    public final static int STATUS_MATCHED_NAME = 4;
    public final static int STATUS_MATCHED_NAME_DATEOFBIRTH = 5;
    public final static int STATUS_MATCHED_NAME_GENDER = 6;
    public final static int STATUS_MATCHED_NAME_DATEOFBIRTH_GENDER = 7;
    //
    private static final int DATE_YYYY_MM_DD_YEAR_INDEX_BEGIN = 0;
    private static final int DATE_YYYY_MM_DD_YEAR_INDEX_END = 3;
    private static final int DATE_YYYY_MM_DD_MONTH_INDEX_BEGIN = 5;
    private static final int DATE_YYYY_MM_DD_MONTH_INDEX_END = 6;
    private static final int DATE_YYYY_MM_DD_DAY_INDEX_BEGIN = 8;
    private static final int DATE_YYYY_MM_DD_DAY_INDEX_END = 9;
    //
    public final static String FIELD_PEOPLE_ID = "PEOPLEKEY";
    public final static String FIELD_PEOPLE_TITLE = "TITLE";
    public final static String FIELD_PEOPLE_INITIALS = "INITIALS";
    public final static String FIELD_PEOPLE_INITIALS_INDEXED = "SHINITS";
    public final static String FIELD_PEOPLE_FORENAMES = "FORENAMES";
    public final static String FIELD_PEOPLE_MIDDLE = "MIDDLE";
    public final static String FIELD_PEOPLE_SURNAME = "SURNAME";
    public final static String FIELD_PEOPLE_SURNAME_INDEXED = "SHSURNAME";
    public final static String FIELD_PEOPLE_BIRTH = "DOFB";
    public final static String FIELD_PEOPLE_GENDER = "SEX";
    public final static String FIELD_PEOPLE_RECTYPE = "RECTYPE";
    public final static String FIELD_PEOPLE_PHONE = "MOBLEPHNE";
    //
    private final static List<String> middlelist = Arrays.asList("af", "aan", "bij", "de", "den", "der", "d'", "het", "'t", "in",
            "onder", "op", "over", "'s", "'t", "te", "ten", "ter", "tot", "uit", "uijt", "van", "vanden", "ver", "voor", "aan de",
            "aan den", "aan der", "aan het", "aan 't", "bij de", "bij den", "bij het", "bij 't", "boven d'", "de die", "de die le",
            "de l'", "de la", "de las", "de le", "de van der", "in de", "in den", "in der", "in het", "in 't", "onder de", "onder den",
            "onder het", "onder 't", "over de", "over den", "over het", "over 't", "op de", "op den", "op der", "op gen", "op het",
            "op 't", "op ten", "van de", "van de l'", "van den", "van der", "van gen", "van het", "van la", "van 't", "van ter",
            "van de", "uit de", "uit den", "uit het", "uit 't", "uit te de ", "uit ten", "uijt de", "uijt den", "uijt het", "uijt 't",
            "uijt te de ", "uijt ten", "voor de", "voor den", "voor in 't", "a", "al", "am", "auf", "aus", "ben", "bin", "da", "dal",
            "dalla", "della", "das", "die", "den", "der", "des", "deca", "degli", "dei", "del", "di", "do", "don", "dos", "du", "el",
            "i", "im", "L", "la", "las", "le", "les", "lo", "los", "tho", "thoe", "thor", "to", "toe", "unter", "vom", "von", "vor",
            "zu", "zum", "zur", "am de", "auf dem", "auf den", "auf der", "auf ter", "aus dem", "aus den", "aus der", "aus 'm",
            "die le", "von dem", "von den", "von der", "von 't", "vor der");
    //
    private long personid;
    private String title;
    private String initials;
    private String forenames;
    private String middle;
    private String surname;
    private Date birth;
    private String gender;
    private int status;

    public Person() {
        this.personid = 0;
        this.title = "";
        this.initials = "";
        this.forenames = "";
        this.middle = "";
        this.surname = "";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1900);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.birth = calendar.getTime();
        this.gender = "U";
        this.status = STATUS_NEW;
    }

    public Person(long personid, String title, String initials, String forenames, String middle, String surname, Date birth, String gender) {
        this.personid = personid;
        this.title = title;
        this.initials = initials;
        this.forenames = forenames;
        this.middle = middle;
        this.surname = surname;
        this.birth = birth;
        this.gender = gender;
        this.status = STATUS_NEW;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getForenames() {
        if (forenames == null) {
            return "";
        } else {
            return forenames;
        }
    }

    public void setForenames(String forenames) {
        this.forenames = forenames;
    }

    public String getGender() {
        if (gender == null) {
            return "";
        } else {
            return gender;
        }
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInitials() {
        if (initials == null) {
            return "";
        } else {
            return initials;
        }
    }

    public void setInitials(String initials) {
        if (initials == null) {
            this.initials = "";
        } else {
            this.initials = initials;
        }
    }

    public String getMiddle() {
        if (middle == null) {
            return "";
        } else {
            return middle;
        }
    }

    public void setMiddle(String middle) {
        if (middle == null) {
            this.middle = "";
        } else {
            this.middle = middle;
        }
    }

    public long getPersonid() {
        return personid;
    }

    public void setPersonid(long personid) {
        this.personid = personid;
    }

    public String getSurname() {
        if (surname == null) {
            return "";
        } else {
            return surname;
        }
    }

    public void setSurname(String surname) {
        if (surname == null) {
            this.surname = "";
        } else {
            this.surname = surname;
        }
    }

    public String getTitle() {
        if (title == null) {
            return "";
        } else {
            return title;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFormattedSalutation() {
        return formatSalutation(forenames, initials, middle, surname, gender);
    }

    private String formatSalutation(String forenames, String initials, String middle, String surname, String gender) {
        if (gender == null) {
            gender = "";
        }
        String salutation = "T.a.v. ";
        if (gender.equals("M")) {
            salutation = salutation.concat("meneer ");
        }
        if (gender.equals("F")) {
            salutation = salutation.concat("mevrouw ");
        }
        return salutation.concat(formatName(forenames, initials, middle, surname));
    }

    public String getFormattedName() {
        return formatName(forenames, initials, middle, surname);
    }

    public String getFormattedFormalName() {
        return formatFormalName(forenames, initials, middle, surname);
    }

    public String getFormattedGender() {
        if ("M".equals(this.gender)) {
            return "man";
        }
        if ("F".equals(this.gender)) {
            return "vrouw";
        }
        return "onbekend";
    }

    private String formatName(String forenames, String initials, String middle, String surname) {
        if (forenames == null) {
            forenames = "";
        }
        if (initials == null) {
            initials = "";
        } else {
            initials = formatInitials(initials);
        }
        if (middle == null) {
            middle = "";
        }
        if (surname == null) {
            surname = "";
        }
        String name = "";
        if (middle.isEmpty()) {
            name = surname;
        } else {
            name = middle.concat(" ").concat(surname);
        }
        if (forenames.isEmpty()) {
            if (!initials.isEmpty()) {
                name = initials.concat(" ").concat(name);
            }
        } else {
            name = forenames.concat(" ").concat(name);
        }
        return name;
    }

    private String formatFormalName(String forenames, String initials, String middle, String surname) {
        if (forenames == null) {
            forenames = "";
        }
        if (initials == null) {
            initials = "";
        } else {
            initials = formatInitials(initials);
        }
        if (middle == null) {
            middle = "";
        }
        if (surname == null) {
            surname = "";
        }
        String name = "";
        if (middle.isEmpty()) {
            name = surname;
        } else {
            name = middle.concat(" ").concat(surname);
        }
        if (!initials.isEmpty()) {
            name = initials.concat(" ").concat(name);
        }
        return name;
    }

    private String formatInitials(String value) {
        if (value == null) {
            return "";
        }
        if (value.isEmpty()) {
            return "";
        }
        String formatted = "";
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != '.') {
                formatted = formatted.concat(String.valueOf(value.charAt(i)));
                formatted = formatted.concat(".");
            }
        }
        return formatted.toUpperCase();
    }

    public String getFormattedNameUnaccent() {
        return formatName(unaccent(forenames), unaccent(initials), unaccent(middle), unaccent(surname));
    }

    private String unaccent(String value) {
        if (value == null) {
            return "";
        }
        value = value.replaceAll("[èéêë]", "e");
        value = value.replaceAll("[ùúûü]", "u");
        value = value.replaceAll("[ìíîï]", "i");
        value = value.replaceAll("[àáâä]", "a");
        value = value.replaceAll("[òóôö]", "o");

        value = value.replaceAll("[ÈÉÊË]", "E");
        value = value.replaceAll("[ÙÚÛÜ]", "U");
        value = value.replaceAll("[ÌÍÎÏ]", "I");
        value = value.replaceAll("[ÀÁÂÄ]", "A");
        value = value.replaceAll("[ÒÓÔÖ]", "O");

        return value;
    }

    public void parseGender(String value) {
        if (value == null) {
            return;
        }
        this.gender = "U";
        value = value.trim();
        if (value.trim().toLowerCase().equals("m")) {
            this.gender = "M";
        }
        if (value.trim().toLowerCase().equals("f")) {
            this.gender = "F";
        }
        if (value.trim().toLowerCase().equals("v")) {
            this.gender = "F";
        }
        if (value.trim().toLowerCase().equals("man")) {
            this.gender = "M";
        }
        if (value.trim().toLowerCase().equals("vrouw")) {
            this.gender = "F";
        }
        if (value.trim().toLowerCase().equals("meneer")) {
            this.gender = "M";
        }
        if (value.trim().toLowerCase().equals("mevrouw")) {
            this.gender = "F";
        }
        if (value.trim().toLowerCase().equals("mannelijk")) {
            this.gender = "M";
        }
        if (value.trim().toLowerCase().equals("vrouwelijk")) {
            this.gender = "F";
        }
        if (value.trim().toLowerCase().equals("male")) {
            this.gender = "M";
        }
        if (value.trim().toLowerCase().equals("female")) {
            this.gender = "F";
        }
    }

    public void parseName(String value) {
        if (value == null) {
            return;
        }
        boolean found = false;
        value = value.trim();
        for (String element : middlelist) {
            if (value.toLowerCase().startsWith(element.concat(" "))) {
                this.middle = element;
                this.surname = value.substring(element.length() + " ".length()).trim();
                found = true;
            }
        }
        if (!found) {
            this.middle = "";
            this.surname = value;
        }
    }

    public void parseInitials(String value) {
        if (value == null) {
            return;
        }
        this.initials = "";
        value = value.replaceAll(" ", "");
        value = value.replaceAll(",", "");
        value = value.replaceAll("\\.", "");
        value = value.trim();
        for (int i = 0; i < value.length(); i++) {
            this.initials = this.initials.concat(String.valueOf(value.charAt(i)));
            this.initials = this.initials.concat(".");
        }
        this.initials = this.initials.toUpperCase();
    }

    public void parseForenames(String value) {
        if (value == null) {
            return;
        } else {
            this.forenames = value;
        }
    }

    public void parseMiddle(String value) {
        value = value.toLowerCase().trim();
        for (String m : middlelist) {
            if (value.equals(m)) {
                this.middle = value;
                return;
            }
        }
    }

    public void parseSurname(String value) {
        if (value == null) {
            return;
        } else {
            if (value.length() > 1) {
                this.surname = value.substring(0, 1).toUpperCase().concat(value.substring(1).toLowerCase());
            } else {
                this.surname = value.toUpperCase();
            }
        }
    }

    public void parseBirth(String value, SimpleDateFormat simpledateformat) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        try {
            // TODO: Validate date of birth to exclude obvious wrong dates like 01-07-1077 and such!!!
            //simpledateformat.toPattern();
            Date date = simpledateformat.parse(value);
            this.birth = date;
        } catch (ParseException pe) {
            Logger.getLogger(Person.class.getName()).log(Level.SEVERE, pe.getMessage(), pe);
        } catch (Exception e) {
            Logger.getLogger(Person.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void parseBirth(String value, int year_index_begin, int year_index_end, int month_index_begin, int month_index_end, int day_index_begin, int day_index_end) {
        if (value == null) {
            return;
        }
        value = value.trim();
        try {
            if (value.length() == "2011-04-07".length()) {
                String yearvalue = value.substring(year_index_begin, year_index_end + 1);
                String monthvalue = value.substring(month_index_begin, month_index_end + 1);
                String dayvalue = value.substring(day_index_begin, day_index_end + 1);
                Calendar calendar = Calendar.getInstance();
                TimeZone timezone = TimeZone.getDefault();
                calendar.setTimeZone(timezone);
                if (isInteger(yearvalue) && isInteger(monthvalue) && isInteger(yearvalue)) {
                    calendar.set(Calendar.YEAR, Integer.valueOf(yearvalue));
                    calendar.set(Calendar.MONTH, Integer.valueOf(monthvalue) - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dayvalue));
                    calendar.set(Calendar.HOUR, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    this.birth = calendar.getTime();
                } else {
                    this.birth = null;
                }
            } else {
                this.birth = null;
            }
        } catch (Exception e) {
            Logger.getLogger(Person.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public String getFormattedInitials() {
        if (this.initials == null) {
            return "";
        }
        String formatted = "";
        for (int i = 0; i < this.initials.length(); i++) {
            if (this.initials.charAt(i) != '.') {
                formatted = formatted.concat(String.valueOf(this.initials.charAt(i)));
                formatted = formatted.concat(".");
            }
        }
        return formatted.toUpperCase();
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAcceptableMatch() {
        if (this == null) {
            return false;
        }
        switch (this.status) {
            case STATUS_MATCHED_ID:
                return true;
            case Person.STATUS_MATCHED_NAME:
                return false;
            case Person.STATUS_MATCHED_NAME_DATEOFBIRTH:
                return true;
            case Person.STATUS_MATCHED_NAME_DATEOFBIRTH_GENDER:
                return true;
            case Person.STATUS_MATCHED_NAME_GENDER:
                return false;
            case Person.STATUS_MATCHED_NONE:
                return false;
            default:
                return false;
        }
    }

    public boolean isNew() {
        if (this == null) {
            return false;
        }
        switch (this.status) {
            case STATUS_NEW:
                return true;
            default:
                return false;
        }
    }

    public String getSQLServerFormattedBirth() {
        String formattedbirth = getFormattedBirth();
        return formattedbirth.concat(" 00:00:00.000");
    }

    public String getFormattedBirth() {
        return DateUtil.formattedDate(this.birth);
    }

    /*
     * private String formattedSQLServerDate(Date date) { String formatteddate =
     * DateUtil.formattedDate(date); return formatteddate.concat("
     * 00:00:00.000"); }
     *
     */
    public void mapPropertyValue(Properties mapping, SimpleDateFormat simpledateformat) {
        parseBirth((mapping.getProperty("person_birth") == null) ? "" : mapping.getProperty("person_birth"), simpledateformat);
        parseForenames((mapping.getProperty("person_forenames") == null) ? "" : mapping.getProperty("person_forenames"));
        parseGender((mapping.getProperty("person_gender") == null) ? "" : properGender(mapping.getProperty("person_gender")));
        parseInitials((mapping.getProperty("person_initials") == null) ? "" : mapping.getProperty("person_initials"));
        parseMiddle((mapping.getProperty("person_middle") == null) ? "" : mapping.getProperty("person_middle"));
        parseSurname((mapping.getProperty("person_surname") == null) ? "" : mapping.getProperty("person_surname"));
    }

    public void mapPropertyValue(Properties mapping, DateFormat dateformat) {
        parseBirth((mapping.getProperty("person_birth") == null) ? "" : mapping.getProperty("person_birth"), dateformat.getYearindexbegin(), dateformat.getYearindexend(), dateformat.getMonthindexbegin(), dateformat.getMonthindexend(), dateformat.getDayindexbegin(), dateformat.getDayindexend());
        parseForenames((mapping.getProperty("person_forenames") == null) ? "" : mapping.getProperty("person_forenames"));
        parseGender((mapping.getProperty("person_gender") == null) ? "" : mapping.getProperty("person_gender"));
        parseInitials((mapping.getProperty("person_initials") == null) ? "" : mapping.getProperty("person_initials"));
        parseMiddle((mapping.getProperty("person_middle") == null) ? "" : mapping.getProperty("person_middle"));
        parseSurname((mapping.getProperty("person_surname") == null) ? "" : mapping.getProperty("person_surname"));
    }

    private String properGender(String input) {
        if (input.equals("m") || input.equals("f") || input.equals("u")) {
            return input;
        }
        // We maken het niet al te ingewikkeld. "Voet" wordt f(email) en "Mus" wordt m(ale), etc.
        if (input.toLowerCase().startsWith("v") || input.toLowerCase().startsWith("f")) {
            return "f";
        }
        if (input.toLowerCase().startsWith("m")) {
            return "m";
        }
        return "u";
    }
}
