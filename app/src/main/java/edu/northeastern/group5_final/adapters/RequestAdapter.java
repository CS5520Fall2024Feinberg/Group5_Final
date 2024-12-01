package edu.northeastern.group5_final.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.northeastern.group5_final.CollabFragment;
import edu.northeastern.group5_final.R;
import edu.northeastern.group5_final.models.Artist;
import edu.northeastern.group5_final.models.Request;
import edu.northeastern.group5_final.models.RequestDBModel;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private final Context context;
    private List<Request> requestList;
    private CollabFragment collabFragment;

    public RequestAdapter(Context context, List<Request> requestList, CollabFragment collabFragment) {
        this.context = context;
        this.requestList = requestList;
        this.collabFragment = collabFragment;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request_row, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);
        Artist artist = request.getRequestor();

        if (request.getProfilePicture() != null) {
            holder.requesteePicture.setImageURI(request.getProfilePicture());
        } else {
            holder.requesteePicture.setImageResource(request.getRequestor().getIsIndividual() ?  R.drawable.single_artist_icon : R.drawable.artists_group_icon);
        }

        holder.requesteeUsername.setText(request.getRequestorUsername());
        holder.suggestedBandName.setText("Suggested Band: " + request.getSuggestedBandName());
        holder.requestMessage.setText(request.getMessage());

        holder.acceptRequestButton.setOnClickListener(v -> acceptListener(artist, position));
        holder.rejectRequestButton.setOnClickListener(v -> rejectListener(artist, position));

        View.OnClickListener profileClickListener = v -> showArtistProfileDialog(artist);
        holder.requesteePicture.setOnClickListener(profileClickListener);
        holder.requesteeUsername.setOnClickListener(profileClickListener);

    }

    private void acceptListener(Artist requestor, int position) {

        if (position < requestList.size()) {
            requestList.remove(position);
            notifyItemRemoved(position);
        }

        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("requests");
        DatabaseReference artistsRef = FirebaseDatabase.getInstance().getReference("artists");
        String currentUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String requestorUsername = requestor.getUsername();

        requestsRef.orderByChild("recipientUsername").equalTo(currentUsername)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            RequestDBModel request = requestSnapshot.getValue(RequestDBModel.class);

                            if (request != null && request.getRequestorUsername().equals(requestorUsername)) {
                                requestSnapshot.getRef().child("status").setValue("DONE")
                                        .addOnSuccessListener(unused -> {
                                            artistsRef.orderByChild("username").equalTo(requestorUsername)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot artistSnapshot) {
                                                            if (artistSnapshot.exists()) {
                                                                for (DataSnapshot artist : artistSnapshot.getChildren()) {
                                                                    artist.getRef().child("requestsSent")
                                                                            .child(currentUsername)
                                                                            .setValue("DONE");
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Log.e("FirebaseError", "Error updating requestsSent: " + error.getMessage());
                                                        }
                                                    });
                                            Toast.makeText(context, "Request accepted successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("FirebaseError", "Failed to update request status: " + e.getMessage());
                                            Toast.makeText(context, "Failed to accept request", Toast.LENGTH_SHORT).show();
                                        });
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Error finding request: " + error.getMessage());
                    }
                });
    }

    private void rejectListener(Artist requestor, int position) {
        requestList.remove(position);
        notifyItemRemoved(position);

        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("requests");
        DatabaseReference artistsRef = FirebaseDatabase.getInstance().getReference("artists");
        String currentUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String requestorUsername = requestor.getUsername();

        requestsRef.orderByChild("recipientUsername").equalTo(currentUsername)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            RequestDBModel request = requestSnapshot.getValue(RequestDBModel.class);

                            if (request != null && request.getRequestorUsername().equals(requestorUsername)) {
                                requestSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(unused -> {
                                            artistsRef.orderByChild("username").equalTo(requestorUsername)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot artistSnapshot) {
                                                            if (artistSnapshot.exists()) {
                                                                for (DataSnapshot artist : artistSnapshot.getChildren()) {
                                                                    artist.getRef().child("requestsSent")
                                                                            .child(currentUsername)
                                                                            .removeValue();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Log.e("FirebaseError", "Error removing from requestsSent: " + error.getMessage());
                                                        }
                                                    });
                                            Toast.makeText(context, "Request rejected successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("FirebaseError", "Failed to delete request: " + e.getMessage());
                                            Toast.makeText(context, "Failed to reject request", Toast.LENGTH_SHORT).show();
                                        });
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Error finding request: " + error.getMessage());
                    }
                });
    }


    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        ImageView requesteePicture;
        TextView requesteeUsername, suggestedBandName, requestMessage;
        ImageButton acceptRequestButton, rejectRequestButton;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requesteePicture = itemView.findViewById(R.id.iv_requestee_picture);
            requesteeUsername = itemView.findViewById(R.id.tv_requestee_username);
            suggestedBandName = itemView.findViewById(R.id.tv_suggested_band_name);
            requestMessage = itemView.findViewById(R.id.tv_request_message);
            acceptRequestButton = itemView.findViewById(R.id.btn_accept_request);
            rejectRequestButton = itemView.findViewById(R.id.btn_reject_request);
        }
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

    public void updateList(List<Request> newRequestList) {
        this.requestList = newRequestList;
        notifyDataSetChanged();
    }

}
