package edu.northeastern.group5_final;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.group5_final.adapters.PlayListAdapter;
import edu.northeastern.group5_final.models.Song;

public class PlayListActivity extends AppCompatActivity implements MyMediaPlayer.Callback {

    TextView emptyMsg;
    private PlayListAdapter adapter;
    private List<Song> playList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        MyMediaPlayer.getInstance(this).addCallback(this);

        emptyMsg = findViewById(R.id.no_list_msg);
        RecyclerView playListView = findViewById(R.id.playList);

        playList = MyMediaPlayer.getInstance(this).getPlayList();

        adapter = new PlayListAdapter(this, playList, (song, position) -> {
            // Remove the song from the playlist
            playList.remove(position);
            MyMediaPlayer.getInstance(this).renewList(playList); // Use renewList to update playlist
            adapter.notifyItemRemoved(position);

            // Update UI if playlist is empty
            if (playList.isEmpty()) {
                playListView.setVisibility(View.GONE);
                emptyMsg.setVisibility(View.VISIBLE);
            }
        });

        if (playList.isEmpty()) {
            playListView.setVisibility(View.GONE);
            emptyMsg.setVisibility(View.VISIBLE);
        } else {
            playListView.setVisibility(View.VISIBLE);
            emptyMsg.setVisibility(View.GONE);
        }

        playListView.setAdapter(adapter);
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
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause(Song song) {
    }

    @Override
    public void onStop(Song song) {
    }
}