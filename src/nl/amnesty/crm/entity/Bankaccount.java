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
 * @author evelzen
 */
public class Bankaccount {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    public final static int STATUS_MATCHED_ID = 3;
    public final static int STATUS_MATCHED_ROLE_NUMBER = 4;
    public final static int STATUS_MATCHED_NUMBER = 5;
    //
    public final static String FIELD_BANKACS_ID = "BANKKEY";
    public final static String FIELD_BANKACS_ROLEID = "PVKEY";
    public final static String FIELD_BANKACS_SOURCE = "SOURCE"; // DATE
    public final static String FIELD_BANKACS_DATE_ADDDATE = "DATEADDED"; // DATE
    public final static String FIELD_BANKACS_DATE_CHANGEDATE = "DATECHG"; // DATE
    public final static String FIELD_BANKACS_DIRECTDEBIT_CANCEL = "DDCANCEL"; // 0
    public final static String FIELD_BANKACS_DIRECTDEBIT_FIRST = "FIRSTDD"; // 0
    public final static String FIELD_BANKACS_DIRECTDEBIT_REJECTED = "REJECTED"; // N
    public final static String FIELD_BANKACS_BANK_REFERENCE = "BANKREF"; // ???
    public final static String FIELD_BANKACS_BANK_CONTACT = "BRNCHCONT"; // The Manager
    public final static String FIELD_BANKACS_BANKACCOUNT_NUMBER = "BANKACNO"; // BANKACCOUNT
    public final static String FIELD_BANKACS_IBAN_NUMBER = "IBAN"; // BANKACCOUNT
    public final static String FIELD_BANKACS_BIC_NUMBER = "BIC"; // BANKACCOUNT
    public final static String FIELD_BANKACS_BANKACCOUNT_NAME = "ACNAME"; // NAME PERSON
    public final static String FIELD_BANKACS_BANKACCOUNT_POSTALCODE = "BANKPCDE"; // POSTALCODE PERSON
    public final static String FIELD_BANKACS_BANKACCOUNT_PHONENUMBER = "PHONE"; // PHONE PERSON
    public final static String FIELD_BANKACS_BANKACCOUNT_STREET = "BANKADD1"; // STREET PERSON
    public final static String FIELD_BANKACS_BANKACCOUNT_CITY = "BANKADD4"; // CITY PERSON
    public final static String FIELD_BANKACS_BANKACCOUNT_PROVINCE = "BANKADD5"; // PROVINCE PERSON
    public final static String FIELD_BANKACS_BANKACCOUNT_LABEL = "LBLNAME"; // T.a.v. mevrouw + NAME / T.a.v. meneer + NAME
    public final static String FIELD_BANKACS_DIRECTDEBIT_STATUS = "STATUS"; // Active
    private long bankaccountid;
    private long number;
    private long roleid;
    private int status;
    private String iban;

    public Bankaccount() {
    }

    public Bankaccount(long bankaccountid, long number, long roleid) {
        this.bankaccountid = bankaccountid;
        this.number = number;
        this.roleid = roleid;
        this.status = STATUS_NEW;
    }

    public long getBankaccountid() {
        return bankaccountid;
    }

    public void setBankaccountid(long bankaccountid) {
        this.bankaccountid = bankaccountid;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
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

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Bankaccount other = (Bankaccount) obj;
        if (this.number != other.number) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (int) (this.number ^ (this.number >>> 32));
        return hash;
    }

    public void parseNumber(String value) {
        if (value == null) {
            return;
        }
        // TODO: CHECK for 9 digits for bankaccountnumbers and 11-check.
        String character = "";
        String numeric = "";
        try {
            for (int i = 0; i < value.length(); i++) {
                character = String.valueOf(value.charAt(i));
                if ("0123456789".contains(character)) {
                    numeric = numeric.concat(character);
                }
            }
            if (isInteger(numeric)) {
                this.number = Integer.parseInt(numeric);
            } else {
                this.number = 0;
            }
        } catch (Exception e) {
            Logger.getLogger(Bankaccount.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            this.number = 0;
        }
    }

    public String getFormattedNumber() {
        String formatted = "";
        String numbervalue = String.valueOf(this.number);
        if (numbervalue.length() == 7) {
            formatted = "P".concat(numbervalue);
            return formatted;
        } else {
            if (numbervalue.length() == 9) {
                for (int i = 0; i < 3; i++) {
                    formatted = formatted.concat(numbervalue.substring((i * 2), (i * 2) + 2)).concat(".");
                }
                formatted = formatted.concat(numbervalue.substring(6));
                return formatted;
            } else {
                return numbervalue;
            }
        }
    }

    public boolean equalsNumber(Bankaccount object) {
        if (this.number == object.getNumber()) {
            return true;
        }
        return false;
    }

    private boolean isInteger(String value) {
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
        switch (this.getStatus()) {
            case STATUS_MATCHED_ID:
                // Bankaccount is matched via id, so we are pretty sure we have got the right bankaccount.
                return true;
            case STATUS_MATCHED_ROLE_NUMBER:
                // Bankaccount is matched via role and bankaccount number, so we are pretty sure we have got the right bankaccount.
                return true;
            case STATUS_MATCHED_NUMBER:
                // Bankaccount is matched via bankaccount number, but also be used by other role.
                return false;
            default:
                return false;
        }

    }

    public boolean isValidNumber() {
        // Gironumbers are always valid
        if (this.number < 10000000) {
            return true;
        } else {
            // Banknumbers should have nine digits
            if (this.number > 999999999) {
                return false;
            } else {
                if (this.number < 100000000) {
                    return false;
                } else {
                    return doCheckEleven();
                }
            }
        }
    }

    public boolean doCheckEleven() {
        int digit = 0;
        int sum = 0;
        long banknumber = this.number;
        while (banknumber != 0) {
            digit = (int) (banknumber % 10);
            sum = sum + digit;
            banknumber = banknumber / 10;
        }
        if (sum % 11 == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void mapPropertyValue(Properties mapping) {
        String bankaccount=(mapping.getProperty("bankaccount_number") == null) ? "" : mapping.getProperty("bankaccount_number").replace(" ","");
        
        iban=(mapping.getProperty("bankaccount_iban") == null) ? "" : mapping.getProperty("bankaccount_iban").replace(" ","");
        // Controle of bban gedeelte van iban is. Anders bban overschijven met wat in iban staat, iban krijgt voorrang.
        if (bankaccount!=null && iban!=null) {
            if (!bankaccount.equals("") && !iban.equals("") && iban.length()==18) {
                if (!iban.endsWith(bankaccount)) {
                    bankaccount=iban.substring(8);  
                }
            }
        }
        parseNumber(bankaccount);
    }

    public void mapPropertyValueOld(Properties mapping) {
        parseNumber((mapping.getProperty("bankaccount_number_old") == null) ? "" : mapping.getProperty("bankaccount_number_old"));
    }

    public void mapPropertyValueNew(Properties mapping) {
        parseNumber((mapping.getProperty("bankaccount_number_new") == null) ? "" : mapping.getProperty("bankaccount_number_new"));
    }
}
