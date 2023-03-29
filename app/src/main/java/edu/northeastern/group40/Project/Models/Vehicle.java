package edu.northeastern.group40.Project.Models;


import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.Place;

import java.io.Serializable;

public class Vehicle implements Serializable{

    private Brand brand;
    private Brand.Model model;
    private Color color;
    private VehicleBodyStyle vehicleBodyStyle;
    private Fuel fuel;
    private Mileage mileage;
    private int capacity;
    private MyLocation place;
    private String reviewResult;
    private int reviewTotalNumber;
    private String vehicleTitle;
    private int rentPrice;
    private Bitmap carImage;

    public Vehicle(Brand brand, Brand.Model model, Color color, VehicleBodyStyle vehicleBodyStyle,
                   Fuel fuel, Mileage mileage, int capacity, MyLocation place, int rentPrice,
                   String vehicleTitle, Bitmap carImage){
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.vehicleBodyStyle = vehicleBodyStyle;
        this.fuel = fuel;
        this.mileage = mileage;
        this.capacity = capacity;
        this.place = place;
        this.rentPrice = rentPrice;
        this.vehicleTitle = vehicleTitle;
        this.carImage = carImage;
        this.reviewResult = null;
        this.reviewTotalNumber = 0;
    }


    public Brand getBrand() {
        return brand;
    }


    public Brand.Model getModel() {
        return model;
    }


    public Color getColor() {
        return color;
    }


    public VehicleBodyStyle getVehicleBodyStyle() {
        return vehicleBodyStyle;
    }


    public Fuel getFuel() {
        return fuel;
    }


    public Mileage getMileage() {
        return mileage;
    }


    public int getCapacity() {
        return capacity;
    }


    public MyLocation getPlace() {
        return place;
    }


    public Bitmap getImage() {
        return carImage;
    }


    public String getTitle() {
        return vehicleTitle;
    }


    public int getReviewTotalNumber() {
        return reviewTotalNumber;
    }


    public String getReviewResult() {
        return reviewResult;
    }


    public int getRentPrice() {
        return rentPrice;
    }

    @NonNull
    @Override
    public String toString() {
        return "Brand: " + brand + ", Model: " + model + ", Color: " + color +
                ", Body Style: " + vehicleBodyStyle + ", Fuel: " + fuel + ", Mileage: " +
                mileage + ", Capacity: " + capacity + ", Place: " + place;
    }
}
