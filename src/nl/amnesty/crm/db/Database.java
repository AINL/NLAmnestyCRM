package nl.amnesty.crm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author ed
 */
public class Database {

    public static final String DB_CRM_JNDI_NAME = "jdbc/FCRM";
    //
    public static final int DB_TYPE_MYSQL = 0;
    public static final int DB_TYPE_MSSQL = 1;
    private static final String CLASSNAME_MYSQL = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
    private static final String CLASSNAME_MSSQL = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String RESOURCETYPE_DATASOURCE = "javax.sql.DataSource";
    private static final String RESOURCETYPE_CONNECTIONPOOL = "javax.sql.ConnectionPoolDataSource";
    private static final String URLPREFIX_MYSQL = "jdbc:mysql:";
    private static final String URLPREFIX_MSSQL = "jdbc:sqlserver:";
    private static final int PORT_MYSQL = 3306;
    private static final int PORT_MSSQL = 1433;
    //
    public final static int OPERATION_CREATE = 1;
    public final static int OPERATION_READ = 2;
    public final static int OPERATION_UPDATE = 3;
    public final static int OPERATION_DELETE = 4;
    //
    private int dbvendor;
    private String classname;
    private String resourcetype;
    private String url;
    private String hostname;
    private String instance;
    private int portnumber;
    private String databasename;
    private String username;
    private String password;
    private Connection connection;
    private String resourcename;

    public static String operationDescription(int operationcode) {
        switch (operationcode) {
            case OPERATION_CREATE:
                return "Read";
            case OPERATION_READ:
                return "Read";
            case OPERATION_UPDATE:
                return "Read";
            case OPERATION_DELETE:
                return "Read";
            default:
                return "Unsupported operation";
        }
    }

    /**
     * Default constructor
     *
     */
    public Database() {
    }

    /**
     * Overloaded constructor to specify database type, name and logon
     * credentials. The constructor sets the following properties: classname
     * resourcetype portnumber url The constructor generates a valid connection
     * URL based on the parameters given. Finally, it sets a connection and
     * stores it in its private connection property.
     *
     * @param dbvendor Database type either Database.DB_TYPE_MYSQL or
     * Database.DB_TYPE_MSSQL
     * @param hostname Database host machine name
     * @param databasename Database name
     * @param username Username for access to database
     * @param password Password for access to database
     */
    public Database(int dbvendor, String hostname, String instance, int portnumber, String databasename, String username, String password) {
        this.dbvendor = dbvendor;
        this.hostname = hostname;
        this.instance = instance;
        this.portnumber = portnumber;
        this.databasename = databasename;
        this.username = username;
        this.password = password;

        switch (dbvendor) {
            case DB_TYPE_MYSQL:
                this.setClassname(CLASSNAME_MYSQL);
                this.setResourcetype(RESOURCETYPE_DATASOURCE);
                if (portnumber == 0) {
                    this.setPortnumber(PORT_MYSQL);
                }
                if (instance != null) {
                    if (instance.isEmpty()) {
                        this.setUrl(URLPREFIX_MYSQL.concat("//").concat(this.getHostname()).concat("\\").concat(instance).concat(":").concat(String.valueOf(this.getPortnumber())).concat("/").concat(this.getDatabasename().concat("?user=").concat(username).concat("&password=").concat(password)));
                    } else {
                        this.setUrl(URLPREFIX_MYSQL.concat("//").concat(this.getHostname()).concat(":").concat(String.valueOf(this.getPortnumber())).concat("/").concat(this.getDatabasename().concat("?user=").concat(username).concat("&password=").concat(password)));
                    }
                } else {
                    this.setUrl(URLPREFIX_MYSQL.concat("//").concat(this.getHostname()).concat(":").concat(String.valueOf(this.getPortnumber())).concat("/").concat(this.getDatabasename().concat("?user=").concat(username).concat("&password=").concat(password)));
                }
                break;
            case DB_TYPE_MSSQL:
                this.setClassname(CLASSNAME_MSSQL);
                this.setResourcetype(RESOURCETYPE_DATASOURCE);
                if (portnumber == 0) {
                    this.setPortnumber(PORT_MSSQL);
                }
                if (instance != null) {
                    if (instance.isEmpty()) {
                        this.setUrl(URLPREFIX_MSSQL.concat("//").concat(this.getHostname()).concat("\\").concat(instance).concat(":").concat(String.valueOf(this.getPortnumber())).concat(";databaseName=").concat(this.getDatabasename()).concat(";user=").concat(this.getUsername().concat(";password=").concat(this.password)));
                    } else {
                        this.setUrl(URLPREFIX_MSSQL.concat("//").concat(this.getHostname()).concat(":").concat(String.valueOf(this.getPortnumber())).concat(";databaseName=").concat(this.getDatabasename()).concat(";user=").concat(this.getUsername().concat(";password=").concat(this.password)));
                    }
                } else {
                    this.setUrl(URLPREFIX_MSSQL.concat("//").concat(this.getHostname()).concat(":").concat(String.valueOf(this.getPortnumber())).concat(";databaseName=").concat(this.getDatabasename()).concat(";user=").concat(this.getUsername().concat(";password=").concat(this.password)));
                }
                break;
            default:
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, "Unsupported database vendor");
                break;
        }
        this.connection = getConnectionViaDriverManager();
    }

    /**
     * Overloaded constructor for establishing a database connection via the
     * resourcename. The constructor stores a open connection to the database in
     * the private connection property.
     *
     * @param resourcename
     */
    public Database(String resourcename) {
        this.resourcename = resourcename;

        this.connection = getConnectionViaResource();
    }

    /**
     * Get a connection to the database via a resourcename lookup. The
     * resourcename is taken from the private property resourcename and should
     * be set using one of the applicable constructors
     *
     * @return java.sql.Connection type connection to the database
     * @since 1.00
     */
    private Connection getConnectionViaResource() {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(this.resourcename);
            return ds.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (NamingException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Get a connection to the database via a connection URL and the driver
     * manager. The URL is taken from the private property url and should be set
     * using the appropriate constructor.
     *
     * @return java.sql.Connection type connection to the database
     * @since 1.00
     */
    private Connection getConnectionViaDriverManager() {
        try {
            Class.forName(this.getClassname());
            Connection con = null;
            try {
                con = DriverManager.getConnection(this.getUrl());
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
            return con;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Open the connection to the database. The connection itself prior to this
     * call should be set by using either the getConnectionViaResource() or
     * getConnectionViaDriverManager() method.
     *
     * @return an open java.sql.Connection type connection to the database
     * @since 1.00
     * @see #getConnectionViaResource()
     * @see #getConnectionViaDriverManager()
     */
    public Connection open() {
        if (this.getConnection() == null) {
            return null;
        } else {
            return this.getConnection();
        }
    }

    /**
     * Determines if the database is closed.
     *
     * @return
     * @since 1.00
     */
    public boolean isClosed() {
        try {
            if (this.getConnection() == null) {
                return true;
            } else {
                if (this.getConnection().isClosed()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
    }

    /**
     * Close the connection to the database. The database must have been opened
     * prior to this call.
     *
     * @return a closed java.sql.Connection type connection to the database
     * @since 1.00
     */
    public Connection close() {
        if (this.getConnection() == null) {
            return null;
        } else {
            try {
                this.getConnection().close();
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
            return this.getConnection();
        }
    }

    /**
     * Get classname property
     *
     * @return classname of type String
     */
    public String getClassname() {
        return classname;
    }

    /**
     * Set classname property
     *
     * @param classname Classname of type String
     */
    private void setClassname(String classname) {
        this.classname = classname;
    }

    /**
     * get databasename property
     *
     * @return databasename of type String
     */
    private String getDatabasename() {
        return databasename;
    }

    /**
     * set database name property
     *
     * @param databasename of type String
     */
    public void setDatabasename(String databasename) {
        this.databasename = databasename;
    }

    /**
     * Get the dbvendor property
     *
     * @return dbvendor of type String
     */
    public int getDbvendor() {
        return dbvendor;
    }

    /**
     * Set the dbvendor property
     *
     * @param dbvendor of type String
     */
    public void setDbvendor(int dbvendor) {
        this.dbvendor = dbvendor;
    }

    /**
     * Get hostname property
     *
     * @return hostname of type String
     */
    private String getHostname() {
        return hostname;
    }

    /**
     * Set the hostname property
     *
     * @param hostname of type String
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Get password property
     *
     * @return password of type String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password property
     *
     * @param password of type String
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get portnumber property
     *
     * @return portnumber of type String
     */
    private int getPortnumber() {
        return portnumber;
    }

    /**
     * Set portnumber property
     *
     * @param portnumber of type String
     */
    private void setPortnumber(int portnumber) {
        this.portnumber = portnumber;
    }

    /**
     * Get resourcetype property
     *
     * @return resourcetype of type String
     */
    public String getResourcetype() {
        return resourcetype;
    }

    /**
     * Set resourcetype property
     *
     * @param resourcetype of type String
     */
    private void setResourcetype(String resourcetype) {
        this.resourcetype = resourcetype;
    }

    /**
     * Get the url property
     *
     * @return url of type String
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the url property
     *
     * @param url of type String
     */
    private void setUrl(String url) {
        this.url = url;
    }

    /**
     * Get the username property
     *
     * @return username of type String
     */
    private String getUsername() {
        return username;
    }

    /**
     * Set the username property
     *
     * @param username of type String
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the connection to the database
     *
     * @return connection of type java.sql.Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Set the connection to the database
     *
     * @param connection of type java.sql.Connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getResourcename() {
        return resourcename;
    }

    public void setResourcename(String resourcename) {
        this.resourcename = resourcename;
    }
}
