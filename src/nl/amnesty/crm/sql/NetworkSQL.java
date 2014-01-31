package nl.amnesty.crm.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.collection.IdStartdateEnddate;
import nl.amnesty.crm.config.NetworkDef;
import nl.amnesty.crm.config.XMLParser;
import nl.amnesty.crm.db.KeyGenerator;
import nl.amnesty.crm.entity.Network;
import nl.amnesty.crm.entity.Phone;
import nl.amnesty.crm.entity.Role;
import nl.amnesty.crm.entity.URL;
import nl.amnesty.crm.exception.CRMNetworkException;

/**
 *
 *
 *
 * @author ed
 */
public class NetworkSQL {

    public static List<NetworkDef> networkdeflist;

    /*
     * Standard CRUD methods
     *
     */
    public Network create(Connection connection, Network network) {
        // TODO: Implementation for adding network, probably by adding entry to definition table
        return null;
    }

    public Network read(Connection connection, java.net.URL url, long networkid, String filter) throws CRMNetworkException {
        if (url == null) {
            return null;
        }
        if (filter == null) {
            return null;
        }
        try {
            NetworkDef networkdef = getNetworkDef(url, networkid);
            if (networkdef == null) {
                return null;
            }
            Network network = new Network();
            //String filter = "";
            Collection<IdStartdateEnddate> collection = readCollection(connection, networkdef, filter);
            if (collection != null) {
                network.setNetworkid(networkid);
                network.setName(networkdef.getName());
                network.setDescription(networkdef.getDescription());
                network.setIdlist(collection);
                return network;
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for networkid {0} subject {1}", new Object[]{networkid}), e);
        }
    }

    public Network read(Connection connection, java.net.URL url, String name, String filter) throws CRMNetworkException {
        if (url == null) {
            return null;
        }
        if (filter == null) {
            return null;
        }
        try {
            NetworkDef networkdef = getNetworkDef(url, name);
            if (networkdef == null) {
                return null;
            }
            Network network = new Network();
            //String filter = "";
            Collection<IdStartdateEnddate> collection = readCollection(connection, networkdef, filter);
            if (collection != null) {
                network.setNetworkid(networkdef.getNetworkid());
                network.setName(name);
                network.setDescription(networkdef.getDescription());
                network.setIdlist(collection);
                return network;
            } else {
                return null;
            }
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for name {0}", new Object[]{name}), e);
        }
    }

    public boolean update(Connection connection, Network network) {
        // TODO: Implementation for updating network, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long roleid) {
        // TODO: Implementation for deleting network, this is just a stub
        return false;
    }

    /*
     * Object matching
     */
    public Network match(Connection connection, Network network) {
        // TODO: Implementation for matching, this is just a stub
        network.setStatus(Network.STATUS_MATCHED_NONE);
        return network;
    }

    public boolean matchPartof(Connection connection, NetworkDef networkdef, long roleid) {
        try {
            return doPartof(connection, networkdef, roleid);
        } catch (CRMNetworkException ex) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /*
     * CREATE methods
     */
    public boolean add(Connection connection, java.net.URL urlconfignetwork, long networkid, long roleid, String source) throws CRMNetworkException {
        if (urlconfignetwork == null) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url is null, cannot search network definition for networkid {0}", networkid);
            return false;
        }
        if (urlconfignetwork.getHost().isEmpty()) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url hostname is empty, cannot search network definition for networkid {0}", networkid);
            return false;
        }
        if (networkid == 0) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Network id is 0");
            return false;
        }
        if (roleid == 0) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Role id is 0, cannot add to network with networkid {0}", networkid);
            return false;
        }
        try {
            NetworkDef networkdef = getNetworkDef(urlconfignetwork, networkid);
            if (networkdef == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "No network definition for network with networkid {0}", networkid);
                return false;
            }
            return doAdd(connection, networkdef, roleid, source);
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for networkid {0} roleid {1}", new Object[]{networkid, roleid}), e);
        }
    }

    public boolean add(Connection connection, java.net.URL urlconfignetwork, String name, long roleid, String source) throws CRMNetworkException {
        if (urlconfignetwork == null) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url is null, cannot search network definition for name {0}", name);
            return false;
        }
        if (urlconfignetwork.getHost().isEmpty()) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url hostname is empty, cannot search network definition for name {0}", name);
            return false;
        }
        if (name == null) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Network name is null");
            return false;
        }
        if (name.isEmpty()) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Network name is empty");
            return false;
        }
        if (roleid == 0) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Role id is 0, cannot add to network with name {0}", name);
            return false;
        }
        try {
            NetworkDef networkdef = getNetworkDef(urlconfignetwork, name);
            if (networkdef == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "No network definition for network with name {0}", name);
                return false;
            }
            return doAdd(connection, networkdef, roleid, source);
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for name {0} roleid {1}", new Object[]{name, roleid}), e);
        }
    }

    public boolean doAdd(Connection connection, NetworkDef networkdef, long roleid, String source) throws CRMNetworkException {
        boolean iserror = false;
        try {
            // Do not add if already part of network
            if (matchPartof(connection, networkdef, roleid)) {

                //DEBUG
                //Logger.getLogger(NetworkSQL.class.getName()).log(Level.INFO, "Role id {0} already part of network {1}", new Object[]{roleid, networkdef.getName()});

                return true;
            }
            List<String> sqllist = networkdef.getSql_add();
            for (String sql : sqllist) {
                if (!doAddStatement(connection, sql, roleid, source)) {
                    iserror = true;
                }
            }
            if (iserror) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for networkid {0} name {1} roleid {2}", new Object[]{networkdef.getNetworkid(), networkdef.getName(), roleid}), e);
        }
    }

    public boolean doAddStatement(Connection connection, String sql, long roleid, String source) throws CRMNetworkException {
        Statement statement = null;
        ResultSet resultset = null;
        try {
            if (sql == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doAddStatement() SQL statement is null");
                return false;
            }
            if (sql.isEmpty()) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doAddStatement() SQL statement is empty");
                return false;
            }
            sql = doSQLParameters(connection, sql, roleid, source);
            if (sql == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doAddStatement() SQL statement after applied parameters is null");
                return false;
            }
            if (roleid == 0) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doAddStatement() Roleid is 0");
                return false;
            }
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            int rowcount = statement.executeUpdate(sql);
            if (rowcount == 0) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMNetworkException(MessageFormat.format("Network SQL exception for roleid {0}", new Object[]{roleid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network SQL exception for roleid {0}", new Object[]{roleid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public boolean end(Connection connection, java.net.URL urlconfignetwork, long networkid, long roleid) throws CRMNetworkException {
        if (urlconfignetwork == null) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url is null, cannot search network definition for networkid {0}", networkid);
            return false;
        }
        if (urlconfignetwork.getHost().isEmpty()) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url hostname is empty, cannot search network definition for networkid {0}", networkid);
            return false;
        }
        if (networkid == 0) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Network id is 0");
            return false;
        }
        if (roleid == 0) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Role id is 0, cannot remove from network with networkid {0}", networkid);
            return false;
        }
        try {
            NetworkDef networkdef = getNetworkDef(urlconfignetwork, networkid);
            if (networkdef == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "No network definition for network with networkid {0}", networkid);
                return false;
            }
            return doEnd(connection, networkdef, roleid);
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for networkid {0} roleid {1}", new Object[]{networkid, roleid}), e);
        }

    }

    public boolean end(Connection connection, java.net.URL urlconfignetwork, String name, long roleid) throws CRMNetworkException {
        if (urlconfignetwork == null) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url is null, cannot search network definition for name {0}", name);
            return false;
        }
        if (urlconfignetwork.getHost().isEmpty()) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url hostname is empty, cannot search network definition for name {0}", name);
            return false;
        }
        if (name == null) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Network name is null");
            return false;
        }
        if (name.isEmpty()) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Network name is empty");
            return false;
        }
        if (roleid == 0) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Role id is 0, cannot remove from network with name {0}", name);
            return false;
        }
        try {
            NetworkDef networkdef = getNetworkDef(urlconfignetwork, name);
            if (networkdef == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "No network definition for network with name {0}", name);
                return false;
            }
            return doEnd(connection, networkdef, roleid);
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for name {0} roleid {1}", new Object[]{name, roleid}), e);
        }

    }

    public boolean doEnd(Connection connection, NetworkDef networkdef, long roleid) throws CRMNetworkException {
        boolean iserror = false;
        try {
            List<String> sqllist = networkdef.getSql_remove();
            for (String sql : sqllist) {
                if (!doEndStatement(connection, sql, roleid)) {
                    iserror = true;
                }
            }
            if (iserror) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for networkid {0} name {1} roleid {2}", new Object[]{networkdef.getNetworkid(), networkdef.getName(), roleid}), e);
        }
    }

    public boolean doEndStatement(Connection connection, String sql, long roleid) throws CRMNetworkException {
        Statement statement = null;
        ResultSet resultset = null;
        try {
            if (sql == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doEndStatement() SQL statement is null");
                return false;
            }
            if (sql.isEmpty()) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doEndStatement() SQL statement is empty");
                return false;
            }
            String source = "";
            sql = doSQLParameters(connection, sql, roleid, source);

            //DEBUG
            //Logger.getLogger(NetworkSQL.class.getName()).log(Level.INFO, sql);

            if (sql == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doEndStatement() SQL statement after applied parameters is null");
                return false;
            }
            if (roleid == 0) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doEndStatement() Roleid is 0");
                return false;
            }
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            int rowcount = statement.executeUpdate(sql);
            if (rowcount == 0) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMNetworkException(MessageFormat.format("Network SQL exception for roleid {0}", new Object[]{roleid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for roleid {0}", new Object[]{roleid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public boolean partof(Connection connection, java.net.URL urlconfignetwork, long networkid, long roleid) throws CRMNetworkException {
        if (urlconfignetwork == null) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url is null, cannot search network definition for networkid {0}", networkid);
            return false;
        }
        if (urlconfignetwork.getHost().isEmpty()) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url hostname is empty, cannot search network definition for networkid {0}", networkid);
            return false;
        }
        if (networkid == 0) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Network id is 0");
            return false;
        }
        if (roleid == 0) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Cannot test if role with id 0 is part of network with networkid {0}", networkid);
            return false;
        }
        try {
            NetworkDef networkdef = getNetworkDef(urlconfignetwork, networkid);
            if (networkdef == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "No network definition for network with networkid {0}", networkid);
                return false;
            }
            return doPartof(connection, networkdef, roleid);
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for networkid {0} roleid {1}", new Object[]{networkid, roleid}), e);
        }

    }

    public boolean partof(Connection connection, java.net.URL urlconfignetwork, String name, long roleid) throws CRMNetworkException {
        if (urlconfignetwork == null) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url is null, cannot search network definition for name {0}", name);
            return false;
        }
        if (urlconfignetwork.getHost().isEmpty()) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Config url hostname is empty, cannot search network definition for name {0}", name);
            return false;
        }
        if (name == null) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Network name is null");
            return false;
        }
        if (name.isEmpty()) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Network name is empty");
            return false;
        }
        if (roleid == 0) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "Cannot test if role with id 0 is part of network with name {0}", name);
            return false;
        }
        try {
            NetworkDef networkdef = getNetworkDef(urlconfignetwork, name);
            if (networkdef == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "No network definition for network with name {0}", name);
                return false;
            }
            return doPartof(connection, networkdef, roleid);
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for name {0} roleid {1}", new Object[]{name, roleid}), e);
        }

    }

    public boolean doPartof(Connection connection, NetworkDef networkdef, long roleid) throws CRMNetworkException {
        boolean notpartof = false;
        try {
            List<String> sqllist = networkdef.getSql_partof();
            for (String sql : sqllist) {
                if (!doPartofQuery(connection, sql, roleid)) {
                    notpartof = true;
                }
            }
            if (notpartof) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for networkid {0} name {1} roleid {2}", new Object[]{networkdef.getNetworkid(), networkdef.getName(), roleid}), e);
        }
    }

    public boolean doPartofQuery(Connection connection, String query, long roleid) throws CRMNetworkException {
        Statement statement = null;
        ResultSet resultset = null;
        try {
            if (query == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doPartofQuery() SQL query is null");
                return false;
            }
            if (query.isEmpty()) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doPartofQuery() SQL query is empty");
                return false;
            }
            String source = "";
            query = doSQLParameters(connection, query, roleid, source);
            if (query == null) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doPartofQuery() SQL query after applied parameters is null");
                return false;
            }
            if (roleid == 0) {
                Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, "doPartofQuery() Roleid is 0");
                return false;
            }

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMNetworkException(MessageFormat.format("Network SQL exception for roleid {0}", new Object[]{roleid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network SQL exception for roleid {0}", new Object[]{roleid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     * READ methods
     */
    private static Collection<IdStartdateEnddate> readCollection(Connection connection, NetworkDef networkdef, String filter) throws CRMNetworkException {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        Collection<IdStartdateEnddate> collection = new ArrayList();
        //DEBUG
        int countpassive = 0;
        int countactive = 0;
        int countpassivenoid = 0;
        int countactivenoid = 0;
        try {

            // DEBUG
            //System.out.println("readCollection networkid: " + networkdef.getName() + " " + networkdef.getDescription());

            List<String> querylist = networkdef.getSql_read();
            if (querylist == null) {
                return null;
            }
            if (querylist.isEmpty()) {
                return null;
            }
            if (querylist.get(0) == null) {
                return null;
            }
            if (querylist.get(0).isEmpty()) {
                return null;
            }
            if (filter == null) {
                return null;
            }

            query = querylist.get(0);
            if (!filter.isEmpty()) {
                query = query.replace("%filter", filter);
            }

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                long roleid = resultset.getInt("roleid");
                String id = resultset.getString("id");
                Date startdate = resultset.getTimestamp("startdate");
                Date enddate = null;
                Object object = resultset.getObject("enddate");
                if (object != null) {
                    enddate = resultset.getTimestamp("enddate");
                }

                if (id != null) {
                    if (id.isEmpty()) {
                        RoleSQL rolesql = new RoleSQL();
                        Role role = rolesql.read(connection, roleid);
                        if (role != null) {
                            if (networkdef.getMediatype().equals("EMAIL")) {
                                URL url = role.getEmail();
                                if (url != null) {
                                    id = url.getInternetAddress();
                                }
                            }
                            if (networkdef.getMediatype().equals("PHONE")) {
                                //Phone phone = role.getPhone();
                                Phone phone = role.getMobilePhone();
                                if (phone != null) {
                                    id = phone.getFormattedNumber();
                                }
                            }
                        }
                    }
                    if (id.isEmpty()) {
                        if (enddate == null) {
                            countactivenoid++;
                            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, MessageFormat.format("Unable to get id for roleid {0}", new Object[]{roleid}));
                        } else {
                            countpassivenoid++;
                        }
                    } else {
                        IdStartdateEnddate element = new IdStartdateEnddate(roleid, id.toLowerCase(), startdate, enddate);
                        collection.add(element);

                        // DEBUG
                        if (enddate == null) {
                            countactive++;
                        } else {
                            countpassive++;
                        }

                    }
                } else {
                    if (enddate == null) {
                        countactivenoid++;
                    } else {
                        countpassivenoid++;
                    }
                    Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, MessageFormat.format("Id is null for roleid {0}", new Object[]{roleid}));
                }

                //DEBUG
                //Logger.getLogger(NetworkSQL.class.getName()).log(Level.INFO, MessageFormat.format("Count: {0} (active: {1}, count passive {2}, count active no id: {3}, count passive no id: {4})", new Object[]{(countactive + countpassive + countactivenoid + countpassivenoid), countactive, countpassive, countactivenoid, countpassivenoid}));

            }
            return collection;
        } catch (SQLException sqle) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMNetworkException(MessageFormat.format("Network SQL exception for networkid {0} name {1} filter {2}", new Object[]{networkdef.getNetworkid(), networkdef.getName(), filter}), sqle);
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for networkid {0} name {1} filter {2}", new Object[]{networkdef.getNetworkid(), networkdef.getName(), filter}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     * private static IdStartdateEnddate getId(Connection connection, NetworkDef
     * networkdef, long roleid, long channelid) throws CRMNetworkException {
     * Role role; URL url = null; Phone phone = null; long urlid; long phoneid;
     * String id; boolean nochannel = false; try { IdStartdateEnddate
     * idstartdateenddate = new IdStartdateEnddate(); // Get contact channel via
     * the retrieved indactions channel id value if
     * (networkdef.getMediatype().equals(Channel.MEDIATYPE_EMAIL)) { if
     * (channelid != 0) { urlid = channelid; URLSQL urlsql = new URLSQL(); url =
     * urlsql.readEmail(connection, urlid); if (url == null) {
     * //Logger.getLogger(NetworkImplementationSQL.class.getName()).log(Level.WARNING,
     * "Unable to read URL via channelid {0}", channelid); nochannel = true; } }
     * else { nochannel = true; } // TODO: Update channelid if it is 0 if (url
     * != null) { id = url.getInternetAddress();
     * idstartdateenddate.setRoleid(roleid); idstartdateenddate.setId(id); }
     * else { // Try to get e-mail address via role RoleSQL rolesql = new
     * RoleSQL(); role = rolesql.read(connection, roleid); if (role != null) {
     * url = role.getEmail(); if (url != null) { id = url.getInternetAddress();
     * idstartdateenddate.setRoleid(roleid); idstartdateenddate.setId(id); if
     * (nochannel) { updateChannelid(connection, networkdef, roleid,
     * url.getUrlid()); } } else { // TODO: No URL while mediatype is e-mail:
     * revert to paper for this subscriber updateMediatype(connection,
     * networkdef, roleid, Channel.MEDIATYPE_PAPER);
     * //Logger.getLogger(NetworkImplementationSQL.class.getName()).log(Level.WARNING,
     * "No URL for roleid {0}", roleid); return null; } } } } if
     * (networkdef.getMediatype().equals(Channel.MEDIATYPE_PHONE)) { if
     * (channelid != 0) { phoneid = channelid; PhoneSQL phonesql = new
     * PhoneSQL(); phone = phonesql.read(connection, phoneid); if (phone ==
     * null) {
     * //Logger.getLogger(NetworkImplementationSQL.class.getName()).log(Level.WARNING,
     * "Unable to read Phone via channelid {0}", channelid); nochannel = true; }
     * } // TODO: Update channelid if it is 0 if (phone != null) { id =
     * phone.getFormattedNumber(); idstartdateenddate.setRoleid(roleid);
     * idstartdateenddate.setId(id); } else { // Try to get phone number via
     * role RoleSQL rolesql = new RoleSQL(); role = rolesql.read(connection,
     * roleid); if (role != null) { phone = role.getPhone(); if (phone != null)
     * { id = phone.getFormattedNumber(); idstartdateenddate.setRoleid(roleid);
     * idstartdateenddate.setId(id); if (nochannel) {
     * updateChannelid(connection, networkdef, roleid, phone.getPhoneid()); } }
     * else { // TODO: No Phone while mediatype is phone: revert to paper for
     * this subscriber updateMediatype(connection, networkdef, roleid,
     * Channel.MEDIATYPE_PAPER);
     * //Logger.getLogger(NetworkImplementationSQL.class.getName()).log(Level.WARNING,
     * "No Phone for roleid {0}", roleid); return null; } } } } return
     * idstartdateenddate; } catch (Exception e) {
     * Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
     * throw new CRMNetworkException(MessageFormat.format("Network exception for
     * networkid {0} roleid {1} channelid {2} ", new Object[]{networkid, roleid,
     * channelid}), e); } }
     *
     */

    /*
     * UPDATE methods
     *
     */
    /*
     * private static boolean updateChannelid(Connection connection, NetworkDef
     * networkdef, long roleid, long channelid) throws CRMNetworkException { int
     * rowcount; Statement statement = null; ResultSet resultset = null; try {
     * statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
     * ResultSet.CONCUR_READ_ONLY); String sql =
     * Networkdefinition.getSQLUpdatechannelid(networkid); sql =
     * sql.replace("%pvkey", String.valueOf(channelid)); sql = sql.replace("%2",
     * String.valueOf(roleid)); sql = sql.replace("%3",
     * Networkdefinition.getNetworkdefinitionName(networkid));
     *
     * rowcount = statement.executeUpdate(sql); if (rowcount == 0) { return
     * false; } else { return true; } } catch (SQLException sqle) {
     * Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null,
     * sqle); throw new CRMNetworkException(MessageFormat.format("Network SQL
     * exception for networkid {0} roleid {1} channelid {2}", new
     * Object[]{networkid, roleid, channelid}), sqle); } catch (Exception e) {
     * Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
     * throw new CRMNetworkException(MessageFormat.format("Network exception for
     * networkid {0} roleid {1} channelid {2} ", new Object[]{networkid, roleid,
     * channelid}), e); } finally { closeSQL(statement, resultset); } }
     *
     */

    /*
     * private static boolean updateMediatype(Connection connection, NetworkDef
     * networkdef, long roleid, int mediatype) throws CRMNetworkException { int
     * rowcount = 0; Statement statement = null; ResultSet resultset = null; try
     * { Channel channel = new Channel(); channel.setMediatype(mediatype);
     * String mediatypename = channel.getMediatypeName(); if
     * (!mediatypename.isEmpty()) { statement =
     * connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
     * ResultSet.CONCUR_READ_ONLY); String sql =
     * Networkdefinition.getSQLUpdatemediatype(networkid); sql =
     * sql.replace("%pvkey", String.valueOf(mediatypename)); sql =
     * sql.replace("%2", String.valueOf(roleid)); sql = sql.replace("%3",
     * Networkdefinition.getNetworkdefinitionName(networkid));
     *
     * rowcount = statement.executeUpdate(sql); if (rowcount == 0) { return
     * false; } else { return true; } } else { return false; } } catch
     * (SQLException sqle) {
     * Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null,
     * sqle); throw new CRMNetworkException(MessageFormat.format("Network SQL
     * exception for networkid {0} roleid {1} mediatype {2}", new
     * Object[]{networkid, roleid, mediatype}), sqle); } catch (Exception e) {
     * Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
     * throw new CRMNetworkException(MessageFormat.format("Network exception for
     * networkid {0} roleid {1} mediatype {2} ", new Object[]{networkid, roleid,
     * mediatype}), e); } finally { closeSQL(statement, resultset); } }
     *
     */
    private NetworkDef getNetworkDef(java.net.URL url, String name) throws CRMNetworkException {
        try {
            // Parse networkconfig
            XMLParser xmlparser = new XMLParser();
            xmlparser.parseNetworkConfig(url);
            // Get network definition for networkid
            for (NetworkDef networkdef : networkdeflist) {
                if (networkdef.getName().equals(name)) {
                    return networkdef;
                }
            }
            return null;
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for name {0}", new Object[]{name}), e);
        }
    }

    private NetworkDef getNetworkDef(java.net.URL url, long networkid) throws CRMNetworkException {
        try {
            // Parse networkconfig
            XMLParser xmlparser = new XMLParser();
            xmlparser.parseNetworkConfig(url);
            // Get network definition for networkid
            for (NetworkDef networkdef : networkdeflist) {
                if (networkdef.getNetworkid() == networkid) {
                    return networkdef;
                }
            }
            return null;
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for networkid {0}", new Object[]{networkid}), e);
        }
    }

    private static String doSQLParameters(Connection connection, String sql, long roleid, String source) throws CRMNetworkException {
        if (source == null) {
            source = "";
        }
        try {
            sql = sql.replace("%pvkey", String.valueOf(roleid));
            sql = sql.replace("%source", source);
            KeyGenerator keygenerator = new KeyGenerator(connection);
            long identity;
            if (sql.contains("%nxtaddkey")) {
                identity = keygenerator.getNextKey(KeyGenerator.KEY_NEXTADDKEY);
                sql = sql.replace("%nxtaddkey", String.valueOf(identity));
            }
            if (sql.contains("%nxtbankkey")) {
                identity = keygenerator.getNextKey(KeyGenerator.KEY_NEXTBANKKEY);
                sql = sql.replace("%nxtbankkey", String.valueOf(identity));
            }
            if (sql.contains("%nxtgenkey")) {
                identity = keygenerator.getNextKey(KeyGenerator.KEY_NEXTGENKEY);
                sql = sql.replace("%nxtgenkey", String.valueOf(identity));
            }
            if (sql.contains("%nxtmemno")) {
                identity = keygenerator.getNextKey(KeyGenerator.KEY_NEXTMEMNO);
                sql = sql.replace("%nxtmemno", String.valueOf(identity));
            }
            if (sql.contains("%nxtpeopkey")) {
                identity = keygenerator.getNextKey(KeyGenerator.KEY_NEXTPEOPKEY);
                sql = sql.replace("%nxtpeopkey", String.valueOf(identity));
            }
            if (sql.contains("%nxtrecno")) {
                identity = keygenerator.getNextKey(KeyGenerator.KEY_NEXTRECNO);
                sql = sql.replace("%nxtrecno", String.valueOf(identity));
            }
            return sql;
        } catch (Exception e) {
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMNetworkException(MessageFormat.format("Network exception for roleid {0}", new Object[]{roleid}), e);
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
            Logger.getLogger(NetworkSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }
}
