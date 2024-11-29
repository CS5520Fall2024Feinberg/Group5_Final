package edu.northeastern.group5_final;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.northeastern.group5_final.adapters.ArtistAdapter;
import edu.northeastern.group5_final.adapters.RequestAdapter;
import edu.northeastern.group5_final.models.Artist;
import edu.northeastern.group5_final.models.Request;
import edu.northeastern.group5_final.models.Song;

public class CollabFragment extends Fragment {

    List<Artist> artistList;
    List<Request> requestList;
    Button toggleBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collabs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populate();
        populateRequests();

        RecyclerView artistsRecyclerView = view.findViewById(R.id.artists_list_recycler_view);
        artistsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ArtistAdapter artistAdapter = new ArtistAdapter(requireContext(), artistList);
        artistsRecyclerView.setAdapter(artistAdapter);
        artistsRecyclerView.setVisibility(View.VISIBLE);

        RecyclerView requestsRecyclerView = view.findViewById(R.id.request_list_recycler_view);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        RequestAdapter requestAdapter = new RequestAdapter(requireContext(), requestList);
        requestsRecyclerView.setAdapter(requestAdapter);
        requestsRecyclerView.setVisibility(View.GONE);

        toggleBtn = view.findViewById(R.id.toggle_btn);
        toggleBtn.setOnClickListener(v -> {

            if (toggleBtn.getText().equals("My Collab Requests")) {
                artistsRecyclerView.setVisibility(View.GONE);
                requestsRecyclerView.setVisibility(View.VISIBLE);
                toggleBtn.setText("Show Artists");
            }else {
                artistsRecyclerView.setVisibility(View.VISIBLE);
                requestsRecyclerView.setVisibility(View.GONE);
                toggleBtn.setText("My Collab Requests");
            }
        });

    }

    private void populate() {
        artistList = new ArrayList<>();
        artistList.add(new Artist(
                "John Doe",
                "Jan 2020",
                Arrays.asList("Song A", "Song B"),
                null,
                10,
                "john_doe",
                "Music lover and aspiring artist."
        ));
        artistList.add(new Artist(
                "Jane Smith",
                "Feb 2019",
                Arrays.asList("Song X", "Song Y", "Song Z"),
                Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.single_artist_icon),
                15,
                "jane_smith",
                "Award-winning singer and songwriter."
        ));
        artistList.add(new Artist(
                "One Direction",
                "June 2002",
                Arrays.asList("Song X", "Song Y", "Song Z"),
                Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.artists_group_icon),
                150,
                "one_D",
                "Award-winning band and awesome gang!"
        ));

    }


    private void populateRequests() {
        requestList = new ArrayList<>();
        requestList.add(new Request("user1", "Hey! Let's form a band.", "The Rockers", null));
        requestList.add(new Request("user2", "Looking forward to collaborating.", "Sufi Stars",
                Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.single_artist_icon)));
        requestList.add(new Request("user3", "Would love to jam together!", "Country Vibes", null));


    }

}