package com.bsit_three_c.dentalrecordapp.ui.users.admin.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;

public class AdminDashboardViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static final String TAG = AdminDashboardViewModel.class.getSimpleName();

    private final PatientRepository patientRepository;
    private final ServiceRepository serviceRepository;

    public AdminDashboardViewModel(PatientRepository patientRepository, ServiceRepository serviceRepository) {
        this.patientRepository = patientRepository;
        this.serviceRepository = serviceRepository;
    }

    public void startCount() {
        patientRepository.countPatients();
        serviceRepository.countServices();
    }

    public LiveData<Long> getPatientsCount() {
        return patientRepository.getPatientCount();
    }

    public LiveData<Long> getServicesCount() {
        return serviceRepository.getServicesCount();
    }

    public void removeListeners() {
        patientRepository.removeValueEventListener();
        serviceRepository.removeListeners();
    }

}