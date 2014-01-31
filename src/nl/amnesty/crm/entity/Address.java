/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ed
 */
public class Address {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    public final static int STATUS_MATCHED_ID = 3;
    public final static int STATUS_MATCHED_STREET_HOUSENUMBER_CITY = 4;
    public final static int STATUS_MATCHED_POSTALCODE_HOUSENUMBER = 5;
    public final static int STATUS_OLD = 6;
    public final static String POSTALCODE_DELIMITER = " ";
    public final static String ISO_COUNTRYCODE_NETHERLANDS = "NLD";
    public final static String FIELD_ADDRESS_ID = "ADDRESSKEY";
    public final static String FIELD_ADDRESS_STREET = "ADDRESS1";
    public final static String FIELD_ADDRESS_STREET_INDEXED = "SHADD1";
    public final static String FIELD_ADDRESS_HOUSENO = "HOUSENO";
    public final static String FIELD_ADDRESS_HOUSENOSUFFIX = "HOUSENOSUFFIX";
    public final static String FIELD_ADDRESS_POSTCODE = "POSTCODE";
    public final static String FIELD_ADDRESS_CITY = "ADDRESS4";
    public final static String FIELD_ADDRESS_CITY_INDEXED = "SHADD4";
    public final static String FIELD_ADDRESS_COUNTRY = "ADDRESS6";
    public final static String FIELD_ADDRESS_COMPANY = "COMPANY";
    //
    public final static String FIELD_ADDRESS_COMPANY_PREFIX_WERELDWINKEL = "Wereldwinkel";
    //
    private final static String HOUSENOSUFFIX_DELIMITER = "-";
    private long addressid;
    private String street;
    private int houseno;
    private String housenosuffix;
    private int postalcodenumeric;
    private String postalcodealpha;
    private String city;
    private String county;
    private String province;
    private String state;
    private String isocountry;
    private int status;

    public Address() {
        this.addressid = 0;
        this.street = "";
        this.houseno = 0;
        this.housenosuffix = "";
        this.postalcodenumeric = 0;
        this.postalcodealpha = "";
        this.city = "";
        this.county = "";
        this.province = "";
        this.state = "";
        this.isocountry = "";
        this.status = STATUS_NEW;
    }

    public Address(long addressid, String street, int houseno, String housenosuffix, int postalcodenumeric, String postalcodealpha, String city, String county, String province, String state, String isocountry) {
        this.addressid = addressid;
        this.street = street;
        this.houseno = houseno;
        this.housenosuffix = housenosuffix;
        this.postalcodenumeric = postalcodenumeric;
        this.postalcodealpha = postalcodealpha;
        this.city = city;
        this.county = county;
        this.province = province;
        this.state = state;
        this.isocountry = isocountry;
        this.status = STATUS_NEW;
    }

    public long getAddressid() {
        return addressid;
    }

    public void setAddressid(long addressid) {
        this.addressid = addressid;
    }

    public String getCity() {
        if (city == null) {
            return "";
        } else {
            return city;
        }
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        if (county == null) {
            return "";
        } else {
            return county;
        }
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public int getHouseno() {
        return houseno;
    }

    public void setHouseno(int houseno) {
        this.houseno = houseno;
    }

    public String getHousenosuffix() {
        if (housenosuffix == null) {
            return "";
        } else {
            return housenosuffix;
        }
    }

    public void setHousenosuffix(String housenosuffix) {
        this.housenosuffix = housenosuffix;
    }

    public String getIsocountry() {
        if (isocountry == null) {
            return "";
        } else {
            return isocountry;
        }
    }

    public void setIsocountry(String isocountry) {
        this.isocountry = isocountry;
    }

    public String getPostalcodealpha() {
        if (postalcodealpha == null) {
            return "";
        } else {
            return postalcodealpha;
        }
    }

    public void setPostalcodealpha(String postalcodealpha) {
        this.postalcodealpha = postalcodealpha;
    }

    public int getPostalcodenumeric() {
        return postalcodenumeric;
    }

    public void setPostalcodenumeric(int postalcodenumeric) {
        this.postalcodenumeric = postalcodenumeric;
    }

    public String getProvince() {
        if (province == null) {
            return "";
        } else {
            return province;
        }
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getState() {
        if (state == null) {
            return "";
        } else {
            return state;
        }
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFormattedStreetHouseno() {
        String value = "";
        if (this.street == null) {
            return "";
        }
        if (this.street.length() > 1) {
            value = this.street.substring(0, 1).toUpperCase();
            value = value.concat(this.street.substring(1).toLowerCase());
        } else {
            value = this.street.toUpperCase();
        }
        value = value.concat(" ").concat(String.valueOf(this.houseno));
        if (this.housenosuffix == null) {
            return value.trim();
        } else {
            value = value.concat(" ").concat(this.housenosuffix.toLowerCase());
            return value.trim();
        }
    }

    public String getStreet() {
        if (street == null) {
            return "";
        } else {
            return street;
        }
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFormatedAddress() {
        if (street == null) {
            street = "";
        }
        if (housenosuffix == null) {
            housenosuffix = "";
        }
        if (city == null) {
            city = "";
        }
        String address = "";
        address = street.concat(" ").concat(String.valueOf(houseno));
        if (!housenosuffix.isEmpty()) {
            address = address.concat(" ").concat(housenosuffix);
        }
        address = address.concat(" ").concat(city);
        return address;
    }

    public String getFormattedPostalcode() {
        String value = "";
        value = String.valueOf(this.postalcodenumeric);
        value = value.concat(" ").concat(this.postalcodealpha.toUpperCase());
        return value;
    }

    // /^([1-9][e][\s])*([a-zA-Z]+(([\.][\s])|([\s]))?)+[1-9][0-9]*(([-][1-9][0-9]*)|([\s]?[a-zA-Z]+))?$/i
    public void parseStreet(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        //TODO: The following regex code does to seem to do anything...
        Pattern p = Pattern.compile("([1-9][e][\\s])*([a-zA-Z]+(([\\.][\\s])|([\\s]))?)+[1-9][0-9]*(([-][1-9][0-9]*)|([\\s]?[a-zA-Z]+))?$");
        Matcher m = p.matcher(value);
        boolean result = m.find();
        while (result) {
            result = m.find();
        }

        if (value.length() > 1) {
            this.street = value.substring(0, 1).toUpperCase().concat(value.substring(1).toLowerCase());

            //DEBUG
            //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parseStreet(): set street to {0}", this.street);
        }
    }

    public void parseHouseno(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        int index = 0;
        String housenovalue = "";
        String housenosuffixvalue = "";
        boolean foundhouseno = false;
        try {
            for (index = 0; index < value.length(); index++) {
                String character = String.valueOf(value.charAt(index));
                if (foundhouseno) {
                    housenosuffixvalue = housenosuffixvalue.concat(character);
                } else {
                    if (isInteger(housenovalue.concat(character))) {
                        housenovalue = housenovalue.concat(character);
                    } else {
                        housenosuffixvalue = character;
                        foundhouseno = true;
                    }
                }
            }
            this.houseno = Integer.valueOf(housenovalue);

            //DEBUG
            //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parseHouseno(): set houseno to {0}", this.houseno);

            housenosuffixvalue = removeSpaces(housenosuffixvalue);
            housenosuffixvalue = removeHyphens(housenosuffixvalue);
            if (!this.housenosuffix.isEmpty()) {
                this.housenosuffix = HOUSENOSUFFIX_DELIMITER.concat(housenosuffixvalue.toUpperCase());

                //DEBUG
                //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parseHouseno(): set housenosuffix to {0}", this.housenosuffix);
            }
        } catch (Exception e) {
            Logger.getLogger(Address.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void parseHousenosuffix(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        this.housenosuffix = HOUSENOSUFFIX_DELIMITER.concat(value.toUpperCase().trim());

        //DEBUG
        //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parseHousenosuffix(): set housenosuffix to {0}", this.housenosuffix);
    }

    public void parseStreetHouseno(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        boolean found_houseno = false;
        this.street = "";
        this.houseno = 0;
        this.housenosuffix = "";
        try {
            String trim = value.toLowerCase().trim();
            String[] tokenlist = trim.split("\\s");
            for (String token : tokenlist) {
                if (found_houseno) {
                    this.housenosuffix = this.housenosuffix.concat(token.concat(" "));
                } else {
                    if (isInteger(token)) {
                        this.houseno = Integer.parseInt(token);

                        //DEBUG
                        //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parseStreetHouseno(): set houseno to {0}", this.houseno);

                        found_houseno = true;
                    } else {
                        if (token.length() > 1) {
                            // Propercase
                            token = token.substring(0, 1).toUpperCase();
                            token = token.concat(token.substring(1));
                        } else {
                            token = token.toUpperCase();
                        }
                        this.street = this.street.concat(token.concat(" "));
                    }
                }
            }
            this.street = this.street.trim();

            //DEBUG
            //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parseStreetHouseno(): set street to {0}", this.street);

            if (!this.housenosuffix.isEmpty()) {
                this.housenosuffix = HOUSENOSUFFIX_DELIMITER.concat(this.housenosuffix.toUpperCase().trim());

                //DEBUG
                //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parseStreetHouseno(): set housenosuffix to {0}", this.housenosuffix);
            }
        } catch (Exception e) {
            Logger.getLogger(Address.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    // Regex: ^[0-9]{4}\s*[a-z|A-Z]{2}$
    // /^[1-9][0-9]{3}[\s]?[A-Za-z]{2}$/i
    public void parsePostalcode(String value) {
        if (value == null) {
            return;
        }
        if (value.length() < 6) {
            return;
        }
        this.postalcodealpha = "";
        this.postalcodenumeric = 0;
        try {
            String codenumeric = value.substring(0, 4);
            if (isInteger(codenumeric)) {
                this.postalcodenumeric = Integer.parseInt(codenumeric);
                this.postalcodealpha = value.substring(4).trim().toUpperCase();

                //DEBUG
                //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parsePostalcode(): set postalcodenumeric to {0}", this.postalcodenumeric);
                //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parsePostalcode(): set postalcodealpha to {0}", this.postalcodealpha);
            } else {
                this.postalcodealpha = value.trim().toUpperCase();

                //DEBUG
                //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parsePostalcode(): set postalcodealpha to {0}", this.postalcodealpha);
            }
        } catch (Exception e) {
            Logger.getLogger(Address.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void parsePostalcodeAlpha(String value) {
        if (value == null) {
            return;
        }
        if (value.trim().length() != 2) {
            return;
        }
        try {
            this.postalcodealpha = value.trim().toUpperCase();

            //DEBUG
            //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parsePostalcodeAlpha(): set postalcodealpha to {0}", this.postalcodealpha);
        } catch (Exception e) {
            Logger.getLogger(Address.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void parsePostalcodeNumeric(String value) {
        if (value == null) {
            return;
        }
        if (value.trim().length() != 4) {
            return;
        }
        this.postalcodenumeric = 0;
        try {
            if (isInteger(value)) {
                this.postalcodenumeric = Integer.parseInt(value);
                
                //DEBUG
                //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parsePostalcodeNumeric(): set postalcodenumeric to {0}", this.postalcodenumeric);
            }
        } catch (Exception e) {
            Logger.getLogger(Address.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public String getFormattedCity() {
        return this.city.toUpperCase();
    }

    // /^(([2][e][[:space:]]|['][ts][-[:space:]]))?[ëéÉËa-zA-Z]{2,}((\s|[-](\s)?)[ëéÉËa-zA-Z]{2,})*$/i
    public void parseCity(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        String token = "";
        this.city = "";
        String trim = value.toLowerCase().trim();
        StringTokenizer stringtokenizer = new StringTokenizer(trim);
        while (stringtokenizer.hasMoreTokens()) {
            token = stringtokenizer.nextToken();
            if (token.length() >= 4) {
                String codenumeric = token.substring(0, 4);
                if (isInteger(codenumeric)) {
                    parsePostalcode(token);
                } else {
                    this.city = this.city.concat(token.concat(" "));
                }
            } else {
                this.city = this.city.concat(token.concat(" "));
            }
        }
        this.city = this.city.trim();
        this.city = this.city.toUpperCase();
        
        //DEBUG
        //Logger.getLogger(Address.class.getName()).log(Level.INFO, "parseCity(): set city to {0}", this.city);
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
                // Address is matched via id, so we are pretty sure we have got the right address.
                return true;
            case STATUS_MATCHED_POSTALCODE_HOUSENUMBER:
                // Address matched via postalcode and housenumber, appendix could be different but enough certainty for now...
                return true;
            case STATUS_MATCHED_STREET_HOUSENUMBER_CITY:
                // Address matched via street, housenumber and city, appendix could be different but enough certainty for now...
                return true;
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

    public String formatPostalcode(int postalcodenumeric, String postalcodealpha) {
        String postalcode = "";
        if (postalcodenumeric >= 1000 && postalcodenumeric < 10000) {
            if (postalcodealpha.length() == 2) {
                postalcode = String.valueOf(postalcodenumeric).concat(POSTALCODE_DELIMITER).concat(postalcodealpha.toUpperCase());
            }
        }
        return postalcode;
    }

    public String removeSpaces(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, " ", false);
        String nospaces = "";
        while (tokenizer.hasMoreElements()) {
            nospaces = nospaces.concat(tokenizer.nextToken());
        }
        return nospaces;
    }

    public String removeHyphens(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, "-", false);
        String nohyphens = "";
        while (tokenizer.hasMoreElements()) {
            nohyphens = nohyphens.concat(tokenizer.nextToken());
        }
        return nohyphens;
    }

    public String removeSinglequotes(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, "'", false);
        String noquotes = "";
        while (tokenizer.hasMoreElements()) {
            noquotes = noquotes.concat(tokenizer.nextToken());
        }
        return noquotes;
    }

    public String removeDoublequotes(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, "\"", false);
        String noquotes = "";
        while (tokenizer.hasMoreElements()) {
            noquotes = noquotes.concat(tokenizer.nextToken());
        }
        return noquotes;
    }

    public boolean isDecentAddress() {
        if (this.city == null) {
            return false;
        }
        if (this.city.isEmpty()) {
            return false;
        }
        if (this.houseno == 0) {
            return false;
        }
        if (this.postalcodealpha == null) {
            return false;
        }
        if (this.postalcodealpha.isEmpty()) {
            return false;
        }
        if (this.postalcodenumeric == 0) {
            return false;
        }
        if (this.street == null) {
            return false;
        }
        if (this.street.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isEqual(Address otheraddress) {
        if (this.postalcodenumeric == otheraddress.postalcodenumeric) {
            if (this.postalcodealpha.equals(otheraddress.postalcodealpha)) {
                if (this.houseno == otheraddress.getHouseno()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void mapPropertyValue(Properties mapping) {
        parseCity((mapping.getProperty("address_city") == null) ? "" : mapping.getProperty("address_city"));
        parseHouseno((mapping.getProperty("address_houseno") == null) ? "" : mapping.getProperty("address_houseno"));
        parseHousenosuffix((mapping.getProperty("address_housenosuffix") == null) ? "" : mapping.getProperty("address_housenosuffix"));
        parsePostalcode((mapping.getProperty("address_postalcode") == null) ? "" : mapping.getProperty("address_postalcode"));
        parsePostalcodeAlpha((mapping.getProperty("address_postalcodealpha") == null) ? "" : mapping.getProperty("address_postalcodealpha"));
        parsePostalcodeNumeric((mapping.getProperty("address_postalcodenumeric") == null) ? "" : mapping.getProperty("address_postalcodenumeric"));
        parseStreet((mapping.getProperty("address_street") == null) ? "" : mapping.getProperty("address_street"));
    }

    public void mapPropertyValueOld(Properties mapping) {
        parseCity((mapping.getProperty("address_city_old") == null) ? "" : mapping.getProperty("address_city_old"));
        parseHouseno((mapping.getProperty("address_houseno_old") == null) ? "" : mapping.getProperty("address_houseno_old"));
        parseHousenosuffix((mapping.getProperty("address_housenosuffix_old") == null) ? "" : mapping.getProperty("address_housenosuffix_old"));
        parsePostalcode((mapping.getProperty("address_postalcode_old") == null) ? "" : mapping.getProperty("address_postalcode_old"));
        parsePostalcodeAlpha((mapping.getProperty("address_postalcodealpha_old") == null) ? "" : mapping.getProperty("address_postalcodealpha_old"));
        parsePostalcodeNumeric((mapping.getProperty("address_postalcodenumeric_old") == null) ? "" : mapping.getProperty("address_postalcodenumeric_old"));
        parseStreet((mapping.getProperty("address_street_old") == null) ? "" : mapping.getProperty("address_street_old"));
    }

    public void mapPropertyValueNew(Properties mapping) {
        parseCity((mapping.getProperty("address_city_new") == null) ? "" : mapping.getProperty("address_city_new"));
        parseHouseno((mapping.getProperty("address_houseno_new") == null) ? "" : mapping.getProperty("address_houseno_new"));
        parseHousenosuffix((mapping.getProperty("address_housenosuffix_new") == null) ? "" : mapping.getProperty("address_housenosuffix_new"));
        parsePostalcode((mapping.getProperty("address_postalcode_new") == null) ? "" : mapping.getProperty("address_postalcode_new"));
        parsePostalcodeAlpha((mapping.getProperty("address_postalcodealpha_new") == null) ? "" : mapping.getProperty("address_postalcodealpha_new"));
        parsePostalcodeNumeric((mapping.getProperty("address_postalcodenumeric_new") == null) ? "" : mapping.getProperty("address_postalcodenumeric_new"));
        parseStreet((mapping.getProperty("address_street_new") == null) ? "" : mapping.getProperty("address_street_new"));
    }
}
