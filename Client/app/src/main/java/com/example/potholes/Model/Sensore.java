package com.example.potholes.Model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.potholes.Service.Comunication;

public class Sensore {

    private Context mContext;
    private double thrashold;
    private Sensor accellerometer;
    private SensorManager sMan;

    //private String s_name = Context.SENSOR_SERVICE;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Sensore(Context context) {
        this.mContext = context;
        this.thrashold = Comunication.getThreshold();
        this.sMan = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        this.accellerometer = sMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void AccelerometerManager() {

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
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        };

        sMan.registerListener(sel,
                sMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

}
