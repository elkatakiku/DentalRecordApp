package com.bsit_three_c.dentalrecordapp;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.LoginDataSource;
import com.bsit_three_c.dentalrecordapp.data.LoginRepository;

public class SampleViewModel extends ViewModel {

    private LoginRepository loginRepository;

    public SampleViewModel(LoginRepository repository) {
        this.loginRepository = repository;
    }

    public void logout() {
        loginRepository.logout();
    }
}
