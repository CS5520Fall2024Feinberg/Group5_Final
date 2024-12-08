package edu.northeastern.group5_final;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import edu.northeastern.group5_final.models.Song;

public class PlayActivity extends AppCompatActivity implements MyMediaPlayer.Callback {

    private TextView text_view_name;
    private TextView text_view_artist;
    private SeekBar seek_bar;
    private View play_or_pause_btn;
    private ImageView siv_icon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        MyMediaPlayer.getInstance(this).addCallback(this);

        findViewById(R.id.play_last_btn).setOnClickListener(v -> {
            MyMediaPlayer.getInstance(this).prefix();
        });

        findViewById(R.id.quit_btn).setOnClickListener(v -> {
            finish();
        });

        text_view_name = findViewById(R.id.text_view_name);
        text_view_artist = findViewById(R.id.text_view_artist);
        seek_bar = findViewById(R.id.seek_bar);
        siv_icon = findViewById(R.id.siv_icon);

        findViewById(R.id.play_next_btn).setOnClickListener(v -> {
            MyMediaPlayer.getInstance(this).next();
        });

        play_or_pause_btn = findViewById(R.id.play_or_pause_btn);

        try {
            List<Song> playList = MyMediaPlayer.getInstance(this).getPlayList();
            Song song = playList.get(MyMediaPlayer.getInstance(this).getCurrent());
            setImage(song);
            text_view_name.setText(song.getTitle());
            text_view_artist.setText(song.getArtist());
        } catch (Exception e) {
            e.printStackTrace();
        }
        play_or_pause_btn.setBackgroundResource(R.drawable.baseline_pause_24);

        play_or_pause_btn.setOnClickListener(v -> {
            boolean playing = MyMediaPlayer.getInstance(this).isPlaying();
            if (playing) {
                MyMediaPlayer.getInstance(this).pause();
                play_or_pause_btn.setBackgroundResource(R.drawable.baseline_play_arrow_24);
            } else {
                play_or_pause_btn.setBackgroundResource(R.drawable.baseline_pause_24);
                MyMediaPlayer.getInstance(this).play();
            }
        });

    }

    @Override
    public void onProgress(int progress) {
        seek_bar.setProgress(progress);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyMediaPlayer.getInstance(this).removeCallback(this);
    }

    @Override
    public void onPlay(Song song) {
        setImage(song);
        text_view_name.setText(song.getTitle());
        text_view_artist.setText(song.getArtist());
        play_or_pause_btn.setBackgroundResource(R.drawable.baseline_pause_24);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean playing = MyMediaPlayer.getInstance(this).isPlaying();
        if (playing) {
            play_or_pause_btn.setBackgroundResource(R.drawable.baseline_pause_24);
        } else {
            play_or_pause_btn.setBackgroundResource(R.drawable.baseline_play_arrow_24);
        }
    }

    @Override
    public void onPause(Song song) {
        play_or_pause_btn.setBackgroundResource(R.drawable.baseline_play_arrow_24);
    }

    @Override
    public void onStop(Song song) {
        play_or_pause_btn.setBackgroundResource(R.drawable.baseline_play_arrow_24);
    }

    private void setImage(Song song){
        String imageName = "_" + song.getTitle().length() % 10;
        int resourceId = siv_icon.getContext().getResources().getIdentifier(imageName, "drawable", siv_icon.getContext().getPackageName());
        siv_icon.setImageResource(resourceId);
    }
}
