package com.bsit_three_c.dentalrecordapp.ui.home;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.add_patient.AddPatientRepository;
import com.bsit_three_c.dentalrecordapp.data.model.interfaces.Person;
import com.bsit_three_c.dentalrecordapp.util.Util;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {
    private static final String TAG = HomeViewModel.class.getSimpleName();

    private MutableLiveData<ArrayList<Person>> mPatientList = new MutableLiveData<>();
    private AddPatientRepository repository;
    private MutableLiveData<Boolean> isOnline = new MutableLiveData<>();

    HomeViewModel(AddPatientRepository repository) {
        this.repository = repository;
    }

    public LiveData<ArrayList<Person>> getmPatientList() {
        return mPatientList;
    }

    public void getPatients(ItemAdapter itemAdapter) {
        repository.setAdapterChange(itemAdapter);
    }

    public AddPatientRepository getRepository() {
        return repository;
    }

    public LiveData<Boolean> getIsOnline() {
        return isOnline;
    }

    public boolean isPatientsLoaded() {
        return repository.isPatientsLoaded();
    }

    public void removeEventListener() {
        repository.removeValueEventListener();
    }

    public void refresh(ItemAdapter adapter) {
        adapter.notifyDataSetChanged();
    }

    public void initializePatients(ItemAdapter adapter){
        Log.d(TAG, "initializePatients: getting arraylist: " + repository.getPersonArrayList());
        mPatientList.setValue(repository.getPersonArrayList());
        adapter.setItems(mPatientList.getValue());
    }

    public boolean isRecordEmpty() {
        return repository.getPersonArrayList() == null || repository.getPersonArrayList().isEmpty();
    }

    public void runTestInternet() {
        new Internet().execute();
    }

    public LiveData<Boolean> getIsPatientsGettingDone() {
        return repository.getIsGettingPatientsDone();
    }

    private class Internet extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            isOnline.setValue(aBoolean);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return Util.isOnline();
        }
    }
}