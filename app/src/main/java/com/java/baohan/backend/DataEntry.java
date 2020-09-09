package com.java.baohan.backend;

import java.time.LocalDate;

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

    @Override
    public String toString() {
        return "" + date + ": (" + confirmed + ", " + cured + ", " + dead;
    }
}
