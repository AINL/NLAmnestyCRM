/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.util.Properties;

/**
 *
 * @author ed
 */
public class Document {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    //
    private long documentid;
    private URL url;
    private String title;
    private String description;
    private int status;

    public Document() {
    }

    public Document(long documentid, URL url, String title, String description) {
        this.documentid = documentid;
        this.url = url;
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDocumentid() {
        return documentid;
    }

    public void setDocumentid(long documentid) {
        this.documentid = documentid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void parseTitle(String value) {
        this.title = value;
    }

    public void parseDescription(String value) {
        this.description = value;
    }

    public void mapPropertyValue(Properties mapping) {
        parseTitle((mapping.getProperty("document_title") == null) ? "" : mapping.getProperty("document_title"));
        parseDescription((mapping.getProperty("document_description") == null) ? "" : mapping.getProperty("document_description"));
    }
}
