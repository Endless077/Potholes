package com.example.potholes.Service;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class SensorApp {

    private final LocationManager lManager;
    private final SensorManager sMan;
    private Context mContext;
    private Sensor accellerometer;

    //private String s_name = Context.SENSOR_SERVICE;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public SensorApp(Context context) {
        this.mContext = context;
        this.lManager= (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        this.sMan = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        this.accellerometer = sMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void gpsManager() {

    }

    public void accellerometerManager() {

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
