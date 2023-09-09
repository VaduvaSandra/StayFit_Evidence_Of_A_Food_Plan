package com.example.prototip;

import com.google.firebase.database.DataSnapshot;

public class Recipe {
    private String firebaseKey;
    private String nume;
    private String imagine;
    private String ingrediente;
    private String reteta;

    public DataSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(DataSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    private DataSnapshot snapshot;

    public Recipe() {}

    public Recipe(String firebaseKey, String nume, String imagine, String ingrediente, String reteta) {
        this.firebaseKey = firebaseKey;
        this.nume = nume;
        this.imagine = imagine;
        this.ingrediente = ingrediente;
        this.reteta = reteta;
    }

    public Recipe(String nume, String imagine) {
        this.nume = nume;
        this.imagine = imagine;
    }

    public String getName() {
        return nume;
    }

    public String getImage() {
        return imagine;
    }

    public String getIngredients() {
        return ingrediente;
    }

    public String getInstructions() {
        return reteta;
    }
    public String getFirebaseKey() {
        return firebaseKey;
    }
}
