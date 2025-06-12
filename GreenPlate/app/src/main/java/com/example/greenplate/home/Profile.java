package com.example.greenplate.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenplate.R;

public class Profile extends AppCompatActivity {

    EditText nameInput, ageInput;
    Spinner genderSpinner, preferenceSpinner;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameInput = findViewById(R.id.nameEditText);
        ageInput = findViewById(R.id.ageEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        preferenceSpinner = findViewById(R.id.preferenceSpinner);
        saveBtn = findViewById(R.id.saveButton);

        // Setup gender spinner
        String[] genders = {"Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Setup preference spinner
        String[] preferences = {"Vegetarian", "Vegan", "Non-Vegetarian", "Other"};
        ArrayAdapter<String> preferenceAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                preferences);
        preferenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        preferenceSpinner.setAdapter(preferenceAdapter);

        saveBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String ageStr = ageInput.getText().toString().trim();

            if (name.isEmpty()) {
                nameInput.setError("Name cannot be empty");
                return;
            }

            if (ageStr.isEmpty()) {
                ageInput.setError("Age cannot be empty");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                ageInput.setError("Age must be a number");
                return;
            }

            SharedPreferences.Editor editor = getSharedPreferences("user_profile", MODE_PRIVATE).edit();
            editor.putString("name", name);
            editor.putInt("age", age);
            editor.putString("gender", genderSpinner.getSelectedItem().toString());
            editor.putString("preference", preferenceSpinner.getSelectedItem().toString());
            editor.apply();

            Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, MainActivity.class));
            setResult(RESULT_OK);
            finish();
        });
    }
}