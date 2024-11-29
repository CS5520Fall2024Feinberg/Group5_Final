package edu.northeastern.group5_final.models;

import android.net.Uri;

public class Request {
    private Artist requestee;
    private String requesteeUsername;
    private String message;
    private String suggestedBandName;
    private Uri profilePicture;

    public Request(Artist requestee, String requesteeUsername, String message, String suggestedBandName, Uri profilePicture) {
        this.requestee = requestee;
        this.requesteeUsername = requesteeUsername;
        this.message = message;
        this.suggestedBandName = suggestedBandName;
        this.profilePicture = profilePicture;
    }

    public Artist getRequestee() { return requestee; }
    public void setRequestee(Artist requestee) { this.requestee = requestee; }

    public String getRequesteeUsername() { return requesteeUsername; }
    public void setRequesteeUsername(String requesteeUsername) { this.requesteeUsername = requesteeUsername; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSuggestedBandName() { return suggestedBandName; }
    public void setSuggestedBandName(String suggestedBandName) { this.suggestedBandName = suggestedBandName; }

    public Uri getProfilePicture() { return profilePicture; }
    public void setProfilePicture(Uri profilePicture) { this.profilePicture = profilePicture; }
}

