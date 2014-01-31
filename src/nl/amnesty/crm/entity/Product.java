/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author ed
 */
public class Product {

    public final static int PRODUCTLIST_INDEX_PRODUCTID = 0;
    public final static int PRODUCTLIST_INDEX_NAME = 1;
    public final static int PRODUCTLIST_INDEX_DESCRIPTION = 2;
    //
    public final static String productlist[][] = {
        {"1", "AG01", ""},
        {"2", "ART2", ""},
        {"3", "DN00", ""},
        {"4", "DON", "Donateur"},
        {"5", "FASE 0", ""},
        {"6", "FASE 1", ""},
        {"7", "FASE 2", ""},
        {"8", "FASE 3", ""},
        {"9", "FASE 4", ""},
        {"10", "FASE 5", ""},
        {"11", "FASE 6", ""},
        {"12", "FASE 7", ""},
        {"13", "FO00", ""},
        {"14", "FT00", "Blad Frontaal"},
        {"15", "FT01", "Blad Frontaal"},
        {"16", "FT05", "Blad Frontaal"},
        {"17", "GALA", ""},
        {"18", "GRLID", "Groepslid"},
        {"19", "IA00", ""},
        {"20", "JV00", ""},
        {"21", "KW00", "Kwartaalblad"},
        {"22", "LID", "Lid"},
        {"23", "NA00", ""},
        {"24", "NE00", ""},
        {"25", "NF00", ""},
        {"26", "NG00", ""},
        {"27", "NWSBR JONG", ""},
        {"28", "NWSBR STUD", ""},
        {"29", "RA00", ""},
        {"30", "RV00", ""},
        {"31", "WV00", "Blad Wordt Vervolgd"},
        {"32", "WV01", "Blad Wordt Vervolgd"},
        {"33", "WV05", "Blad Wordt Vervolgd"}
    };
    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_NONE = 2;
    //
    /*
     * 1. E-nieuws 2. E-mailacties 3. RSVP* 4. Write for Rights** 5.
     * Actieplatform 6. Regiobulletin DFG*** 7. Regiobulletin FNHU 8.
     * Regiobulletin NOG 9. Regiobulletin ZZ 10. Regiobulletin BL 11.
     * AmnestyNU**** 12. COCO
     */
    public final static String PRODUCT_TYPE_ROLE = "ROLE";
    //
    public final static String PRODUCT_NAME_ROLE_COLLECTOR = "FLW CO JA";
    public final static String PRODUCT_DESCRIPTION_ROLE_COLLECTOR = "Collectant";
    public final static String NETWORK_PRODUCT_NAME_ACTIVE_MEMBERS = "";
    public final static String NETWORK_PRODUCT_NAME_ACTIVE_MEMBERS_EMAIL_AS_ID = "";
    public final static String NETWORK_PRODUCT_NAME_DIGITAL_ACTION_FILES = "";
    public final static String NETWORK_PRODUCT_NAME_EMAILACTION = "EMAILACTIE";
    public final static String NETWORK_PRODUCT_NAME_ENEWS = "ENIEUWS";
    public final static String NETWORK_PRODUCT_NAME_FAIRTRADE_SHOPS = "";
    public final static String NETWORK_PRODUCT_NAME_LETTER_WRITING = "RSVP";
    public final static String NETWORK_PRODUCT_NAME_TEXTMESSAGING_ACTION = "SMS";
    public final static String NETWORK_PRODUCT_NAME_URGENT_ACTIONS = "UA";
    public final static String NETWORK_PRODUCT_NAME_WRITE_FOR_RIGHTS = "WRITERIGHTS";
    private static final List<Product> NETWORK_PRODUCT_LIST = Arrays.asList(
            new Product(1, "ACTIENTWK", "ENIEUWS", "E-nieuws", "", 0),
            new Product(2, "ACTIENTWK", "EMAILACTIE", "E-mailacties", "", 0),
            new Product(3, "ACTIENTWK", "RSVP", "RSVP", "", 0),
            new Product(4, "ACTIENTWK", "WRITERIGHTS", "Write for Rights", "", 0),
            new Product(5, "ACTIENTWK", "PLATFORM", "Actieplatform", "", 0),
            new Product(6, "ACTIENTWK", "REGIODFG", "Regiobulletin DFG", "", 0),
            new Product(7, "ACTIENTWK", "REGIOFNU", "Regiobulletin FNU", "", 0),
            new Product(8, "ACTIENTWK", "REGIONOG", "Regiobulletin NOG", "", 0),
            new Product(9, "ACTIENTWK", "REGIOZZ", "Regiobulletin ZZ", "", 0),
            new Product(10, "ACTIENTWK", "REGIOBL", "Regiobulletin BL", "", 0),
            new Product(11, "ACTIENTWK", "AINU", "AmnestyNU", "", 0),
            new Product(12, "ACTIENTWK", "COCO", "COCO", "", 0));
    //
    private long productid;
    private String type;
    private String name;
    private String description;
    private int price;
    private String source;
    private int status;

    public Product() {
        this.productid = 0;
        this.type = "";
        this.name = "";
        this.description = "";
        this.price = 0;
        this.source = "";
        this.status = STATUS_NEW;
    }

    public Product(long productid, String type, String name, String description, String source, int price) {
        this.productid = productid;
        this.type = type;
        this.name = name;
        this.description = description;
        this.price = price;
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        if (description == null) {
            return "";
        } else {
            return description;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        if (name == null) {
            return "";
        } else {
            return name;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public long getProductid() {
        return productid;
    }

    public void setProductid(long productid) {
        this.productid = productid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public void parseType(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        this.type = value;
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

    public void parseDescription(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        this.description = value;
    }

    public int getIdViaName() {
        for (String product[] : productlist) {
            if (product[1].equals(name)) {
                return Integer.valueOf(product[0]);
            }
        }
        return 0;
    }

    public void mapPropertyValue(Properties mapping) {
        parseDescription((mapping.getProperty("product_description") == null) ? "" : mapping.getProperty("product_description"));
        parseName((mapping.getProperty("product_name") == null) ? "" : mapping.getProperty("product_name"));
        parseSource((mapping.getProperty("product_source") == null) ? "" : mapping.getProperty("product_source"));
        parseType((mapping.getProperty("product_type") == null) ? "" : mapping.getProperty("product_type"));
    }
}
