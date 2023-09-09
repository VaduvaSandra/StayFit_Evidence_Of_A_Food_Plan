package com.example.prototip;

/**
 * Clasa pentru a reprezenta informațiile utilizatorului, inclusiv atributele precum vârsta, genul,
 * înălțimea, greutatea, nivelul de activitate, obiectivul, greutatea dorită și numele de utilizator.
 */
public class UserInfo {
    private int age;
    private String gender;
    private int height;
    private int weight;
    private String activityLevel;
    private String goal;
    private int desiredWeight;
    private String Username;

    public UserInfo() {
        // Constructor gol necesar pentru Firebase
    }

    //Constructor cu argumente pentru a initializa obiectul UserInfo
    public UserInfo(int age, String gender, int height, int weight, String activityLevel, String goal, int desiredWeight, String Username) {
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.desiredWeight = desiredWeight;
        this.Username = Username;
    }

    //Metoda pentru a obtine username-ul utilizatorului
    public String getUsername() {
        return Username;
    }

    //Metoda pentru a seta username-ul utilizatorului
    public void setUsername(String username) {
        this.Username = username;
    }

    //Metoda pentru a obtine varsta utilizatorului
    public int getAge() {
        return age;
    }

    //Metoda pentru a seta varsta utilizatorului
    public void setAge(int age) {
        this.age = age;
    }

    //Metoda pentru a obtine sex-ul utilizatorului
    public String getGender() {
        return gender;
    }

    //Metoda pentru a seta sex-ul utilizatorului
    public void setGender(String gender) {
        this.gender = gender;
    }

    //Metoda pentru a obtine inaltimea utilizatorului
    public int getHeight() {
        return height;
    }

    //Metoda pentru a seta inaltimea utilizatorului
    public void setHeight(int height) {
        this.height = height;
    }

    //Metoda pentru a obtine greutatea utilizatorului
    public int getWeight() {
        return weight;
    }

    //Metoda pentru a seta greutatea utilizatorului
    public void setWeight(int weight) {
        this.weight = weight;
    }

    //Metoda pentru a obtine nivelul de activitate al utilizatorului
    public String getActivityLevel() {
        return activityLevel;
    }

    //Metoda pentru a seta nivelul de activitate al utilizatorului
    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    //Metoda pentru a obtine obiectivul utilizatorului
    public String getGoal() {
        return goal;
    }

    //Metoda pentru a seta obiectivul utilizatorului
    public void setGoal(String goal) {
        this.goal = goal;
    }

    //Metoda pentru a obtine greutatea dorita de utilizator
    public int getDesiredWeight() {
        return desiredWeight;
    }

    //Metoda pentru seta greutatea dorita de utilizator
    public void setDesiredWeight(int desiredWeight) {
        this.desiredWeight = desiredWeight;
    }
}