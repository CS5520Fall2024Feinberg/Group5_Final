package edu.northeastern.group5_final;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.group5_final.models.Song;

public class PlayActivity extends AppCompatActivity implements MyMediaPlayer.Callback {

    TextView text_view_name;
    TextView text_view_artist;
    SeekBar seek_bar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        MyMediaPlayer.getInstance(this).setCallback(this);

        findViewById(R.id.play_last_btn).setOnClickListener(v -> {
            MyMediaPlayer.getInstance(this).prefix();
        });

        findViewById(R.id.quit_btn).setOnClickListener(v -> {
            finish();
        });

        text_view_name = findViewById(R.id.text_view_name);
        text_view_artist = findViewById(R.id.text_view_artist);
        seek_bar = findViewById(R.id.seek_bar);

        findViewById(R.id.play_next_btn).setOnClickListener(v -> {
            MyMediaPlayer.getInstance(this).next();
        });

        findViewById(R.id.play_stop_btn).setOnClickListener(v -> {
            MyMediaPlayer.getInstance(this).stop();
        });

        View play_or_pause_btn = findViewById(R.id.play_or_pause_btn);
        play_or_pause_btn.setOnClickListener(v -> {
            boolean playing = MyMediaPlayer.getInstance(this).isPlaying();
            if (playing) {
                MyMediaPlayer.getInstance(this).pause();
                play_or_pause_btn.setBackgroundResource(R.drawable.baseline_pause_24);
            } else {
                play_or_pause_btn.setBackgroundResource(R.drawable.baseline_play_arrow_24);
                MyMediaPlayer.getInstance(this).play();
            }
        });

    }

    @Override
    public void onProgress(int progress) {
        seek_bar.setProgress(progress);
    }

    @Override
    public void onSongChange(Song song) {
        text_view_name.setText(song.getTitle());
        text_view_artist.setText(song.getArtist());
    }
}
