package edu.northeastern.group40.Project.Models;


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

    public Vehicle(Brand brand, Brand.Model model, Color color, VehicleBodyStyle vehicleBodyStyle,
                   Fuel fuel, Mileage mileage, int capacity, Place place){
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.vehicleBodyStyle = vehicleBodyStyle;
        this.fuel = fuel;
        this.mileage = mileage;
        this.capacity = capacity;
        this.place = place;
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
    public String toString() {
        return "Brand: " + brand + ", Model: " + model + ", Color: " + color +
                ", Body Style: " + vehicleBodyStyle + ", Fuel: " + fuel + ", Mileage: " +
                mileage + ", Capacity: " + capacity + ", Place: " + place;
    }
}
