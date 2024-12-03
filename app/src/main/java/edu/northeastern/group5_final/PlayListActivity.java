package edu.northeastern.group5_final;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.group5_final.adapters.PlayListAdapter;
import edu.northeastern.group5_final.models.Song;

public class PlayListActivity extends AppCompatActivity implements MyMediaPlayer.Callback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        MyMediaPlayer.getInstance(this).addCallback(this);
        findViewById(R.id.clean).setOnClickListener(v -> {
            MyMediaPlayer.getInstance(this).stop();
            MyMediaPlayer.getInstance(this).getPlayList().clear();
            List<Song> playList = MyMediaPlayer.getInstance(this).getPlayList();
            RecyclerView playListView = findViewById(R.id.playList);
            playListView.setAdapter(new PlayListAdapter(this, playList));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Song> playList = MyMediaPlayer.getInstance(this).getPlayList();
        RecyclerView playListView = findViewById(R.id.playList);
        playListView.setAdapter(new PlayListAdapter(this, playList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyMediaPlayer.getInstance(this).removeCallback(this);
    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onPlay(Song song) {
        List<Song> playList = MyMediaPlayer.getInstance(this).getPlayList();
        RecyclerView playListView = findViewById(R.id.playList);
        playListView.setAdapter(new PlayListAdapter(this, playList));
    }

    @Override
    public void onPause(Song song) {

    }

    @Override
    public void onStop(Song song) {

    }
}
