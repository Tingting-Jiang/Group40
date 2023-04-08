package edu.northeastern.group40.Project.Models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AvailableDate implements Serializable {
    private static final String FORMAT = "MM/dd/yyyy";
    public String startDate;
    public String endDate;

    public AvailableDate(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AvailableDate() {}

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

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    private boolean isAfter(Date endThis, Date endThat) {
        return endThis.after(endThat) || endThis.equals(endThat);
    }

    private boolean isBefore(Date startThis, Date startThat) {
        return startThis.before(startThat) || startThis.equals(startThat) ;
    }

    public int calculateDuration() {
        Date startThis, endThis;
        try {
            startThis = new SimpleDateFormat(FORMAT).parse(startDate);
            endThis = new SimpleDateFormat(FORMAT).parse(endDate);
            long diffInMillis = Math.abs(endThis.getTime() - startThis.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            return (int) diff;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        return startDate + " - " + endDate;
    }
}
