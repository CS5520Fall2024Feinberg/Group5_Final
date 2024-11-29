package edu.northeastern.group5_final.models;

import android.net.Uri;

import java.util.List;

public class Artist {
    private String name;
    private String joinedDate;
    private List<String> songNames;
    private Uri profilePicture; // Can be null if no picture is provided
    private int totalSongsReleased;

    public Artist(String name, String joinedDate, List<String> songNames, Uri profilePicture, int totalSongsReleased) {
        this.name = name;
        this.joinedDate = joinedDate;
        this.songNames = songNames;
        this.profilePicture = profilePicture;
        this.totalSongsReleased = totalSongsReleased;
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
}
