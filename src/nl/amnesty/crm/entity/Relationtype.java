/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

/**
 *
 * @author evelzen
 */
public class Relationtype {

    private long relationtypeid;
    private String name;
    private String description;

    public Relationtype() {
    }

    public Relationtype(long relationtypeid, String name, String description) {
        this.relationtypeid = relationtypeid;
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRelationtypeid() {
        return relationtypeid;
    }

    public void setRelationtypeid(long relationtypeid) {
        this.relationtypeid = relationtypeid;
    }
}
