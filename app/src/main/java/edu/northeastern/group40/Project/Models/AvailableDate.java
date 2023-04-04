package edu.northeastern.group40.Project.Models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AvailableDate implements Serializable {
    private static final String FORMAT = "MM/dd/yyyy";
    public String startDate;
    public String endDate;

    public AvailableDate(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @SuppressLint("SimpleDateFormat")
    public boolean isAvailable(AvailableDate anotherDate) {
        Date startThis, endThis, startThat, endThat;
        try {
            startThis = new SimpleDateFormat(FORMAT).parse(startDate);
            endThis = new SimpleDateFormat(FORMAT).parse(endDate);
            startThat = new SimpleDateFormat(FORMAT).parse(anotherDate.startDate);
            endThat = new SimpleDateFormat(FORMAT).parse(anotherDate.endDate);
            return isBefore(startThis, startThat) && isAfter(endThis, endThat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isAfter(Date endThis, Date endThat) {
        return endThis.after(endThat);
    }

    private boolean isBefore(Date startThis, Date startThat) {
        return startThis.before(startThat);
    }

    @NonNull
    @Override
    public String toString() {
        return startDate + " - " + endDate;
    }
}
