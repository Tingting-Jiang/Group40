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
            return "0-10000 miles";
        } else if(this == OVER_100K){
            return "more than 100000 miles";
        }
        else {
            return String.format("%d-%d miles", lowerBound, upperBound);
        }
    }

    public static Mileage fromString(String str) {
        switch (str) {
            case "0-10000 miles":
                return LESS_THAN_10K;
            case "more than 100000 miles":
                return OVER_100K;
            case "5000-10000 miles":
                return BETWEEN_5K_AND_10K;
            case "10000-100000 miles":
                return BETWEEN_10K_AND_100K;
            default:
                return null;
        }
    }

}
