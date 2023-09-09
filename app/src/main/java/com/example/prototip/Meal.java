package com.example.prototip;

/**
 * Clasa Meal reprezintă o entitate care stochează informații despre un aliment în cadrul unei mese.
 */
public class Meal {

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Meal(String foodName, int calories, double protein, double fat, double carbohydrates) {
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
    }

    //Numele alimentului
    private String foodName;
    //Numarul de calorii
    private int calories;
    //Cantitatea de proteine
    private double protein;
    //Cantitatea de grasimi
    private double fat;
    //Cantitatea de carbohudrati
    private double carbohydrates;
}
