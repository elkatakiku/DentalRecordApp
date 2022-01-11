package com.bsit_three_c.dentalrecordapp.ui.patients;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.util.Internet;

import java.util.List;

public class PatientsViewModel extends ViewModel {
    private static final String TAG = PatientsViewModel.class.getSimpleName();

    private final MutableLiveData<List<Person>> mPatientList = new MutableLiveData<>();
    private final PatientRepository repository;

    private final PatientRepository.PatientsListener patientsListener;

    public PatientsViewModel(PatientRepository repository) {
        this.repository = repository;
        patientsListener = new PatientRepository.PatientsListener(mPatientList);
    }

    public void removeEventListener() {
        Log.d(TAG, "removeEventListener: this is called");
        repository.getDatabaseReference().removeEventListener(patientsListener);
    }

    public void runInternetTest() {
        new Internet().execute();
    }

    public void loadPatients() {
        repository.getPatientsPath().addValueEventListener(patientsListener);
    }

    public LiveData<List<Person>> getmPatientList() {
        return mPatientList;
    }

}