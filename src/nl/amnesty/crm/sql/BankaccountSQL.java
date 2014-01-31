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
import nl.amnesty.crm.entity.Address;
import nl.amnesty.crm.entity.Bankaccount;
import nl.amnesty.crm.entity.Role;
import nl.amnesty.crm.exception.CRMBankaccountException;

/**
 *
 * @author evelzen
 */
public class BankaccountSQL {

    private static final String MSG_EXCEPTION = "Fatal error while updating bankaccount object for {0}";
    private static final String MSG_EXCEPTION_SQL = "Fatal SQL error while updating bankaccount object for {0}";
    private static final boolean USETIMESTAMP = false;

    /*
     * ----------------------------------------
     * Standard CRUD methods
     * ----------------------------------------
     */
    public Bankaccount create(Connection connection, Bankaccount bankaccount) throws CRMBankaccountException {
        if (bankaccount == null) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Bankaccount is null");
            return null;
        }
        if (bankaccount.getNumber() == 0) {
            if (bankaccount.getIban() == null) {
                Logger.getLogger(BankaccountSQL.class.getName()).log(Level.WARNING, "Bankaccount number is 0 and IBAN is null");
                return new Bankaccount();
            } else {
                if (bankaccount.getIban().equals("")) {
                    Logger.getLogger(BankaccountSQL.class.getName()).log(Level.WARNING, "Bankaccount number is 0 and IBAN is empty");
                    return new Bankaccount();
                }
            }
        }
        if (bankaccount.getRoleid() == 0) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Roleid for bankaccount {0} is 0", bankaccount.getFormattedNumber());
            return null;
        }
        Bankaccount bankaccountmatched;
        Statement statement = null;
        ResultSet resultset = null;
        long bankaccountid = 0;
        try {
            // try to match bankaccount
            bankaccountmatched = match(connection, bankaccount);
            if (bankaccountmatched != null) {
                if (bankaccountmatched.isAcceptableMatch()) {
                    return bankaccountmatched;
                }
            }

            // At this point no matching bankaccount is found in CRM, so let's create a new one
            String SQL = "SELECT * FROM bankacs";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();

            resultset = setResultsetColumns(connection, resultset, bankaccount);

            if (resultset != null) {
                if (bankaccount.getBankaccountid() == 0) {
                    KeyGenerator keygenerator = new KeyGenerator(connection);
                    bankaccountid = keygenerator.getNextKey(KeyGenerator.KEY_NEXTBANKKEY);
                    // Set the bankaccount id in the bankaccount object
                    bankaccount.setBankaccountid(bankaccountid);
                }
                resultset.updateInt(Bankaccount.FIELD_BANKACS_ID, (int) bankaccountid);
                resultset.updateString(Bankaccount.FIELD_BANKACS_IBAN_NUMBER, bankaccount.getIban());
                resultset.insertRow();
                bankaccount.setStatus(Bankaccount.STATUS_NEW);
                resultset.close();
            }
            statement.close();
            return bankaccount;
        } catch (SQLException sqle) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount SQL exception {0} {1}", new Object[]{bankaccount.getBankaccountid(), bankaccount.getFormattedNumber()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount exception {0} {1}", new Object[]{bankaccount.getBankaccountid(), bankaccount.getFormattedNumber()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public Bankaccount read(Connection connection, long bankaccountid) throws CRMBankaccountException {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        if (bankaccountid == 0) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Bankaccountid is 0");
            return null;
        }
        try {
            query = "SELECT * FROM bankacs b WHERE " + Bankaccount.FIELD_BANKACS_ID + "=" + bankaccountid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                Bankaccount bankaccount = setBeanProperties(resultset);
                return bankaccount;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount SQL exception {0}", new Object[]{bankaccountid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount exception {0}", new Object[]{bankaccountid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public boolean update(Connection connection, Bankaccount bankaccount) throws CRMBankaccountException {
        String sql = "";
        Statement statement = null;
        if (bankaccount == null) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Bankaccount is null");
            return false;
        }
        if (bankaccount.getBankaccountid() == 0) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Bankaccountid for bankaccount {0} is 0", bankaccount.getFormattedNumber());
            return false;
        }
        try {
            if (bankaccount.getNumber() != 0) {
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                sql = "UPDATE bankacs SET "
                        + Bankaccount.FIELD_BANKACS_BANKACCOUNT_NUMBER + "=" + bankaccount.getNumber()
                        + "WHERE " + Bankaccount.FIELD_BANKACS_ID + "=" + bankaccount.getBankaccountid();
                statement.executeUpdate(sql);
                statement.close();
            }
            if (bankaccount.getIban() != null) {
                if (!bankaccount.getIban().equals("")) {
                    statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    sql = "UPDATE bankacs SET "
                            + Bankaccount.FIELD_BANKACS_IBAN_NUMBER + "=" + bankaccount.getIban()
                            + "WHERE " + Bankaccount.FIELD_BANKACS_ID + "=" + bankaccount.getBankaccountid();
                    statement.executeUpdate(sql);
                    statement.close();
                }
            }
            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount SQL exception {0} {1}", new Object[]{bankaccount.getBankaccountid(), bankaccount.getFormattedNumber()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount exception {0} {1}", new Object[]{bankaccount.getBankaccountid(), bankaccount.getFormattedNumber()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    public boolean delete(Connection connection, long bankaccountid) {
        // TODO: Implementation for deleting bankaccount, this is just a stub
        return false;
    }

    /*
     * ----------------------------------------
     * Object matching
     * ----------------------------------------
     */
    public Bankaccount match(Connection connection, Bankaccount bankaccount) throws CRMBankaccountException {
        List<Bankaccount> bankaccountfoundlist;
        Bankaccount bankaccountfound;
        try {
            // First of all let's see if bankaccount can be found in CRM via the bankaccount id
            if (bankaccount.getBankaccountid() != 0) {
                bankaccountfound = read(connection, bankaccount.getBankaccountid());
                if (bankaccountfound != null) {
                    bankaccountfound.setStatus(Bankaccount.STATUS_MATCHED_ID);
                    return bankaccountfound;
                }
            }
            // Next best thing is to search for the role and bankaccount number
            if (bankaccount.getNumber() != 0) {
                bankaccountfound = readViaNumberRoleid(connection, bankaccount.getNumber(), bankaccount.getRoleid(), bankaccount.getIban());
                if (bankaccountfound != null) {
                    bankaccountfound.setStatus(Bankaccount.STATUS_MATCHED_ROLE_NUMBER);
                    return bankaccountfound;
                }
            }
            // Next best thing is to search for the bankaccount number
            if (bankaccount.getNumber() != 0) {
                bankaccountfoundlist = readViaNumber(connection, bankaccount.getNumber());
                if (bankaccountfoundlist != null) {
                    for (Bankaccount bankaccountelement : bankaccountfoundlist) {
                        if (bankaccountelement.getNumber() == bankaccount.getNumber()) {
                            bankaccountfound = bankaccountelement;
                            bankaccountfound.setStatus(Bankaccount.STATUS_MATCHED_NUMBER);
                            return bankaccountfound;
                        }
                    }
                }
            }

            bankaccount.setStatus(Bankaccount.STATUS_MATCHED_NONE);
            return bankaccount;

        } catch (Exception e) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount exception {0} {1}", new Object[]{bankaccount.getBankaccountid(), bankaccount.getFormattedNumber()}), e);
        }
    }

    /*
     * ----------------------------------------
     * READ methods
     * ----------------------------------------
     */
    public List<Bankaccount> readBankaccountlistViaRoleid(Connection connection, long roleid) throws CRMBankaccountException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        List<Bankaccount> bankaccountlist = new ArrayList();
        if (roleid == 0) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Roleid is 0");
            return bankaccountlist;
        }
        try {
            query = "SELECT * FROM bankacs b WHERE " + Bankaccount.FIELD_BANKACS_ROLEID + "=" + roleid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    Bankaccount bankaccount = setBeanProperties(resultset);
                    bankaccountlist.add(bankaccount);
                } while (resultset.next());
                return bankaccountlist;
            } else {
                return bankaccountlist;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount SQL exception for roleid {0}", new Object[]{roleid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount exception for roleid {0}", new Object[]{roleid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private Bankaccount readViaNumberRoleid(Connection connection, long number, long roleid, String iban) throws CRMBankaccountException {
        Bankaccount bankaccount = null;
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        if (roleid == 0) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Roleid is 0");
            return null;
        }
        if (number == 0) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Number is 0");
            return null;
        }
        try {
            String queryroleid = Bankaccount.FIELD_BANKACS_ROLEID.concat("=").concat(String.valueOf(roleid)).replace(";", "").replace("%", "").replace("&", "");
            String querynumber = Bankaccount.FIELD_BANKACS_BANKACCOUNT_NUMBER.concat("='").concat(String.valueOf(number)).concat("'").replace(";", "").replace("%", "").replace("&", "");
            String queryiban = Bankaccount.FIELD_BANKACS_IBAN_NUMBER.concat("='").concat(iban).concat("'").replace(";", "").replace("%", "").replace("&", "");

            query = "SELECT * FROM bankacs WHERE ".concat(queryroleid).concat(" AND ").concat(querynumber);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                bankaccount = setBeanProperties(resultset);
            } else {
                query = "SELECT * FROM bankacs WHERE ".concat(queryroleid).concat(" AND ").concat(queryiban);
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                resultset = statement.executeQuery(query);
                if (resultset.first()) {
                    bankaccount = setBeanProperties(resultset);
                }
            }
            return bankaccount;
        } catch (SQLException sqle) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount SQL exception for roleid {0} number {1}", new Object[]{roleid, number}), sqle);
        } catch (Exception e) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount exception roleid {0} number {1}", new Object[]{roleid, number}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private List<Bankaccount> readViaNumber(Connection connection, long number) throws CRMBankaccountException {
        List<Bankaccount> bankaccountlist = new ArrayList();
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        if (number == 0) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Number is 0");
            return bankaccountlist;
        }
        try {
            String querynumber = Bankaccount.FIELD_BANKACS_BANKACCOUNT_NUMBER.concat("='").concat(String.valueOf(number)).concat("'").replace(";", "").replace("%", "").replace("&", "");

            query = "SELECT * FROM bankacs WHERE ".concat(querynumber);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    Bankaccount bankaccount = setBeanProperties(resultset);
                    bankaccountlist.add(bankaccount);
                } while (resultset.next());
                return bankaccountlist;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount SQL exception {0}", new Object[]{number}), sqle);
        } catch (Exception e) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount exception {0}", new Object[]{number}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     * ----------------------------------------
     * Misc methods
     * ----------------------------------------
     */
    private ResultSet setResultsetColumns(Connection connection, ResultSet resultset, Bankaccount bankaccount) throws CRMBankaccountException {
        if (resultset == null) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Resultset is null");
            return null;
        }
        if (bankaccount == null) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Bankaccount is null");
            return null;
        }
        try {
            resultset.updateLong(Bankaccount.FIELD_BANKACS_ROLEID, bankaccount.getRoleid());

            resultset.updateString(Bankaccount.FIELD_BANKACS_SOURCE, "");

            Calendar calendar = Calendar.getInstance();
            TimeZone timezone = TimeZone.getDefault();
            calendar.setTimeZone(timezone);

            if (USETIMESTAMP) {
                Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
                resultset.updateTimestamp(Bankaccount.FIELD_BANKACS_DATE_ADDDATE, timestamp);
                resultset.updateTimestamp(Bankaccount.FIELD_BANKACS_DATE_CHANGEDATE, timestamp);
            } else {
                java.sql.Date today = new java.sql.Date(calendar.getTime().getTime());
                resultset.updateDate(Bankaccount.FIELD_BANKACS_DATE_ADDDATE, today);
                resultset.updateDate(Bankaccount.FIELD_BANKACS_DATE_CHANGEDATE, today);
            }

            resultset.updateBoolean(Bankaccount.FIELD_BANKACS_DIRECTDEBIT_CANCEL, false);
            resultset.updateBoolean(Bankaccount.FIELD_BANKACS_DIRECTDEBIT_FIRST, true);
            resultset.updateString(Bankaccount.FIELD_BANKACS_DIRECTDEBIT_REJECTED, "N");
            resultset.updateString(Bankaccount.FIELD_BANKACS_DIRECTDEBIT_STATUS, "Active");

            resultset.updateString(Bankaccount.FIELD_BANKACS_BANK_REFERENCE, "");
            resultset.updateString(Bankaccount.FIELD_BANKACS_BANK_CONTACT, "The Manager");

            String number = String.valueOf(bankaccount.getNumber());
            resultset.updateString(Bankaccount.FIELD_BANKACS_BANKACCOUNT_NUMBER, number);
            RoleSQL rolesql = new RoleSQL();
            Role role = rolesql.read(connection, bankaccount.getRoleid());
            if (role != null) {
                resultset.updateString(Bankaccount.FIELD_BANKACS_BANKACCOUNT_NAME, role.getFormattedFormalName());
                Address address = role.getAddress();
                if (address != null) {
                    resultset.updateString(Bankaccount.FIELD_BANKACS_BANKACCOUNT_POSTALCODE, address.getFormattedPostalcode());
                    resultset.updateString(Bankaccount.FIELD_BANKACS_BANKACCOUNT_STREET, address.getFormattedStreetHouseno());
                    resultset.updateString(Bankaccount.FIELD_BANKACS_BANKACCOUNT_CITY, address.getFormattedCity());
                }
                resultset.updateString(Bankaccount.FIELD_BANKACS_BANKACCOUNT_PHONENUMBER, role.getFormattedPhonenumber());
                resultset.updateString(Bankaccount.FIELD_BANKACS_BANKACCOUNT_PROVINCE, "");
                resultset.updateString(Bankaccount.FIELD_BANKACS_BANKACCOUNT_LABEL, role.getFormattedSalutation());
            }

            /*
             BigDecimal bigdecimal = BigDecimal.valueOf(1000, 2); // Ten Euro
             resultset.updateBigDecimal(SQL, BigDecimal.ONE);
             * 
             */

        } catch (SQLException sqle) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount SQL exception {0} {1}", new Object[]{bankaccount.getBankaccountid(), bankaccount.getFormattedNumber()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMBankaccountException(MessageFormat.format("Bankaccount exception {0} {1}", new Object[]{bankaccount.getBankaccountid(), bankaccount.getFormattedNumber()}), e);
        }
        return resultset;

    }

    private Bankaccount setBeanProperties(ResultSet resultset) {
        Bankaccount bankaccount = new Bankaccount();
        if (resultset == null) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, "Resultset is null");
            return null;
        }
        try {
            bankaccount.setBankaccountid(resultset.getInt(Bankaccount.FIELD_BANKACS_ID));
            if (resultset.getString(Bankaccount.FIELD_BANKACS_IBAN_NUMBER) != null) {
                bankaccount.setIban(resultset.getString(Bankaccount.FIELD_BANKACS_IBAN_NUMBER));
            }
            String bankaccountnumbervalue = resultset.getString(Bankaccount.FIELD_BANKACS_BANKACCOUNT_NUMBER);
            if (bankaccountnumbervalue == null) {
                bankaccount.setNumber(0);
            } else {
                if (!bankaccountnumbervalue.isEmpty()) {
                    try {
                        int bankaccountnumber = Integer.valueOf(bankaccountnumbervalue);
                        bankaccount.setNumber(bankaccountnumber);
                    } catch (NumberFormatException nfe) {
                        bankaccount.setNumber(0);
                    }
                } else {
                    bankaccount.setNumber(0);
                }
            }
            return bankaccount;
        } catch (SQLException sqle) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, sqle);
            return null;
        } catch (Exception e) {
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    /**
     *
     * @param statement SQL statement
     */
    private void closeSQL(Statement statement, ResultSet resultset) {
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
            Logger.getLogger(BankaccountSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }
}
