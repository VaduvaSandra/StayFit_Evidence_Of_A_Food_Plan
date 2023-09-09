package com.example.prototip;

public class FoodInfo {
    private String foodName;
    private int calories;
    private float protein;
    private float fat;
    private float carbohydrates;
    private int mealSelection;

    public FoodInfo() {
        // Constructorul fără argumente necesar pentru Firebase Database
    }
    public int getMealSelection() {
        return mealSelection;
    }

    public void setMealSelection(int mealSelection) {
        this.mealSelection = mealSelection;
    }

    public FoodInfo(String foodName, int calories, float protein, float fat, float carbohydrates) {
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
    }

    public String getFoodName() {
        return foodName;
    }

    public int getCalories() {
        return calories;
    }

    public float getProtein() {
        return protein;
    }

    public float getFat() {
        return fat;
    }

    public float getCarbohydrates() {
        return carbohydrates;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public void setCarbohydrates(float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public void add(FoodInfo foodInfo) {
    }
}
