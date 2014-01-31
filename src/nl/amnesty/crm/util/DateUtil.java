/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.amnesty.crm.sql.PersonSQL;

/**
 *
 * @author evelzen
 */
public class DateUtil {

    public static String todaySQLServerformat(Date date) {
        try {
            //SQLServer date format is 2007-1-20 10:44:22.717
            Calendar calendar = Calendar.getInstance();
            // Date of today
            calendar.setTime(date);
            return formattedSQLServerDate(calendar.getTime());
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public static String tomorrowSQLServerformat(Date date) {
        try {
            //SQLServer date format is 2007-1-20 10:44:22.717
            Calendar calendar = Calendar.getInstance();
            // Date of today
            calendar.setTime(date);
            // Day after today
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            return formattedSQLServerDate(calendar.getTime());
        } catch (Exception e) {
            Logger.getLogger(PersonSQL.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    private static String formattedSQLServerDate(Date date) {
        if (date == null) {
            return "null";
        }
        String formatteddate = formattedDate(date);
        return formatteddate.concat(" 00:00:00.000");
    }

    public static String formattedDate(Date date) {
        if (date == null) {
            return "null";
        }
        String yearvalue;
        String monthvalue;
        String dayvalue;
        Calendar calendar = Calendar.getInstance();
        TimeZone timezone = TimeZone.getDefault();
        calendar.setTimeZone(timezone);
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        yearvalue = String.valueOf(year);
        monthvalue = String.valueOf(month);
        if (month < 10) {
            monthvalue = "0".concat(monthvalue);
        }
        dayvalue = String.valueOf(day);
        if (day < 10) {
            dayvalue = "0".concat(dayvalue);
        }
        return yearvalue.concat("-").concat(monthvalue).concat("-").concat(dayvalue);
    }
}
