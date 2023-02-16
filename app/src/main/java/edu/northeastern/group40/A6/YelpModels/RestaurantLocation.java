package edu.northeastern.group40.A6.YelpModels;

import com.google.gson.annotations.SerializedName;

public class RestaurantLocation {
    @SerializedName("address1")
    String address;

    public String getAddress() {
        return this.address;
    }
}
