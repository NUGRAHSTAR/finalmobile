package com.example.greenplate.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.RecipeAdapter;
import com.example.greenplate.Resep;
import com.example.greenplate.network.ApiService;
import com.example.greenplate.network.ResepResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private TextView greetingText;
    private RecyclerView breakfastRecyclerView, lunchRecyclerView, dinnerRecyclerView;
    private View errorLayout, contentLayout;
    private ProgressBar progressBar;
    private String preference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi view
        greetingText = view.findViewById(R.id.greetingTextView);
        breakfastRecyclerView = view.findViewById(R.id.breakfastRecyclerView);
        lunchRecyclerView = view.findViewById(R.id.lunchRecyclerView);
        dinnerRecyclerView = view.findViewById(R.id.dinnerRecyclerView);
        errorLayout = view.findViewById(R.id.errorLayout);
        progressBar = view.findViewById(R.id.pb);
        contentLayout = view.findViewById(R.id.contentLayout);

        breakfastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        lunchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dinnerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Ambil data user dari SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        String name = preferences.getString("name", "User");
        preference = preferences.getString("preference", "Vegetarian");

        greetingText.setText("Hi " + name + ", here are some " + preference + " recipes for you!");

        ImageView refreshButton = view.findViewById(R.id.refresh);
        refreshButton.setOnClickListener(v -> {
            if (isNetworkConnected()) {
                errorLayout.setVisibility(View.GONE);

                showLoading(true);
                loadAllRecipes();
            } else {
                Toast.makeText(getContext(), "Masih tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
            }
        });
        if (!isNetworkConnected()) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Tidak ada koneksi internet", Toast.LENGTH_LONG).show();
        } else {
            errorLayout.setVisibility(View.GONE);
            showLoading(true);
            loadAllRecipes();
        }

        return view;
    }

    private void loadAllRecipes() {
        fetchRecipesByMeal("breakfast", preference, breakfastRecyclerView);
        fetchRecipesByMeal("lunch", preference, lunchRecyclerView);
        fetchRecipesByMeal("dinner", preference, dinnerRecyclerView);
    }

    private void fetchRecipesByMeal(String mealTime, String preference, RecyclerView recyclerView) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        String apiKey = "eebfd99a2a8140e3be9b889b44e2ba2f";

        Call<ResepResponse> call = apiService.getRecipesByMealAndPreference(mealTime, preference, apiKey);

        call.enqueue(new Callback<ResepResponse>() {
            @Override
            public void onResponse(Call<ResepResponse> call, Response<ResepResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Resep> recipes = response.body().getResults();

                    requireActivity().runOnUiThread(() -> {
                        RecipeAdapter adapter = new RecipeAdapter(getContext(), recipes, new RecipeAdapter.OnResepClickListener() {
                            @Override
                            public void onResepClick(Resep resep) {
                                Intent intent = new Intent(getContext(), DetailResepActivity.class);
                                intent.putExtra("resep_id", resep.getId());
                                startActivity(intent);
                            }

                            @Override
                            public void onFavoriteClick(Resep resep) {
                                Toast.makeText(getContext(), "Favorited: " + resep.getNama(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        recyclerView.setAdapter(adapter);
                        showLoading(false);
                    });
                } else {
                    Log.e("HomeFragment", mealTime + " API not successful: " + response.code());
                    requireActivity().runOnUiThread(() -> showLoading(false));
                }
            }

            @Override
            public void onFailure(Call<ResepResponse> call, Throwable t) {
                Log.e("HomeFragment", mealTime + " API call failed: " + t.getMessage());
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    errorLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Gagal memuat data. Periksa koneksi Anda.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        contentLayout.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}

