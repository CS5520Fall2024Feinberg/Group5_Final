package edu.northeastern.group5_final;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import edu.northeastern.group5_final.models.Song;

public class MyMediaPlayer {

    private static volatile MyMediaPlayer myMediaPlayer;
    private final Application applicationContext;

    private MediaPlayer mediaPlayer;
    private final List<Song> playList = new ArrayList<>();
    private int current = 0; // Default to first song
    private Timer timer;
    private TimerTask timerTask;
    private final List<Callback> callbacks = new ArrayList<>();
    private final Handler handler;
    private int playbackPosition = 0;

    public interface Callback {
        void onProgress(int progress);
        void onPlay(Song song);
        void onPause(Song song);
        void onStop(Song song);
    }

    private MyMediaPlayer(Context applicationContext) {
        this.applicationContext = (Application) applicationContext;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public static MyMediaPlayer getInstance(Context context) {
        if (myMediaPlayer == null) {
            synchronized (MyMediaPlayer.class) {
                if (myMediaPlayer == null) {
                    myMediaPlayer = new MyMediaPlayer(context.getApplicationContext());
                }
            }
        }
        return myMediaPlayer;
    }

    public void addCallback(Callback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    public void removeCallback(Callback callback) {
        callbacks.remove(callback);
    }

    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void onProgress(int progress) {
        for (Callback callback : new ArrayList<>(callbacks)) {
            callback.onProgress(progress);
        }
    }

    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int progress = (mediaPlayer.getCurrentPosition() * 100) / mediaPlayer.getDuration();
                        onProgress(progress);
                    }
                });
            }
        };
        timer.schedule(timerTask, 100, 100);
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    private void notifyOnPlay(Song song) {
        for (Callback callback : new ArrayList<>(callbacks)) {
            callback.onPlay(song);
        }
    }

    private void notifyOnPause(Song song) {
        for (Callback callback : new ArrayList<>(callbacks)) {
            callback.onPause(song);
        }
    }

    private void notifyOnStop(Song song) {
        for (Callback callback : new ArrayList<>(callbacks)) {
            callback.onStop(song);
        }
    }

    public void play() {
        if (playList.isEmpty()) {
            return;
        }

        Song song = playList.get(current);

        // Was plying Already
        if (mediaPlayer != null && playbackPosition > 0) {
            song.setPlaying(true);
            mediaPlayer.seekTo(playbackPosition);
            mediaPlayer.start();
            notifyOnPlay(song);
            startTimer();
            return;
        }

        playbackPosition = 0;

        if (mediaPlayer != null) {
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();
        }

        try {
            mediaPlayer.setDataSource(song.getSongUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                song.setPlaying(true);
                mediaPlayer.start();
                notifyOnPlay(song);
                startTimer();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                playbackPosition = 0;
                song.setPlaying(false);
                stopTimer();
                next();
            });

        } catch (Exception e) {
            Log.e("MyMediaPlayer", "Error playing song", e);
        }
    }

    public void next() {
        if (playList.isEmpty()) {
            Log.e("MyMediaPlayer", "Playlist is empty");
            return;
        }
        current = (current + 1) % playList.size();
        stop();
        play();
    }

    public void prefix() {
        if (playList.isEmpty()) {
            Log.e("MyMediaPlayer", "Playlist is empty");
            return;
        }
        current = (current - 1 + playList.size()) % playList.size();
        play();
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            playbackPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            Song song = playList.get(current);
            song.setPlaying(false);
            notifyOnPause(song);
        }
    }


    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            stopTimer();
            Song song = playList.get(current);
            notifyOnStop(song);
        }
    }

    public int getCurrent() {
        return current;
    }

    public void addSong(Song song) {
        playList.add(song);
    }

    public void removeSong(Song song) {
        playList.remove(song);
        if (current >= playList.size()) {
            current = playList.size() - 1;
        }
    }

    public void removeSongById(String songId) {
        playList.removeIf(song -> song.getId().equals(songId));
        if (current >= playList.size()) {
            current = Math.max(0, playList.size() - 1);
        }
    }


    public List<Song> getPlayList() {
        return new ArrayList<>(playList);
    }

    public List<String> getSongIds() {
        return playList
                .stream()
                .map(Song::getId)
                .collect(Collectors.toList());
    }

    public void renewList(List<Song> newPlaylist) {
        playList.clear();
        playList.addAll(newPlaylist);
    }

}
