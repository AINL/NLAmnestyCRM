package nl.amnesty.crm.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.db.KeyGenerator;
import nl.amnesty.crm.entity.Address;
import nl.amnesty.crm.entity.Bankaccount;
import nl.amnesty.crm.entity.Commitment;
import nl.amnesty.crm.entity.Involvement;
import nl.amnesty.crm.entity.Person;
import nl.amnesty.crm.entity.Phone;
import nl.amnesty.crm.entity.Product;
import nl.amnesty.crm.entity.Role;
import nl.amnesty.crm.entity.Subscription;
import nl.amnesty.crm.entity.URL;
import nl.amnesty.crm.exception.CRMAddressException;
import nl.amnesty.crm.exception.CRMException;
import nl.amnesty.crm.exception.CRMPersonException;
import nl.amnesty.crm.exception.CRMPhoneException;
import nl.amnesty.crm.exception.CRMURLException;

/**
 *
 * @author ed
 */
public class RoleSQL {

    private final boolean USETIMESTAMP = false;

    /*
     * ---------------------------------------- Standard CRUD methods
     * ----------------------------------------
     */
    /**
     *
     * @param connection
     * @param role
     * @return
     */
    public Role create(Connection connection, Role role) {
        Role rolefound = null;
        Role rolecreated = null;
        if (role == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "create() Role is null");
            return null;
        }
        if (role.getPerson() == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "create() Role person is null");
            return null;
        }
        if (role.getAddress() == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "create() Role address is null");
            return null;
        }
        try {
            rolefound = match(connection, role);
            if (rolefound == null) {
                return null;
            }
            if (rolefound.isNoMatch()) {
                Person person = createPerson(connection, role);
                if (person == null) {
                    return null;
                }
                Address address = createAddress(connection, role);
                if (address == null) {
                    return null;
                }
                rolecreated = createViaPersonAddress(connection, person, address, role.getSource());
                if (rolecreated == null) {
                    return null;
                }
                return createPhoneURLBankaccount(connection, role, rolecreated);
            } else {
                // Reset the contact type to "PERSOON" as it may be an archived type record.
                if (!updateContacttype(connection, rolefound.getRoleid(), "PERSOON")) {
                    return null;
                }
                // Update contactmedia flags to default values
                // Bert: Nee, update naar default waardes kan ongewenst zijn omdat bv. al aangegevens is dat ze niet gebeld willen worden.
                // Ik zet deze regel dus uit.
                //if (!updateContactFlagDefaults(connection, rolefound.getRoleid())) {
                //    return null;
                //}
                return createPhoneURLBankaccount(connection, role, rolefound);
            }
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     *
     * @param connection
     * @param person
     * @param address
     * @param source
     * @return
     */
    private Role createViaPersonAddress(Connection connection, Person person, Address address, String source) {
        Role role = null;
        if (person == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createViaPersonAddress() Person is null");
            return null;
        }
        if (address == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createViaPersonAddress() Address is null");
            return null;
        }
        try {
            // Let Role point to the address and person objects that are either created or found.
            if (person.isNew()) {
                // New person
                if (address.isNew()) {
                    // New person and new address: we have got a new role
                    role = createRole(connection, source, person, address);
                    if (role == null) {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create role via personid {0} and addressid {1}", new Object[]{person.getPersonid(), address.getAddressid()});
                        return null;
                    }
                    role.setAddress(address);
                    role.setPerson(person);
                } else {
                    // New person at existing address: possibly a family member
                    role = createRole(connection, source, person, address);
                    if (role == null) {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create role via personid {0} and addressid {1}", new Object[]{person.getPersonid(), address.getAddressid()});
                        return null;
                    }
                    role.setAddress(address);
                    role.setPerson(person);
                }
            } else {
                // Existing person
                if (address.isNew()) {
                    // Existing person at new address: most likely a change of address
                    List<Role> rolelist = readViaPersonid(connection, person.getPersonid());
                    if (rolelist.isEmpty()) {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to find role via personid {0}", person.getPersonid());
                        return null;
                    }
                    for (Role roleviapersonid : rolelist) {
                        // Check if it is a decent address, with street name, housenumber etc.
                        if (address.isDecentAddress()) {
                            // Retain person of existing role and replace address with newly created address
                            if (!updateAddressid(connection, roleviapersonid.getRoleid(), address.getAddressid())) {
                                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to update addressid {0} for roleid {1}", new Object[]{address.getAddressid(), role.getRoleid()});
                                return null;
                            } else {
                                // Assume that one of the roles for the existing person and the new address is the role that we want
                                role = roleviapersonid;
                            }
                        }
                    }

                    if (role == null) {
                        // We may have found an existing person and a new address, but these do not match up, so create a new role.
                        role = createRole(connection, source, person, address);
                        if (role == null) {
                            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create role via personid {0} and addressid {1}", new Object[]{person.getPersonid(), address.getAddressid()});
                            return null;
                        }
                        role.setAddress(address);
                        role.setPerson(person);
                    }
                } else {
                    // Existing person at existing address: we already know this role
                    role = readViaPersonidAddressid(connection, person.getPersonid(), address.getAddressid());
                    if (role == null) {
                        // We may have found an existing person and a new address, but these do not match up, so create a new role.
                        role = createRole(connection, source, person, address);
                        if (role == null) {
                            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create role via personid {0} and addressid {1}", new Object[]{person.getPersonid(), address.getAddressid()});
                            return null;
                        }
                        role.setAddress(address);
                        role.setPerson(person);
                    }
                }
            }
            return role;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     *
     * @param connection
     * @param role
     * @return
     */
    private Person createPerson(Connection connection, Role role) {
        Person person = null;
        if (role == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createPerson() Role is null");
            return null;
        }
        try {
            if (role.getPerson() != null) {
                PersonSQL personsql = new PersonSQL();
                person = personsql.create(connection, role.getPerson());
                if (person == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create person for roleid {0}", role.getRoleid());
                    return null;
                }
            }
            return person;
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     *
     * @param connection
     * @param role
     * @return
     */
    private Address createAddress(Connection connection, Role role) {
        Address address = null;
        if (role == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createAddress() Role is null");
            return null;
        }
        try {
            if (role.getAddress() != null) {
                AddressSQL addresssql = new AddressSQL();
                address = addresssql.create(connection, role.getAddress());
                if (address == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create address for roleid {0}", role.getRoleid());
                    return null;
                }
            }
            return address;
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    private Role createPhoneURLBankaccount(Connection connection, Role role, Role rolecreated) {
        if (role == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createPhoneURLBankaccount() Role is null");
            return null;
        }
        if (rolecreated == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createPhoneURLBankaccount() New role is null");
            return null;
        }
        try {
            List<Phone> phonelist = new ArrayList();
            List<URL> urllist = new ArrayList();
            List<Bankaccount> bankaccountlist = new ArrayList();

            if (role.getUrllist() != null) {
                if (!role.getUrllist().isEmpty()) {
                    urllist = createURL(connection, rolecreated, role.getUrllist());
                    if (urllist == null) {
                        return null;
                    }
                }
            }
            if (role.getPhonelist() != null) {
                if (!role.getPhonelist().isEmpty()) {
                    phonelist = createPhone(connection, rolecreated, role.getPhonelist());
                    if (phonelist == null) {
                        return null;
                    }
                }
            }
            if (role.getBankaccountlist() != null) {
                if (!role.getBankaccountlist().isEmpty()) {
                    bankaccountlist = createBankaccount(connection, rolecreated, role.getBankaccountlist());
                    if (bankaccountlist == null) {
                        return null;
                    }
                }
            }
            rolecreated.setBankaccountlist(bankaccountlist);
            rolecreated.setPhonelist(phonelist);
            rolecreated.setUrllist(urllist);

            return rolecreated;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    private List<Phone> createPhone(Connection connection, Role rolecreated, List<Phone> phonelist) {
        if (phonelist == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createPhone() Phonelist is null");
            phonelist = new ArrayList();
            return phonelist;
        }
        if (phonelist.isEmpty()) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "createPhone() Phonelist is empty");
            return phonelist;
        }
        if (rolecreated == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createPhone() Role is null");
            return phonelist;
        }
        if (rolecreated.getRoleid() == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createPhone() Roleid is 0");
            return phonelist;
        }
        try {
            List<Phone> phonerolecreatedlist = rolecreated.getPhonelist();
            if (phonerolecreatedlist == null) {
                phonerolecreatedlist = new ArrayList();
            }
            if (phonerolecreatedlist.isEmpty()) {
                for (Phone phonetobeadded : phonelist) {

                    //DEBUG
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createPhone() add phone number {0} to empty list.", phonetobeadded.getFormattedNumber());

                    PhoneSQL phonesql = new PhoneSQL();
                    phonetobeadded.setRoleid(rolecreated.getRoleid());
                    phonetobeadded = phonesql.create(connection, phonetobeadded);
                    if (phonetobeadded == null) {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create phone for role");
                        return null;
                    } else {
                        phonerolecreatedlist.add(phonetobeadded);
                    }
                }
                return phonerolecreatedlist;
            } else {
                for (Phone phonetobeadded : phonelist) {

                    //DEBUG
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createPhone() add phone number {0} to existing list.", phonetobeadded.getFormattedNumber());

                    if (!phonerolecreatedlist.contains(phonetobeadded)) {

                        //DEBUG
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createPhone() existing list does not contain phone number {0}, will now be created and added.", phonetobeadded.getFormattedNumber());

                        PhoneSQL phonesql = new PhoneSQL();
                        phonetobeadded.setRoleid(rolecreated.getRoleid());
                        phonetobeadded = phonesql.create(connection, phonetobeadded);
                        if (phonetobeadded == null) {
                            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create phone for role");
                            return null;
                        } else {
                            phonerolecreatedlist.add(phonetobeadded);
                        }
                    } else {

                        //DEBUG
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createPhone() existing list already contains phone number {0}, does not need to be added.", phonetobeadded.getFormattedNumber());

                    }
                }
                return phonerolecreatedlist;
            }
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    private List<URL> createURL(Connection connection, Role rolecreated, List<URL> urllist) {
        if (urllist == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createURL() URLlist is null");
            urllist = new ArrayList();
            return urllist;
        }
        if (urllist.isEmpty()) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "createURL() URLlist is empty");
            return urllist;
        }
        if (rolecreated == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createURL() Role is null");
            return urllist;
        }
        if (rolecreated.getRoleid() == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createURL() Roleid is 0");
            return urllist;
        }
        try {
            List<URL> urlrolecreatedlist = rolecreated.getUrllist();
            if (urlrolecreatedlist == null) {
                urlrolecreatedlist = new ArrayList();
            }
            if (urlrolecreatedlist.isEmpty()) {
                for (URL urltobeadded : urllist) {

                    //DEBUG
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createURL() add URL {0} to empty list.", urltobeadded.getInternetAddress());

                    URLSQL urlsql = new URLSQL();
                    urltobeadded.setRoleid(rolecreated.getRoleid());
                    urltobeadded = urlsql.create(connection, urltobeadded);
                    if (urltobeadded == null) {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create url for role");
                        return null;
                    } else {
                        urlrolecreatedlist.add(urltobeadded);
                    }
                }
                return urlrolecreatedlist;
            } else {
                for (URL urltobeadded : urllist) {

                    //DEBUG
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createURL() add URL {0} to existing list.", urltobeadded.getInternetAddress());

                    if (!urlrolecreatedlist.contains(urltobeadded)) {

                        //DEBUG
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createURL() existing list does not contain URL {0}, will now be created and added.", urltobeadded.getInternetAddress());

                        // Set old url's as invalid to indicate that those entries are obsolete
                        // CANCELLED: This also cancels WRK when PRV is entered. Or 2nd PRV which might work.
                        /*for (URL urlobsolete : urlrolecreatedlist) {
                            URLSQL urlsql = new URLSQL();
                            urlsql.updateType(connection, urlobsolete, URL.TYPE_EMAIL_ERROR);

                            //DEBUG
                            Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createURL() URL {0} marked as obsolete.", urlobsolete.getInternetAddress());

                        }
                        */

                        // Add new url to the end of the list
                        URLSQL urlsql = new URLSQL();
                        //urlsql = new URLSQL();
                        urltobeadded.setRoleid(rolecreated.getRoleid());
                        urltobeadded = urlsql.create(connection, urltobeadded);
                        if (urltobeadded == null) {
                            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create url for role");
                            return null;
                        } else {
                            urlrolecreatedlist.add(urltobeadded);
                        }
                    } else {

                        //DEBUG
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createURL() existing list already contains URL {0}, does not need to be added.", urltobeadded.getInternetAddress());

                        // Set all old url's but the new one as invalid to indicate that those entries are obsolete
                        // CANCELLED: This also cancels WRK when PRV is entered. Or 2nd PRV which might work.
                        /*for (URL urlobsolete : urlrolecreatedlist) {
                            if (!urlobsolete.getInternetAddress().equals(urltobeadded.getInternetAddress())) {
                                URLSQL urlsql = new URLSQL();
                                urlsql.updateType(connection, urlobsolete, URL.TYPE_EMAIL_ERROR);

                                //DEBUG
                                Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createURL() URL {0} marked as obsolete.", urlobsolete.getInternetAddress());

                            }
                        }
                        */
                    }
                }
                return urlrolecreatedlist;
            }
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    private List<Bankaccount> createBankaccount(Connection connection, Role rolecreated, List<Bankaccount> bankaccountlist) {
        if (bankaccountlist == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createBankaccount() Bankaccountlist is null");
            bankaccountlist = new ArrayList();
            return bankaccountlist;
        }
        if (bankaccountlist.isEmpty()) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "createBankaccount() Bankaccountlist is empty");
            return bankaccountlist;
        }
        if (rolecreated == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createBankaccount() Role is null");
            return bankaccountlist;
        }
        if (rolecreated.getRoleid() == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createBankaccount() Roleid is 0");
            return bankaccountlist;
        }
        try {
            List<Bankaccount> bankaccountrolecreatedlist = rolecreated.getBankaccountlist();
            if (bankaccountrolecreatedlist == null) {
                bankaccountrolecreatedlist = new ArrayList();
            }
            if (bankaccountrolecreatedlist.isEmpty()) {
                for (Bankaccount bankaccounttobeadded : bankaccountlist) {

                    //DEBUG
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createBankaccount() add bankaccount number {0} to empty list.", bankaccounttobeadded.getFormattedNumber());

                    BankaccountSQL bankaccountsql = new BankaccountSQL();
                    bankaccounttobeadded.setRoleid(rolecreated.getRoleid());
                    bankaccounttobeadded = bankaccountsql.create(connection, bankaccounttobeadded);
                    if (bankaccounttobeadded == null) {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create bankaccount for role");
                        return null;
                    } else {
                        bankaccountrolecreatedlist.add(bankaccounttobeadded);
                    }
                }
                return bankaccountrolecreatedlist;
            } else {
                for (Bankaccount bankaccounttobeadded : bankaccountlist) {

                    //DEBUG
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createBankaccount() add bankaccount number {0} to existing list.", bankaccounttobeadded.getFormattedNumber());

                    if (!bankaccountrolecreatedlist.contains(bankaccounttobeadded)) {

                        //DEBUG
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createBankaccount() existing list does not contain bankaccount number {0}, will now be created and added.", bankaccounttobeadded.getFormattedNumber());

                        BankaccountSQL bankaccountsql = new BankaccountSQL();
                        bankaccounttobeadded.setRoleid(rolecreated.getRoleid());
                        bankaccounttobeadded = bankaccountsql.create(connection, bankaccounttobeadded);
                        if (bankaccounttobeadded == null) {
                            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create bankaccount for role");
                            return null;
                        } else {
                            bankaccountrolecreatedlist.add(bankaccounttobeadded);
                        }
                    } else {

                        //DEBUG
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "createBankaccount() existing list already contains bankaccount number {0}, does not need to be added.", bankaccounttobeadded.getFormattedNumber());

                    }
                }
                return bankaccountrolecreatedlist;
            }
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public Role read(Connection connection, long roleid) {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;

        Role role = new Role();
        if (roleid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "read() Role id is 0");
            return null;
        }
        try {
            query = "SELECT * FROM contact c WHERE c.pvkey=" + roleid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                long personid = resultset.getInt("peoplekey");
                long addressid = resultset.getInt("addresskey");
                String rectype = resultset.getString(Role.FIELD_CONTACT_RECTYPE);
                PersonSQL personsql = new PersonSQL();
                Person person = personsql.read(connection, personid);
                if (person == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "No Person object for personid {0}, roleid {1}", new Object[]{personid, roleid});
                    return null;
                }
                AddressSQL addresssql = new AddressSQL();
                Address address = addresssql.read(connection, addressid);
                if (address == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "No Address object for addressid {0}, roleid {1}", new Object[]{addressid, roleid});
                    return null;
                }
                if (rectype.equals(Role.RECTYPE_ORGANIZATION)) {
                    String company = addresssql.readCompany(connection, addressid);
                    person.setForenames("");
                    person.setInitials("");
                    person.setMiddle("");
                    person.setSurname(company);
                }
                role.setRoleid(roleid);
                role.setPerson(person);
                role.setAddress(address);
                PhoneSQL phonesql = new PhoneSQL();
                List<Phone> phonelist = phonesql.readPhonelistViaRoleid(connection, roleid);
                URLSQL urlsql = new URLSQL();
                List<URL> urllist = urlsql.readEmaillistViaRoleid(connection, roleid);
                BankaccountSQL bankaccountsql = new BankaccountSQL();
                List<Bankaccount> bankaccountlist = bankaccountsql.readBankaccountlistViaRoleid(connection, roleid);
                role.setPhonelist(phonelist);
                role.setUrllist(urllist);
                role.setBankaccountlist(bankaccountlist);
                role.setStatus(Role.STATUS_MATCHED_ID);
                return role;
            } else {
                return null;
            }
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
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

    public boolean update(Connection connection, Role role) {
        // TODO: This will be used to update person / address combinations.
        // TODO: Watch out for orphaned person and address records that can occur for address changes etc.
        return false;
    }

    public boolean delete(Connection connection, long roleid) {
        // TODO: Implementation for deleting role, this is just a stub
        return false;
    }

    /*
     * ---------------------------------------- Object matching
     * ----------------------------------------
     */
    public Role match(Connection connection, Role role) {
        Role rolefound = null;
        boolean isfoundperson = false;
        boolean isfoundaddress = false;
        List<Long> phoneroleidlist = new ArrayList();
        List<Long> urlroleidlist = new ArrayList();

        if (role == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Role is null");
            rolefound = new Role();
            rolefound.setStatus(Role.STATUS_MATCHED_NONE);
            return rolefound;
        }
        try {
            // Return if there is no person
            if (role.getPerson() == null) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "match(): Person is null");
                role.setStatus(Role.STATUS_MATCHED_NONE);
                return role;
            }
            // Return if there is no address
            if (role.getAddress() == null) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "match(): Address is null");
                role.setStatus(Role.STATUS_MATCHED_NONE);
                return role;
            }
            // Cater for missing phonelist
            if (role.getPhonelist() == null) {
                List<Phone> phonelist = new ArrayList();
                role.setPhonelist(phonelist);
            }
            // Cater for missing urllist
            if (role.getUrllist() == null) {
                List<URL> urllist = new ArrayList();
                role.setUrllist(urllist);
            }

            // Try to find role via roleid first
            rolefound = matchRoleid(connection, role);
            if (!rolefound.isNoMatch()) {
                return rolefound;
            }

            // Try to find person via email address
            rolefound = matchURL(connection, role);
            if (rolefound.isAcceptableMatch()) {
                // Update person details
                PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                return rolefound;
            }
            // Try to find person via phone number
            rolefound = matchPhone(connection, role);
            if (rolefound.isAcceptableMatch()) {
                // Update person details
                PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                return rolefound;
            }

            // Try to find the person and address object first
            Person personfound = matchPerson(connection, role);
            if (personfound.isAcceptableMatch()) {
                isfoundperson = true;
            }
            Address addressfound = matchAddress(connection, role);
            if (addressfound.isAcceptableMatch()) {
                isfoundaddress = true;
            }

            if (isfoundperson && isfoundaddress) {
                // Try to find the role as a combination of the person and address objects
                //DEBUG
                //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match role via person and address objects");
                rolefound = readViaPersonidAddressid(connection, personfound.getPersonid(), addressfound.getAddressid());
                if (rolefound != null) {
                    rolefound.setStatus(Role.STATUS_MATCHED_PERSON_ADDRESS);
                    // Update person details
                    PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                    AddressSQL.updateDetails(connection, rolefound.getAddress(), role.getAddress());
                    //DEBUG
                    //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Role {0} found via person {1} and address {2}", new Object[]{rolefound.getRoleid(), personfound.getPersonid(), addressfound.getAddressid()});
                    return rolefound;
                } else {
                    // The personfound and addressfound combination did not work out, try to find other combinations that do match
                    rolefound = matchPersonToRoleAddress(connection, role, personfound);
                    if (rolefound.isAcceptableMatch()) {
                        // Update person details
                        PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                        AddressSQL.updateDetails(connection, rolefound.getAddress(), role.getAddress());
                        return rolefound;
                    }
                    rolefound = matchAddressToRolePerson(connection, role, addressfound);
                    if (rolefound.isAcceptableMatch()) {
                        // Update person details
                        PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                        AddressSQL.updateDetails(connection, rolefound.getAddress(), role.getAddress());
                        return rolefound;
                    }
                }
            } else {
                // We either have a matching person object or a matching address object, but not both
                if (isfoundperson) {
                    // We may not have an acceptable match for the address but let's see if we can find a role anyway
                    if (addressfound.getStatus() == Address.STATUS_MATCHED_NONE) {
                        rolefound = matchPersonToRoleAddress(connection, role, personfound);
                        if (!rolefound.isNoMatch()) {
                            // Update person details
                            PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                            // Update address details
                            AddressSQL.updateDetails(connection, rolefound.getAddress(), role.getAddress());
                            return rolefound;
                        }
                    } else {
                        //DEBUG
                        //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match role via person and partly matched address objects");
                        rolefound = readViaPersonidAddressid(connection, personfound.getPersonid(), addressfound.getAddressid());
                        if (rolefound != null) {
                            rolefound.setStatus(Role.STATUS_MATCHED_PERSON_ADDRESS);
                            // Update person details
                            PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                            // Update address details
                            AddressSQL.updateDetails(connection, rolefound.getAddress(), role.getAddress());
                            //DEBUG
                            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Role {0} found via person {1} and address {2}", new Object[]{rolefound.getRoleid(), personfound.getPersonid(), addressfound.getAddressid()});
                            return rolefound;
                        }
                    }
                    rolefound = matchPersonToRolePhone(connection, role, personfound);
                    if (rolefound.isAcceptableMatch()) {
                        // Update person details
                        PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                        // Update address details
                        AddressSQL.updateDetails(connection, rolefound.getAddress(), role.getAddress());
                        return rolefound;
                    }
                    rolefound = matchPersonToRoleURL(connection, role, personfound);
                    if (rolefound.isAcceptableMatch()) {
                        // Update person details
                        PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                        // Update address details
                        AddressSQL.updateDetails(connection, rolefound.getAddress(), role.getAddress());
                        return rolefound;
                    }
                }
                if (isfoundaddress) {
                    // We may not have an acceptable match for the person but let's see if we can find a role anyway
                    if (personfound.getStatus() == Person.STATUS_MATCHED_NONE) {
                        rolefound = matchAddressToRolePerson(connection, role, addressfound);
                        if (rolefound.isAcceptableMatch()) {
                            // Update person details
                            PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                            return rolefound;
                        }
                    } else {
                        //DEBUG
                        //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match role via partly matched person and address objects");
                        rolefound = readViaPersonidAddressid(connection, personfound.getPersonid(), addressfound.getAddressid());
                        if (rolefound != null) {
                            rolefound.setStatus(Role.STATUS_MATCHED_PERSON_ADDRESS);
                            // Update person details
                            PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                            //DEBUG
                            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Role {0} found via person {1} and address {2}", new Object[]{rolefound.getRoleid(), personfound.getPersonid(), addressfound.getAddressid()});
                            return rolefound;
                        }
                    }
                    rolefound = matchAddressToRolePhone(connection, role, addressfound);
                    if (rolefound.isAcceptableMatch()) {
                        // Update person details
                        PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                        return rolefound;
                    }
                    rolefound = matchAddressToRoleURL(connection, role, addressfound);
                    if (rolefound.isAcceptableMatch()) {
                        // Update person details
                        PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                        return rolefound;
                    }
                }
            }
            // Test if there is only one role for the supplied emailaddress, if so, consider the role found
            if (urlroleidlist.size() == 1) {
                RoleSQL rolesql = new RoleSQL();
                rolefound = rolesql.read(connection, urlroleidlist.get(0));
                if (rolefound != null) {
                    rolefound.setStatus(Role.STATUS_MATCHED_URL);
                    // Update person details
                    PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                    // Update address details
                    AddressSQL.updateDetails(connection, rolefound.getAddress(), role.getAddress());
                    return rolefound;
                }
            }
            // Test if there is only one role for the supplied phonenumber, if so, consider the role found
            if (phoneroleidlist.size() == 1) {
                RoleSQL rolesql = new RoleSQL();
                rolefound = rolesql.read(connection, phoneroleidlist.get(0));
                if (rolefound != null) {
                    rolefound.setStatus(Role.STATUS_MATCHED_PHONE);
                    // Update person details
                    PersonSQL.updateDetails(connection, rolefound.getPerson(), role.getPerson());
                    // Update address details
                    AddressSQL.updateDetails(connection, rolefound.getAddress(), role.getAddress());
                    return rolefound;
                }
            }

            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): No matching role found");

            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        }
    }

    private Role matchPhone(Connection connection, Role role) {
        RoleSQL rolesql = new RoleSQL();
        PhoneSQL phonesql = new PhoneSQL();
        String surnameobject = "";
        String surnamesubject = "";
        boolean found = false;
        try {
            for (Phone phone : role.getPhonelist()) {
                if (phone.getNumber() != 0) {
                    //DEBUG
                    //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "matchPhone(): Begin to read phone list via phone number {0}", phone.getNumber());
                    List<Phone> phonefoundlist = phonesql.readPhonelistViaNumber(connection, phone.getNumber());
                    for (Phone phonefound : phonefoundlist) {
                        if (phonefound.getRoleid() != 0) {
                            Role rolefound = rolesql.read(connection, phonefound.getRoleid());
                            if (rolefound != null) {
                                surnameobject = role.getPerson().getSurname().toLowerCase();
                                surnamesubject = rolefound.getPerson().getSurname().toLowerCase();
                                //DEBUG
                                //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "matchPhone(): Begin to match person''s surname {0} for phone number {1} to person''s surname {2}", new Object[]{surnamesubject, phone.getNumber(), surnameobject});
                                if (surnameobject.contains(surnamesubject)) {
                                    found = true;
                                }
                                if (surnamesubject.contains(surnameobject)) {
                                    found = true;
                                }
                                if (found) {
                                    //DEBUG
                                    //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "matchPhone(): Found roleid {0} for phone number {1}", new Object[]{rolefound.getRoleid(), phone.getNumber()});
                                    rolefound.setStatus(Role.STATUS_MATCHED_PERSON_PHONE);
                                    return rolefound;
                                }
                            }
                        }
                    }
                }
            }
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        }
    }

    private Role matchURL(Connection connection, Role role) {
        RoleSQL rolesql = new RoleSQL();
        URLSQL urlsql = new URLSQL();
        String surnameobject;
        String surnamesubject;
        boolean found = false;
        try {
            for (URL url : role.getUrllist()) {
                if (url.isInternetAddress()) {
                    //DEBUG
                    //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "matchURL(): Begin to read URL list via email address {0}", url.getInternetAddress());
                    List<URL> urlfoundlist = urlsql.readEmailViaAddress(connection, url.getInternetAddress());
                    if (urlfoundlist.size() == 1) { 
                        // Bert 18062013: precies 1 gevonden -- die nemen we! Het is lastig vast te stellen of het precies de juiste persoon is, maar in de meeste gevallen
                        // zal dit zo zijn. Pete vindt nu veel gevallen waar e-mail niet correct gematched is. Eens proberen of het zo beter gaat.
                        Role rolefound = rolesql.read(connection, urlfoundlist.get(0).getRoleid());
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "matchURL(): Found {0} for role {1}", new Object[]{url.getInternetAddress(), urlfoundlist.get(0).getRoleid()});
                        rolefound.setStatus(Role.STATUS_MATCHED_PERSON_URL);
                        return rolefound;
                    }
                    for (URL urlfound : urlfoundlist) {
                        if (urlfound.getRoleid() != 0) {
                            Role rolefound = rolesql.read(connection, urlfound.getRoleid());
                            if (rolefound != null) {
                                surnameobject = role.getPerson().getSurname().toLowerCase();
                                surnamesubject = rolefound.getPerson().getSurname().toLowerCase();
                                //DEBUG
                                //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "matchURL(): Begin to match person''s surname {0} for URL {1} to person''s surname {2}", new Object[]{surnamesubject, url.getInternetAddress(), surnameobject});
                                if (surnameobject.contains(surnamesubject)) {
                                    found = true;
                                }
                                if (surnamesubject.contains(surnameobject)) {
                                    found = true;
                                }
                                if (found) {
                                    //DEBUG
                                    //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "matchURL(): Found roleid {0} for URL {1}", new Object[]{rolefound.getRoleid(), url.getInternetAddress()});
                                    rolefound.setStatus(Role.STATUS_MATCHED_PERSON_URL);
                                    return rolefound;
                                }
                            }
                        }
                    }
                }
            }
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        }
    }

    private Role matchRoleid(Connection connection, Role role) {
        try {
            if (role.getRoleid() != 0) {
                Role rolefound = read(connection, role.getRoleid());
                if (rolefound != null) {
                    rolefound.setStatus(Address.STATUS_MATCHED_ID);
                    return rolefound;
                }
            }
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        }
    }

    private Person matchPerson(Connection connection, Role role) {
        try {
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match person");
            PersonSQL personsql = new PersonSQL();
            Person personfound = personsql.match(connection, role.getPerson());
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Person match status is {0}", personfound.getStatus());
            if (personfound.isAcceptableMatch()) {
                //DEBUG
                //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Found person with personid {0}", personfound.getPersonid());
                return personfound;
            }
            return personfound;
        } catch (CRMPersonException ex) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, null, ex);
            Person personfound = role.getPerson();
            personfound.setStatus(Role.STATUS_MATCHED_NONE);
            return personfound;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            Person personfound = role.getPerson();
            personfound.setStatus(Role.STATUS_MATCHED_NONE);
            return personfound;
        }
    }

    private Address matchAddress(Connection connection, Role role) {
        try {
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match address");
            AddressSQL addresssql = new AddressSQL();
            Address addressfound = addresssql.match(connection, role.getAddress());
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Address match status is {0}", addressfound.getStatus());
            if (addressfound.isAcceptableMatch()) {
                //DEBUG
                //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Found address with addressid {0}", addressfound.getAddressid());
                return addressfound;
            }
            return addressfound;
        } catch (CRMAddressException ex) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, null, ex);
            Address addressfound = role.getAddress();
            addressfound.setStatus(Role.STATUS_MATCHED_NONE);
            return addressfound;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            Address addressfound = role.getAddress();
            addressfound.setStatus(Role.STATUS_MATCHED_NONE);
            return addressfound;
        }
    }

    private Role matchPersonToRoleAddress(Connection connection, Role role, Person personfound) {
        try {
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match role via person and non-matched address objects");
            List<Role> rolelist = readViaPersonid(connection, personfound.getPersonid());
            for (Role roleviapersonid : rolelist) {
                if (roleviapersonid.getAddress() != null && role.getAddress() != null) {
                    // Do not match against blank postal codes
                    if (roleviapersonid.getAddress().getPostalcodenumeric() != 0 && !roleviapersonid.getAddress().getPostalcodealpha().isEmpty()) {
                        // Check numeric and alphanumeric postalcode elements
                        if (roleviapersonid.getAddress().getPostalcodenumeric() == role.getAddress().getPostalcodenumeric()) {
                            if (roleviapersonid.getAddress().getPostalcodealpha().equals(role.getAddress().getPostalcodealpha())) {
                                Role rolefound = roleviapersonid;
                                rolefound.setStatus(Role.STATUS_MATCHED_PERSON_ADDRESS);
                                //DEBUG
                                //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Role {0} found via person {1} and address {2}", new Object[]{rolefound.getRoleid(), personfound.getPersonid(), roleviapersonid.getAddress().getAddressid()});
                                return rolefound;
                            }
                        }
                    }
                }
            }
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        }
    }

    private Role matchAddressToRolePerson(Connection connection, Role role, Address addressfound) {
        try {
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match role via address and non-matched person objects");
            List<Role> rolelist = readViaAddressid(connection, addressfound.getAddressid());
            for (Role roleviaaddressid : rolelist) {
                if (roleviaaddressid.getPerson() != null && role.getPerson() != null) {
                    if (roleviaaddressid.getPerson().getSurname().equals(role.getPerson().getSurname())) {
                        if (roleviaaddressid.getPerson().getInitials().equals(role.getPerson().getInitials())) {
                            Role rolefound = roleviaaddressid;
                            rolefound.setStatus(Role.STATUS_MATCHED_PERSON_ADDRESS);
                            //DEBUG
                            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Role {0} found via person {1} and address {2}", new Object[]{rolefound.getRoleid(), roleviaaddressid.getPerson().getPersonid(), addressfound.getAddressid()});
                            return rolefound;
                        }
                    }
                }
            }
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        }
    }

    private Role matchPersonToRolePhone(Connection connection, Role role, Person personfound) {
        List<Long> phoneroleidlist = new ArrayList();
        boolean isfoundphone = false;
        try {
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match phone");
            for (Phone phone : role.getPhonelist()) {
                PhoneSQL phonesql = new PhoneSQL();
                Phone phonefound = phonesql.match(connection, phone);
                if (phonefound.isAcceptableMatch()) {
                    if (!phoneroleidlist.contains(phonefound.getRoleid())) {
                        phoneroleidlist.add(phonefound.getRoleid());
                    }
                    //DEBUG
                    //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Found phone with phoneid {0}, roleid {1}", new Object[]{phonefound.getPhoneid(), phonefound.getRoleid()});
                    isfoundphone = true;
                }
            }
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match role via person and phone/url combos");
            List<Long> roleidlist = readPersonRoleidList(connection, personfound.getPersonid());
            if (isfoundphone) {
                // Try to find a person phone object combination
                for (long roleid : roleidlist) {
                    if (phoneroleidlist.contains(roleid)) {
                        RoleSQL rolesql = new RoleSQL();
                        Role rolefound = rolesql.read(connection, roleid);
                        rolefound.setStatus(Role.STATUS_MATCHED_PERSON_PHONE);
                        //DEBUG
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Role {0} found via person {1} and phone", new Object[]{rolefound.getRoleid(), personfound.getPersonid()});
                        return rolefound;
                    }
                }
            }
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (CRMPhoneException cpe) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, cpe.getMessage(), cpe);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        }
    }

    private Role matchPersonToRoleURL(Connection connection, Role role, Person personfound) {
        List<Long> urlroleidlist = new ArrayList();
        boolean isfoundurl = false;
        try {
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match url");
            for (URL url : role.getUrllist()) {
                URLSQL urlsql = new URLSQL();
                URL urlfound = urlsql.match(connection, url);
                if (urlfound.isAcceptableMatch()) {
                    if (!urlroleidlist.contains(urlfound.getRoleid())) {
                        urlroleidlist.add(urlfound.getRoleid());
                    }
                    //DEBUG
                    //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Found url with urlid {0}, roleid {1}", new Object[]{urlfound.getUrlid(), urlfound.getRoleid()});
                    isfoundurl = true;
                }
            }
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match role via person and url combos");
            List<Long> roleidlist = readPersonRoleidList(connection, personfound.getPersonid());
            if (isfoundurl) {
                // Try to find a person url object combination
                for (long roleid : roleidlist) {
                    if (urlroleidlist.contains(roleid)) {
                        RoleSQL rolesql = new RoleSQL();
                        Role rolefound = rolesql.read(connection, roleid);
                        rolefound.setStatus(Role.STATUS_MATCHED_PERSON_PHONE);
                        //DEBUG
                        //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Role {0} found via person {1} and url", new Object[]{rolefound.getRoleid(), personfound.getPersonid()});
                        return rolefound;
                    }
                }
            }
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (CRMURLException cue) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, cue.getMessage(), cue);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        }
    }

    private Role matchAddressToRolePhone(Connection connection, Role role, Address addressfound) {
        List<Long> phoneroleidlist = new ArrayList();
        boolean isfoundphone = false;
        try {
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match phone");
            for (Phone phone : role.getPhonelist()) {
                PhoneSQL phonesql = new PhoneSQL();
                Phone phonefound = phonesql.match(connection, phone);
                if (phonefound.isAcceptableMatch()) {
                    if (!phoneroleidlist.contains(phonefound.getRoleid())) {
                        phoneroleidlist.add(phonefound.getRoleid());
                    }
                    //DEBUG
                    //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Found phone with phoneid {0}, roleid {1}", new Object[]{phonefound.getPhoneid(), phonefound.getRoleid()});
                    isfoundphone = true;
                }
            }
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match role via address and phone/url combos");
            List<Long> roleidlist = readAddressRoleidList(connection, addressfound.getAddressid());
            if (isfoundphone) {
                // Try to find a address phone object combination
                for (long roleid : roleidlist) {
                    if (phoneroleidlist.contains(roleid)) {
                        RoleSQL rolesql = new RoleSQL();
                        role = rolesql.read(connection, roleid);
                        role.setStatus(Role.STATUS_MATCHED_PERSON_PHONE);
                        //DEBUG
                        //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Role {0} found via address {1} and phone", new Object[]{role.getRoleid(), addressfound.getAddressid()});
                        return role;
                    }
                }
            }
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (CRMPhoneException cpe) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, cpe.getMessage(), cpe);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        }
    }

    private Role matchAddressToRoleURL(Connection connection, Role role, Address addressfound) {
        List<Long> urlroleidlist = new ArrayList();
        boolean isfoundurl = false;
        try {
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match url");
            for (URL url : role.getUrllist()) {
                URLSQL urlsql = new URLSQL();
                URL urlfound = urlsql.match(connection, url);
                if (urlfound.isAcceptableMatch()) {
                    if (!urlroleidlist.contains(urlfound.getRoleid())) {
                        urlroleidlist.add(urlfound.getRoleid());
                    }
                    //DEBUG
                    //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Found url with urlid {0}, roleid {1}", new Object[]{urlfound.getUrlid(), urlfound.getRoleid()});
                    isfoundurl = true;
                }
            }
            //DEBUG
            ///Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match role via address and url/url combos");
            List<Long> roleidlist = readAddressRoleidList(connection, addressfound.getAddressid());
            if (isfoundurl) {
                // Try to find a address url object combination
                for (long roleid : roleidlist) {
                    if (urlroleidlist.contains(roleid)) {
                        RoleSQL rolesql = new RoleSQL();
                        role = rolesql.read(connection, roleid);
                        role.setStatus(Role.STATUS_MATCHED_PERSON_PHONE);
                        //DEBUG
                        //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Role {0} found via address {1} and url", new Object[]{role.getRoleid(), addressfound.getAddressid()});
                        return role;
                    }
                }
            }
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (CRMURLException cue) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, cue.getMessage(), cue);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            role.setStatus(Role.STATUS_MATCHED_NONE);
            return role;
        }
    }
    /*
     * ---------------------------------------- CREATE methods
     * ----------------------------------------
     */

    private Role createRole(Connection connection, String source, Person person, Address address) {
        Statement statement = null;
        ResultSet resultset = null;
        Role role = new Role();
        if (person == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createRole() Person is null");
            return null;
        }
        if (address == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "createRole() Address is null");
            return null;
        }
        try {
            if (role.getRoleid() == 0) {
                KeyGenerator keygenerator = new KeyGenerator(connection);
                long roleid = keygenerator.getNextKey(KeyGenerator.KEY_NEXTRECNO);
                // Set roleid in role object
                role.setRoleid(roleid);
            }

            String SQL = "SELECT * FROM contact";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();

            resultset = setDefaultFieldValues(resultset);

            resultset.updateInt(Role.FIELD_CONTACT_PERSONID, (int) person.getPersonid());
            resultset.updateInt(Role.FIELD_CONTACT_ADDRESSID, (int) address.getAddressid());
            //resultset.updateString(FIELD_CONTACT_PHONE, "0".concat(String.valueOf(phonenumber)));
            resultset.updateString(Role.FIELD_CONTACT_SOURCE, source);

            resultset.updateInt(Role.FIELD_CONTACT_ROLEID, (int) role.getRoleid());

            resultset.insertRow();
            resultset.close();
            statement.close();

            //Set to P-SCHADUW if person and address details are below minimum
            if (person.getSurname().isEmpty() || address.getPostalcodealpha().isEmpty() || address.getPostalcodenumeric() == 0 || address.getHouseno() == 0) {
                updateContacttype(connection, role.getRoleid(), "P-SCHADUW");
            }

            role.setPerson(person);
            role.setAddress(address);
            List<Bankaccount> bankaccountlist = new ArrayList();
            role.setBankaccountlist(bankaccountlist);
            List<Phone> phonelist = new ArrayList();
            role.setPhonelist(phonelist);
            List<URL> urllist = new ArrayList();
            role.setUrllist(urllist);
            role.setSource("");
            role.setStatus(Role.STATUS_NEW);
            return role;
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

    /*
     * ---------------------------------------- READ methods
     * ----------------------------------------
     */
    private List<Long> readPersonRoleidList(Connection connection, long personid) {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        List<Long> roleidlist = new ArrayList();
        if (personid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "readPersonRoleidList() Person id is 0");
            return null;
        }
        try {
            query = "SELECT * FROM contact c WHERE c.peoplekey=" + personid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    long roleid = resultset.getInt("pvkey");
                    roleidlist.add(roleid);
                } while (resultset.next());
                return roleidlist;
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

    private List<Long> readAddressRoleidList(Connection connection, long addressid) {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        List<Long> roleidlist = new ArrayList();
        if (addressid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "readAddressRoleidList() Address id is 0");
            return null;
        }
        try {
            query = "SELECT * FROM contact c WHERE c.addresskey=" + addressid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    long roleid = resultset.getInt(Role.FIELD_CONTACT_ROLEID);
                    roleidlist.add(roleid);
                } while (resultset.next());
                return roleidlist;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, null, sqle);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public Role readViaEmail(Connection connection, String email) {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        if (email == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "readViaEmail() Email is null");
            return null;
        }
        if (email.isEmpty()) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "readViaEmail() Email is empty");
            return null;
        }
        try {
            email = email.replace(";", "").replace("%", "").replace("&", "");
            query = "SELECT * FROM contnos c WHERE rtrim(ltrim(c.contactno))=rtrim(ltrim('" + email + "')) AND c.phntyp IN ('EMAILPRV','EMAILWRK') ORDER BY c.pvkey";
            //query = "select top 1 c.pvkey from contnos o join contact c on o.pvkey = c.pvkey join people p on c.peoplekey = p.peoplekey where contactno = '" + email + "'";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                int roleid = resultset.getInt("pvkey");
                Role role = read(connection, roleid);
                if (role != null) {
                    role.setStatus(Role.STATUS_MATCHED_URL);
                    return role;
                } else {
                    return null;
                }
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

    public List<Role> readEmailRolelist(Connection connection, String email) {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        List<Role> rolelist = new ArrayList();
        if (email == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "readViaEmail() Email is null");
            return null;
        }
        if (email.isEmpty()) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "readViaEmail() Email is empty");
            return null;
        }
        try {
            email = email.replace(";", "").replace("%", "").replace("&", "");
            query = "SELECT * FROM contnos c WHERE rtrim(ltrim(c.contactno))=rtrim(ltrim('" + email + "')) AND c.phntyp IN ('EMAILPRV','EMAILWRK') ORDER BY c.pvkey";
            //query = "select top 1 c.pvkey from contnos o join contact c on o.pvkey = c.pvkey join people p on c.peoplekey = p.peoplekey where contactno = '" + email + "'";
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    int roleid = resultset.getInt("pvkey");
                    Role role = read(connection, roleid);
                    if (role != null) {
                        role.setStatus(Role.STATUS_MATCHED_URL);
                        rolelist.add(role);
                    }
                } while (resultset.next());
                return rolelist;
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

    private List<Role> readViaPersonid(Connection connection, long personid) {
        List<Role> rolelist = new ArrayList();
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        if (personid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "readViaPersonid() Person id is 0");
            return null;
        }
        try {
            query = "SELECT * FROM contact c WHERE c.peoplekey=" + personid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    long roleid = resultset.getInt(Role.FIELD_CONTACT_ROLEID);
                    if (roleid != 0) {
                        Role role = read(connection, roleid);
                        if (role != null) {
                            rolelist.add(role);
                        }
                    }
                } while (resultset.next());
            }
            return rolelist;
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

    private List<Role> readViaAddressid(Connection connection, long addressid) {
        List<Role> rolelist = new ArrayList();
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        if (addressid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "readViaAddessid() Address id is 0");
            return null;
        }
        try {
            query = "SELECT * FROM contact c WHERE c.addresskey=" + addressid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                do {
                    long roleid = resultset.getInt(Role.FIELD_CONTACT_ROLEID);
                    if (roleid != 0) {
                        Role role = read(connection, roleid);
                        if (role != null) {
                            rolelist.add(role);
                        }
                    }
                } while (resultset.next());
            }
            return rolelist;
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

    /**
     * Find a role based on the person id and address id.
     *
     * @param connection
     * @param personid
     * @param addressid
     * @return
     */
    private Role readViaPersonidAddressid(Connection connection, long personid, long addressid) {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;

        Role role = new Role();
        if (personid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "readViaPersonidAddressid() Person id is 0");
            return null;
        }
        if (addressid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "readViaPersonidAddressid() Address id is 0");
            return null;
        }
        try {
            query = "SELECT * FROM contact c WHERE c.peoplekey=" + personid + " AND c.addresskey=" + addressid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                long roleid = resultset.getInt("pvkey");
                PersonSQL personsql = new PersonSQL();
                Person person = personsql.read(connection, personid);
                if (person == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "No Person object for personid {0}, roleid {1}", new Object[]{personid, roleid});
                    return null;
                }
                AddressSQL addresssql = new AddressSQL();
                Address address = addresssql.read(connection, addressid);
                if (address == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "No Address object for addressid {0}, roleid {1}", new Object[]{addressid, roleid});
                    return null;
                }
                //long urlid = roleid;
                PhoneSQL phonesql = new PhoneSQL();
                List<Phone> phonelist = phonesql.readPhonelistViaRoleid(connection, roleid);
                URLSQL urlsql = new URLSQL();
                List<URL> urllist = urlsql.readEmaillistViaRoleid(connection, roleid);
                BankaccountSQL bankaccountsql = new BankaccountSQL();
                List<Bankaccount> bankaccountlist = bankaccountsql.readBankaccountlistViaRoleid(connection, roleid);
                role.setRoleid(roleid);
                role.setPerson(person);
                role.setAddress(address);
                role.setPhonelist(phonelist);
                role.setUrllist(urllist);
                role.setBankaccountlist(bankaccountlist);
                role.setStatus(Role.STATUS_MATCHED_PERSON_ADDRESS);
                return role;
            } else {
                return null;
            }
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
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


    /*
     * ---------------------------------------- UPDATE methods
     * ----------------------------------------
     */
    public Role changeAddress(Connection connection, Role role, Address addressold, Address addressnew) {
        if (addressold == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changeAddress() Address is null");
            return null;
        }
        if (addressnew == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changeAddress() Address is null");
            return null;
        }
        try {
            // Save the old address for audit purposes
            //AddressSQL.createPrevious(connection, addressold, role.getRoleid());

            AddressSQL addresssql = new AddressSQL();
            Address addressnewfound = addresssql.match(connection, addressnew);
            if (addressnewfound.isAcceptableMatch()) {
                // New address already present in CRM system
                // Check if it is a decent address, with street name, housenumber etc.
                if (addressnewfound.isDecentAddress()) {
                    // Retain person of existing role and replace address id with the one from the matched address
                    if (!updateAddressid(connection, role.getRoleid(), addressnewfound.getAddressid())) {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to update address with addressid {0} for role with roleid {1}", new Object[]{addressnewfound.getAddressid(), role.getRoleid()});
                        return null;
                    } else {
                        role.setAddress(addressnewfound);
                        return role;
                    }
                } else {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "No proper address with addressid {0} for role with roleid {1}", new Object[]{addressnewfound.getAddressid(), role.getRoleid()});
                    return null;
                }
            } else {
                // New address needs to be created
                addressnew = addresssql.create(connection, addressnew);
                if (addressnew == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create address with addressid {0}", addressnewfound.getAddressid());
                    return null;
                }
                // Check if it is a decent address, with street name, housenumber etc.
                if (addressnew.isDecentAddress()) {
                    // Retain person of existing role and replace address with newly created address
                    if (!updateAddressid(connection, role.getRoleid(), addressnew.getAddressid())) {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to update address with addressid {0} for role with roleid {1}", new Object[]{addressnew.getAddressid(), role.getRoleid()});
                        return null;
                    } else {
                        role.setAddress(addressnew);
                        return role;
                    }
                } else {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "No proper address with addressid {0} for role with roleid {1}", new Object[]{addressnewfound.getAddressid(), role.getRoleid()});
                    return null;
                }
            }
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public Role addEmail(Connection connection, Role role, URL url) {
        if (role == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "addEmail() Role is null");
            return null;
        }
        if (role.getRoleid() == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "addEmail() Roleid is 0");
            return null;
        }
        if (url == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "addEmail() URL is null");
            return null;
        }
        if (!url.getProtocol().equals(URL.PROTOCOL_EMAIL)) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "addEmail() URL protocol {0} differs from being PROTOCOL_EMAIL", url.getProtocol());
            url.setProtocol(URL.PROTOCOL_EMAIL);
        }
        try {
            if (!role.hasEmail(url.getInternetAddress())) {
                // Add the new e-mail address
                url.setRoleid(role.getRoleid());
                URLSQL urlsql = new URLSQL();
                url.setRoleid(role.getRoleid());
                URL urlcreated = urlsql.create(connection, url);
                if (urlcreated == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to add email {0} for roleid {0}", new Object[]{url.getInternetAddress(), role.getRoleid()});
                    return null;
                }
                // Rescan the url list...
                List<URL> urllist = urlsql.readEmaillistViaRoleid(connection, role.getRoleid());
                if (urllist == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to read emaillist for roleid {0}", new Object[]{role.getRoleid()});
                    return null;
                }
                role.setUrllist(urllist);
            }
            return role;
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public Role changeEmail(Connection connection, Person person, Address address, URL urlold, URL urlnew) {
        Role rolefound = null;
        if (person == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changeEmail() Person is null");
            return null;
        }
        if (address == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changeEmail() Address is null");
            return null;
        }
        if (urlold == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changeEmail() URL is null");
            return null;
        }
        if (!urlold.isInternetAddress()) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changeEmail() URL is not a valid internetaddress");
            return null;
        }
        if (urlnew == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changeEmail() URL is null");
            return null;
        }
        if (!urlnew.isInternetAddress()) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changeEmail() URL is not a valid internetaddress");
            return null;
        }
        if (!urlold.getProtocol().equals(URL.PROTOCOL_EMAIL)) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "addEmail() URL protocol {0} differs from being PROTOCOL_EMAIL", urlold.getProtocol());
            urlold.setProtocol(URL.PROTOCOL_EMAIL);
        }
        if (!urlnew.getProtocol().equals(URL.PROTOCOL_EMAIL)) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "addEmail() URL protocol {0} differs from being PROTOCOL_EMAIL", urlnew.getProtocol());
            urlnew.setProtocol(URL.PROTOCOL_EMAIL);
        }
        try {
            PersonSQL personsql = new PersonSQL();
            Person personfound = personsql.match(connection, person);
            if (!personfound.isAcceptableMatch()) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to change email adress from {0} to {1} because person is not found", new Object[]{urlold.getInternetAddress(), urlnew.getInternetAddress()});
                return null;
            }
            AddressSQL addresssql = new AddressSQL();
            Address addressfound = addresssql.match(connection, address);
            if (!addressfound.isAcceptableMatch()) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to change emailadress from {0} to {1} because address is not found", new Object[]{urlold.getInternetAddress(), urlnew.getInternetAddress()});
                return null;
            }
            rolefound = readViaPersonidAddressid(connection, personfound.getPersonid(), addressfound.getAddressid());
            if (rolefound == null) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to change emailadress from {0} to {1} because role is not found", new Object[]{urlold.getInternetAddress(), urlnew.getInternetAddress()});
                return null;
            }
            List<URL> urllist = rolefound.getUrllist();
            if (urllist == null) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to read urllist for roleid {0}", new Object[]{rolefound.getRoleid()});
                return null;
            }
            // Delete the old e-mail address
            URLSQL urlsql = new URLSQL();
            for (URL url : urllist) {
                if (url.equalsEmail(urlold)) {
                    if (!urlsql.delete(connection, url.getUrlid())) {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Error while deleting emailaddress {0} urlid {1} for roleid {2}", new Object[]{url.getInternetAddress(), url.getUrlid(), rolefound.getRoleid()});
                        return null;
                    }
                }
            }
            // Add the new e-mail address
            urlnew.setRoleid(rolefound.getRoleid());
            URL url = urlsql.create(connection, urlnew);
            if (url == null) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create url for roleid {0}", new Object[]{rolefound.getRoleid()});
                return null;
            }
            // Rescan the url list...
            urllist = urlsql.readEmaillistViaRoleid(connection, rolefound.getRoleid());
            if (urllist == null) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to read urllist for roleid {0}", new Object[]{rolefound.getRoleid()});
                return null;
            }
            rolefound.setUrllist(urllist);
            return rolefound;
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public Role addPhone(Connection connection, Role role, Phone phone) {
        if (role == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "addPhone() Role is null");
            return null;
        }
        if (role.getRoleid() == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "addPhone() Roleid is 0");
            return null;
        }
        if (phone == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "addPhone() Phone is null");
            return null;
        }
        if (phone.getNumber() == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "addPhone() Phonenumber is 0");
            return null;
        }
        try {
            if (role.hasPhone(phone.getNumber())) {
                // Add the new phone number
                phone.setRoleid(role.getRoleid());
                PhoneSQL phonesql = new PhoneSQL();
                phone.setRoleid(role.getRoleid());
                Phone phonecreated = phonesql.create(connection, phone);
                if (phonecreated == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to add phonenumber {0} for roleid {1}", new Object[]{phone.getFormattedNumber(), role.getRoleid()});
                    return null;
                }
                // Rescan the phone list...
                List<Phone> phonelist = phonesql.readPhonelistViaRoleid(connection, role.getRoleid());
                if (phonelist == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to read urllist for roleid {0}", new Object[]{role.getRoleid()});
                    return null;
                }
                role.setPhonelist(phonelist);
            }
            return role;
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public Role addBankaccount(Connection connection, Role role, Bankaccount bankaccount) {
        if (role == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "addBankaccount() Role is null");
            return null;
        }
        if (role.getRoleid() == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "addBankaccount() Roleid is 0");
            return null;
        }
        if (bankaccount == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "addBankaccount() Bankaccount is null");
            return null;
        }
        try {
            if (role.hasBankaccount(bankaccount.getNumber())) {
                // Add the new bankaccount number
                bankaccount.setRoleid(role.getRoleid());
                BankaccountSQL bankaccountsql = new BankaccountSQL();
                Bankaccount bankaccountcreated = bankaccountsql.create(connection, bankaccount);
                if (bankaccountcreated == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to add bankaccountnumber {0} for roleid {1}", new Object[]{bankaccount.getFormattedNumber(), role.getRoleid()});
                    return null;
                }
                // Rescan the bankaccount list...
                List<Bankaccount> bankaccountlist = bankaccountsql.readBankaccountlistViaRoleid(connection, role.getRoleid());
                if (bankaccountlist == null) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to read bankaccountlist for roleid {0}", new Object[]{role.getRoleid()});
                    return null;
                }
                role.setBankaccountlist(bankaccountlist);
            }
            return role;
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public Role changePhone(Connection connection, Person person, Address address, Phone phoneold, Phone phonenew) {
        Role rolefound = null;
        if (person == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changePhone() Person is null");
            return null;
        }
        if (address == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changePhone() Address is null");
            return null;
        }
        if (phoneold == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changePhone() Phone is null");
            return null;
        }
        if (phoneold.getNumber() != 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changePhone() Phonenumber is 0");
            return null;
        }
        if (phonenew == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changePhone() Phone is null");
            return null;
        }
        if (phonenew.getNumber() != 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "changePhone() Phonenumber is 0");
            return null;
        }
        try {
            PersonSQL personsql = new PersonSQL();
            Person personfound = personsql.match(connection, person);
            if (!personfound.isAcceptableMatch()) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to change phonenumber from {0} to {1} because person is not found", new Object[]{phoneold.getFormattedNumber(), phonenew.getFormattedNumber()});
                return null;
            }
            AddressSQL addresssql = new AddressSQL();
            Address addressfound = addresssql.match(connection, address);
            if (!addressfound.isAcceptableMatch()) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to change phonenumber from {0} to {1} because address is not found", new Object[]{phoneold.getFormattedNumber(), phonenew.getFormattedNumber()});
                return null;
            }
            rolefound = readViaPersonidAddressid(connection, personfound.getPersonid(), addressfound.getAddressid());
            if (rolefound == null) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to change phonenumber from {0} to {1} because role is not found", new Object[]{phoneold.getFormattedNumber(), phonenew.getFormattedNumber()});
                return null;
            }
            List<Phone> phonelist = rolefound.getPhonelist();
            PhoneSQL phonesql = new PhoneSQL();
            for (Phone phone : phonelist) {
                if (phone.equalsNumber(phonenew)) {
                    if (!phonesql.delete(connection, phone.getPhoneid())) {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Error while deleting phonenumber {0} phoneid {1} for roleid {2}", new Object[]{phone.getFormattedNumber(), phone.getPhoneid(), rolefound.getRoleid()});
                        return null;
                    }
                }
            }
            phonenew.setRoleid(rolefound.getRoleid());
            Phone phone = phonesql.create(connection, phonenew);
            if (phone == null) {
                Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to create phone for roleid {0}", new Object[]{rolefound.getRoleid()});
                return null;
            }
            // Rescan the phone list...
            phonelist = phonesql.readPhonelistViaRoleid(connection, rolefound.getRoleid());
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Unable to read phonelist for roleid {0}", new Object[]{rolefound.getRoleid()});
            rolefound.setPhonelist(phonelist);
            return rolefound;
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return null;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    private boolean updateAddressid(Connection connection, long roleid, long addressid) {
        String query = "";
        String sql = "";
        Statement statement = null;
        ResultSet resultset = null;
        if (roleid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "updateAddressid() Role id is 0");
            return false;
        }
        if (addressid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "updateAddresid() Address id is 0");
            return false;
        }
        try {
            query = "SELECT * FROM contact c WHERE c.pvkey=" + roleid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                long addressidold = resultset.getInt("addresskey");
                resultset.close();
                statement.close();
                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                sql = "UPDATE contact SET " + Role.FIELD_CONTACT_ADDRESSID + "=" + String.valueOf(addressid) + " WHERE " + Role.FIELD_CONTACT_ROLEID + "=" + roleid;
                int rowcount = statement.executeUpdate(sql);
                statement.close();
                if (rowcount == 1) {
                    // Check for orphaned address records now that we have updated the role object
                    query = "SELECT * FROM contact c WHERE " + Role.FIELD_CONTACT_ADDRESSID + "=" + addressidold;
                    statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    resultset = statement.executeQuery(query);
                    if (!resultset.first()) {
                        // Delete the old address if it has become an orphan.
                        AddressSQL addresssql = new AddressSQL();
                        if (!addresssql.delete(connection, addressidold)) {
                            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Error while deleting adresss with addressid {1} for roleid {2}", new Object[]{addressid, roleid});
                            return false;
                        }
                    }
                    return true;
                } else {
                    if (rowcount == 0) {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "Role update for contacttype failed for roleid {0} (role not found)", roleid);
                        return false;
                    } else {
                        Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "Role update resulted in multiple updates for roleid {0} ({1} rows got updated for {2} {3})", new Object[]{roleid, rowcount});
                        return false;
                    }
                }
            } else {
                return false;
            }
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return false;
        } catch (SQLException sqle) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            return false;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return false;
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private boolean updateContactFlagDefaults(Connection connection, long roleid) {
        if (roleid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "updateContactFlagDefaults() Role id is 0");
            return false;
        }
        boolean updatesucceeded = true;
        if (!updateContactFlagEmail(connection, roleid, true)) {
            updatesucceeded = false;
        }
        if (!updateContactFlagMail(connection, roleid, true)) {
            updatesucceeded = false;
        }
        if (!updateContactFlagPhone(connection, roleid, true)) {
            updatesucceeded = false;
        }
        if (!updateContactFlagSMS(connection, roleid, true)) {
            updatesucceeded = false;
        }
        if (!updateContactFlagAcquisition(connection, roleid, true)) {
            updatesucceeded = false;
        }
        return updatesucceeded;
    }

    private boolean updateContactFlagEmail(Connection connection, long roleid, boolean flag) {
        return updateContactFlag(connection, Role.FIELD_CONTACT_EMAIL_OK, "Email flag", roleid, flag);
    }

    private boolean updateContactFlagMail(Connection connection, long roleid, boolean flag) {
        return updateContactFlag(connection, Role.FIELD_CONTACT_MAIL_OK, "Mail flag", roleid, flag);
    }

    private boolean updateContactFlagPhone(Connection connection, long roleid, boolean flag) {
        return updateContactFlag(connection, Role.FIELD_CONTACT_PHONE_OK, "Phone flag", roleid, flag);
    }

    private boolean updateContactFlagSMS(Connection connection, long roleid, boolean flag) {
        return updateContactFlag(connection, Role.FIELD_CONTACT_SMS_OK, "SMS flag", roleid, flag);
    }

    private boolean updateContactFlagAcquisition(Connection connection, long roleid, boolean flag) {
        return updateContactFlag(connection, Role.FIELD_CONTACT_NO_AC, "Acquisition flag", roleid, flag);
    }

    private boolean updateContactFlag(Connection connection, String field, String description, long roleid, boolean flag) {
        if (field == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "updateContactFlag() Field is null");
            return false;
        }
        if (description == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "updateContactFlag() Description is null");
            return false;
        }
        if (roleid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "updateContactFlag() Role id is 0");
            return false;
        }
        Statement statement = null;
        ResultSet resultset = null;
        String sql = "";
        String value = "";
        try {
            if (flag) {
                value = "Y";
            } else {
                value = "N";
            }
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "UPDATE contact SET " + field + "='" + value + "' WHERE " + Role.FIELD_CONTACT_ROLEID + "=" + roleid;
            int rowcount = statement.executeUpdate(sql);
            if (rowcount == 1) {
                return true;
            } else {
                if (rowcount == 0) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "Role update for {0} failed for roleid {1} (role not found)", new Object[]{description, roleid});
                    return false;
                } else {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "Role update resulted in multiple updates for roleid {0} ({1} rows got updated for {2} {3})", new Object[]{roleid, rowcount, description, flag});
                    return false;
                }
            }
        } catch (SQLException sqle) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            return false;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return false;
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private boolean updateContacttype(Connection connection, long roleid, String contacttype) {
        String sql = "";
        Statement statement = null;
        ResultSet resultset = null;
        if (roleid == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "updateContacttype() Role id is 0");
            return false;
        }
        if (contacttype == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "updateContacttype() Contacttype is null");
            return false;
        }
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            contacttype = contacttype.replace(";", "").replace("%", "").replace("&", "");
            sql = "update contact set " + Role.FIELD_CONTACT_RECTYPE + "='" + contacttype + "' where " + Role.FIELD_CONTACT_ROLEID + "=" + roleid;
            int rowcount = statement.executeUpdate(sql);
            if (rowcount == 1) {
                return true;
            } else {
                if (rowcount == 0) {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "Role update for contacttype failed for roleid {0} (role not found)", roleid);
                    return false;
                } else {
                    Logger.getLogger(RoleSQL.class.getName()).log(Level.WARNING, "Role update resulted in multiple updates for roleid {0} ({1} rows got updated for contacttype {2})", new Object[]{roleid, rowcount, contacttype});
                    return false;
                }
            }
        } catch (SQLException sqle) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            return false;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return false;
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     * ---------------------------------------- Misc methods
     * ----------------------------------------
     */
    private ResultSet setDefaultFieldValues(ResultSet resultset) {
        if (resultset == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "setDefaultFieldValues() Resultset is null");
        }
        try {
            resultset.updateString(Role.FIELD_CONTACT_RECTYPE, "PERSOON");
            resultset.updateString(Role.FIELD_CONTACT_ADDTYPEKEY, "HUIS");
            Calendar startdate = Calendar.getInstance();

            if (USETIMESTAMP) {
                Timestamp starttime = new Timestamp(startdate.getTimeInMillis());
                resultset.updateTimestamp(Role.FIELD_CONTACT_STARTDATE, starttime);
            } else {
                java.sql.Date starttime = new java.sql.Date(startdate.getTime().getTime());
                resultset.updateDate(Role.FIELD_CONTACT_STARTDATE, starttime);
            }

            resultset.updateString(Role.FIELD_CONTACT_EMAIL_OK, "Y");
            resultset.updateString(Role.FIELD_CONTACT_MAIL_OK, "Y");
            resultset.updateString(Role.FIELD_CONTACT_PHONE_OK, "Y");
            resultset.updateString(Role.FIELD_CONTACT_SMS_OK, "Y");
            resultset.updateString(Role.FIELD_CONTACT_NO_AC, "N");
            // Not OK to contact fields
            resultset.updateString(Role.FIELD_CONTACT_RECP_OK, "N");

            // What the hell are these fields for!
            resultset.updateFloat(Role.FIELD_CONTACT_RNUMB_1, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_RNUMB_2, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_RNUMB_3, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_RNUMB_4, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_RNUMB_5, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_RNUMB_6, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_RNUMB_7, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_RNUMB_8, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_RNUMB_9, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_RNUMB_10, 0);
            // What the hell are these fields for!
            resultset.updateFloat(Role.FIELD_CONTACT_UNUMB_1, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_UNUMB_2, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_UNUMB_3, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_UNUMB_4, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_UNUMB_5, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_UNUMB_6, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_UNUMB_7, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_UNUMB_8, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_UNUMB_9, 0);
            resultset.updateFloat(Role.FIELD_CONTACT_UNUMB_10, 0);
            // What the hell are these fields for!
            resultset.updateBoolean(Role.FIELD_CONTACT_RLOGIC_1, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_RLOGIC_2, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_RLOGIC_3, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_RLOGIC_4, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_RLOGIC_5, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_RLOGIC_6, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_RLOGIC_7, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_RLOGIC_8, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_RLOGIC_9, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_RLOGIC_10, false);
            // What the hell are these fields for!
            resultset.updateBoolean(Role.FIELD_CONTACT_ULOGIC_1, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_ULOGIC_2, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_ULOGIC_3, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_ULOGIC_4, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_ULOGIC_5, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_ULOGIC_6, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_ULOGIC_7, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_ULOGIC_8, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_ULOGIC_9, false);
            resultset.updateBoolean(Role.FIELD_CONTACT_ULOGIC_10, false);

            Calendar nulldate = Calendar.getInstance();
            nulldate.set(1900, Calendar.JANUARY, 1);

            if (USETIMESTAMP) {
                Timestamp nulltime = new Timestamp(nulldate.getTimeInMillis());
                // What the hell are these fields for!
                resultset.updateTimestamp(Role.FIELD_CONTACT_RDATE_1, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_RDATE_2, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_RDATE_3, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_RDATE_4, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_RDATE_5, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_RDATE_6, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_RDATE_7, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_RDATE_8, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_RDATE_9, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_RDATE_10, nulltime);
                // What the hell are these fields for!
                resultset.updateTimestamp(Role.FIELD_CONTACT_UDATE_1, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_UDATE_2, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_UDATE_3, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_UDATE_4, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_UDATE_5, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_UDATE_6, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_UDATE_7, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_UDATE_8, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_UDATE_9, nulltime);
                resultset.updateTimestamp(Role.FIELD_CONTACT_UDATE_10, nulltime);
            } else {
                java.sql.Date nulltime = new java.sql.Date(nulldate.getTime().getTime());
                // What the hell are these fields for!
                resultset.updateDate(Role.FIELD_CONTACT_RDATE_1, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_RDATE_2, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_RDATE_3, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_RDATE_4, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_RDATE_5, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_RDATE_6, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_RDATE_7, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_RDATE_8, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_RDATE_9, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_RDATE_10, nulltime);
                // What the hell are these fields for!
                resultset.updateDate(Role.FIELD_CONTACT_UDATE_1, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_UDATE_2, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_UDATE_3, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_UDATE_4, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_UDATE_5, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_UDATE_6, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_UDATE_7, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_UDATE_8, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_UDATE_9, nulltime);
                resultset.updateDate(Role.FIELD_CONTACT_UDATE_10, nulltime);
            }
            // What the hell are these fields for!
            resultset.updateString(Role.FIELD_CONTACT_RALPHA_1, "");
            resultset.updateString(Role.FIELD_CONTACT_RALPHA_2, "");
            resultset.updateString(Role.FIELD_CONTACT_RALPHA_3, "");
            resultset.updateString(Role.FIELD_CONTACT_RALPHA_4, "");
            resultset.updateString(Role.FIELD_CONTACT_RALPHA_5, "");
            resultset.updateString(Role.FIELD_CONTACT_RALPHA_6, "");
            resultset.updateString(Role.FIELD_CONTACT_RALPHA_7, "");
            resultset.updateString(Role.FIELD_CONTACT_RALPHA_8, "");
            resultset.updateString(Role.FIELD_CONTACT_RALPHA_9, "");
            resultset.updateString(Role.FIELD_CONTACT_RALPHA_10, "");
            // What the hell are these fields for!
            resultset.updateString(Role.FIELD_CONTACT_UALPHA_1, "");
            resultset.updateString(Role.FIELD_CONTACT_UALPHA_2, "");
            resultset.updateString(Role.FIELD_CONTACT_UALPHA_3, "");
            resultset.updateString(Role.FIELD_CONTACT_UALPHA_4, "");
            resultset.updateString(Role.FIELD_CONTACT_UALPHA_5, "");
            resultset.updateString(Role.FIELD_CONTACT_UALPHA_6, "");
            resultset.updateString(Role.FIELD_CONTACT_UALPHA_7, "");
            resultset.updateString(Role.FIELD_CONTACT_UALPHA_8, "");
            resultset.updateString(Role.FIELD_CONTACT_UALPHA_9, "");
            resultset.updateString(Role.FIELD_CONTACT_UALPHA_10, "");

            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            return resultset;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return resultset;
        }
    }

    /**
     *
     * @param connection
     * @param role
     * @return
     */
    public List<String> getDetailsPlaintext(Connection connection, Role role) {
        List<String> msglist = new ArrayList();
        if (role == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "getDetailsPlaintext() Role is null");
            return msglist;
        }
        if (role.getRoleid() == 0) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "getDetailsPlaintext() Role id is 0");
            return msglist;
        }
        try {
            msglist.add("Lidnummer: " + String.valueOf(role.getRoleid()));
            if (role.getPerson() != null) {
                msglist.add("Naam: " + role.getPerson().getFormattedName());
                msglist.add("Geslacht: " + role.getPerson().getFormattedGender());
                msglist.add("Geboortedatum: " + role.getPerson().getFormattedBirth());
            }
            if (role.getAddress() != null) {
                msglist.add("Adres: " + role.getAddress().getFormattedStreetHouseno());
                msglist.add("Woonplaats: " + role.getAddress().getFormattedPostalcode() + " " + role.getAddress().getFormattedCity());
            }
            if (role.getPhonelist() != null) {
                for (Phone phone : role.getPhonelist()) {
                    msglist.add("Telefoon: " + phone.getFormattedNumber());
                }
            }
            if (role.getUrllist() != null) {
                for (URL url : role.getUrllist()) {
                    msglist.add("E-mail: " + url.getInternetAddress());
                }
            }
            if (role.getBankaccountlist() != null) {
                for (Bankaccount bankaccount : role.getBankaccountlist()) {
                    msglist.add("Rekeningnummer: " + bankaccount.getFormattedNumber());
                }
            }
            CommitmentSQL commitmentsql = new CommitmentSQL();
            List<Commitment> commitmentlist = commitmentsql.readViaRoleid(connection, role.getRoleid());
            if (commitmentlist != null) {
                for (Commitment commitment : commitmentlist) {
                    String amount = commitment.getFormattedAmount();
                    String frequency = commitment.getFormattedFrequency();
                    msglist.add("Incasso: bedrag " + amount + " " + frequency);
                }
            }
            InvolvementSQL involvementsql = new InvolvementSQL();
            List<Involvement> involvementlist = involvementsql.readViaRoleid(connection, role.getRoleid());
            if (involvementlist != null) {
                for (Involvement involvement : involvementlist) {
                    String name = involvement.getName();
                    String description = involvement.getDescription();
                    msglist.add("Betrokkenheid: naam: " + name + " omschrijving: " + description);
                }
            }
            SubscriptionSQL subscriptionsql = new SubscriptionSQL();
            List<Subscription> subscriptionlist = subscriptionsql.readViaRoleid(connection, role.getRoleid());
            if (subscriptionlist != null) {
                for (Subscription subscription : subscriptionlist) {
                    long productid = subscription.getProductid();
                    ProductSQL productsql = new ProductSQL();
                    Product product = productsql.read(connection, productid);
                    if (product != null) {
                        String name = product.getName();
                        String description = product.getDescription();
                        msglist.add("Abonnement: naam: " + name + " omschrijving: " + description);
                    }
                }
            }
            return msglist;
        } catch (CRMException crme) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, crme.getMessage(), crme);
            return msglist;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return msglist;
        }
    }

    public String getDetailsHTML(Role role) {
        String msg = "";
        if (role == null) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, "Role is null");
            return msg;
        }
        try {
            // TODO: Implement this method
            msg = "<div> Not yet implemented </div>";
            return msg;
        } catch (Exception e) {
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return "";
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
            Logger.getLogger(RoleSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
        }
    }
}
