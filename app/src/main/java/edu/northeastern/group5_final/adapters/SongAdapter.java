package edu.northeastern.group5_final.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import edu.northeastern.group5_final.R;
import edu.northeastern.group5_final.models.Song;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final List<Song> songs;
    private final Context context;
    private int currentlyPlayingIndex = -1;

    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song_card, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int p) {
        int position = holder.getAdapterPosition();
        Song song = songs.get(position);

        if (currentlyPlayingIndex == -1 && song.isPlaying()) {
            currentlyPlayingIndex = position;
        }

        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());
        holder.songGenre.setText(song.getGenre());
        holder.progressBar.setProgress(song.getProgress());

        holder.playPauseButton.setImageResource(song.isPlaying() ? R.drawable.pauset : R.drawable.playt);
        holder.favoriteButton.setImageResource(song.isFavorite() ? R.drawable.heartfilled48 : R.drawable.heartunfilled48);
        holder.progressBar.setVisibility(song.isPlaying() ? View.VISIBLE : View.GONE);

        holder.playPauseButton.setOnClickListener(v -> {
            if (song.isPlaying()) {
                song.setPlaying(false);
                currentlyPlayingIndex = -1;
            } else {
                if (currentlyPlayingIndex != -1 && currentlyPlayingIndex != position) {
                    songs.get(currentlyPlayingIndex).setPlaying(false);
                    notifyItemChanged(currentlyPlayingIndex);
                }
                song.setPlaying(true);
                currentlyPlayingIndex = position;
            }
            notifyItemChanged(position);
        });


        holder.favoriteButton.setOnClickListener(v -> {
            song.setFavorite(!song.isFavorite());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle, songArtist, songGenre;
        ImageButton playPauseButton, favoriteButton;
        ProgressBar progressBar;
        MaterialCardView card;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            songGenre = itemView.findViewById(R.id.song_genre);
            playPauseButton = itemView.findViewById(R.id.btn_play_pause);
            favoriteButton = itemView.findViewById(R.id.btn_favorite);
            progressBar = itemView.findViewById(R.id.song_progress_bar);
        }
    }


}
