package edu.northeastern.group40.A6.YelpModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Restaurant {
    @SerializedName("name")
    String name;

    @SerializedName("rating")
    Double rating;

    @SerializedName("price")
    String price;

    @SerializedName("review_count")
    Integer reviewCount;

    @SerializedName("image_url")
    String imageUrl;

    @SerializedName("categories")
    List<Category> categories;


    @SerializedName("location")
    RestaurantLocation location;


    public String getName() {
        return name;
    }

    public Double getRating() {
        return rating;
    }

    public String getPrice() {
        return price;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public RestaurantLocation getLocation() {
        return location;
    }
}
