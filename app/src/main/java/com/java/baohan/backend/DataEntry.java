package com.java.baohan.backend;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataEntry {

    public int confirmed;
    public int cured;
    public int dead;
    public LocalDate date;

    public DataEntry(int confirmed, int cured, int dead, LocalDate date) {
        this.confirmed = confirmed;
        this.cured = cured;
        this.dead = dead;
        this.date = date;
    }

    public void accumulate(DataEntry e) {
        if(date.equals(e.date)) {
            confirmed += e.confirmed;
            cured += e.cured;
            dead += e.dead;
        }
    }

    public int getCurConfirmed() { return confirmed-cured-dead; }

    public String dispCurConfirmed() {
        return NumberFormat.getNumberInstance(Locale.US).format(confirmed - cured - dead);
    }

    public String dispConfirmed() {
        return NumberFormat.getNumberInstance(Locale.US).format(confirmed);
    }

    public String dispCured() {
        return NumberFormat.getNumberInstance(Locale.US).format(cured);
    }

    public String dispDead() {
        return NumberFormat.getNumberInstance(Locale.US).format(dead);
    }

    public String dispDate() {
        return date.format(DateTimeFormatter.ofPattern("MM-dd"));
    }

    @Override
    public String toString() {
        return "" + date + ": (" + confirmed + ", " + cured + ", " + dead;
    }
}
