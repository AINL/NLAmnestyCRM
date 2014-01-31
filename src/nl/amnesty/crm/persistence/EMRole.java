package nl.amnesty.crm.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.db.Database;
import nl.amnesty.crm.entity.Address;
import nl.amnesty.crm.entity.Bankaccount;
import nl.amnesty.crm.entity.Person;
import nl.amnesty.crm.entity.Phone;
import nl.amnesty.crm.entity.Role;
import nl.amnesty.crm.entity.URL;
import nl.amnesty.crm.sql.RoleSQL;

/**
 *
 * @author Amnesty International Dutch Section
 * @version 1.00
 */
public class EMRole implements EntityManager {

    /**
     * @param <T> The type of the object being persisted
     * @param t The CRM object being persisted
     * @return The persisted object
     * @exception Failed database operation
     */
    public <T> T persist(T t) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return (T) rolesql.create(connection, (Role) t);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     *
     * @param <T> The type of the object being found
     * @param id The CRM object being found
     * @return The found object
     * @exception Failed database operation
     */
    public <T> T find(long id) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return (T) rolesql.read(connection, id);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     *
     * @param <T> The type of the object being merged
     * @param t The CRM object being merged
     * @return Flag to indicate a successful merge
     * @exception Failed database operation
     */
    public <T> boolean merge(T t) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return rolesql.update(connection, (Role) t);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     *
     * @param <T> The type of the object being removed
     * @param id The CRM object being removed
     * @return Indication of successful removal
     * @exception Failed database operation
     */
    public <T> boolean remove(long id) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return rolesql.delete(connection, id);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     *
     * @param <T> The type of the object being matched
     * @param t The CRM object being matched
     * @return The matched object
     * @exception Failed database operation
     */
    public <T> T match(T t) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return (T) rolesql.match(connection, (Role) t);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     *
     * @param email Email address of role that we would want to find
     * @return Role matching the email address, if any
     * @exception Database error
     */
    public Role roleReadViaEmail(String email) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return rolesql.readViaEmail(connection, email);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     *
     * @param role Role for which the address will be updated
     * @param addressold Old address
     * @param addressnew New address to replace the old one
     * @return Updated role including changed address details
     * @exception Database error
     */
    public Role roleChangeAddress(Role role, Address addressold, Address addressnew) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return rolesql.changeAddress(connection, role, addressold, addressnew);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     *
     * @param role Role for which the phone will be updated
     * @param phoneold Old phone
     * @param phonenew New phone to replace the old one
     * @return Updated role including changed phone details
     * @exception Database error
     */
    public Role roleChangePhone(Person person, Address address, Phone phoneold, Phone phonenew) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return rolesql.changePhone(connection, person, address, phoneold, phonenew);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    public Role roleAddEmail(Role role, URL url) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return rolesql.addEmail(connection, role, url);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    public Role roleAddPhone(Role role, Phone phone) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return rolesql.addPhone(connection, role, phone);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    public Role roleAddBankaccount(Role role, Bankaccount bankaccount) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return rolesql.addBankaccount(connection, role, bankaccount);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     *
     * @param role Role for which the url will be updated
     * @param urlold Old email
     * @param urlnew New email to replace the old one
     * @return Updated role including changed url details
     * @exception Database error
     */
    public Role roleChangeEmail(Person person, Address address, URL urlold, URL urlnew) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return rolesql.changeEmail(connection, person, address, urlold, urlnew);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    public List<String> roleDetailsList(Role role) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            RoleSQL rolesql = new RoleSQL();
            return rolesql.getDetailsPlaintext(connection, role);
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    public String roleDetailsPlaintext(Role role) {
        String text = "";
        try {
            List<String> roledetaillist = roleDetailsList(role);
            for (String roledetail : roledetaillist) {
                text = text.concat(roledetail);
                text = text.concat("\n");
            }
            return text;
        } catch (Exception e) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     *
     * @param database CRM database object
     * @param connection SQL connection
     */
    private static void closeSQL(Database database, Connection connection) {
        try {
            if (connection != null) {
                connection.commit();
                if (!connection.isClosed()) {
                    connection.close();
                }
            }
            if (database != null) {
                if (!database.isClosed()) {
                    database.close();
                }
            }
        } catch (SQLException sqle) {
            Logger.getLogger(EMRole.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }

    public <T> T find(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
