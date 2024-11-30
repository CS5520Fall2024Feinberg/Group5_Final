package edu.northeastern.group5_final;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.group5_final.adapters.ArtistAdapter;
import edu.northeastern.group5_final.adapters.RequestAdapter;
import edu.northeastern.group5_final.models.Artist;
import edu.northeastern.group5_final.models.ArtistDBModel;
import edu.northeastern.group5_final.models.Request;
import edu.northeastern.group5_final.models.Song;

public class CollabFragment extends Fragment {

    List<Artist> artistList;
    List<Request> requestList;
    Button toggleBtn;
    TextView emptyMsg;

    RecyclerView artistsRecyclerView;
    RecyclerView requestsRecyclerView;
    ArtistAdapter artistAdapter;
    RequestAdapter requestAdapter;

    private ArtistDBModel selfUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collabs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateArtists();

        emptyMsg = view.findViewById(R.id.tv_empty_msg);

        artistsRecyclerView = view.findViewById(R.id.artists_list_recycler_view);
        artistsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        initializeArtistsAdapter();

        requestsRecyclerView = view.findViewById(R.id.request_list_recycler_view);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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

    private void initializeRequestAdapter() {
        requestAdapter = new RequestAdapter(requireContext(), requestList);
        requestsRecyclerView.setAdapter(requestAdapter);
    }

    private void initializeArtistsAdapter() {
        artistAdapter = new ArtistAdapter(requireContext(), artistList);
        artistsRecyclerView.setAdapter(artistAdapter);
    }

    private void populateArtists() {
        artistList = new ArrayList<>();

        DatabaseReference artistsRef = FirebaseDatabase.getInstance().getReference("artists");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserEmail = firebaseAuth.getCurrentUser().getEmail();

        artistsRef.orderByChild("email").equalTo(currentUserEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("Error", "Self user not found in artists");
                    return;
                }

                selfUser = null;
                for (DataSnapshot selfSnapshot : snapshot.getChildren()) {
                    selfUser = selfSnapshot.getValue(ArtistDBModel.class);
                    if (selfUser != null) break;
                }

                if (selfUser == null) {
                    Log.e("Error", "Self user data could not be retrieved");
                    return;
                }


                Map<String, String> sentRequests = new HashMap<>();
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot selfSnapshot : snapshot.getChildren()) {
                        if (selfSnapshot.hasChild("requestsSent")) {
                            for (DataSnapshot requestSnapshot : selfSnapshot.child("requestsSent").getChildren()) {
                                sentRequests.put(requestSnapshot.getKey(), requestSnapshot.getValue(String.class));
                            }
                        }
                    }
                }

                artistsRef.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        artistList.clear();

                        for (DataSnapshot artistSnapshot : snapshot.getChildren()) {
                            ArtistDBModel a = artistSnapshot.getValue(ArtistDBModel.class);
                            if (a != null && !a.getUsername().equals(selfUser.getUsername())) {

                                Artist.Status status = Artist.Status.PLUS;
                                if (a.getUsername() != null && sentRequests.containsKey(a.getUsername())) {
                                    status = Artist.Status.valueOf(sentRequests.get(a.getUsername()));
                                }

                                artistList.add(new Artist(
                                        a.getName(),
                                        a.getDateJoined(),
                                        Arrays.asList("Song A", "Song B"),
                                        a.getProfilePictureUrl() == null ? null : Uri.parse(a.getProfilePictureUrl()),
                                        status,
                                        a.getUsername(),
                                        a.getBio(),
                                        true
                                ));
                            }
                        }
                        artistAdapter.notifyDataSetChanged();
                        populateRequests();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Error fetching artists: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error fetching self user: " + error.getMessage());
            }
        });
    }


    private void populateRequests() {
        requestList = new ArrayList<>();
        requestList.add(new Request(artistList.get(0), artistList.get(0).getUsername(), "Hey! Let's form a band.", "The Rockers", artistList.get(0).getProfilePicture()));
        requestList.add(new Request(artistList.get(1), artistList.get(1).getUsername(), "Looking forward to collaborating. Looking forward to collaborating. Looking forward to collaborating. Looking forward to collaborating. Looking forward to collaborating. Looking forward to collaborating. Looking forward to collaborating. Looking forward to collaborating.", "Sufi Stars", artistList.get(1).getProfilePicture()));
        requestList.add(new Request(artistList.get(2), artistList.get(2).getUsername(), "Would love to jam together!", "Country Vibes", artistList.get(2).getProfilePicture()));

        initializeRequestAdapter();
    }
}