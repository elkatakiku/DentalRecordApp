package com.bsit_three_c.dentalrecordapp.ui.patients;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.util.Internet;

public class PatientsViewModel extends ViewModel {
    private static final String TAG = PatientsViewModel.class.getSimpleName();

    private final PatientRepository repository;
//    private final MutableLiveData<List<Person>> mPatientList;

//    private final PatientRepository.PatientsListener patientsListener;
    private PatientRepository.PatientsAdapterListener patientsAdapterListener;

    private final MutableLiveData<Boolean> hasPatient;

    public PatientsViewModel(PatientRepository repository) {
        this.repository = repository;
//        this.mPatientList = new MutableLiveData<>();
//        this.patientsListener = new PatientRepository.PatientsListener(mPatientList);
        this.hasPatient = new MutableLiveData<>();
    }

    public void runInternetTest() {
        new Internet().execute();
    }

    public void loadPatients() {
        Log.d(TAG, "loadPatients: adding listener");
//        repository.getPatientsPath().addValueEventListener(patientsListener);
        repository.getPatientsPath().addValueEventListener(patientsAdapterListener);
    }

//    public LiveData<List<Person>> getmPatientList() {
//        return mPatientList;
//    }

    public void setPatientsAdapterListener(ItemAdapter itemAdapter) {
        patientsAdapterListener = new PatientRepository.PatientsAdapterListener(itemAdapter, hasPatient);
    }

    public PatientRepository.PatientsAdapterListener getPatientsAdapterListener() {
        return patientsAdapterListener;
    }

    public LiveData<Boolean> getHasPatient() {
        return hasPatient;
    }

    public void removeEventListener() {
        Log.d(TAG, "removeEventListener: this is called");
//        repository.getDatabaseReference().removeEventListener(patientsListener);
        repository.getDatabaseReference().removeEventListener(patientsAdapterListener);
    }

}