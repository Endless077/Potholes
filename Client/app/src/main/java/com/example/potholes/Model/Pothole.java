package com.example.potholes.Model;

public class Pothole {

    //Field
    private String username;
    private double latitudine;
    private double longitudine;

    //Constructor
    public Pothole() {}

    public Pothole(String username, double latitudine, double longitudine) {
        this.username = username;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
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
}
