package com.example.potholes.Exception;

public class NoInternetConnectionException extends RuntimeException {

    private String cause;

    public NoInternetConnectionException() {}

    public NoInternetConnectionException(String cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "cause: " + cause;
    }

}
