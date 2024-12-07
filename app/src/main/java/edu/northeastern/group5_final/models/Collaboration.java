package edu.northeastern.group5_final.models;

public class Collaboration {
    private String bandName;
    private String artists;

    public Collaboration(String bandName, String artists) {
        this.bandName = bandName;
        this.artists = artists;
    }

    public String getBandName() {
        return bandName;
    }

    public String getArtists() {
        return artists;
    }
}
