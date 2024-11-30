package edu.northeastern.group5_final.models;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ArtistDBModel {

    private String name;
    private String username;
    private String dateJoined;
    private String profilePictureUrl;
    private String bio;

    @Exclude
    private Map<String,String> requestsSent = new HashMap<>();

    @Exclude
    private String password;

    @Exclude
    private String email;

    //    @Exclude
    //    private boolean isIndividual;
    //
    //    @Exclude
    //    private List<String> songNames;
//


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Map<String, String> getRequestsSent() {
        return requestsSent;
    }

    public void setRequestsSent(Map<String, String> requestsSent) {
        this.requestsSent = requestsSent;
    }
}
