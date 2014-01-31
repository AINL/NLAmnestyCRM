/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ed
 */
public class Subscription {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    public final static int STATUS_MATCHED_ID = 3;
    public final static int STATUS_MATCHED_ROLE_TYPE = 4;
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
    public final static String FIELD_MEMBERS_PAYMENT_PAID_DAY = "DAYPAID"; // 0
    //
    public final static String SUBSCRIPTION_STATUS_ACTIVE = "ACTIEF";
    public final static String SUBSCRIPTION_STATUS_SUSPENDED = "INACTIEF";
    public final static String SUBSCRIPTION_STATUS_ENDED = "OPGEZEGD";
    public final static String SUBSCRIPTION = "ABONNEMENT";
    public final static String SOURCE_016AA351 = "016AA351";
    //
    private long subscriptionid;
    private String source;
    private Date startdate;
    private Date enddate;
    private int frequency;
    private int mediatype;
    private long contactchannelid;
    private long roleid;
    private long productid;
    private int status;

    public Subscription() {
    }

    public Subscription(long subscriptionid, String source, Date startdate, Date enddate, int frequency, int mediatype, long contactchannelid, long roleid, long productid) {
        this.subscriptionid = subscriptionid;
        this.source = source;
        this.startdate = startdate;
        this.enddate = enddate;
        this.frequency = frequency;
        this.mediatype = mediatype;
        this.contactchannelid = contactchannelid;
        this.roleid = roleid;
        this.productid = productid;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public long getSubscriptionid() {
        return subscriptionid;
    }

    public void setSubscriptionid(long subscriptionid) {
        this.subscriptionid = subscriptionid;
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

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getMediatype() {
        return mediatype;
    }

    public void setMediatype(int mediatype) {
        this.mediatype = mediatype;
    }

    public long getContactchannelid() {
        return contactchannelid;
    }

    public void setContactchannelid(long contactchannelid) {
        this.contactchannelid = contactchannelid;
    }

    public long getProductid() {
        return productid;
    }

    public void setProductid(long productid) {
        this.productid = productid;
    }

    public long getRoleid() {
        return roleid;
    }

    public void setRoleid(long roleid) {
        this.roleid = roleid;
    }

    public boolean isAcceptableMatch() {
        switch (this.getStatus()) {
            case STATUS_MATCHED_ID:
                // Subscription is matched via id, so we are pretty sure we have got the right commitment.
                return true;
            case STATUS_MATCHED_ROLE_TYPE:
                // Subscription is matched via role and type.
                return true;
            default:
                return false;
        }

    }

    public void mapPropertyValue(Properties mapping, SimpleDateFormat simpledateformat) {
        parseEnddate((mapping.getProperty("subscription_stopdate") == null) ? "" : mapping.getProperty("subscription_stopdate"), simpledateformat);
        parseSource((mapping.getProperty("subscription_source") == null) ? "" : mapping.getProperty("subscription_source"));
    }

    public void parseEnddate(String value, SimpleDateFormat simpledateformat) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        try {
            // TODO: Validate date to exclude obvious wrong dates like 01-07-1077 and such!!!
            //simpledateformat.toPattern();
            Date date = simpledateformat.parse(value);
            this.enddate = date;
        } catch (ParseException pe) {
            Logger.getLogger(Person.class.getName()).log(Level.SEVERE, pe.getMessage(), pe);
        } catch (Exception e) {
            Logger.getLogger(Person.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void parseSource(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        this.source = value;
    }
}
