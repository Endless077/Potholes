package com.example.potholes.Exception;

public class PotholesNotFoundException extends Exception {

    private String cause;

    public PotholesNotFoundException() {}

    public PotholesNotFoundException(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "cause: " + cause;
    }

}
