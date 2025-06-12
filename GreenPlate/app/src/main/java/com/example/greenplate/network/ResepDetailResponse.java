package com.example.greenplate.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResepDetailResponse {

    private String title;
    private String image;
    private String instructions;

    @SerializedName("extendedIngredients")
    private List<Ingredient> ingredients;

    public ResepDetailResponse(String title, String image, String instructions) {
        this.title = title;
        this.image = image;
        this.instructions = instructions;
    }
    @SerializedName("id")
    private int id;
    @SerializedName("calories")
    private int calories;

    @SerializedName("protein")
    private String protein;
    @SerializedName("fat")
    private String fat;
    @SerializedName("carbs")
    private String carbs;

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getInstructions() {
        return instructions != null ? instructions : "Instruksi tidak tersedia";

    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public static class Ingredient {
        @SerializedName("original")
        private String name;

        public String getName() {
            return name;
        }
    }
}
