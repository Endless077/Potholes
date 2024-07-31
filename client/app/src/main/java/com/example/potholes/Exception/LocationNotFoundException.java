package com.example.potholes.Exception;

public class LocationNotFoundException extends RuntimeException {

    private String cause;

    public LocationNotFoundException() {}

    public LocationNotFoundException(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "cause: " + cause;
    }

}
