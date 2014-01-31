/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.util.List;

/**
 *
 * @author ed
 */
public class Group {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    //
    private long groupid;
    private String name;
    private String description;
    private List<Long> roleidlist;
    private int status;

    public Group() {
    }

    public Group(long groupid, String name, String description, List<Long> roleidlist) {
        this.groupid = groupid;
        this.name = name;
        this.description = description;
        this.roleidlist = roleidlist;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getGroupid() {
        return groupid;
    }

    public void setGroupid(long groupid) {
        this.groupid = groupid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getRoleidlist() {
        return roleidlist;
    }

    public void setRoleidlist(List<Long> roleidlist) {
        this.roleidlist = roleidlist;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
