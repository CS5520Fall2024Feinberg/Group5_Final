package edu.northeastern.group5_final.models;

import com.google.firebase.database.Exclude;

import java.util.List;

public class SongDBModel {

    private String id;
    private String title;
    private String url;
    private String genre;

    @Exclude
    private List<String> artists;

    public SongDBModel() {}

    public SongDBModel(String id, String title, String url, String genre, List<String> artists) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.genre = genre;
        this.artists = artists;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
    }
}
