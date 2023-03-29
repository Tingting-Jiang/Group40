package edu.northeastern.group40.Project.Models;

import java.util.ArrayList;
import java.util.List;

public class User {

    String username;

    String password;

    String email;

    String phone;

    double balance;

    boolean isCarRenter;

    List<Vehicle> vehicles;

    public User(String username, String password, String email, String phone, double balance, boolean isCarRenter) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.phone = phone;
            this.balance = balance;
            this.isCarRenter = false;
            if (isCarRenter) {
                changeCarRenterStatus();
            }
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
            vehicles = new ArrayList<>();
        }
    }

    public List<Vehicle> getVehicles() throws IllegalArgumentException{
        if (!isCarRenter) {
            throw new IllegalArgumentException("This user is not a car renter!");
        }
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

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
