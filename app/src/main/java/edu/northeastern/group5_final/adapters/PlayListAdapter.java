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


    public PlayListAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_music_play, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int p) {
        int position = holder.getAdapterPosition();
        Song song = songs.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());

        if (p == MyMediaPlayer.getInstance(context).getCurrent() && MyMediaPlayer.getInstance(context).isPlaying()) {
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
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {

        private TextView songTitle, songArtist, tvType;
        private ImageView imvIsplaying;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            imvIsplaying = itemView.findViewById(R.id.icon_isplaying);

        }
    }
}
