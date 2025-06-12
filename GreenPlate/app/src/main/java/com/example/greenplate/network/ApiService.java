package com.example.greenplate.network;



import com.example.greenplate.Resep;
import com.example.greenplate.home.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("recipes") // Ganti endpoint API sesuai yang kamu punya
    Call<List<Recipe>> getRecipesByPreference(@Query("preference") String preference);
    @GET("recipes/complexSearch")
    Call<ResepResponse> getRecipesByMealAndPreference(
            @Query("type") String mealTime,
            @Query("diet") String preference,
            @Query("apiKey") String apiKey
    );
}


