package edu.northeastern.group5_final.messaging;

public class Messages {
    String sender;
    String message;
    String date;
    String topic;
    String receiver;


    public Messages(String sender, String message, String date, String topic, String receiver) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.topic = topic;
        this.receiver = receiver;
    }


    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getTopic() {
        return topic;
    }

    public String getReceiver() {
        return receiver;
    }
}
