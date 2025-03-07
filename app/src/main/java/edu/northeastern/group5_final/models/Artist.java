package edu.northeastern.group5_final.models;

import android.net.Uri;

import java.util.List;

public class Artist {

    public enum Status {
        PLUS, WAITING, DONE
    }

    private String name;
    private String joinedDate;
    private List<String> songNames;
    private Uri profilePicture; // Can be null if no picture is provided
    private Status status;
    private String bio;
    private String username;
    private boolean isIndividual;

    public Artist(String name, String joinedDate, List<String> songNames, Uri profilePicture, Status status, String username, String bio, boolean isInvidual) {
        this.name = name;
        this.joinedDate = joinedDate;
        this.songNames = songNames;
        this.profilePicture = profilePicture;
        this.status = status;
        this.username = username;
        this.bio = bio;
        this.isIndividual = isInvidual;
    }

    public String getName() {
        return name;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public List<String> getSongNames() {
        return songNames;
    }

    public Uri getProfilePicture() {
        return profilePicture;
    }

    public int getTotalSongsReleased() {
        return songNames != null ? songNames.size() : 0;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getIsIndividual() {
        return isIndividual;
    }

    public void setIsIndividual(boolean isIndividual) {
        this.isIndividual = isIndividual;
    }

}
