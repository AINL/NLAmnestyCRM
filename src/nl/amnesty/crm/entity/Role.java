/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.text.SimpleDateFormat;
import java.util.*;
import nl.amnesty.crm.config.DateFormat;

/**
 *
 * @author ed
 */
public class Role {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    public final static int STATUS_MATCHED_ID = 3;
    public final static int STATUS_MATCHED_PERSON = 4;
    public final static int STATUS_MATCHED_ADDRESS = 5;
    public final static int STATUS_MATCHED_PERSON_ADDRESS = 6;
    public final static int STATUS_MATCHED_PERSON_PHONE = 7;
    public final static int STATUS_MATCHED_PERSON_URL = 8;
    public final static int STATUS_MATCHED_PERSON_BANKACCOUNT = 9;
    public final static int STATUS_MATCHED_ADDRESS_PHONE = 10;
    public final static int STATUS_MATCHED_ADDRESS_URL = 11;
    public final static int STATUS_MATCHED_ADDRESS_BANKACCOUNT = 12;
    public final static int STATUS_MATCHED_PHONE = 13;
    public final static int STATUS_MATCHED_URL = 14;
    //
    private final static String MESSAGE_LEVEL_INFO = "INFO:    ";
    private final static String MESSAGE_LEVEL_WARNING = "WARNING: ";
    private final static String MESSAGE_LEVEL_SEVERE = "SEVERE:  ";
    /*
     * CRM Data Structure
     */
    public final static String RECTYPE_INDIVIDUAL = "PERSOON";
    public final static String RECTYPE_ORGANIZATION = "ORG";
    public final static String RECTYPE_AI_GROUP = "AI GROEP";
    //
    public final static String FIELD_CONTACT_ROLEID = "PVKEY";
    public final static String FIELD_CONTACT_PERSONID = "PEOPLEKEY";
    public final static String FIELD_CONTACT_ADDRESSID = "ADDRESSKEY";
    public final static String FIELD_CONTACT_RECTYPE = "RECTYPE";
    public final static String FIELD_CONTACT_STARTDATE = "DATEADDED";
    public final static String FIELD_CONTACT_SOURCE = "SOURCE";
    public final static String FIELD_CONTACT_ADDTYPEKEY = "ADDTYPEKEY";
    public final static String FIELD_CONTACT_PHONE = "PHONENO";
    //
    public final static String FIELD_CONTACT_RNUMB_1 = "RNUMB_1";
    public final static String FIELD_CONTACT_RNUMB_2 = "RNUMB_2";
    public final static String FIELD_CONTACT_RNUMB_3 = "RNUMB_3";
    public final static String FIELD_CONTACT_RNUMB_4 = "RNUMB_4";
    public final static String FIELD_CONTACT_RNUMB_5 = "RNUMB_5";
    public final static String FIELD_CONTACT_RNUMB_6 = "RNUMB_6";
    public final static String FIELD_CONTACT_RNUMB_7 = "RNUMB_7";
    public final static String FIELD_CONTACT_RNUMB_8 = "RNUMB_8";
    public final static String FIELD_CONTACT_RNUMB_9 = "RNUMB_9";
    public final static String FIELD_CONTACT_RNUMB_10 = "RNUMB_10";
    //
    public final static String FIELD_CONTACT_UNUMB_1 = "UNUMB_1";
    public final static String FIELD_CONTACT_UNUMB_2 = "UNUMB_2";
    public final static String FIELD_CONTACT_UNUMB_3 = "UNUMB_3";
    public final static String FIELD_CONTACT_UNUMB_4 = "UNUMB_4";
    public final static String FIELD_CONTACT_UNUMB_5 = "UNUMB_5";
    public final static String FIELD_CONTACT_UNUMB_6 = "UNUMB_6";
    public final static String FIELD_CONTACT_UNUMB_7 = "UNUMB_7";
    public final static String FIELD_CONTACT_UNUMB_8 = "UNUMB_8";
    public final static String FIELD_CONTACT_UNUMB_9 = "UNUMB_9";
    public final static String FIELD_CONTACT_UNUMB_10 = "UNUMB_10";
    //
    public final static String FIELD_CONTACT_RLOGIC_1 = "RLOGIC_1";
    public final static String FIELD_CONTACT_RLOGIC_2 = "RLOGIC_2";
    public final static String FIELD_CONTACT_RLOGIC_3 = "RLOGIC_3";
    public final static String FIELD_CONTACT_RLOGIC_4 = "RLOGIC_4";
    public final static String FIELD_CONTACT_RLOGIC_5 = "RLOGIC_5";
    public final static String FIELD_CONTACT_RLOGIC_6 = "RLOGIC_6";
    public final static String FIELD_CONTACT_RLOGIC_7 = "RLOGIC_7";
    public final static String FIELD_CONTACT_RLOGIC_8 = "RLOGIC_8";
    public final static String FIELD_CONTACT_RLOGIC_9 = "RLOGIC_9";
    public final static String FIELD_CONTACT_RLOGIC_10 = "RLOGIC_10";
    //
    public final static String FIELD_CONTACT_ULOGIC_1 = "ULOGIC_1";
    public final static String FIELD_CONTACT_ULOGIC_2 = "ULOGIC_2";
    public final static String FIELD_CONTACT_ULOGIC_3 = "ULOGIC_3";
    public final static String FIELD_CONTACT_ULOGIC_4 = "ULOGIC_4";
    public final static String FIELD_CONTACT_ULOGIC_5 = "ULOGIC_5";
    public final static String FIELD_CONTACT_ULOGIC_6 = "ULOGIC_6";
    public final static String FIELD_CONTACT_ULOGIC_7 = "ULOGIC_7";
    public final static String FIELD_CONTACT_ULOGIC_8 = "ULOGIC_8";
    public final static String FIELD_CONTACT_ULOGIC_9 = "ULOGIC_9";
    public final static String FIELD_CONTACT_ULOGIC_10 = "ULOGIC_10";
    //
    public final static String FIELD_CONTACT_RDATE_1 = "RDATE_1";
    public final static String FIELD_CONTACT_RDATE_2 = "RDATE_2";
    public final static String FIELD_CONTACT_RDATE_3 = "RDATE_3";
    public final static String FIELD_CONTACT_RDATE_4 = "RDATE_4";
    public final static String FIELD_CONTACT_RDATE_5 = "RDATE_5";
    public final static String FIELD_CONTACT_RDATE_6 = "RDATE_6";
    public final static String FIELD_CONTACT_RDATE_7 = "RDATE_7";
    public final static String FIELD_CONTACT_RDATE_8 = "RDATE_8";
    public final static String FIELD_CONTACT_RDATE_9 = "RDATE_9";
    public final static String FIELD_CONTACT_RDATE_10 = "RDATE_10";
    //
    public final static String FIELD_CONTACT_UDATE_1 = "UDATE_1";
    public final static String FIELD_CONTACT_UDATE_2 = "UDATE_2";
    public final static String FIELD_CONTACT_UDATE_3 = "UDATE_3";
    public final static String FIELD_CONTACT_UDATE_4 = "UDATE_4";
    public final static String FIELD_CONTACT_UDATE_5 = "UDATE_5";
    public final static String FIELD_CONTACT_UDATE_6 = "UDATE_6";
    public final static String FIELD_CONTACT_UDATE_7 = "UDATE_7";
    public final static String FIELD_CONTACT_UDATE_8 = "UDATE_8";
    public final static String FIELD_CONTACT_UDATE_9 = "UDATE_9";
    public final static String FIELD_CONTACT_UDATE_10 = "UDATE_10";
    //
    public final static String FIELD_CONTACT_RALPHA_1 = "RALPHA_1";
    public final static String FIELD_CONTACT_RALPHA_2 = "RALPHA_2";
    public final static String FIELD_CONTACT_RALPHA_3 = "RALPHA_3";
    public final static String FIELD_CONTACT_RALPHA_4 = "RALPHA_4";
    public final static String FIELD_CONTACT_RALPHA_5 = "RALPHA_5";
    public final static String FIELD_CONTACT_RALPHA_6 = "RALPHA_6";
    public final static String FIELD_CONTACT_RALPHA_7 = "RALPHA_7";
    public final static String FIELD_CONTACT_RALPHA_8 = "RALPHA_8";
    public final static String FIELD_CONTACT_RALPHA_9 = "RALPHA_9";
    public final static String FIELD_CONTACT_RALPHA_10 = "RALPHA_10";
    //
    public final static String FIELD_CONTACT_UALPHA_1 = "UALPHA_1";
    public final static String FIELD_CONTACT_UALPHA_2 = "UALPHA_2";
    public final static String FIELD_CONTACT_UALPHA_3 = "UALPHA_3";
    public final static String FIELD_CONTACT_UALPHA_4 = "UALPHA_4";
    public final static String FIELD_CONTACT_UALPHA_5 = "UALPHA_5";
    public final static String FIELD_CONTACT_UALPHA_6 = "UALPHA_6";
    public final static String FIELD_CONTACT_UALPHA_7 = "UALPHA_7";
    public final static String FIELD_CONTACT_UALPHA_8 = "UALPHA_8";
    public final static String FIELD_CONTACT_UALPHA_9 = "UALPHA_9";
    public final static String FIELD_CONTACT_UALPHA_10 = "UALPHA_10";
    //
    public final static String FIELD_CONTACT_EMAIL_OK = "EMAILOK";
    public final static String FIELD_CONTACT_MAIL_OK = "MAILTO";
    public final static String FIELD_CONTACT_NO_AC = "NOACK";
    public final static String FIELD_CONTACT_PHONE_OK = "PHONE";
    public final static String FIELD_CONTACT_RECP_OK = "RECPOK";
    public final static String FIELD_CONTACT_SMS_OK = "SMSOK";
    private long roleid;
    private String source;
    private Person person;
    private Address address;
    private Organization organization;
    private List<Phone> phonelist;
    private List<URL> urllist;
    private List<Bankaccount> bankaccountlist;
    private int status;

    public Role() {
        this.source = "";
        this.person = new Person();
        this.address = new Address();
        this.organization=new Organization();
        this.phonelist = new ArrayList();
        this.urllist = new ArrayList();
        this.bankaccountlist = new ArrayList();
        this.status = STATUS_NEW;
    }

    public Role(long roleid, String source, Person person, Address address, List<Phone> phonelist, List<URL> urllist, List<Bankaccount> bankaccountlist) {
        this.roleid = roleid;
        this.source = source;
        this.person = person;
        this.address = address;
        this.phonelist = phonelist;
        this.urllist = urllist;
        this.bankaccountlist = bankaccountlist;
    }
    
    public Role(long roleid, String source, Person person, Address address, Organization organization, List<Phone> phonelist, List<URL> urllist, List<Bankaccount> bankaccountlist) {
        this.roleid = roleid;
        this.source = source;
        this.person = person;
        this.address = address;
        this.organization=organization;
        this.phonelist = phonelist;
        this.urllist = urllist;
        this.bankaccountlist = bankaccountlist;
    }

    public Role(long roleid, String source, Person person, Address address, Phone phone, URL url, Bankaccount bankaccount) {
        this.roleid = roleid;
        this.source = source;
        this.person = person;
        this.address = address;
        this.phonelist = new ArrayList();
        if (phone != null) {
            if (phone.getNumber() != 0) {
                this.phonelist.add(phone);
            }
        }
        this.urllist = new ArrayList();
        if (url != null) {
            if (!url.getInternetAddress().isEmpty()) {
                this.urllist.add(url);
            }
        }
        this.bankaccountlist = new ArrayList();
        if (bankaccount != null) {
            if (bankaccount.getNumber() != 0) {
                this.bankaccountlist.add(bankaccount);
            }
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<Phone> getPhonelist() {
        return phonelist;
    }

    public void setPhonelist(List<Phone> phonelist) {
        this.phonelist = phonelist;
    }

    public long getRoleid() {
        return roleid;
    }

    public void setRoleid(long roleid) {
        this.roleid = roleid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<URL> getUrllist() {
        return urllist;
    }

    public void setUrllist(List<URL> urllist) {
        this.urllist = urllist;
    }

    public List<Bankaccount> getBankaccountlist() {
        return bankaccountlist;
    }

    public void setBankaccountlist(List<Bankaccount> bankaccountlist) {
        this.bankaccountlist = bankaccountlist;
    }

    public String getFormattedSalutation() {
        if (this.person != null) {
            return this.person.getFormattedSalutation();
        } else {
            return "";
        }
    }

    public String getFormattedName() {
        if (this.person != null) {
            return this.person.getFormattedName();
        } else {
            return "";
        }
    }

    public String getFormattedRoleid() {
        return String.valueOf(roleid);
    }

    public String getFormattedFormalName() {
        if (this.person != null) {
            return this.person.getFormattedFormalName();
        } else {
            return "";
        }
    }

    /*
     * public String getEmailAddress() { String emailhome = ""; String emailwork
     * = ""; if (urllist.size() > 0) { for (URL url : urllist) { if
     * (url.getType() == URL.TYPE_EMAIL_HOME_PRIMARY) { emailhome =
     * url.getUsername().concat("@").concat(url.getDomain()).toLowerCase().trim();
     * } if (url.getType() == URL.TYPE_EMAIL_WORK_PRIMARY) { emailwork =
     * url.getUsername().concat("@").concat(url.getDomain()).toLowerCase().trim();
     * } } } if (!emailhome.isEmpty()) { return emailhome; } else { if
     * (!emailwork.isEmpty()) { return emailwork; } } return ""; }
     *
     */
    public String getEmailinternetaddress() {
        if (getEmail() != null) {
            return getEmail().getInternetAddress();
        }
        return "";
    }

    public URL getEmail() {
        URL urlfound = null;
        Calendar mostrecent = Calendar.getInstance();
        TimeZone timezone = TimeZone.getDefault();
        mostrecent.setTimeZone(timezone);
        mostrecent.set(Calendar.YEAR, 1900);
        mostrecent.set(Calendar.MONTH, Calendar.JANUARY);
        mostrecent.set(Calendar.DAY_OF_MONTH, 1);
        for (URL url : urllist) {
            if (url.getDateadded() != null) {
                if (url.getDateadded().after(mostrecent.getTime())) {
                    urlfound = url;
                    mostrecent.setTime(url.getDateadded());
                }
            }
        }
        return urlfound;
    }

    public URL getEmail(String internetaddress) {
        if (this.urllist == null) {
            return null;
        }
        for (URL url : urllist) {
            if (url.getInternetAddress().equals(internetaddress)) {
                return url;
            }
        }
        return null;
    }

    public long getPhonenumber() {
        if (getPhone() != null) {
            return getPhone().getNumber();
        }
        return 0;
    }

    public Phone getPhone() {
        Phone phonehome = null;
        Phone phonemobile = null;
        if (phonelist == null) {
            return null;
        }
        if (phonelist.isEmpty()) {
            return null;
        }
        for (Phone phone : phonelist) {
            if (phone.getType() == Phone.PHONE_LANDLINE_PRIMARY) {
                phonehome = phone;
            }
            if (phone.getType() == Phone.PHONE_MOBILE_PRIMARY) {
                phonemobile = phone;
            }
        }
        if (phonehome != null) {
            return phonehome;
        } else {
            if (phonemobile != null) {
                return phonemobile;
            } else {
                return this.phonelist.get(0);
            }
        }
    }

    public Phone getHomePhone() {
        Phone phonehome = null;
        if (phonelist == null) {
            return null;
        }
        if (phonelist.isEmpty()) {
            return null;
        }
        for (Phone phone : phonelist) {
            if (phone.getType() == Phone.PHONE_LANDLINE_PRIMARY) {
                phonehome = phone;
            }
        }
        if (phonehome != null) {
            return phonehome;
        } else {
            for (Phone phone : phonelist) {
                if (phone.getType() == Phone.PHONE_LANDLINE_SECONDARY) {
                    phonehome = phone;
                }
            }
            if (phonehome != null) {
                return phonehome;
            } else {
                return null;
            }
        }
    }

    public Phone getMobilePhone() {
        Phone phonemobile = null;
        if (phonelist == null) {
            return null;
        }
        if (phonelist.isEmpty()) {
            return null;
        }
        for (Phone phone : phonelist) {
            if (phone.getType() == Phone.PHONE_MOBILE_PRIMARY) {
                phonemobile = phone;
            }
        }
        if (phonemobile != null) {
            return phonemobile;
        } else {
            for (Phone phone : phonelist) {
                if (phone.getType() == Phone.PHONE_MOBILE_SECONDARY) {
                    phonemobile = phone;
                }
            }
            if (phonemobile != null) {
                return phonemobile;
            } else {
                return null;
            }
        }
    }

    public Phone getPhone(long number) {
        if (this.phonelist != null) {
            for (Phone phone : phonelist) {
                if (phone.getNumber() == number) {
                    return phone;
                }
            }
        }
        return null;
    }

    public long getBankaccountnumber() {
        if (getBankaccount() != null) {
            return getBankaccount().getNumber();
        }
        return 0;
    }

    public Bankaccount getBankaccount() {
        if (bankaccountlist == null) {
            return null;
        }
        if (bankaccountlist.isEmpty()) {
            return null;
        }
        return bankaccountlist.get(0);
    }

    public Bankaccount getBankaccount(long number) {
        if (this.bankaccountlist != null) {
            for (Bankaccount bankaccount : this.bankaccountlist) {
                if (bankaccount.getNumber() == number) {
                    return bankaccount;
                }
            }
        }
        return null;
    }

    public String getFormattedPhonenumber() {
        Phone phone = getPhone();
        if (phone != null) {
            return phone.getFormattedNumber();
        } else {
            return "";
        }
    }

    public boolean hasEmail(String email) {
        boolean found = false;
        for (URL url : this.getUrllist()) {
            if (url.getInternetAddress() != null) {
                if (url.getInternetAddress().equals(email)) {
                    found = true;
                }
            }
        }
        return found;
    }

    public boolean hasPhone(long number) {
        boolean found = false;
        for (Phone phone : this.getPhonelist()) {
            if (phone.getNumber() != 0) {
                if (phone.getNumber() == number) {
                    found = true;
                }
            }
        }
        return found;
    }

    public boolean hasBankaccount(long number) {
        boolean found = false;
        for (Bankaccount bankaccount : this.getBankaccountlist()) {
            if (bankaccount.getNumber() != 0) {
                if (bankaccount.getNumber() == number) {
                    found = true;
                }
            }
        }
        return found;
    }

    public boolean hasDateofbirth() {
        if (this.person != null) {
            Date birth = this.person.getBirth();
            if (birth == null) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean isAcceptableMatch() {
        if (this == null) {
            return false;
        }
        switch (this.status) {
            case STATUS_MATCHED_ID:
                // Role is matched via id, so we are pretty sure we have got the right address.
                return true;
            case STATUS_MATCHED_PERSON:
                // Role is matched via person object so this must be the right one
                return true;
            case STATUS_MATCHED_ADDRESS:
                // Role is matched via address object, there may be more than one person living here
                return false;
            case STATUS_MATCHED_PERSON_ADDRESS:
                // Role is matched via a combination of a person and an address object so this must be the right one
                return true;
            case STATUS_MATCHED_PERSON_PHONE:
                // Role matched via combination of name and a matching name and phone number
                return true;
            case STATUS_MATCHED_PERSON_URL:
                // Role matched via combination of name and a matching e-mail address
                return true;
            case STATUS_MATCHED_PERSON_BANKACCOUNT:
                // Role matched via combination of name and a matching bankaccount number
                return true;
            case STATUS_MATCHED_ADDRESS_PHONE:
                // Role matched via combination of address and a matching name and phone number
                return true;
            case STATUS_MATCHED_ADDRESS_URL:
                // Role matched via combination of address and a matching e-mail address
                return true;
            case STATUS_MATCHED_ADDRESS_BANKACCOUNT:
                // Role matched via combination of address and a matching bankaccount number
                return true;
            case STATUS_MATCHED_PHONE:
                // Role matched via phone number
                return true;
            case STATUS_MATCHED_URL:
                // Role matched via e-mail address
                return true;
            default:
                return false;
        }
    }

    public void parseSource(String value) {
        if (value == null) {
            return;
        }
        this.source = value;
    }

    public void parseRoleid(String value) {
        if (value == null) {
            return;
        }
        if (isInteger(value)) {
            this.roleid = Integer.valueOf(value);
        } else {
            return;
        }
    }

    public boolean isNoMatch() {
        switch (this.status) {
            case STATUS_MATCHED_NONE:
                return true;
            default:
                return false;
        }
    }

    public boolean isNew() {
        switch (this.status) {
            case STATUS_NEW:
                return true;
            default:
                return false;
        }
    }

    public boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * public void validate() { if (this == null) { return; } if (this.person ==
     * null) { this.person = new Person(); } if (this.address == null) {
     * this.address = new Address(); } if (this.bankaccountlist == null) {
     * List<Bankaccount> bankaccountlistnew = new ArrayList();
     * this.bankaccountlist = bankaccountlistnew; } if (this.phonelist == null)
     * { List<Phone> phonelistnew = new ArrayList(); this.phonelist =
     * phonelistnew; } if (this.urllist == null) { List<URL> urllistnew = new
     * ArrayList(); this.urllist = urllistnew; } }
     *
     */
    public String matchDescription() {
        String message = "";
        if (this == null) {
            message = message.concat(MESSAGE_LEVEL_SEVERE).concat("Persoon is niet gevonden aan de hand van naam- en adresgegevens voor het wijzigen van het adres.");
            message = message.concat("\n");
        } else {
            switch (this.getStatus()) {
                case Role.STATUS_MATCHED_ADDRESS_BANKACCOUNT:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van adresgegevens en bankrekeningnummer. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                case Role.STATUS_MATCHED_ADDRESS_PHONE:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van adresgegevens en telefoonnummer. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                case Role.STATUS_MATCHED_ADDRESS_URL:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van adresgegevens en emailadres. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                case Role.STATUS_MATCHED_ID:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van het lidnummer. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                case Role.STATUS_MATCHED_NONE:
                    message = message.concat(MESSAGE_LEVEL_WARNING).concat("Persoon niet gevonden.");
                    break;
                case Role.STATUS_MATCHED_PERSON:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van naamgegevens. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                case Role.STATUS_MATCHED_ADDRESS:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van adresgegevens. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                case Role.STATUS_MATCHED_PERSON_ADDRESS:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van naam- en adresgegevens. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                case Role.STATUS_MATCHED_PERSON_BANKACCOUNT:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van naam en bankrekeningnummer. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                case Role.STATUS_MATCHED_PERSON_PHONE:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van naam en telefoonnummer. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                case Role.STATUS_MATCHED_PERSON_URL:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van naam en emailadres. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                case Role.STATUS_MATCHED_PHONE:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van telefoonnummer. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                case Role.STATUS_MATCHED_URL:
                    message = message.concat(MESSAGE_LEVEL_INFO).concat("Persoon is gevonden aan de hand van emailadres. Lidnummer is " + String.valueOf(this.roleid) + ".");
                    break;
                default:
                    message = message.concat(MESSAGE_LEVEL_SEVERE).concat("Invalid matching status value " + this.getStatus() + ".");
                    break;
            }
        }
        return message;
    }

    public void mapPropertyValue(Properties mapping) {
        DateFormat dateformat = new DateFormat(0, 3, 5, 6, 8, 9);
        mapPropertyValue(mapping, dateformat);
    }

    public void mapPropertyValueOld(Properties mapping) {
        DateFormat dateformat = new DateFormat(0, 3, 5, 6, 8, 9);
        mapPropertyValueOld(mapping, dateformat);
    }

    public void mapPropertyValueNew(Properties mapping) {
        DateFormat dateformat = new DateFormat(0, 3, 5, 6, 8, 9);
        mapPropertyValueNew(mapping, dateformat);
    }

    public void mapPropertyValue(Properties mapping, SimpleDateFormat simpledateformat) {
        person = new Person();
        address = new Address();
        phonelist.clear();
        urllist.clear();
        bankaccountlist.clear();

        parseRoleid((mapping.getProperty("role_roleid") == null) ? "" : mapping.getProperty("role_roleid"));
        parseSource((mapping.getProperty("role_source") == null) ? "" : mapping.getProperty("role_source"));

        Phone phone = new Phone();
        URL url = new URL();
        Bankaccount bankaccount = new Bankaccount();

        person.mapPropertyValue(mapping, simpledateformat);
        address.mapPropertyValue(mapping);
        phone.mapPropertyValue(mapping);
        url.mapPropertyValue(mapping);
        bankaccount.mapPropertyValue(mapping);

        phonelist.add(phone);
        urllist.add(url);
        bankaccountlist.add(bankaccount);
    }

    public void mapPropertyValue(Properties mapping, DateFormat dateformat) {
        person = new Person();
        address = new Address();
        phonelist.clear();
        urllist.clear();
        bankaccountlist.clear();

        parseRoleid((mapping.getProperty("role_roleid") == null) ? "" : mapping.getProperty("role_roleid"));
        parseSource((mapping.getProperty("role_source") == null) ? "" : mapping.getProperty("role_source"));

        Phone phone = new Phone();
        URL url = new URL();
        Bankaccount bankaccount = new Bankaccount();

        person.mapPropertyValue(mapping, dateformat);
        address.mapPropertyValue(mapping);
        phone.mapPropertyValue(mapping);
        url.mapPropertyValue(mapping);
        bankaccount.mapPropertyValue(mapping);

        phonelist.add(phone);
        urllist.add(url);
        bankaccountlist.add(bankaccount);
    }

    public void mapPropertyValueOld(Properties mapping, DateFormat dateformat) {
        person = new Person();
        address = new Address();
        phonelist.clear();
        urllist.clear();
        bankaccountlist.clear();

        parseRoleid((mapping.getProperty("role_roleid_old") == null) ? "" : mapping.getProperty("role_roleid_old"));
        parseSource((mapping.getProperty("role_source_old") == null) ? "" : mapping.getProperty("role_source_old"));

        Phone phone = new Phone();
        URL url = new URL();
        Bankaccount bankaccount = new Bankaccount();

        person.mapPropertyValue(mapping, dateformat);
        address.mapPropertyValueOld(mapping);
        phone.mapPropertyValueOld(mapping);
        url.mapPropertyValueOld(mapping);
        bankaccount.mapPropertyValueOld(mapping);

        phonelist.add(phone);
        urllist.add(url);
        bankaccountlist.add(bankaccount);
    }

    public void mapPropertyValueNew(Properties mapping, DateFormat dateformat) {
        person = new Person();
        address = new Address();
        phonelist.clear();
        urllist.clear();
        bankaccountlist.clear();

        parseRoleid((mapping.getProperty("role_roleid_new") == null) ? "" : mapping.getProperty("role_roleid_new"));
        parseSource((mapping.getProperty("role_source_new") == null) ? "" : mapping.getProperty("role_source_new"));

        Phone phone = new Phone();
        URL url = new URL();
        Bankaccount bankaccount = new Bankaccount();

        person.mapPropertyValue(mapping, dateformat);
        address.mapPropertyValueNew(mapping);
        phone.mapPropertyValueNew(mapping);
        url.mapPropertyValueNew(mapping);
        bankaccount.mapPropertyValueNew(mapping);

        phonelist.add(phone);
        urllist.add(url);
        bankaccountlist.add(bankaccount);
    }
}
