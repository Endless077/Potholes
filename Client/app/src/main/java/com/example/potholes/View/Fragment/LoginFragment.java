package com.example.potholes.View.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.potholes.Presenter.LoginPresenter;
import com.example.potholes.R;
import com.example.potholes.Service.CheckService;
import com.example.potholes.View.MainActivity;
import com.google.android.material.textfield.TextInputLayout;

import es.dmoral.toasty.Toasty;

public class LoginFragment extends Fragment {

    //Local attribute
    private final String LOG = "Login";
    private LoginPresenter mLoginPresenter;

    private EditText nicknameEditText;
    private TextInputLayout layoutNickname;

    private Button loginButton;

    private Toolbar toolbar;

    public LoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoginPresenter = new LoginPresenter(this);

        nicknameEditText = getView().findViewById(R.id.editTextNickname);
        loginButton = getView().findViewById(R.id.accediButton);
        layoutNickname = getView().findViewById(R.id.editTextNicknameLayout);

        loginButton.setOnClickListener(v -> {
            String nickname = nicknameEditText.getText().toString();
            if(!nickname.matches("^[A-Za-z0-9_-]{3,10}$")) {
                Toasty.error(LoginFragment.this.getActivity(),"Nickname non conforme.",
                        Toasty.LENGTH_SHORT,true).show();
                layoutNickname.setError("Richeste solo lettere e/o numeri [3,10].");
            }else{
                mLoginPresenter.login(nickname);
            }
        });

        nicknameEditText.setOnTouchListener((v, event)->{
            layoutNickname.setErrorEnabled(false);
            return false;
        });
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

    public void goHomePage() {
        Toasty.success(getActivity(),"Login effettuato",
                Toasty.LENGTH_SHORT, true).show();

        ((MainActivity)getActivity()).changeFragment(((MainActivity)getActivity()).homePageFragment);
    }

    public void reset() {
        nicknameEditText.setText("");
    }

    public String toString(){
        return "LoginFragment";
    }

}