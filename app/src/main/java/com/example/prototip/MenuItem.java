package com.example.prototip;

public class MenuItem {
    private int imageResource;
    private String title;

    public MenuItem(int imageResource, String title) {
        this.imageResource = imageResource;
        this.title = title;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTitle() {
        return title;
    }
}
