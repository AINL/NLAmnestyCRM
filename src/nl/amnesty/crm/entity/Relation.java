/*
 * personto change this template, choose persontools | Templates
 * and open the template in the edipersontor.
 */
package nl.amnesty.crm.entity;

import java.util.Properties;

/**
 *
 * @author ed
 */
public class Relation {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    //
    private long relationid;
    private Person personfrom;
    private Person personto;
    private Relationtype relationtype;
    private int status;

    public Relation() {
    }

    public Relation(long relationid, Person personfrom, Person personto, Relationtype relationtype) {
        this.relationid = relationid;
        this.personfrom = personfrom;
        this.personto = personto;
        this.relationtype = relationtype;
    }

    public Person getPersonfrom() {
        return personfrom;
    }

    public void setPersonfrom(Person personfrom) {
        this.personfrom = personfrom;
    }

    public Person getPersonto() {
        return personto;
    }

    public void setPersonto(Person personto) {
        this.personto = personto;
    }

    public Relationtype getRelationtype() {
        return relationtype;
    }

    public void setRelationtype(Relationtype relationtype) {
        this.relationtype = relationtype;
    }

    public long getRelationid() {
        return relationid;
    }

    public void setRelationid(long relationid) {
        this.relationid = relationid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void mapPropertyValue(Properties mapping) {
    }
}
