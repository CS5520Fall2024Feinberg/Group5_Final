package edu.northeastern.group5_final;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import edu.northeastern.group5_final.messaging.InboxActivity;

public class SearchActivity extends AppCompatActivity {
    ArrayList<SearchResults> searchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        //RecyclerView recyclerView = findViewById(R.id.rcv_searchResults);
        //testSearchResults();
        //SR_RecyclerViewAdapter adapter = new SR_RecyclerViewAdapter(this,searchResults);
        //recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Nav menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Buttons in nav menu
        Button btnMessages = findViewById(R.id.btn_messages);
        Button btnHome = findViewById(R.id.btn_home);

        btnMessages.setOnClickListener(v -> {
            startActivity(new Intent(this, InboxActivity.class));
        });
        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, loginPage.class));
        });

        Button btnSearch = findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(v -> {
            searchResults.clear();
            EditText txtSearch = findViewById(R.id.txte_sa_search);
            String searchTerm = txtSearch.getText().toString();
            RecyclerView recyclerView = findViewById(R.id.rcv_searchResults);
            //testSearchResults();
            //buildSearchResults(searchTerm);
            SR_RecyclerViewAdapter adapter = new SR_RecyclerViewAdapter(this,searchResults);
            //buildSearchResults(adapter,searchTerm);
            //testSearchResults();
            buildSearchResults(adapter,searchTerm);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //buildSearchResults(searchTerm);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void buildSearchResults(SR_RecyclerViewAdapter adapter,String searchTerm){
        //TODO: Build search results connect to database for information

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference userRef = database.getReference("messages");
        Log.d("BuildingSeach", "Test Test Test");
        RadioGroup radioGroup = findViewById(R.id.radioGroup_sa);

        if (radioGroup == null) {
            Log.e("RadioGroup", "RadioGroup not found!");
        } else {
            Log.d("RadioGroup", "RadioGroup initialized.");
        }

        RadioButton rbtnUsers = findViewById(R.id.rbtn_sa_users);
        RadioButton rbtnBands = findViewById(R.id.rbtn_sa_bands);
        if (rbtnUsers.isChecked()) {
            Log.d("RadioButton", "Users selected");
            // Perform user-related Firebase query
            DatabaseReference userRef = database.getReference("artists");
            userRef.orderByChild("name")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot messageSnapshot : snapshot.getChildren()){
                                Map<String, String> artistdb = (Map<String, String>) messageSnapshot.getValue();
                                if(artistdb != null){
                                    String name = artistdb.get("name");
                                    String username = artistdb.get("username");
                                    String profilePic = artistdb.get("profilePic");

                                    if (name != null && name.toLowerCase().contains(searchTerm.toLowerCase())) {
                                        // Add matching results to the list
                                        SearchResults result = new SearchResults("", username, "", name, profilePic);
                                        searchResults.add(result);
                                    }

                                }
                            }
                            adapter.notifyDataSetChanged();
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Error querying messages: " + error.getMessage());
                        }
                    });


        } else if (rbtnBands.isChecked()) {
            Log.d("RadioButton", "Bands selected");
            // Perform band-related Firebase query
        }


    }
    private void testSearchResults(){
        SearchResults result = new SearchResults("Band1", "Tod123", "Tod", "Tod", "N/A");
        //result = new SearchResults("Band2", "Mike123", "Mike", "Mike", "N/A");
        searchResults.add(result);
        result = new SearchResults("Band2", "Mike1", "Mike", "Mike", "N/A");
        searchResults.add(result);
        result = new SearchResults("Band3", "Mike2", "Mike", "MikeTheSecond", "N/A");
        searchResults.add(result);
        result = new SearchResults("Band4", "Mike4", "Mike", "MikeTheThird", "N/A");
        searchResults.add(result);
        result = new SearchResults("Band5", "Mike5", "Mike", "MikeTheFourth", "N/A");
        searchResults.add(result);

    }
    //For nav menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu); // Use the correct menu file name
        return true;
    }


}