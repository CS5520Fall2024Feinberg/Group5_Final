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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group5_final.adapters.SongAdapter;
import edu.northeastern.group5_final.models.Song;


public class HomeFragment extends Fragment {

    List<Song> songList = new ArrayList<>();
    private static final int PICK_AUDIO_REQUEST = 1;
    SongAdapter adapter;
    private AlertDialog dialog;
    private Uri selectedFileUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.song_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dummy song data
        songList.add(new Song("Song 1", "Artist 1", "rock", false, false, 0, R.raw.sample_song2));
        songList.add(new Song("Song 2", "Artist 2", "countryside", false, true, 0, R.raw.sample_song3));
        songList.add(new Song("Song 3", "Artist 3", "relaxing", false, false, 0, R.raw.sample_song));

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
                String artistName = getArtistName(selectedFileUri);

                EditText editSongTitle = dialog.findViewById(R.id.edit_song_title);
                EditText editArtistName = dialog.findViewById(R.id.edit_artist_name);
                Spinner genreSpinner = dialog.findViewById(R.id.genre_spinner);
                Button confirmAddButton = dialog.findViewById(R.id.confirm_add_btn);

                if (editSongTitle != null && editArtistName != null && confirmAddButton != null) {
                    editSongTitle.setVisibility(View.VISIBLE);
                    editArtistName.setVisibility(View.VISIBLE);
                    confirmAddButton.setVisibility(View.VISIBLE);
                    genreSpinner.setVisibility(View.VISIBLE);

                    editSongTitle.setText(songTitle);
                    editArtistName.setText(artistName);
                }
            }
        }
    }


    private void openAddSongDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_file_picker, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        Button openFilePickerButton = dialogView.findViewById(R.id.open_file_picker_btn);
        EditText editSongTitle = dialogView.findViewById(R.id.edit_song_title);
        EditText editArtistName = dialogView.findViewById(R.id.edit_artist_name);
        Spinner genreSpinner = dialogView.findViewById(R.id.genre_spinner);
        Button confirmAddButton = dialogView.findViewById(R.id.confirm_add_btn);

        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"Rock", "Sufi", "Countryside"});
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(genreAdapter);

        openFilePickerButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select a Song"), PICK_AUDIO_REQUEST);

            this.dialog = dialog;
        });

        confirmAddButton.setOnClickListener(v -> {

            if (selectedFileUri != null) {

                String songTitle = editSongTitle.getText().toString().trim();
                String artistName = editArtistName.getText().toString().trim();
                String selectedGenre = genreSpinner.getSelectedItem().toString();

                if (!songTitle.isEmpty() && !artistName.isEmpty() && !selectedGenre.isEmpty()) {
                    Song newSong = new Song(songTitle, artistName, selectedGenre, false, false, 0, R.raw.sample_song); //selectedFileUri[0].toString()
                    songList.add(newSong);
                    adapter.notifyItemInserted(songList.size() - 1);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }


            }
        });

        dialog.show();
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

}



