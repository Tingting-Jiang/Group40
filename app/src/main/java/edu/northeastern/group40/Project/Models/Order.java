package edu.northeastern.group40.Project.Models;

import java.io.Serializable;

public class Order implements Serializable {
    String orderId;

    Vehicle orderedVehicle;

    AvailableDate orderDate;

    int orderPriceTotal;

    private String ownerId;

    int review;


    public Order(String orderId, Vehicle orderedVehicle, AvailableDate orderDate, int orderPriceTotal, String ownerId) {
        this.orderId = orderId;
        this.orderedVehicle = orderedVehicle;
        this.orderDate = orderDate;
        this.orderPriceTotal = orderPriceTotal;
        this.ownerId = ownerId;
        this.review = 0;
    }

    public Order() {}

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Vehicle getOrderedVehicle() {
        return orderedVehicle;
    }

    public void setOrderedVehicle(Vehicle orderedVehicle) {
        this.orderedVehicle = orderedVehicle;
    }

    public AvailableDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(AvailableDate orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderPriceTotal() {
        return orderPriceTotal;
    }

    public void setOrderPriceTotal(int orderPriceTotal) {
        this.orderPriceTotal = orderPriceTotal;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }
}
