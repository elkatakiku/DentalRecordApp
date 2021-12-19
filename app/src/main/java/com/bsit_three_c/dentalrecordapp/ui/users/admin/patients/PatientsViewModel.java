package com.bsit_three_c.dentalrecordapp.ui.users.admin.patients;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.util.Internet;

import java.util.ArrayList;

public class PatientsViewModel extends ViewModel {
    private static final String TAG = PatientsViewModel.class.getSimpleName();

    private final MutableLiveData<ArrayList<Person>> mPatientList = new MutableLiveData<>();
    private final PatientRepository repository;

    public PatientsViewModel(PatientRepository repository) {
        this.repository = repository;
    }

    public boolean isPatientsLoaded() {
        return repository.isPatientsLoaded();
    }

    public void removeEventListener() {
        Log.d(TAG, "removeEventListener: this is called");
        repository.removeValueEventListener();
    }

    public void refresh(ItemAdapter adapter) {
        adapter.notifyDataSetChanged();
    }

    public void initializePatients(ItemAdapter adapter) {
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
        return repository.isGettingPatientsDone();
    }

    public void loadPatients() {
        repository.getPatients();
    }

    public void initializeEventListener(ItemAdapter adapter) {
        repository.setAdapter(adapter);
    }

}