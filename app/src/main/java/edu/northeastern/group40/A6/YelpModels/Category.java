package edu.northeastern.group40.A6.YelpModels;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("title")
    String title;

    public String getTitle() {
        return title;
    }
}
