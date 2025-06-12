package com.example.greenplate.home;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.greenplate.R;

public class MainActivity extends AppCompatActivity {

    ImageView homeImg, resepImg, favImg, profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeImg = findViewById(R.id.homeImg);
        resepImg = findViewById(R.id.resepImg);
        favImg = findViewById(R.id.favImg);


        profileImg = findViewById(R.id.profileImg);

        loadFragment(new HomeFragment());

        homeImg.setOnClickListener(v -> loadFragment(new HomeFragment()));
        resepImg.setOnClickListener(v -> loadFragment(new ResepFragment()));
        favImg.setOnClickListener(v -> loadFragment(new FavoriteFragment()));

        profileImg.setOnClickListener(v -> loadFragment(new ProfileFragment()));
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}