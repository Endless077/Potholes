package com.example.potholes.Presenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.potholes.Exception.LocationNotFoundException;
import com.example.potholes.Exception.NoGpsConnectionException;
import com.example.potholes.Model.Pothole;
import com.example.potholes.R;
import com.example.potholes.Service.CheckService;
import com.example.potholes.Service.Handler;
import com.example.potholes.Service.Network;
import com.example.potholes.Thread.ThreadSpotter;
import com.example.potholes.View.Fragment.HomePageFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    /*Manager*/
    private boolean gpsManager() {
        Log.i(LOG,"GPS service loading...");
        if(CheckService.isGpsOnline(mContext.getContext())) {
            if(lManager==null && fusedLocationClient==null) {
                this.lManager = (LocationManager) mContext.getContext().getSystemService(Context.LOCATION_SERVICE);
                this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext.getContext());
            }
            return true;
        }else{
            CheckService.checkGpsEnabled(mContext.getActivity(),333);
            Handler.handleException(new NoGpsConnectionException(), mContext.getActivity());
            return false;
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
                    Log.i(LOG,"Spotting hole...");
                    Map<String,Double> loc = new HashMap<>();

                    Thread getLocation = new Thread(() -> {
                        Log.i(LOG,"Thread getLocation started.");
                        getLocation(loc);
                    });

                    ThreadSpotter thread = new ThreadSpotter(loc, getLocation,HomePagePresenter.this);
                    getLocation.start();
                    thread.start();
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

    /*Tasks*/
    public void getLocation(Map<String, Double> loc) {
        Log.i(LOG,"Getting GPS Position");
        if(!gpsManager())
            return;

        if (ActivityCompat.checkSelfPermission(mContext.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(mContext.getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            CheckService.checkGpsEnabled(mContext.getActivity(), 333);
        }else {
            Log.i(LOG,"Getting Location");
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
                                getLocation(loc);
                            })
                            .setNegativeButton("Chiudi", (dialog, i) -> {
                            })
                            .show();
                }
            });

            //Wait getting coordinates
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }
    }

    public List<Pothole>
    getPotHoles(Map<String,Double> location, String range) {

        Network network = new Network(mContext.getActivity());

        if(range.equals("Tutti"))
            return network.getAllPotoles();
        else {
            List<Pothole> potholes = new ArrayList<>();
            double latitude, longitude, raggio, degree;
            if(!location.isEmpty()) {
                latitude = location.get("Latitude");
                longitude = location.get("Longitude");
                raggio = Double.parseDouble(range.substring(0,range.indexOf(" ")));
                degree = convertMetersToDegree(raggio);
                potholes = network.getNearPotholes(latitude,longitude,degree);
            }

            return potholes;
        }
    }

    public void sendData(Map<String, Double> loc) {
        Log.i(LOG,"Sending data...");

        Network network = new Network();

        if(!loc.isEmpty()) {
            Log.i(LOG,"Spotted here: " + loc.get("Latitude") + " - " + loc.get("Longitude"));
            mContext.getActivity().runOnUiThread(() -> Toasty.info(mContext.getContext(),
                    "Hole Spotted.",
                    Toasty.LENGTH_SHORT).show());
            network.insertNewPothole(loc.get("Latitude"), loc.get("Longitude"));
        }else{
            Log.e(LOG,"Location not found.");
            Handler.handleException(new LocationNotFoundException(), mContext.getActivity());
        }
    }

    private void unregisterListener() {
        sMan.unregisterListener(spotterEvent, accellerometer);
    }

    /*********************************************************************************************/

    /*Spotting*/
    public void startSpotting() {
        Log.i(LOG,"Starting recording...");
        mContext.getActivity().runOnUiThread(() -> Toasty.info(mContext.getContext(),"Starting recording...",
                Toasty.LENGTH_SHORT,true).show());
        accelerometerManager();
    }

    public void stopSpotting() {
        Log.i(LOG,"Stopping recording...");
        mContext.getActivity().runOnUiThread(() -> Toasty.info(mContext.getContext(),"Stopping recording...",
                Toasty.LENGTH_SHORT,true).show());
        unregisterListener();
    }

    /*********************************************************************************************/

    /*Utils*/
    public void setAddresses(List<Pothole> potholes) {
        List<Address> addresses;
        String address, city, postalCode;
        Geocoder geocoder;

        geocoder = new Geocoder(mContext.getContext(), Locale.getDefault());

        for(Pothole p : potholes) {
            try {
                addresses = geocoder.getFromLocation(p.getLatitudine(), p.getLongitudine(), 1);
                if(addresses.isEmpty())
                    p.setIndirizzo("Not found.");
                else{
                    address = addresses.get(0).getAddressLine(0);
                    city = addresses.get(0).getLocality();
                    postalCode = addresses.get(0).getPostalCode();
                    p.setIndirizzo(city + ", " + address + ", " + postalCode);
                }
            } catch (IOException e) {
                Log.i(LOG,"Set Address Error");
                Handler.handleException(e, mContext.getActivity());
            }
        }
    }

    private double convertMetersToDegree(double raggio) {
        double degreeBase = 0.0002777778;
        double convertedDegree = 0;

        if((raggio/30)>=0)
            convertedDegree = (raggio/30)*degreeBase;
        else
            Handler.handleException(new IllegalArgumentException(), mContext.getActivity());

        return convertedDegree;
    }

    public void viewUpload(List<Pothole> potholes, Map<String,Double> location) {
        mContext.upload(potholes, location);
    }

    public HomePageFragment getContext() {
        return mContext;
    }

    /*********************************************************************************************/

}
