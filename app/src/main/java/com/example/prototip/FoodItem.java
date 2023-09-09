package com.example.prototip;

public class FoodItem {
    // Variabile pentru a stoca titlul, descrierea si URL-ul imaginii alimentului
    private String title;
    private String description;
    private String imageUrl;

    // Constructor pentru a inițializa un obiect FoodItem cu titlu, descriere și URL al imaginii
    public FoodItem(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    //Metoda pentru a obtine titlul elementului
    public String getTitle() {
        return title;
    }

    //Metoda pentru a obtine descrierea alimentului
    public String getDescription() {
        return description;
    }

    //Metoda pentru a obtine URL-ul imaginii alimentului
    public String getImageUrl() {
        return imageUrl;
    }
}
