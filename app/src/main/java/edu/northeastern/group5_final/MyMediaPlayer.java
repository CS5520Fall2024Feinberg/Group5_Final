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
    private volatile int current;
    private Timer timer;
    private TimerTask timerTask;
    private Callback callback;
    private Handler handler;

    public interface Callback {
        void onProgress(int progress);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
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

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                int progress = (mediaPlayer.getCurrentPosition() * 100) / mediaPlayer.getDuration();
                handler.post(() -> callback.onProgress(progress));
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 1000, 1000);
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        try {
            Song song = playList.get(current);
            mediaPlayer = MediaPlayer.create(applicationContext, song.getSongId());
            mediaPlayer.start();
            startTimer();
            mediaPlayer.setOnCompletionListener(mp -> {

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void next() {
        if (current > playList.size()) {
            current = -1;
        }
        current += 1;
        play();
    }

    public void prefix() {
        if (current < 0) {
            current = playList.size();
        }
        current -= 1;
        play();
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            stopTimer();
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
