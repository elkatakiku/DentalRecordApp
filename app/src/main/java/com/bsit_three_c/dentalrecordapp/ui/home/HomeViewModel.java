package com.bsit_three_c.dentalrecordapp.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.patient.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.util.Internet;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();

    private final MutableLiveData<ArrayList<Person>> mPatientList = new MutableLiveData<>();
    private final PatientRepository repository;

    public HomeViewModel(PatientRepository repository) {
        this.repository = repository;
    }

    public PatientRepository getRepository() {
        return repository;
    }

    public boolean isPatientsLoaded() {
        return repository.getIsPatientsLoaded();
    }

    public void removeEventListener() {
        repository.removeValueEventListener();
    }

    public void refresh(ItemAdapter adapter) {
        adapter.notifyDataSetChanged();
    }

    public void initializePatients(ItemAdapter adapter) {
        Log.d(TAG, "initializePatients: getting arraylist: " + repository.getPersonArrayList());
        mPatientList.setValue(repository.getPersonArrayList());
        adapter.setItems(mPatientList.getValue());
    }

    public boolean isRecordEmpty() {
        return repository.getPersonArrayList() == null || repository.getPersonArrayList().isEmpty();
    }

    public void runInternetTest() {
        new Internet().execute();
    }

    public LiveData<Boolean> getIsPatientsGettingDone() {
        return repository.getIsGettingPatientsDone();
    }

}