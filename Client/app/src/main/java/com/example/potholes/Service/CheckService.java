package com.example.potholes.Service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.potholes.Exception.NoGpsConnectionException;
import com.example.potholes.Exception.NoInternetConnectionException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class CheckService {

    private static final String LOG = "CHECK_SERVICE";

    public static int GPS_PERMISSION_CODE = 333;
    public static final int NETWORK_PERMISSION_CODE = 444;

    //Constructor private
    private CheckService() {}

    //Check is permission is granted
    public static boolean checkGpsEnabled(Activity context, int requestCode) {
        Log.i(LOG, "checkGpsEnabled: started.");
        if (checkFineLocation(context) && checkCoarseLocation(context)) {
            return true;
        } else {
            requestGPS(context, requestCode);
            return false;
        }
    }

    private static boolean checkFineLocation(Context context) {
        Log.i(LOG, "checkFineLocation: started.");
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean checkCoarseLocation(Context context) {
        Log.i(LOG, "checkCoarseLocation: started.");
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkNetworkEnabled(Activity context, int requestCode) {
        Log.i(LOG, "checkNetworkEnabled: started.");
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestNetwork(context, requestCode);
            return false;
        }
    }

    private static boolean checkChangeNetworkLocation(Context context) {
        Log.i(LOG, "checkChangeNetworkLocation: started.");
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean checkAccessNetworkLocation(Context context) {
        Log.i(LOG, "checkAccessNetworkLocation: started.");
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    //Check if activated
    public static boolean checkConnection(Activity a) {
        if(!CheckService.isOnline(a.getApplicationContext())) {
            Handler.handleException(new NoInternetConnectionException(), a);
            return false;
        }
        if(!CheckService.isGpsOnline(a.getApplicationContext())) {
            Handler.handleException(new NoGpsConnectionException(), a);
            return false;
        }
        return true;
    }

    public static boolean pingServer(Activity a) {
        Log.i(LOG, "isServerOnline: started.");

        String hostName = "localhost";
        int port = 8080;
        int timeout = 3*1000;

        SocketAddress socketAddress = new InetSocketAddress(hostName, port);

        try {
            Socket socket = new Socket(hostName, port);
            socket.connect(socketAddress, timeout);
            socket.close();
            Log.i(LOG, "isServerOnline: online.");
            return true;
        } catch (SocketTimeoutException exception) {
            Log.e(LOG, "SocketTimeoutException " + hostName + ":" + port + ". " + exception.getMessage());
            Handler.handleException(exception, a);
            return false;
        } catch (IOException exception) {
            Log.e(LOG, "IOException - Unable to connect to " + hostName + ":" + port + ". " + exception.getMessage());
            Handler.handleException(exception, a);
            return false;
        }
    }

    public static boolean isOnline(Context context) {
        Log.i(LOG, "isOnline: started.");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (ni != null && ni.isConnected());
    }

    public static boolean isGpsOnline(Context context) {
        Log.i(LOG, "isGpsOnline: started.");
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return (locationManager != null) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //Request permissions
    private static void requestGPS(Activity context, int requestCode) {
        Log.i(LOG, "requestGPS: started.");
        context.runOnUiThread(() -> {
            ActivityCompat.requestPermissions(context,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
            ActivityCompat.requestPermissions(context,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
        });
    }

    private static void requestNetwork(Activity context, int requestCode) {
        Log.i(LOG, "requestNetwork: started.");
        context.runOnUiThread(() -> {
//            ActivityCompat.requestPermissions((Activity) context,
//                    new String[] {Manifest.permission.CHANGE_NETWORK_STATE}, requestCode);
//            ActivityCompat.requestPermissions((Activity) context,
//                    new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, requestCode);
            ActivityCompat.requestPermissions((Activity) context,
                    new String[] {Manifest.permission.INTERNET}, requestCode);
        });
    }
}
