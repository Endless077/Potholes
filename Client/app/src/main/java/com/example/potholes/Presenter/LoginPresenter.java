package com.example.potholes.Presenter;

import com.example.potholes.Service.Network;
import com.example.potholes.Thread.ThreadPotholes;
import com.example.potholes.View.Fragment.LoginFragment;

public class LoginPresenter {

    private final LoginFragment mContex;
    private final String LOG = "LoginPresenter";

    public LoginPresenter(LoginFragment loginFragment) {
        this.mContex = loginFragment;
    }

    public void login(String nickname) {
        ThreadPotholes thread = new ThreadPotholes(mContex.getContext(), this);
        thread.start();
        Network.NICKNAME = nickname;
    }

    public LoginFragment getmContex() {
        return mContex;
    }

}
