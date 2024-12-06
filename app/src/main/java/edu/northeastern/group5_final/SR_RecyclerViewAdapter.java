package edu.northeastern.group5_final;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.northeastern.group5_final.models.Artist;

public class SR_RecyclerViewAdapter extends RecyclerView.Adapter<SR_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<SearchResults> searchResults;


    public SR_RecyclerViewAdapter(Context context, ArrayList<SearchResults> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public SR_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_recycler_view_row, parent, false);

        return new SR_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SR_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.txtFullName.setText(searchResults.get(position).getFirstName() + " " + searchResults.get(position).getLastName() +
                " (" + searchResults.get(position).getUsername() + ")");

        holder.txtMemberInfo.setText("Member of: " + searchResults.get(position).getMemberInfo());
        //TODO: add profile pic
        holder.profilePic.setImageResource(R.drawable.ic_launcher_background);
        SearchResults searchResult = searchResults.get(position);

        if (searchResult.getProfilePic() != null) {
            String Url = searchResult.getProfilePic().toString();
            //Log.e("URL", "URL of image: : " + Url2);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReferenceFromUrl(Url);
            storageReference.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(context)
                                .load(uri.toString())
                                .placeholder(R.drawable.single_artist_icon)
                                .error(R.drawable.single_artist_icon)           // Fallback image
                                .circleCrop()                                   // Make the image circular
                                .into(holder.profilePic);
                    })
                    .addOnFailureListener(e -> {//cant find image
                        holder.profilePic.setImageResource(R.drawable.single_artist_icon); // Fallback image
                    });
        }
        else {//when they dont have a profile picture
            //Log.e("URL", "URL of image is null: " + artist.getUsername());
            holder.profilePic.setImageResource(R.drawable.single_artist_icon);
        }


    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePic;
        TextView txtFullName;
        ;
        TextView txtMemberInfo;
        Button btnView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.artist_picture);
            txtFullName = itemView.findViewById(R.id.artist_name);
            txtMemberInfo = itemView.findViewById(R.id.artist_name);

        }
    }
}
