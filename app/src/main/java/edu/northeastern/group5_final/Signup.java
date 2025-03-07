package edu.northeastern.group5_final;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ImageView ivProfilePicture;
    EditText etxtName, etxtUsername, etxtEmail, etxtPassword, etxtRePassword;
    Uri profilePictureUri;
    FirebaseDatabase database;
    DatabaseReference artistsRef;
    RadioGroup radioGroup;
    String role = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ivProfilePicture = findViewById(R.id.iv_add_picture);
        ivProfilePicture.setOnClickListener(v -> openFilePicker());

        etxtName = findViewById(R.id.et_name);
        etxtUsername = findViewById(R.id.et_username);
        etxtEmail = findViewById(R.id.et_email);
        etxtPassword = findViewById(R.id.et_password);
        etxtRePassword = findViewById(R.id.et_reenter_password);

        radioGroup = findViewById(R.id.rg_artist_band);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_yes) {
                role = "ARTIST";
            } else if (checkedId == R.id.rb_no) {
                role = "LISTENER";
            }
        });

        Button btnSignup = findViewById(R.id.btn_sign_up);
        btnSignup.setOnClickListener(view -> signUp());

        Button btnSignin = findViewById(R.id.btn_sign_in);
        btnSignin.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
        });

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
        String email = etxtEmail.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordRe.isEmpty()) {
            Toast.makeText(Signup.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidEmail(email)) {
            Toast.makeText(Signup.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordRe)) {
            Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (role == null) {
            Toast.makeText(Signup.this, "Please select a role before signing up", Toast.LENGTH_SHORT).show();
            return;
        }

        database = FirebaseDatabase.getInstance();
        artistsRef = database.getReference("artists");

        artistsRef.orderByChild("username").equalTo(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Toast.makeText(Signup.this, "Username already exists", Toast.LENGTH_SHORT).show();
                } else {
                    if (profilePictureUri != null) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pictures/" + username + ".jpg");
                        storageReference.putFile(profilePictureUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        String profilePictureUrl = uri.toString();
                                        saveUserData(name, username, email, password, profilePictureUrl);
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(Signup.this, "Failed to get profile picture URL", Toast.LENGTH_SHORT).show();
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Signup.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // No profile picture selected
                        saveUserData(name, username, email, password, null);
                    }
                }
            } else {
                Toast.makeText(Signup.this, "Error checking username availability", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(String name, String username, String email, String password, String profilePictureUrl) {
        Map<String,String> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("username", username);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("dateJoined", getCurrentDate());
        userData.put("bio", "");
        userData.put("role", role);

        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            userData.put("profilePictureUrl", profilePictureUrl);
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        artistsRef.push().setValue(userData).addOnSuccessListener(unused -> {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username).build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(Signup.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Signup.this, Dashboard.class);
                                            intent.putExtra("username", username);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(Signup.this, "Failed to set display name", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }).addOnFailureListener(e -> {
                            Toast.makeText(Signup.this, "Signup failed while saving data", Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        Toast.makeText(Signup.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    private String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        return currentDate.format(formatter);
    }
}