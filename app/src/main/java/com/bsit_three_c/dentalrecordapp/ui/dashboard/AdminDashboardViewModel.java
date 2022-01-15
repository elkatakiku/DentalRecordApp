package com.bsit_three_c.dentalrecordapp.ui.dashboard;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.repository.AppointmentRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.BaseRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;

public class AdminDashboardViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static final String TAG = AdminDashboardViewModel.class.getSimpleName();

    private final PatientRepository patientRepository;
    private final ServiceRepository serviceRepository;
    private final EmployeeRepository employeeRepository;
    private final AppointmentRepository appointmentRepository;

    private final MutableLiveData<Long> mPatientsCount = new MutableLiveData<>();
    private final MutableLiveData<Long> mServicesCount = new MutableLiveData<>();
    private final MutableLiveData<Long> mEmployeesCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> mTodayCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> mUpComingCount = new MutableLiveData<>();

    private final BaseRepository.CountChildren countPatient = new BaseRepository.CountChildren(mPatientsCount);
    private final BaseRepository.CountChildren countServices = new BaseRepository.CountChildren(mServicesCount);
    private final BaseRepository.CountChildren countEmployees = new BaseRepository.CountChildren(mEmployeesCount);
    private final AppointmentRepository.CountAppointment countAppointment = new AppointmentRepository.CountAppointment(mTodayCount, mUpComingCount);

    public AdminDashboardViewModel(PatientRepository patientRepository, ServiceRepository serviceRepository, EmployeeRepository employeeRepository) {
        this.patientRepository = patientRepository;
        this.serviceRepository = serviceRepository;
        this.employeeRepository = employeeRepository;
        this.appointmentRepository = AppointmentRepository.getInstance();
    }

    public void startCount() {
        Log.d(TAG, "startCount: called");
        patientRepository.getDatabaseReference().addValueEventListener(countPatient);
        serviceRepository.getDatabaseReference().addValueEventListener(countServices);
        employeeRepository.getDatabaseReference().addValueEventListener(countEmployees);
        appointmentRepository.getDatabaseReference().addValueEventListener(countAppointment);
    }

    public LiveData<Long> getPatientsCount() {
        return mPatientsCount;
    }

    public LiveData<Long> getServicesCount() {
        return mServicesCount;
    }

    public LiveData<Long> getEmployeesCount() {
        return mEmployeesCount;
    }

    public LiveData<Integer> getmTodayCount() {
        return mTodayCount;
    }

    public LiveData<Integer> getmUpComingCount() {
        return mUpComingCount;
    }

    public void removeListeners() {
        patientRepository.getDatabaseReference().removeEventListener(countPatient);
        serviceRepository.getDatabaseReference().removeEventListener(countServices);
        employeeRepository.getDatabaseReference().removeEventListener(countEmployees);
        appointmentRepository.getDatabaseReference().removeEventListener(countAppointment);
    }

}