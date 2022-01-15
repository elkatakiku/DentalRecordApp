package com.bsit_three_c.dentalrecordapp.ui.menu;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.repository.LoginRepository;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class MenuAdminViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    LoginRepository loginRepository;

    private final MutableLiveData<LoggedInUser> mLoggedInUser;

    public MenuAdminViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;

        this.mLoggedInUser = new MutableLiveData<>();
    }

    public void setmLoggedInUser(LoggedInUser loggedInUser) {
        mLoggedInUser.setValue(loggedInUser);
    }

    public LiveData<LoggedInUser> getmLoggedInUser() {
        return mLoggedInUser;
    }

    public void logout(Context context) {
        LocalStorage.clearSavedUser(context);
//        isLogged = false;
        loginRepository.logout();
    }
}