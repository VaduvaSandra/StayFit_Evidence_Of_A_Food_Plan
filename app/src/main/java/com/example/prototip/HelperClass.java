package com.example.prototip;

public class HelperClass {

    String nume,email,username,datanastere,parola;

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDatanastere() {
        return datanastere;
    }

    public void setDatanastere(String datanastere) {
        this.datanastere = datanastere;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public HelperClass(String nume, String email, String username, String datanastere, String parola) {
        this.nume = nume;
        this.email = email;
        this.username = username;
        this.datanastere = datanastere;
        this.parola = parola;
    }

    public HelperClass() {
    }
}
