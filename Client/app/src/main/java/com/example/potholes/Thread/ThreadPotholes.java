package com.example.potholes.Thread;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.potholes.Model.Pothole;
import com.example.potholes.Presenter.HomePagePresenter;
import com.example.potholes.Presenter.LoginPresenter;
import com.example.potholes.Service.Network;

import java.util.List;
import java.util.Map;

public class ThreadPotholes extends Thread {

    private final Map<String,Double> location;
    private final Thread otherThread;
    private String range;

    private final LoginPresenter loginPresenter;
    private final HomePagePresenter homePagePresenter;

    private final String LOG = "ThreadGetter";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ThreadPotholes(LoginPresenter loginPresenter) {
        this.location = null;
        this.loginPresenter = loginPresenter;
        this.homePagePresenter = null;
        this.otherThread = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ThreadPotholes(Map<String, Double> location, String range, Thread getLocation, HomePagePresenter homePagePresenter) {
        this.location = location;
        this.loginPresenter = null;
        this.homePagePresenter = homePagePresenter;
        this.range = range;
        this.otherThread = getLocation;
    }

    @Override
    public void run() {
        Log.i(LOG,"Thread Start: " + Thread.currentThread().getName());
        if(homePagePresenter!=null) {
            try { otherThread.join(); } catch (InterruptedException ignored) {}
            List<Pothole> potholeList = homePagePresenter.getPotHoles(location, range);
            homePagePresenter.getmContext().getActivity().runOnUiThread(() -> homePagePresenter.viewUpload(potholeList, location));
        } else if(loginPresenter!=null) {
            Network network = new Network(loginPresenter.getContext().getActivity());
            network.getThreshold();
            if(Network.THRESHOLD!=1) {
                loginPresenter.getContext().getActivity().runOnUiThread(() -> {
                    loginPresenter.getContext().reset();
                    loginPresenter.getContext().goHomePage();
                });
            }
        }

    }
}
