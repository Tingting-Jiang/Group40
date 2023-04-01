package edu.northeastern.group40.Project.Models;

public class Order {
    String orderId;

    Vehicle orderedVehicle;

    AvailableDate orderDate;

    int orderPriceTotal;

    Review review;

    public Order(String orderId, Vehicle orderedVehicle, AvailableDate orderDate, int orderPriceTotal) {
        this.orderId = orderId;
        this.orderedVehicle = orderedVehicle;
        this.orderDate = orderDate;
        this.orderPriceTotal = orderPriceTotal;
    }

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
}
