package com.bsit_three_c.dentalrecordapp.ui.menu;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Clinic;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.ClinicRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;

import java.util.ArrayList;

public class MenuClientViewModel extends ViewModel {
    private static final String TAG = MenuClientViewModel.class.getSimpleName();
    // TODO: Implement the ViewModel

    private final PatientRepository patientRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeeRepository employeeRepository;
    private final ClinicRepository clinicRepository;

    private final MutableLiveData<ArrayList<DentalService>> mDentalServices;
    private final MutableLiveData<ArrayList<Employee>> mEmployees;
    private final MutableLiveData<Patient> mPatient;
    private final MutableLiveData<Clinic> mCLinic;

    private final PatientRepository.PatientListener patientListener;
    private final ClinicRepository.ClinicListener clinicListener;
    private final ServiceRepository.ServicesEventListener servicesEventListener;
    private final EmployeeRepository.EmployeesEventListener employeesEventListener;

    public MenuClientViewModel() {
        this.patientRepository = PatientRepository.getInstance();
        this.serviceRepository = ServiceRepository.getInstance();
        this.employeeRepository = EmployeeRepository.getInstance();
        this.clinicRepository = (ClinicRepository) ClinicRepository.getInstance();

        this.mDentalServices = new MutableLiveData<>();
        this.mEmployees = new MutableLiveData<>();
        this.mPatient = new MutableLiveData<>();
        this.mCLinic = new MutableLiveData<>();

        this.patientListener = new PatientRepository.PatientListener(mPatient);
        this.clinicListener = new ClinicRepository.ClinicListener(mCLinic);
        this.servicesEventListener = new ServiceRepository.ServicesEventListener(mDentalServices);
        this.employeesEventListener = new EmployeeRepository.EmployeesEventListener(mEmployees);
    }

    public void loadData() {
        Log.d(TAG, "loadServices: called");
        serviceRepository.getServicesPath().addValueEventListener(servicesEventListener);
        employeeRepository.getEmployeesPath().addValueEventListener(employeesEventListener);
        clinicRepository.getDatabaseReference().addValueEventListener(clinicListener);
    }

    public void getPatient(String patietUid) {
        patientRepository.getPath(patietUid).addValueEventListener(patientListener);
    }

    public LiveData<ArrayList<DentalService>> getmDentalServices() {
        return mDentalServices;
    }

    public LiveData<ArrayList<Employee>> getmEmployees() {
        return mEmployees;
    }

    public LiveData<Clinic> getmCLinic() {
        return mCLinic;
    }

    public LiveData<Patient> getmPatient() {
        return mPatient;
    }

    public void removeListeners() {
        if (mPatient.getValue() != null) {
            patientRepository.getPath(mPatient.getValue().getUid()).removeEventListener(patientListener);
        }
        serviceRepository.getDatabaseReference().removeEventListener(servicesEventListener);
        employeeRepository.getDatabaseReference().removeEventListener(employeesEventListener);
        clinicRepository.getDatabaseReference().removeEventListener(clinicListener);
    }
}