/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author ed
 */
public class Contact {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    public final static int STATUS_MATCHED_ID = 3;
    public final static int STATUS_MATCHED_SUBJECT_DATE = 4;
    public final static String FIELD_CONTHIST_ID = "CNKEY";
    public final static String FIELD_CONTHIST_ROLEID = "PVKEY";
    //public final static String FIELD_CONTHIST_PERSONID = "PEOPLEKEY";
    //public final static String FIELD_CONTHIST_ADDRESSID = "ADDRESSKEY";
    public final static String FIELD_CONTHIST_PHONE = "CONTHISTNO";
    public final static String FIELD_CONTHIST_EMAIL = "CONTHISTNO";
    public final static String FIELD_CONTHIST_CONTENT = "NOTES";
    public final static String FIELD_CONTHIST_DATE = "CONTDATE";
    public final static String FIELD_CONTHIST_TIME = "CONTTIME";
    public final static String FIELD_CONTHIST_STATUS = "CLOSED"; // Max size 1 char
    public final static String FIELD_CONTHIST_TYPE = "CONTTYPE"; // Max size 10 char
    public final static String FIELD_CONTHIST_DIRECTION = "CODE1"; // Max size 50 char
    public final static String FIELD_CONTHIST_SOURCE = "SOURCE"; // Max size 10 char
    public final static String FIELD_CONTHIST_SUBJECT = "CODE2"; // Max size 50 char
    public final static String FIELD_CONTHIST_USERDEFCODE3 = "CODE3"; // Max size 50 char
    public final static String FIELD_CONTHIST_USERDEFCODE4 = "CODE4"; // Max size 50 char
    public final static String FIELD_CONTHIST_USERDEFCODE5 = "CODE5"; // Max size 50 char
    public final static String FIELD_CONTHIST_USERDEFNUMB5 = "NUMB5";   // Float, maar wij doen een long
    //public final static String FIELD_INFO = "";
    public final static String FIELD_CONTHIST_AUTHOR = "ADDEDBY"; // Max size 50 char
    public final static String FIELD_CONTHIST_OWNER = "OWNEDBY"; // Max size 10 char
    //
    public final static String FIELD_CONTACT_DEBTORNUMBER = "UNUMB_1"; // This is where the debtor number for organizations is stored
    /*
     * conttype = relatie in code1 sms in code2 mailreact contdate source -
     * tekst - notes - close = 'y' addedby
     */
    //
    public final static String TYPE_SMS = "SMS";
    //
    private long contactid;
    private Document document;
    private String type;
    private boolean incoming;
    private Date date;
    private String subject;
    private String content;
    private String source;
    private String userdef3, userdef4, userdef5;
    private long usernumb5;
    private long roleid;
    private int status;

    public Contact() {
        Calendar calendar = Calendar.getInstance();
        this.contactid = 0;
        this.document = new Document();
        this.type = "";
        this.incoming = true;
        this.date = calendar.getTime();
        this.subject = "";
        this.content = "";
        this.source = "";
        this.roleid = 0;
        this.status = STATUS_NEW;
    }

    public Contact(long contactid, Document document, String type, boolean incoming, Date date, String subject, String content, String source, long roleid) {
        this.contactid = contactid;
        this.document = document;
        this.type = type;
        this.incoming = incoming;
        this.date = date;
        this.subject = subject;
        this.content = content;
        this.source = source;
        this.roleid = roleid;
    }

    public long getContactid() {
        return contactid;
    }

    public void setContactid(long contactid) {
        this.contactid = contactid;
    }

    public String getContent() {
        return content;
    }

    public long getRoleid() {
        return roleid;
    }

    public void setRoleid(long roleid) {
        this.roleid = roleid;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserdef3() {
        return userdef3;
    }

    public void setUserdef3(String userdef3) {
        this.userdef3 = userdef3;
    }

    public String getUserdef4() {
        return userdef4;
    }

    public void setUserdef4(String userdef4) {
        this.userdef4 = userdef4;
    }

    public String getUserdef5() {
        return userdef5;
    }

    public void setUserdef5(String userdef5) {
        this.userdef5 = userdef5;
    }

    public long getUsernumb5() {
        return usernumb5;
    }

    public void setUsernumb5(long usernumb5) {
        this.usernumb5 = usernumb5;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isAcceptableMatch() {
        switch (this.status) {
            case STATUS_MATCHED_ID:
                // Contact is matched via id, so we are pretty sure we have got the right address.
                return true;
            case STATUS_MATCHED_SUBJECT_DATE:
                // Contact matched via subject and date, time could be different but enough certainty for now...
                return true;
            default:
                return false;
        }
    }

    public void parseContent(String value) {
        if (value == null) {
            return;
        }
        this.content = value;
    }

    public void parseSource(String value) {
        if (value == null) {
            return;
        }
        this.source = value;
    }

    public void parseSubject(String value) {
        if (value == null) {
            return;
        }
        this.subject = value;
    }

    public void parseType(String value) {
        if (value == null) {
            return;
        }
        this.type = value;
    }

    public void parseUdef3(String value) {
        if (value == null) {
            return;
        }
        this.userdef3 = value;
    }

    public void parseUdef4(String value) {
        if (value == null) {
            return;
        }
        this.userdef4 = value;
    }

    public void parseUdef5(String value) {
        if (value == null) {
            return;
        }
        this.userdef5 = value;
    }

    public void mapPropertyValue(Properties mapping) {
        parseContent((mapping.getProperty("contact_content") == null) ? "" : mapping.getProperty("contact_content"));
        parseSource((mapping.getProperty("contact_source") == null) ? "" : mapping.getProperty("contact_source"));
        parseSubject((mapping.getProperty("contact_subject") == null) ? "" : mapping.getProperty("contact_subject"));
        parseType((mapping.getProperty("contact_type") == null) ? "" : mapping.getProperty("contact_type"));
        parseUdef3((mapping.getProperty("contact_userdef3") == null) ? "" : mapping.getProperty("contact_userdef3"));
        parseUdef4((mapping.getProperty("contact_userdef4") == null) ? "" : mapping.getProperty("contact_userdef4"));
        parseUdef5((mapping.getProperty("contact_userdef5") == null) ? "" : mapping.getProperty("contact_userdef5"));
    }
}
