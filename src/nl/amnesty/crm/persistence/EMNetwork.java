package nl.amnesty.crm.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.db.Database;
import nl.amnesty.crm.entity.Network;
import nl.amnesty.crm.exception.CRMNetworkException;
import nl.amnesty.crm.sql.NetworkSQL;

/**
 *
 * @author Amnesty International Dutch Section
 * @version 1.00
 */
public class EMNetwork implements EntityManager {

    /**
     * @param <T>   The type of the object being persisted
     * @param t     The CRM object being persisted
     * @return      The persisted object
     * @exception   Failed database operation
     */
    public <T> T persist(T t) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return (T) networksql.create(connection, (Network) t);
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param <T>   The type of the object being found
     * @param id    The CRM object being found
     * @return      The found object 
     * @exception   Failed database operation
     */
    public <T> T find(long id) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return (T) networksql.read(connection, null, id, null);
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param <T>   The type of the object being found
     * @param id    The CRM object being found
     * @return      The found object 
     * @exception   Failed database operation
     */
    public <T> T find(java.net.URL url, long id, String filter) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return (T) networksql.read(connection, url, id, filter);
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param <T>   The type of the object being found
     * @param id    The CRM object being found
     * @return      The found object 
     * @exception   Failed database operation
     */
    public <T> T find(String name) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return (T) networksql.read(connection, null, name, null);
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param <T>   The type of the object being found
     * @param id    The CRM object being found
     * @return      The found object 
     * @exception   Failed database operation
     */
    public <T> T find(java.net.URL url, String name, String filter) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return (T) networksql.read(connection, url, name, filter);
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param <T>   The type of the object being merged
     * @param t     The CRM object being merged
     * @return      Flag to indicate a successful merge
     * @exception   Failed database operation
     */
    public <T> boolean merge(T t) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return networksql.update(connection, (Network) t);
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param <T>   The type of the object being removed
     * @param id    The CRM object being removed
     * @return      Indication of successful removal
     * @exception   Failed database operation
     */
    public <T> boolean remove(long id) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return networksql.delete(connection, id);
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param <T>   The type of the object being matched
     * @param t     The CRM object being matched
     * @return      The matched object
     * @exception   Failed database operation
     */
    public <T> T match(T t) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return (T) networksql.match(connection, (Network) t);
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param networkid Network id
     * @param roleid    Role id of person that needs to be added to the network
     * @param channelid Channel that us used for communication, i.e. phone of email
     * @return          True if addition has been done, false if an error has occured
     * @exception       Database error hase occured
     */
    public boolean networkAdd(java.net.URL urlconfignetwork, long networkid, long roleid, String source) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return networksql.add(connection, urlconfignetwork, networkid, roleid, source);
        } catch (CRMNetworkException ne) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, ne.getMessage(), ne);
            return false;
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param networkname   Network name
     * @param roleid        Role id of person that needs to be added to the network
     * @param channelid     Channel that us used for communication, i.e. phone of email
     * @return              True if addition has been done, false if an error has occured
     * @exception           Database error hase occured
     */
    public boolean networkAdd(java.net.URL urlconfignetwork, String networkname, long roleid, String source) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return networksql.add(connection, urlconfignetwork, networkname, roleid, source);
        } catch (CRMNetworkException ne) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, ne.getMessage(), ne);
            return false;
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param networkid Network id
     * @param roleid    Role id of person that needs to be removed from the network
     * @param channelid Channel that us used for communication, i.e. phone of email
     * @param id        Id that uniquely identifies the network entry, i.e. a email address or a phone number
     * @return          True if removal has been done, false if an error has occured
     * @exception       Database error hase occured
     */
    public boolean networkEnd(java.net.URL urlconfignetwork, long networkid, long roleid) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return networksql.end(connection, urlconfignetwork, networkid, roleid); 
        } catch (CRMNetworkException ne) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, ne.getMessage(), ne);
            return false;
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param networkname   Network name
     * @param roleid        Role id of person that needs to be removed from the network
     * @param channelid     Channel that us used for communication, i.e. phone of email
     * @param id            Id that uniquely identifies the network entry, i.e. a email address or a phone number
     * @return              True if removal has been done, false if an error has occured
     * @exception           Database error hase occured
     */
    public boolean networkEnd(java.net.URL urlconfignetwork, String networkname, long roleid) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return networksql.end(connection, urlconfignetwork, networkname, roleid);
        } catch (CRMNetworkException ne) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, ne.getMessage(), ne);
            return false;
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param urlconfignetwork
     * @param networkid
     * @param roleid
     * @return 
     */
    public boolean networkPartof(java.net.URL urlconfignetwork, long networkid, long roleid) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return networksql.partof(connection, urlconfignetwork, networkid, roleid); 
        } catch (CRMNetworkException ne) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, ne.getMessage(), ne);
            return false;
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            closeSQL(database, connection);
        }
    }
    
    /**
     * 
     * @param urlconfignetwork
     * @param networkname
     * @param roleid
     * @return 
     */
    public boolean networkPartof(java.net.URL urlconfignetwork, String networkname, long roleid) {
        Database database = null;
        Connection connection = null;
        try {
            database = new Database(Database.DB_CRM_JNDI_NAME);
            connection = database.open();
            NetworkSQL networksql = new NetworkSQL();
            return networksql.partof(connection, urlconfignetwork, networkname, roleid);
        } catch (CRMNetworkException ne) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, ne.getMessage(), ne);
            return false;
        } catch (Exception e) {
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally {
            closeSQL(database, connection);
        }
    }

    /**
     * 
     * @param database      CRM database object
     * @param connection    SQL connection
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
            Logger.getLogger(EMNetwork.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }
}
