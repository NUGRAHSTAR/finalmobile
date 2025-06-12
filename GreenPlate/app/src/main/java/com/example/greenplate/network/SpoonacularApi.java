package com.example.greenplate.network;


import com.example.greenplate.Resep;
import com.example.greenplate.home.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SpoonacularApi {

    @GET("recipes/complexSearch")
    Call<SpoonacularResponse> searchRecipes(
            @Query("query") String keyword,
            @Query("number") int number,
            @Query("apiKey") String apiKey

    );
    @GET("recipes/{id}/information")
    Call<ResepDetailResponse> getRecipeDetail(
            @Path("id") int id,
            @Query("includeNutrition") boolean includeNutrition,
            @Query("apiKey") String apiKey
    );
}
