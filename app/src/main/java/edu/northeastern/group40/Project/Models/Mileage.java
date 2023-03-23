package edu.northeastern.group40.Project.Models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

public enum Mileage {
    LESS_THAN_10K(0, 5000),
    BETWEEN_5K_AND_10K(5000, 10000),
    BETWEEN_10K_AND_100K(10000, 100000),
    OVER_100K(100000, Integer.MAX_VALUE);

    private final int lowerBound;
    private final int upperBound;

    private Mileage(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }


    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        if (this == LESS_THAN_10K) {
            return "0-10000";
        } else if(this == OVER_100K){
            return "more than 100000";
        }
        else {
            return String.format("%d-%d", lowerBound, upperBound);
        }
    }
}
