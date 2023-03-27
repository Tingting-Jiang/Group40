package edu.northeastern.group40.Project.Models;

import com.google.android.libraries.places.api.model.Place;

public interface VehicleInterface {
    Brand getBrand();
    Brand.Model getModel();
    Color getColor();
    VehicleBodyStyle getVehicleBodyStyle();
    Fuel getFuel();
    Mileage getMileage();
    int getCapacity();
    Place getPlace();
}
