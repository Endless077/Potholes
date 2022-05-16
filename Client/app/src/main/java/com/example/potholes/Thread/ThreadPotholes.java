package com.example.potholes.Thread;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.potholes.Presenter.HomePagePresenter;
import com.example.potholes.Presenter.LoginPresenter;
import com.example.potholes.Service.Network;

public class ThreadPotholes extends Thread {

    private Context mContext;
    private String range;

    private LoginPresenter loginPresenter;
    private HomePagePresenter homePagePresenter;

    private final String LOG = "ThreadGetter";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ThreadPotholes(Context context, LoginPresenter loginPresenter) {
        this.mContext = context;
        this.loginPresenter = loginPresenter;
        this.homePagePresenter = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ThreadPotholes(Context context, HomePagePresenter homePagePresenter, String range) {
        this.mContext = context;
        this.loginPresenter = null;
        this.homePagePresenter = homePagePresenter;
        this.range = range;
    }

    @Override
    public void run() {
        if(homePagePresenter!=null)
            homePagePresenter.getPotHoles(range);
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
