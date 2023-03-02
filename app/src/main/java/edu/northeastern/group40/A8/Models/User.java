package edu.northeastern.group40.A8.Models;

public class User {
    String name, email, userId;

    public User(String email, String userId) {
        this.email = email;
        this.userId = userId;
    }

    public User(){}

    public String getContactName() {
        String name = email.substring(0, email.indexOf("@"));
        return name;
    }

    public String getContactEmail() {
        return email;
    }

    public String getContactId() {
        return userId;
    }

}
