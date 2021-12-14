package com.bsit_three_c.dentalrecordapp.ui.users.admin.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;

public class AdminDashboardViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private PatientRepository patientRepository;

    public AdminDashboardViewModel(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public void countPatients() {
        patientRepository.countPatients();
    }

    public LiveData<Long> getPatientsCount() {
        return patientRepository.getPatientCount();
    }

}