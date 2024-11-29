package edu.northeastern.group5_final;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button redirectButton = view.findViewById(R.id.login_page_btn);
        redirectButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), loginPage.class);
            startActivity(intent);
        });

        Button logoutredirectButton = view.findViewById(R.id.logout_btn);
        logoutredirectButton.setOnClickListener(v -> {
            firebaseAuth.signOut();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        });

    }
}
