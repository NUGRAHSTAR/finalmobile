package com.example.greenplate.home;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.greenplate.R;
import com.example.greenplate.ResepFavoritManager;
import com.example.greenplate.network.ResepDetailResponse;
import com.example.greenplate.network.SpoonacularApi;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailResepActivity extends AppCompatActivity {
    private ResepFavoritManager resepFavoritManager;
    private Gson gson = new Gson();

    private ImageView imgResepDetail, backBtn;
    private TextView txtNamaResepDetail, txtIsiBahan, txtIsiCaraMasak;

    private SpoonacularApi api;
    private static final String API_KEY = "6278f30671e04195a0d0f5c1deaf802a";
    private static final String BASE_URL = "https://api.spoonacular.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_resep);

        resepFavoritManager = new ResepFavoritManager(this);
        gson = new Gson();

        imgResepDetail = findViewById(R.id.imgResepDetail);
        txtNamaResepDetail = findViewById(R.id.txtNamaResepDetail);
        txtIsiBahan = findViewById(R.id.txtIsiBahan);
        txtIsiCaraMasak = findViewById(R.id.txtIsiCaraMasak);
        backBtn = findViewById(R.id.back);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(SpoonacularApi.class);

        backBtn.setOnClickListener(v -> onBackPressed());

        int resepId = getIntent().getIntExtra("resep_id", -1);
        if (resepId != -1) {
            loadResepDetail(resepId);
        } else {
            Toast.makeText(this, "ID resep tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadResepDetail(int id) {

        if (!isNetworkAvailable()) {
            String savedJson = resepFavoritManager.getRecipeDetail(id);
            if (savedJson != null) {
                ResepDetailResponse detail = gson.fromJson(savedJson, ResepDetailResponse.class);
                tampilkanDetail(detail);
            } else {
                showError();
            }
            return;
        }

        Call<ResepDetailResponse> call = api.getRecipeDetail(id, true, API_KEY);
        call.enqueue(new Callback<ResepDetailResponse>() {
            @Override
            public void onResponse(Call<ResepDetailResponse> call, Response<ResepDetailResponse> response) {


                if (response.isSuccessful() && response.body() != null) {
                    ResepDetailResponse detail = response.body();
                    tampilkanDetail(detail);

                    // Simpan untuk offline
                    String json = gson.toJson(detail);
                    resepFavoritManager.saveRecipeDetail(detail.getId(), json);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<ResepDetailResponse> call, Throwable t) {
                showError();
            }
        });
    }

    private void tampilkanDetail(ResepDetailResponse detail) {
        txtNamaResepDetail.setText(detail.getTitle());

        StringBuilder bahanStr = new StringBuilder();
        if (detail.getIngredients() != null) {
            for (ResepDetailResponse.Ingredient ing : detail.getIngredients()) {
                bahanStr.append("- ").append(ing.getName()).append("\n");
            }
        }
        txtIsiBahan.setText(bahanStr.toString());
        txtIsiCaraMasak.setText(detail.getInstructions());

        Glide.with(this)
                .load(detail.getImage())
                .placeholder(R.drawable.logo2)
                .error(R.drawable.logo2)
                .into(imgResepDetail);
    }

    private void showError() {
        Toast.makeText(this, "Gagal memuat detail resep", Toast.LENGTH_SHORT).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
