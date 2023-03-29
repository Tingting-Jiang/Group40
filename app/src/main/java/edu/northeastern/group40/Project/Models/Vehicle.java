package edu.northeastern.group40.Project.Models;

import androidx.annotation.NonNull;
import java.io.Serializable;

public class Vehicle implements Serializable {

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
    private String carImage;
    private String ownerID;
    private String vehicleID;

    // date format: "MM/DD/YYYY"
    private String startDate;
    private String endDate;

    public Vehicle(Brand brand, Brand.Model model, Color color, VehicleBodyStyle vehicleBodyStyle,
                   Fuel fuel, Mileage mileage, int capacity, MyLocation place, int rentPrice,
                   String vehicleTitle, String carImage, String startDate, String endDate, String ownerID, String vehicleID){
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
        this.startDate = startDate;
        this.endDate = endDate;
        this.ownerID = ownerID;
    }


    public String getOwnerID() {
        return ownerID;
    }

    public String getVehicleID(){
        return vehicleID;
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

    public String getImage() {
        return carImage;
    }

    public MyLocation getPlace() {
        return place;
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

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setReviewResult(String reviewResult) {
        this.reviewResult = reviewResult;
    }

    public void setReviewTotalNumber(int reviewTotalNumber) {
        this.reviewTotalNumber = reviewTotalNumber;
    }

    @NonNull
    @Override
    public String toString() {
        return " Brand: " + brand + "\n Model: " + model + "\n Color: " + color +
                "\n Body Style: " + vehicleBodyStyle + "\n Fuel: " + fuel + "\n Mileage: " +
                mileage + "\n Capacity: " + capacity;
    }
}
