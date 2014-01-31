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
 * @author evelzen
 */
public class Involvement {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    public final static int STATUS_MATCHED_ID = 3;
    public final static int STATUS_MATCHED_ROLE_NAME = 4;
    //
    public final static String FIELD_GRPMBRS_ID = "GMKEY";
    public final static String FIELD_GRPMBRS_ROLEID = "PVKEY";
    public final static String FIELD_GRPMBRS_INVOLVEMENT_NAME = "GROUP_KEY";
    public final static String FIELD_GRPMBRS_INVOLVEMENT_DESCRIPTION = "TEXT";
    public final static String FIELD_GRPMBRS_INVOLVEMENT_SOURCE = "SOURCE";
    public final static String FIELD_GRPMBRS_INVOLVEMENT_STARTDATE = "JOINDATE";
    public final static String FIELD_GRPMBRS_INVOLVEMENT_ENDDATE = "LEFTDATE";
    //
    public final static String INVOLVEMENT_NAME_ACTIVE_MEMBERSHIP = "LA ACTINF";
    public final static String INVOLVEMENT_DESCRIPTION_ACTIVE_MEMBERSHIP = "Actief Lidmaatschap";
    //
    private long involvementid;
    private String source;
    private String name;
    private String description;
    private Date startdate;
    private Date enddate;
    private long roleid;
    private int status;

    public Involvement() {
        this.involvementid = 0;
        this.source = "";
        this.name = "";
        this.description = "";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.startdate = calendar.getTime();
        this.enddate = null;
        this.status = STATUS_NEW;
    }

    public Involvement(long involvementid, String source, String name, String description, Date startdate, Date enddate, long roleid) {
        this.involvementid = involvementid;
        this.source = source;
        this.name = name;
        this.description = description;
        this.startdate = startdate;
        this.enddate = enddate;
        this.roleid = roleid;
    }

    public long getInvolvementid() {
        return involvementid;
    }

    public void setInvolvementid(long involvementid) {
        this.involvementid = involvementid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        if (description == null) {
            return "";
        } else {
            return description;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getName() {
        if (name == null) {
            return "";
        } else {
            return name;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRoleid() {
        return roleid;
    }

    public void setRoleid(long roleid) {
        this.roleid = roleid;
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

    public boolean isAcceptableMatch() {
        switch (this.status) {
            case STATUS_MATCHED_ID:
                return true;
            case STATUS_MATCHED_ROLE_NAME:
                return true;
            default:
                return false;
        }
    }

    public void parseName(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        this.name = value;
    }

    public void parseDescription(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        this.description = value;
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

    public void mapPropertyValue(Properties mapping) {
        parseName((mapping.getProperty("involvement_name") == null) ? "" : mapping.getProperty("involvement_name"));
        parseDescription((mapping.getProperty("involvement_description") == null) ? "" : mapping.getProperty("involvement_description"));
        parseSource((mapping.getProperty("involvement_source") == null) ? "" : mapping.getProperty("involvement_source"));
    }
}
