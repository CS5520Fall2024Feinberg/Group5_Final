package edu.northeastern.group5_final;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.northeastern.group5_final.adapters.SongAdapter;
import edu.northeastern.group5_final.models.RequestDBModel;
import edu.northeastern.group5_final.models.Song;
import edu.northeastern.group5_final.models.SongDBModel;


public class HomeFragment extends Fragment {

    List<Song> songList = new ArrayList<>();
    private static final int PICK_AUDIO_REQUEST = 1;
    SongAdapter adapter;
    private AlertDialog dialog;
    private Uri selectedFileUri;
    List<RequestDBModel> userRequests = new ArrayList<>();
    private RequestDBModel selectedArtist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        populateSongs();
        fetchCurrentUserCollabirations();

        RecyclerView recyclerView = view.findViewById(R.id.song_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SongAdapter(getContext(), songList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.add_song_btn);
        fab.setOnClickListener(v -> openAddSongDialog());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null && dialog != null) {
                String songTitle = getFileName(selectedFileUri);

                EditText editSongTitle = dialog.findViewById(R.id.edit_song_title);
                Spinner artistsSpinner = dialog.findViewById(R.id.artists_spinner);
                Spinner genreSpinner = dialog.findViewById(R.id.genre_spinner);
                Button confirmAddButton = dialog.findViewById(R.id.confirm_add_btn);

                if (editSongTitle != null && confirmAddButton != null) {
                    editSongTitle.setVisibility(View.VISIBLE);
                    artistsSpinner.setVisibility(View.VISIBLE);
                    confirmAddButton.setVisibility(View.VISIBLE);
                    genreSpinner.setVisibility(View.VISIBLE);

                    editSongTitle.setText(songTitle);
                }
            }
        }
    }


    private void openAddSongDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_file_picker, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        handleArtistSpinner(dialogView);
        handleGenreSpinner(dialogView);

        Button openFilePickerButton = dialogView.findViewById(R.id.open_file_picker_btn);
        openFilePickerButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select a Song"), PICK_AUDIO_REQUEST);
            this.dialog = dialog;
        });

        Button confirmAddButton = dialogView.findViewById(R.id.confirm_add_btn);
        confirmAddButton.setOnClickListener(v -> confirmAddSong(dialogView));
        dialog.show();
    }

    private void handleArtistSpinner(View dialogView) {
        List<RequestDBModel> options = new ArrayList<>();
        RequestDBModel r = new RequestDBModel();
        r.setBandName("SOLO");
        options.add(r);
        options.addAll(userRequests);

        Spinner artistsSpinner = dialogView.findViewById(R.id.artists_spinner);
        ArrayAdapter<RequestDBModel> artistsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, options);
        artistsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        artistsSpinner.setAdapter(artistsAdapter);

        artistsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RequestDBModel s = (RequestDBModel) artistsSpinner.getSelectedItem();
                Log.d("TAG", "onItemSelected: " + s.getBandName());
                if (s != null) { selectedArtist = s; }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void handleGenreSpinner(View dialogView) {
        Spinner genreSpinner = dialogView.findViewById(R.id.genre_spinner);
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Rock", "Sufi", "Countryside"});
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);
    }

    private void confirmAddSong(View dialogView) {

        EditText editSongTitle = dialogView.findViewById(R.id.edit_song_title);
        Spinner genreSpinner = dialogView.findViewById(R.id.genre_spinner);

        if (selectedFileUri != null) {

            String songTitle = editSongTitle.getText().toString().trim();
            String selectedGenre = genreSpinner.getSelectedItem().toString();

            if (!songTitle.isEmpty() && !selectedArtist.getBandName().isEmpty() && !selectedGenre.isEmpty()) {
                uploadSongToFirebase(songTitle, selectedGenre);
            } else {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadSongToFirebase(String songTitle, String selectedGenre) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference("songs");

        StorageReference songRef = storageReference.child(songTitle + "_" + System.currentTimeMillis());

        songRef.putFile(selectedFileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    songRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");
                        String songId = databaseReference.push().getKey();

                        String currentUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        List<String> artists = new ArrayList<>();
                        artists.add(currentUsername);

                        if (!selectedArtist.getBandName().equals("SOLO")) {
                            String otherArtistName = selectedArtist.getRecipientUsername().equals(currentUsername) ?
                                    selectedArtist.getRequestorUsername() :
                                    selectedArtist.getRecipientUsername();;
                            artists.add(otherArtistName);
                        }


                        SongDBModel newSong = new SongDBModel(
                                songId,
                                songTitle,
                                uri.toString(),
                                selectedGenre,
                                artists,
                                getCurrentDate(),
                                new ArrayList<>()
                        );
                        dialog.dismiss();


                        if (songId != null) {
                            databaseReference.child(songId).setValue(newSong)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Song added successfully to Realtime Database!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Failed to add song to Realtime Database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(getContext(), "Failed to generate song ID", Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(getContext(), "Song added successfully!", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to get song URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to upload song: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchCurrentUserCollabirations() {

        String currentUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("requests");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RequestDBModel request = snapshot.getValue(RequestDBModel.class);

                    if (request != null &&
                            "DONE".equals(request.getStatus()) &&
                            (currentUsername.equals(request.getRecipientUsername()) ||
                                    currentUsername.equals(request.getRequestorUsername()))) {

                        userRequests.add(request);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch requests: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateSongs() {
        String currentUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                songList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String songId = snapshot.getKey();
                    SongDBModel songdb = snapshot.getValue(SongDBModel.class);
                    if (songdb != null) {
                        songList.add(
                                new Song(
                                        songId,
                                        songdb.getTitle(),
                                        String.join(", ", songdb.getArtists()),
                                        songdb.getGenre(),
                                        false,
                                        songdb.getLikedBy() != null && songdb.getLikedBy().contains(currentUsername),
                                        0,
                                        songdb.getUrl(),
                                        songdb.getLikedBy() == null ? 0 : songdb.getLikedBy().size(),
                                        songdb.getReleaseDate()
                                )
                        );
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch songs: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        if (result == null) {
            return "Unknown Song Title";
        }
        return result;
    }

    private String getArtistName(Uri uri) {
        String artistName = "Unknown Artist";
        String[] projection = {MediaStore.Audio.Media.ARTIST};

        try (Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                artistName = cursor.getString(artistIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (artistName == null) {
            return "Unknown Artist";
        }
        return artistName;
    }

    private String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        return currentDate.format(formatter);
    }

}



