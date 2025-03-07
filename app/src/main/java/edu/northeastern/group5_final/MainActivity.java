package edu.northeastern.group5_final;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.group5_final.models.ArtistDBModel;
import edu.northeastern.group5_final.utils.SharedPreferenceManager;
import edu.northeastern.group5_final.utils.Utils;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (firebaseAuth.getCurrentUser() != null) {
            setUserRoleAndProceed();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Button btnSignup = findViewById(R.id.btn_sign_up);
        btnSignup.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Signup.class);
            startActivity(intent);
        });

        EditText etxtUsername = findViewById(R.id.et_username);
        EditText etxtPassword = findViewById(R.id.et_password);

        Button btnLogin = findViewById(R.id.btn_sign_in);
        btnLogin.setOnClickListener(view -> {

            String username = etxtUsername.getText().toString().trim();
            String password = etxtPassword.getText().toString().trim();

            // Check if username and password are empty
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter both username and password!", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("artists");

            usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        boolean authenticated = false;
                        for (DataSnapshot userSnapshot : snapshot.getChildren()){
                            String storedPassword = userSnapshot.child("password").getValue(String.class);
                            String storedEmail = userSnapshot.child("email").getValue(String.class);

                            if (storedPassword != null && storedPassword.equals(password)) {
                                authenticated = true;
                                firebaseAuth.signInWithEmailAndPassword(storedEmail, password)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Utils.fetchSelfUserData(MainActivity.this, new Utils.UserCallback() {
                                                    @Override
                                                    public void onSuccess(ArtistDBModel selfUser) {
                                                        String role = selfUser.getRole() == null ? "LISTENER" : selfUser.getRole();
                                                        SharedPreferenceManager.saveUserRole(MainActivity.this, role);
                                                        Intent intent = new Intent(MainActivity.this, Dashboard.class);
                                                        intent.putExtra("username", username);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFailure(String errorMessage) {
                                                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                                        SharedPreferenceManager.saveUserRole(MainActivity.this, "LISTENER");
                                                        Intent intent = new Intent(MainActivity.this, Dashboard.class);
                                                        intent.putExtra("username", username);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(MainActivity.this, "Sign-in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                break;
                            }
                        }
                        if (!authenticated) {
                            Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Login failed. Please try again." , Toast.LENGTH_SHORT).show();
                }
            });
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setUserRoleAndProceed() {
        Utils.fetchSelfUserData(MainActivity.this, new Utils.UserCallback() {
            @Override
            public void onSuccess(ArtistDBModel selfUser) {
                String role = selfUser.getRole() == null ? "LISTENER" : selfUser.getRole();
                SharedPreferenceManager.saveUserRole(MainActivity.this, role);
                startActivity(new Intent(MainActivity.this, Dashboard.class));
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                SharedPreferenceManager.saveUserRole(MainActivity.this, "LISTENER");
                startActivity(new Intent(MainActivity.this, Dashboard.class));
                finish();
            }
        });
    }
}