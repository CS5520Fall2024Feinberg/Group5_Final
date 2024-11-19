package edu.northeastern.group5_final;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("message");


        EditText etxtFirstName = findViewById(R.id.etxt_su_firstName);
        EditText etxtLastName = findViewById(R.id.etxt_su_lastName);
        EditText etxtUsername = findViewById(R.id.etxt_su_username);
        EditText etxtPassword = findViewById(R.id.etxt_su_password);
        EditText etxtPassword2 = findViewById(R.id.etxt_su_password2);
        EditText etxtEmail = findViewById(R.id.etxt_su_email);


        //myRef.setValue("Hello, World!");
        Button btnSignup = findViewById(R.id.btn_su_signup);
        btnSignup.setOnClickListener(view -> {
            String firstName = etxtFirstName.getText().toString().trim();
            String lastName = etxtLastName.getText().toString().trim();
            String username = etxtUsername.getText().toString().trim();
            String password = etxtPassword.getText().toString().trim();
            String password2 = etxtPassword2.getText().toString().trim();
            String email = etxtEmail.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(Signup.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(password2)) {
                Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("users");

            usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(Signup.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String,String> userData = new HashMap<>();
                        userData.put("firstName", firstName);
                        userData.put("lastName", lastName);
                        userData.put("username", username);
                        userData.put("password", password);
                        userData.put("email", email);

                        usersRef.push().setValue(userData).addOnSuccessListener(unused -> {
                                    Toast.makeText(Signup.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(Signup.this, "Signup failed", Toast.LENGTH_SHORT).show();
                                });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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