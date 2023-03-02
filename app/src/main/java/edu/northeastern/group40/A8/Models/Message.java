package edu.northeastern.group40.A8.Models;

public class Message {
    String message;
    String sender;
    String receiver;
    String date;
    String time;


    public Message(String message, String sender, String receiver, String date, String time) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.time = time;
    }

    public Message(){}

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setDate() {
        this.date = "";
    }

    public String getReceiver() {
        return receiver;
    }


}
