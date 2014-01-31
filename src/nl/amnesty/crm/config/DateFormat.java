/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.config;

/**
 *
 * @author evelzen
 */
public class DateFormat {

    private int yearindexbegin;
    private int yearindexend;
    private int monthindexbegin;
    private int monthindexend;
    private int dayindexbegin;
    private int dayindexend;

    public DateFormat() {
    }

    public DateFormat(int yearindexbegin, int yearindexend, int monthindexbegin, int monthindexend, int dayindexbegin, int dayindexend) {
        this.yearindexbegin = yearindexbegin;
        this.yearindexend = yearindexend;
        this.monthindexbegin = monthindexbegin;
        this.monthindexend = monthindexend;
        this.dayindexbegin = dayindexbegin;
        this.dayindexend = dayindexend;
    }

    public int getDayindexbegin() {
        return dayindexbegin;
    }

    public void setDayindexbegin(int dayindexbegin) {
        this.dayindexbegin = dayindexbegin;
    }

    public int getDayindexend() {
        return dayindexend;
    }

    public void setDayindexend(int dayindexend) {
        this.dayindexend = dayindexend;
    }

    public int getMonthindexbegin() {
        return monthindexbegin;
    }

    public void setMonthindexbegin(int monthindexbegin) {
        this.monthindexbegin = monthindexbegin;
    }

    public int getMonthindexend() {
        return monthindexend;
    }

    public void setMonthindexend(int monthindexend) {
        this.monthindexend = monthindexend;
    }

    public int getYearindexbegin() {
        return yearindexbegin;
    }

    public void setYearindexbegin(int yearindexbegin) {
        this.yearindexbegin = yearindexbegin;
    }

    public int getYearindexend() {
        return yearindexend;
    }

    public void setYearindexend(int yearindexend) {
        this.yearindexend = yearindexend;
    }
}
