package com.example.greenplate;

import android.content.Context;
import android.content.SharedPreferences;

public class ResepFavoritManager {
    private SharedPreferences preferences;
    private static final String PREF_NAME = "resep_favorit";

    public ResepFavoritManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveRecipeDetail(int id, String json) {
        preferences.edit().putString("resep_" + id, json).apply();
    }

    public String getRecipeDetail(int id) {
        return preferences.getString("resep_" + id, null);
    }
}
