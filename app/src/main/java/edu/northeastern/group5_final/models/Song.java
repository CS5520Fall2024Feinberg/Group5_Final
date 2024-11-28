package edu.northeastern.group5_final.models;

public class Song {
    private String title;
    private String artist;
    private String genre;
    private boolean isPlaying;
    private boolean isFavorite;
    private int progress;
    private int songId;


    public Song(String title, String artist, String genre, boolean isPlaying, boolean isFavorite, int progress, int songId) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.isPlaying = isPlaying;
        this.isFavorite = isFavorite;
        this.progress = progress;
        this.songId = songId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }


    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

}
