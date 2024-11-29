package edu.northeastern.group5_final.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

        holder.sendRequestButton.setOnClickListener(v -> {
            Toast.makeText(context, "Request sent to " + artist.getName(), Toast.LENGTH_SHORT).show();
        });
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
}
