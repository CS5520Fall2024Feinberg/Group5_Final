package edu.northeastern.group5_final.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.northeastern.group5_final.MyMediaPlayer;
import edu.northeastern.group5_final.R;
import edu.northeastern.group5_final.models.Song;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.SongViewHolder> {

    private final List<Song> songs;
    private final Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Song song, int position);
    }

    public PlayListAdapter(Context context, List<Song> songs, OnItemClickListener listener) {
        this.context = context;
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music_play, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());

        if (position == MyMediaPlayer.getInstance(context).getCurrent() && MyMediaPlayer.getInstance(context).isPlaying()) {
            Glide.with(context)
                    .asGif()
                    .load(R.drawable.song_playing)
                    .placeholder(R.drawable.music)
                    .into(holder.imvIsplaying);
        } else {
            Glide.with(context)
                    .asGif()
                    .load(R.drawable.music)
                    .placeholder(R.drawable.music)
                    .into(holder.imvIsplaying);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(song, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {

        private final TextView songTitle;
        private final TextView songArtist;
        private final ImageView imvIsplaying;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            imvIsplaying = itemView.findViewById(R.id.icon_isplaying);
        }
    }
}