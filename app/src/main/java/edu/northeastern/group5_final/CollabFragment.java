package edu.northeastern.group5_final;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.northeastern.group5_final.adapters.ArtistAdapter;
import edu.northeastern.group5_final.models.Artist;

public class CollabFragment extends Fragment {

    List<Artist> artistList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collabs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populate();

        RecyclerView recyclerView = view.findViewById(R.id.artists_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ArtistAdapter adapter = new ArtistAdapter(requireContext(), artistList);
        recyclerView.setAdapter(adapter);
    }

    private void populate() {
        artistList = new ArrayList<>();
        artistList.add(new Artist("John Doe", "Jan 2020", Arrays.asList("Song A", "Song B"), null,
                10));
        artistList.add(new Artist("One Direction", "June 1990", Arrays.asList("One", "Two", "Song Z"), Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.drawable.artists_group_icon), 150));
        artistList.add(new Artist("Jane Smith", "Feb 2019", Arrays.asList("Song X", "Song Y", "Song Z"), Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.drawable.single_artist_icon), 15));
    }

}