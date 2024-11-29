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
    private int totalSongsReleased;
    private Status status;
    private String bio;
    private String username;

    public Artist(String name, String joinedDate, List<String> songNames, Uri profilePicture, int totalSongsReleased, String username, String bio) {
        this.name = name;
        this.joinedDate = joinedDate;
        this.songNames = songNames;
        this.profilePicture = profilePicture;
        this.totalSongsReleased = totalSongsReleased;
        this.status = Status.PLUS;
        this.username = username;
        this.bio = bio;

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
        return totalSongsReleased;
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

}
