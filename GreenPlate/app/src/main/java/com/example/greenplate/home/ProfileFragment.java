package com.example.greenplate.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.greenplate.R;

public class ProfileFragment extends Fragment {

    private TextView nameTextView, ageTextView, genderTextView, preferenceTextView;
    private ImageView imageProfile;
    private SharedPreferences prefs;
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        prefs.edit().putString("image_uri", selectedImageUri.toString()).apply();
                        Glide.with(getContext())
                                .load(selectedImageUri)
                                .circleCrop()
                                .into(imageProfile);
                    }
                }
            });
    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    // Profil sudah diedit, reload data
                    loadProfileData();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {




        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        prefs = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);

        nameTextView = view.findViewById(R.id.nameTextView);
        ageTextView = view.findViewById(R.id.ageTextView);
        genderTextView = view.findViewById(R.id.genderTextView);
        preferenceTextView = view.findViewById(R.id.preferenceTextView);
        imageProfile = view.findViewById(R.id.imageProfile);
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile);

        ImageButton themeToggleBtn = view.findViewById(R.id.themeToggleBtn);

// Ganti ikon sesuai mode saat ini
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            themeToggleBtn.setImageResource(R.drawable.light); // ikon terang
        } else {
            themeToggleBtn.setImageResource(R.drawable.night); // ikon gelap
        }

// Saat tombol diklik, toggle mode
        themeToggleBtn.setOnClickListener(v -> {
            int mode = AppCompatDelegate.getDefaultNightMode();
            if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });

        loadProfileData();

        imageProfile.setOnClickListener(v -> openGallery());

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Profile.class);
            editProfileLauncher.launch(intent);
        });

        return view;
    }

    private void loadProfileData() {
        String name = prefs.getString("name", null);
        int age = prefs.getInt("age", -1);
        String gender = prefs.getString("gender", null);
        String preference = prefs.getString("preference", null);

        if (name != null) {
            nameTextView.setText(name);
        }

        if (age != -1) {
            ageTextView.setText(age + " TAHUN");
        }

        if (gender != null) {
            genderTextView.setText(gender.toUpperCase());
        }

        if (preference != null) {
            preferenceTextView.setText(preference.toUpperCase());
        }

        String imageUri = prefs.getString("image_uri", null);
        if (imageUri != null) {
            Glide.with(requireContext())
                    .load(Uri.parse(imageUri))
                    .circleCrop()
                    .into(imageProfile);
        }
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
}
