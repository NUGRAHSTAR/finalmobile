package com.example.greenplate.home;

public class Recipe {
    private String title;
    private String description;
    private String imageUrl;

    // Constructor
    public Recipe(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getter
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
