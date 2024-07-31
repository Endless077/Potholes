package com.example.potholes.Model;

public class Pothole {

    //Field
    private String username;
    private double latitudine;
    private double longitudine;
    private String indirizzo;

    //Constructor
    public Pothole() {}

    public Pothole(String username, double latitudine, double longitudine) {
        this.username = username;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }


    public Pothole(String username, double latitudine, double longitudine, String indirizzo) {
        this.username = username;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.indirizzo = indirizzo;
    }

    @Override
    public String toString() {
        return "Pothole{" +
                "Username='" + username + '\'' +
                ", latitudine=" + latitudine +
                ", longitudine=" + longitudine +
                '}';
    }

    //Getter and Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(double latitudine) {
        this.latitudine = latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(double longitudine) {
        this.longitudine = longitudine;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getLatLong(){ return latitudine + " - " + longitudine;}

}
