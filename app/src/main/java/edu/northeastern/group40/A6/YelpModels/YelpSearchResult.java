package edu.northeastern.group40.A6.YelpModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YelpSearchResult {
    @SerializedName("businesses")
    @Expose
    List<Restaurant> restaurants;

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }
}
