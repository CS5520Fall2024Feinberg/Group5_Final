package edu.northeastern.group5_final.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.northeastern.group5_final.R;
import edu.northeastern.group5_final.models.FavoriteSong;

public class FavoritesListAdapter extends RecyclerView.Adapter<FavoritesListAdapter.FavoriteViewHolder> {

    private Context context;
    private List<FavoriteSong> favoriteSongs;

    public FavoritesListAdapter(Context context, List<FavoriteSong> favoriteSongs) {
        this.context = context;
        this.favoriteSongs = favoriteSongs;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_song, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteSong song = favoriteSongs.get(position);

        holder.title.setText(song.getTitle());
        holder.artistNames.setText(song.getArtistNames());
        holder.releaseDate.setText(song.getReleaseDate());
    }

    @Override
    public int getItemCount() {
        return favoriteSongs.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView artistNames;
        TextView releaseDate;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.favorite_song_title);
            title.setSelected(true);
            
            artistNames = itemView.findViewById(R.id.favorite_song_artist_names);
            releaseDate = itemView.findViewById(R.id.favorite_song_release_date);
        }
    }
}
