package edu.northeastern.group40.Project.Models;

public class Car {

    private String carTitle;
    private String totalMiles;
    private String review_result;
    private String review_num;
    private String distance;
    private String carImage;

    public Car(String carTitle, String totalMiles, String review_result, String review_num, String distance, String carImage) {
        this.carTitle = carTitle;
        this.totalMiles = totalMiles;
        this.review_result = review_result;
        this.review_num = review_num;
        this.distance = distance;
        this.carImage = carImage;
    }

    public String getCarTitle() {
        return carTitle;
    }

    public String getTotalMiles() {
        return totalMiles;
    }

    public String getReview_result() {
        return review_result;
    }

    public String getReview_num() {
        return review_num;
    }

    public String getDistance() {
        return distance;
    }

    public String getCarImage() {
        return carImage;
    }
}
