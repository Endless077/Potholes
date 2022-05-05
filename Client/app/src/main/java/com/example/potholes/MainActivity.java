package com.example.potholes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    public Fragment loginFragment = new LoginFragment();
    public Fragment homePageFragment = new HomePageFragment();
    String actualFragment;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = (FrameLayout) findViewById(R.id.frameLayoutActivityMain);
        changeFragment(loginFragment);
    }


    public void changeFragment(Fragment fragmentToChange) {
        actualFragment = fragmentToChange.toString();
        if(actualFragment.equals(homePageFragment.toString())){
            Bundle bundle = new Bundle();
            bundle.putString("nickname",nickname);
            homePageFragment.setArguments(bundle);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutActivityMain, fragmentToChange);
        fragmentTransaction.commit();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}