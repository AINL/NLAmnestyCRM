/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.util.IntUtil;

/**
 *
 * @author evelzen
 */
public class Commitment {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    public final static int STATUS_MATCHED_ID = 3;
    public final static int STATUS_MATCHED_ROLE_AMOUNT_FREQUENCY = 4;
    public final static int STATUS_MATCHED_ROLE_MEMBERTYPE_PAYMENTMETHOD = 5;
    //
    public final static String FIELD_MEMBERS_ID = "MEMKEY"; // GEN
    public final static String FIELD_MEMBERS_ID_SAME = "MBNO"; // GEN
    public final static String FIELD_MEMBERS_ROLEID = "PVKEY";
    public final static String FIELD_MEMBERS_FOREIGNKEY_BANKACS = "DDKEY";
    public final static String FIELD_MEMBERS_DATE_STARTDATE = "JOINDATE"; //DATE
    public final static String FIELD_MEMBERS_DATE_ENDDATE = "LEAVEDATE"; // NULL
    public final static String FIELD_MEMBERS_DATE_ADDDATE = "DATEADDED"; // DATE
    public final static String FIELD_MEMBERS_DATE_CHANGEDATE = "DATECHG"; // DATE
    public final static String FIELD_MEMBERS_DATE_RENEWDATE = "RENEWDATE"; // DATE + YEAR
    public final static String FIELD_MEMBERS_TYPE = "SUBSTYPE"; // LID
    public final static String FIELD_MEMBERS_MEMBERTYPE = "MEMTYPE"; // LIDDON
    public final static String FIELD_MEMBERS_TRANSACTIONTYPE = "TRANSTYPE1"; // LID
    public final static String FIELD_MEMBERS_PAYMENTMETHOD = "PAYMETHOD"; // INCASSO
    public final static String FIELD_MEMBERS_SOURCE = "SOURCE"; // WEBFORMSOURCE
    public final static String FIELD_MEMBERS_PAYMENT_SCHEDULE = "SCHEDULE";
    public final static String FIELD_MEMBERS_PAYMENT_REJECTED = "REJECTED";
    public final static String FIELD_MEMBERS_PAYMENT_FREQUENCY = "PAYFREQ";
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_TOTAL = "PAYAMOUNT"; // TOTAL 
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JAN = "JANUARY"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_FEB = "FEBRUARY"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAR = "MARCH"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_APR = "APRIL"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAY = "MAY"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUN = "JUNE"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUL = "JULY"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_AUG = "AUGUST"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_SEP = "SEPTEMBER"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_OCT = "OCTOBER"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_NOV = "NOVEMBER"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_DEC = "DECEMBER"; // TOTAL / FREQ
    public final static String FIELD_MEMBERS_PAYMENT_STARTDATE = "PAYMENTSTART"; // DATE
    public final static String FIELD_MEMBERS_PAYMENT_ENDDATE = "PAYMENTEND"; // 2199-12-31
    public final static String FIELD_MEMBERS_PAYMENT_PAID_MONTH = "MONTHPAID"; // 0
    public final static String FIELD_MEMBERS_PAYMENT_PAID_DAY = "DAYPAID"; // 0
    //
    public final static String FREQUENCY_YEAR_INDICATOR = "yearly";
    public final static String FREQUENCY_SEMESTER_INDICATOR = "semester";
    public final static String FREQUENCY_QUARTER_INDICATOR = "quarterly";
    public final static String FREQUENCY_MONTH_INDICATOR = "monthly";
    public final static String FREQUENCY_ONCE_INDICATOR = "onetime";
    public final static int FREQUENCY_YEAR = 1;
    public final static int FREQUENCY_SEMESTER = 2;
    public final static int FREQUENCY_QUARTER = 4;
    public final static int FREQUENCY_MONTH = 12;
    public final static String SOURCE_016AA351 = "016AA351";
    //
    private long commitmentid;
    private String source;
    private int amount;
    private int frequency;
    private Date startdate;
    private Date enddate;
    private long roleid;
    private long bankaccountid;
    private int status;

    public Commitment() {
    }

    public Commitment(long commitmentid, String source, int amount, int frequency, Date startdate, Date enddate, long roleid, long bankaccountid) {
        this.commitmentid = commitmentid;
        this.source = source;
        this.amount = amount;
        this.frequency = frequency;
        this.startdate = startdate;
        this.enddate = enddate;
        this.roleid = roleid;
        this.bankaccountid = bankaccountid;
    }

    public int getAmount() {
        return amount;
    }

    public String getFormattedAmount() {
        int euro = amount / 100;
        int cent = amount % 100;
        String value = "Euro ".concat(String.valueOf(euro)).concat(",").concat(String.valueOf(cent));
        if (cent == 0) {
            value = value.concat("0");
        }
        return value;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getCommitmentid() {
        return commitmentid;
    }

    public void setCommitmentid(long commitmentid) {
        this.commitmentid = commitmentid;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getFormattedFrequency() {
        switch (frequency) {
            case FREQUENCY_YEAR:
                return "per jaar";
            case FREQUENCY_SEMESTER:
                return "per half jaar";
            case FREQUENCY_QUARTER:
                return "per kwartaal";
            case FREQUENCY_MONTH:
                return "per maand";
            default:
                return "Error: unsupported frequency ".concat(String.valueOf(frequency));
        }
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getRoleid() {
        return roleid;
    }

    public void setRoleid(long roleid) {
        this.roleid = roleid;
    }

    public long getBankaccountid() {
        return bankaccountid;
    }

    public void setBankaccountid(long bankaccountid) {
        this.bankaccountid = bankaccountid;
    }

    public void parseFrequency(String value) {
        if (value == null) {
            return;
        }
        setFrequency(0);
        if (value.equals(Commitment.FREQUENCY_YEAR_INDICATOR)) {
            setFrequency(Commitment.FREQUENCY_YEAR);
        }
        if (value.equals(Commitment.FREQUENCY_SEMESTER_INDICATOR)) {
            setFrequency(Commitment.FREQUENCY_SEMESTER);
        }
        if (value.equals(Commitment.FREQUENCY_QUARTER_INDICATOR)) {
            setFrequency(Commitment.FREQUENCY_QUARTER);
        }
        if (value.equals(Commitment.FREQUENCY_MONTH_INDICATOR)) {
            setFrequency(Commitment.FREQUENCY_MONTH);
        }
    }

    public void parseAmount(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        parseAmountGeneric(value, true);
    }

    public void parseAmountEuro(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        parseAmountGeneric(value, false);
    }

    public void parseAmountEurocent(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        parseAmountGeneric(value, true);
    }

    public void parseAmountGeneric(String value, boolean iseurocents) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        String character = "";
        String numeric = "";
        String euro = "";
        String cent = "";
        boolean done = true;
        try {
            if (!iseurocents) {
                if (!value.contains(",") && !value.contains(".")) {
                    value = value.concat("00");
                } else {
                    if (value.contains(",") && !done) {
                        euro = value.substring(0, value.indexOf(","));
                        cent = value.substring(value.indexOf(",") + 1);
                        done = true;
                    }
                    if (value.contains(".") && !done) {
                        euro = value.substring(0, value.indexOf("."));
                        cent = value.substring(value.indexOf(".") + 1);
                        done = true;
                    }
                    value = euro.concat(cent);
                }
            }
            for (int i = 0; i < value.length(); i++) {
                character = String.valueOf(value.charAt(i));
                if ("0123456789".contains(character)) {
                    numeric = numeric.concat(character);
                }
            }
            if (isInteger(numeric)) {
                this.amount = Integer.parseInt(numeric);
            } else {
                this.amount = 0;
            }
        } catch (Exception e) {
            Logger.getLogger(Commitment.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            this.amount = 0;
        }
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
        switch (this.getStatus()) {
            case STATUS_MATCHED_ID:
                // Commitment is matched via id, so we are pretty sure we have got the right commitment.
                return true;
            case STATUS_MATCHED_ROLE_AMOUNT_FREQUENCY:
                // Commitment is matched via role, frequency and total amount.
                return true;
            case STATUS_MATCHED_ROLE_MEMBERTYPE_PAYMENTMETHOD:
                // Commitment is matched via role, membertype and payment method.
                return false;
            default:
                return false;
        }

    }

    public void mapPropertyValue(Properties mapping) {
        // amount_euro is other_amount is dus afwijkend, zelf opgegeven bedrag. kan een komma in zitten, of een punt, of...
        parseAmount((mapping.getProperty("commitment_amount") == null) ? "" : mapping.getProperty("commitment_amount"));
        String valueeuro = (mapping.getProperty("commitment_amount_euro") == null) ? "" : mapping.getProperty("commitment_amount_euro");
        String valueeurocent = (mapping.getProperty("commitment_amount_eurocent") == null) ? "" : mapping.getProperty("commitment_amount_eurocent");
        this.amount = 0;
        if (!valueeuro.trim().isEmpty()) {
            //if (IntUtil.isInteger(valueeuro.trim())) {
            parseAmountEuro(valueeuro.trim());
            //}
        } else {
            if (!valueeurocent.trim().isEmpty()) {
                //if (IntUtil.isInteger(valueeurocent.trim())) {
                parseAmountEurocent(valueeurocent.trim());
                //}
            }
        }
        parseFrequency((mapping.getProperty("commitment_frequency") == null) ? "" : mapping.getProperty("commitment_frequency"));
        source=(mapping.getProperty("commitment_source") == null) ? "" : mapping.getProperty("commitment_source");
    }
}
