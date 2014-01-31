/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.amnesty.crm.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.db.KeyGenerator;
import nl.amnesty.crm.entity.Address;
import nl.amnesty.crm.entity.Role;
import nl.amnesty.crm.exception.CRMAddressException;
import nl.amnesty.crm.util.StringUtil;

/**
 *
 * @author ed
 */
public class AddressSQL {

    /*
     * ----------------------------------------
     * CREATE Methods
     * ----------------------------------------
     */
    public Address create(Connection connection, Address address) throws CRMAddressException {
        long addressid = 0;
        Address addressmatched = null;
        Statement statement = null;
        ResultSet resultset = null;
        if (address == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address is null");
            return null;
        }
        try {
            // try to match address
            addressmatched = match(connection, address);
            if (addressmatched != null) {
                if (addressmatched.isAcceptableMatch()) {
                    return addressmatched;
                }
            }
            // At this point no matching address is found in CRM, so let's create a new one
            String SQL = "select * from address";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();
            resultset = setResultsetColumns(resultset, address);
            if (resultset != null) {
                if (address.getAddressid() == 0) {
                    KeyGenerator keygenerator = new KeyGenerator(connection);
                    addressid = keygenerator.getNextKey(KeyGenerator.KEY_NEXTADDKEY);
                    // Set the address id in the address object
                    address.setAddressid(addressid);
                }
                resultset.updateInt(Address.FIELD_ADDRESS_ID, (int) addressid);
                resultset.insertRow();
            }
            address.setStatus(Address.STATUS_NEW);
            return address;
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0} {1} {2} {3}", new Object[]{address.getAddressid(), address.getFormattedStreetHouseno(), address.getFormattedPostalcode(), address.getFormattedCity()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMAddressException(MessageFormat.format("Address exception {0} {1} {2} {3}", new Object[]{address.getAddressid(), address.getFormattedStreetHouseno(), address.getFormattedPostalcode(), address.getFormattedCity()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public Address createPrevious(Connection connection, Address address, long roleid) throws CRMAddressException {
        Statement statement = null;
        ResultSet resultset = null;
        //long addressid = 0;
        if (address == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address is null");
            return null;
        }
        try {
            String SQL = "select * from addrprev";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();

            resultset = setResultsetColumns(resultset, address);

            if (resultset != null) {
                resultset.updateInt(Role.FIELD_CONTACT_ADDRESSID, (int) address.getAddressid());
                resultset.updateInt(Role.FIELD_CONTACT_ROLEID, (int) roleid);
                resultset.insertRow();
            }
            address.setStatus(Address.STATUS_OLD);
            return address;
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0} {1} {2} {3}", new Object[]{address.getAddressid(), address.getFormattedStreetHouseno(), address.getFormattedPostalcode(), address.getFormattedCity()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMAddressException(MessageFormat.format("Address exception {0} {1} {2} {3}", new Object[]{address.getAddressid(), address.getFormattedStreetHouseno(), address.getFormattedPostalcode(), address.getFormattedCity()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     * ----------------------------------------
     * READ Methods
     * ----------------------------------------
     */
    public Address read(Connection connection, long addressid) throws CRMAddressException {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        if (addressid == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address id is 0");
            return null;
        }
        try {
            query = "select * from address where addresskey = " + addressid;

            //DEBUG
            //Logger.getLogger(AddressSQL.class.getName()).log(Level.WARNING, "query: read() {0}", query);

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                Address address = setBeanProperties(resultset);
                return address;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0}", new Object[]{addressid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMAddressException(MessageFormat.format("Address exception {0}", new Object[]{addressid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private Address readViaStreetHousenoCity(Connection connection, String street, int houseno, String housenosuffix, String city) throws CRMAddressException {
        Address address = null;
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        if (housenosuffix == null) {
            housenosuffix = "";
        }
        if (street == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address street is null");
            return null;
        }
        if (street.isEmpty()) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address street is empty");
            return null;
        }
        if (houseno == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address houseno is 0");
            return null;
        }
        if (city == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address city is null");
            return null;
        }
        if (city.isEmpty()) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address city is empty");
            return null;
        }
        try {
            street = StringUtil.removeSpaces(street).toUpperCase();
            street = street.replaceAll("'", "").replace(";", "").replace("%", "").replace("&", "");
            city = city.toUpperCase();
            city = city.replaceAll("'", "").replace(";", "").replace("%", "").replace("&", "");
            query = "select * from address where shadd1 = " + "'".concat(street).concat("'") + " and houseno = " + houseno + " and shadd4 = " + "'".concat(city).concat("'");

            //DEBUG
            //Logger.getLogger(AddressSQL.class.getName()).log(Level.WARNING, "query: readViaStreetHousenoCity() {0}", query);

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                if (!housenosuffix.isEmpty()) {
                    do {
                        String value = resultset.getString(Address.FIELD_ADDRESS_HOUSENOSUFFIX);
                        if (value != null) {
                            if (value.equals(housenosuffix)) {
                                address = setBeanProperties(resultset);
                            }
                        }
                    } while (resultset.next());
                } else {
                    address = setBeanProperties(resultset);
                }
            }
            resultset.close();
            statement.close();
            return address;
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0} {1} {2} {3}", new Object[]{address.getAddressid(), address.getFormattedStreetHouseno(), address.getFormattedPostalcode(), address.getFormattedCity()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0} {1} {2} {3}", new Object[]{address.getAddressid(), address.getFormattedStreetHouseno(), address.getFormattedPostalcode(), address.getFormattedCity()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private Address readViaPostalcodeHouseno(Connection connection, int postalcodenumeric, String postalcodealpha, int houseno, String housenosuffix) throws CRMAddressException {
        Address address = null;
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        if (housenosuffix == null) {
            housenosuffix = "";
        }
        if (postalcodenumeric == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address postalcodenumeric is 0");
            return null;
        }
        if (postalcodealpha == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address postalcodealpha is null");
            return null;
        }
        if (postalcodealpha.isEmpty()) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address postalcodealpha is empty");
            return null;
        }
        if (houseno == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address houseno is 0");
            return null;
        }
        try {
            String postalcodenospace = String.valueOf(postalcodenumeric).concat(postalcodealpha).replace(";", "").replace("%", "").replace("&", "");;
            String postalcodespace = String.valueOf(postalcodenumeric).concat(" ").concat(postalcodealpha).replace(";", "").replace("%", "").replace("&", "");;
            query = "select * from address where (postcode = " + "'".concat(postalcodenospace).concat("'") + " or postcode = " + "'".concat(postalcodespace).concat("'") + ") and houseno = " + houseno;

            //DEBUG
            //Logger.getLogger(AddressSQL.class.getName()).log(Level.WARNING, "query: readViaPostalcodeHouseno() {0}", query);

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                if (!housenosuffix.isEmpty()) {
                    do {
                        String value = resultset.getString(Address.FIELD_ADDRESS_HOUSENOSUFFIX);
                        if (value != null) {
                            if (value.equals(housenosuffix)) {
                                address = setBeanProperties(resultset);
                            }
                        }
                    } while (resultset.next());
                } else {
                    address = setBeanProperties(resultset);
                }
            }
            resultset.close();
            statement.close();
            return address;
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0} {1} {2} {3}", new Object[]{address.getAddressid(), address.getFormattedStreetHouseno(), address.getFormattedPostalcode(), address.getFormattedCity()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0} {1} {2} {3}", new Object[]{address.getAddressid(), address.getFormattedStreetHouseno(), address.getFormattedPostalcode(), address.getFormattedCity()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public String readCompany(Connection connection, long addressid) throws CRMAddressException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        if (addressid == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address id is 0");
            return "";
        }
        try {
            query = "select * from address where addresskey = " + addressid;

            //DEBUG
            //Logger.getLogger(AddressSQL.class.getName()).log(Level.WARNING, "query: read() {0}", query);

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                String company = resultset.getString(Address.FIELD_ADDRESS_COMPANY);
                return company;
            } else {
                return "";
            }
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0}", new Object[]{addressid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMAddressException(MessageFormat.format("Address exception {0}", new Object[]{addressid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     * ----------------------------------------
     * UDPATE Methods
     * ----------------------------------------
     */
    public static void updateDetails(Connection connection, Address addressmatched, Address address) throws CRMAddressException {
        try {
            String oldstreet = "", oldhousenosuffix = "", oldpostcode = "", oldcity = "";
            int oldhouseno = 0;
            boolean oldaddress = false;
            // Debug
            Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, MessageFormat.format("Update address {0} -> {1}", new Object[]{addressmatched.getCity(), address.getCity()}));
            // Update the street of address found in CRM with new street
            if (address.getStreet() != null) {
                if (addressmatched.getStreet() != null) {
                    if (!addressmatched.getStreet().equals(address.getStreet())) {
                        oldaddress = true;
                        oldstreet = addressmatched.getStreet();
                        oldhouseno = addressmatched.getHouseno();
                        oldhousenosuffix = addressmatched.getHousenosuffix();
                        oldpostcode = String.valueOf(addressmatched.getPostalcodenumeric()) + " " + addressmatched.getPostalcodealpha();
                        oldcity = addressmatched.getCity();

                        addressmatched.setStreet(address.getStreet());
                        updateStreet(connection, addressmatched);
                    }
                }
            }
            // Update the housenumber of address found in CRM with new housenumber
            if (address.getHouseno() != 0) {
                if (addressmatched.getHouseno() != address.getHouseno()) {
                    oldaddress = true;
                    oldstreet = addressmatched.getStreet();
                    oldhouseno = addressmatched.getHouseno();
                    oldhousenosuffix = addressmatched.getHousenosuffix();
                    oldpostcode = String.valueOf(addressmatched.getPostalcodenumeric()) + " " + addressmatched.getPostalcodealpha();
                    oldcity = addressmatched.getCity();

                    addressmatched.setHouseno(address.getHouseno());
                    updateHouseno(connection, addressmatched);
                }
            }
            // Update the housenumber suffix of address found in CRM with new suffix
            if (address.getHousenosuffix() != null) {
                if (addressmatched.getHousenosuffix() != null) {
                    if (!addressmatched.getHousenosuffix().equals(address.getHousenosuffix())) {
                        oldaddress = true;
                        oldstreet = addressmatched.getStreet();
                        oldhouseno = addressmatched.getHouseno();
                        oldhousenosuffix = addressmatched.getHousenosuffix();
                        oldpostcode = String.valueOf(addressmatched.getPostalcodenumeric()) + " " + addressmatched.getPostalcodealpha();
                        oldcity = addressmatched.getCity();

                        addressmatched.setHousenosuffix(address.getHousenosuffix());
                        updateHousenosuffix(connection, addressmatched);
                    }
                }
            }
            // Update the postalcode of address found in CRM with new postalcode
            if (address.getPostalcodenumeric() != 0) {
                if (address.getPostalcodealpha() != null) {
                    if (!addressmatched.getPostalcodealpha().equals(address.getPostalcodealpha())
                            || addressmatched.getPostalcodenumeric() != address.getPostalcodenumeric()) {
                        oldaddress = true;
                        oldstreet = addressmatched.getStreet();
                        oldhouseno = addressmatched.getHouseno();
                        oldhousenosuffix = addressmatched.getHousenosuffix();
                        oldpostcode = String.valueOf(addressmatched.getPostalcodenumeric()) + " " + addressmatched.getPostalcodealpha();
                        oldcity = addressmatched.getCity();

                        addressmatched.setPostalcodenumeric(address.getPostalcodenumeric());
                        addressmatched.setPostalcodealpha(address.getPostalcodealpha());
                        updatePostalcode(connection, addressmatched);
                    }
                }
            }
            // Update the city of address found in CRM with new city
            if (address.getCity() != null) {
                if (addressmatched.getCity() != null) {
                    if (!addressmatched.getCity().equals(address.getCity())) {
                        oldaddress = true;
                        oldstreet = addressmatched.getStreet();
                        oldhouseno = addressmatched.getHouseno();
                        oldhousenosuffix = addressmatched.getHousenosuffix();
                        oldpostcode = String.valueOf(addressmatched.getPostalcodenumeric()) + " " + addressmatched.getPostalcodealpha();
                        oldcity = addressmatched.getCity();

                        addressmatched.setCity(address.getCity());
                        updateCity(connection, addressmatched);
                    }
                }
            }
            if (oldaddress) {
                addPreviousAddress(connection, oldstreet, oldhouseno, oldhousenosuffix, oldpostcode, oldcity);
            }
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMAddressException(MessageFormat.format("Address exception #02 for addressid {0} name {1}", new Object[]{address.getAddressid(), address.getFormatedAddress()}), e);
        }
    }

    private static void addPreviousAddress(Connection connection, String oldstreet, int oldhouseno, String oldhousenosuffix, String oldpostcode, String oldcity) throws CRMAddressException {
        Statement statement;
        ResultSet resultset;
        try {
            String SQL = "select * from addrprev";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();
            resultset.updateString("ADDRESS1", oldstreet);
            resultset.updateInt("HOUSENO", oldhouseno);
            resultset.updateString("HOUSENOSUFFIX", oldhousenosuffix);
            resultset.updateString("POSTCODE", oldpostcode);
            resultset.updateString("ADDRESS4", oldcity);
            if (resultset != null) {
                KeyGenerator keygenerator = new KeyGenerator(connection);
                int addressid = keygenerator.getNextKey(KeyGenerator.KEY_NEXTADDKEY);
                resultset.updateInt("ADDRESSKEY", (int) addressid);
                resultset.insertRow();
            }
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMAddressException(MessageFormat.format("Address exception: Could not add previous address ({0},{1},{2})", new Object[]{oldcity, oldpostcode, oldhouseno}) , e);
        }

    }

    private static boolean updateStreet(Connection connection, Address address) throws CRMAddressException {
        String sql;
        Statement statement = null;
        if (address == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address is null");
            return false;
        }
        if (address.getAddressid() == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address id is 0");
            return false;
        }
        if (address.getStreet() == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address street is null");
            return false;
        }
        if (address.getStreet().isEmpty()) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address street is empty");
            return false;
        }
        try {
            String street = address.getStreet().replace(";", "").replace("%", "").replace("&", "");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update address set address1 = " + "'".concat(street).concat("'") + ", "
                    + " shadd1 = isnull(replace(replace(replace(replace(CONVERT(CHAR(40),upper('" + street + "')),' ',''),'-',''),',',''),char(39),''),'')"
                    + " where addresskey = " + address.getAddressid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, MessageFormat.format("Street updated to {0} for addressid {1}", new Object[]{street, address.getAddressid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception #09 for addressid {0} address {1}", new Object[]{address.getAddressid(), address.getFormatedAddress()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMAddressException(MessageFormat.format("Address exception #11 for addressid {0} address {1}", new Object[]{address.getAddressid(), address.getFormatedAddress()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    private static boolean updateCity(Connection connection, Address address) throws CRMAddressException {
        String sql;
        Statement statement = null;
        if (address == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address is null");
            return false;
        }
        if (address.getAddressid() == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address id is 0");
            return false;
        }
        if (address.getCity() == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address city is null");
            return false;
        }
        if (address.getCity().isEmpty()) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address city is empty");
            return false;
        }
        try {
            String city = address.getCity().replace(";", "").replace("%", "").replace("&", "");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update address set address4 = " + "'".concat(city).concat("'") + ", "
                    + " shadd4 = isnull(replace(replace(replace(replace(CONVERT(CHAR(40),upper('" + city + "')),' ',''),'-',''),',',''),char(39),''),'')"
                    + " where addresskey = " + address.getAddressid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, MessageFormat.format("City updated to {0} for addressid {1}", new Object[]{city, address.getAddressid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception #09 for addressid {0} address {1}", new Object[]{address.getAddressid(), address.getFormatedAddress()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMAddressException(MessageFormat.format("Address exception #11 for addressid {0} address {1}", new Object[]{address.getAddressid(), address.getFormatedAddress()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    private static boolean updatePostalcode(Connection connection, Address address) throws CRMAddressException {
        String sql;
        Statement statement = null;
        if (address == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address is null");
            return false;
        }
        if (address.getAddressid() == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address id is 0");
            return false;
        }
        if (address.getPostalcodenumeric() == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address postalcodealpha is 0");
            return false;
        }
        if (address.getPostalcodealpha() == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address postalcodealpha is null");
            return false;
        }
        if (address.getPostalcodealpha().isEmpty()) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address postalcodealpha is empty");
            return false;
        }
        try {
            int postalcodenumeric = address.getPostalcodenumeric();
            String postalcodealpha = address.getPostalcodealpha().replace(";", "").replace("%", "").replace("&", "");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update address set postcode = " + "'".concat(String.valueOf(postalcodenumeric)).concat(" ").concat(postalcodealpha).concat("'") + ", "
                    + " shpcode = isnull(replace(replace(replace(replace(CONVERT(CHAR(40),upper(" + String.valueOf(postalcodenumeric).concat(" ").concat(postalcodealpha) + ")),' ',''),'-',''),',',''),char(39),''),'') "
                    + " where addresskey = " + address.getAddressid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, MessageFormat.format("Postalcodealpha updated to {0} for addressid {1}", new Object[]{postalcodealpha, address.getAddressid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception #09 for addressid {0} address {1}", new Object[]{address.getAddressid(), address.getFormatedAddress()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMAddressException(MessageFormat.format("Address exception #11 for addressid {0} address {1}", new Object[]{address.getAddressid(), address.getFormatedAddress()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    private static boolean updateHousenosuffix(Connection connection, Address address) throws CRMAddressException {
        String sql;
        Statement statement = null;
        if (address == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address is null");
            return false;
        }
        if (address.getAddressid() == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address id is 0");
            return false;
        }
        if (address.getHousenosuffix() == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address housenosuffix is null");
            return false;
        }
        if (address.getHousenosuffix().isEmpty()) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address housenosuffix is empty");
            return false;
        }
        try {
            String housenosuffix = address.getHousenosuffix().replace(";", "").replace("%", "").replace("&", "");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update address set housenosuffix = " + "'".concat(housenosuffix).concat("'") + "where addresskey = " + address.getAddressid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, MessageFormat.format("Housenosuffix updated to {0} for addressid {1}", new Object[]{housenosuffix, address.getAddressid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception #09 for addressid {0} address {1}", new Object[]{address.getAddressid(), address.getFormatedAddress()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMAddressException(MessageFormat.format("Address exception #11 for addressid {0} address {1}", new Object[]{address.getAddressid(), address.getFormatedAddress()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    private static boolean updateHouseno(Connection connection, Address address) throws CRMAddressException {
        String sql;
        Statement statement = null;
        if (address == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address is null");
            return false;
        }
        if (address.getAddressid() == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address id is 0");
            return false;
        }
        if (address.getHouseno() == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address houseno is 0");
            return false;
        }
        try {
            int houseno = address.getHouseno();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update address set houseno = " + String.valueOf(houseno) + " where addresskey = " + address.getAddressid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, MessageFormat.format("Street updated to {0} for addressid {1}", new Object[]{houseno, address.getAddressid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception #09 for addressid {0} address {1}", new Object[]{address.getAddressid(), address.getFormatedAddress()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMAddressException(MessageFormat.format("Address exception #11 for addressid {0} address {1}", new Object[]{address.getAddressid(), address.getFormatedAddress()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    public boolean update(Connection connection, Address address) throws CRMAddressException {
        String sql = "";
        Statement statement = null;
        if (address == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address is null");
        }
        if (address.getStreet() == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address street is null");
        }
        if (address.getStreet().isEmpty()) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address street is empty");
        }
        if (address.getHouseno() == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address houseno is zero");
        }
        if (address.getPostalcodealpha() == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address postalcodealpha is null");
        }
        if (address.getPostalcodealpha().isEmpty()) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address postalcodealpha is empty");
        }
        if (address.getPostalcodenumeric() == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address postalcodenumeric is zero");
        }
        if (address.getCity() == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address city is null");
        }
        if (address.getCity().isEmpty()) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address city is empty");
        }
        try {
            String city = address.getCity();
            int houseno = address.getHouseno();
            String housenosuffix = address.getHousenosuffix().replace(";", "").replace("%", "").replace("&", "");
            String postcode = address.getFormattedPostalcode().replace(";", "").replace("%", "").replace("&", "");
            String street = address.getStreet().replace(";", "").replace("%", "").replace("&", "");
            String isocountry = address.getIsocountry().replace(";", "").replace("%", "").replace("&", "");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update address set address1 = " + "'".concat(street).concat("'") + ", houseno = " + houseno + ", housenosuffix = " + "'".concat(housenosuffix).concat("'") + ", postcode=" + "'".concat(postcode).concat("'") + ", address4 = " + "'".concat(city).concat("'") + ", address6 = " + "'".concat(isocountry).concat("'") + " where " + Address.FIELD_ADDRESS_ID + " addresskey =" + address.getAddressid();
            statement.executeUpdate(sql);
            statement.close();
            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0} {1} {2} {3}", new Object[]{address.getAddressid(), address.getFormattedStreetHouseno(), address.getFormattedPostalcode(), address.getFormattedCity()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMAddressException(MessageFormat.format("Address exception {0} {1} {2} {3}", new Object[]{address.getAddressid(), address.getFormattedStreetHouseno(), address.getFormattedPostalcode(), address.getFormattedCity()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    /*
     * ----------------------------------------
     * DELETE Methods
     * ----------------------------------------
     */
    public boolean delete(Connection connection, long addressid) throws CRMAddressException {
        String sql = "";
        Statement statement = null;
        if (addressid == 0) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address id is 0");
        }
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "delete address where " + Address.FIELD_ADDRESS_ID + "=" + String.valueOf(addressid);
            int rowcount = statement.executeUpdate(sql);
            statement.close();
            if (rowcount == 0) {
                Logger.getLogger(AddressSQL.class.getName()).log(Level.WARNING, "Address removal failed for addressid {0} (address not found)", addressid);
                //throw new CRMAddressException(MessageFormat.format("Address removal failed for addressid {0} (address not found)", new Object[]{addressid}));
                return false;
            } else {
                if (rowcount != 1) {
                    Logger.getLogger(AddressSQL.class.getName()).log(Level.WARNING, "Address removal resulted in multiple deletions for roleid {0}, addressid {1} ({2} rows got deleted)", new Object[]{addressid, rowcount});
                    //throw new CRMAddressException(MessageFormat.format("Address removal resulted in multiple deletions for addressid {0} ({1} rows got deleted)", new Object[]{addressid, rowcount}));
                    return false;
                } else {
                    return true;
                }
            }
        } catch (SQLException sqle) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, sqle);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0}", new Object[]{addressid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMAddressException(MessageFormat.format("Address exception {0}", new Object[]{addressid}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    /*
     * ----------------------------------------
     * Object matching
     * ----------------------------------------
     */
    public Address match(Connection connection, Address address) throws CRMAddressException {
        Address addressfound = null;
        if (address == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address is null");
        }
        try {
            //DEBUG
            //Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, "match(): Begin to match address via addressid");
            // First of all let's see if address can be found in CRM via the address id
            if (address.getAddressid() != 0) {
                addressfound = read(connection, address.getAddressid());
                if (addressfound != null) {
                    //DEBUG
                    //Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, "match(): Found address read() addressid {0}", addressfound.getAddressid());
                    addressfound.setStatus(Address.STATUS_MATCHED_ID);
                    return addressfound;
                }
            }
            //DEBUG
            //Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, "match(): Begin to match address via street, housenumber and city");
            // Next best thing is to search for the street, housenumber and city
            if (address.getStreet() != null && address.getCity() != null) {
                if (!address.getStreet().isEmpty() && address.getHouseno() != 0 && !address.getCity().isEmpty()) {
                    addressfound = readViaStreetHousenoCity(connection, address.getStreet(), address.getHouseno(), address.getHousenosuffix(), address.getCity());
                    if (addressfound != null) {
                        //DEBUG
                        //Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, "match(): Found address readViaStreetHousenoCity() addressid {0}", addressfound.getAddressid());
                        addressfound.setStatus(Address.STATUS_MATCHED_STREET_HOUSENUMBER_CITY);
                        return addressfound;
                    }
                }
            }
            //DEBUG
            //Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, "match(): Begin to match address via postalcode and housenumber");
            // Search the postalcode and housenumber
            if (address.getPostalcodealpha() != null) {
                if (address.getPostalcodenumeric() != 0 && !address.getPostalcodealpha().isEmpty() && address.getHouseno() != 0) {
                    addressfound = readViaPostalcodeHouseno(connection, address.getPostalcodenumeric(), address.getPostalcodealpha(), address.getHouseno(), address.getHousenosuffix());
                    if (addressfound != null) {
                        //DEBUG
                        //Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, "match(): Found address readViaPostalcodeHouseno() addressid {0}", addressfound.getAddressid());
                        addressfound.setStatus(Address.STATUS_MATCHED_POSTALCODE_HOUSENUMBER);
                        return addressfound;
                    }
                }
            }

            //DEBUG
            //Logger.getLogger(AddressSQL.class.getName()).log(Level.INFO, "match(): No matching address found");

            //address = new Address();
            address.setStatus(Address.STATUS_MATCHED_NONE);
            return address;
        } catch (Exception e) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, e);
            throw new CRMAddressException(MessageFormat.format("Address SQL exception {0} {1} {2} {3}", new Object[]{address.getAddressid(), address.getFormattedStreetHouseno(), address.getFormattedPostalcode(), address.getFormattedCity()}), e);
        }
    }

    /*
     * ----------------------------------------
     * Misc Methods
     * ----------------------------------------
     */
    private Address setBeanProperties(ResultSet resultset) {
        Address address = new Address();
        if (resultset == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Resultset is null");
            return address;
        }
        try {
            address.setAddressid(resultset.getLong(Address.FIELD_ADDRESS_ID));
            String city = resultset.getString(Address.FIELD_ADDRESS_CITY);
            address.parseCity(city);
            address.setCounty(resultset.getString(Address.FIELD_ADDRESS_COUNTRY));
            String housenovalue = resultset.getString(Address.FIELD_ADDRESS_HOUSENO);
            address.parseHouseno(housenovalue);
            address.setHousenosuffix(resultset.getString(Address.FIELD_ADDRESS_HOUSENOSUFFIX));
            address.setIsocountry(Address.ISO_COUNTRYCODE_NETHERLANDS);
            String postalcode = resultset.getString(Address.FIELD_ADDRESS_POSTCODE);
            address.parsePostalcode(postalcode);
            address.setProvince("");
            address.setState("");
            String street = resultset.getString(Address.FIELD_ADDRESS_STREET);
            address.parseStreet(street);
            return address;
        } catch (SQLException ex) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, ex);
            return address;
        }
    }

    private ResultSet setResultsetColumns(ResultSet resultset, Address address) {
        if (resultset == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Resultset is null");
            return null;
        }
        if (address == null) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, "Address is null");
            return resultset;
        }
        try {
            resultset.updateString(Address.FIELD_ADDRESS_STREET, address.getStreet());
            resultset.updateInt(Address.FIELD_ADDRESS_HOUSENO, address.getHouseno());
            resultset.updateString(Address.FIELD_ADDRESS_HOUSENOSUFFIX, address.getHousenosuffix());
            resultset.updateString(Address.FIELD_ADDRESS_POSTCODE, String.valueOf(address.getPostalcodenumeric()).concat(Address.POSTALCODE_DELIMITER).concat(address.getPostalcodealpha()));
            resultset.updateString(Address.FIELD_ADDRESS_CITY, address.getCity());
            resultset.updateString(Address.FIELD_ADDRESS_COUNTRY, address.getIsocountry());
            // Zoekvelden ook gelijk vullen
            if (address.getStreet()!=null) {
                resultset.updateString("shadd1", address.getStreet().toUpperCase().replace(" ", "").replace("-","").replace(",","").replace("'", ""));
            }
            if (address.getCity()!=null) {
                resultset.updateString("shadd4", address.getCity().toUpperCase().replace(" ", "").replace("-","").replace(",","").replace("'", ""));
            }
            if (address.getPostalcodealpha()!=null) {
                String postcode=address.getPostalcodenumeric()+address.getPostalcodealpha();
                resultset.updateString("shpcode", postcode.toUpperCase().replace(" ", "").replace("-","").replace(",","").replace("'", ""));
            }
            return resultset;
        } catch (SQLException ex) {
            Logger.getLogger(AddressSQL.class.getName()).log(Level.SEVERE, null, ex);
            return resultset;
        }

    }

    /**
     *
     * @param statement SQL statement
     */
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
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, null, sqle);
        }
    }
}
