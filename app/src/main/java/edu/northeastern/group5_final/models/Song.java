package edu.northeastern.group5_final.models;

public class Song {
    private String id;
    private String title;
    private String artist;
    private String genre;
    private boolean isPlaying;
    private boolean isFavorite;
    private int progress;
    private String songUrl;
    private int likedBy;
    private String releaseDate;

    public Song(String id, String title, String artist, String genre, boolean isPlaying, boolean isFavorite, int progress, String songUrl, int likedBy, String releaseDate) {

        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.isPlaying = isPlaying;
        this.isFavorite = isFavorite;
        this.progress = progress;
        this.songUrl = songUrl;
        this.likedBy = likedBy;
        this.releaseDate = releaseDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(int likedBy) {
        this.likedBy = likedBy;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
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

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

}
