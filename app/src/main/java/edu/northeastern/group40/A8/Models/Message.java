package edu.northeastern.group40.A8.Models;

public class Message {
    private String message;
    private String sender;
    private String receiver;
    private String date;
    private String time;
    private String senderFullName;


    public Message(String message, String sender, String receiver, String date, String time, String senderFullName) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.time = time;
        this.senderFullName = senderFullName;
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

    public String getSenderFullName() {
        return senderFullName;
    }
}
