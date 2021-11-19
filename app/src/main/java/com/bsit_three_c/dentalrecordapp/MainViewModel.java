package com.bsit_three_c.dentalrecordapp;

import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.LoginRepository;
import com.bsit_three_c.dentalrecordapp.data.add_patient.AddPatientDataSource;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;

public class MainViewModel extends ViewModel {

    private LoginRepository loginRepository;
    private AddPatientDataSource dataSource;

    public MainViewModel(LoginRepository repository) {
        this.loginRepository = repository;
        this.dataSource = new AddPatientDataSource();
    }

    public void logout() {
        loginRepository.logout();
    }

    public void addPatient(Patient patient) {
        dataSource.addPatient(patient);
    }
}
