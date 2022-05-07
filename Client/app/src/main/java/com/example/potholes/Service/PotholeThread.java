package com.example.potholes.Service;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class PotholeThread implements Runnable {

    private Context mContext;
    private final String LOG = "PotholeThread";

    private LocationManager lManager;
    private FusedLocationProviderClient fusedLocationClient;

    private SensorManager sMan;
    private Sensor accellerometer;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public PotholeThread(Context context) {
        this.mContext = context;
        gpsManager();
        accelerometerManager();
    }

    @Override
    public void run() {
        Log.i(LOG,"Thread Start");
        //running thread
    }

    public void gpsManager() {
        Log.i(LOG,"GPS service loading...");
        if(CheckService.isGpsOnline(mContext)) {
            this.lManager= (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        }else{
            CheckService.checkGpsEnabled((Activity) mContext,333);
        }
    }

    public void accelerometerManager() {
        Log.i(LOG,"Accelerometer service loading...");
        this.sMan = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        this.accellerometer = sMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener sel = new SensorEventListener() {

            public void onSensorChanged(SensorEvent sEvent) {
                final float alpha = 0.8f;

                //Gravità influenza il calcolo quindi prendiamo x,y,z e applichiamo
                //Ad = -g - F / massa
                //@AndroidDeveloper
                double[] gravity = new double[3];
                gravity[0] = alpha * gravity[0] + (1 - alpha) * sEvent.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * sEvent.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * sEvent.values[2];

                //Prendere l'accelerazione x,y,z reale del dispositivo
                double[] linear_acceleration = new double[3];
                linear_acceleration[0] = sEvent.values[0] - gravity[0];
                linear_acceleration[1] = sEvent.values[1] - gravity[1];
                linear_acceleration[2] = sEvent.values[2] - gravity[2];

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        };

        sMan.registerListener(sel,
                sMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
}
