package edu.northeastern.group5_final.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.group5_final.R;
import edu.northeastern.group5_final.models.Artist;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private final Context context;
    private final List<Artist> artistList;
    private final Map<String, Artist.Status> localStatusMap;

    public ArtistAdapter(Context context, List<Artist> artistList, Map<String, Artist.Status> localStatusMap) {
        this.context = context;
        this.artistList = artistList;
        this.localStatusMap = localStatusMap;
    }

    private void updateStatus(String username, Artist.Status status) {
        localStatusMap.put(username, status);
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

        //not sure if we need this
        /*
        if (artist.getProfilePicture() != null) {
            holder.artistPicture.setImageURI(artist.getProfilePicture());
        } else {
            holder.artistPicture.setImageResource(artist.getIsIndividual() ? R.drawable.single_artist_icon : R.drawable.artists_group_icon);
        }

         */

        //setting profile picture
        //String Url = "gs://cs5520-group5-final.firebasestorage.app/profile_pictures/" + artist.getUsername() + ".jpg";
        if (artist.getProfilePicture() != null) {
            String Url = artist.getProfilePicture().toString();
            //Log.e("URL", "URL of image: : " + Url2);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl(Url);
            storageReference.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(context)
                                .load(uri.toString())
                                .placeholder(R.drawable.single_artist_icon)
                                .error(R.drawable.single_artist_icon)           // Fallback image
                                .circleCrop()                                   // Make the image circular
                                .into(holder.artistPicture);
                    })
                    .addOnFailureListener(e -> {//cant find image
                        holder.artistPicture.setImageResource(R.drawable.single_artist_icon); // Fallback image
                    });
        }
        else {//when they dont have a profile picture
            //Log.e("URL", "URL of image is null: " + artist.getUsername());
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
                addRequestToFirebase(artist, bandName, subject, content, position);
                Toast.makeText(context, "Request Sent:\nBand: " + bandName + "\nSubject: " + subject, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            } else {
                Toast.makeText(context, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            }
        });

        btnDismiss.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addRequestToFirebase(Artist artist, String bandName, String subject, String content, int position) {
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("requests");
        DatabaseReference artistRef = FirebaseDatabase.getInstance().getReference("artists");
        String requestorUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String recipientUsername = artist.getUsername();
        String requestId = requestsRef.push().getKey();

        Map<String, Object> request = new HashMap<>();
        request.put("requestorUsername", requestorUsername);
        request.put("recipientUsername", recipientUsername);
        request.put("status", "WAITING");
        request.put("bandName", bandName);
        request.put("subject", subject);
        request.put("message", content);
        request.put("timestamp", ServerValue.TIMESTAMP);

        requestsRef.child(requestId).setValue(request)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, "Request sent successfully", Toast.LENGTH_SHORT).show();
                    updateStatus(recipientUsername, Artist.Status.WAITING);

                    artistRef.orderByChild("username").equalTo(requestorUsername)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                            userSnapshot.getRef()
                                                    .child("requestsSent")
                                                    .child(recipientUsername)
                                                    .setValue("WAITING");
                                            break;
                                        }
                                    } else {
                                        Log.e("FirebaseError", "User not found for username: " + requestorUsername);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("FirebaseError", "Error finding user by username: " + error.getMessage());
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to send request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    artist.setStatus(Artist.Status.PLUS);
                    notifyItemChanged(position);
                });
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


        //setting profile picture
        if (artist.getProfilePicture() != null) {
            String Url = artist.getProfilePicture().toString();
            //Log.e("URL", "URL of image: : " + Url2);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl(Url);
            storageReference.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(context)
                                .load(uri.toString())
                                .placeholder(R.drawable.single_artist_icon)
                                .error(R.drawable.single_artist_icon)           // Fallback image
                                .circleCrop()                                   // Make the image circular
                                .into(dialogArtistPicture);
                    })
                    .addOnFailureListener(e -> {//cant find image
                        dialogArtistPicture.setImageResource(R.drawable.single_artist_icon); // Fallback image
                    });
        }
        else {//when they dont have a profile picture
            //Log.e("URL", "URL of image is null: " + artist.getUsername());
            dialogArtistPicture.setImageResource(R.drawable.single_artist_icon);
        }

        /*
        if (artist.getProfilePicture() != null) {
            dialogArtistPicture.setImageURI(artist.getProfilePicture());
        } else {
            dialogArtistPicture.setImageResource(R.drawable.single_artist_icon);
        }

         */


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
