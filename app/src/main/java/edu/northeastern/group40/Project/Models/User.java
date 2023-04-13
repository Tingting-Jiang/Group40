package edu.northeastern.group40.Project.Models;

import java.util.ArrayList;
import java.util.List;

public class User {

    static final int INITIAL_BALANCE = 1000;

    String username;

    String password;

    String email;

    String phone;

    int balance;

    boolean isCarRenter;

    List<String> vehicles;

    List<Order> ordersAsCarOwner = new ArrayList<Order>();

    List<Order> ordersAsCarUser = new ArrayList<Order>();

    List<Order> allOrder = new ArrayList<Order>();

    String userID;

    public User(String username, String password, String email, String phone, boolean isCarRenter) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.isCarRenter = false;
        if (isCarRenter) {
            changeCarRenterStatus();
        }
        this.balance = INITIAL_BALANCE;
        this.ordersAsCarUser = new ArrayList<>();
    }

    public User(String username, String password, String email, String phone, boolean isCarRenter, String userID) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.isCarRenter = false;
        if (isCarRenter) {
            changeCarRenterStatus();
        }
        this.balance = INITIAL_BALANCE;
        this.ordersAsCarUser = new ArrayList<>();
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    public boolean isCarRenter() {
        return isCarRenter;
    }

    public void changeCarRenterStatus() {
        if (isCarRenter) {
            isCarRenter = false;
            vehicles = null;
        } else {
            isCarRenter = true;
            if (ordersAsCarOwner == null) {
                ordersAsCarOwner = new ArrayList<>();
            }
            vehicles = new ArrayList<>();
        }
    }

    public User() {

    }

    public List<String> getVehicles() {
        return vehicles;
    }

    public void setCarRenter(boolean carRenter) {
        isCarRenter = carRenter;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getBalance() {
        return balance;
    }

    public void setVehicles(List<String> vehicles) {
        this.vehicles = vehicles;
    }

    public void addOrderAsCarOwner(Order order){
        ordersAsCarOwner.add(order);

    }

    public void addOrderAsCarUser(Order order){
        ordersAsCarUser.add(order);
    }

    public void addToAllOrder(Order order){
        allOrder.add(order);
    }

    public List<Order> getOrderAsCarOwner(){
        return this.ordersAsCarOwner;
    }

    public List<Order> getOrderAsCarUser(){
        return this.ordersAsCarUser;
    }

    public List<Order> getAllOrder(){
        return this.allOrder;
    }
}
