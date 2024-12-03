package edu.northeastern.group5_final.messaging;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.northeastern.group5_final.R;
import edu.northeastern.group5_final.SearchActivity;
import edu.northeastern.group5_final.Signup;
import edu.northeastern.group5_final.loginPage;

public class InboxActivity extends AppCompatActivity {

    Dialog composeDialog;
    ArrayList<Messages> messages = new ArrayList<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inbox);

        composeDialog = new Dialog(InboxActivity.this);
        composeDialog.setContentView(R.layout.compose_message);
        composeDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        Button btnCompose = findViewById(R.id.btn_compse);
        btnCompose.setOnClickListener(v -> {
            composeDialog.show();
        });
        Button btnCancel = composeDialog.findViewById(R.id.btn_cm_cancel);
        btnCancel.setOnClickListener(v -> {
            composeDialog.dismiss();
        });

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            // Log user details
            Log.d("FirebaseAuth", "User ID: " + currentUser.getUid());
            Log.d("FirebaseAuth", "Email: " + currentUser.getEmail());
            Log.d("FirebaseAuth", "Display Name: " + currentUser.getDisplayName());
            Log.d("FirebaseAuth", "Phone Number: " + currentUser.getPhoneNumber());
            Log.d("FirebaseAuth", "Is Email Verified: " + currentUser.isEmailVerified());
        } else {
            // No user is signed in
            Log.d("FirebaseAuth", "No user is currently signed in.");
        }
        // composing message
        Button btnSend = composeDialog.findViewById(R.id.btn_cm_send);
        btnSend.setOnClickListener(v -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference messageRef = database.getReference("messages");

            EditText etxtReceiver = composeDialog.findViewById(R.id.etxt_cm_receiver);
            EditText etxtSubject = composeDialog.findViewById(R.id.etxt_cm_subject);
            EditText etxtMessage = composeDialog.findViewById(R.id.etxt_cm_message);

            String receiver = etxtReceiver.getText().toString().trim();
            String subject = etxtSubject.getText().toString().trim();
            String message = etxtMessage.getText().toString().trim();
            String sender = currentUser.getDisplayName();

            // Input check for empty fields
            if (receiver.isEmpty() || subject.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return; // Stop execution if fields are empty
            }

            // Create a message object or Map
            Map<String, String> messageData = new HashMap<>();
            messageData.put("sender", sender);
            messageData.put("receiver", receiver);
            messageData.put("subject", subject);
            messageData.put("message", message);
            messageData.put("timestamp", String.valueOf(System.currentTimeMillis()));

            messageRef.push().setValue(messageData).addOnSuccessListener(aVoid -> {
                //success callback
                Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
                composeDialog.dismiss(); // Close dialog
            }).addOnFailureListener(e -> {
                // Failure callback
                Toast.makeText(this, "Failed to send message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });

            //Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
            //composeDialog.dismiss();
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Set up nav menu
        Button btn_search = findViewById(R.id.btn_search);
        Button btnHome = findViewById(R.id.btn_home);
        btn_search.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, loginPage.class));
        });


        //Recycler View set up
        RecyclerView recyclerView = findViewById(R.id.rc_inbox_messages);
        testSetUpMessages();
        Inbox_recyclerViewAdapter adapter = new Inbox_recyclerViewAdapter(this, messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu); // Use the correct menu file name
        return true;
    }

    private void setUpMessages(){

    }
    private void testSetUpMessages(){
        String contents = "test message. repeat this is a test message to see if everything works";
        Messages message = new Messages("friend123",contents,"dd-MM-yyyy HH:mm z","test message","nnaim");
        messages.add(message);

        contents = "test message. repeat this is a test message to see if everything works part 2";
        message = new Messages("friend124",contents,"dd-MM-yyyy HH:mm z","test message2","nnaim");
        messages.add(message);

        contents = "test message. repeat this is a test message to see if everything works part 3";
        message = new Messages("friend125",contents,"dd-MM-yyyy HH:mm z","test message3","nnaim");
        messages.add(message);

        contents = "test message. repeat this is a test message to see if everything works part 4";
        message = new Messages("friend126",contents,"dd-MM-yyyy HH:mm z","test message4","nnaim");
        messages.add(message);
    }
}