package com.example.prototip;

/**
 * Clasa Meal reprezintă o entitate care stochează informații despre un aliment în cadrul unei mese.
 */
public class Meal {

    /**
     * Obține numele alimentului.
     *
     * @return Numele alimentului.
     */
    public String getFoodName() {
        return foodName;
    }

    /**
     * Setează numele alimentului.
     *
     * @param foodName Numele alimentului.
     */
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    /**
     * Obține numărul de calorii.
     *
     * @return Numărul de calorii.
     */
    public int getCalories() {
        return calories;
    }

    /**
     * Setează numărul de calorii.
     *
     * @param calories Numărul de calorii.
     */
    public void setCalories(int calories) {
        this.calories = calories;
    }

    /**
     * Obține cantitatea de proteine.
     *
     * @return Cantitatea de proteine.
     */
    public double getProtein() {
        return protein;
    }

    /**
     * Setează cantitatea de proteine.
     *
     * @param protein Cantitatea de proteine.
     */
    public void setProtein(double protein) {
        this.protein = protein;
    }

    /**
     * Obține cantitatea de grăsimi.
     *
     * @return Cantitatea de grăsimi.
     */
    public double getFat() {
        return fat;
    }

    /**
     * Setează cantitatea de grăsimi.
     *
     * @param fat Cantitatea de grăsimi.
     */
    public void setFat(double fat) {
        this.fat = fat;
    }

    /**
     * Obține cantitatea de carbohidrați.
     *
     * @return Cantitatea de carbohidrați.
     */
    public double getCarbohydrates() {
        return carbohydrates;
    }

    /**
     * Setează cantitatea de carbohidrați.
     *
     * @param carbohydrates Cantitatea de carbohidrați.
     */
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
