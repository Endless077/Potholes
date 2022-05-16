package com.example.potholes.Thread;

import android.content.Context;
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

    private Map<String,Double> location;
    private String range;

    private LoginPresenter loginPresenter;
    private HomePagePresenter homePagePresenter;

    private final String LOG = "ThreadGetter";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ThreadPotholes(LoginPresenter loginPresenter) {
        this.location = null;
        this.loginPresenter = loginPresenter;
        this.homePagePresenter = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ThreadPotholes(Map<String,Double> location, HomePagePresenter homePagePresenter, String range) {
        this.location = location;
        this.loginPresenter = null;
        this.homePagePresenter = homePagePresenter;
        this.range = range;
    }

    @Override
    public void run() {
        if(homePagePresenter!=null) {
            List<Pothole> potholeList = homePagePresenter.getPotHoles(location, range);
            homePagePresenter.getmContext().getActivity().runOnUiThread(() -> homePagePresenter.viewUpload(potholeList, location));
        }
        else if(loginPresenter!=null) {
            Network network = new Network(loginPresenter.getmContex().getActivity());
            network.getThreshold();
            if(Network.THRESHOLD!=1) {
                loginPresenter.getmContex().getActivity().runOnUiThread(() -> {
                    loginPresenter.getmContex().reset();
                    loginPresenter.getmContex().goHomePage();
                });
            }
        }

    }
}
