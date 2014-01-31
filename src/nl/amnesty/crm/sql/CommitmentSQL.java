/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import nl.amnesty.crm.entity.Commitment;
import nl.amnesty.crm.exception.CRMCommitmentException;

/**
 *
 * @author evelzen
 */
public class CommitmentSQL {

    private static final boolean USETIMESTAMP = false;

    /*
     * ---------------------------------------- Standard CRUD methods
     * ----------------------------------------
     */
    public Commitment create(Connection connection, Commitment commitment) throws CRMCommitmentException {
        Commitment commitmentmatched;
        Statement statement = null;
        ResultSet resultset = null;
        long commitmentid = 0;
        long commitmentidold = 0;
        try {
            if (commitment == null) {
                Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, "Commitment is null");
                return null;
            }
            if (commitment.getRoleid() == 0) {
                Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, "Roleid for commitment {0} {1} is 0", new Object[]{commitment.getFormattedAmount(), commitment.getFormattedFrequency()});
                return null;
            }
            // try to match commitment
            commitmentmatched = match(connection, commitment);
            if (commitmentmatched != null) {
                if (commitmentmatched.isAcceptableMatch()) {
                    return commitmentmatched;
                } else {
                    if (commitmentmatched.getStatus() == Commitment.STATUS_MATCHED_ROLE_MEMBERTYPE_PAYMENTMETHOD) {
                        // We do not have an acceptable match, but we did find an old record that can be deleted once we have got a new one in place.
                        commitmentidold = commitmentmatched.getCommitmentid();
                    }
                }
            }

            // At this point no matching commitment is found in CRM, so let's create a new one
            String SQL = "SELECT * FROM members";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();
            // All of the interesting stuff takes place in setResultsetColumns...
            resultset = setResultsetColumns(resultset, commitment);
            if (resultset != null) {
                if (commitment.getCommitmentid() == 0) {
                    KeyGenerator keygenerator = new KeyGenerator(connection);
                    commitmentid = keygenerator.getNextKey(KeyGenerator.KEY_NEXTMEMNO);
                    // Set the commitment id in the commitment object
                    commitment.setCommitmentid(commitmentid);
                }
                resultset.updateInt(Commitment.FIELD_MEMBERS_ID, (int) commitmentid);
                resultset.updateInt(Commitment.FIELD_MEMBERS_ID_SAME, (int) commitmentid);

                resultset.insertRow();
                commitment.setStatus(Commitment.STATUS_NEW);
                resultset.close();

                if (commitmentidold != 0) {
                    // We found an old record that was replaced by a new commitment, let's deleted the old one...
                    delete(connection, commitmentidold);
                }
            }
            statement.close();
            return commitment;
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for commitmentid {0} amount {1} frequency {2}", new Object[]{commitment.getCommitmentid(), commitment.getAmount(), commitment.getFrequency()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment exception for commitmentid {0} amount {1} frequency {2}", new Object[]{commitment.getCommitmentid(), commitment.getAmount(), commitment.getFrequency()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public Commitment read(Connection connection, long commitmentid) throws CRMCommitmentException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        Commitment commitment = new Commitment();
        try {
            query = "SELECT * FROM members WHERE " + Commitment.FIELD_MEMBERS_ID + "=" + commitmentid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                commitment = setBeanProperties(resultset);
                return commitment;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for commitmentid {0} amount {1} frequency {2}", new Object[]{commitment.getCommitmentid(), commitment.getAmount(), commitment.getFrequency()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment exception for commitmentid {0} amount {1} frequency {2}", new Object[]{commitment.getCommitmentid(), commitment.getAmount(), commitment.getFrequency()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public boolean update(Connection connection, Commitment commitment) throws CRMCommitmentException {
        // Code goes here...
        return false;
    }

    public boolean updateLeavedateLeaversn(Connection connection, long commitmentid) throws CRMCommitmentException {
        Calendar now = Calendar.getInstance();
        PreparedStatement preparedstatement = null;
        try {
            preparedstatement = connection.prepareStatement("UPDATE members SET leavedate = ?, leaversn = ? where pvkey = ?");
            java.sql.Date leavedate = new java.sql.Date(now.getTime().getTime());
            preparedstatement.setDate(1, leavedate);
            // Leave reason 15 means 'For administrative purposes'
            preparedstatement.setInt(2, 15);
            preparedstatement.setLong(3, commitmentid);
            preparedstatement.executeUpdate();
            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for commitmentid {0}", new Object[]{commitmentid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment exception for commitmentid {0}", new Object[]{commitmentid}), e);
        } finally {
            closeSQL(preparedstatement, null);
        }
    }

    /**
     * We will not really delete entries but mark them as ended instead
     *
     * @param connection
     * @param commitmentid
     * @return
     * @throws CRMCommitmentException
     */
    public boolean delete(Connection connection, long commitmentid) throws CRMCommitmentException {
        return updateLeavedateLeaversn(connection, commitmentid);
    }

    /* Gebruik delete in plaats van deze functie.
     */
    @Deprecated
    public boolean deleteDisabled(Connection connection, long commitmentid) throws CRMCommitmentException {
        String sql = "";
        Statement statement = null;
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "DELETE members where " + Commitment.FIELD_MEMBERS_ID + "=" + String.valueOf(commitmentid);
            int rowcount = statement.executeUpdate(sql);
            statement.close();
            if (rowcount == 0) {
                Logger.getLogger(CommitmentSQL.class.getName()).log(Level.WARNING, "Commitment removal failed for commitmentid {0} (commitment not found)", commitmentid);
                //throw new CRMCommitmentException(MessageFormat.format("Commitment removal failed for commitmentid {0} (commitment not found)", new Object[]{commitmentid}));
                return false;
            } else {
                if (rowcount != 1) {
                    Logger.getLogger(CommitmentSQL.class.getName()).log(Level.WARNING, "Commitment removal resulted in multiple deletions for commitmentid {0} ({1} rows got deleted)", new Object[]{commitmentid, rowcount});
                    //throw new CRMCommitmentException(MessageFormat.format("Commitment removal resulted in multiple deletions for commitmentid {0} ({1} rows got deleted)", new Object[]{commitmentid, rowcount}));
                    return false;
                } else {
                    return true;
                }
            }
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for commitmentid {0}", new Object[]{commitmentid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment exception for commitmentid {0}", new Object[]{commitmentid}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    /*
     * ---------------------------------------- Object matching
     * ----------------------------------------
     */
    public Commitment match(Connection connection, Commitment commitment) throws CRMCommitmentException {
        Commitment commitmentfound;
        try {
            // First of all let's see if commitment can be found in CRM via the commitment id
            if (commitment.getCommitmentid() != 0) {
                commitmentfound = read(connection, commitment.getCommitmentid());
                if (commitmentfound != null) {
                    commitmentfound.setStatus(Commitment.STATUS_MATCHED_ID);
                    return commitmentfound;
                }
            }
            // Try to find commitment via roleid, amount and payment frequency
            if (commitment.getRoleid() != 0) {
                if (commitment.getRoleid() != 0 && commitment.getAmount() != 0 && commitment.getFrequency() != 0) {
                    int total = commitment.getAmount() * commitment.getFrequency();
                    commitmentfound = readViaRoleidAmountFrequency(connection, commitment.getRoleid(), total, commitment.getFrequency());
                    if (commitmentfound != null) {
                        commitmentfound.setStatus(Commitment.STATUS_MATCHED_ROLE_AMOUNT_FREQUENCY);
                        return commitmentfound;
                    }
                }
            }
            // Try to find commitment via roleid
            if (commitment.getRoleid() != 0) {
                if (commitment.getRoleid() != 0) {
                    List<Commitment> commitmentlist = readViaRoleid(connection, commitment.getRoleid());
                    if (commitmentlist != null) {
                        if (!commitmentlist.isEmpty()) {
                            commitmentfound = commitmentlist.get(0);
                            commitmentfound.setStatus(Commitment.STATUS_MATCHED_ROLE_MEMBERTYPE_PAYMENTMETHOD);
                            return commitmentfound;
                        }
                    }
                }
            }
            commitment.setStatus(Commitment.STATUS_MATCHED_NONE);
            return commitment;
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment exception for commitmentid {0} amount {1} frequency {2}", new Object[]{commitment.getCommitmentid(), commitment.getAmount(), commitment.getFrequency()}), e);
        }
    }

    /*
     * ---------------------------------------- CREATE methods
     * ----------------------------------------
     */
    private ResultSet doYear(ResultSet resultset, BigDecimal amount, int month) throws CRMCommitmentException {
        try {
            resultset = doClear(resultset);
            if (resultset != null) {
                switch (month) {
                    case Calendar.JANUARY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JAN, amount);
                        break;
                    case Calendar.FEBRUARY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_FEB, amount);
                        break;
                    case Calendar.MARCH:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAR, amount);
                        break;
                    case Calendar.APRIL:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_APR, amount);
                        break;
                    case Calendar.MAY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAY, amount);
                        break;
                    case Calendar.JUNE:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUN, amount);
                        break;
                    case Calendar.JULY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUL, amount);
                        break;
                    case Calendar.AUGUST:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_AUG, amount);
                        break;
                    case Calendar.SEPTEMBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_SEP, amount);
                        break;
                    case Calendar.OCTOBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_OCT, amount);
                        break;
                    case Calendar.NOVEMBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_NOV, amount);
                        break;
                    case Calendar.DECEMBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_DEC, amount);
                        break;
                }
            }
            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for amount {0} month {1}", new Object[]{amount, month}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for amount {0} month {1}", new Object[]{amount, month}), e);
        }
    }

    private ResultSet doSemester(ResultSet resultset, BigDecimal amount, int month) throws CRMCommitmentException {
        try {
            resultset = doClear(resultset);
            if (resultset != null) {
                switch (month) {
                    case Calendar.JANUARY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JAN, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUL, amount);
                        break;
                    case Calendar.FEBRUARY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_FEB, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_AUG, amount);
                        break;
                    case Calendar.MARCH:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAR, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_SEP, amount);
                        break;
                    case Calendar.APRIL:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_APR, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_OCT, amount);
                        break;
                    case Calendar.MAY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAY, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_NOV, amount);
                        break;
                    case Calendar.JUNE:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUN, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_DEC, amount);
                        break;
                    case Calendar.JULY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUL, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JAN, amount);
                        break;
                    case Calendar.AUGUST:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_AUG, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_FEB, amount);
                        break;
                    case Calendar.SEPTEMBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_SEP, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAR, amount);
                        break;
                    case Calendar.OCTOBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_OCT, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_APR, amount);
                        break;
                    case Calendar.NOVEMBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_NOV, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAY, amount);
                        break;
                    case Calendar.DECEMBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_DEC, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUN, amount);
                        break;
                }
            }
            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for amount {0} month {1}", new Object[]{amount, month}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for amount {0} month {1}", new Object[]{amount, month}), e);
        }
    }

    private ResultSet doQuarter(ResultSet resultset, BigDecimal amount, int month) throws CRMCommitmentException {
        try {
            resultset = doClear(resultset);
            if (resultset != null) {
                switch (month) {
                    case Calendar.JANUARY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JAN, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_APR, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUL, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_OCT, amount);
                        break;
                    case Calendar.FEBRUARY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_FEB, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAY, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_AUG, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_NOV, amount);
                        break;
                    case Calendar.MARCH:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAR, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUN, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_SEP, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_DEC, amount);
                        break;
                    case Calendar.APRIL:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_APR, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUL, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_OCT, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JAN, amount);
                        break;
                    case Calendar.MAY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAY, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_AUG, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_NOV, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_FEB, amount);
                        break;
                    case Calendar.JUNE:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUN, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_SEP, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_DEC, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAR, amount);
                        break;
                    case Calendar.JULY:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUL, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_OCT, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JAN, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_APR, amount);
                        break;
                    case Calendar.AUGUST:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_AUG, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_NOV, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_FEB, amount);
                        //resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUN, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAY, amount);
                        break;
                    case Calendar.SEPTEMBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_SEP, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_DEC, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAR, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUN, amount);
                        break;
                    case Calendar.OCTOBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_OCT, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JAN, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_APR, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUL, amount);
                        break;
                    case Calendar.NOVEMBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_NOV, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_FEB, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAY, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_AUG, amount);
                        break;
                    case Calendar.DECEMBER:
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_DEC, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAR, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUN, amount);
                        resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_SEP, amount);
                        break;
                }
            }
            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for amount {0} month {1}", new Object[]{amount, month}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for amount {0} month {1}", new Object[]{amount, month}), e);
        }
    }

    private ResultSet doMonth(ResultSet resultset, BigDecimal amount) throws CRMCommitmentException {
        try {
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JAN, amount);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_FEB, amount);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAR, amount);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_APR, amount);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAY, amount);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUN, amount);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUL, amount);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_AUG, amount);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_SEP, amount);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_OCT, amount);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_NOV, amount);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_DEC, amount);
            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for amount {0}", new Object[]{amount}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for amount {0}", new Object[]{amount}), e);
        }
    }

    private ResultSet doClear(ResultSet resultset) throws CRMCommitmentException {
        try {
            // Set all months to zero
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JAN, BigDecimal.ZERO);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_FEB, BigDecimal.ZERO);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAR, BigDecimal.ZERO);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_APR, BigDecimal.ZERO);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_MAY, BigDecimal.ZERO);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUN, BigDecimal.ZERO);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_JUL, BigDecimal.ZERO);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_AUG, BigDecimal.ZERO);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_SEP, BigDecimal.ZERO);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_OCT, BigDecimal.ZERO);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_NOV, BigDecimal.ZERO);
            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_MONTH_DEC, BigDecimal.ZERO);

            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException("Commitment SQL exception", sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException("Commitment exception", e);
        }
    }
    /*
     * ---------------------------------------- READ methods
     * ----------------------------------------
     */

    public List<Commitment> readViaRoleid(Connection connection, long roleid) throws CRMCommitmentException {
        return readViaRoleidMembertypePaymentmethod(connection, roleid, "LIDDON", "INCASSO");
    }

    private List<Commitment> readViaRoleidMembertypePaymentmethod(Connection connection, long roleid, String membertype, String paymentmethod) throws CRMCommitmentException {
        List<Commitment> commitmentlist = new ArrayList();
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        try {

            //DEBUG
            //Logger.getLogger(CommitmentSQL.class.getName()).log(Level.INFO, "Retrieving commitments for roleid {0}", roleid);

            String queryroleid = Commitment.FIELD_MEMBERS_ROLEID.concat("=").concat(String.valueOf(roleid));
            String querymembertype = Commitment.FIELD_MEMBERS_MEMBERTYPE.concat("='").concat(membertype).concat("'");
            String querypaymentmethod = Commitment.FIELD_MEMBERS_PAYMENTMETHOD.concat("='").concat(paymentmethod).concat("'");

            query = "SELECT * FROM members WHERE ".concat(queryroleid).concat(" AND ").concat(querymembertype).concat(" AND ").concat(querypaymentmethod).concat(" AND (leavedate is null OR leavedate > getdate())");

            //DEBUG
            //Logger.getLogger(CommitmentSQL.class.getName()).log(Level.INFO, "query {0}", query);

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    Commitment commitment = setBeanProperties(resultset);
                    commitmentlist.add(commitment);
                } while (resultset.next());
            }
            return commitmentlist;
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for roleid {0} membertype {1} paymentmethod {2}", new Object[]{roleid, membertype, paymentmethod}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment exception for roleid {0} membertype {1} paymentmethod {2}", new Object[]{roleid, membertype, paymentmethod}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private Commitment readViaRoleidAmountFrequency(Connection connection, long roleid, int amount, int frequency) throws CRMCommitmentException {
        Commitment commitment = null;
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            String queryroleid = Commitment.FIELD_MEMBERS_ROLEID.concat("=").concat(String.valueOf(roleid));
            String queryamount = Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_TOTAL.concat("=").concat(String.valueOf(amount));
            String queryfrequency = Commitment.FIELD_MEMBERS_PAYMENT_FREQUENCY.concat("=").concat(String.valueOf(frequency));

            query = "SELECT * FROM members WHERE ".concat(queryroleid).concat(" AND ").concat(queryamount).concat(" AND ").concat(queryfrequency);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                commitment = setBeanProperties(resultset);
            }
            return commitment;
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for commitmentid {0} amount {1} frequency {2}", new Object[]{commitment.getCommitmentid(), commitment.getAmount(), commitment.getFrequency()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment exception for commitmentid {0} amount {1} frequency {2}", new Object[]{commitment.getCommitmentid(), commitment.getAmount(), commitment.getFrequency()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     * ---------------------------------------- Misc methods
     * ----------------------------------------
     */
    /**
     *
     * @param resultset
     * @param role
     * @param bankaccount
     * @param commitment
     * @return
     */
    private ResultSet setResultsetColumns(ResultSet resultset, Commitment commitment) throws CRMCommitmentException {
        try {
            resultset.updateLong(Commitment.FIELD_MEMBERS_ROLEID, commitment.getRoleid());
            resultset.updateLong(Commitment.FIELD_MEMBERS_FOREIGNKEY_BANKACS, commitment.getBankaccountid());

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
            // Set to the first day of this or the following month, depending if remainder of this month is less than 7 days.
            calendar_firstofmonth.set(Calendar.DATE, 1);
            calendar_farfuture.set(2099, Calendar.DECEMBER, 31);

            if (USETIMESTAMP) {
                Timestamp today = new Timestamp(calendar_today.getTimeInMillis());
                Timestamp yearfromnow = new Timestamp(calendar_yearfromnow.getTimeInMillis());
                Timestamp firstofmonth = new Timestamp(calendar_firstofmonth.getTimeInMillis());
                Timestamp farfuture = new Timestamp(calendar_farfuture.getTimeInMillis());
                resultset.updateTimestamp(Commitment.FIELD_MEMBERS_DATE_ADDDATE, today);
                resultset.updateTimestamp(Commitment.FIELD_MEMBERS_DATE_CHANGEDATE, today);
                if (commitment.getStatus() == Commitment.STATUS_MATCHED_NONE) {
                    resultset.updateTimestamp(Commitment.FIELD_MEMBERS_DATE_STARTDATE, today);
                }
                // Set the startdate for payments to the first day of the current month unless the remainder of this month is less than 7 days.
                resultset.updateTimestamp(Commitment.FIELD_MEMBERS_PAYMENT_STARTDATE, firstofmonth);
                resultset.updateTimestamp(Commitment.FIELD_MEMBERS_DATE_RENEWDATE, yearfromnow);
                resultset.updateTimestamp(Commitment.FIELD_MEMBERS_PAYMENT_ENDDATE, farfuture);
            } else {
                java.sql.Date today = new java.sql.Date(calendar_today.getTime().getTime());
                java.sql.Date yearfromnow = new java.sql.Date(calendar_yearfromnow.getTime().getTime());
                java.sql.Date firstofmonth = new java.sql.Date(calendar_firstofmonth.getTime().getTime());
                java.sql.Date farfuture = new java.sql.Date(calendar_farfuture.getTime().getTime());
                resultset.updateDate(Commitment.FIELD_MEMBERS_DATE_ADDDATE, today);
                resultset.updateDate(Commitment.FIELD_MEMBERS_DATE_CHANGEDATE, today);
                resultset.updateDate(Commitment.FIELD_MEMBERS_DATE_STARTDATE, today);
                // Set the startdate for payments to the first day of the current month unless the remainder of this month is less than 7 days.
                resultset.updateDate(Commitment.FIELD_MEMBERS_PAYMENT_STARTDATE, firstofmonth);
                resultset.updateDate(Commitment.FIELD_MEMBERS_DATE_RENEWDATE, yearfromnow);
                resultset.updateDate(Commitment.FIELD_MEMBERS_PAYMENT_ENDDATE, farfuture);
            }

            resultset.updateString(Commitment.FIELD_MEMBERS_MEMBERTYPE, "LIDDON");
            resultset.updateString(Commitment.FIELD_MEMBERS_TYPE, "LID");

            resultset.updateString(Commitment.FIELD_MEMBERS_TRANSACTIONTYPE, "LID");
            resultset.updateString(Commitment.FIELD_MEMBERS_PAYMENTMETHOD, "INCASSO");
            resultset.updateString(Commitment.FIELD_MEMBERS_SOURCE, commitment.getSource());

            resultset.updateString(Commitment.FIELD_MEMBERS_PAYMENT_SCHEDULE, "Y");
            resultset.updateInt(Commitment.FIELD_MEMBERS_PAYMENT_FREQUENCY, commitment.getFrequency());

            resultset.updateInt(Commitment.FIELD_MEMBERS_PAYMENT_PAID_MONTH, 0);
            resultset.updateInt(Commitment.FIELD_MEMBERS_PAYMENT_PAID_DAY, 0);

            BigDecimal bigdecimal = BigDecimal.valueOf((commitment.getAmount() * commitment.getFrequency()), 2);

            // DEBUG
            //Logger.getLogger(CommitmentImplementationSQL.class.getName()).log(Level.INFO, "Commitment: commitment.getAmount(): " + commitment.getAmount());
            //Logger.getLogger(CommitmentImplementationSQL.class.getName()).log(Level.INFO, "Commitment: commitment.getFrequency: " + commitment.getAmount());
            //Logger.getLogger(CommitmentImplementationSQL.class.getName()).log(Level.INFO, "Commitment: bigdecimal: " + bigdecimal);

            resultset.updateBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_TOTAL, bigdecimal);

            bigdecimal = BigDecimal.valueOf(commitment.getAmount(), 2);
            switch (commitment.getFrequency()) {
                case Commitment.FREQUENCY_YEAR:
                    resultset = doYear(resultset, bigdecimal, month);
                    break;
                case Commitment.FREQUENCY_SEMESTER:
                    resultset = doSemester(resultset, bigdecimal, month);
                    break;
                case Commitment.FREQUENCY_QUARTER:
                    resultset = doQuarter(resultset, bigdecimal, month);
                    break;
                case Commitment.FREQUENCY_MONTH:
                    resultset = doMonth(resultset, bigdecimal);
                    break;
            }
            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMCommitmentException(MessageFormat.format("Commitment SQL exception for commitmentid {0} amount {1} frequency {2}", new Object[]{commitment.getCommitmentid(), commitment.getAmount(), commitment.getFrequency()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMCommitmentException(MessageFormat.format("Commitment exception for commitmentid {0} amount {1} frequency {2}", new Object[]{commitment.getCommitmentid(), commitment.getAmount(), commitment.getFrequency()}), e);
        }
    }

    private Commitment setBeanProperties(ResultSet resultset) {
        Commitment commitment = new Commitment();
        int amount = 0;
        int frequency;
        try {
            commitment.setCommitmentid(resultset.getInt(Commitment.FIELD_MEMBERS_ID));

            long roleid = resultset.getLong(Commitment.FIELD_MEMBERS_ROLEID);
            commitment.setRoleid(roleid);

            BigDecimal totalamount = resultset.getBigDecimal(Commitment.FIELD_MEMBERS_PAYMENT_AMOUNT_TOTAL);
            frequency = resultset.getInt(Commitment.FIELD_MEMBERS_PAYMENT_FREQUENCY);
            // Convert the 2 scale BigDecimal to the corresponding int value
            totalamount = totalamount.multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
            if (totalamount != null) {
                amount = totalamount.intValue();
                if (frequency>0) {
                    amount = amount / frequency;
                }
            }
            commitment.setAmount(amount);
            commitment.setFrequency(frequency);

            commitment.setStartdate(resultset.getDate(Commitment.FIELD_MEMBERS_DATE_STARTDATE));
            commitment.setEnddate(resultset.getDate(Commitment.FIELD_MEMBERS_DATE_STARTDATE));

            return commitment;
        } catch (SQLException sqle) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
            return null;
        } catch (Exception e) {
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, e);
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
            Logger.getLogger(CommitmentSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }
}
