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

import edu.northeastern.group5_final.adapters.FavoritesListAdapter;
import edu.northeastern.group5_final.models.FavoriteSong;
import edu.northeastern.group5_final.models.Song;
import edu.northeastern.group5_final.models.SongDBModel;

public class ProfileFragment extends Fragment {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private RecyclerView favoritesListRV;
    private FavoritesListAdapter adapter;
    private List<FavoriteSong> favoriteSongs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        favoriteSongs = new ArrayList<>();
        populateFavoriteSongs();

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Beat Connect");

        setHasOptionsMenu(true);

        favoritesListRV = view.findViewById(R.id.favorites_list);
        favoritesListRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        adapter = new FavoritesListAdapter(getContext(), favoriteSongs);
        favoritesListRV.setAdapter(adapter);

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
                adapter.notifyDataSetChanged();
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
