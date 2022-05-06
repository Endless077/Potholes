package com.example.potholes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomePageFragment extends Fragment {

    private String nickname;
    private TextView benvenutoTextView;

    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nickname = getArguments().getString("nickname");


        return inflater.inflate(R.layout.fragment_home_page, container, false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        benvenutoTextView = (TextView) getView().findViewById(R.id.ciaoUtenteTW);
        benvenutoTextView.setText(getResources().getString(R.string.ciaoUtente, nickname));

    }

    public String toString() {
        return "HomaPageFragment";
    }
}