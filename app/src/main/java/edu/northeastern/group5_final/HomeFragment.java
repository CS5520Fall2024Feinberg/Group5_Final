package edu.northeastern.group5_final;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group5_final.adapters.SongAdapter;
import edu.northeastern.group5_final.models.Song;


public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.song_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dummy song data
        List<Song> songList = new ArrayList<>();
        songList.add(new Song("Song 1", "Artist 1", "rock", false, false, 0, R.raw.sample_song2));
        songList.add(new Song("Song 2", "Artist 2", "countryside", false, true, 0, R.raw.sample_song3));
        songList.add(new Song("Song 3", "Artist 3", "relaxing", false, false, 0, R.raw.sample_song));

        SongAdapter adapter = new SongAdapter(getContext(), songList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private String generateSongURIPath(int songId) {

        return "android.resource://" + getContext().getPackageName() + "/" + songId;

    }
}



