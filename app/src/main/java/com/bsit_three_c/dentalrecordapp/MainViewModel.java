package com.bsit_three_c.dentalrecordapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.login.LoginRepository;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;

public class MainViewModel extends ViewModel {

    private final LoginRepository loginRepository;
    private LoggedInUser loggedInUser;

    private final MutableLiveData<LoggedInUser> mLoggedInUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();

    private boolean isLogged = false;

    public MainViewModel(LoginRepository repository) {
        this.loginRepository = repository;
    }

    public void logout() {
        isLogged = false;
        loginRepository.logout();
    }

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
    }

    public LiveData<LoggedInUser> getmLoggedInUser() {
        return mLoggedInUser;
    }

    public void setLoggedInUser(LoggedInUser loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.mLoggedInUser.setValue(loggedInUser);
        this.isLogged = true;
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }
}
