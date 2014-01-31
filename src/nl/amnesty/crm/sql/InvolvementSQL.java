/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.db.KeyGenerator;
import nl.amnesty.crm.entity.Involvement;
import nl.amnesty.crm.entity.Network;
import nl.amnesty.crm.exception.CRMInvolvementException;

/**
 *
 * @author evelzen
 */
public class InvolvementSQL {

    private static final boolean USETIMESTAMP = false;

    /*
     * ----------------------------------------
     * Standard CRUD methods
     * ----------------------------------------
     */
    public Involvement create(Connection connection, Involvement involvement) throws CRMInvolvementException {
        long involvementid = 0;
        Involvement involvementmatched;
        Statement statement = null;
        ResultSet resultset = null;
        if (involvement == null) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, "Involvement is null");
            return null;
        }
        if (involvement.getRoleid() == 0) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, "Roleid for involvement {0} is 0", involvement.getName());
            return null;
        }
        try {
            // try to match involvement
            involvementmatched = match(connection, involvement);
            if (involvementmatched != null) {
                if (involvementmatched.isAcceptableMatch()) {
                    return involvementmatched;
                }
            }

            // At this point no matching involvement is found in CRM, so let's create a new one
            String SQL = "SELECT * FROM grpmbrs";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();

            resultset = setResultsetColumns(resultset, involvement);

            if (involvement.getInvolvementid() == 0) {
                KeyGenerator keygenerator = new KeyGenerator(connection);
                involvementid = keygenerator.getNextKey(KeyGenerator.KEY_NEXTGENKEY);
                // Set the involvement id in the involvement object
                involvement.setInvolvementid(involvementid);
            }
            resultset.updateInt(Involvement.FIELD_GRPMBRS_ID, (int) involvementid);
            resultset.insertRow();
            return involvement;
        } catch (SQLException sqle) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMInvolvementException(MessageFormat.format("Involvement SQL exception for involvementid {0} name {1}", new Object[]{involvement.getInvolvementid(), involvement.getName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for involvementid {0} name {1}", new Object[]{involvement.getInvolvementid(), involvement.getName()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public Involvement read(Connection connection, long involvementid) throws CRMInvolvementException {
        Involvement involvementgrpmbrs;
        Involvement involvementindactions;
        try {
            involvementgrpmbrs = readGrpmbrs(connection, involvementid);
            if (involvementgrpmbrs != null) {
                return involvementgrpmbrs;
            }
            involvementindactions = readIndactions(connection, involvementid);
            if (involvementindactions != null) {
                return involvementindactions;
            }
            return null;
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for involvementid {0}", new Object[]{involvementid}), e);
        }
    }

    public Involvement readGrpmbrs(Connection connection, long involvementid) throws CRMInvolvementException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            query = "SELECT * FROM grpmbrs WHERE " + Involvement.FIELD_GRPMBRS_ID + "=" + involvementid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                Involvement involvement = setBeanPropertiesGrpmbrs(connection, resultset);
                return involvement;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMInvolvementException(MessageFormat.format("Involvement SQL exception for involvementid {0}", new Object[]{involvementid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for involvementid {0}", new Object[]{involvementid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public Involvement readIndactions(Connection connection, long involvementid) throws CRMInvolvementException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            query = "SELECT * FROM indactions WHERE " + Network.FIELD_INDACTIONS_ID + "=" + involvementid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                Involvement involvement = setBeanPropertiesIndactions(connection, resultset);
                return involvement;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMInvolvementException(MessageFormat.format("Involvement SQL exception for involvementid {0}", new Object[]{involvementid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for involvementid {0}", new Object[]{involvementid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public boolean update(Connection connection, Involvement involvement) {
        // TODO: Implementation for updating involvement, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long involvementid) {
        // TODO: Implementation for deleting involvement, this is just a stub
        return false;
    }

    /*
     * ----------------------------------------
     * Object matching
     * ----------------------------------------
     */
    public Involvement match(Connection connection, Involvement involvement) throws CRMInvolvementException {
        Involvement involvementfound;
        try {
            // First of all let's see if involvement can be found in CRM via the involvement id
            if (involvement.getInvolvementid() != 0) {
                involvementfound = read(connection, involvement.getInvolvementid());
                if (involvementfound != null) {
                    involvementfound.setStatus(Involvement.STATUS_MATCHED_ID);
                    return involvementfound;
                }
            }
            // Secondly try to find the involvement by role and name
            if (involvement.getRoleid() != 0) {
                if (involvement.getName() != null) {
                    involvementfound = readViaRoleidName(connection, involvement.getRoleid(), involvement.getName());
                    if (involvementfound != null) {
                        involvementfound.setStatus(Involvement.STATUS_MATCHED_ROLE_NAME);
                        return involvementfound;
                    }
                }
            }
            involvement.setStatus(Involvement.STATUS_MATCHED_NONE);
            return involvement;
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for involvementid {0} name {1}", new Object[]{involvement.getInvolvementid(), involvement.getName()}), e);
        }
    }

    /*
     * ----------------------------------------
     * READ methods
     * ----------------------------------------
     */
    public List<Involvement> readViaRoleid(Connection connection, long roleid) throws CRMInvolvementException {
        List<Involvement> involvementlist = new ArrayList();
        List<Involvement> involvementlistgrpmbrs;
        List<Involvement> involvementlistindactions;
        try {
            involvementlistgrpmbrs = readViaRoleidGrpmbrs(connection, roleid);
            if (involvementlistgrpmbrs == null) {
                return null;
            }
            involvementlist.addAll(involvementlistgrpmbrs);
            involvementlistindactions = readViaRoleidIndactions(connection, roleid);
            if (involvementlistindactions == null) {
                return involvementlist;
            }
            involvementlist.addAll(involvementlistindactions);
            return involvementlist;
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for roleid {0}", new Object[]{roleid}), e);
        }
    }

    public List<Involvement> readViaRoleidGrpmbrs(Connection connection, long roleid) throws CRMInvolvementException {
        List<Involvement> involvementlist = new ArrayList();
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            String queryroleid = Involvement.FIELD_GRPMBRS_ROLEID.concat("=").concat(String.valueOf(roleid));
            String querydate = Involvement.FIELD_GRPMBRS_INVOLVEMENT_STARTDATE.concat("<= getdate() AND ").concat(Involvement.FIELD_GRPMBRS_INVOLVEMENT_ENDDATE).concat(" IS NULL");

            query = "SELECT * FROM grpmbrs WHERE ".concat(queryroleid).concat(" AND ").concat(querydate);

            // DEBUG
            //Logger.getLogger(InvolvementSQL.class.getName()).log(Level.INFO, "query: readViaRoleidGrpmbrs() {0}", query);
            
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    Involvement involvement = setBeanPropertiesGrpmbrs(connection, resultset);
                    if (involvement != null) {
                        involvementlist.add(involvement);
                    }
                } while (resultset.next());
            }
            return involvementlist;
        } catch (SQLException sqle) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMInvolvementException(MessageFormat.format("Involvement SQL exception for roleid {0}", new Object[]{roleid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for roleid {0}", new Object[]{roleid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public List<Involvement> readViaRoleidIndactions(Connection connection, long roleid) throws CRMInvolvementException {
        List<Involvement> involvementlist = new ArrayList();
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            String queryroleid = Network.FIELD_INDACTIONS_ROLEID.concat("=").concat(String.valueOf(roleid));
            String querydate = Network.FIELD_INDACTIONS_STARTDATE.concat("<= getdate() AND ").concat(Network.FIELD_INDACTIONS_ENDDATE).concat(" IS NULL");

            query = "SELECT * FROM indactions WHERE ".concat(queryroleid).concat(" AND ").concat(querydate);

            // DEBUG
            //Logger.getLogger(InvolvementSQL.class.getName()).log(Level.INFO, "query: readViaRoleidIndactions() {0}", query);
            
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    Involvement involvement = setBeanPropertiesIndactions(connection, resultset);
                    if (involvement != null) {
                        involvementlist.add(involvement);
                    }
                } while (resultset.next());
            }
            return involvementlist;
        } catch (SQLException sqle) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMInvolvementException(MessageFormat.format("Involvement SQL exception for roleid {0}", new Object[]{roleid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for roleid {0}", new Object[]{roleid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private static Involvement readViaRoleidName(Connection connection, long roleid, String name) throws CRMInvolvementException {
        Involvement involvementgrpmbrs;
        Involvement involvementindactions;
        try {
            involvementgrpmbrs = readViaRoleidNameGrpmbrs(connection, roleid, name);
            if (involvementgrpmbrs != null) {
                return involvementgrpmbrs;
            }
            involvementindactions = readViaRoleidNameIndactions(connection, roleid, name);
            if (involvementindactions != null) {
                return involvementindactions;
            }
            return null;
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for roleid {0}", new Object[]{roleid}), e);
        }

    }

    private static Involvement readViaRoleidNameGrpmbrs(Connection connection, long roleid, String name) throws CRMInvolvementException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            String queryroleid = Involvement.FIELD_GRPMBRS_ROLEID.concat("=").concat(String.valueOf(roleid));
            String queryname = Involvement.FIELD_GRPMBRS_INVOLVEMENT_NAME.concat("='").concat(name).concat("'").replace(";","").replace("%","").replace("&", "");
            String querydate = Involvement.FIELD_GRPMBRS_INVOLVEMENT_STARTDATE.concat("<= getdate() AND ").concat(Involvement.FIELD_GRPMBRS_INVOLVEMENT_ENDDATE).concat(" IS NULL");

            query = "SELECT * FROM grpmbrs WHERE ".concat(queryroleid).concat(" AND ").concat(queryname).concat(" AND ").concat(querydate);

            //DEBUG
            //Logger.getLogger(InvolvementSQL.class.getName()).log(Level.INFO, "query: readViaRoleidNameGrpmbrs() {0}", query);
            
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                Involvement involvement = setBeanPropertiesGrpmbrs(connection, resultset);
                return involvement;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMInvolvementException(MessageFormat.format("Involvement SQL exception for roleid {0} name {1}", new Object[]{roleid, name}), sqle);
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for roleid {0} name {1}", new Object[]{roleid, name}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private static Involvement readViaRoleidNameIndactions(Connection connection, long roleid, String name) throws CRMInvolvementException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            String queryroleid = Network.FIELD_INDACTIONS_ROLEID.concat("=").concat(String.valueOf(roleid));
            String queryname = Network.FIELD_INDACTIONS_SORT.concat("='").concat(name).concat("'").replace(";","").replace("%","").replace("&", "");
            String querydate = Network.FIELD_INDACTIONS_STARTDATE.concat("<= getdate() AND ").concat(Network.FIELD_INDACTIONS_ENDDATE).concat(" IS NULL");

            query = "SELECT * FROM indactions WHERE ".concat(queryroleid).concat(" AND ").concat(queryname).concat(" AND ").concat(querydate);

            //DEBUG
            //Logger.getLogger(InvolvementSQL.class.getName()).log(Level.INFO, "query: readViaRoleidNameIndactions() {0}", query);
            
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                Involvement involvement = setBeanPropertiesGrpmbrs(connection, resultset);
                return involvement;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMInvolvementException(MessageFormat.format("Involvement SQL exception for roleid {0} name {1}", new Object[]{roleid, name}), sqle);
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for roleid {0} name {1}", new Object[]{roleid, name}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     * ----------------------------------------
     * Misc methods
     * ----------------------------------------
     */
    private static Involvement setBeanPropertiesGrpmbrs(Connection connection, ResultSet resultset) throws CRMInvolvementException {
        Involvement involvement = new Involvement();
        try {
            involvement.setInvolvementid(resultset.getLong(Involvement.FIELD_GRPMBRS_ID));
            long roleid = resultset.getLong(Involvement.FIELD_GRPMBRS_ROLEID);
            involvement.setRoleid(roleid);
            involvement.setName(resultset.getString(Involvement.FIELD_GRPMBRS_INVOLVEMENT_NAME));
            involvement.setDescription(resultset.getString(Involvement.FIELD_GRPMBRS_INVOLVEMENT_DESCRIPTION));
            involvement.setSource(resultset.getString(Involvement.FIELD_GRPMBRS_INVOLVEMENT_SOURCE));

            java.sql.Date sqlstartdate = resultset.getDate(Involvement.FIELD_GRPMBRS_INVOLVEMENT_STARTDATE);
            if (sqlstartdate != null) {
                Calendar calendarstartdate = Calendar.getInstance();
                calendarstartdate.setTimeInMillis(sqlstartdate.getTime());
                involvement.setStartdate(calendarstartdate.getTime());
            }

            java.sql.Date sqlenddate = resultset.getDate(Involvement.FIELD_GRPMBRS_INVOLVEMENT_ENDDATE);
            if (sqlenddate != null) {
                Calendar calendarenddate = Calendar.getInstance();
                calendarenddate.setTimeInMillis(sqlenddate.getTime());
                involvement.setEnddate(calendarenddate.getTime());
            }
            return involvement;

        } catch (SQLException sqle) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMInvolvementException(MessageFormat.format("Involvement SQL exception for involvementid {0} name {1}", new Object[]{involvement.getInvolvementid(), involvement.getName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for involvementid {0} name {1}", new Object[]{involvement.getInvolvementid(), involvement.getName()}), e);
        }
    }

    private static Involvement setBeanPropertiesIndactions(Connection connection, ResultSet resultset) throws CRMInvolvementException {
        Involvement involvement = new Involvement();
        try {
            involvement.setInvolvementid(resultset.getLong(Network.FIELD_INDACTIONS_ID));
            long roleid = resultset.getLong(Network.FIELD_INDACTIONS_ROLEID);
            involvement.setRoleid(roleid);
            involvement.setName(resultset.getString(Network.FIELD_INDACTIONS_SORT));
            involvement.setDescription("");
            involvement.setSource(resultset.getString(Network.FIELD_INDACTIONS_SOURCE));

            java.sql.Date sqlstartdate = resultset.getDate(Network.FIELD_INDACTIONS_STARTDATE);
            if (sqlstartdate != null) {
                Calendar calendarstartdate = Calendar.getInstance();
                calendarstartdate.setTimeInMillis(sqlstartdate.getTime());
                involvement.setStartdate(calendarstartdate.getTime());
            }

            java.sql.Date sqlenddate = resultset.getDate(Network.FIELD_INDACTIONS_ENDDATE);
            if (sqlenddate != null) {
                Calendar calendarenddate = Calendar.getInstance();
                calendarenddate.setTimeInMillis(sqlenddate.getTime());
                involvement.setEnddate(calendarenddate.getTime());
            }
            return involvement;
        } catch (SQLException sqle) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMInvolvementException(MessageFormat.format("Involvement SQL exception for involvementid {0} name {1}", new Object[]{involvement.getInvolvementid(), involvement.getName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for involvementid {0} name {1}", new Object[]{involvement.getInvolvementid(), involvement.getName()}), e);
        }
    }

    private static ResultSet setResultsetColumns(ResultSet resultset, Involvement involvement) throws CRMInvolvementException {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone timezone = TimeZone.getDefault();
            calendar.setTimeZone(timezone);

            if (involvement.getStartdate() == null) {
                if (USETIMESTAMP) {
                    Timestamp today = new Timestamp(calendar.getTimeInMillis());
                    resultset.updateTimestamp(Involvement.FIELD_GRPMBRS_INVOLVEMENT_STARTDATE, today);
                } else {
                    java.sql.Date today = new java.sql.Date(calendar.getTime().getTime());
                    resultset.updateDate(Involvement.FIELD_GRPMBRS_INVOLVEMENT_STARTDATE, today);
                }
            } else {
                Calendar calendarstart = Calendar.getInstance();
                calendarstart.setTimeZone(timezone);
                calendarstart.setTime(involvement.getStartdate());

                if (USETIMESTAMP) {
                    Timestamp start = new Timestamp(calendarstart.getTimeInMillis());
                    resultset.updateTimestamp(Involvement.FIELD_GRPMBRS_INVOLVEMENT_STARTDATE, start);
                } else {
                    java.sql.Date start = new java.sql.Date(calendarstart.getTime().getTime());
                    resultset.updateDate(Involvement.FIELD_GRPMBRS_INVOLVEMENT_STARTDATE, start);
                }
            }
            if (involvement.getEnddate() != null) {
                Calendar calendarend = Calendar.getInstance();
                calendarend.setTimeZone(timezone);
                calendarend.setTime(involvement.getEnddate());
                if (USETIMESTAMP) {
                    Timestamp end = new Timestamp(calendarend.getTimeInMillis());
                    resultset.updateTimestamp(Involvement.FIELD_GRPMBRS_INVOLVEMENT_ENDDATE, end);
                } else {
                    java.sql.Date end = new java.sql.Date(calendarend.getTime().getTime());
                    resultset.updateDate(Involvement.FIELD_GRPMBRS_INVOLVEMENT_ENDDATE, end);
                }
            }
            resultset.updateLong(Involvement.FIELD_GRPMBRS_ROLEID, involvement.getRoleid());
            resultset.updateString(Involvement.FIELD_GRPMBRS_INVOLVEMENT_SOURCE, involvement.getSource());
            resultset.updateString(Involvement.FIELD_GRPMBRS_INVOLVEMENT_NAME, involvement.getName());
            resultset.updateString(Involvement.FIELD_GRPMBRS_INVOLVEMENT_DESCRIPTION, involvement.getDescription());
            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMInvolvementException(MessageFormat.format("Involvement SQL exception for involvementid {0} name {1}", new Object[]{involvement.getInvolvementid(), involvement.getName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMInvolvementException(MessageFormat.format("Involvement exception for involvementid {0} name {1}", new Object[]{involvement.getInvolvementid(), involvement.getName()}), e);
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
            Logger.getLogger(InvolvementSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }
}
