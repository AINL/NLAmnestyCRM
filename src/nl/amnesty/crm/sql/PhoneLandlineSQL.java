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
import nl.amnesty.crm.entity.Phone;
import nl.amnesty.crm.entity.Role;
import nl.amnesty.crm.exception.CRMPhoneException;

/**
 *
 * @author evelzen
 */
public class PhoneLandlineSQL {

    public List<Phone> readPrimaryViaRoleid(Connection connection, long roleid) throws CRMPhoneException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        List<Phone> phonelist = new ArrayList();
        long phoneid;
        String value;
        try {
            // Try the first landline-type phonenumber for a person which is stored in the PHONE column of the CONTACT table
            query = "SELECT * FROM contact c WHERE " + Role.FIELD_CONTACT_ROLEID + "=" + roleid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                // Phonenumber is found and belongs to role because the CONTACT table defines a role in CRM
                phoneid = resultset.getInt(Role.FIELD_CONTACT_ROLEID);
                phoneid = (phoneid * 10) + Phone.PHONE_LANDLINE_PRIMARY;
                value = resultset.getString(Role.FIELD_CONTACT_PHONE);
                if (value != null) {
                    if (value.length() > 0) {
                        Phone phone = new Phone();
                        phone.setPhoneid(phoneid);
                        phone.parseNumber(value);
                        phone.setRoleid(roleid);
                        phone.setStatus(Phone.STATUS_MATCHED_NONE);
                        phonelist.add(phone);
                    }
                }
            }
            // Return the list of phone objects
            return phonelist;
        } catch (SQLException sqle) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMPhoneException(MessageFormat.format("Phone SQL exception for roleid {0}", new Object[]{roleid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMPhoneException(MessageFormat.format("Phone exception for roleid {0}", new Object[]{roleid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public List<Phone> readSecondaryViaRoleid(Connection connection, long roleid) throws CRMPhoneException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        List<Phone> phonelist = new ArrayList();
        String value;
        long phoneid;
        try {
            // Try landline phonenumbers which are stored in the CONTACTNO column of the CONTNOS table
            query = "SELECT * FROM contnos c WHERE " + Phone.FIELD_CONTNOS_ROLEID + "=" + roleid + " AND phntyp in ('TELWRK', 'TELPRV')";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    phoneid = resultset.getInt(Phone.FIELD_CONTNOS_ID);
                    phoneid = (phoneid * 10) + Phone.PHONE_LANDLINE_SECONDARY;
                    value = resultset.getString(Phone.FIELD_CONTNOS_PHONE);
                    if (value != null) {
                        if (value.length() > 0) {
                            Phone phone = new Phone();
                            phone.setPhoneid(phoneid);
                            phone.parseNumber(value);
                            phone.setRoleid(roleid);
                            phone.setStatus(Phone.STATUS_MATCHED_NONE);
                            phonelist.add(phone);
                        }
                    }
                } while (resultset.next());
            }
            // Return the list of phone objects
            return phonelist;
        } catch (SQLException sqle) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMPhoneException(MessageFormat.format("Phone SQL exception for roleid {0}", new Object[]{roleid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMPhoneException(MessageFormat.format("Phone exception for roleid {0}", new Object[]{roleid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public List<Phone> readPrimaryViaNumbervalue(Connection connection, long number, String numbervalue) throws CRMPhoneException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        List<Phone> phonelist = new ArrayList();
        try {
            if (number == 0) {
                return phonelist;
            }
            if (numbervalue.length() == 0) {
                return phonelist;
            }

            // Try the first landline-type phonenumber for a person which is stored in the PHONE column of the CONTACT table
            numbervalue=numbervalue.replace(";","").replace("%","").replace("&", "");
            query = "SELECT * FROM contact c WHERE " + Role.FIELD_CONTACT_PHONE + "='" + numbervalue + "'";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    Phone phone = new Phone();
                    long phoneid = resultset.getInt(Phone.FIELD_CONTNOS_ROLEID);
                    long roleid = resultset.getInt(Phone.FIELD_CONTNOS_ROLEID);
                    phoneid = (phoneid * 10) + Phone.PHONE_LANDLINE_PRIMARY;
                    phone.setPhoneid(phoneid);
                    phone.setNumber(number);
                    phone.setRoleid(roleid);
                    phone.setStatus(Phone.STATUS_MATCHED_NUMBER);
                    phonelist.add(phone);
                } while (resultset.next());
                return phonelist;
            } else {
                return phonelist;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMPhoneException(MessageFormat.format("Phone SQL exception for number {0} value {1}", new Object[]{number, numbervalue}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMPhoneException(MessageFormat.format("Phone exception for number {0} value {1}", new Object[]{number, numbervalue}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public List<Phone> readSecondaryViaNumbervalue(Connection connection, long number, String numbervalue) throws CRMPhoneException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        List<Phone> phonelist = new ArrayList();
        try {
            if (number == 0) {
                return phonelist;
            }
            if (numbervalue.length() == 0) {
                return phonelist;
            }

            numbervalue=numbervalue.replace(";","").replace("%","").replace("&", "");
            query = "SELECT * FROM contnos c WHERE " + Phone.FIELD_CONTNOS_PHONE + "='" + numbervalue + "' AND phntyp in ('TELWRK', 'TELPRV')";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    Phone phone = new Phone();
                    long phoneid = resultset.getInt(Phone.FIELD_CONTNOS_ROLEID);
                    long roleid = resultset.getInt(Phone.FIELD_CONTNOS_ROLEID);
                    phoneid = (phoneid * 10) + Phone.PHONE_LANDLINE_SECONDARY;
                    phone.setPhoneid(phoneid);
                    phone.setNumber(number);
                    phone.setRoleid(roleid);
                    phone.setStatus(Phone.STATUS_MATCHED_NUMBER);
                    phonelist.add(phone);
                } while (resultset.next());
                return phonelist;
            } else {
                return phonelist;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMPhoneException(MessageFormat.format("Phone SQL exception for number {0} value {1}", new Object[]{number, numbervalue}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMPhoneException(MessageFormat.format("Phone exception for number {0} value {1}", new Object[]{number, numbervalue}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /**
     * The first landline-type phonenumber for a person is stored in the PHONE column
     * of the CONTACT table. The CONTACT table has got the field PVKEY as it's primary key.
     * This means amongst other things that a landline-type phonenumber is store on
     * a per-role basis; one person can have more than one role for each of which a
     * contactrecord and a corresponding phonenumber would exist.
     * The second landline-type phonenumber for a person is stored in the CONTACTNO
     * column of the CONTNOS table. The PHNTYP column will further indicate the nature of
     * the phone-information: 'TELPRV' for a lindline phonenumber at home, 'TELWRK' for
     * a landline phonenumber at work and 'FAXPRV' and 'FAXWRK' for facimile numbers.
     * The CONTNOS table info is primarily linked to a specific role, hence the phonenumbers
     * stored there are also role-related.
     *
     * @param connection Database connection
     * @param roleid Phonebean id
     * @param phonenumber Phonenumber
     * @return Created Phone
     */
    public Phone addLandline(Connection connection, Phone phone) throws CRMPhoneException {
        int phoneid;
        int personid;
        int addressid;
        String query;
        String sql;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            // Try to get the first landline-type phonenumber
            query = "SELECT * FROM contact c WHERE c.pvkey=" + phone.getRoleid();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                personid = resultset.getInt(Role.FIELD_CONTACT_PERSONID);
                addressid = resultset.getInt(Role.FIELD_CONTACT_ADDRESSID);
                String value = resultset.getString(Role.FIELD_CONTACT_PHONE);
                phoneid = resultset.getInt(Role.FIELD_CONTACT_ROLEID);
                resultset.close();
                statement.close();
                if (value == null) {
                    // Update PHONE field in CONTACT with new phonenumber
                    statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    sql = "UPDATE contact SET " + Role.FIELD_CONTACT_PHONE + "=" + "'0".
                            concat(String.valueOf(phone.getNumber()).replace(";","").replace("%","").replace("&", "")) 
                            + "' WHERE " + Role.FIELD_CONTACT_ROLEID + "=" + phone.getRoleid();
                    statement.executeUpdate(sql);
                    statement.close();
                    phoneid = (phoneid * 10) + Phone.PHONE_LANDLINE_PRIMARY;
                } else {
                    if (value.trim().length() > 0) {
                        // Phonenumber already in the CONTACT, add new in CONTNOS
                        sql = "SELECT * FROM contnos";
                        statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                        resultset = statement.executeQuery(sql);
                        resultset.moveToInsertRow();
                        Calendar c = Calendar.getInstance();
                        resultset.updateDate(Phone.FIELD_CONTNOS_ADDEDDATE, new java.sql.Date(c.getTimeInMillis()));
                        resultset.updateString(Phone.FIELD_CONTNOS_TYPE, Phone.TYPE_PHONE_HOME);
                        resultset.updateString(Phone.FIELD_CONTNOS_PHONE, "0".concat(String.valueOf(phone.getNumber())));
                        resultset.updateInt(Phone.FIELD_CONTNOS_ROLEID, (int) phone.getRoleid());
                        resultset.updateInt(Phone.FIELD_CONTNOS_ADDRESSID, addressid);
                        resultset.updateInt(Phone.FIELD_CONTNOS_PERSONID, personid);
                        KeyGenerator keygenerator = new KeyGenerator(connection);
                        int id = keygenerator.getNextKey(KeyGenerator.KEY_NEXTGENKEY);
                        phoneid = id;
                        resultset.updateInt(Phone.FIELD_CONTNOS_ID, id);
                        resultset.insertRow();
                        resultset.close();
                        statement.close();
                        phoneid = (phoneid * 10) + Phone.PHONE_LANDLINE_SECONDARY;
                    } else {
                        // Update PHONE field in CONTACT with new phonenumber
                        statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        sql = "UPDATE contact SET " + Role.FIELD_CONTACT_PHONE + "=" + "'0".
                                concat(String.valueOf(phone.getNumber()).replace(";","").replace("%","").replace("&", "")) + 
                                "' WHERE " + Role.FIELD_CONTACT_ROLEID + "=" + phone.getRoleid();
                        statement.executeUpdate(sql);
                        statement.close();
                        phoneid = (phoneid * 10) + Phone.PHONE_LANDLINE_PRIMARY;
                    }
                }
                phone.setPhoneid(phoneid);
                phone.setRoleid(phone.getRoleid());
                phone.setStatus(Phone.STATUS_NEW);
                return phone;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMPhoneException(MessageFormat.format("Phone SQL exception for phoneid {0} number {1}", new Object[]{phone.getPhoneid(), phone.getFormattedNumber()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMPhoneException(MessageFormat.format("Phone exception for phoneid {0} number {1}", new Object[]{phone.getPhoneid(), phone.getFormattedNumber()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public boolean removeLandline(Connection connection, long phoneid) throws CRMPhoneException {
        String sql;
        Statement statement = null;
        ResultSet resultset = null;
        int rowcount;
        try {
            int type = (int) phoneid % 10;
            phoneid = phoneid / 10;
            switch (type) {
                case Phone.PHONE_LANDLINE_PRIMARY:
                    // Update PHONE field in CONTACT with new phonenumber
                    statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    sql = "UPDATE contact SET " + Role.FIELD_CONTACT_PHONE + "=" + "'' WHERE " + Role.FIELD_CONTACT_ROLEID + "=" + phoneid;
                    rowcount = statement.executeUpdate(sql);
                    if (rowcount > 0) {
                        return true;
                    } else {
                        return false;
                    }
                case Phone.PHONE_LANDLINE_SECONDARY:
                    statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    sql = "UPDATE contnos SET " + Phone.FIELD_CONTNOS_TYPE + "=" + "'DELETED' WHERE " + Phone.FIELD_CONTNOS_ID + "=" + phoneid;
                    rowcount = statement.executeUpdate(sql);
                    if (rowcount > 0) {
                        return true;
                    } else {
                        return false;
                    }
                default:
                    return false;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMPhoneException(MessageFormat.format("Phone SQL exception for phoneid {0}", new Object[]{phoneid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMPhoneException(MessageFormat.format("Phone exception for phoneid {0}", new Object[]{phoneid}), e);
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
            Logger.getLogger(PhoneLandlineSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }
}
