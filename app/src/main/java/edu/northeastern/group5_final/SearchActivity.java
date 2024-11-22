package edu.northeastern.group5_final;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ArrayList<SearchResults> searchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        RecyclerView recyclerView = findViewById(R.id.rcv_searchResults);
        testSearchResults();
        SR_RecyclerViewAdapter adapter = new SR_RecyclerViewAdapter(this,searchResults);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void buildSearchResults(){
        //TODO: Build search results connect to database for information
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
}