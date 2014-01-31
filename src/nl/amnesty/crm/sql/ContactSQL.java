package nl.amnesty.crm.sql;

import java.sql.*;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.db.KeyGenerator;
import nl.amnesty.crm.entity.Contact;
import nl.amnesty.crm.exception.CRMContactException;

/**
 *
 * @author ed
 */
public class ContactSQL {

    private static final boolean USETIMESTAMP = false;

    /*
     * ---------------------------------------- Standard CRUD methods
     * ----------------------------------------
     */
    public Contact create(Connection connection, Contact contact) throws CRMContactException {
        long contactid = 0;
        Contact contactmatched;
        Statement statement = null;
        ResultSet resultset = null;
        //long contactid = 0;
        try {
            // try to match contact
            contactmatched = match(connection, contact);
            if (contactmatched != null) {
                if (contactmatched.isAcceptableMatch()) {
                    return contactmatched;
                }
            }

            // At this point no matching contact is found in CRM, so let's create a new one
            String SQL = "SELECT * FROM conthist";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();

            resultset = setResultsetColumns(resultset, contact);

            if (resultset != null) {
                if (contact.getContactid() == 0) {
                    KeyGenerator keygenerator = new KeyGenerator(connection);
                    contactid = keygenerator.getNextKey(KeyGenerator.KEY_NEXTGENKEY);
                    // Set the contact id in the contact object
                    contact.setContactid(contactid);
                }
                resultset.updateInt(Contact.FIELD_CONTHIST_ID, (int) contactid);
                resultset.insertRow();
            }
            contact.setStatus(Contact.STATUS_NEW);
            return contact;
        } catch (SQLException sqle) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMContactException(MessageFormat.format("Contact SQL exception for contactid {0} subject {1}", new Object[]{contact.getContactid(), contact.getSubject()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMContactException(MessageFormat.format("Contact exception for contactid {0} subject {1}", new Object[]{contact.getContactid(), contact.getSubject()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public Contact read(Connection connection, long contactid) {
        // TODO: Implementation for adding contact, probably by adding entry to definition table
        return null;
    }

    public boolean update(Connection connection, Contact contact) {
        // TODO: Implementation for updating contact, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long contactid) {
        // TODO: Implementation for deleting contact, this is just a stub
        return false;
    }

    /*
     * ---------------------------------------- Object matching
     * ----------------------------------------
     */
    public Contact match(Connection connection, Contact contact) throws CRMContactException {
        Contact contactfound;
        try {
            // First of all let's see if contact can be found in CRM via the contact id
            if (contact.getContactid() != 0) {
                contactfound = read(connection, contact.getContactid());
                if (contactfound != null) {
                    contactfound.setStatus(Contact.STATUS_MATCHED_ID);
                    return contactfound;
                }
            }
            // Next best thing is to search for the street, housenumber and city
            if (!contact.getSubject().isEmpty() && contact.getDate() != null) {
                contactfound = readViaSubjectDate(connection, contact.getSubject(), contact.getDate());
                if (contactfound != null) {
                    contactfound.setStatus(Contact.STATUS_MATCHED_SUBJECT_DATE);
                    return contactfound;
                }
            }
            //contact = new Contact();
            contact.setStatus(Contact.STATUS_MATCHED_NONE);
            return contact;

        } catch (Exception e) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMContactException(MessageFormat.format("Contact exception for contactid {0} subject {1}", new Object[]{contact.getContactid(), contact.getSubject()}), e);
        }
    }

    /*
     * ---------------------------------------- Misc methods
     * ----------------------------------------
     */
    private static ResultSet setResultsetColumns(ResultSet resultset, Contact contact) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone timezone = TimeZone.getDefault();
            calendar.setTimeZone(timezone);

            if (USETIMESTAMP) {
                Timestamp today = new Timestamp(calendar.getTimeInMillis());
                resultset.updateTimestamp(Contact.FIELD_CONTHIST_DATE, today);
            } else {
                java.sql.Date today = new java.sql.Date(calendar.getTime().getTime());
                resultset.updateDate(Contact.FIELD_CONTHIST_DATE, today);
            }
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            short time = (short) ((hour * 100) + minute);
            resultset.updateShort(Contact.FIELD_CONTHIST_TIME, time);

            if (contact.getRoleid() != 0) {
                resultset.updateInt(Contact.FIELD_CONTHIST_ROLEID, (int) contact.getRoleid());
            }

            //DEBUG
            Logger.getLogger(ContactSQL.class.getName()).log(Level.INFO, "setResultsetColumns(): roleid: {0}", contact.getRoleid());

            resultset.updateString(Contact.FIELD_CONTHIST_STATUS, "Y");
            //resultset.updateString(Contact.FIELD_CONTHIST_TYPE, contact.getType());
            resultset.updateString(Contact.FIELD_CONTHIST_TYPE, "EXT RL I/U");
            resultset.updateString(Contact.FIELD_CONTHIST_AUTHOR, "WEBSERVICE");
            resultset.updateString(Contact.FIELD_CONTHIST_OWNER, "");
            String source = contact.getSource();
            if (source == null) {
                source = "";
            } else {
                if (source.length() > 10) {
                    source = source.substring(0, 7).concat("...");
                }
            }
            resultset.updateString(Contact.FIELD_CONTHIST_SOURCE, source);
            //resultset.updateString(Contact.FIELD_CONTHIST_SOURCE, "EMAIL");
            resultset.updateString(Contact.FIELD_CONTHIST_DIRECTION, "WWW IN");
            String subject = contact.getSubject();
            if (subject == null) {
                subject = "";
            } else {
                if (subject.length() > 50) {
                    subject = subject.substring(0, 47).concat("...");
                }
            }
            resultset.updateString(Contact.FIELD_CONTHIST_SUBJECT, subject);
            //resultset.updateString(Contact.FIELD_CONTHIST_SUBJECT, "INFOADVIES");
            String content = contact.getContent();
            if (content == null) {
                content = "";
            } else {
                if (content.length() > 8000) {  // kan nog meer, maar laten we het hier eens op houden. Is max voor varchar. Het is in Progress een text-veld.
                    content = content.substring(0, 7997).concat("...");
                }
            }
            resultset.updateString(Contact.FIELD_CONTHIST_CONTENT, content);

            String udef = contact.getUserdef3();
            if (udef == null) {
                udef = "";
            } else {
                if (udef.length() > 50) {
                    udef = content.substring(0, 47).concat("...");
                }
            }
            resultset.updateString(Contact.FIELD_CONTHIST_USERDEFCODE3, udef);

            udef = contact.getUserdef4();
            if (udef == null) {
                udef = "";
            } else {
                if (udef.length() > 50) {
                    udef = content.substring(0, 47).concat("...");
                }
            }
            resultset.updateString(Contact.FIELD_CONTHIST_USERDEFCODE4, udef);

            udef = contact.getUserdef5();
            if (udef == null) {
                udef = "";
            } else {
                if (udef.length() > 50) {
                    udef = content.substring(0, 47).concat("...");
                }
            }
            resultset.updateString(Contact.FIELD_CONTHIST_USERDEFCODE5, udef);

            resultset.updateLong(Contact.FIELD_CONTHIST_USERDEFNUMB5, contact.getUsernumb5());

            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, null, sqle);
            return null;
        } catch (Exception e) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }

    }

    /*
     * ---------------------------------------- READ methods
     * ----------------------------------------
     */
    private static Contact readViaSubjectDate(Connection connection, String subject, Date date) {
        // TODO: This is just a stub: implement SQL search here!
        return null;
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
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }
}
