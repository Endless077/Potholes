package com.example.potholes.Service;

import android.app.Activity;
import android.util.Log;

import com.example.potholes.Exception.LocationNotFoundException;
import com.example.potholes.Exception.NoGpsConnectionException;
import com.example.potholes.Exception.NoInternetConnectionException;
import com.example.potholes.Exception.PotholesNotFoundException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import es.dmoral.toasty.Toasty;

public class Handler {

    private static final String Handler = "Handler Error";
    private Handler() {}

    public static void handleException(Exception e, Activity a) {

        if(e instanceof LocationNotFoundException) {
            Log.e(Handler, "LocationNotFoundException: " + e.getLocalizedMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.warning(a,
                        "Location not found.",
                        Toasty.LENGTH_SHORT,true).show();
            });
        }else if(e instanceof PotholesNotFoundException) {
            Log.e(Handler, "PotholesNotFoundException " + e.getLocalizedMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.warning(a,
                        "Pothole not found.",
                        Toasty.LENGTH_SHORT,true).show();
            });
        }else if(e instanceof NoInternetConnectionException) {
            Log.e(Handler, "NoInternetConnectionException: " + e.getLocalizedMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.warning(a,
                        "Internet Connection Not Found.",
                        Toasty.LENGTH_SHORT,true).show();
            });
        }else if(e instanceof NoGpsConnectionException) {
            Log.e(Handler, "NoGpsConnectionException: " + e.getLocalizedMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.warning(a,
                        "GPS Connection Not Found.",
                        Toasty.LENGTH_SHORT, true).show();
            });
        }else if(e instanceof InterruptedException) {
            Log.e(Handler, "InterruptedException: " + e.getLocalizedMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.error(a,
                        "Service Interrupted.",
                        Toasty.LENGTH_SHORT, true).show();
            });
        }else if(e instanceof SocketTimeoutException) {
            Log.e(Handler, "SocketTimeoutException: " + e.getLocalizedMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.error(a,
                        "Timeout connection to server.",
                        Toasty.LENGTH_SHORT, true).show();
            });
        }else if(e instanceof IOException) {
            Log.e(Handler, "IOException: " + e.getLocalizedMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.error(a,
                        "Server Not Available.",
                        Toasty.LENGTH_SHORT, true).show();
            });
        }else{
            Log.e(Handler, e.getClass() + ": " + e.getLocalizedMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.error(a,
                        "Errore Generico.",
                        Toasty.LENGTH_SHORT,true).show();
            });
        }
    }
}
