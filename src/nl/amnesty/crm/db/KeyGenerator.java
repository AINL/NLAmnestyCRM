package nl.amnesty.crm.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The KeyGenerator class allows access to the stored procedures within the
 * Progress CRM system for generating the next available value for use with
 * primary keys of Progress'entities. The generated key can be of the following
 * types:
 *
 * KeyGenerator.KEY_NEXTRECNO KeyGenerator.KEY_NEXTPEOPKEY
 * KeyGenerator.KEY_NEXTADDKEY KeyGenerator.KEY_NEXTGENKEY
 *
 * @author evelzen
 * @version 1.00
 */
public class KeyGenerator {

    public final static String KEY_NEXTRECNO = "NXTRECNO";
    public final static String KEY_NEXTPEOPKEY = "NXTPEOPKEY";
    public final static String KEY_NEXTADDKEY = "NXTADDKEY";
    public final static String KEY_NEXTBANKKEY = "NXTBANKKEY";
    public final static String KEY_NEXTMEMNO = "NXTMEMNO";
    public final static String KEY_NEXTGENKEY = "NXTGENKEY";
    public final static String KEY_NEXTPAYKEY = "NXTPAYKEY";
    private Database database;
    private Connection connection;

    /**
     * Default constructor
     */
    public KeyGenerator() {
    }

    /**
     * Overloaded constructor that instantiates a KeyGenerator object based on
     * the current java.sql.Connection type connection.
     *
     * @param connection
     * @since 1.00
     */
    public KeyGenerator(Connection connection) {
        this.connection = connection;
    }

    /**
     * Overloaded constructor that instantiates a KeyGenerator object based on
     * the current database and java.sql.Connection type connection.
     *
     * @param database
     * @param connection
     * @since 1.00
     */
    public KeyGenerator(Database database, Connection connection) {
        this.database = database;
        this.connection = connection;
    }

    /**
     * Generic method to get the next available primary key value, based on the
     * identity parameter that calls the specific stored procedure for
     * generating the key. Possible calls include the following identities:
     *
     * KeyGenerator.KEY_NEXTRECNO KeyGenerator.KEY_NEXTPEOPKEY
     * KeyGenerator.KEY_NEXTADDKEY KeyGenerator.KEY_NEXTGENKEY
     *
     * @param identity of type String
     * @return gerenated key value of type int
     * @since 1.00
     */
    public int getNextKey(String identity) {
        try {
            if (identity.compareTo(KEY_NEXTRECNO) == 0) {
                return getKey(KEY_NEXTRECNO);
            } else if (identity.compareTo(KEY_NEXTPEOPKEY) == 0) {
                return getKey(KEY_NEXTPEOPKEY);
            } else if (identity.compareTo(KEY_NEXTADDKEY) == 0) {
                return getKey(KEY_NEXTADDKEY);
            } else if (identity.compareTo(KEY_NEXTBANKKEY) == 0) {
                return getKey(KEY_NEXTBANKKEY);
            } else if (identity.compareTo(KEY_NEXTMEMNO) == 0) {
                return getKey(KEY_NEXTMEMNO);
            } else if (identity.compareTo(KEY_NEXTGENKEY) == 0) {
                return getKey(KEY_NEXTGENKEY);
            } else if (identity.compareTo(KEY_NEXTPAYKEY) == 0) {
                return getKey(KEY_NEXTPAYKEY);
            } else {
                Logger.getLogger(KeyGenerator.class.getName()).log(Level.SEVERE, "Unsupported key generation request.");
                return 0;
            }
        } catch (Exception e) {
            Logger.getLogger(KeyGenerator.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Calls a SQLServer stored procedure based on the keytype, which can be one
     * of the following:
     *
     * KeyGenerator.KEY_NEXTRECNO KeyGenerator.KEY_NEXTPEOPKEY
     * KeyGenerator.KEY_NEXTADDKEY KeyGenerator.KEY_NEXTGENKEY
     *
     * Normally this private method would not be called directly but from the
     * getNextKey() method.
     *
     * @param keytype of type String
     * @return next p;rimary key value as an int
     * @since 1.00
     * @see #getNextKey(String identity)
     */
    private int getKey(final String keytype) {
        int newkey;
        CallableStatement proc = null;
        try {
            proc = this.connection.prepareCall("{ call PROG_FN_GET_CONTROL_KEY(" + keytype + ",1,?) }");
            if (proc != null) {
                proc.registerOutParameter(1, Types.INTEGER);
                proc.execute();
                newkey = proc.getInt(1);
                proc.close();
                return newkey;
            } else {
                return 0;
            }
        } catch (final SQLException e) {
            Logger.getLogger(KeyGenerator.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return 0;
        }
    }
}
