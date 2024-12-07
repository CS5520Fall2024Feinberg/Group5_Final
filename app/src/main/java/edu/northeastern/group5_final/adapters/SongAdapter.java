package edu.northeastern.group5_final.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.northeastern.group5_final.MyMediaPlayer;
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
        holder.btnAddSong.setOnClickListener(v -> {
            List<Song> playList = MyMediaPlayer.getInstance(context).getPlayList();
            if (!playList.contains(song)){
                MyMediaPlayer.getInstance(context).addSong(song);
                Toast.makeText(context, context.getString(R.string.add) + song.getTitle() + context.getString(R.string.to_play_list), Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, song.getTitle() + context.getString(R.string.exits_play_list), Toast.LENGTH_SHORT).show();
            }
        });

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


        holder.favoriteButton.setOnClickListener(v -> favoriteBtnListener(song, position));

        if (position == currentlyPlayingIndex) {
            updateProgressBar(holder);
        } else {
            holder.progressBar.setProgress(0);
        }

        holder.songInfo.setOnClickListener(v -> moreInfoListener(holder));


    }

    private void favoriteBtnListener(Song song, int position) {
        String currentUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if (currentUsername == null || currentUsername.isEmpty()) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String songId = song.getId();
        DatabaseReference songRef = FirebaseDatabase.getInstance().getReference("songs").child(songId).child("likedBy");
        boolean isFavorite = song.isFavorite();

        if (isFavorite) {
            songRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> likedBy = new ArrayList<>();
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String username = userSnapshot.getValue(String.class);
                            if (username != null && !username.equals(currentUsername)) {
                                likedBy.add(username);
                            }
                        }
                    }

                    songRef.setValue(likedBy)
                            .addOnSuccessListener(aVoid -> {
                                song.setFavorite(false);
                                notifyItemChanged(position);
                                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to update favorites", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            songRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> likedBy = new ArrayList<>();
                    if (snapshot.exists()) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            String username = userSnapshot.getValue(String.class);
                            if (username != null) {
                                likedBy.add(username);
                            }
                        }
                    }
                    likedBy.add(currentUsername);

                    songRef.setValue(likedBy)
                            .addOnSuccessListener(aVoid -> {
                                song.setFavorite(true);
                                notifyItemChanged(position);
                                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed to update favorites", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void moreInfoListener(@NonNull SongViewHolder holder) {

        int position = holder.getAdapterPosition();
        Song song = songs.get(position);

        Context context = holder.itemView.getContext();

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_song_profile);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.y = -200;
            window.setAttributes(params);

        }

        ImageView songPicture = dialog.findViewById(R.id.dialog_song_picture);
        TextView songTitle = dialog.findViewById(R.id.dialog_song_title);
        TextView likedBy = dialog.findViewById(R.id.dialog_song_liked_by);
        TextView genre = dialog.findViewById(R.id.dialog_song_genre);
        TextView releaseDate = dialog.findViewById(R.id.dialog_song_release_date);
        TextView artists = dialog.findViewById(R.id.dialog_song_artists);

        Glide.with(this.context)
                .asGif()
                .load(getGifUrl())
                .placeholder(R.drawable.song_info)
                .into(songPicture);

        songTitle.setText(song.getTitle());
        likedBy.setText("Liked By: " + song.getLikedBy());
        genre.setText("Genre: " + song.getGenre());
        releaseDate.setText("Release Date: " + song.getReleaseDate());
        artists.setText("Artist(s): " + song.getArtist());

        dialog.show();
    }

    private String getGifUrl() {
        String[] urls = {
                "https://media.tenor.com/dmb-UuLw7w4AAAAi/milk-and.gif",
                "https://media.tenor.com/IZH_k7F9aqcAAAAj/music.gif",
                "https://c.tenor.com/9SFSfC2n0lkAAAAd/tenor.gif",
                "https://media.tenor.com/_8P584En7EcAAAAi/ukulele-music.gif",
                "https://media.tenor.com/3mYA38cHUbsAAAAi/music-sheet-song.gif"
        };

        Random random = new Random();
        int randomIndex = random.nextInt(urls.length);
        return urls[randomIndex];
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle, songArtist, songGenre, songInfo;
        ImageButton playPauseButton, favoriteButton;
        ProgressBar progressBar;
        MaterialCardView card;
        View btnAddSong;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            songGenre = itemView.findViewById(R.id.song_genre);
            playPauseButton = itemView.findViewById(R.id.btn_play_pause);
            favoriteButton = itemView.findViewById(R.id.btn_favorite);
            progressBar = itemView.findViewById(R.id.song_progress_bar);
            btnAddSong = itemView.findViewById(R.id.btn_add_song);
            songInfo = itemView.findViewById(R.id.song_more_info);

            songInfo.setText(Html.fromHtml("<u>More Info</u>"));
            songTitle.setSelected(true);
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
            mediaPlayer = null;
        }

        try {
            mediaPlayer = new MediaPlayer();
            Log.d("TAG", "playSong: " + song.getSongUrl());
            mediaPlayer.setDataSource(song.getSongUrl());
            mediaPlayer.prepareAsync();
            notifyItemChanged(position);

            mediaPlayer.setOnPreparedListener(mp -> {
                song.setPlaying(true);
                notifyItemChanged(position);
                mediaPlayer.start();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                song.setPlaying(false);
                notifyItemChanged(position);
                currentlyPlayingIndex = -1;

                mediaPlayer.release();
                mediaPlayer = null;
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

    public void stopSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
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
