package nl.amnesty.crm.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.entity.*;
import nl.amnesty.crm.exception.CRMAddressException;
import nl.amnesty.crm.exception.CRMGroupException;

/**
 *
 * @author ed
 */
public class GroupSQL {

    /*
     * ---------------------------------------- Standard CRUD methods
     * ----------------------------------------
     */
    public Group create(Connection connection, Group group) {
        // TODO: Implementation for adding group, probably by adding entry to definition table
        return null;
    }

    public Group read(Connection connection, long groupid) {
        // TODO: Implementation for reading group, this is just a stub
        return null;
    }

    public boolean update(Connection connection, Group group) {
        // TODO: Implementation for updating group, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long groupid) {
        // TODO: Implementation for deleting group, this is just a stub
        return false;
    }

    /*
     * ---------------------------------------- Object matching
     * ----------------------------------------
     */
    public Group match(Connection connection, Group group) {
        // TODO: Implementation for matching, this is just a stub
        group.setStatus(Group.STATUS_MATCHED_NONE);
        return group;
    }

    /*
     * ---------------------------------------- READ methods
     * ----------------------------------------
     */
    public Role readAIGroupTreasurerViaRoleid(Connection connection, long roleid) throws CRMGroupException {
        long groupid;
        long treasurerid;
        Role role = new Role();
        try {
            // Get the relationid for the local Amnesty group that this role belongs to
            groupid = getRelatedRoleid(connection, roleid, "ZF");
            if (groupid == 0) {
                groupid = getRelatedRoleid(connection, roleid, "RCP");
            }
            if (groupid == 0) {
                groupid = getRelatedRoleid(connection, roleid, "RCG");
            }
            if (groupid == 0) {
                groupid = getRelatedRoleid(connection, roleid, "RCO");
            }
            if (groupid != 0) {
                // Get the relationid for the treasurer that belongs to the local Amnesty group
                treasurerid = getRelatedRoleid(connection, groupid, "PM");
                if (treasurerid != 0) {
                    RoleSQL rolesql = new RoleSQL();
                    role = rolesql.read(connection, treasurerid);

                    //DEBUG
                    String treasurerformalname = role.getPerson().getFormattedFormalName();
                    Logger.getLogger(GroupSQL.class.getName()).log(Level.INFO, "Found treasurer {0} for AIGroupid {1} for roleid {2} ({3})", new Object[]{treasurerformalname, groupid, roleid, "readAIGroupTreasurerViaRoleid()"});
                }
            }
            return role;
        } catch (Exception e) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMGroupException(MessageFormat.format("Group exception for roleid {0} type {1}", new Object[]{roleid}), e);
        }
    }

    public int readAIGroupNumberViaRoleid(Connection connection, long roleid) throws CRMGroupException {
        long groupid;
        try {
            // Get the relationid for the local Amnesty group that this role belongs to
            groupid = getRelatedRoleid(connection, roleid, "ZF");
            if (groupid == 0) {
                groupid = getRelatedRoleid(connection, roleid, "RCP");
            }
            if (groupid == 0) {
                groupid = getRelatedRoleid(connection, roleid, "RCG");
            }
            if (groupid == 0) {
                groupid = getRelatedRoleid(connection, roleid, "RCO");
            }
            if (groupid != 0) {
                String groupname = doAIGroupName(connection, groupid);

                //DEBUG
                Logger.getLogger(GroupSQL.class.getName()).log(Level.INFO, "Found group {0} for AIGroupid {1} {2})", new Object[]{groupname, roleid, "readAIGroupNumberViaRoleid()"});

                return parseAIGroupNumber(groupname);
            } else {
                return 0;
            }
        } catch (Exception e) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMGroupException(MessageFormat.format("Group exception for roleid {0} type {1}", new Object[]{roleid}), e);
        }
    }

    public int readDebtornumberViaRoleid(Connection connection, long roleid) throws CRMGroupException {
        long groupid;
        int debtornumber = 0;
        try {
            debtornumber = doCompanyDebtornumber(connection, roleid);
            if (debtornumber != 0) {

                //DEBUG
                Logger.getLogger(GroupSQL.class.getName()).log(Level.INFO, "Found debtornumber {0} for roleid (org) {1} {2})", new Object[]{debtornumber, roleid, "readDebtorumberViaRoleid()"});

                return debtornumber;
            }
            // Get the relationid for the local Amnesty group that this role belongs to
            groupid = getRelatedRoleid(connection, roleid, "ZF");
            if (groupid == 0) {
                groupid = getRelatedRoleid(connection, roleid, "RCP");
            }
            if (groupid == 0) {
                groupid = getRelatedRoleid(connection, roleid, "RCG");
            }
            if (groupid == 0) {
                groupid = getRelatedRoleid(connection, roleid, "RCO");
            }
            if (groupid != 0) {
                String groupname = doAIGroupName(connection, groupid);

                // Test if this is a Wereldwinkel, in which case the (debtor)number can be found in the 'unumb_1' field of the 'contact' table...
                if (groupname.startsWith(Address.FIELD_ADDRESS_COMPANY_PREFIX_WERELDWINKEL)) {
                    debtornumber = doCompanyDebtornumber(connection, roleid);
                } else {
                    debtornumber = 10000 + parseAIGroupNumber(groupname);
                }

                //DEBUG
                Logger.getLogger(GroupSQL.class.getName()).log(Level.INFO, "Found org {0} debtornumber {1} for roleid (org) {2} {3})", new Object[]{groupname, debtornumber, roleid, "readDebtorumberViaRoleid()"});

                return debtornumber;
            } else {
                return 0;
            }
        } catch (Exception e) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMGroupException(MessageFormat.format("Group exception for roleid {0} type {1}", new Object[]{roleid}), e);
        }
    }

    private String doAIGroupName(Connection connection, long roleid) {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;

        if (roleid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "read() Role id is 0");
            return null;
        }
        try {
            query = "SELECT * FROM contact c WHERE c.pvkey=" + roleid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                long addressid = resultset.getInt("addresskey");
                return doAddressCompany(connection, addressid);
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private int doCompanyDebtornumber(Connection connection, long roleid) {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;

        if (roleid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "read() Role id is 0");
            return 0;
        }
        try {
            query = "SELECT * FROM contact c WHERE c.pvkey=" + roleid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                String rectype = resultset.getString(Role.FIELD_CONTACT_RECTYPE);
                if (rectype.equals(Role.RECTYPE_ORGANIZATION)) {
                    return resultset.getInt(Contact.FIELD_CONTACT_DEBTORNUMBER);
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            return 0;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return 0;
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private String doAddressCompany(Connection connection, long addressid) throws CRMAddressException {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        if (addressid == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address id is 0");
            return null;
        }
        try {
            query = "select * from address where addresskey = " + addressid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                String company = resultset.getString(Address.FIELD_ADDRESS_COMPANY);
                return company;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0}", new Object[]{addressid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMAddressException(MessageFormat.format("Address exception {0}", new Object[]{addressid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private static long getRelatedRoleid(Connection connection, long id, String type) throws CRMGroupException {
        long relationsubjectid = 0;
        long relationsubject;
        long relationobject;
        boolean isrelationobject;
        boolean isrelationsubject;
        try {
            // Test if role is member of local Amnesty group
            relationsubject = getRelationSubjectid(connection, id, type);
            isrelationsubject = relationsubject == 0 ? false : true;
            // Test if local Amnesty Group has role as one of its members
            relationobject = getRelationObjectid(connection, id, type);
            isrelationobject = relationobject == 0 ? false : true;

            if (isrelationsubject && isrelationobject) {
                // Are relation and inverse relation to each other or to others?
                // Role can be member of other local Amnesty group than role thinks it is related to.
                if (relationsubject == relationobject) {
                    // Role and local Amnesty group both agree they are related to each other
                    relationsubjectid = relationsubject;
                }
            }
            if (isrelationobject) {
                relationsubjectid = relationobject;
            }
            if (isrelationsubject) {
                relationsubjectid = relationsubject;
            }

            //DEBUG
            Logger.getLogger(GroupSQL.class.getName()).log(Level.INFO, "getRelatedRoleid(): related roleid {0} found for roleid {1}", new Object[]{relationsubjectid, id});

            return relationsubjectid;
        } catch (Exception e) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMGroupException(MessageFormat.format("Group exception for id {0} type {1}", new Object[]{id, type}), e);
        }
    }

    private static long getRelationSubjectid(Connection connection, long id, String type) throws CRMGroupException {
        long groupid = 0;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            String query = "SELECT * FROM wkwlink w WHERE w.linkpeop1 = " + id + " AND w.wkwkey = '" + 
                    type.replace(";","").replace("%","").replace("&", "") + "' AND w.linkdate <= getdate( ) AND w.enddate IS NULL";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    groupid = resultset.getLong("LINKPEOP2");
                } while (resultset.next());

                //DEBUG
                Logger.getLogger(GroupSQL.class.getName()).log(Level.INFO, "getRelationSubjectid(): found related id {0} for roleid {1}", new Object[]{groupid, id});

                return groupid;
            } else {

                //DEBUG
                Logger.getLogger(GroupSQL.class.getName()).log(Level.INFO, "getRelationSubjectid(): no related id found for roleid {0}", groupid);

                return 0;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMGroupException(MessageFormat.format("Group SQL exception for id {0} type {1}", new Object[]{id, type}), sqle);
        } catch (Exception e) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMGroupException(MessageFormat.format("Group exception for id {0} type {1}", new Object[]{id, type}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private static long getRelationObjectid(Connection connection, long id, String type) throws CRMGroupException {
        long groupid = 0;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            String query = "SELECT * FROM wkwlink w WHERE w.linkpeop2 = " + id + " AND w.wkwkey = '" + 
                    type.replace(";","").replace("%","").replace("&", "") + "' AND w.linkdate <= getdate( ) AND w.enddate IS NULL";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    groupid = resultset.getLong("LINKPEOP1");
                } while (resultset.next());

                //DEBUG
                Logger.getLogger(GroupSQL.class.getName()).log(Level.INFO, "getRelationObjectid(): found related id {0} for roleid {1}", new Object[]{groupid, id});

                return groupid;
            } else {

                //DEBUG
                Logger.getLogger(GroupSQL.class.getName()).log(Level.INFO, "getRelationObjectid(): no related id found for roleid {0}", groupid);

                return 0;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMGroupException(MessageFormat.format("Group SQL exception for id {0} type {1}", new Object[]{id, type}), sqle);
        } catch (Exception e) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMGroupException(MessageFormat.format("Group exception for id {0} type {1}", new Object[]{id, type}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private static List<Long> getRolelist(Connection connection, long groupid) throws CRMGroupException {
        long roleid;
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        List<Long> roleidlist = new ArrayList();
        try {
            query = "SELECT * FROM wkwlink w WHERE w.linkpeop1 = " + groupid + " AND w.wkwkey in ('PM', 'ZF', 'RCP', 'RCG', 'RCO') AND w.linkdate <= getdate( ) AND w.enddate IS NULL";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    roleid = resultset.getLong("LINKPEOP2");
                    if (roleid != 0) {
                        roleidlist.add(roleid);
                    }
                } while (resultset.next());
            }

            query = "SELECT * FROM wkwlink w WHERE w.linkpeop2 = " + groupid + " AND w.wkwkey in ('PM', 'ZF', 'RCP', 'RCG', 'RCO') AND w.linkdate <= getdate( ) AND w.enddate IS NULL";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    roleid = resultset.getLong("LINKPEOP1");
                    if (roleid != 0) {
                        roleidlist.add(roleid);
                    }
                } while (resultset.next());
            }

            return roleidlist;
        } catch (SQLException sqle) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMGroupException(MessageFormat.format("Group SQL exception for groupid {0}", new Object[]{groupid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMGroupException(MessageFormat.format("Group exception for groupid {0}", new Object[]{groupid}), e);
        } finally {
            closeSQL(statement, resultset);
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
            Logger.getLogger(FlagSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }

    public int parseAIGroupNumber(String value) {
        if (value == null) {
            return 0;
        }
        String numbervalue = "";
        try {
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if ("0123456789".contains(String.valueOf(c))) {
                    numbervalue = numbervalue.concat(String.valueOf(value.charAt(i)));
                }
            }
            if (isInteger(numbervalue)) {
                return Integer.valueOf(numbervalue);
            } else {
                return 0;
            }
        } catch (Exception e) {
            Logger.getLogger(Phone.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return 0;
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
}
