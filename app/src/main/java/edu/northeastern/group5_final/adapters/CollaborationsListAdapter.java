package edu.northeastern.group5_final.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group5_final.R;
import edu.northeastern.group5_final.models.Collaboration;

public class CollaborationsListAdapter extends RecyclerView.Adapter<CollaborationsListAdapter.CollaborationViewHolder> {

    private Context context;
    private List<Collaboration> collaborations;

    public CollaborationsListAdapter(Context context, List<Collaboration> collaborations) {
        this.context = context;
        this.collaborations = collaborations != null ? collaborations : new ArrayList<>();
    }

    @NonNull
    @Override
    public CollaborationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_collaboration, parent, false);
        return new CollaborationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollaborationViewHolder holder, int position) {
        Collaboration collaboration = collaborations.get(position);

        holder.bandName.setText(collaboration.getBandName());
        holder.artists.setText(collaboration.getArtists());
    }

    @Override
    public int getItemCount() {
        return collaborations.size();
    }

    public static class CollaborationViewHolder extends RecyclerView.ViewHolder {
        TextView bandName;
        TextView artists;

        public CollaborationViewHolder(@NonNull View itemView) {
            super(itemView);
            bandName = itemView.findViewById(R.id.collaboration_band_name);
            artists = itemView.findViewById(R.id.collaboration_artists);
        }
    }
}
