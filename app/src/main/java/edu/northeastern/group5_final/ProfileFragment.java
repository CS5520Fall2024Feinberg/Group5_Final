package edu.northeastern.group5_final;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import java.util.List;

import edu.northeastern.group5_final.adapters.CollaborationsListAdapter;
import edu.northeastern.group5_final.adapters.FavoritesListAdapter;
import edu.northeastern.group5_final.models.Collaboration;
import edu.northeastern.group5_final.models.FavoriteSong;
import edu.northeastern.group5_final.models.RequestDBModel;
import edu.northeastern.group5_final.models.Song;
import edu.northeastern.group5_final.models.SongDBModel;

public class ProfileFragment extends Fragment {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private RecyclerView favoritesListRV, collaborationsListRV;
    private FavoritesListAdapter favoritesAdapter;
    private CollaborationsListAdapter collaborationsAdapter;
    private List<FavoriteSong> favoriteSongs;
    private List<Collaboration> collaborations;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Beat Connect");

        setHasOptionsMenu(true);

        favoriteSongs = new ArrayList<>();
        populateFavoriteSongs();

        favoritesListRV = view.findViewById(R.id.favorites_list);
        favoritesListRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        favoritesAdapter = new FavoritesListAdapter(getContext(), favoriteSongs);
        favoritesListRV.setAdapter(favoritesAdapter);

        collaborations = new ArrayList<>();
        populateCollaborations();

        collaborationsListRV = view.findViewById(R.id.collabs_list);
        collaborationsListRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        collaborationsAdapter = new CollaborationsListAdapter(getContext(), collaborations);
        collaborationsListRV.setAdapter(collaborationsAdapter);


        return view;
    }

    private void populateFavoriteSongs() {
        String currentUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteSongs.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SongDBModel songdb = snapshot.getValue(SongDBModel.class);

                    if (songdb == null || songdb.getLikedBy() == null || !songdb.getLikedBy().contains(currentUsername)) continue;
                    favoriteSongs.add(
                            new FavoriteSong(
                                    songdb.getTitle(),
                                    String.join(", ", songdb.getArtists()),
                                    songdb.getReleaseDate()
                            )
                    );
                }
                favoritesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch songs: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateCollaborations() {
        collaborations.add(new Collaboration("Band A", "Artist X, Artist Y"));
        collaborations.add(new Collaboration("Band B", "Artist Z"));
        collaborations.add(new Collaboration("Band C", "Artist P, Artist Q"));



        String currentUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("requests");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                collaborations.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RequestDBModel request = snapshot.getValue(RequestDBModel.class);

                    if (request == null || request.getRequestorUsername() == null || request.getRecipientUsername() == null) continue;
                    if (!request.getRequestorUsername().equals(currentUsername) && !request.getRecipientUsername().equals(currentUsername)) continue;

                    collaborations.add(
                            new Collaboration(
                                    request.getBandName(),
                                    request.getRequestorUsername() + ", " + request.getRecipientUsername()
                            )
                    );
                }
                collaborationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch songs: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Toast.makeText(getActivity(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
        firebaseAuth.signOut();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
        return true;
    }
}
