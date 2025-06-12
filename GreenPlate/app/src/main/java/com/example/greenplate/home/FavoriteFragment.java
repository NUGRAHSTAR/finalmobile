package com.example.greenplate.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenplate.R;
import com.example.greenplate.Resep;
import com.example.greenplate.ResepAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView rvFav;
    private TextView emptyView;
    private ResepAdapter resepAdapter;

    public FavoriteFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFav = view.findViewById(R.id.rv_fav);
        emptyView = view.findViewById(R.id.empty_view);

        rvFav.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavoritResep();
    }

    private void loadFavoritResep() {
        SharedPreferences prefs = requireContext().getSharedPreferences("favorit_resep", Context.MODE_PRIVATE);
        String json = prefs.getString("favorit_list", "");

        if (json.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("Belum ada resep favorit.");
            rvFav.setVisibility(View.GONE);
            return;
        }

        List<Resep> favoritList = new Gson().fromJson(json, new TypeToken<List<Resep>>(){}.getType());
        if (favoritList == null || favoritList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("Belum ada resep favorit.");
            rvFav.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            rvFav.setVisibility(View.VISIBLE);

            resepAdapter = new ResepAdapter(getContext(), favoritList, new ResepAdapter.OnResepClickListener() {
                @Override
                public void onResepClick(Resep resep) {
                    // Intent ke DetailResepActivity
                    Intent intent = new Intent(getContext(), DetailResepActivity.class);
                    intent.putExtra("resep_id", resep.getId());
                    startActivity(intent);
                }

                @Override
                public void onFavoriteClick(Resep resep) {
                    Toast.makeText(getContext(), "Favorited: " + resep.getNama(), Toast.LENGTH_SHORT).show();
                }
            });

            rvFav.setAdapter(resepAdapter);
        }
    }
}
