package com.bsit_three_c.dentalrecordapp.ui.users.admin.menu;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.login.LoginRepository;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class MenuAdminViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    LoginRepository loginRepository;

    public MenuAdminViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public void logout(Context context) {
        LocalStorage.clearSavedUser(context);
//        isLogged = false;
        loginRepository.logout();
    }
}