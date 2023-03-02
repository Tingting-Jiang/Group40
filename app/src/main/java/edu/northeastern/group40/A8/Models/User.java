package edu.northeastern.group40.A8.Models;

public class User {
    String name, email, userId;

    public User(String email, String userId, String name) {
        this.email = email;
        this.userId = userId;
        this.name = name;
    }

    public User(){}

    public String getContactName() {
        return name;
    }

    public String getContactEmail() {
        return email;
    }

    public String getContactId() {
        return userId;
    }

}
