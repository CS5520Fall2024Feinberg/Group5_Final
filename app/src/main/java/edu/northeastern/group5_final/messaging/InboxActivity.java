package edu.northeastern.group5_final.messaging;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.northeastern.group5_final.R;
import edu.northeastern.group5_final.SearchActivity;
import edu.northeastern.group5_final.loginPage;

public class InboxActivity extends AppCompatActivity {

    Dialog composeDialog;

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
}