package edu.northeastern.group5_final.models;

public class FavoriteSong {
    private String title;
    private String artistNames;
    private String releaseDate;

    public FavoriteSong(String title, String artistNames, String releaseDate) {
        this.title = title;
        this.artistNames = artistNames;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public String getArtistNames() {
        return artistNames;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
}
