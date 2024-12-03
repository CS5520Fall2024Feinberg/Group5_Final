package edu.northeastern.group5_final;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.group5_final.adapters.PlayListAdapter;
import edu.northeastern.group5_final.models.Song;

public class PlayListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Song> playList = MyMediaPlayer.getInstance(this).getPlayList();
        RecyclerView playListView = findViewById(R.id.playList);
        playListView.setAdapter(new PlayListAdapter(this, playList));
    }
}
