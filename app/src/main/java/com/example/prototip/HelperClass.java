package com.example.prototip;

/**
 * Clasa HelperClass reprezintă o clasă de ajutor pentru gestionarea informațiilor utilizatorului.
 */

public class HelperClass {

    //Atributele ce reprezinta informatiile utilizatorului
    String nume;
    String email;
    String username;
    String datanastere;
    String parola;

    //Metoda pentru a obtine numele utilizatorului
    public String getNume() {
        return nume;
    }

    //Metoda pentru a seta numele utilizatorului
    public void setNume(String nume) {
        this.nume = nume;
    }

    //Metoda pentru a obtine adresa de email a utilizatorului
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //Metoda pentru a obtine numele de utilizator
    public String getUsername() {
        return username;
    }

    //Metoda pentru a seta numele de utilizator
    public void setUsername(String username) {
        this.username = username;
    }

    //Metoda pentru a seta data de nastere a utilizatorului
    public String getDatanastere() {
        return datanastere;
    }

    //Metoda pentru a seta data de nastere a utilizatorului
    public void setDatanastere(String datanastere) {
        this.datanastere = datanastere;
    }

    //Metoda pentru a obtine parola utilizatorului
    public String getParola() {
        return parola;
    }

    //Metoda pentru a seta parola utilizatorului
    public void setParola(String parola) {
        this.parola = parola;
    }

    //Constructor cu paramentrii pentru initializarea obiectului HelperClass cu informatiile utilizatorului
    public HelperClass(String nume, String email, String username, String datanastere, String parola) {
        this.nume = nume;
        this.email = email;
        this.username = username;
        this.datanastere = datanastere;
        this.parola = parola;
    }

    // Constructor fără argumente necesar pentru Firebase Database
    public HelperClass() {
    }
}
