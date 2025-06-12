package com.example.greenplate.network;

import com.example.greenplate.Resep;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResepResponse {
    @SerializedName("results")
    private List<Resep> results;

    public List<Resep> getResults() {
        return results;
    }
}
