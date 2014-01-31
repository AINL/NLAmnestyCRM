/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.config;

import java.util.List;

/**
 *
 * @author evelzen
 */
public class NetworkDef {

    public final static String URLCONFIGNETWORK = "http://zeebaars.amnesty.nl:8080/nlamnestyconfig/network.xml";
    //
    private long networkid;
    private String name;
    private String description;
    private String mediatype;
    private List<String> sql_read;
    private List<String> sql_add;
    private List<String> sql_remove;
    private List<String> sql_partof;

    public NetworkDef() {
    }

    public NetworkDef(long networkid, String name, String description, String mediatype, List<String> sql_read, List<String> sql_add, List<String> sql_remove, List<String> sql_partof) {
        this.networkid = networkid;
        this.name = name;
        this.description = description;
        this.mediatype = mediatype;
        this.sql_read = sql_read;
        this.sql_add = sql_add;
        this.sql_remove = sql_remove;
        this.sql_partof = sql_partof;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediatype() {
        return mediatype;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNetworkid() {
        return networkid;
    }

    public void setNetworkid(long networkid) {
        this.networkid = networkid;
    }

    public List<String> getSql_add() {
        return sql_add;
    }

    public void setSql_add(List<String> sql_add) {
        this.sql_add = sql_add;
    }

    public List<String> getSql_read() {
        return sql_read;
    }

    public void setSql_read(List<String> sql_read) {
        this.sql_read = sql_read;
    }

    public List<String> getSql_remove() {
        return sql_remove;
    }

    public void setSql_remove(List<String> sql_remove) {
        this.sql_remove = sql_remove;
    }

    public List<String> getSql_partof() {
        return sql_partof;
    }

    public void setSql_partof(List<String> sql_partof) {
        this.sql_partof = sql_partof;
    }
}
