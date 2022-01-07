package com.bsit_three_c.dentalrecordapp.ui.dashboard;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;

public class AdminDashboardViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static final String TAG = AdminDashboardViewModel.class.getSimpleName();

    private final PatientRepository patientRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeeRepository employeeRepository;

    public AdminDashboardViewModel(PatientRepository patientRepository, ServiceRepository serviceRepository, EmployeeRepository employeeRepository) {
        this.patientRepository = patientRepository;
        this.serviceRepository = serviceRepository;
        this.employeeRepository = employeeRepository;
    }

    public void startCount() {
        Log.d(TAG, "startCount: called");
        patientRepository.countPatients();
        serviceRepository.countServices();
        employeeRepository.countEmployees();
    }

    public LiveData<Long> getPatientsCount() {
        return patientRepository.getPatientCount();
    }

    public LiveData<Long> getServicesCount() {
        return serviceRepository.getServicesCount();
    }

    public LiveData<Long> getEmployeesCount() {
        return employeeRepository.getEmployeesCount();
    }

    public void removeListeners() {
        patientRepository.removeValueEventListener();
        serviceRepository.removeListeners();
    }

}