/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

/**
 *
 * @author evelzen
 */
public class NetworkSQL {

    private String sqlreaddate;
    private String sqladd;
    private String sqlend;
    private String sqlupdatechannelid;
    private String sqlupdatemediatype;

    public NetworkSQL() {
    }

    public NetworkSQL(String sqlreaddate, String sqladd, String sqlend, String sqlupdatechannelid, String sqlupdatemediatype) {
        this.sqlreaddate = sqlreaddate;
        this.sqladd = sqladd;
        this.sqlend = sqlend;
        this.sqlupdatechannelid = sqlupdatechannelid;
        this.sqlupdatemediatype = sqlupdatemediatype;
    }

    public String getSqladd() {
        return sqladd;
    }

    public void setSqladd(String sqladd) {
        this.sqladd = sqladd;
    }

    public String getSqlend() {
        return sqlend;
    }

    public void setSqlend(String sqlend) {
        this.sqlend = sqlend;
    }

    public String getSqlreaddate() {
        return sqlreaddate;
    }

    public void setSqlreaddate(String sqlreaddate) {
        this.sqlreaddate = sqlreaddate;
    }

    public String getSqlupdatechannelid() {
        return sqlupdatechannelid;
    }

    public void setSqlupdatechannelid(String sqlupdatechannelid) {
        this.sqlupdatechannelid = sqlupdatechannelid;
    }

    public String getSqlupdatemediatype() {
        return sqlupdatemediatype;
    }

    public void setSqlupdatemediatype(String sqlupdatemediatype) {
        this.sqlupdatemediatype = sqlupdatemediatype;
    }
}
