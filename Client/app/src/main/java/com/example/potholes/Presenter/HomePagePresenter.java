package com.example.potholes.Presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.potholes.Exception.LocationNotFoundException;
import com.example.potholes.Model.Pothole;
import com.example.potholes.R;
import com.example.potholes.Service.CheckService;
import com.example.potholes.Service.Handler;
import com.example.potholes.Service.Network;
import com.example.potholes.View.Fragment.HomePageFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class HomePagePresenter {

    private LocationManager lManager;
    private FusedLocationProviderClient fusedLocationClient;

    private final String LOG = "HomePagePresenter";

    private SensorManager sMan;
    private SensorEventListener spotterEvent;
    private Sensor accellerometer;

    private final HomePageFragment mContext;

    public HomePagePresenter(HomePageFragment homePageFragment) {
        this.mContext = homePageFragment;
    }

    /*********************************************************************************************/

    private void gpsManager() {
        Log.i(LOG,"GPS service loading...");
        if(CheckService.isGpsOnline(mContext.getContext())) {
            this.lManager = (LocationManager) mContext.getContext().getSystemService(Context.LOCATION_SERVICE);
            this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext.getContext());
        }else{
            CheckService.checkGpsEnabled(mContext.getActivity(),333);
        }
    }

    private void accelerometerManager() {
        Log.i(LOG,"Accelerometer service loading...");
        this.sMan = (SensorManager) mContext.getContext().getSystemService(Context.SENSOR_SERVICE);
        this.accellerometer = sMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener spotter = new SensorEventListener() {

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

                //Hole Spotter
                if (Math.abs(linear_acceleration[1]) > Network.THRESHOLD) {
                    Map<String,Double> loc = getLocation();
                    mContext.getActivity().runOnUiThread(() -> Toasty.info(mContext.getContext(),
                            "Hole Spotted.",
                            Toasty.LENGTH_SHORT).show());
                    if(!loc.isEmpty())
                        sendData(loc);
                    else{
                        mContext.getActivity().runOnUiThread(() -> Toasty.error(mContext.getActivity(),
                                "Location not found.",
                                Toasty.LENGTH_SHORT,true));
                        Handler.handleException(new LocationNotFoundException());
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        spotterEvent = spotter;

        sMan.registerListener(spotter,
                accellerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*********************************************************************************************/

    public Map<String, Double> getLocation() {
        Log.i(LOG,"Getting GPS Position");
        Map<String, Double> loc = new HashMap<>();
        if (ActivityCompat.checkSelfPermission(mContext.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(mContext.getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mContext.getActivity().runOnUiThread(() -> CheckService.checkGpsEnabled(mContext.getActivity(), 333));
            return null;
        }else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(mContext.getActivity(), location -> {
                if (location != null) {
                    Log.i(LOG, "Latitudine: " + location.getLatitude());
                    Log.i(LOG, "Longitudine: " + location.getLongitude());
                    loc.put("Latitude",location.getLatitude());
                    loc.put("Longitude",location.getLongitude());
                } else {
                    Log.i("GPS", "Nessuna location.");
                    new MaterialAlertDialogBuilder(mContext.getActivity(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
                            .setTitle("Errore GPS")
                            .setMessage("Il GPS è stato disattivato, verrai reindirizzato alla homepage.")
                            .setPositiveButton("Riprova", (dialogInterface, i) -> {
                                getLocation();
                            })
                            .setNegativeButton("Chiudi", (dialog, i) -> {})
                            .show();
                }
            });
            return loc;
        }
    }

    public List<GeoPoint> getPotHoles(String range) {
        Map<String,Double> location = getLocation();
        Network network = new Network();

        if(range.equals("Tutti"))
            return convertPotholes(network.getAllPotoles());
        else{
            double latitude, longitude, raggio;
            latitude = location.get("Latitude");
            longitude = location.get("Longitude");
            raggio = Double.parseDouble(range.substring(0, 3));
            return convertPotholes(network.getNearPotholes(latitude,longitude,raggio));
        }
    }

    private void sendData(Map<String,Double> loc) {
        Log.i(LOG,"Sending data...");
        Network network = new Network();
        network.insertNewPothole(loc.get("Latitude"), loc.get("Longitude"));
    }

    private List<GeoPoint> convertPotholes(List<Pothole> listPothole) {
        List<GeoPoint> geopoints = new ArrayList<>();
        for (Pothole pothole : listPothole)
            geopoints.add(new GeoPoint(pothole.getLatitudine(),pothole.getLongitudine()));

        return  geopoints;
    }

    private void unregisterListener() {
        sMan.unregisterListener(spotterEvent, accellerometer);
    }

    /*********************************************************************************************/

    public void startSpotting() {
        Log.i(LOG,"Starting recording...");
        mContext.getActivity().runOnUiThread(() -> Toasty.info(mContext.getContext(),"Starting recording...",
                Toasty.LENGTH_SHORT,true).show());
        gpsManager();
        accelerometerManager();
    }

    public void stopSpotting() {
        Log.i(LOG,"Stopping recording...");
        mContext.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toasty.info(mContext.getContext(),"Stopping recording...",
                        Toasty.LENGTH_SHORT,true).show();
            }
        });
        unregisterListener();
    }

    /*********************************************************************************************/
}
