package com.example.potholes.Thread;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.potholes.Presenter.HomePagePresenter;

public class ThreadSpotter implements Runnable {

    private Context mContext;
    private HomePagePresenter homePagePresenter;

    private final String LOG = "ThreadSpotter";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ThreadSpotter(Context context, HomePagePresenter homePagePresenter) {
        this.mContext = context;
        this.homePagePresenter = homePagePresenter;
    }

    @Override
    public void run() {
        Log.i(LOG,"Thread Start: " + Thread.currentThread().getName());
        homePagePresenter.startSpotting();
        while(!Thread.currentThread().isInterrupted()) {}
        homePagePresenter.stopSpotting();
    }

}
