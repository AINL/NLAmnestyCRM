/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.collection;

import java.util.Date;

/**
 *
 * @author evelzen
 */
public class IdStartdateEnddate {

    private long roleid;
    private String id;
    private Date startdate;
    private Date enddate;

    public IdStartdateEnddate() {
    }

    public IdStartdateEnddate(long roleid, String id, Date startdate, Date enddate) {
        this.roleid = roleid;
        this.id = id;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public long getRoleid() {
        return roleid;
    }

    public void setRoleid(long roleid) {
        this.roleid = roleid;
    }

}
