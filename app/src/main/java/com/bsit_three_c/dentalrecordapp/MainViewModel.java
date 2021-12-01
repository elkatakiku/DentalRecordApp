package com.bsit_three_c.dentalrecordapp;

import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.login.LoginRepository;

public class MainViewModel extends ViewModel {

    private final LoginRepository loginRepository;

    public MainViewModel(LoginRepository repository) {
        this.loginRepository = repository;
    }

    public void logout() {
        loginRepository.logout();
    }

}
