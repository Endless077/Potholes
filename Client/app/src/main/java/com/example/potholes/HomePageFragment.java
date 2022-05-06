package com.example.potholes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class HomePageFragment extends Fragment {

    private String nickname;
    private TextView benvenutoTextView;
    private MapView mapView;
    private Button visualizzaBucheButton;
    private Button iniziaRegistrazioneButton;
    private Spinner spinner;
    private boolean isRecording = false;

    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Configuration.getInstance().setUserAgentValue("MyOwnUserAgent/1.0");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        nickname = getArguments().getString("nickname");


        return inflater.inflate(R.layout.fragment_home_page, container, false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        benvenutoTextView = (TextView) getView().findViewById(R.id.ciaoUtenteTW);
        mapView = (MapView) getView().findViewById(R.id.mapView);
        visualizzaBucheButton = (Button) getView().findViewById(R.id.visualizzaBucheButton);
        iniziaRegistrazioneButton = (Button) getView().findViewById(R.id.recordButton);
        spinner = (Spinner) getView().findViewById(R.id.distanzeSpinner);

        benvenutoTextView.setText(getResources().getString(R.string.ciaoUtente, nickname));

        ArrayAdapter<CharSequence> adapterDifficulty = ArrayAdapter.createFromResource(getActivity(),
                R.array.distanze, R.layout.dropdown_menu);
        adapterDifficulty.setDropDownViewResource(R.layout.dropdown_menu);
        spinner.setAdapter(adapterDifficulty);

        //INITIALIZING MAP
        mapView.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(40.863, 14.2767);
        IMapController mapController = mapView.getController();
        mapController.setZoom(9);
        mapController.setCenter(startPoint);


        //ESEMPIO MARKER USER
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(startPoint);
        startMarker.setIcon(getResources().getDrawable(R.mipmap.user));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(startMarker);
        mapView.invalidate();

        //ESEMPIO MARKER RO FUOSS
        Marker pothole = new Marker(mapView);
        GeoPoint potholeGeoPoint = new GeoPoint(40.1235, 14.6758);
        pothole.setPosition(potholeGeoPoint);
        pothole.setIcon(getResources().getDrawable(R.mipmap.cone));
        pothole.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(pothole);
        mapView.invalidate();

        iniziaRegistrazioneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isRecording){
                    isRecording = true;
                    iniziaRegistrazioneButton.setText(R.string.endRecord);
                }else{
                    isRecording = false;
                    iniziaRegistrazioneButton.setText(R.string.startRecord);
                }
            }
        });

    }

    @Override
    public String toString() {
        return "HomaPageFragment";
    }
}