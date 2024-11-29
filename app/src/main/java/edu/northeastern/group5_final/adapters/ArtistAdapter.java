package edu.northeastern.group5_final.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.group5_final.R;
import edu.northeastern.group5_final.models.Artist;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private final Context context;
    private final List<Artist> artistList;

    public ArtistAdapter(Context context, List<Artist> artistList) {
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_recycler_view_row, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = artistList.get(position);

        holder.artistName.setText(artist.getName());
        holder.albumsReleased.setText("Albums: " + artist.getTotalSongsReleased());
        holder.joinDate.setText("Joined: " + artist.getJoinedDate());

        if (artist.getProfilePicture() != null) {
            holder.artistPicture.setImageURI(artist.getProfilePicture());
        } else {
            holder.artistPicture.setImageResource(R.drawable.single_artist_icon);
        }

        switch (artist.getStatus()) {
            case PLUS:
                holder.sendRequestButton.setBackgroundResource(R.drawable.send_req_icon3);
                break;
            case WAITING:
                holder.sendRequestButton.setBackgroundResource(R.drawable.waiting_icon);
                break;
            case DONE:
                holder.sendRequestButton.setBackgroundResource(R.drawable.done_icon);
                break;
        }


        holder.sendRequestButton.setOnClickListener(v -> {
            if (artist.getStatus() == Artist.Status.PLUS) {
                showRequestDialog(artist, position);
            }
        });

        View.OnClickListener profileClickListener = v -> showArtistProfileDialog(artist);
        holder.artistPicture.setOnClickListener(profileClickListener);
        holder.artistName.setOnClickListener(profileClickListener);


    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {
        ImageView artistPicture;
        TextView artistName, albumsReleased, joinDate;
        ImageButton sendRequestButton;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            artistPicture = itemView.findViewById(R.id.artist_picture);
            artistName = itemView.findViewById(R.id.artist_name);
            albumsReleased = itemView.findViewById(R.id.albums_released);
            joinDate = itemView.findViewById(R.id.join_date);
            sendRequestButton = itemView.findViewById(R.id.send_request_button);
        }
    }

    private void showRequestDialog(Artist artist, int position) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_send_request, null);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();

        EditText etSuggestedBandName = dialogView.findViewById(R.id.et_suggested_band_name);
        EditText etSubject = dialogView.findViewById(R.id.et_subject);
        EditText etContent = dialogView.findViewById(R.id.et_content);
        Button btnSendRequest = dialogView.findViewById(R.id.btn_send_request);
        Button btnDismiss = dialogView.findViewById(R.id.btn_dismiss);

        btnSendRequest.setOnClickListener(v -> {
            String bandName = etSuggestedBandName.getText().toString().trim();
            String subject = etSubject.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (!bandName.isEmpty() && !subject.isEmpty() && !content.isEmpty()) {
                artist.setStatus(Artist.Status.WAITING);
                notifyItemChanged(position);
                Toast.makeText(context, "Request Sent:\nBand: " + bandName + "\nSubject: " + subject, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                // TODO Remove this later
                new android.os.Handler().postDelayed(() -> {
                    boolean isAccepted = Math.random() > 0.1;
                    if (isAccepted) {
                        artist.setStatus(Artist.Status.DONE);
                    } else {
                        artist.setStatus(Artist.Status.PLUS);
                    }
                    notifyItemChanged(position);
                }, 1000);


            } else {
                Toast.makeText(context, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            }
        });

        btnDismiss.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void showArtistProfileDialog(Artist artist) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_artist_profile, null);
        AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogView).create();

        ImageView dialogArtistPicture = dialogView.findViewById(R.id.dialog_artist_picture);
        TextView dialogArtistUsername = dialogView.findViewById(R.id.dialog_artist_username);
        TextView dialogArtistName = dialogView.findViewById(R.id.dialog_artist_name);
        TextView dialogArtistBio = dialogView.findViewById(R.id.dialog_artist_bio);
        TextView dialogArtistJoinedDate = dialogView.findViewById(R.id.dialog_artist_joined_date);
        RecyclerView dialogArtistSongsRecyclerView = dialogView.findViewById(R.id.dialog_artist_songs_recycler_view);

        if (artist.getProfilePicture() != null) {
            dialogArtistPicture.setImageURI(artist.getProfilePicture());
        } else {
            dialogArtistPicture.setImageResource(R.drawable.single_artist_icon);
        }
        dialogArtistUsername.setText("@" + artist.getUsername());
        dialogArtistName.setText(artist.getName());
        dialogArtistBio.setText(artist.getBio());
        dialogArtistJoinedDate.setText("Joined: " + artist.getJoinedDate());

        dialogArtistSongsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ProfileSongsAdapter songsAdapter = new ProfileSongsAdapter(context, artist.getSongNames());
        dialogArtistSongsRecyclerView.setAdapter(songsAdapter);

        dialog.show();
    }
}
