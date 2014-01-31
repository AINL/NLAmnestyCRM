package nl.amnesty.crm.sql;

import java.sql.Connection;
import java.util.Properties;
import nl.amnesty.crm.entity.Product;
import nl.amnesty.crm.exception.CRMProductException;

/**
 *
 * @author ed
 */
public class ProductSQL {
    private static final String MSG_EXCEPTION = "Fatal error while updating product object for {0}";
    private static final String MSG_EXCEPTION_SQL = "Fatal SQL error while updating product object for {0}";

    /*
     * Standard CRUD methods
     */
    public Product create(Connection connection, Product product) {
        // TODO: Implementation for adding product, probably by adding entry to definition table
        return null;
    }

    public Product read(Connection connection, long productid) throws CRMProductException {
        Product product = new Product();
        try {
            Properties propertiesidname = setIdNameProperties();
            Properties propertiesiddescription = setIdDescriptionProperties();
            String key = String.valueOf(productid).trim();
            String name = propertiesidname.getProperty(key);
            String description = propertiesiddescription.getProperty(key);
            if (name == null) {
                product.setName("");
            } else {
                product.setName(name);
            }
            if (description == null) {
                product.setDescription("");
            } else {
                product.setDescription(description);
            }
            product.setPrice(0);
            product.setType("");
            product.setProductid(productid);
            return product;
        } catch (Exception e) {
            throw new CRMProductException(MSG_EXCEPTION, e);
        }
    }

    public boolean update(Connection connection, Product product) {
        // TODO: Implementation for updating product, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long productid) {
        // TODO: Implementation for deleting product, this is just a stub
        return false;
    }

    /*
     * Object matching
     */
    public Product match(Connection connection, Product product) {
        // TODO: Implementation for matching, this is just a stub
        product.setStatus(Product.STATUS_MATCHED_NONE);
        return product;
    }

    /*
     * READ methods
     */
    public Product readViaName(Connection connection, String name) throws CRMProductException {
        Product product = new Product();
        if (name == null) {
            return null;
        }
        if (name.isEmpty()) {
            return null;
        }
        try {
            long productid;
            Properties propertiesnameid = setNameIdProperties();
            Properties propertiesiddescription = setIdDescriptionProperties();
            String value = propertiesnameid.getProperty(name);
            if (value == null) {
                return null;
            } else {
                if (isInteger(value)) {
                    String description = propertiesiddescription.getProperty(value);
                    if (description == null) {
                        product.setDescription("");
                    } else {
                        product.setDescription(description);
                    }
                    productid = Long.valueOf(value);
                    product.setName(name);
                    product.setPrice(0);
                    product.setType("");
                    product.setProductid(productid);
                    return product;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            throw new CRMProductException(MSG_EXCEPTION, e);
        }
    }

    public boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Properties setIdNameProperties() {
        Properties properties = new Properties();
        for (String[] product : Product.productlist) {
            properties.setProperty(product[Product.PRODUCTLIST_INDEX_PRODUCTID], product[Product.PRODUCTLIST_INDEX_NAME]);
        }
        return properties;
    }

    private static Properties setNameIdProperties() {
        Properties properties = new Properties();
        for (String[] product : Product.productlist) {
            properties.setProperty(product[Product.PRODUCTLIST_INDEX_NAME], product[Product.PRODUCTLIST_INDEX_PRODUCTID]);
        }
        return properties;
    }

    private static Properties setIdDescriptionProperties() {
        Properties properties = new Properties();
        for (String[] product : Product.productlist) {
            properties.setProperty(product[Product.PRODUCTLIST_INDEX_PRODUCTID], product[Product.PRODUCTLIST_INDEX_DESCRIPTION]);
        }
        return properties;
    }
}
