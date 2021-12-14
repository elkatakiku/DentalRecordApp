package com.bsit_three_c.dentalrecordapp.main_activity.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.login.LoginRepository;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;

public class MainAdminViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private final LoginRepository loginRepository;
    private LoggedInUser loggedInUser;

    private final MutableLiveData<LoggedInUser> mLoggedInUser = new MutableLiveData<>();

    private boolean isLoggedIn = false;

    public MainAdminViewModel(LoginRepository repository) {
        this.loginRepository = repository;
    }

    public void logout() {
        isLoggedIn = false;
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
        this.isLoggedIn = true;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}