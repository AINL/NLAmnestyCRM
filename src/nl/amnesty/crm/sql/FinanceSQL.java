/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.db.KeyGenerator;
import nl.amnesty.crm.entity.Finance;
import nl.amnesty.crm.exception.CRMFinanceException;

/**
 *
 * @author evelzen
 */
public class FinanceSQL {

    private static final boolean USETIMESTAMP = false;

    /*
     * ---------------------------------------- Standard CRUD methods
     * ----------------------------------------
     */
    public Finance create(Connection connection, Finance finance) throws CRMFinanceException {
        Finance financematched;
        Statement statement = null;
        ResultSet resultset = null;
        long financeid = 0;
        long financeidold = 0;
        try {
            if (finance == null) {
                Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, "Finance is null");
                return null;
            }
            if (finance.getRoleid() == 0) {
                Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, "Roleid for finance {0} is 0", new Object[]{finance.getFormattedAmount()});
                return null;
            }
            // try to match finance
            financematched = match(connection, finance);
            if (financematched != null) {
                if (financematched.isAcceptableMatch()) {
                    return financematched;
                }
            }

            // At this point no matching finance is found in CRM, so let's create a new one
            String SQL = "SELECT * FROM finance";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();
            // All of the interesting stuff takes place in setResultsetColumns...
            resultset = setResultsetColumns(resultset, finance);
            if (resultset != null) {
                if (finance.getFinanceid() == 0) {
                    KeyGenerator keygenerator = new KeyGenerator(connection);
                    financeid = keygenerator.getNextKey(KeyGenerator.KEY_NEXTPAYKEY);
                    // Set the finance id in the finance object
                    finance.setFinanceid(financeid);
                }
                resultset.updateLong(Finance.FIELD_FINANCE_ID, (long) financeid);

                resultset.insertRow();
                finance.setStatusRecord(Finance.STATUS_NEW);
                resultset.close();

                if (financeidold != 0) {
                    // We found an old record that was replaced by a new finance, let's deleted the old one...
                    delete(connection, financeidold);
                }
            }
            statement.close();
            return finance;
        } catch (SQLException sqle) {
            Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMFinanceException(MessageFormat.format("Finance SQL exception for financeid {0} amount {1}", new Object[]{finance.getFinanceid(), finance.getAmount()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMFinanceException(MessageFormat.format("Finance exception for financeid {0} amount {1}", new Object[]{finance.getFinanceid(), finance.getAmount()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public Finance read(Connection connection, float financeid) throws CRMFinanceException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        Finance finance = new Finance();
        try {
            query = "SELECT * FROM finance WHERE " + Finance.FIELD_FINANCE_ID + "=" + financeid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                finance = setBeanProperties(resultset);
                return finance;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMFinanceException(MessageFormat.format("Finance SQL exception for financeid {0} amount {1}", new Object[]{finance.getFinanceid(), finance.getAmount()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMFinanceException(MessageFormat.format("Finance exception for financeid {0} amount {1}", new Object[]{finance.getFinanceid(), finance.getAmount()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public boolean update(Connection connection, Finance finance) throws CRMFinanceException {
        // Code goes here...
        return false;
    }

    /**
     * We will not really delete entries but mark them as ended instead
     *
     * @param connection
     * @param financeid
     * @return
     * @throws CRMFinanceException
     */
    public boolean delete(Connection connection, long financeid) throws CRMFinanceException {
        throw new UnsupportedOperationException("Deletion of finance is not possible");
    }

    /*
     * ---------------------------------------- Object matching
     * ----------------------------------------
     */
    public Finance match(Connection connection, Finance finance) throws CRMFinanceException {
        Finance financefound;
        try {
            // First of all let's see if finance can be found in CRM via the finance id
            if (finance.getFinanceid() != 0) {
                financefound = read(connection, finance.getFinanceid());
                if (financefound != null) {
                    financefound.setStatusRecord(Finance.STATUS_MATCHED_ID);
                    return financefound;
                }
            }

            finance.setStatusRecord(Finance.STATUS_MATCHED_NONE);
            return finance;
        } catch (Exception e) {
            Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMFinanceException(MessageFormat.format("Finance exception for financeid {0} amount {1}", new Object[]{finance.getFinanceid(), finance.getAmount()}), e);
        }
    }
  
    /*
     * ---------------------------------------- READ methods
     * ----------------------------------------
     */

    /*
     * ---------------------------------------- Misc methods
     * ----------------------------------------
     */
    /**
     *
     * @param resultset
     * @param role
     * @param bankaccount
     * @param finance
     * @return
     */
    private ResultSet setResultsetColumns(ResultSet resultset, Finance finance) throws CRMFinanceException {
        try {
            resultset.updateLong(Finance.FIELD_FINANCE_ROLE_ID, finance.getRoleid());
            resultset.updateLong(Finance.FIELD_FINANCE_BANKKEY, finance.getBankkey());
            resultset.updateString(Finance.FIELD_FINANCE_ORGBANK, finance.getOrgbank());
            resultset.updateString(Finance.FIELD_FINANCE_PAYSOURCE, finance.getPaysource());
            resultset.updateString(Finance.FIELD_FINANCE_PAYTYPE, finance.getPaytype());
            resultset.updateString(Finance.FIELD_FINANCE_PAYMETHOD, finance.getPaymethod());
            
            Calendar calendar_today = Calendar.getInstance();
            Calendar calendar_yearfromnow = Calendar.getInstance();
            Calendar calendar_firstofmonth = Calendar.getInstance();
            Calendar calendar_farfuture = Calendar.getInstance();
            TimeZone timezone = TimeZone.getDefault();

            calendar_today.setTimeZone(timezone);
            calendar_yearfromnow.setTimeZone(timezone);
            calendar_yearfromnow.add(Calendar.YEAR, 1);
            calendar_firstofmonth.setTimeZone(timezone);
            // Direct debits can start beginning a week after someone hase become a member, becomes two weeks with SEPA
            calendar_firstofmonth.add(Calendar.DATE, 14);
            // The first month at the end of which the direct debit can start.
            int month = calendar_firstofmonth.get(Calendar.MONTH);
            // Set to the first day of this or the following month, depending if remainder of this month is less than 14 days.
            calendar_firstofmonth.set(Calendar.DATE, 1);
            calendar_farfuture.set(2099, Calendar.DECEMBER, 31);

            if (USETIMESTAMP) {
                Timestamp today = new Timestamp(calendar_today.getTimeInMillis());
                Timestamp firstofmonth = new Timestamp(calendar_firstofmonth.getTimeInMillis());
                resultset.updateTimestamp(Finance.FIELD_FINANCE_DATEDUE, firstofmonth);
                resultset.updateTimestamp(Finance.FIELD_FINANCE_DATEADDED, today);
                resultset.updateTimestamp(Finance.FIELD_FINANCE_TRANSDATE, firstofmonth);
                //resultset.updateTimestamp(Finance.FIELD_FINANCE_TIMEADDED, today);
            } else {
                java.sql.Date today = new java.sql.Date(calendar_today.getTime().getTime());
                java.sql.Date firstofmonth = new java.sql.Date(calendar_firstofmonth.getTime().getTime());
                resultset.updateDate(Finance.FIELD_FINANCE_DATEDUE, firstofmonth);
                resultset.updateDate(Finance.FIELD_FINANCE_DATEADDED, today);
                resultset.updateDate(Finance.FIELD_FINANCE_TRANSDATE, firstofmonth);
                //resultset.updateDate(Finance.FIELD_FINANCE_TIMEADDED, today);
            }

            resultset.updateLong(Finance.FIELD_FINANCE_AMOUNTDUE, (long) finance.getAmountdue());
            resultset.updateString(Finance.FIELD_FINANCE_STATUS, "A");
            //resultset.updateString(Finance.FIELD_FINANCE_PAIDBY, ""); // Hier zou iets met naam vanuit bankacno, of role kunnen
            
            // Diverse velden nog even updaten. Geen idee wat de meeste doen, waardes afgekeken bij andere 'rode regels'
            resultset.updateLong(Finance.FIELD_FINANCE_AMOUNT, 0);
            resultset.updateLong(Finance.FIELD_FINANCE_GAKEY,0);
            resultset.updateLong(Finance.FIELD_FINANCE_BATCHNO,0);
            resultset.updateLong(Finance.FIELD_FINANCE_COVKEY,0);
            resultset.updateLong(Finance.FIELD_FINANCE_RELPVKEY,0);
            resultset.updateFloat(Finance.FIELD_FINANCE_VATAMT,0);
            resultset.updateString(Finance.FIELD_FINANCE_DOCPRT, "N");
            resultset.updateString(Finance.FIELD_FINANCE_GACLOSECMP, "N");
            resultset.updateString(Finance.FIELD_FINANCE_GACOMPANY, "N");
            resultset.updateString(Finance.FIELD_FINANCE_GALINKFLAG, "");
            resultset.updateString(Finance.FIELD_FINANCE_RENEWMEMB, "N");
            resultset.updateString(Finance.FIELD_FINANCE_TIMEADDED, "0:00");
            resultset.updateString(Finance.FIELD_FINANCE_CAMPAIGN, "");
            resultset.updateString(Finance.FIELD_FINANCE_DEPT, "");
            resultset.updateString(Finance.FIELD_FINANCE_DOCREQ, "");
            resultset.updateString(Finance.FIELD_FINANCE_GASTATUS, "");
            resultset.updateString(Finance.FIELD_FINANCE_USERID, "webservice");
            resultset.updateString(Finance.FIELD_FINANCE_CAMPAIGN, "");
            
            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMFinanceException(MessageFormat.format("Finance SQL exception for financeid {0} amount {1}", new Object[]{finance.getFinanceid(), finance.getAmount()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMFinanceException(MessageFormat.format("Finance exception for financeid {0} amount {1}", new Object[]{finance.getFinanceid(), finance.getAmount()}), e);
        }
    }

    private Finance setBeanProperties(ResultSet resultset) {
        Finance finance = new Finance();
        try {
            finance.setFinanceid(resultset.getInt(Finance.FIELD_FINANCE_ID));
            long roleid = resultset.getLong(Finance.FIELD_FINANCE_ROLE_ID);
            finance.setRoleid(roleid);
            // Convert the 2 scale BigDecimal to the corresponding int value
            finance.setAmountdue(resultset.getFloat(Finance.FIELD_FINANCE_AMOUNTDUE));

            finance.setDatedue(resultset.getDate(Finance.FIELD_FINANCE_DATEDUE));
            finance.setDateadded(resultset.getDate(Finance.FIELD_FINANCE_DATEADDED));
            finance.setTransdate(resultset.getDate(Finance.FIELD_FINANCE_TRANSDATE));
            finance.setTimeadded(resultset.getString(Finance.FIELD_FINANCE_TIMEADDED));
            return finance;
        } catch (SQLException sqle) {
            Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, null, sqle);
            return null;
        } catch (Exception e) {
            Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

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
            Logger.getLogger(FinanceSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }

    
}
