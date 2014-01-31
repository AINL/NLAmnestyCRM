package nl.amnesty.crm.entity;

/**
 *
 * @author bmenting
 */
public class Organization {
    public final static String FIELD_ORGANIZATION_NAME = "Company";
    
    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;

    private long groupid;
    private String name;
    private int status;

    public Organization() {
    }

    public Organization(long groupid, String name) {
        this.groupid = groupid;
        this.name = name;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
