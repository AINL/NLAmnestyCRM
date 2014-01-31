/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

/**
 *
 * @author ed
 */
public class Flag {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    //
    public final static String FIELD_CONTACT_FLAG1 = "RLOGIC_1";
    public final static String FIELD_CONTACT_FLAG2 = "RLOGIC_2";
    public final static String FIELD_CONTACT_FLAG3 = "RLOGIC_3";
    public final static String FIELD_CONTACT_FLAG4 = "RLOGIC_4";
    public final static String FIELD_CONTACT_FLAG5 = "RLOGIC_5";
    public final static String FIELD_CONTACT_FLAG6 = "RLOGIC_6";
    public final static String FIELD_CONTACT_FLAG7 = "RLOGIC_7";
    public final static String FIELD_CONTACT_FLAG8 = "RLOGIC_8";
    public final static String FIELD_CONTACT_FLAG9 = "RLOGIC_9";
    public final static String FIELD_CONTACT_FLAG10 = "RLOGIC_10";
    //
    public final static int FLAG_COLLECTANT = 1;
    public final static int FLAG2 = 2;
    public final static int FLAG3 = 3;
    public final static int FLAG4 = 4;
    public final static int FLAG5 = 5;
    public final static int FLAG6 = 6;
    public final static int FLAG7 = 7;
    public final static int FLAG8 = 8;
    public final static int FLAG9 = 9;
    public final static int FLAG10 = 10;
    //
    private long flagid;
    private boolean flag;
    private long roleid;
    private int status;

    public Flag() {
    }

    public Flag(long flagid, boolean flag, long roleid) {
        this.flagid = flagid;
        this.flag = flag;
        this.roleid = roleid;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public long getFlagid() {
        return flagid;
    }

    public void setFlagid(long flagid) {
        this.flagid = flagid;
    }

    public long getPropertyid() {
        return flagid;
    }

    public void setPropertyid(long flagid) {
        this.flagid = flagid;
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
}
