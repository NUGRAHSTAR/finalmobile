package com.example.greenplate.network;



import com.example.greenplate.Resep;
import com.example.greenplate.home.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("recipes/complexSearch")
    Call<ResepResponse> getRecipesByMealAndPreference(
            @Query("type") String mealType,
            @Query("diet") String diet,
            @Query("sort") String sort,
            @Query("number") int number,
            @Query("apiKey") String apiKey
    );

}

