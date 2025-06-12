package com.example.greenplate;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final Context context;
    private final List<Resep> recipeList;
    private final OnResepClickListener listener;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public interface OnResepClickListener {
        void onResepClick(Resep resep);
        void onFavoriteClick(Resep resep);
    }

    public RecipeAdapter(Context context, List<Resep> recipeList, OnResepClickListener listener) {
        this.context = context;
        this.recipeList = recipeList;
        this.listener = listener;
        this.prefs = context.getSharedPreferences("favorit_resep", Context.MODE_PRIVATE);
    }

    private List<Resep> getFavoritList() {
        String json = prefs.getString("favorit_list", "");
        if (json.isEmpty()) return new ArrayList<>();
        return gson.fromJson(json, new TypeToken<List<Resep>>() {}.getType());
    }

    private void saveFavoritList(List<Resep> list) {
        prefs.edit().putString("favorit_list", gson.toJson(list)).apply();
    }

    private boolean isFavorited(Resep resep) {
        List<Resep> list = getFavoritList();
        for (Resep r : list) {
            if (r.getId() == resep.getId()) return true;
        }
        return false;
    }

    private void toggleFavorite(Resep resep) {
        List<Resep> list = getFavoritList();
        boolean found = false;

        for (Resep r : list) {
            if (r.getId() == resep.getId()) {
                found = true;
                break;
            }
        }

        if (found) {
            // Remove
            List<Resep> newList = new ArrayList<>();
            for (Resep r : list) {
                if (r.getId() != resep.getId()) newList.add(r);
            }
            saveFavoritList(newList);
        } else {
            // Add
            list.add(resep);
            saveFavoritList(list);
        }
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Resep resep = recipeList.get(position);

        holder.menuNameTextView.setText(resep.getNama());

        Glide.with(context)
                .load(resep.getGambarUrl())
                .placeholder(R.drawable.logo2)
                .into(holder.menuImageView);

        // Set ikon favorite
        boolean isFav = isFavorited(resep);
        holder.favoriteButton.setImageResource(isFav ? R.drawable.full : R.drawable.no);

        // Klik item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onResepClick(resep);
        });

        // Klik favorite
        holder.favoriteButton.setOnClickListener(v -> {
            toggleFavorite(resep);
            notifyItemChanged(holder.getAdapterPosition()); // update icon
            if (listener != null) listener.onFavoriteClick(resep);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView menuImageView;
        TextView menuNameTextView;
        ImageView favoriteButton;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            menuImageView = itemView.findViewById(R.id.imgResep);
            menuNameTextView = itemView.findViewById(R.id.txtNamaResep);
            favoriteButton = itemView.findViewById(R.id.imgFavorite);
        }
    }
}
