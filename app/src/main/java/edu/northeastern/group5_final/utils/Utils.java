package edu.northeastern.group5_final.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.group5_final.models.ArtistDBModel;

public class Utils {

    public interface UserCallback {
        void onSuccess(ArtistDBModel user);
        void onFailure(String errorMessage);
    }

    public static void fetchSelfUserData(Context context, UserCallback callback) {
        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        if (username == null) {
            callback.onFailure("User is not authenticated");
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists");
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ArtistDBModel selfUser = snapshot.getValue(ArtistDBModel.class);
                        if (selfUser != null) {
                            if (selfUser.getRole() == null || selfUser.getRole().isEmpty()) {
                                snapshot.getRef().child("role").setValue("ARTIST").addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Role set to ARTIST", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Failed to set role", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            callback.onSuccess(selfUser);
                            return;
                        }
                    }
                    callback.onFailure("Failed to parse user data");
                } else {
                    callback.onFailure("User data not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure("Error fetching data: " + databaseError.getMessage());
            }
        });
    }
}
