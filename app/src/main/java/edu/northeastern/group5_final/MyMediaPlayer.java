package edu.northeastern.group5_final;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import edu.northeastern.group5_final.models.Song;

public class MyMediaPlayer {

    private static volatile MyMediaPlayer myMediaPlayer;
    private final Application applicationContext;

    private MediaPlayer mediaPlayer;
    private final List<Song> playList = new ArrayList<>();
    private int current;
    private Timer timer;
    private TimerTask timerTask;
    private List<Callback> callbacks = new ArrayList<>();
    private Handler handler;


    public interface Callback {
        void onProgress(int progress);

        void onPlay(Song song);

        void onPause(Song song);

        void onStop(Song song);
    }

    public void addCallback(Callback callback) {
        this.callbacks.add(callback);
    }

    public void removeCallback(Callback callback) {
        this.callbacks.remove(callback);
    }

    public MyMediaPlayer(Context applicationContext) {
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
        for (Callback callback : callbacks) {
            callback.onProgress(progress);
        }
    }

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    try {
                        int progress = (mediaPlayer.getCurrentPosition() * 100) / mediaPlayer.getDuration();
                        onProgress(progress);
                    } catch (Exception ignored) {

                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 1000, 1000);
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void play(Song song) {
        current = playList.indexOf(song);
        play();
    }

    public void onPlay(Song song) {
        for (Callback callback : callbacks) {
            callback.onPlay(song);
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        try {
            Song song = playList.get(current);
            mediaPlayer = MediaPlayer.create(applicationContext, song.getSongId());
            mediaPlayer.start();
            onPlay(song);
            startTimer();
            mediaPlayer.setOnCompletionListener(mp -> {
                next();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void next() {
        if (current >= playList.size() - 1) {
            current = -1;
        }
        current += 1;
        play();
    }

    public void prefix() {
        if (current <= 0) {
            current = playList.size();
        }
        current -= 1;
        play();
    }

    public void onPause(Song song) {
        for (Callback callback : callbacks) {
            callback.onPause(song);
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Song song = playList.get(current);
            onPause(song);
        }
    }

    public int getCurrent() {
        return current;
    }

    private void onStop(Song song) {
        for (Callback callback : callbacks) {
            callback.onStop(song);
        }
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            stopTimer();
            Song song = playList.get(current);
            onStop(song);
        }
    }

    public void addSong(Song song) {
        playList.add(song);
    }

    public void removeSong(Song song) {
        playList.remove(song);
    }

    public List<Song> getPlayList() {
        return playList;
    }
}
