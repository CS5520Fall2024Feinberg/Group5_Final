package edu.northeastern.group5_final;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import edu.northeastern.group5_final.models.Song;

public class PlayListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        List<Song> playList = MyMediaPlayer.getInstance(this).getPlayList();

    }
}
