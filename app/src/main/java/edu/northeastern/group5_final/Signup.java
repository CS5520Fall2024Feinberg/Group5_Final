package edu.northeastern.group5_final;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView ivProfilePicture;
    EditText etxtName, etxtUsername, etxtPassword, etxtRePassword;
    Uri profilePictureUri;
    FirebaseDatabase database;
    DatabaseReference artistsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ivProfilePicture = findViewById(R.id.iv_add_picture);
        ivProfilePicture.setOnClickListener(v -> openFilePicker());

        etxtName = findViewById(R.id.et_name);
        etxtUsername = findViewById(R.id.et_username);
        etxtPassword = findViewById(R.id.et_password);
        etxtRePassword = findViewById(R.id.et_reenter_password);

        Button btnSignup = findViewById(R.id.btn_sign_up);
        btnSignup.setOnClickListener(view -> signUp());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    profilePictureUri = result.getData().getData();
                    if (profilePictureUri != null) {
                        ivProfilePicture.setImageURI(profilePictureUri);
                    } else {
                        Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(Intent.createChooser(intent, "Select Profile Picture"));
    }

    private void signUp() {

        String name = etxtName.getText().toString().trim();
        String username = etxtUsername.getText().toString().trim();
        String password = etxtPassword.getText().toString().trim();
        String passwordRe = etxtRePassword.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(Signup.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordRe)) {
            Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        database = FirebaseDatabase.getInstance();
        artistsRef = database.getReference("artists");

        artistsRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(Signup.this, "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    if (profilePictureUri != null) {
                        StorageReference storageReference = FirebaseStorage
                                .getInstance()
                                .getReference("profile_pictures/" + username + ".jpg");

                        storageReference.putFile(profilePictureUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        String profilePictureUrl = uri.toString();
                                        saveUserData(name, username, password, profilePictureUrl);
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(Signup.this, "Failed to get profile picture URL", Toast.LENGTH_SHORT).show();
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Signup.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                                });


                    } else {
                        saveUserData(name, username, password, null);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void saveUserData(String name, String username, String password, @Nullable String profilePictureUrl) {
        Map<String,String> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("username", username);
        userData.put("password", password);

        if (profilePictureUrl != null) {
            userData.put("profilePictureUrl", profilePictureUrl);
        }

        artistsRef.push().setValue(userData).addOnSuccessListener(unused -> {
            Toast.makeText(Signup.this, "Signup successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Signup.this, Dashboard.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(Signup.this, "Signup failed", Toast.LENGTH_SHORT).show();
        });
    }
}