/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ed
 */
public class Phone {

    public final static int TYPE_HOME_PRIMARY = 1;
    public final static int TYPE_HOME_SECONDARY = 2;
    public final static int TYPE_WORK_PRIMARY = 3;
    public final static int TYPE_WORK_SECONDARY = 4;
    //
    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    public final static int STATUS_MATCHED_ID = 3;
    public final static int STATUS_MATCHED_NUMBER = 4;
    //
    //
    public final static String FIELD_PEOPLE_ID = "PEOPLEKEY";
    public final static String FIELD_PEOPLE_PHONE = "MOBLEPHNE";
    //
    public final static String FIELD_CONTNOS_ID = "COKEY";
    public final static String FIELD_CONTNOS_ROLEID = "PVKEY";
    public final static String FIELD_CONTNOS_PERSONID = "PEOPLEKEY";
    public final static String FIELD_CONTNOS_ADDRESSID = "ADDRESSKEY";
    public final static String FIELD_CONTNOS_PHONE = "CONTACTNO";
    public final static String FIELD_CONTNOS_ADDEDDATE = "DATEADDED";
    public final static String FIELD_CONTNOS_TYPE = "PHNTYP";
    //
    public final static String TYPE_PHONE_HOME = "TELPRV";
    public final static String TYPE_PHONE_WORK = "TELWRK";
    public final static String TYPE_PHONE_MOBILE = "MOBIEL";
    //
    public final static int PHONE_LANDLINE = 1;
    public final static int PHONE_MOBILE = 2;
    public final static int PHONE_LANDLINE_PRIMARY = 3;
    public final static int PHONE_MOBILE_PRIMARY = 4;
    public final static int PHONE_LANDLINE_SECONDARY = 5;
    public final static int PHONE_MOBILE_SECONDARY = 6;
    // The identifying primary key for the phone object is defined as the sum of the roleid and the (telephone) number.
    private long phoneid;
    private long number;
    private long roleid;
    private int status;
    //

    //            // Try the first landline-type phonenumber for a person which is stored in the PHONE column of the CONTACT table
    //query = "SELECT * FROM contact c WHERE " + Role.FIELD_CONTACT_ROLEID + "=" + roleid;
    // Try the first mobile phonenumber for a person which is stored in the MOBLEPHNE column of the PEOPLE table
//                query = "SELECT * FROM people p WHERE " + Person.FIELD_PEOPLE_ID + "=" + personid;
    // Try subsequent landline and mobile phonenumbers which are stored in the CONTACTNO column of the CONTNOS table
//            query = "SELECT * FROM contnos c WHERE " + Phone.FIELD_CONTNOS_ROLEID + "=" + roleid + " AND phntyp in ('TELWRK', 'TELPRV')";
    // Try subsequent landline and mobile phonenumbers which are stored in the CONTACTNO column of the CONTNOS table
//            query = "SELECT * FROM contnos c WHERE " + Phone.FIELD_CONTNOS_ROLEID + "=" + roleid + " AND phntyp = 'MOBIEL'";
    public Phone() {
        this.phoneid = 0;
        this.number = 0;
        this.status = STATUS_NEW;
    }

    public Phone(long phoneid, long number) {
        this.phoneid = phoneid;
        this.number = number;
        this.status = STATUS_NEW;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getPhoneid() {
        return phoneid;
    }

    public void setPhoneid(long phoneid) {
        this.phoneid = phoneid;
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

    // Vaste nummers
    // /^(((0)[1-9]{2}[0-9][-]?[1-9][0-9]{5})|((\\+31|0|0031)[1-9][0-9][-]?[1-9][0-9]{6}))$/
    // Mobiele nummers
    // /^(((\\+31|0|0031)6){1}[1-9]{1}[0-9]{7})$/i
    public void parseNumber(String value) {
        if (value == null) {
            return;
        }
        String phonevalue = "";
        int type = 0;
        try {
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if ("0123456789".contains(String.valueOf(c))) {
                    phonevalue = phonevalue.concat(String.valueOf(value.charAt(i)));
                }
            }
            if (isInteger(phonevalue)) {
                this.number = Long.valueOf(phonevalue);
            }
        } catch (Exception e) {
            Logger.getLogger(Phone.class.getName()).log(Level.SEVERE, e.getMessage(), e);
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

    public boolean isNew() {
        switch (this.status) {
            case STATUS_NEW:
                return true;
            default:
                return false;
        }
    }

    public boolean isAcceptableMatch() {
        if (this == null) {
            return false;
        }
        switch (this.getStatus()) {
            case STATUS_MATCHED_ID:
                // Phone is matched via id, so we are pretty sure we have got the right phone object.
                return true;
            case STATUS_MATCHED_NUMBER:
                // Phone is matched via bankaccount number, so we are pretty sure we have got the right phone object.
                return true;
            default:
                return false;
        }
    }

    public int getType() {
        return (int) this.phoneid % 10;
    }

    public String getFormattedNumber() {
        if (this.number != 0) {
            return "0".concat(String.valueOf(this.number));
        } else {
            return "";
        }
    }

    public boolean isLandline() {
        int type = (int) this.phoneid % 10;
        switch (type) {
            case Phone.PHONE_LANDLINE_PRIMARY:
                return true;
            case Phone.PHONE_LANDLINE_SECONDARY:
                return true;
            default:
                return false;
        }
    }

    public boolean isMobile() {
        int type = (int) this.phoneid % 10;
        switch (type) {
            case Phone.PHONE_MOBILE_PRIMARY:
                return true;
            case Phone.PHONE_MOBILE_SECONDARY:
                return true;
            default:
                return false;
        }
    }

    public boolean isLandlinePrimary() {
        int type = (int) this.phoneid % 10;
        switch (type) {
            case Phone.PHONE_LANDLINE_PRIMARY:
                return true;
            default:
                return false;
        }
    }

    public boolean isLandlineSecondary() {
        int type = (int) this.phoneid % 10;
        switch (type) {
            case Phone.PHONE_LANDLINE_SECONDARY:
                return true;
            default:
                return false;
        }
    }

    public boolean isMobilePrimary() {
        int type = (int) this.phoneid % 10;
        switch (type) {
            case Phone.PHONE_MOBILE_PRIMARY:
                return true;
            default:
                return false;
        }
    }

    public boolean isMobileSecondary() {
        int type = (int) this.phoneid % 10;
        switch (type) {
            case Phone.PHONE_MOBILE_SECONDARY:
                return true;
            default:
                return false;
        }
    }

    public boolean equalsNumber(Phone object) {
        if (this.number == object.getNumber()) {
            return true;
        }
        return false;
    }

    public boolean isEqual(Phone otherphone) {
        if (this.number == otherphone.number) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Phone other = (Phone) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (int) (this.number ^ (this.number >>> 32));
        return hash;
    }

    public void mapPropertyValue(Properties mapping) {
        parseNumber((mapping.getProperty("phone_number") == null) ? "" : mapping.getProperty("phone_number"));
    }

    public void mapPropertyValueOld(Properties mapping) {
        parseNumber((mapping.getProperty("phone_number_old") == null) ? "" : mapping.getProperty("phone_number_old"));
    }

    public void mapPropertyValueNew(Properties mapping) {
        parseNumber((mapping.getProperty("phone_number_new") == null) ? "" : mapping.getProperty("phone_number_new"));
    }
}
