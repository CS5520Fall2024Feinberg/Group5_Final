package edu.northeastern.group5_final.messaging;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.group5_final.R;
import edu.northeastern.group5_final.SearchActivity;
import edu.northeastern.group5_final.loginPage;

public class InboxActivity extends AppCompatActivity {

    Dialog composeDialog;
    ArrayList<Messages> messages = new ArrayList<>();

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
        Button btnSend = composeDialog.findViewById(R.id.btn_cm_send);
        btnSend.setOnClickListener(v -> {
            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
            composeDialog.dismiss();
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