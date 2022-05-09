package com.example.potholes.Thread;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.potholes.Presenter.HomePagePresenter;

public class ThreadGetter implements Runnable {

    private Context mContext;
    private String range;
    private HomePagePresenter homePagePresenter;

    private final String LOG = "ThreadGetter";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public ThreadGetter(Context context, HomePagePresenter homePagePresenter) {
        this.mContext = context;
        this.homePagePresenter = homePagePresenter;
    }

    @Override
    public void run() {
        homePagePresenter.getPotHoles(range);
    }
}
