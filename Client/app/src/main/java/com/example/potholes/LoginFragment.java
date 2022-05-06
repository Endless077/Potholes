package com.example.potholes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    private TextInputLayout layoutNickname;
    private EditText nicknameEditText;
    private Button loginButton;
    private Toolbar toolbar;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nicknameEditText = (EditText) getView().findViewById(R.id.editTextNickname);
        loginButton = (Button) getView().findViewById(R.id.accediButton);
        layoutNickname = (TextInputLayout) getView().findViewById(R.id.editTextNicknameLayout);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickname = nicknameEditText.getText().toString();
                if(nickname.isEmpty()){
                    layoutNickname.setError("Nickname vuoto");
                }else{
                    ((MainActivity)getActivity()).setNickname(nickname);
                    ((MainActivity)getActivity()).changeFragment(((MainActivity)getActivity()).homePageFragment);
                }
            }
        });

        nicknameEditText.setOnTouchListener((v, event)->{
            layoutNickname.setErrorEnabled(false);
            return false;
        });
    }

    public String toString(){
        return "LoginFragment";
    }
}