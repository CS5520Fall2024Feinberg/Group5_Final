package edu.northeastern.group5_final.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import edu.northeastern.group5_final.R;
import edu.northeastern.group5_final.models.Artist;
import edu.northeastern.group5_final.models.Request;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private final Context context;
    private final List<Request> requestList;

    public RequestAdapter(Context context, List<Request> requestList) {
        this.context = context;
        this.requestList = requestList;
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

        if (request.getProfilePicture() != null) {
            holder.requesteePicture.setImageURI(request.getProfilePicture());
        } else {
            holder.requesteePicture.setImageResource(request.getRequestor().getIsIndividual() ?  R.drawable.single_artist_icon : R.drawable.artists_group_icon);
        }

        holder.requesteeUsername.setText(request.getRequestorUsername());
        holder.suggestedBandName.setText("Suggested Band: " + request.getSuggestedBandName());
        holder.requestMessage.setText(request.getMessage());

        holder.acceptRequestButton.setOnClickListener(v -> {
            requestList.remove(position);
            notifyItemRemoved(position);
        });

        holder.rejectRequestButton.setOnClickListener(v -> {
            requestList.remove(position);
            notifyItemRemoved(position);
        });

        Artist artist = request.getRequestor();
        View.OnClickListener profileClickListener = v -> showArtistProfileDialog(artist);
        holder.requesteePicture.setOnClickListener(profileClickListener);
        holder.requesteeUsername.setOnClickListener(profileClickListener);

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
}
