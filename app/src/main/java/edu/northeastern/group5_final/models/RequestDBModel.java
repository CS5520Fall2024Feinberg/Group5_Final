package edu.northeastern.group5_final.models;

public class RequestDBModel {
    private String bandName;
    private String message;
    private String recipientUsername;
    private String requestorUsername;
    private String status;
    private String subject;
    private long timestamp;

    public RequestDBModel() {}

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public String getRequestorUsername() {
        return requestorUsername;
    }

    public void setRequestorUsername(String requestorUsername) {
        this.requestorUsername = requestorUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

