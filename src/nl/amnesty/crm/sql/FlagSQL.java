package nl.amnesty.crm.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.entity.Flag;
import nl.amnesty.crm.entity.Role;
import nl.amnesty.crm.exception.CRMFlagException;

/**
 *
 * @author ed
 */
public class FlagSQL {

    /*
     * ----------------------------------------
     * Standard CRUD methods
     * ----------------------------------------
     */
    public Flag create(Connection connection, Flag property) throws CRMFlagException {
        String sql;
        Statement statement = null;
        ResultSet resultset = null;
        //long addressid = 0;
        try {
            long roleid = property.getRoleid();
            if (roleid != 0) {
                String propertyfield = flagField(property.getPropertyid()).replace(";","").replace("%","").replace("&", "");
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                if (property.isFlag()) {
                    sql = "UPDATE contact SET " + propertyfield + "=1" + " WHERE " + Role.FIELD_CONTACT_ROLEID + "=" + roleid;
                } else {
                    sql = "UPDATE contact SET " + propertyfield + "=0" + " WHERE " + Role.FIELD_CONTACT_ROLEID + "=" + roleid;
                }
                statement.executeUpdate(sql);
                statement.close();
            }
            return property;
        } catch (SQLException sqle) {
            Logger.getLogger(FlagSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMFlagException(MessageFormat.format("Flag SQL exception for propertyid {0}", new Object[]{property.getPropertyid()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMFlagException(MessageFormat.format("Flag exception for propertyid {0}", new Object[]{property.getPropertyid()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public Flag read(Connection connection, long propertyid) throws CRMFlagException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        Flag property = new Flag();
        //int id = 0;
        try {
            query = "SELECT * FROM contact c WHERE c.propertykey=" + propertyid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                property.setPropertyid((int) propertyid);
                //property.setName("");
                //property.setDescription("");
                return property;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(FlagSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMFlagException(MessageFormat.format("Flag SQL exception for propertyid {0}", new Object[]{propertyid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMFlagException(MessageFormat.format("Flag exception for propertyid {0}", new Object[]{propertyid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public boolean update(Connection connection, Flag property) {
        // TODO: Implementation for updating property, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long propertyid) {
        // TODO: Implementation for deleting property, this is just a stub
        return false;
    }


    /*
     * ----------------------------------------
     * Object matching
     * ----------------------------------------
     */
    public Flag match(Connection connection, Flag flag) {
        // TODO: Implementation for matching, this is just a stub
        flag.setStatus(Flag.STATUS_MATCHED_NONE);
        return flag;
    }

    /*
     * ----------------------------------------
     * Misc methods
     * ----------------------------------------
     */
    private static String flagField(long flagid) {
        switch ((int) flagid) {
            case Flag.FLAG_COLLECTANT:
                return Flag.FIELD_CONTACT_FLAG1;
            case Flag.FLAG2:
                return Flag.FIELD_CONTACT_FLAG2;
            case Flag.FLAG3:
                return Flag.FIELD_CONTACT_FLAG3;
            case Flag.FLAG4:
                return Flag.FIELD_CONTACT_FLAG4;
            case Flag.FLAG5:
                return Flag.FIELD_CONTACT_FLAG5;
            case Flag.FLAG6:
                return Flag.FIELD_CONTACT_FLAG6;
            case Flag.FLAG7:
                return Flag.FIELD_CONTACT_FLAG7;
            case Flag.FLAG8:
                return Flag.FIELD_CONTACT_FLAG8;
            case Flag.FLAG9:
                return Flag.FIELD_CONTACT_FLAG9;
            case Flag.FLAG10:
                return Flag.FIELD_CONTACT_FLAG10;
            default:
                return "";
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
}
