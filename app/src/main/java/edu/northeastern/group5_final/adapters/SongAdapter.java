package edu.northeastern.group5_final.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
    private MediaPlayer mediaPlayer;
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
                pauseSong(position);
            } else {
                playSong(position);
            }
        });


        holder.favoriteButton.setOnClickListener(v -> {
            song.setFavorite(!song.isFavorite());
            notifyItemChanged(position);
        });

        if (position == currentlyPlayingIndex) {
            updateProgressBar(holder);
        } else {
            holder.progressBar.setProgress(0);
        }
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

    private void playSong(int position) {
        if (currentlyPlayingIndex != -1 && currentlyPlayingIndex != position) {
            Song previousSong = songs.get(currentlyPlayingIndex);
            previousSong.setPlaying(false);
            notifyItemChanged(currentlyPlayingIndex);
        }

        Song song = songs.get(position);
        song.setPlaying(true);
        currentlyPlayingIndex = position;

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        try {
            mediaPlayer = MediaPlayer.create(context, song.getSongId());
            mediaPlayer.start();

            notifyItemChanged(position);

            mediaPlayer.setOnCompletionListener(mp -> {
                song.setPlaying(false);
                notifyItemChanged(position);
                currentlyPlayingIndex = -1;

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pauseSong(int position) {
        currentlyPlayingIndex = -1;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Song song = songs.get(position);
            song.setPlaying(false);
            notifyItemChanged(position);
        }
    }


    private void updateProgressBar(SongViewHolder holder) {
        if (mediaPlayer == null) {
            return;
        }

        ProgressBar progressBar = holder.progressBar;
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int progress = (int) ((mediaPlayer.getCurrentPosition() * 100) / mediaPlayer.getDuration());
                    handler.postDelayed(this, 100);
                    holder.progressBar.post(() -> holder.progressBar.setProgress(progress));
                }
            }
        };
        handler.post(runnable);

        mediaPlayer.setOnCompletionListener(mp -> {
            progressBar.setProgress(100);
            handler.removeCallbacksAndMessages(null);
        });

    }


}
