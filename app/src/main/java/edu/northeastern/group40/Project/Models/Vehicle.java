package edu.northeastern.group40.Project.Models;


import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.Place;

public class Vehicle implements VehicleInterface{

    private Brand brand;
    private Brand.Model model;
    private Color color;
    private VehicleBodyStyle vehicleBodyStyle;
    private Fuel fuel;
    private Mileage mileage;
    private int capacity;
    private Place place;
    private String reviewResult;
    private int reviewTotalNumber;
    private String vehicleTitle;
    private int rentPrice;
    private Bitmap carImage;

    public Vehicle(Brand brand, Brand.Model model, Color color, VehicleBodyStyle vehicleBodyStyle,
                   Fuel fuel, Mileage mileage, int capacity, Place place, int rentPrice,
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

    @Override
    public Brand getBrand() {
        return brand;
    }

    @Override
    public Brand.Model getModel() {
        return model;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public VehicleBodyStyle getVehicleBodyStyle() {
        return vehicleBodyStyle;
    }

    @Override
    public Fuel getFuel() {
        return fuel;
    }

    @Override
    public Mileage getMileage() {
        return mileage;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public Place getPlace() {
        return place;
    }

    @Override
    public Bitmap getImage() {
        return carImage;
    }

    @Override
    public String getTitle() {
        return vehicleTitle;
    }

    @Override
    public int getReviewTotalNumber() {
        return reviewTotalNumber;
    }

    @Override
    public String getReviewResult() {
        return reviewResult;
    }

    @Override
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
