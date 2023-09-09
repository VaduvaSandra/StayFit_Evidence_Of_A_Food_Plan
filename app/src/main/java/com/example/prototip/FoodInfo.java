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

    //Constructor cu argumente pentru a initializa obiectul FoodInfo
    public FoodInfo(String foodName, int calories, float protein, float fat, float carbohydrates) {
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
    }

    //Metoda pentru a obtine numele alimentului
    public String getFoodName() {
        return foodName;
    }

    //Metoda pentru a obtine numarul de calorii al alimentului
    public int getCalories() {
        return calories;
    }

    //Metoda pentru a obtine cantitatea de proteine a alimentului
    public float getProtein() {
        return protein;
    }

    //Metoda pentru a obtine cantitatea de grasimi a alimentului
    public float getFat() {
        return fat;
    }

    //Metoda pentru a obtine cantitatea de carbohidrati a alimentului
    public float getCarbohydrates() {
        return carbohydrates;
    }

    //Metoda pentru a seta numele alimentului
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    //Metoda pentru a seta numarul de calorii al alimentului
    public void setCalories(int calories) {
        this.calories = calories;
    }

    //Metoda pentru a seta cantitatea de proteine a alimentului
    public void setProtein(float protein) {
        this.protein = protein;
    }

    //Metoda pentru a seta cantitatea de grasimi a alimentului
    public void setFat(float fat) {
        this.fat = fat;
    }

    //Metoda pentru a seta cantitatea de carbohidrati a alimentului
    public void setCarbohydrates(float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public void add(FoodInfo foodInfo) {
        // Așteaptă să fie completată sau generează o excepție UnsupportedOperationException
    }
}
