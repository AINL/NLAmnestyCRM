package nl.amnesty.crm.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.db.KeyGenerator;
import nl.amnesty.crm.entity.Role;
import nl.amnesty.crm.entity.URL;
import nl.amnesty.crm.exception.CRMURLException;

/**
 *
 * @author ed
 */
public class URLSQL {
    /*
     * Standard CRUD Methods
     */

    public URL create(Connection connection, URL url) throws CRMURLException {
        if (url.getRoleid() == 0) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, "Roleid for URL is 0");
            return null;
        }
        if (url.getProtocol().equals(URL.PROTOCOL_EMAIL)) {
            return createEmail(connection, url);
        }
        if (url.getProtocol().equals(URL.PROTOCOL_FTP)) {
            //TODO: Implementation pending...
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, "Unsupported URL protocol '" + URL.PROTOCOL_FTP + "'");
            return null;
        }
        if (url.getProtocol().equals(URL.PROTOCOL_HTTP)) {
            //TODO: Implementation pending...
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, "Unsupported URL protocol '" + URL.PROTOCOL_HTTP + "'");
            return null;
        }
        if (url.getProtocol().equals(URL.PROTOCOL_HTTPS)) {
            //TODO: Implementation pending...
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, "Unsupported URL protocol '" + URL.PROTOCOL_HTTPS + "'");
            return null;
        }
        if (url.getProtocol().equals(URL.PROTOCOL_SMTP)) {
            //TODO: Implementation pending...
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, "Unsupported URL protocol '" + URL.PROTOCOL_SMTP + "'");
            return null;
        }
        Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, "Unsupported URL protocol '{0}'", url.getProtocol());
        return null;
    }

    public URL read(Connection connection, long urlid) throws CRMURLException {
        // TODO: Implementation for updating url, this is just a stub
        return readEmail(connection, urlid);
    }

    public boolean update(Connection connection, URL url) {
        // TODO: Implementation for updating url, this is just a stub
        return false;
    }

    public boolean updateType(Connection connection, URL url, String type) throws CRMURLException {
        if (url == null) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, "URL is null");
            return false;
        }
        if (url.getUrlid() == 0) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, "URL id is 0");
            return false;            
        }
        Statement statement = null;
        ResultSet resultset = null;
        String sql;
        try {
            type=type.replace(";","").replace("%","").replace("&", "");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "UPDATE contnos SET " + URL.FIELD_CONTNOS_TYPE + "=" + "'" + type + "' WHERE " + URL.FIELD_CONTNOS_ID + "=" + url.getUrlid();
            int rowcount = statement.executeUpdate(sql);
            if (rowcount > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMURLException(MessageFormat.format("URL SQL exception for urlid {0}", new Object[]{url.getUrlid()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMURLException(MessageFormat.format("URL exception for urlid {0}", new Object[]{url.getUrlid()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public boolean delete(Connection connection, long urlid) throws CRMURLException {
        Statement statement = null;
        ResultSet resultset = null;
        String sql;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "UPDATE contnos SET " + URL.FIELD_CONTNOS_TYPE + "=" + "'DELETED' WHERE " + URL.FIELD_CONTNOS_ID + "=" + urlid;
            int rowcount = statement.executeUpdate(sql);
            if (rowcount > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMURLException(MessageFormat.format("URL SQL exception for urlid {0}", new Object[]{urlid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMURLException(MessageFormat.format("URL exception for urlid {0}", new Object[]{urlid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     *
     * Object matching
     *
     */
    public URL match(Connection connection, URL url) throws CRMURLException {
        if (url.getProtocol().equals(URL.PROTOCOL_EMAIL)) {
            return matchEmail(connection, url);
        }
        if (url.getProtocol().equals(URL.PROTOCOL_FTP)) {
            url.setStatus(URL.STATUS_MATCHED_NONE);
            return url;
        }
        if (url.getProtocol().equals(URL.PROTOCOL_HTTP)) {
            url.setStatus(URL.STATUS_MATCHED_NONE);
            return url;
        }
        if (url.getProtocol().equals(URL.PROTOCOL_HTTPS)) {
            url.setStatus(URL.STATUS_MATCHED_NONE);
            return url;
        }
        if (url.getProtocol().equals(URL.PROTOCOL_SMTP)) {
            url.setStatus(URL.STATUS_MATCHED_NONE);
            return url;
        }
        return url;
    }

    private URL matchEmail(Connection connection, URL url) throws CRMURLException {
        List<URL> urlfoundlist;
        URL urlfound;
        try {
            // First of all let's see if url can be found in CRM via the url id
            if (url.getUrlid() != 0) {
                urlfound = readEmail(connection, url.getUrlid());
                if (urlfound != null) {
                    urlfound.setStatus(URL.STATUS_MATCHED_ID);
                    return urlfound;
                }
            }
            // Next best thing is to search for the url internetaddress
            if (!url.getInternetAddress().isEmpty()) {
                urlfoundlist = readEmailViaAddress(connection, url.getInternetAddress());
                if (urlfoundlist != null) {
                    if (!urlfoundlist.isEmpty()) {
                        urlfound = urlfoundlist.get(0);
                        urlfound.setStatus(URL.STATUS_MATCHED_EMAIL);
                        return urlfound;
                    }
                }
            }
            url.setStatus(URL.STATUS_MATCHED_NONE);
            return url;
        } catch (Exception e) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMURLException(MessageFormat.format("URL exception for urlid {0} internetaddress {1}", new Object[]{url.getUrlid(), url.getInternetAddress()}), e);
        }
    }

    /*
     *
     * CREATE Methods
     *
     */
    private URL createEmail(Connection connection, URL url) throws CRMURLException {
        Statement statement = null;
        ResultSet resultset = null;
        URL urlmatched;
        long id = 0;
        if (url == null) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, "URL is null");
            return null;
        }
        if (!url.isInternetAddress()) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.WARNING, "URL is invalid");
            return new URL();
        }
        if (url.getRoleid() == 0) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, "Roleid for URL is 0");
            return null;
        }
        try {
            // Try to find the e-mail address for this role
            urlmatched = matchEmail(connection, url);
            if (urlmatched != null) {
                if (urlmatched.isAcceptableMatch()) {
                    return urlmatched;
                }
            }

            // At this point we have not found a matching e-mail address in CRM so let's create a new one.
            String SQL = "SELECT * FROM contnos";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();

            setResultsetColumns(connection, resultset, url);

            if (url.getUrlid() == 0) {
                KeyGenerator keygenerator = new KeyGenerator(connection);
                id = keygenerator.getNextKey(KeyGenerator.KEY_NEXTGENKEY);
                url.setUrlid((long) id);
            }
            resultset.updateInt(URL.FIELD_CONTNOS_ID, (int) id);

            resultset.insertRow();
            url.setStatus(URL.STATUS_NEW);
            return url;
        } catch (SQLException sqle) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMURLException(MessageFormat.format("URL SQL exception for urlid {0} internetaddress {1}", new Object[]{url.getUrlid(), url.getInternetAddress()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMURLException(MessageFormat.format("URL exception for urlid {0} internetaddress {1}", new Object[]{url.getUrlid(), url.getInternetAddress()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     * READ Methods
     */
    public URL readEmail(Connection connection, long urlid) throws CRMURLException {
        String type;
        String address;
        URL urlfound = null;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            String query = "SELECT * FROM contnos c WHERE c.cokey=" + urlid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    type = resultset.getString(URL.FIELD_CONTNOS_TYPE);
                    long roleid = resultset.getInt(URL.FIELD_CONTNOS_ROLEID);
                    address = resultset.getString(URL.FIELD_CONTNOS_ADDRESS);
                    if (address.contains("@")) {
                        // Strip < > characters
                        address = trimEmail(address);

                        Calendar calendardate = Calendar.getInstance();
                        java.sql.Date sqldate = resultset.getDate(URL.FIELD_CONTNOS_DATEADDED);
                        if (sqldate != null) {
                            calendardate.setTimeInMillis(sqldate.getTime());
                        } else {
                            calendardate.set(1900, Calendar.JANUARY, 1);
                        }

                        if (type.compareTo(URL.TYPE_EMAIL_HOME) == 0) {
                            URL url = new URL();
                            url.setDomain(address.substring(address.indexOf("@") + 1));
                            url.setFragment("");
                            url.setPassword("");
                            url.setPort(0);
                            url.setProtocol("email:");
                            url.setQuery("");
                            url.setRoleid(roleid);
                            url.setUrlid(resultset.getInt(URL.FIELD_CONTNOS_ID));
                            url.setUsername(address.substring(0, address.indexOf("@")));
                            url.setDateadded(calendardate.getTime());
                            urlfound = url;
                        }
                        if (type.compareTo(URL.TYPE_EMAIL_WORK) == 0) {
                            URL url = new URL();
                            url.setDomain(address.substring(address.indexOf("@") + 1));
                            url.setFragment("");
                            url.setPassword("");
                            url.setPort(0);
                            url.setProtocol("email:");
                            url.setQuery("");
                            url.setRoleid(roleid);
                            url.setUrlid(resultset.getInt(URL.FIELD_CONTNOS_ID));
                            url.setUsername(address.substring(0, address.indexOf("@")));
                            url.setDateadded(calendardate.getTime());
                            urlfound = url;
                        }
                        if (type.compareTo(URL.TYPE_EMAIL_ERROR) == 0) {
                            // Not a valid e-mail address
                        }
                    }
                } while (resultset.next());
                return urlfound;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMURLException(MessageFormat.format("URL SQL exception for urlid {0}", new Object[]{urlid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMURLException(MessageFormat.format("URL exception for urlid {0}", new Object[]{urlid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public List<URL> readEmailViaAddress(Connection connection, String email) throws CRMURLException {
        List<URL> urllist = new ArrayList();
        Statement statement = null;
        ResultSet resultset = null;
        String type;
        String address;
        try {
            email=email.replace(";","").replace("%","").replace("&", "");
            String query = "SELECT * FROM contnos c WHERE c.contactno='" + email + "' ORDER BY dateadded DESC";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    type = resultset.getString(URL.FIELD_CONTNOS_TYPE);
                    long roleid = resultset.getInt(URL.FIELD_CONTNOS_ROLEID);
                    address = resultset.getString(URL.FIELD_CONTNOS_ADDRESS);
                    if (address.contains("@")) {
                        // Strip < > characters
                        address = trimEmail(address);

                        Calendar calendardate = Calendar.getInstance();
                        java.sql.Date sqldate = resultset.getDate(URL.FIELD_CONTNOS_DATEADDED);
                        if (sqldate != null) {
                            calendardate.setTimeInMillis(sqldate.getTime());
                        } else {
                            calendardate.set(1900, Calendar.JANUARY, 1);
                        }

                        if (type.compareTo(URL.TYPE_EMAIL_HOME) == 0) {
                            URL url = new URL();
                            url.setDomain(address.substring(address.indexOf("@") + 1));
                            url.setFragment("");
                            url.setPassword("");
                            url.setPort(0);
                            url.setProtocol("email:");
                            url.setQuery("");
                            url.setRoleid(roleid);
                            url.setUrlid(resultset.getInt(URL.FIELD_CONTNOS_ID));
                            url.setUsername(address.substring(0, address.indexOf("@")));
                            url.setDateadded(calendardate.getTime());
                            urllist.add(url);
                        }
                        if (type.compareTo(URL.TYPE_EMAIL_WORK) == 0) {
                            URL url = new URL();
                            url.setDomain(address.substring(address.indexOf("@") + 1));
                            url.setFragment("");
                            url.setPassword("");
                            url.setPort(0);
                            url.setProtocol("email:");
                            url.setQuery("");
                            url.setRoleid(roleid);
                            url.setUrlid(resultset.getInt(URL.FIELD_CONTNOS_ID));
                            url.setUsername(address.substring(0, address.indexOf("@")));
                            url.setDateadded(calendardate.getTime());
                            urllist.add(url);
                        }
                        if (type.compareTo(URL.TYPE_EMAIL_ERROR) == 0) {
                            // Not a valid e-mail address
                        }
                    }
                } while (resultset.next());
            }
            return urllist;
        } catch (SQLException sqle) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMURLException(MessageFormat.format("URL SQL exception for email {0}", new Object[]{email}), sqle);
        } catch (Exception e) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMURLException(MessageFormat.format("URL exception for email {0}", new Object[]{email}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public List<URL> readEmaillistViaRoleid(Connection connection, long roleid) throws CRMURLException {
        String type;
        String address;
        List<URL> urllist = new ArrayList();
        Statement statement = null;
        ResultSet resultset = null;
        try {
            String query = "SELECT * FROM contnos c WHERE c.pvkey=" + roleid + " ORDER BY dateadded desc";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    type = resultset.getString(URL.FIELD_CONTNOS_TYPE);
                    address = resultset.getString(URL.FIELD_CONTNOS_ADDRESS);

                        Calendar calendardate = Calendar.getInstance();
                        java.sql.Date sqldate = resultset.getDate(URL.FIELD_CONTNOS_DATEADDED);
                        if (sqldate != null) {
                            calendardate.setTimeInMillis(sqldate.getTime());
                        } else {
                            calendardate.set(1900, Calendar.JANUARY, 1);
                        }

                    if (address.contains("@")) {
                        // Strip < > characters
                        if (type.compareTo(URL.TYPE_EMAIL_HOME) == 0) {
                            URL url = new URL();
                            url.setDomain(address.substring(address.indexOf("@") + 1));
                            url.setFragment("");
                            url.setPassword("");
                            url.setPort(0);
                            url.setProtocol(URL.PROTOCOL_EMAIL);
                            url.setRoleid(roleid);
                            url.setQuery("");
                            url.setUrlid(resultset.getInt(URL.FIELD_CONTNOS_ID));
                            url.setUsername(address.substring(0, address.indexOf("@")));
                            url.setDateadded(calendardate.getTime());
                            urllist.add(url);
                        }
                        if (type.compareTo(URL.TYPE_EMAIL_WORK) == 0) {
                            URL url = new URL();
                            url.setDomain(address.substring(address.indexOf("@") + 1));
                            url.setFragment("");
                            url.setPassword("");
                            url.setPort(0);
                            url.setProtocol("email:");
                            url.setQuery("");
                            url.setRoleid(roleid);
                            url.setUrlid(resultset.getInt(URL.FIELD_CONTNOS_ID));
                            url.setUsername(address.substring(0, address.indexOf("@")));
                            url.setDateadded(calendardate.getTime());
                            urllist.add(url);
                        }
                        if (type.compareTo(URL.TYPE_EMAIL_ERROR) == 0) {
                            // Not a valid e-mail address
                        }
                    }
                } while (resultset.next());
                return urllist;
            } else {
                return urllist;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMURLException(MessageFormat.format("URL SQL exception for roleid {0}", new Object[]{roleid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMURLException(MessageFormat.format("URL exception for roleid {0}", new Object[]{roleid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }


    /*
     * Misc methods
     */
    private static ResultSet setResultsetColumns(Connection connection, ResultSet resultset, URL url) throws CRMURLException {
        try {
            Calendar c = Calendar.getInstance();
            resultset.updateDate(URL.FIELD_CONTNOS_DATEADDED, new java.sql.Date(c.getTimeInMillis()));

            resultset.updateString(URL.FIELD_CONTNOS_TYPE, URL.TYPE_EMAIL_HOME);
            String email = url.getUsername().concat("@").concat(url.getDomain());
            resultset.updateString(URL.FIELD_CONTNOS_ADDRESS, email);

            resultset.updateInt(URL.FIELD_CONTNOS_ROLEID, (int) url.getRoleid());
            RoleSQL rolesql = new RoleSQL();
            Role role = rolesql.read(connection, url.getRoleid());
            if (role != null) {
                resultset.updateInt(URL.FIELD_CONTNOS_ADDRESSID, (int) role.getAddress().getAddressid());
                resultset.updateInt(URL.FIELD_CONTNOS_PERSONID, (int) role.getPerson().getPersonid());
            }
            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMURLException(MessageFormat.format("URL SQL exception for urlid {0} internetaddress {1}", new Object[]{url.getUrlid(), url.getInternetAddress()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMURLException(MessageFormat.format("URL exception for urlid {0} internetaddress {1}", new Object[]{url.getUrlid(), url.getInternetAddress()}), e);
        }

    }

    private static void closeSQL(Statement statement, ResultSet resultset) {
        try {
            if (resultset != null) {
                if (!resultset.isClosed()) {
                    resultset.close();
                }
            }
            if (statement != null) {
                if (!statement.isClosed()) {
                    statement.close();
                }
            }
        } catch (SQLException sqle) {
            Logger.getLogger(URLSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }

    private String trimEmail(String email) {
        if (email.contains("@")) {
            if (email.contains("<") && email.contains(">")) {
                int beginindex = email.indexOf("<");
                int endindex = email.indexOf(">");
                email = email.substring(beginindex + 1, endindex);
            }
        } else {
            email = "";
        }
        return email;
    }
}
