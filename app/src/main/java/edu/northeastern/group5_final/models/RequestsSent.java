package edu.northeastern.group5_final.models;

import java.util.HashMap;
import java.util.Map;

public class RequestsSent {
    public enum Status {
        PLUS, WAITING, DONE
    }

    private String recipientId;
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
}
