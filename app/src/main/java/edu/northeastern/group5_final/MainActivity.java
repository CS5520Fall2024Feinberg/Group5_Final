package edu.northeastern.group5_final;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, Dashboard.class));
            finish();
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

            //check if username and password are empty
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
                        for (DataSnapshot userSnapshot : snapshot.getChildren()){
                            String storedPassword = userSnapshot.child("password").getValue(String.class);
                            if(storedPassword.equals(password) && storedPassword != null){
                                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                    String storedEmail = userSnapshot.child("email").getValue(String.class);
                                    firebaseAuth.signInWithEmailAndPassword(storedEmail, password)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                                                intent.putExtra("username", username);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                return;
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else{
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
}