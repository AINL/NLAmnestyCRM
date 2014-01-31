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
import nl.amnesty.crm.entity.Person;
import nl.amnesty.crm.entity.Phone;
import nl.amnesty.crm.entity.Role;
import nl.amnesty.crm.exception.CRMPhoneException;

/**
 * Creating a phone entity in the FundraisingCRM system is not a straightforward
 * task. The logical location within the database where the phonenumber
 * information is stored largely depends on the type of phonenumber (mobile or
 * landline) as well on the fact if it is a first phonenumber that is stored or
 * a subsequent phonenumber for any given person.
 *
 * The first landline-type phonenumber for a person is stored in the PHONE
 * column of the CONTACT table. The CONTACT table has got the field PVKEY as
 * it's primary key. This means amongst other things that a landline-type
 * phonenumber is store on a per-role basis; one person can have more than one
 * role for each of which a contactrecord and a corresponding phonenumber would
 * exist. The second landline-type phonenumber for a person is stored in the
 * CONTACTNO column of the CONTNOS table. The PHNTYP column will further
 * indicate the nature of the phone-information: 'TELPRV' for a lindline
 * phonenumber at home, 'TELWRK' for a landline phonenumber at work and 'FAXPRV'
 * and 'FAXWRK' for facimile numbers. The CONTNOS table info is primarily linked
 * to a specific role, hence the phonenumbers stored there are also
 * role-related.
 *
 * The first mobile phonenumber for a person is stored in the MOBLEPHNE column
 * of the PEOPLE table. The primary key of the PEOPLE table is the PEOPLEKEY
 * field. As a consequence, the first mobile phonenumber is not role-related,
 * but person-related. One person may have more than one role but it remains
 * unclear to which of those roles the first mobile number for that person is
 * linked. All we know is that a person owns a certain mobile number, but not to
 * which role it relates. The second and subsequent mobilephonenumbers are - in
 * analogy with the landlines - stored in the CONTNOS table. The PHNTYP column
 * of this CONTNOS table will be set to 'MOBIEL' to indicate that this is indeed
 * a mobile number. Second and subsequent mobile phonenumbers are as a result
 * role-centered in contrast with the first mobile phonenumber which is
 * person-centered.
 *
 * @author ed
 */
public class PhoneSQL {

    private static final String MSG_EXCEPTION = "Fatal error while updating phone object for {0}";
    private static final String MSG_EXCEPTION_SQL = "Fatal SQL error while updating phone object for {0}";

    /*
     * Standard CRUD methods
     */
    public Phone create(Connection connection, Phone phone) throws CRMPhoneException {
        if (phone == null) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, "Phone is null");
            return null;
        }
        if (phone.getNumber() == 0) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.WARNING, "Phone number is 0");
            return new Phone();
        }
        if (phone.getRoleid() == 0) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, "Roleid for phone is 0");
            return null;
        }
        Phone phonematched;
        try {
            phonematched = match(connection, phone);
            if (phonematched != null) {
                if (phonematched.isAcceptableMatch()) {
                    return phonematched;
                } else {
                    phone = add(connection, phone);
                    phone.setStatus(Phone.STATUS_NEW);
                    return phone;
                }
            } else {
                phone = add(connection, phone);
                phone.setStatus(Phone.STATUS_NEW);
                return phone;
            }
        } catch (Exception e) {
            throw new CRMPhoneException(MSG_EXCEPTION, e);
        }
    }

    /**
     * Read the phone object for a specific phone id. The implementation of the
     * phone object in our CRM system is far from straightforward. The phone
     * objects are not stored in one specific location but is scattered
     * throughout the system depending on the type of phonenumber (landline or
     * mobile). Primary phonenumbers are stored in a different location
     * (different database table) than the secondary number.
     *
     * @param connection SQL Database connection
     * @param phoneid Id of the phone object
     * @return Phone object if it is found, null otherwise
     */
    public Phone read(Connection connection, long phoneid) throws CRMPhoneException {
        String value = "";
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        Phone phone = new Phone();
        try {
            // TODO: replace calculation with phone reference as in line below.
            //phone.getType();
            int type = (int) phoneid % 10;
            phoneid = phoneid / 10;
            switch (type) {
                case Phone.PHONE_LANDLINE_PRIMARY:
                    query = "SELECT * FROM contact c WHERE " + Role.FIELD_CONTACT_ROLEID + "=" + phoneid;
                    break;
                case Phone.PHONE_LANDLINE_SECONDARY:
                    query = "SELECT * FROM contnos c WHERE " + Phone.FIELD_CONTNOS_ID + "=" + phoneid + " AND phntyp in ('TELWRK', 'TELPRV')";
                    break;
                case Phone.PHONE_MOBILE_PRIMARY:
                    query = "SELECT * FROM contact c JOIN people p ON c.peoplekey = p.peoplekey WHERE p." + Person.FIELD_PEOPLE_ID + "=" + phoneid;
                    break;
                case Phone.PHONE_MOBILE_SECONDARY:
                    query = "SELECT * FROM contnos c WHERE " + Phone.FIELD_CONTNOS_ID + "=" + phoneid + " AND phntyp = 'MOBIEL'";
                    break;
                default:
                    return null;
            }
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                long roleid = 0;
                switch (type) {
                    case Phone.PHONE_LANDLINE_PRIMARY:
                        value = resultset.getString(Role.FIELD_CONTACT_PHONE);
                        roleid = resultset.getInt(Role.FIELD_CONTACT_ROLEID);
                        break;
                    case Phone.PHONE_LANDLINE_SECONDARY:
                        value = resultset.getString(Phone.FIELD_CONTNOS_PHONE);
                        roleid = resultset.getInt(Phone.FIELD_CONTNOS_ROLEID);
                        break;
                    case Phone.PHONE_MOBILE_PRIMARY:
                        value = resultset.getString(Phone.FIELD_PEOPLE_PHONE);
                        roleid = resultset.getInt(Role.FIELD_CONTACT_ROLEID);
                        break;
                    case Phone.PHONE_MOBILE_SECONDARY:
                        value = resultset.getString(Phone.FIELD_CONTNOS_PHONE);
                        roleid = resultset.getInt(Phone.FIELD_CONTNOS_ROLEID);
                        break;
                    default:
                        break;
                }
                phone.setPhoneid(phoneid);
                phone.parseNumber(value);
                phone.setRoleid(roleid);
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

    public boolean update(Connection connection, Phone phone) {
        // TODO: Implementation for updating phone, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long phoneid) throws CRMPhoneException {
        return remove(connection, phoneid);
    }

    /*
     * Object matching
     */
    public Phone match(Connection connection, Phone phone) throws CRMPhoneException {
        List<Phone> phonefoundlist;
        Phone phonefound;
        try {
            // First of all let's see if phone can be found in CRM via the phone id
            if (phone.getPhoneid() != 0) {
                phonefound = read(connection, phone.getPhoneid());
                if (phonefound != null) {
                    phonefound.setStatus(Phone.STATUS_MATCHED_ID);
                    return phonefound;
                }
            }
            // Next best thing is to search for the phone number
            if (phone.getNumber() != 0) {
                phonefoundlist = readPhonelistViaNumber(connection, phone.getNumber());
                if (phonefoundlist != null) {
                    if (!phonefoundlist.isEmpty()) {
                        phonefound = phonefoundlist.get(0);
                        phonefound.setStatus(Phone.STATUS_MATCHED_NUMBER);
                        return phonefound;
                    }
                }
            }
            phone.setStatus(Phone.STATUS_MATCHED_NONE);
            return phone;
        } catch (Exception e) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMPhoneException(MessageFormat.format("Phone exception for phoneid {0} number {1}", new Object[]{phone.getPhoneid(), phone.getFormattedNumber()}), e);
        }
    }

    /*
     * CREATE methods
     */
    private static Phone add(Connection connection, Phone phone) throws CRMPhoneException {
        if (String.valueOf(phone.getNumber()).startsWith("6")) {
            PhoneMobileSQL phonemobilesql = new PhoneMobileSQL();
            return phonemobilesql.addMobile(connection, phone);
        } else {
            PhoneLandlineSQL phonelandlinesql = new PhoneLandlineSQL();
            return phonelandlinesql.addLandline(connection, phone);
        }
    }

    /*
     *
     * READ methods
     *
     */
    public List<Phone> readPhonelistViaRoleid(Connection connection, long roleid) throws CRMPhoneException {
        List<Phone> phonelist = new ArrayList();
        PhoneLandlineSQL phonelandlinesql = new PhoneLandlineSQL();
        phonelist.addAll(phonelandlinesql.readPrimaryViaRoleid(connection, roleid));
        phonelist.addAll(phonelandlinesql.readSecondaryViaRoleid(connection, roleid));
        PhoneMobileSQL phonemobilesql = new PhoneMobileSQL();
        phonelist.addAll(phonemobilesql.readPrimaryViaRoleid(connection, roleid));
        phonelist.addAll(phonemobilesql.readSecondaryViaRoleid(connection, roleid));
        return phonelist;
    }

    public List<Phone> readPhonelistViaNumber(Connection connection, long number) throws CRMPhoneException {
        List<String> phonevaluelist = new ArrayList();
        List<Phone> landlineprimarylist;
        List<Phone> landlinesecondarylist;
        List<Phone> mobileprimarylist;
        List<Phone> mobilesecondarylist;
        List<Phone> phonelist = new ArrayList();
        try {
            phonevaluelist.add(phoneVariationStandard(number, Phone.PHONE_LANDLINE));
            phonevaluelist.add(phoneVariationHyphen(number, Phone.PHONE_LANDLINE));
            //phonevaluelist.add(Phone.phoneVariationSpace(number, Phone.PHONE_LANDLINE));
            //phonevaluelist.add(Phone.phoneVariationUnderscore(number, Phone.PHONE_LANDLINE));
            PhoneLandlineSQL phonelandlinesql = new PhoneLandlineSQL();
            for (String phonevalue : phonevaluelist) {
                landlineprimarylist = phonelandlinesql.readPrimaryViaNumbervalue(connection, number, phonevalue);
                if (!landlineprimarylist.isEmpty()) {
                    phonelist.addAll(landlineprimarylist);
                }
                landlinesecondarylist = phonelandlinesql.readSecondaryViaNumbervalue(connection, number, phonevalue);
                if (!landlinesecondarylist.isEmpty()) {
                    phonelist.addAll(landlinesecondarylist);
                }
            }

            phonevaluelist.clear();
            phonevaluelist.add(phoneVariationStandard(number, Phone.PHONE_MOBILE));
            phonevaluelist.add(phoneVariationHyphen(number, Phone.PHONE_MOBILE));
            //phonevaluelist.add(Phone.phoneVariationSpace(number, Phone.PHONE_MOBILE));
            //phonevaluelist.add(Phone.phoneVariationUnderscore(number, Phone.PHONE_MOBILE));
            PhoneMobileSQL phonemobilesql = new PhoneMobileSQL();
            for (String phonevalue : phonevaluelist) {
                mobileprimarylist = phonemobilesql.readPrimaryViaNumbervalue(connection, number, phonevalue);
                if (!mobileprimarylist.isEmpty()) {
                    phonelist.addAll(mobileprimarylist);
                }
                mobilesecondarylist = phonemobilesql.readSecondaryViaNumbervalue(connection, number, phonevalue);
                if (!mobilesecondarylist.isEmpty()) {
                    phonelist.addAll(mobilesecondarylist);
                }
            }
            return phonelist;
        } catch (Exception e) {
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMPhoneException(MessageFormat.format("Phone exception for number {0}", new Object[]{number}), e);
        }
    }

    /*
     * DELETE methods
     */
    private static boolean remove(Connection connection, long phoneid) throws CRMPhoneException {
        int type = (int) phoneid % 10;
        phoneid = phoneid / 10;
        PhoneLandlineSQL phonelandlinesql = new PhoneLandlineSQL();
        PhoneMobileSQL phonemobilesql = new PhoneMobileSQL();
        switch (type) {
            case Phone.PHONE_LANDLINE_PRIMARY:
                return phonelandlinesql.removeLandline(connection, phoneid);
            case Phone.PHONE_LANDLINE_SECONDARY:
                return phonelandlinesql.removeLandline(connection, phoneid);
            case Phone.PHONE_MOBILE_PRIMARY:
                return phonemobilesql.removeMobile(connection, phoneid);
            case Phone.PHONE_MOBILE_SECONDARY:
                return phonemobilesql.removeMobile(connection, phoneid);
            default:
                return false;
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
            Logger.getLogger(PhoneSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }

    private String phoneVariationStandard(long number, int type) {
        String phonevalue = "0".concat(String.valueOf(number));
        return phonevalue;
    }

    private String phoneVariationUnderscore(long number, int type) {
        return phoneVariationGeneric(number, type, "_");
    }

    private String phoneVariationHyphen(long number, int type) {
        return phoneVariationGeneric(number, type, "-");
    }

    private String phoneVariationSpace(long number, int type) {
        return phoneVariationGeneric(number, type, " ");
    }

    private String phoneVariationGeneric(long number, int type, String delimit) {
        int clip;
        switch (type) {
            case Phone.PHONE_LANDLINE:
                clip = 4;
                break;
            case Phone.PHONE_MOBILE:
                clip = 2;
                break;
            default:
                return "";
        }
        String phonevalue = "0".concat(String.valueOf(number));
        if (phonevalue.length() > clip) {
            phonevalue = phonevalue.substring(0, clip).concat(delimit).concat(phonevalue.substring(clip));
        }
        return phonevalue;
    }
}
