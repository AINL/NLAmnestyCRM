/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.util.Collection;
import java.util.Properties;
import nl.amnesty.crm.collection.IdStartdateEnddate;

/**
 *
 * @author ed
 */
public class Network {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    //
    /*
     * 1. E-nieuws 2. E-mailacties 3. RSVP* 4. Write for Rights** 5.
     * Actieplatform 6. Regiobulletin DFG*** 7. Regiobulletin FNHU 8.
     * Regiobulletin NOG 9. Regiobulletin ZZ 10. Regiobulletin BL 11.
     * AmnestyNU**** 12. COCO
     */
    /*
     * CRM Adhoc Network Blocking Codes
     */
    // Adhoc networks (the adhoc networks are not stored in CRM as such, the content of these networks vary)
    //public final static String NETWORK_BLOCK_SHP = "FLW US WSM"; // Mailing aan webshop klanten
    //public final static String NETWORK_BLOCK_OCC = "AA US MAIL"; // Indicentele mailingen
    //public final static String NETWORK_BLOCK_ENR = "FLW US LW"; // Ledenwerving
    // The numeric id's used here are directly liked to the drupal id's of the webforms that are part of www.amnesty.nl
    //public final static int NETWORK_ID_ACTIVE_MEMBERS = 1;
    //public final static int NETWORK_ID_DIGITAL_ACTION_FILES = 2;
    //public final static int NETWORK_ID_EMAILACTION = 283;
    //public final static int NETWORK_ID_ENEWS = 4;
    //public final static int NETWORK_ID_FAIRTRADE_SHOPS = 5;
    //public final static int NETWORK_ID_LETTER_WRITING = 279;
    //public final static int NETWORK_ID_TEXTMESSAGING_ACTION = 280;
    //public final static int NETWORK_ID_URGENT_ACTIONS = 281;
    //public final static int NETWORK_ID_WRITE_FOR_RIGHTS = 238;
    //
    //public final static String NETWORK_DESCRIPTION_ACTIVE_MEMBERS = "Active members";
    // public final static String NETWORK_DESCRIPTION_ACTIVE_MEMBERS_EMAIL_AS_ID = "Active members (New OpenDS LDAP)";
    //public final static String NETWORK_DESCRIPTION_DIGITAL_ACTION_FILES = "Digital action files";
    //public final static String NETWORK_DESCRIPTION_EMAILACTION = "Emailaction network";
    //public final static String NETWORK_DESCRIPTION_ENEWS = "Enieuws network";
    //public final static String NETWORK_DESCRIPTION_FAIRTRADE_SHOPS = "Fairtrade shops";
    //public final static String NETWORK_DESCRIPTION_LETTER_WRITING = "RSVP";
    //public final static String NETWORK_DESCRIPTION_TEXTMESSAGING_ACTION = "Textmessaging action";
    //public final static String NETWORK_DESCRIPTION_URGENT_ACTIONS = "Urgent Actions";
    //public final static String NETWORK_DESCRIPTION_WRITE_FOR_RIGHTS = "Write for rights";
    //
    /*
     * CRM Possible values for SORT field of INDACTIONS table
     *
     * DAN EMAILACTIE ENIEUWS HAN JAN JURIST MA MEDBER MEDICI MEDOND MILIT MRAN
     * ONDERW PLATFORM POLIT PRG RSVP SAN SMS UA VAN WRITERIGHT
     */
    /*
     * CRM Email communication block codes
     */
    public final static String NETWORK_BLOCK_EMAIL_TRUE = "N"; // E-mail communicatie ongewenst
    public final static String NETWORK_BLOCK_EMAIL_FALSE = "J"; // E-mail communicatie toegestaan

    /*
     * CRM Data Structure
     */
    public final static String FIELD_CONTACT_ROLEID = "PVKEY";
    public final static String FIELD_CONTACT_STARTDATE = "DATEADDED";
    //
    public final static String FIELD_CONTNOS_ROLEID = "PVKEY";
    //
    public final static String FIELD_INDACTIONS_ID = "IDKEY";
    public final static String FIELD_INDACTIONS_ROLEID = "PVKEY";
    public final static String FIELD_INDACTIONS_PHONEID = "FREQUENCY";
    public final static String FIELD_INDACTIONS_FREQUENCY = "FREQUENCY";
    public final static String FIELD_INDACTIONS_CONTACTCHANNELID = "FREQUENCY";
    public final static String FIELD_INDACTIONS_STARTDATE = "STARTDATE";
    public final static String FIELD_INDACTIONS_ENDDATE = "ENDDATE";
    public final static String FIELD_INDACTIONS_TYPE = "TYPE";
    public final static String FIELD_INDACTIONS_SORT = "SORT";
    public final static String FIELD_INDACTIONS_STATUS = "STATUS";
    public final static String FIELD_INDACTIONS_MEDIAPREFERENCE = "MEDIAPREFERENCE";
    public final static String FIELD_INDACTIONS_SOURCE = "SOURCE";
    //
    //public final static String DEFAULT_INDACTIONS_TYPE = "ACTIENTWK";
    public final static String DEFAULT_INDACTIONS_SORT = "";
    public final static String DEFAULT_INDACTIONS_STATUS = "ACTIEF";
    public final static String DEFAULT_INDACTIONS_MEDIAPREFERENCE = "EMAIL";
    public final static String DEFAULT_INDACTIONS_SOURCE = "";
    //
    public final static String FIELD_GRPMBRS_ID = "GMKEY";
    public final static String FIELD_GRPMBRS_ROLEID = "PVKEY";
    public final static String FIELD_GRPMBRS_BLOCKCODE = "GROUP_KEY";
    public final static String FIELD_GRPMBRS_STARTDATE = "JOINDATE";
    public final static String FIELD_GRPMBRS_ENDDATE = "LEFTDATE";
    public final static String FIELD_GRPMBRS_TEXT = "TEXT";
    //
    public final static String FIELD_AIBASIS_EMAIL_HOME = "EMAIL THUIS";
    public final static String FIELD_AIBASIS_EMAIL_WORK = "EMAIL WERK";
    //
    public final static String FIELD_MEMBERS_ROLEID = "PVKEY";
    //
    public final static String FIELD_WKWLINK_ID = "LINKKEY";
    public final static String FIELD_WKWLINK_LINKPEOP1 = "LINKPEOP1";
    public final static String FIELD_WKWLINK_LINKPEOP2 = "LINKPEOP2";
    public final static String FIELD_WKWLINK_STARTDATE = "LINKDATE";
    public final static String FIELD_WKWLINK_ENDDATE = "ENDDATE";
    //
    public final static String FIELD_FLAGS_ROLEID = "PVKEY";
    //
    public final static String FIELD_UDLOOKUP_DESCRIPTION = "CODEDESC";
    //
    public final static int COLLECTION_ID_IS_ROLEID = 1;
    public final static int COLLECTION_ID_IS_EMAIL = 2;
    public final static int COLLECTION_ID_IS_PHONE = 3;
    //
    public final static int CONTACTCHANNEL_EMAIL = 1;
    public final static int CONTACTCHANNEL_PHONE = 2;
    //
    /*
     * public final static String NETWORK_PRODUCT_NAME_ACTIVE_MEMBERS = "";
     * public final static String
     * NETWORK_PRODUCT_NAME_ACTIVE_MEMBERS_EMAIL_AS_ID = ""; public final static
     * String NETWORK_PRODUCT_NAME_DIGITAL_ACTION_FILES = ""; public final
     * static String NETWORK_PRODUCT_NAME_EMAILACTION = "EMAILACTIE"; public
     * final static String NETWORK_PRODUCT_NAME_ENEWS = "ENIEUWS"; public final
     * static String NETWORK_PRODUCT_NAME_FAIRTRADE_SHOPS = ""; public final
     * static String NETWORK_PRODUCT_NAME_LETTER_WRITING = "RSVP"; public final
     * static String NETWORK_PRODUCT_NAME_TEXTMESSAGING_ACTION = "SMS"; public
     * final static String NETWORK_PRODUCT_NAME_URGENT_ACTIONS = "UA"; public
     * final static String NETWORK_PRODUCT_NAME_WRITE_FOR_RIGHTS =
     * "WRITERIGHTS";
     *
     */
    //
    private static final String TYPE_NETWORK_MEMBER = "LEDEN";
    private static final String TYPE_NETWORK_COUNTRY = "DAF";
    private static final String TYPE_NETWORK_ACTION = "ACTIENTWK";
    //
    private long networkid;
    private String type;
    private String name;
    private String source;
    private String description;
    private Collection<IdStartdateEnddate> idlist;
    private int status;

    public Network() {
    }

    public Network(long networkid, String type, String name, String source, String description, Collection<IdStartdateEnddate> idlist) {
        this.networkid = networkid;
        this.type = type;
        this.name = name;
        this.source = source;
        this.description = description;
        this.idlist = idlist;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Collection<IdStartdateEnddate> getIdlist() {
        return idlist;
    }

    public void setIdlist(Collection<IdStartdateEnddate> idlist) {
        this.idlist = idlist;
    }

    public long getNetworkid() {
        return networkid;
    }

    public void setNetworkid(long networkid) {
        this.networkid = networkid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void parseName(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        this.name = value;
    }

    public void parseSource(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        this.source = value;
    }

    public void parseDescription(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        this.description = value;
    }

    public void mapPropertyValue(Properties mapping) {
        parseName((mapping.getProperty("network_name") == null) ? "" : mapping.getProperty("network_name"));
        parseSource((mapping.getProperty("network_source") == null) ? "" : mapping.getProperty("network_source"));
        parseDescription((mapping.getProperty("network_description") == null) ? "" : mapping.getProperty("network_description"));
    }
}
