package nl.amnesty.crm.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.db.KeyGenerator;
import nl.amnesty.crm.entity.Person;
import nl.amnesty.crm.exception.CRMPersonException;
import nl.amnesty.crm.util.DateUtil;
import nl.amnesty.crm.util.StringUtil;

/**
 *
 * @author ed
 */
public class PersonSQL {
    /*
     * Standard CRUD methods
     */

    public Person create(Connection connection, Person person) throws CRMPersonException {
        long personid = 0;
        Person personmatched;

        Statement statement = null;
        ResultSet resultset = null;
        String gender = person.getGender();
        Date birth = person.getBirth();
        if (person == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person is null");
        }
        try {
            // Try to match person
            personmatched = match(connection, person);
            if (personmatched != null) {
                if (personmatched.isAcceptableMatch()) {
                    //updateDetails(connection, personmatched, person);
                    return personmatched;
                }
            }

            // At this point no matching person is found in CRM, so let's create a new one
            String SQL = "SELECT * FROM people";
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            resultset = statement.executeQuery(SQL);
            resultset.moveToInsertRow();

            if (gender.length() != 1 || (!gender.toLowerCase().equals("m") && !gender.toLowerCase().matches("f") && !gender.toLowerCase().matches("u"))) {
                throw new CRMPersonException(MessageFormat.format("Invalid gender code {0}", new Object[]{gender}));
            } else {
                if (birth != null) {
                    Calendar calendar = Calendar.getInstance();
                    int thisyear = calendar.get(Calendar.YEAR);
                    calendar.setTime(birth);
                    // Check for possible date of birth if it is not the default date of 01-01-1900
                    if (!(calendar.get(Calendar.YEAR) == 1900 && calendar.get(Calendar.MONTH) == Calendar.JANUARY && calendar.get(Calendar.DAY_OF_MONTH) == 1)) {
                        if (calendar.get(Calendar.YEAR) < (thisyear - 100)) {
                            Logger.getLogger(PersonSQL.class.getName()).log(Level.WARNING, MessageFormat.format("Invalid date {0} (person probably not over hundred)", calendar.getTime().toString()));
                            // Reset date of birth to default of 1900-01-01
                            calendar.set(Calendar.YEAR, 1900);
                            calendar.set(Calendar.MONTH, Calendar.JANUARY);
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            calendar.set(Calendar.HOUR, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            birth = calendar.getTime();
                            person.setBirth(birth);
                            //throw new CRMPersonException(MessageFormat.format("Invalid date {0}", new Object[]{calendar.getTime().toString()}));
                        }
                        if (calendar.get(Calendar.YEAR) > thisyear) {
                            Logger.getLogger(PersonSQL.class.getName()).log(Level.WARNING, MessageFormat.format("Invalid date {0} (date of birth lies in the future)", calendar.getTime().toString()));
                            // Reset date of birth to default of 1900-01-01
                            calendar.set(Calendar.YEAR, 1900);
                            calendar.set(Calendar.MONTH, Calendar.JANUARY);
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            calendar.set(Calendar.HOUR, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            birth = calendar.getTime();
                            person.setBirth(birth);
                            //throw new CRMPersonException(MessageFormat.format("Invalid date {0}", new Object[]{calendar.getTime().toString()}));
                        }
                    }
                }

                // Match title to gender
                if (gender.toLowerCase().equals("m")) {
                    person.setTitle("Meneer");
                }
                if (gender.toLowerCase().equals("f")) {
                    person.setTitle("Mevrouw");
                }

                // Add new person
                resultset = setResultsetColumns(resultset, person);
                if (resultset != null) {
                    if (person.getPersonid() == 0) {
                        KeyGenerator keygenerator = new KeyGenerator(connection);
                        personid = keygenerator.getNextKey(KeyGenerator.KEY_NEXTPEOPKEY);
                        // Set the address id in the address object
                        person.setPersonid(personid);
                    }
                    resultset.updateInt(Person.FIELD_PEOPLE_ID, (int) personid);
                    resultset.insertRow();
                    resultset.close();
                }
            }
            statement.close();
            person.setStatus(Person.STATUS_NEW);
            return person;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #01 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #01 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public static void updateDetails(Connection connection, Person personmatched, Person person) throws CRMPersonException {
        Calendar birthdate = Calendar.getInstance();
        try {
            // Update gender of person found in CRM with new gender.
            if (person.getGender() != null) {
                if (personmatched.getGender() == null) {
                    personmatched.setGender(person.getGender());
                    updateGender(connection, personmatched);
                    if (personmatched.getGender().toLowerCase().equals("m")) {
                        personmatched.setTitle("Meneer");
                        updateTitle(connection, personmatched);
                    }
                    if (personmatched.getGender().toLowerCase().equals("f")) {
                        personmatched.setTitle("Mevrouw");
                        updateTitle(connection, personmatched);
                    }
                } else {
                    // Update person gender if matched person's gender differs from new person, but not if new value would be "u"
                    if (!personmatched.getGender().equals(person.getGender()) && !person.getGender().toLowerCase().equals("u")) {
                        personmatched.setGender(person.getGender());
                        updateGender(connection, personmatched);
                        if (personmatched.getGender().toLowerCase().equals("m")) {
                            personmatched.setTitle("Meneer");
                            updateTitle(connection, personmatched);
                        }
                        if (personmatched.getGender().toLowerCase().equals("f")) {
                            personmatched.setTitle("Mevrouw");
                            updateTitle(connection, personmatched);
                        }
                    }
                }
            }
            // Update date of birth of person found in CRM with new date of birth.
            if (person.getBirth() != null) {
                if (personmatched.getBirth() == null) {
                    personmatched.setBirth(person.getBirth());
                    updateBirth(connection, personmatched);
                } else {
                    if (!personmatched.getBirth().equals(person.getBirth())) {
                        // Update date of birth of matched person with new value, but not if new value would be '1900-01-01'
                        birthdate.setTime(person.getBirth());
                        if (!(birthdate.get(Calendar.YEAR) == 1900 && birthdate.get(Calendar.MONTH) == Calendar.JANUARY && birthdate.get(Calendar.DAY_OF_MONTH) == 1)) {
                            personmatched.setBirth(person.getBirth());
                            updateBirth(connection, personmatched);
                        }
                    }
                }
            }
            // Update initials of person found in CRM with new forenames.
            if (person.getInitials() != null) {
                if (personmatched.getInitials() == null) {
                    personmatched.setInitials(person.getInitials());
                    updateInitials(connection, personmatched);
                } else {
                    // Update initials of matched person with new value, but not if it would be empty
                    if (!personmatched.getInitials().equals(person.getInitials()) && !person.getInitials().isEmpty()) {
                        personmatched.setInitials(person.getInitials());
                        updateInitials(connection, personmatched);
                    }
                }
            }
            // Update forenames of person found in CRM with new forenames.
            if (person.getForenames() != null) {
                if (personmatched.getForenames() == null) {
                    personmatched.setForenames(person.getForenames());
                    updateForenames(connection, personmatched);
                } else {
                    // Update forenames of matched person with new value, but not if it would be empty
                    if (!personmatched.getForenames().equals(person.getForenames()) && !person.getForenames().isEmpty()) {
                        personmatched.setForenames(person.getForenames());
                        updateForenames(connection, personmatched);
                    }
                }
            }
            // Update surname of person found in CRM with new forenames.
            // Update middle at the same time of updating a surname
            if (person.getSurname() != null) {
                if (personmatched.getSurname() == null) {
                    personmatched.setSurname(person.getSurname());
                    personmatched.setMiddle(person.getMiddle());
                    updateSurname(connection, personmatched);
                    updateMiddle(connection, personmatched);

                } else {
                    // Update surname of matched person with new value, but not if it would be empty
                    if (!personmatched.getSurname().equals(person.getSurname()) && !person.getSurname().isEmpty()) {
                        personmatched.setSurname(person.getSurname());
                        personmatched.setMiddle(person.getMiddle());
                        updateSurname(connection, personmatched);
                        updateMiddle(connection, personmatched);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #02 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), e);
        }

    }

    public Person read(Connection connection, long personid) throws CRMPersonException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        if (personid == 0) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person id is 0");
        }
        try {
            query = "SELECT * FROM people p WHERE p.peoplekey=" + personid;
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            if (resultset.first()) {
                Person person = setBeanProperties(resultset);
                return person;
            } else {
                return null;
            }
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #02 for personid {0}", new Object[]{personid}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #03 for personid {0}", new Object[]{personid}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    public boolean update(Connection connection, Person person) {
        // TODO: Implementation for updating person, this is just a stub
        return false;
    }

    public boolean delete(Connection connection, long personid) {
        // TODO: Implementation for deleting person, this is just a stub
        return false;
    }

    /*
     *
     * Object matching
     *
     */
    public Person match(Connection connection, Person person) throws CRMPersonException {
        Person personfound;
        List<Person> personlistfound;
        String initials = "";
        if (person == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person is null");
        }
        try {
            //DEBUG
            //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Begin to match person object");
            // First of all let's see if address can be found in CRM via the person id
            if (person.getPersonid() != 0) {
                personfound = read(connection, person.getPersonid());
                if (personfound != null) {
                    //DEBUG
                    //Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Found person read() personid {0}", personfound.getPersonid());
                    personfound.setStatus(Person.STATUS_MATCHED_ID);
                    return personfound;
                }
            }
            // Next best thing is to search for a person when the date of birth and gender is known
            if (person.getSurname() != null && person.getGender() != null) {
                if (!person.getSurname().isEmpty() && person.getBirth() != null && !person.getGender().isEmpty()) {
                    personlistfound = readViaMiddleSurnameBirthGender(connection, person.getMiddle(), person.getSurname(), person.getBirth(), person.getGender());
                    if (personlistfound != null) {
                        //DEBUG
                        //for (Person persondebug : personlistfound) {
                        //    Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Found person readViaMiddleSurnameBirthGender() personid {0}", persondebug.getPersonid());
                        //}
                        if (personlistfound.size() == 1) {
                            personfound = personlistfound.get(0);
                            personfound.setStatus(Person.STATUS_MATCHED_NAME_DATEOFBIRTH_GENDER);
                            return personfound;
                        }
                    }
                }
            }
            // Try to find person based on name and date of birth
            if (person.getSurname() != null) {
                if (!person.getSurname().isEmpty() && person.getBirth() != null) {
                    personlistfound = readViaMiddleSurnameBirth(connection, person.getMiddle(), person.getSurname(), person.getBirth());
                    if (personlistfound != null) {
                        //DEBUG
                        //for (Person persondebug : personlistfound) {
                        //    Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Found person readViaMiddleSurnameBirth() personid {0}", persondebug.getPersonid());
                        //}
                        if (personlistfound.size() == 1) {
                            personfound = personlistfound.get(0);
                            personfound.setStatus(Person.STATUS_MATCHED_NAME_DATEOFBIRTH);
                            return personfound;
                        }
                    }
                }
            }
            // Try to find person based on name and gender
            if (person.getSurname() != null && person.getGender() != null) {
                if (!person.getSurname().isEmpty() && !person.getGender().isEmpty()) {
                    personlistfound = readViaMiddleSurnameGender(connection, person.getMiddle(), person.getSurname(), person.getGender());
                    if (personlistfound != null) {
                        //DEBUG
                        //for (Person persondebug : personlistfound) {
                        //    Logger.getLogger(RoleSQL.class.getName()).log(Level.INFO, "match(): Found person readViaMiddleSurnameGender() personid {0}", persondebug.getPersonid());
                        //}
                        if (personlistfound.size() == 1) {
                            personfound = personlistfound.get(0);
                            personfound.setStatus(Person.STATUS_MATCHED_NAME_GENDER);
                            return personfound;
                        }
                    }
                }
            }
            // Now try to find someone based on the name only
            if (person.getInitials() != null && person.getSurname() != null) {
                if (!person.getInitials().isEmpty() && !person.getSurname().isEmpty()) {
                    // Start with only one initial and narrow down until we only find one person
                    for (int i = 0; i < person.getInitials().length(); i++) {
                        initials = person.getInitials().substring(0, i + 1);
                        initials = formatInitials(initials);
                        // The personfound method will return a non-null object only if exactly one match is found
                        personlistfound = readViaInitialsForenamesMiddleSurname(connection, initials, person.getForenames(), person.getMiddle(), person.getSurname());
                        if (personlistfound != null) {
                            if (personlistfound.size() == 1) {
                                personfound = personlistfound.get(0);
                                personfound.setStatus(Person.STATUS_MATCHED_NAME);
                                return personfound;
                            }
                        }
                    }
                }
            }
            if (person.getForenames() != null && person.getSurname() != null) {
                if (!person.getForenames().isEmpty() && !person.getSurname().isEmpty()) {
                    personlistfound = readViaInitialsForenamesMiddleSurname(connection, initials, person.getForenames(), person.getMiddle(), person.getSurname());
                    if (personlistfound != null) {
                        if (personlistfound.size() == 1) {
                            personfound = personlistfound.get(0);
                            personfound.setStatus(Person.STATUS_MATCHED_NAME);
                            return personfound;
                        }
                    }
                }
            }
            person.setStatus(Person.STATUS_MATCHED_NONE);
            return person;
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #04 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), e);
        }
    }

    /*
     * READ methods
     */
    private static List<Person> readViaInitialsForenamesMiddleSurname(Connection connection, String initials, String forenames, String middle, String surname) throws CRMPersonException {
        String query = "";
        Statement statement = null;
        ResultSet resultset = null;
        List<Person> personlist = new ArrayList();
        if (initials == null) {
            initials = "";
        }
        if (forenames == null) {
            forenames = "";
        }
        if (middle == null) {
            middle = "";
        }
        if (surname == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person surname is null");
            return personlist;
        }
        if (surname.isEmpty()) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person surname is empty");
            return personlist;
        }
        if (initials.isEmpty() && forenames.isEmpty()) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person initials and forenames are both empty");
            return personlist;
        }
        try {
            surname = StringUtil.removeSpaces(surname).toUpperCase();
            surname = surname.replaceAll("'", "''").replace(";", "").replace("%", "").replace("&", "");
            if (!initials.isEmpty()) {
                initials = StringUtil.removeDots(initials);
                initials = StringUtil.removeSpaces(initials).toUpperCase();
                initials = initials.replaceAll("'", "''").replace(";", "").replace("%", "").replace("&", "");
                query = "select * from people where shinits = " + "'".concat(initials).concat("'") + "and shsurname = " + "'".concat(surname).concat("'");
            }
            if (!forenames.isEmpty()) {
                forenames = StringUtil.properCase(forenames);
                forenames = forenames.replaceAll("'", "''").replace(";", "").replace("%", "").replace("&", "");
                query = "select * from people where forenames = " + "'".concat(forenames).concat("'") + "and shsurname = " + "'".concat(surname).concat("'");
            }
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                if (!middle.isEmpty()) {
                    String value = resultset.getString(Person.FIELD_PEOPLE_MIDDLE);
                    if (value != null) {
                        value = value.toLowerCase();
                        if (value.equals(middle.toLowerCase())) {
                            Person person = setBeanProperties(resultset);
                            personlist.add(person);
                        }
                    }
                } else {
                    Person person = setBeanProperties(resultset);
                    personlist.add(person);
                }
            }
            return personlist;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #03 for initials {0} forenames {1} middle {2} surname {3}", new Object[]{initials, forenames, middle, surname}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #05 for initials {0} forenames {1} middle {2} surname {3}", new Object[]{initials, forenames, middle, surname}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private static List<Person> readViaMiddleSurnameBirthGender(Connection connection, String middle, String surname, Date birth, String gender) throws CRMPersonException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        List<Person> personlist = new ArrayList();
        if (middle == null) {
            middle = "";
        }
        if (gender == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person gender is null");
            return personlist;
        }
        if (!"M".equals(gender) && !"F".equals(gender)) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.WARNING, "Person gender is invalid");
            return personlist;
        }
        if (surname == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person surname is null");
            return personlist;
        }
        if (surname.isEmpty()) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person surname is empty");
            return personlist;
        }
        if (birth == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person birth is null");
            return personlist;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birth);
            if (calendar.get(Calendar.YEAR) == 1900) {
                Logger.getLogger(PersonSQL.class.getName()).log(Level.WARNING, "Person birth is invalid");
                return personlist;
            }
            surname = StringUtil.removeSpaces(surname).toUpperCase();
            surname = surname.replaceAll("'", "''").replace(";", "").replace("%", "").replace("&", "");
            gender = gender.toUpperCase();
            String birthtoday = DateUtil.todaySQLServerformat(birth);
            String birthtomorrow = DateUtil.tomorrowSQLServerformat(birth);
            query = "select * from people where shsurname = " + "'".concat(surname).concat("'") + " and dofb >= " + "'".concat(birthtoday).concat("' and dofb < '").concat(birthtomorrow).concat("'") + " and sex = " + "'".concat(gender).concat("'");

            //DEBUG
            Logger.getLogger(PersonSQL.class.getName()).log(Level.INFO, "query: {0}", query);

            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                if (!middle.isEmpty()) {
                    String value = resultset.getString(Person.FIELD_PEOPLE_MIDDLE);
                    if (value != null) {
                        value = value.toLowerCase();
                        if (value.equals(middle.toLowerCase())) {
                            Person person = setBeanProperties(resultset);
                            personlist.add(person);
                        }
                    }
                } else {
                    Person person = setBeanProperties(resultset);
                    personlist.add(person);
                }
            }
            return personlist;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #04 for middle {0} surname {1} birth {2} gender {3}", new Object[]{middle, surname, birth, gender}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #06 for middle {0} name {1} birth {2} gender {3}", new Object[]{middle, surname, birth, gender}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private static List<Person> readViaMiddleSurnameBirth(Connection connection, String middle, String surname, Date birth) throws CRMPersonException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        List<Person> personlist = new ArrayList();
        if (middle == null) {
            middle = "";
        }
        if (surname == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person surname is null");
            return personlist;
        }
        if (surname.isEmpty()) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person surname is empty");
            return personlist;
        }
        if (birth == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person birth is null");
            return personlist;
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birth);
            if (calendar.get(Calendar.YEAR) == 1900) {
                Logger.getLogger(PersonSQL.class.getName()).log(Level.WARNING, "Person birth is invalid");
                return personlist;
            }
            surname = StringUtil.removeSpaces(surname).toUpperCase();
            surname = surname.replaceAll("'", "''").replace(";", "").replace("%", "").replace("&", "");
            String birthtoday = DateUtil.todaySQLServerformat(birth);
            String birthtomorrow = DateUtil.tomorrowSQLServerformat(birth);
            query = "select * from people where shsurname = " + "'".concat(surname).concat("'") + " and dofb >= " + "'".concat(birthtoday).concat("' and dofb < '").concat(birthtomorrow).concat("'");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                if (!middle.isEmpty()) {
                    String value = resultset.getString(Person.FIELD_PEOPLE_MIDDLE);
                    if (value != null) {
                        value = value.toLowerCase();
                        if (value.equals(middle.toLowerCase())) {
                            Person person = setBeanProperties(resultset);
                            personlist.add(person);
                        }
                    }
                } else {
                    Person person = setBeanProperties(resultset);
                    personlist.add(person);
                }
            }
            return personlist;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #05 for middle {0} surname {1} birth{2}", new Object[]{middle, surname, birth}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #07 for middle {0} name {1} birth {2}", new Object[]{middle, surname, birth}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    private static List<Person> readViaMiddleSurnameGender(Connection connection, String middle, String surname, String gender) throws CRMPersonException {
        String query;
        Statement statement = null;
        ResultSet resultset = null;
        List<Person> personlist = new ArrayList();
        if (middle == null) {
            middle = "";
        }
        if (gender == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person gender is null");
            return personlist;
        }
        if (!"M".equals(gender) && !"F".equals(gender)) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.WARNING, "Person gender is invalid");
            return personlist;
        }
        if (surname == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person surname is null");
            return personlist;
        }
        if (surname.isEmpty()) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person surname is empty");
            return personlist;
        }
        try {
            surname = StringUtil.removeSpaces(surname).toUpperCase();
            surname = surname.replaceAll("'", "''").replace(";", "").replace("%", "").replace("&", "");;
            gender = gender.toUpperCase().replace(";", "").replace("%", "").replace("&", "");
            query = "select * from people where shsurname = " + "'".concat(surname).concat("'") + " and sex = " + "'".concat(gender).concat("'");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                if (!middle.isEmpty()) {
                    String value = resultset.getString(Person.FIELD_PEOPLE_MIDDLE);
                    if (value != null) {
                        value = value.toLowerCase();
                        if (value.equals(middle.toLowerCase())) {
                            Person person = setBeanProperties(resultset);
                            personlist.add(person);
                        }
                    }
                } else {
                    Person person = setBeanProperties(resultset);
                    personlist.add(person);
                }
            }
            return personlist;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #06 for middle {0} surname {1} gender {2}", new Object[]{middle, surname, gender}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #08 for middle {0} name {1} gender {2}", new Object[]{middle, surname, gender}), e);
        } finally {
            closeSQL(statement, resultset);
        }
    }

    /*
     * UPDATE methods
     */
    public static boolean updateGender(Connection connection, Person person) throws CRMPersonException {
        String sql;
        Statement statement = null;
        if (person == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person is null");
            return false;
        }
        if (person.getPersonid() == 0) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person id is 0");
            return false;
        }
        if (person.getGender() == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person gender is null");
            return false;
        }
        if (person.getGender().isEmpty()) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person gender is empty");
            return false;
        }
        if (!"m".equals(person.getGender().toLowerCase()) && !"f".equals(person.getGender().toLowerCase())) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person gender is invalid");
            return false;
        }
        try {
            String gender = person.getGender().replace(";", "").replace("%", "").replace("&", "");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update people set sex = " + "'".concat(gender).concat("'") + "where peoplekey = " + person.getPersonid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(PersonSQL.class.getName()).log(Level.INFO, MessageFormat.format("Gender updated to {0} for personid {1}", new Object[]{gender, person.getPersonid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #07 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #09 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    private static boolean updateTitle(Connection connection, Person person) throws CRMPersonException {
        String sql;
        Statement statement = null;
        if (person == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person is null");
            return false;
        }
        if (person.getPersonid() == 0) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person id is 0");
            return false;
        }
        if (person.getTitle() == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person title is null");
            return false;
        }
        if (person.getTitle().isEmpty()) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person title is empty");
            return false;
        }
        try {
            String title = person.getTitle().replace(";", "").replace("%", "").replace("&", "");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update people set title = " + "'".concat(title).concat("'") + "where peoplekey = " + person.getPersonid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(PersonSQL.class.getName()).log(Level.INFO, MessageFormat.format("Title updated to {0} for personid {1}", new Object[]{title, person.getPersonid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #08 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #10 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    private static boolean updateForenames(Connection connection, Person person) throws CRMPersonException {
        String sql;
        Statement statement = null;
        if (person == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person is null");
            return false;
        }
        if (person.getPersonid() == 0) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person id is 0");
            return false;
        }
        if (person.getForenames() == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person forenames is null");
            return false;
        }
        if (person.getForenames().isEmpty()) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person forenames is empty");
            return false;
        }
        try {
            String forenames = person.getForenames().replace(";", "").replace("%", "").replace("&", "");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update people set forenames = " + "'".concat(forenames).concat("'") + "where peoplekey = " + person.getPersonid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(PersonSQL.class.getName()).log(Level.INFO, MessageFormat.format("Fornames updated to {0} for personid {1}", new Object[]{forenames, person.getPersonid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #09 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #11 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    private static boolean updateMiddle(Connection connection, Person person) throws CRMPersonException {
        String sql;
        Statement statement = null;
        if (person == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person is null");
            return false;
        }
        if (person.getPersonid() == 0) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person id is 0");
            return false;
        }
        String middle;
        if (person.getMiddle() == null) {
            //Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person middle is null");
            middle = "";
        } else {
            if (person.getMiddle().isEmpty()) {
                //Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person middle is empty");
                middle = "";
            } else {
                middle = person.getMiddle().replace(";", "").replace("%", "").replace("&", "");
            }
        }
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update people set middle = " + "'".concat(middle).concat("'") + "where peoplekey = " + person.getPersonid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(PersonSQL.class.getName()).log(Level.INFO, MessageFormat.format("Middle updated to {0} for personid {1}", new Object[]{middle, person.getPersonid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #09b for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #11b for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    private static boolean updateSurname(Connection connection, Person person) throws CRMPersonException {
        String sql;
        Statement statement = null;
        if (person == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person is null");
            return false;
        }
        if (person.getPersonid() == 0) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person id is 0");
            return false;
        }
        if (person.getSurname() == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person surname is null");
            return false;
        }
        if (person.getSurname().isEmpty()) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person surname is empty");
            return false;
        }
        try {
            String surname = person.getSurname().replace(";", "").replace("%", "").replace("&", "");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update people set surname = " + "'".concat(surname).concat("'") + "where peoplekey = " + person.getPersonid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(PersonSQL.class.getName()).log(Level.INFO, MessageFormat.format("Surname updated to {0} for personid {1}", new Object[]{surname, person.getPersonid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #09a for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #11a for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    private static boolean updateInitials(Connection connection, Person person) throws CRMPersonException {
        String sql;
        Statement statement = null;
        if (person == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person is null");
            return false;
        }
        if (person.getPersonid() == 0) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person id is 0");
            return false;
        }
        if (person.getInitials() == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person initials is null");
            return false;
        }
        if (person.getInitials().isEmpty()) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person initials is empty");
            return false;
        }
        try {
            String initials = person.getInitials().replace(";", "").replace("%", "").replace("&", "");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update people set initials = " + "'".concat(initials).concat("'") + "where peoplekey = " + person.getPersonid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(PersonSQL.class.getName()).log(Level.INFO, MessageFormat.format("Initials updated to {0} for personid {1}", new Object[]{initials, person.getPersonid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #10 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #12 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    public static boolean updateBirth(Connection connection, Person person) throws CRMPersonException {
        String sql;
        Statement statement = null;
        Calendar birthdate = Calendar.getInstance();
        if (person == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person is null");
            return false;
        }
        if (person.getPersonid() == 0) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person id is 0");
            return false;
        }
        if (person.getBirth() == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person birth is null");
            return false;
        }
        birthdate.setTime(person.getBirth());
        if (birthdate.get(Calendar.YEAR) == 1900 && birthdate.get(Calendar.MONTH) == Calendar.JANUARY && birthdate.get(Calendar.DAY_OF_MONTH) == 1) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.INFO, "Person birth is lowvalue");
            return false;
        }
        try {
            String birth = person.getSQLServerFormattedBirth();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            sql = "update people set dofb =" + "'".concat(birth).concat("'") + "where peoplekey =" + person.getPersonid();
            statement.executeUpdate(sql);
            statement.close();

            //DEBUG
            Logger.getLogger(PersonSQL.class.getName()).log(Level.INFO, MessageFormat.format("Birth updated to {0} for personid {1}", new Object[]{birth, person.getPersonid()}));

            return true;
        } catch (SQLException sqle) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            throw new CRMPersonException(MessageFormat.format("Person SQL exception #11 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), sqle);
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            throw new CRMPersonException(MessageFormat.format("Person exception #13 for personid {0} name {1}", new Object[]{person.getPersonid(), person.getFormattedName()}), e);
        } finally {
            closeSQL(statement, null);
        }
    }

    /*
     * Misc methods
     */
    private static Person setBeanProperties(ResultSet resultset) {
        Person person = new Person();
        if (resultset == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Resultset is null");
            return person;
        }
        try {
            person.setPersonid(resultset.getLong(Person.FIELD_PEOPLE_ID));

            Calendar calendardate = Calendar.getInstance();
            java.sql.Date sqldate = resultset.getDate(Person.FIELD_PEOPLE_BIRTH);
            if (sqldate != null) {
                calendardate.setTimeInMillis(sqldate.getTime());
            } else {
                calendardate.set(1900, Calendar.JANUARY, 1);
            }
            person.setBirth(calendardate.getTime());

            person.setForenames(resultset.getString(Person.FIELD_PEOPLE_FORENAMES));
            person.setInitials(resultset.getString(Person.FIELD_PEOPLE_INITIALS));
            person.setMiddle(resultset.getString(Person.FIELD_PEOPLE_MIDDLE));
            person.setSurname(resultset.getString(Person.FIELD_PEOPLE_SURNAME));
            person.setTitle(resultset.getString(Person.FIELD_PEOPLE_TITLE));
            person.setGender(resultset.getString(Person.FIELD_PEOPLE_GENDER));
            return person;
        } catch (SQLException sqle) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            return person;
        } catch (Exception e) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return person;
        }
    }

    private static ResultSet setResultsetColumns(ResultSet resultset, Person person) {
        if (resultset == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Resultset is null");
            return null;
        }
        if (person == null) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, "Person is null");
            return resultset;
        }
        try {
            resultset.updateString(Person.FIELD_PEOPLE_TITLE, person.getTitle());
            resultset.updateString(Person.FIELD_PEOPLE_FORENAMES, person.getForenames());
            resultset.updateString(Person.FIELD_PEOPLE_MIDDLE, person.getMiddle());
            resultset.updateString(Person.FIELD_PEOPLE_INITIALS, person.getInitials());
            resultset.updateString(Person.FIELD_PEOPLE_SURNAME, person.getSurname());

            if (person.getBirth() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(person.getBirth());
                if (calendar.get(Calendar.YEAR) != 1900) {
                    resultset.updateDate(Person.FIELD_PEOPLE_BIRTH, new java.sql.Date(calendar.getTimeInMillis()));
                }

            }

            resultset.updateString(Person.FIELD_PEOPLE_GENDER, person.getGender());
            //resultset.updateString(FIELD_PEOPLE_RECTYPE, rectype);

            return resultset;
        } catch (SQLException sqle) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
            return null;
        } catch (Exception e) {
            Logger.getLogger(ContactSQL.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            return null;
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
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, sqle.getMessage(), sqle);
        }
    }

    private String formatInitials(String value) {
        if (value == null) {
            return "";
        }
        if (value.isEmpty()) {
            return "";
        }
        String formatted = "";
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != '.') {
                formatted = formatted.concat(String.valueOf(value.charAt(i)));
                formatted = formatted.concat(".");
            }
        }
        return formatted.toUpperCase();
    }
}
