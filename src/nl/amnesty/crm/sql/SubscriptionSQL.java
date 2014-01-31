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
import nl.amnesty.crm.entity.Product;
import nl.amnesty.crm.entity.Subscription;
import nl.amnesty.crm.exception.CRMSubscriptionException;

/**
 *
 * @author ed
 */
public class SubscriptionSQL {

    private static final String MSG_EXCEPTION = "Fatal error while updating subscription object for {0}";
    private static final String MSG_EXCEPTION_SQL = "Fatal SQL error while updating subscription object for {0}";
    // Dit stond op false, maar dan wordt een einddatum niet meegenomen. Probeer eens true.
    private static final boolean USETIMESTAMP = true;

    /*
     * 
     * Standard CRUD methods
     * 
     */
    /*
     * 
     * 
     *  PVKEY           MEMKEY  SUBSTYP PAYMETH JOINDATE                RENEWDATE               LEAVE   LEAVRSN SOURCE          NOOFMEM PAIDBY  PAIBYRN MBRINF  MEMTYPE         COKEY   DDKEY   CCKEY   SITECO  RCODE   PROMO   SERIAL  MEMPACK INCENTI JCRD    RCRD    MBNO    CRD                                                                                                                                                                                                                                                                                                                                                             DATEADDED                   DATECHG                     GAKEY   PAYAM   CFA     TRANSTYPE1                                                                                                                                                                              SCHEDULE                                                                                                                DAYPAID                                                         
     *  15051087	1171082	LID	INCASSO	2007-02-26 00:00:00.000	2011-03-01 00:00:00.000	NULL	NULL	753PU154	1	NULL	NULL	NULL	LIDDON          NULL	532999	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	1171082	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	2007-02-26 00:00:00.000     2009-04-01 00:00:00.000	NULL    120,00	NULL	LID         NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	Y	10,00	10,00	10,00	10,00	10,00	10,00	10,00	10,00	10,00	10,00	10,00	10,00	12	0	1	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	2007-03-01 00:00:00.000	2199-12-31 00:00:00.000	NULL	NULL	NULL	NULL
     *  15051087	1171083	KW00	NULL	2007-02-26 00:00:00.000	NULL                    NULL	NULL	753PU154	1	NULL	NULL	NULL	ABONNEMENT	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	1171083	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL    2007-02-26 00:00:00.000     2007-02-26 00:00:00.000	NULL	NULL	NULL	OVERIG      NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	N	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	1	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL	NULL                    NULL                    NULL	NULL	NULL	NULL	NULL     
     */
    public Subscription create(Connection connection, Subscription subscription) throws CRMSubscriptionException {
        Subscription subscriptionmatched;
        Statement statement = null;
        ResultSet resultset = null;
        long subscriptionid = 0;
        try {
            if (subscription.getRoleid() == 0) {
                return null;
            }
            if (subscription.getProductid() == 0) {
                return null;
            }
            // try to match subscription
            subscriptionmatched = match(connection, subscription);
            if (subscriptionmatched != null) {
                if (subscriptionmatched.isAcceptableMatch()) {
                    return subscriptionmatched;
                }
            }

            // At this point no matching subscription is found in CRM, so let's create a new one
            String SQL = "SELECT * FROM members";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();
            resultset = setResultsetColumns(connection, resultset, subscription);
            if (resultset != null) {
                if (subscription.getSubscriptionid() == 0) {
                    KeyGenerator keygenerator = new KeyGenerator(connection);
                    subscriptionid = keygenerator.getNextKey(KeyGenerator.KEY_NEXTMEMNO);
                    // Set the subscription id in the subscription object
                    subscription.setSubscriptionid(subscriptionid);
                }
                resultset.updateInt(Subscription.FIELD_MEMBERS_ID, (int) subscriptionid);
                resultset.updateInt(Subscription.FIELD_MEMBERS_ID_SAME, (int) subscriptionid);

                resultset.insertRow();
                subscription.setStatus(Subscription.STATUS_NEW);
                resultset.close();
            }
            statement.close();
            return subscription;
        } catch (SQLException sqle) {
            Logger.getLogger(SubscriptionSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMSubscriptionException(MessageFormat.format("Subscription SQL exception for subscriptionid {0} productid {1}", new Object[]{subscription.getSubscriptionid(), subscription.getProductid()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(SubscriptionSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMSubscriptionException(MessageFormat.format("Subscription exception for subscriptionid {0} productid {1}", new Object[]{subscription.getSubscriptionid(), subscription.getProductid()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public Subscription read(Connection connection, long subscriptionid) throws CRMSubscriptionException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        Subscription subscription = new Subscription();
        try {
            query = "SELECT * FROM grpmbrs g WHERE g.pvkey=" + subscriptionid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                subscription.setSubscriptionid((int) subscriptionid);
                return subscription;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(SubscriptionSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMSubscriptionException(MessageFormat.format("Subscription SQL exception for subscriptionid {0}", new Object[]{subscriptionid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(SubscriptionSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMSubscriptionException(MessageFormat.format("Subscription exception for subscriptionid {0}", new Object[]{subscriptionid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public boolean update(Connection connection, Subscription subscription) {
        // TODO: Implementation for updating subscription, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long subscriptionid) {
        // TODO: Implementation for deleting subscription, this is just a stub
        return false;
    }

    /*
     * Object matching
     */
    public Subscription match(Connection connection, Subscription subscription) throws CRMSubscriptionException {
        Subscription subscriptionfound;
        try {
            // First of all let's see if subscription can be found in CRM via the subscription id
            if (subscription.getSubscriptionid() != 0) {
                subscriptionfound = read(connection, subscription.getSubscriptionid());
                if (subscriptionfound != null) {
                    subscriptionfound.setStatus(Subscription.STATUS_MATCHED_ID);
                    return subscriptionfound;
                }
            }
            // Try to find subscription via roleid and product
            if (subscription.getRoleid() != 0) {
                if (subscription.getRoleid() != 0 && subscription.getProductid() != 0) {
                    ProductSQL productsql = new ProductSQL();
                    Product product = productsql.read(connection, subscription.getProductid());
                    if (product != null) {
                        if (product.getName() != null) {
                            if (!product.getName().isEmpty()) {
                                String productname = product.getName();
                                subscriptionfound = readViaRoleidProductname(connection, subscription.getRoleid(), productname);
                                if (subscriptionfound != null) {
                                    subscriptionfound.setStatus(Subscription.STATUS_MATCHED_ROLE_TYPE);
                                    return subscriptionfound;
                                }
                            }
                        }
                    }
                }
            }
            subscription.setStatus(Subscription.STATUS_MATCHED_NONE);
            return subscription;
        } catch (Exception e) {
            Logger.getLogger(SubscriptionSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMSubscriptionException(MessageFormat.format("Subscription exception for subscriptionid {0} productid {1}", new Object[]{subscription.getSubscriptionid(), subscription.getProductid()}), e);
        }
    }

    /*
     * READ methods
     */
    private static Subscription readViaRoleidProductname(Connection connection, long roleid, String productname) throws CRMSubscriptionException {
        Subscription subscription = null;
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            if (roleid == 0) {
                return null;
            }
            if (productname == null) {
                return null;
            }
            if (productname.isEmpty()) {
                return null;
            }
            String queryroleid = Subscription.FIELD_MEMBERS_ROLEID.concat("=").concat(String.valueOf(roleid)).replace(";","").replace("%","").replace("&", "");
            String querytype = Subscription.FIELD_MEMBERS_TYPE.concat("='").concat(productname).concat("'").replace(";","").replace("%","").replace("&", "");

            query = "SELECT * FROM members WHERE ".concat(queryroleid).concat(" AND ").concat(querytype).concat(" AND (leavedate IS NULL OR leavedate>getdate())");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                subscription = setBeanProperties(connection, resultset);
            }
            return subscription;
        } catch (SQLException sqle) {
            Logger.getLogger(SubscriptionSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMSubscriptionException(MessageFormat.format("Subscription SQL exception for roleid {0} productname {1}", new Object[]{roleid, productname}), sqle);
        } catch (Exception e) {
            Logger.getLogger(SubscriptionSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMSubscriptionException(MessageFormat.format("Subscription exception for roleid {0} productname {1}", new Object[]{roleid, productname}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public List<Subscription> readViaRoleid(Connection connection, long roleid) throws CRMSubscriptionException {
        List<Subscription> subscriptionlist = new ArrayList();
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            if (roleid == 0) {
                return null;
            }
            String queryroleid = Subscription.FIELD_MEMBERS_ROLEID.concat("=").concat(String.valueOf(roleid)).replace(";","").replace("%","").replace("&", "");

            query = "SELECT * FROM members WHERE ".concat(queryroleid).concat(" AND (leavedate IS NULL OR leavedate>getdate())");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    Subscription subscription = setBeanProperties(connection, resultset);
                    subscriptionlist.add(subscription);
                } while (resultset.next());
            }
            return subscriptionlist;
        } catch (SQLException sqle) {
            Logger.getLogger(SubscriptionSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMSubscriptionException(MessageFormat.format("Subscription SQL exception for roleid {0}", new Object[]{roleid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(SubscriptionSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMSubscriptionException(MessageFormat.format("Subscription exception for roleid {0}", new Object[]{roleid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     * 
     * Misc methods
     * 
     */
    private static ResultSet setResultsetColumns(Connection connection, ResultSet resultset, Subscription subscription) throws CRMSubscriptionException {
        try {
            resultset.updateLong(Subscription.FIELD_MEMBERS_ROLEID, subscription.getRoleid());
            //resultset.updateLong(Subscription.FIELD_MEMBERS_FOREIGNKEY_BANKACS, bankaccount.getBankaccountid());

            Calendar calendar = Calendar.getInstance();
            TimeZone timezone = TimeZone.getDefault();
            calendar.setTimeZone(timezone);

            if (USETIMESTAMP) {
                Timestamp today = new Timestamp(calendar.getTimeInMillis());
                resultset.updateTimestamp(Subscription.FIELD_MEMBERS_DATE_ADDDATE, today);
                resultset.updateTimestamp(Subscription.FIELD_MEMBERS_DATE_CHANGEDATE, today);
            } else {
                java.sql.Date today = new java.sql.Date(calendar.getTime().getTime());
                resultset.updateDate(Subscription.FIELD_MEMBERS_DATE_ADDDATE, today);
                resultset.updateDate(Subscription.FIELD_MEMBERS_DATE_CHANGEDATE, today);
            }

            if (subscription.getStartdate() == null) {
                if (USETIMESTAMP) {
                    Timestamp today = new Timestamp(calendar.getTimeInMillis());
                    resultset.updateTimestamp(Subscription.FIELD_MEMBERS_DATE_STARTDATE, today);
                } else {
                    java.sql.Date today = new java.sql.Date(calendar.getTime().getTime());
                    resultset.updateDate(Subscription.FIELD_MEMBERS_DATE_STARTDATE, today);
                }
            } else {
                Calendar calendarstart = Calendar.getInstance();
                calendarstart.setTimeZone(timezone);
                calendarstart.setTime(subscription.getStartdate());
                if (USETIMESTAMP) {
                    Timestamp start = new Timestamp(calendarstart.getTimeInMillis());
                    resultset.updateTimestamp(Subscription.FIELD_MEMBERS_DATE_STARTDATE, start);
                } else {
                    java.sql.Date start = new java.sql.Date(calendar.getTime().getTime());
                    resultset.updateDate(Subscription.FIELD_MEMBERS_DATE_STARTDATE, start);
                }
            }
            if (subscription.getEnddate() != null) {
                Calendar calendarend = Calendar.getInstance();
                calendarend.setTimeZone(timezone);
                calendarend.setTime(subscription.getEnddate());
                if (USETIMESTAMP) {
                    Timestamp end = new Timestamp(calendarend.getTimeInMillis());
                    resultset.updateTimestamp(Subscription.FIELD_MEMBERS_DATE_ENDDATE, end);
                } else {
                    java.sql.Date end = new java.sql.Date(calendar.getTime().getTime());
                    resultset.updateDate(Subscription.FIELD_MEMBERS_DATE_ENDDATE, end);
                }
            }
            resultset.updateString(Subscription.FIELD_MEMBERS_MEMBERTYPE, Subscription.SUBSCRIPTION);
            ProductSQL productsql = new ProductSQL();
            Product product = productsql.read(connection, subscription.getProductid());
            if (product != null) {
                String name = product.getName();
                resultset.updateString(Subscription.FIELD_MEMBERS_TYPE, name);
            }
            resultset.updateString(Subscription.FIELD_MEMBERS_SOURCE, subscription.getSource());

            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, null, sqle);
            return null;
        } catch (Exception e) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    private static Subscription setBeanProperties(Connection connection, ResultSet resultset) throws CRMSubscriptionException {
        Subscription subscription = new Subscription();
        try {
            subscription.setSubscriptionid(resultset.getInt(Subscription.FIELD_MEMBERS_ID));

            long roleid = resultset.getLong(Subscription.FIELD_MEMBERS_ROLEID);
            subscription.setRoleid(roleid);
            subscription.setSource(Subscription.FIELD_MEMBERS_SOURCE);
            String productname = resultset.getString(Subscription.FIELD_MEMBERS_TYPE).replace(";","").replace("%","").replace("&", "");;
            //String productdescription = resultset.getString(Subscription.FIELD_MEMBERS_MEMBERTYPE);
            ProductSQL productsql = new ProductSQL();
            Product product = productsql.readViaName(connection, productname);
            if (product != null) {
                subscription.setProductid(product.getProductid());
                subscription.setStartdate(resultset.getDate(Subscription.FIELD_MEMBERS_DATE_STARTDATE));
                subscription.setEnddate(resultset.getDate(Subscription.FIELD_MEMBERS_DATE_ENDDATE));

                return subscription;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, null, sqle);
            return null;
        } catch (Exception e) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, null, e);
            return null;
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
            Logger.getLogger(SubscriptionSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }
}
