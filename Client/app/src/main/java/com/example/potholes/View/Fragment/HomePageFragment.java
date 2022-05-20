package com.example.potholes.View.Fragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potholes.Exception.PotholesNotFoundException;
import com.example.potholes.Model.Pothole;
import com.example.potholes.Presenter.HomePagePresenter;
import com.example.potholes.R;
import com.example.potholes.Service.Handler;
import com.example.potholes.Service.Network;
import com.example.potholes.Thread.ThreadPotholes;
import com.example.potholes.Thread.ThreadSpotter;
import com.example.potholes.View.MainActivity;
import com.example.potholes.View.PotholesAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePageFragment extends Fragment {

    //Local attribute
    private final String LOG = "HomePage";
    private HomePagePresenter mHomePagePresenter;

    private MapView mapView;

    private TextView benvenutoTextView;

    private ImageButton exitButton;
    private Button visualizzaBucheButton, changeViewButton, recordingButton;

    private Spinner spinner;

    private boolean isRecording = false;
    private boolean isViewingMap = true;

    private RecyclerView recyclerView;

    public HomePageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Configuration.getInstance().setUserAgentValue("MyOwnUserAgent/1.0");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Configurazione iniziale
        mHomePagePresenter = new HomePagePresenter(this);

        benvenutoTextView = getView().findViewById(R.id.ciaoUtenteTW);
        mapView = getView().findViewById(R.id.mapView);
        visualizzaBucheButton = getView().findViewById(R.id.visualizzaBucheButton);
        recordingButton = getView().findViewById(R.id.recordButton);
        changeViewButton = getView().findViewById(R.id.CambiaVisualizzazioneButton);
        spinner = getView().findViewById(R.id.distanzeSpinner);
        recyclerView = getView().findViewById(R.id.recyclerViewPotholes);
        exitButton = getView().findViewById(R.id.LogoutImageButton);

        benvenutoTextView.setText(getResources().getString(R.string.ciaoUtente, Network.NICKNAME));

        initMap();
        initRecyclerView();

        ArrayAdapter<CharSequence> adapterDifficulty = ArrayAdapter.createFromResource(getActivity(),
                R.array.distanze, R.layout.dropdown_menu);
        adapterDifficulty.setDropDownViewResource(R.layout.dropdown_menu);
        spinner.setAdapter(adapterDifficulty);

        changeViewButton.setOnClickListener(v -> {
            if(isViewingMap){
                isViewingMap = false;
                mapView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }else{
                isViewingMap = true;
                recyclerView.setVisibility(View.INVISIBLE);
                mapView.setVisibility(View.VISIBLE);
            }
        });

        recordingButton.setOnClickListener(v -> {
            if(!isRecording){
                isRecording = true;
                recordingButton.setText(R.string.endRecord);
                mHomePagePresenter.startSpotting();
            }else{
                isRecording = false;
                recordingButton.setText(R.string.startRecord);
                mHomePagePresenter.stopSpotting();
            }
        });

        exitButton.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(getContext(), R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Esci")
                .setMessage("Sei sicuro di voler uscire?")
                .setPositiveButton("Esci", (dialogInterface, i) -> {
                    ((MainActivity) getActivity()).changeFragment(((MainActivity) getActivity()).loginFragment);
                })
                .setNegativeButton("Annulla", (dialogInterface, i) -> {
                })
                .show());

        visualizzaBucheButton.setOnClickListener(v -> {
            Map<String,Double> loc = new HashMap<>();
            String range = spinner.getSelectedItem().toString();
            Thread getLocation = new Thread(() -> {
                Log.i(LOG, "Thread getLocation started.");
                mHomePagePresenter.getLocation(loc);
            });
            ThreadPotholes thread = new ThreadPotholes(loc, range, getLocation ,mHomePagePresenter);
            getLocation.start();
            thread.start();
        });

    }

    public void upload(List<Pothole> potholes, Map<String, Double> location) {
        Log.i(LOG,"Upload GUI...");
        clear();

        if(location.isEmpty())
            return;
        else if(potholes.isEmpty()){
            Handler.handleException(new PotholesNotFoundException(), getActivity());
            return;
        }

        mHomePagePresenter.setAddresses(potholes);
        uploadMap(potholes);
        uploadRecyclerView(potholes);
        userMarker(location);
    }

    public void initMap() {
        //Map configuration
        recyclerView.setVisibility(View.INVISIBLE);
        mapView.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(40.863, 14.2767);
        IMapController mapController = mapView.getController();
        mapController.setZoom(9);
        mapController.setCenter(startPoint);
    }

    private void uploadMap(List<Pothole> list) {
        for (Pothole p : list) {
            Marker pothole = new Marker(mapView);
            GeoPoint potholeGeoPoint = new GeoPoint(p.getLatitudine(), p.getLongitudine());
            pothole.setPosition(potholeGeoPoint);
            pothole.setTitle("Pothole");
            pothole.setSnippet(p.getIndirizzo());
            pothole.setIcon(getResources().getDrawable(R.mipmap.cone));
            pothole.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(pothole);
            mapView.invalidate();
        }
    }

    public void initRecyclerView() {
        //RecyclerView Configuration
        PotholesAdapter potholesAdapter = new PotholesAdapter(getActivity(), new ArrayList<>(), mHomePagePresenter);
        recyclerView.setAdapter(potholesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void uploadRecyclerView(List<Pothole> list) {
        PotholesAdapter adapter = (PotholesAdapter) recyclerView.getAdapter();
        adapter.clearList();
        adapter.setListCompilation(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG, "onResume Called.");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG, "onStart Called.");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG, "onPause Called.");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG, "onStop Called.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG, "onDestroy Called.");
    }

    @Override
    public String toString() {
        return "HomaPageFragment";
    }

    private void clear(){
        mapView.getOverlays().clear();
        PotholesAdapter adapter = (PotholesAdapter) recyclerView.getAdapter();
        adapter.clearList();
        adapter.notifyDataSetChanged();
    }

    private void userMarker(Map<String, Double> location) {
        if(location==null)
            return;

        Marker startMarker = new Marker(mapView);
        GeoPoint userGeopoint = new GeoPoint(location.get("Latitude"), location.get("Longitude"));
        startMarker.setPosition(userGeopoint);
        startMarker.setIcon(getResources().getDrawable(R.mipmap.user));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(startMarker);
        startMarker.setOnMarkerClickListener((marker, mapView) -> false);
        mapView.invalidate();
    }

}