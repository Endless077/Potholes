package com.example.potholes.Service;

import android.util.Log;

import com.example.potholes.Exception.LocationNotFoundException;

import java.io.IOException;

public class Handler {

    private static final String Handler = "Handler Error";
    private Handler() {}

    public static void handleException(Exception e) {

        if(e instanceof LocationNotFoundException){
            Log.e(Handler,"LocationNotFoundException: " + e.getMessage());
            e.printStackTrace();
        }else if(e instanceof InterruptedException) {
            Log.e(Handler,"InterruptedException: " + e.getMessage());
            e.printStackTrace();
        }else if(e instanceof IOException) {
            Log.e(Handler, "IOException: " + e.getMessage());
            e.printStackTrace();
        }else{
            Log.e(Handler, e.getClass() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
