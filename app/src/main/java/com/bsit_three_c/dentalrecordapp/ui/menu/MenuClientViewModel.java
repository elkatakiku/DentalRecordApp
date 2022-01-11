package com.bsit_three_c.dentalrecordapp.ui.menu;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.listeners.EmployeesEventListener;
import com.bsit_three_c.dentalrecordapp.data.repository.listeners.ServicesEventListener;

import java.util.ArrayList;

public class MenuClientViewModel extends ViewModel {
    private static final String TAG = MenuClientViewModel.class.getSimpleName();
    // TODO: Implement the ViewModel

    private final ServiceRepository serviceRepository;
    private final EmployeeRepository employeeRepository;

    private final MutableLiveData<ArrayList<DentalService>> mDentalServices = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Employee>> mEmployees = new MutableLiveData<>();
    private final MutableLiveData<Patient> mPatient = new MutableLiveData<>();

    public MenuClientViewModel() {
        this.serviceRepository = ServiceRepository.getInstance();
        this.employeeRepository = EmployeeRepository.getInstance();
    }

    public void loadData() {
        Log.d(TAG, "loadServices: called");
        serviceRepository.getServicesPath().addValueEventListener(new ServicesEventListener(mDentalServices));
        employeeRepository.getEmployeesPath().addValueEventListener(new EmployeesEventListener(mEmployees));
    }

    public LiveData<ArrayList<DentalService>> getmDentalServices() {
        return mDentalServices;
    }

    public LiveData<ArrayList<Employee>> getmEmployees() {
        return mEmployees;
    }
}