package com.example.greenplate.home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.ResepAdapter;
import com.example.greenplate.network.SpoonacularApi;
import com.example.greenplate.network.SpoonacularResponse;
import com.example.greenplate.Resep;

import java.util.*;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
public class ResepFragment extends Fragment {

    private LinearLayout contentLayout, errorLayout;
    private ProgressBar progressBar;
    private RecyclerView rvResep;
    private ImageView refresh;

    private ResepAdapter adapter;
    private List<Resep> resepList = new ArrayList<>();
    private SearchView searchView;

    private SpoonacularApi api;
    private static final String API_KEY = "6278f30671e04195a0d0f5c1deaf802a";
    private static final String BASE_URL = "https://api.spoonacular.com/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resep, container, false);

        contentLayout = view.findViewById(R.id.contentLayout);
        errorLayout = view.findViewById(R.id.errorLayout);
        progressBar = view.findViewById(R.id.pb);
        rvResep = view.findViewById(R.id.rvResep);
        searchView = view.findViewById(R.id.searchView);
        refresh = view.findViewById(R.id.refresh);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(SpoonacularApi.class);

        adapter = new ResepAdapter(getContext(), resepList, new ResepAdapter.OnResepClickListener() {
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

        rvResep.setLayoutManager(new LinearLayoutManager(getContext()));
        rvResep.setAdapter(adapter);

        refresh.setOnClickListener(v -> loadResep("chicken"));

        // Load default resep
        loadResep("chicken");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadResep(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        refresh.setVisibility(View.GONE);
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        refresh.setVisibility(View.GONE);
    }

    private void showError(boolean isNetworkError) {
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        if (isNetworkError) {
            refresh.setVisibility(View.VISIBLE); // tampilkan tombol refresh hanya jika error jaringan
        } else {
            refresh.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void loadResep(String keyword) {
        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
            showError(true);
            return;
        }

        showLoading();
        Call<SpoonacularResponse> call = api.searchRecipes(keyword, 20, API_KEY);
        call.enqueue(new Callback<SpoonacularResponse>() {
            @Override
            public void onResponse(Call<SpoonacularResponse> call, Response<SpoonacularResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resepList.clear();
                    resepList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                    showContent();
                } else {
                    Toast.makeText(getContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                    showError(false);
                }
            }

            @Override
            public void onFailure(Call<SpoonacularResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Anggap error gagal jaringan
                showError(true);
            }
        });
    }
}
