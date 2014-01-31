/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 *
 * @author ed
 *
 * http://user:pass@voorbeeld.com:992/dier/vogel?soort=zeemeeuw#vleugels
 */
public class URL {

    public final static int TYPE_EMAIL_HOME_PRIMARY = 1;
    public final static int TYPE_EMAIL_HOME_SECONDARY = 2;
    public final static int TYPE_EMAIL_WORK_PRIMARY = 3;
    public final static int TYPE_EMAIL_WORK_SECONDARY = 4;
    //
    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    public final static int STATUS_MATCHED_ID = 3;
    public final static int STATUS_MATCHED_EMAIL = 4;
    public final static String PROTOCOL_EMAIL = "email:";
    public final static String PROTOCOL_FTP = "ftp:";
    public final static String PROTOCOL_HTTP = "http:";
    public final static String PROTOCOL_HTTPS = "https:";
    public final static String PROTOCOL_SMTP = "smtp:";
    //
    public final static String FIELD_CONTNOS_ID = "COKEY";
    public final static String FIELD_CONTNOS_ROLEID = "PVKEY";
    public final static String FIELD_CONTNOS_PERSONID = "PEOPLEKEY";
    public final static String FIELD_CONTNOS_ADDRESSID = "ADDRESSKEY";
    public final static String FIELD_CONTNOS_ADDRESS = "CONTACTNO";
    public final static String FIELD_CONTNOS_DATEADDED = "DATEADDED";
    public final static String FIELD_CONTNOS_TYPE = "PHNTYP";
    public final static String TYPE_EMAIL_HOME = "EMAILPRV";
    public final static String TYPE_EMAIL_WORK = "EMAILWRK";
    public final static String TYPE_EMAIL_ERROR = "EMAILERR";
    private long urlid;
    private String protocol;
    private String username;
    private String password;
    private String domain;
    private int port;
    private String query;
    private String fragment;
    private long roleid;
    private Date dateadded;
    private int status;

    public URL() {
        this.urlid = 0;
        this.protocol = "";
        this.username = "";
        this.password = "";
        this.domain = "";
        this.port = 0;
        this.query = "";
        this.fragment = "";
        this.roleid = 0;
        Calendar calendar = Calendar.getInstance();
        TimeZone timezone = TimeZone.getDefault();
        calendar.setTimeZone(timezone);
        this.dateadded = calendar.getTime();
        this.status = STATUS_NEW;
    }

    public URL(long urlid, String protocol, String username, String password, String domain, int port, String query, String fragment, int roleid, Date dateadded) {
        this.urlid = urlid;
        this.protocol = protocol;
        this.username = username;
        this.password = password;
        this.domain = domain;
        this.port = port;
        this.query = query;
        this.fragment = fragment;
        this.roleid = roleid;
        this.dateadded = dateadded;
        this.status = STATUS_NEW;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public long getUrlid() {
        return urlid;
    }

    public void setUrlid(long urlid) {
        this.urlid = urlid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getRoleid() {
        return roleid;
    }

    public void setRoleid(long roleid) {
        this.roleid = roleid;
    }

    public Date getDateadded() {
        return dateadded;
    }

    public void setDateadded(Date dateadded) {
        this.dateadded = dateadded;
    }

    public void parseInternetAddress(String value) {
        if (value == null) {
            return;
        }
        this.protocol = PROTOCOL_EMAIL;
        this.password = "";
        this.port = 0;
        this.query = "";
        this.fragment = "";
        String trim = trimEmail(value);
        try {
            InternetAddress internetaddress = new InternetAddress(trim);
            this.username = trim.substring(0, trim.indexOf("@"));
            this.domain = trim.substring(trim.indexOf("@") + 1);
        } catch (AddressException ae) {
            this.username = "";
            this.domain = "";
        } catch (Exception e) {
            Logger.getLogger(URL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public boolean isInternetAddress() {
        String email;
        try {
            if (this.getUsername() != null && this.getDomain() != null) {
                email = this.getUsername().concat("@").concat(this.getDomain()).toLowerCase().trim();
                InternetAddress internetaddress = new InternetAddress(email);
                return true;
            }
            return false;
        } catch (AddressException ae) {
            return false;
        }
    }

    public static boolean isInternetAddress(String email) {
        try {
            if (email.contains("@")) {
                InternetAddress internetaddress = new InternetAddress(email);
                return true;
            }
            return false;
        } catch (AddressException ae) {
            return false;
        }
    }

    public String getInternetAddress() {
        String email;
        try {
            if (this.getUsername() != null && this.getDomain() != null) {
                email = this.getUsername().concat("@").concat(this.getDomain()).toLowerCase().trim();
                InternetAddress internetaddress = new InternetAddress(email);
                return email;
            }
            return "";
        } catch (AddressException ae) {
            return "";
        }
    }

    public void setInternetAddress(String email) {
        if (email.contains("@")) {
            this.username = email.substring(0, email.indexOf("@"));
            this.domain = email.substring(email.indexOf("@") + 1);
            this.protocol = PROTOCOL_EMAIL;
            this.fragment = "";
            this.password = "";
            this.port = 0;
            this.query = "";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final URL other = (URL) obj;
        if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username)) {
            return false;
        }
        if ((this.domain == null) ? (other.domain != null) : !this.domain.equals(other.domain)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.username != null ? this.username.hashCode() : 0);
        hash = 89 * hash + (this.domain != null ? this.domain.hashCode() : 0);
        return hash;
    }

    public boolean equalsEmail(URL object) {
        if (this.getUsername().equals(object.getUsername())) {
            if (this.getDomain().equals(object.getDomain())) {
                return true;
            }
        }
        return false;
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
                // URL is matched via id, so we are pretty sure we have got the right URL.
                return true;
            case STATUS_MATCHED_EMAIL:
                // URL is matched via email address, so we are pretty sure we have got the right URL.
                return true;
            default:
                return false;
        }
    }

    public boolean isEqual(URL otherurl) {
        if (this.username.equals(otherurl.username)) {
            if (this.domain.equals(otherurl.domain)) {
                return true;
            }
        }
        return false;
    }

    public void mapPropertyValue(Properties mapping) {
        parseInternetAddress((mapping.getProperty("url_email") == null) ? "" : mapping.getProperty("url_email"));
    }

    public void mapPropertyValueOld(Properties mapping) {
        parseInternetAddress((mapping.getProperty("url_email_old") == null) ? "" : mapping.getProperty("url_email_old"));
    }

    public void mapPropertyValueNew(Properties mapping) {
        parseInternetAddress((mapping.getProperty("url_email_new") == null) ? "" : mapping.getProperty("url_email_new"));
    }

    private String trimEmail(String email) {
        if (email.contains("@")) {
            if (email.contains("<") && email.contains(">")) {
                int beginindex = email.indexOf("<");
                int endindex = email.indexOf(">");
                email = email.substring(beginindex + 1, endindex);
            }
        } else {
            email = "";
        }
        return email;
    }
}
