package com.example.potholes.Thread;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.potholes.Exception.LocationNotFoundException;
import com.example.potholes.Presenter.HomePagePresenter;
import com.example.potholes.Service.Handler;

import java.util.Map;

public class ThreadSpotter extends Thread {

    private final Map<String, Double> location;
    private final HomePagePresenter homePagePresenter;
    private final Thread otherThread;

    private final String LOG = "ThreadSpotter";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ThreadSpotter(Map<String, Double> location, Thread getLocation, HomePagePresenter homePagePresenter) {
        this.location = location;
        this.homePagePresenter = homePagePresenter;
        this.otherThread = getLocation;
    }

    @Override
    public void run() {
        Log.i(LOG,"Thread Start: " + Thread.currentThread().getName());
        try { otherThread.join(); } catch (InterruptedException ignored) {}
        homePagePresenter.sendData(location);
    }

}
