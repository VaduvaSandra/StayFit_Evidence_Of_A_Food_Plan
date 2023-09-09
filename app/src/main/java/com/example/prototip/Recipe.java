package com.example.prototip;

import com.google.firebase.database.DataSnapshot;

//Clasa care reprezinta o reteta
public class Recipe {
    private String firebaseKey;
    private String nume;
    private String imagine;
    private String ingrediente;
    private String reteta;

    //Metoda pentru a obtine un snapshot asociat retetei in baza de date Firebase
    public DataSnapshot getSnapshot() {
        return snapshot;
    }

    //Metoda pentru a seta un snapshot asociat retetei in baza de date Firebase
    public void setSnapshot(DataSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    private DataSnapshot snapshot;

    //Constructor implicit necesar pentru Firebase Database
    public Recipe() {}

    //Constructor pentru reteta completa
    public Recipe(String firebaseKey, String nume, String imagine, String ingrediente, String reteta) {
        this.firebaseKey = firebaseKey;
        this.nume = nume;
        this.imagine = imagine;
        this.ingrediente = ingrediente;
        this.reteta = reteta;
    }

    //Constructor simplificat pentru o reteta fara detalii complete
    public Recipe(String nume, String imagine) {
        this.nume = nume;
        this.imagine = imagine;
    }

    //Metoda pentru a obtine numele retetei
    public String getName() {
        return nume;
    }

    //Metoda pentru a obtine URL-ul imaginii asociate retetei
    public String getImage() {
        return imagine;
    }

    //Metoda pentru a obtine lista de ingrediente ale retetei
    public String getIngredients() {
        return ingrediente;
    }

    //Metoda pentru a obtine instructiunile pentru prepararea retetei
    public String getInstructions() {
        return reteta;
    }

    //Metoda pentru obtinerea cheii unice asociate in baza de date Firebase
    public String getFirebaseKey() {
        return firebaseKey;
    }
}
