package com.example.potholes.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.potholes.Presenter.MainActivityPresenter;
import com.example.potholes.R;
import com.example.potholes.View.Fragment.HomePageFragment;
import com.example.potholes.View.Fragment.LoginFragment;

public class MainActivity extends AppCompatActivity {

    //Local attribute
    private final String LOG = "MainActivity";
    private MainActivityPresenter mainActivityPresenter;

    private FrameLayout frameLayout;

    public Fragment loginFragment = new LoginFragment();
    public Fragment homePageFragment = new HomePageFragment();

    private String actualFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityPresenter = new MainActivityPresenter(this);

        frameLayout = (FrameLayout) findViewById(R.id.frameLayoutActivityMain);
        changeFragment(loginFragment);
    }


    public void changeFragment(Fragment fragmentToChange) {
        actualFragment = fragmentToChange.toString();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutActivityMain, fragmentToChange);
        fragmentTransaction.commit();
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

}