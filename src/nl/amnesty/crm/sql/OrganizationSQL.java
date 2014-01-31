/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.entity.Organization;
import nl.amnesty.crm.entity.Role;
import nl.amnesty.crm.exception.CRMOrganizationException;

/**
 *
 * @author bmenting
 */
public class OrganizationSQL {

    /*
     * ---------------------------------------- Standard CRUD methods
     * ----------------------------------------
     */
    public Organization create(Connection connection, Organization organization) {
        // TODO: Implementation for adding organization, probably by adding entry to definition table
        return null;
    }

    public Organization read(Connection connection, long organizationid) {
        // TODO: Implementation for reading organization, this is just a stub
        return null;
    }

    public boolean update(Connection connection, Organization organization) {
        // TODO: Implementation for updating organization, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long organizationid) {
        // TODO: Implementation for deleting organization, this is just a stub
        return false;
    }

    /*
     * ---------------------------------------- READ methods
     * ----------------------------------------
     */
    public String readOganizationNameViaRoleid(Connection connection, long roleid) throws CRMOrganizationException {
        String organizationName = "";
        try {
            organizationName = doOrganizationName(connection, roleid);
            if (organizationName != null) {

                //DEBUG
                Logger.getLogger(GroupSQL.class.getName()).log(Level.INFO, "Found organizatioame {0} for roleid (org) {1} {2})", new Object[]{organizationName, roleid, "readOrganizationNameViaRoleId()"});

                return organizationName;
            } else {
                return "";
            }
        } catch (Exception e) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMOrganizationException(MessageFormat.format("Organization exception for roleid {0} type {1}", new Object[]{roleid}), e);
        }
    }
    
    public String readAIGroupNameViaRoleid(Connection connection, long roleid) throws CRMOrganizationException {
        String organizationName = "";
        try {
            organizationName = doAIGroupName(connection, roleid);
            if (organizationName != null) {

                //DEBUG
                Logger.getLogger(GroupSQL.class.getName()).log(Level.INFO, "Found organizatioame {0} for roleid (org) {1} {2})", new Object[]{organizationName, roleid, "readOrganizationNameViaRoleId()"});

                return organizationName;
            } else {
                return "";
            }
        } catch (Exception e) {
            Logger.getLogger(GroupSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMOrganizationException(MessageFormat.format("Organization exception for roleid {0} type {1}", new Object[]{roleid}), e);
        }
    }

    /*
     * ---------------------------------------- Object matching
     * ----------------------------------------
     */
    public Organization match(Connection connection, Organization organization) {
        // TODO: Implementation for matching, this is just a stub
        organization.setStatus(Organization.STATUS_MATCHED_NONE);
        return organization;
    }
    
    private String doOrganizationName(Connection connection, long roleid) {
        String query;
        Statement statement = null;
        ResultSet resultset = null;

        if (roleid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "read() Role id is 0");
            return null;
        }
        try {
            query = "SELECT * FROM contact c, address a WHERE a.addresskey=c.addresskey and c.pvkey=" + roleid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            String returnvalue="";
            if (resultset.first()) {
                String rectype = resultset.getString(Role.FIELD_CONTACT_RECTYPE);
                if (rectype.equals(Role.RECTYPE_ORGANIZATION) || (rectype.equals(Role.RECTYPE_AI_GROUP))) {
                    returnvalue=resultset.getString(Organization.FIELD_ORGANIZATION_NAME);
                    if (returnvalue==null) {
                        returnvalue="";
                    }
                } 
            }
            return returnvalue;
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
    
    private String doAIGroupName(Connection connection, long roleid) {
        String query;
        Statement statement = null;
        ResultSet resultset = null;

        if (roleid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "read() Role id is 0");
            return null;
        }
        try {
            query = "SELECT a.company FROM contact cl "
                    + " INNER JOIN wkwlink w ON (cl.pvkey=linkpeop1 or cl.pvkey=linkpeop2) and w.wkwkey='ZF' "
                    + "	and linkdate<=getdate() and (enddate is null or enddate>=getdate()) "
                    + " INNER JOIN contact cg ON (cg.pvkey=linkpeop1 or cg.pvkey=linkpeop2) "
                    + " and cg.pvkey<>cl.pvkey and cg.rectype='AI GROEP' "
                    + " INNER JOIN address a ON a.addresskey=cg.addresskey "
                    + " WHERE cl.pvkey=" + roleid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                return resultset.getString(Organization.FIELD_ORGANIZATION_NAME);
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

    public boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
