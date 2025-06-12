package com.example.greenplate;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ResepAdapter extends RecyclerView.Adapter<ResepAdapter.ResepViewHolder> {

    private Context context;
    private List<Resep> resepList;
    private OnResepClickListener listener;
    private SharedPreferences prefs;
    private Gson gson = new Gson();

    public interface OnResepClickListener {
        void onResepClick(Resep resep);
        void onFavoriteClick(Resep resep);
    }

    public ResepAdapter(Context context, List<Resep> resepList, OnResepClickListener listener) {
        this.context = context;
        this.resepList = resepList;
        this.listener = listener;
        this.prefs = context.getSharedPreferences("favorit_resep", Context.MODE_PRIVATE);
    }

    private List<Resep> getFavoritList() {
        String json = prefs.getString("favorit_list", "");
        if (json.isEmpty()) return new ArrayList<>();
        return gson.fromJson(json, new TypeToken<List<Resep>>(){}.getType());
    }

    private void saveFavoritList(List<Resep> list) {
        prefs.edit().putString("favorit_list", gson.toJson(list)).apply();
    }

    @NonNull
    @Override
    public ResepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resep, parent, false);
        return new ResepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResepViewHolder holder, int position) {
        Resep resep = resepList.get(position);
        holder.txtNamaResep.setText(resep.getNama());

        Glide.with(context)
                .load(resep.getGambarUrl())
                .placeholder(R.drawable.logo2)
                .into(holder.imgResep);

        List<Resep> favoritList = getFavoritList();

        boolean isFavorite = false;
        for (Resep r : favoritList) {
            if (r.getId() == resep.getId()) {
                isFavorite = true;
                break;
            }
        }

        holder.imgFavorite.setImageResource(isFavorite ? R.drawable.full : R.drawable.no);

        holder.imgFavorite.setOnClickListener(v -> {
            List<Resep> currentFavorites = getFavoritList();
            boolean currentlyFavorite = false;
            for (Resep r : currentFavorites) {
                if (r.getId() == resep.getId()) {
                    currentlyFavorite = true;
                    break;
                }
            }

            if (currentlyFavorite) {
                List<Resep> newList = new ArrayList<>();
                for (Resep r : currentFavorites) {
                    if (r.getId() != resep.getId()) {
                        newList.add(r);
                    }
                }
                saveFavoritList(newList);
                holder.imgFavorite.setImageResource(R.drawable.no);
            } else {
                // Tambah ke favorit
                currentFavorites.add(resep);
                saveFavoritList(currentFavorites);
                holder.imgFavorite.setImageResource(R.drawable.full);
            }
            notifyItemChanged(position);

            if (listener != null) {
                listener.onFavoriteClick(resep);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onResepClick(resep);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resepList.size();
    }

    public static class ResepViewHolder extends RecyclerView.ViewHolder {
        ImageView imgResep, imgFavorite;
        TextView txtNamaResep;

        public ResepViewHolder(@NonNull View itemView) {
            super(itemView);
            imgResep = itemView.findViewById(R.id.imgResep);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
            txtNamaResep = itemView.findViewById(R.id.txtNamaResep);
        }
    }
}
