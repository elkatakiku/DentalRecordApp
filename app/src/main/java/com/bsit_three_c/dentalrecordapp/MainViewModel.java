package com.bsit_three_c.dentalrecordapp;

import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.login.LoginRepository;
import com.bsit_three_c.dentalrecordapp.data.patient.PatientDataSource;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;

public class MainViewModel extends ViewModel {

    private final LoginRepository loginRepository;
    private final PatientDataSource dataSource;

    public MainViewModel(LoginRepository repository) {
        this.loginRepository = repository;
        this.dataSource = new PatientDataSource();
    }

    public void logout() {
        loginRepository.logout();
    }

    public void addPatient(Patient patient) {
        dataSource.addPatient(patient);
    }
}
