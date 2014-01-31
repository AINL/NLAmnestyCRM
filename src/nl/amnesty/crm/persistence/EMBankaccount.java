package nl.amnesty.crm.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.db.Database;
import nl.amnesty.crm.entity.Bankaccount;
import nl.amnesty.crm.sql.BankaccountSQL;

/**
 *
 * @author Amnesty International Dutch Section
 * @version 1.00
 */
public class EMBankaccount implements EntityManager {

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
            BankaccountSQL bankaccountsql = new BankaccountSQL();
            return (T) bankaccountsql.create(connection, (Bankaccount) t);
        } catch (Exception e) {
            Logger.getLogger(EMBankaccount.class.getName()).log(Level.SEVERE, null, e);
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
            BankaccountSQL bankaccountsql = new BankaccountSQL();
            return (T) bankaccountsql.read(connection, id);
        } catch (Exception e) {
            Logger.getLogger(EMBankaccount.class.getName()).log(Level.SEVERE, null, e);
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
            BankaccountSQL bankaccountsql = new BankaccountSQL();
            return bankaccountsql.update(connection, (Bankaccount) t);
        } catch (Exception e) {
            Logger.getLogger(EMBankaccount.class.getName()).log(Level.SEVERE, null, e);
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
            BankaccountSQL bankaccountsql = new BankaccountSQL();
            return bankaccountsql.delete(connection, id);
        } catch (Exception e) {
            Logger.getLogger(EMBankaccount.class.getName()).log(Level.SEVERE, null, e);
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
            BankaccountSQL bankaccountsql = new BankaccountSQL();
            return (T) bankaccountsql.match(connection, (Bankaccount) t);
        } catch (Exception e) {
            Logger.getLogger(EMBankaccount.class.getName()).log(Level.SEVERE, null, e);
            return null;
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
            Logger.getLogger(EMBankaccount.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }

    public <T> T find(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
