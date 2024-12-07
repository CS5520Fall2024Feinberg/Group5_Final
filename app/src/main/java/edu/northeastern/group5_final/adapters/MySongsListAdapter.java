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
import edu.northeastern.group5_final.models.MySong;

public class MySongsListAdapter extends RecyclerView.Adapter<MySongsListAdapter.MySongsViewHolder> {

    private Context context;
    private List<MySong> mySongs;

    public MySongsListAdapter(Context context, List<MySong> mySongs) {
        this.context = context;
        this.mySongs = mySongs;
    }

    @NonNull
    @Override
    public MySongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_song, parent, false);
        return new MySongsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySongsViewHolder holder, int position) {
        MySong song = mySongs.get(position);

        holder.title.setText(song.getTitle());
        holder.releaseDate.setText(song.getReleaseDate());
        holder.genre.setText(song.getGenre());
    }

    @Override
    public int getItemCount() {
        return mySongs.size();
    }

    public static class MySongsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView releaseDate;
        TextView genre;

        public MySongsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.song_title);
            releaseDate = itemView.findViewById(R.id.song_release_date);
            genre = itemView.findViewById(R.id.song_genre);
        }
    }
}
