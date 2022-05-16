package com.example.potholes.Service;

import android.app.Activity;
import android.util.Log;

import com.example.potholes.Exception.LocationNotFoundException;
import com.example.potholes.Exception.NoGpsConnectionException;
import com.example.potholes.Exception.NoInternetConnectionException;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import es.dmoral.toasty.Toasty;

public class Handler {

    private static final String Handler = "Handler Error";
    private Handler() {}

    public static void handleException(Exception e, Activity a) {

        if(e instanceof LocationNotFoundException) {
            Log.e(Handler, "LocationNotFoundException: " + e.getMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.error(a,
                        "Location not found.",
                        Toasty.LENGTH_SHORT,true).show();
            });
        }else if(e instanceof NoInternetConnectionException) {
            Log.e(Handler, "NoInternetConnectionException: " + e.getMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.error(a,
                        "Internet Connection Not Found.",
                        Toasty.LENGTH_SHORT,true).show();
            });
        }else if(e instanceof NoGpsConnectionException) {
            Log.e(Handler, "NoGpsConnectionException: " + e.getMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.error(a,
                        "GPS Connection Not Found.",
                        Toasty.LENGTH_SHORT, true).show();
            });
        }else if(e instanceof InterruptedException) {
            Log.e(Handler,"InterruptedException: " + e.getMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.error(a,
                        "Thread Exception.",
                        Toasty.LENGTH_SHORT,true).show();
            });
        }else if(e instanceof IOException) {
            Log.e(Handler, "IOException: " + e.getMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.error(a,
                        "Server Not Available.",
                        Toasty.LENGTH_SHORT,true).show();
            });
        }else{
            Log.e(Handler, e.getClass() + ": " + e.getMessage());
            e.printStackTrace();
            a.runOnUiThread(() -> {
                Toasty.error(a,
                        "Errore Generico.",
                        Toasty.LENGTH_SHORT,true).show();
            });
        }
    }
}
