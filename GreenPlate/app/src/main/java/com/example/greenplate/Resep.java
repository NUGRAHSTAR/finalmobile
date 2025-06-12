package com.example.greenplate;

import com.google.gson.annotations.SerializedName;

public class Resep {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String nama;

    @SerializedName("image")
    private String gambarUrl;
    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getGambarUrl() {
        return gambarUrl;
    }
}
