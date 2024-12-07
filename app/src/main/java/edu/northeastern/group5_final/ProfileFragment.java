package edu.northeastern.group5_final;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group5_final.adapters.CollaborationsListAdapter;
import edu.northeastern.group5_final.adapters.FavoritesListAdapter;
import edu.northeastern.group5_final.adapters.MySongsListAdapter;
import edu.northeastern.group5_final.models.Artist;
import edu.northeastern.group5_final.models.ArtistDBModel;
import edu.northeastern.group5_final.models.Collaboration;
import edu.northeastern.group5_final.models.FavoriteSong;
import edu.northeastern.group5_final.models.MySong;
import edu.northeastern.group5_final.models.RequestDBModel;
import edu.northeastern.group5_final.models.Song;
import edu.northeastern.group5_final.models.SongDBModel;

public class ProfileFragment extends Fragment {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private TextView username, fullname, joined;
    private ImageView profileImage;
    private RecyclerView favoritesListRV, collaborationsListRV, mySongsListRV;
    private FavoritesListAdapter favoritesAdapter;
    private CollaborationsListAdapter collaborationsAdapter;
    private MySongsListAdapter mySongsAdapter;
    private List<FavoriteSong> favoriteSongs;
    private List<Collaboration> collaborations;
    private List<MySong> mySongs;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        profileImage = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        fullname = view.findViewById(R.id.full_name);
        joined = view.findViewById(R.id.member_since);
        Toast.makeText(getContext(), "Logged In Successfully1", Toast.LENGTH_SHORT).show();
        fetchSelfUserData();

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Beat Connect");

        setHasOptionsMenu(true);

        favoriteSongs = new ArrayList<>();
        populateFavoriteSongs();
        Toast.makeText(getContext(), "Logged In Successfully2", Toast.LENGTH_SHORT).show();
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

        mySongs = new ArrayList<>();
        populateMySongs();
        Toast.makeText(getContext(), "Logged In Successfully3", Toast.LENGTH_SHORT).show();
        mySongsListRV = view.findViewById(R.id.my_songs_list);
        mySongsListRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mySongsAdapter = new MySongsListAdapter(getContext(), mySongs);
        mySongsListRV.setAdapter(mySongsAdapter);
        Toast.makeText(getContext(), "Logged In Successfully4", Toast.LENGTH_SHORT).show();

        return view;
    }

    private void populateMySongs() {

        String currentUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mySongs.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SongDBModel songdb = snapshot.getValue(SongDBModel.class);

                    if (songdb == null || songdb.getArtists() == null) continue;
                    if (!songdb.getArtists().contains(currentUsername)) continue;

                    mySongs.add(
                            new MySong(
                                songdb.getTitle(),
                                songdb.getReleaseDate(),
                                songdb.getGenre()
                            )
                    );
                }
                mySongsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch songs: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchSelfUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists");
        databaseReference.orderByChild("username").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArtistDBModel selfUser = dataSnapshot.getValue(ArtistDBModel.class);
                    if (selfUser != null) {
                        for (DataSnapshot selfSnapshot : dataSnapshot.getChildren()) {
                            selfUser = selfSnapshot.getValue(ArtistDBModel.class);
                            if (selfUser != null) break;
                        }
                        updateUI(selfUser);
                    } else {
                        Toast.makeText(getContext(), "Failed to parse user data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUI(ArtistDBModel user) {
        username.setText("@" + user.getUsername());
        fullname.setText(user.getName());
        joined.setText("Member since: " + user.getDateJoined());

        if (user.getProfilePictureUrl() != null) {
            Log.d("ProfileFragment", "Profile picture URL is not null:" + user.getProfilePictureUrl());
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(user.getProfilePictureUrl());
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.d("ProfileFragment", "Download URL: " + downloadUrl);

                Glide.with(getContext())
                        .load(downloadUrl)
                        .placeholder(R.drawable.single_artist_icon)
                        .error(R.drawable.single_artist_icon)
                        .circleCrop()
                        .into(profileImage);
            }).addOnFailureListener(exception -> {// incase failure with connecting to db storage
                Log.e("ProfileFragment", "Failed to get download URL", exception);

                Glide.with(getContext())
                        .load(R.drawable.single_artist_icon)
                        .placeholder(R.drawable.single_artist_icon)
                        .error(R.drawable.single_artist_icon)
                        .circleCrop()
                        .into(profileImage);
            });
        }
        else{// incase user does not have a profile picture
            Glide.with(getContext())
                    .load(R.drawable.single_artist_icon)
                    .placeholder(R.drawable.single_artist_icon)
                    .error(R.drawable.single_artist_icon)
                    .circleCrop()
                    .into(profileImage);
        }


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
