package edu.northeastern.group5_final.models;

public class MySong {
    private String title;
    private String releaseDate;
    private String genre;

    public MySong(String title, String releaseDate, String genre) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.genre = genre;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getGenre() {
        return genre;
    }
}
