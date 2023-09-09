package com.example.prototip;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeModel implements Parcelable {
    private String title;
    private String imageUrl;

    public RecipeModel() {
        // Constructorul implicit necesar pentru Firebase
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Implementare Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(imageUrl);
    }

    protected RecipeModel(Parcel in) {
        title = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<RecipeModel> CREATOR = new Creator<RecipeModel>() {
        @Override
        public RecipeModel createFromParcel(Parcel in) {
            return new RecipeModel(in);
        }

        @Override
        public RecipeModel[] newArray(int size) {
            return new RecipeModel[size];
        }
    };

    public String getImage() {
        return imageUrl;
    }
}
