/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.amnesty.crm.entity;

/**
 *
 * @author evelzen
 */
public class Country {
    private String name;
    private String isocode;

    public Country() {
    }

    public Country(String name, String isocode) {
        this.name = name;
        this.isocode = isocode;
    }

    public String getIsocode() {
        return isocode;
    }

    public void setIsocode(String isocode) {
        this.isocode = isocode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
