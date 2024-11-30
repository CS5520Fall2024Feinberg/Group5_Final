package edu.northeastern.group5_final.models;

import android.net.Uri;

public class Request {
    private Artist requestor;
    private String requestorUsername;
    private String subject;
    private String message;
    private String suggestedBandName;
    private Uri profilePicture;
    
    public Request(Artist requestor, String requestorUsername, String subject, String message, String suggestedBandName, Uri profilePicture) {
        this.requestor = requestor;
        this.requestorUsername = requestorUsername;
        this.subject = subject;
        this.message = message;
        this.suggestedBandName = suggestedBandName;
        this.profilePicture = profilePicture;
    }

    public Artist getRequestor() { return requestor; }
    public void setRequestor(Artist requestor) { this.requestor = requestor; }

    public String getRequestorUsername() { return requestorUsername; }
    public void setRequestorUsername(String requestorUsername) {this.requestorUsername = requestorUsername; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSuggestedBandName() { return suggestedBandName; }
    public void setSuggestedBandName(String suggestedBandName) { this.suggestedBandName = suggestedBandName; }

    public Uri getProfilePicture() { return profilePicture; }
    public void setProfilePicture(Uri profilePicture) { this.profilePicture = profilePicture; }
}

