package com.example.prototip;

/**
 * Clasa reprezentând un element din meniu, conținând o imagine și un titlu.
 */
public class MenuItem {
    private int imageResource;
    private String title;

    //Constructorul clasei MenuItem
    public MenuItem(int imageResource, String title) {
        this.imageResource = imageResource;
        this.title = title;
    }

    //Obtine identificatorul resursei imaginii
    public int getImageResource() {
        return imageResource;
    }

    //Obtine titlul elementului din meniu
    public String getTitle() {
        return title;
    }
}
