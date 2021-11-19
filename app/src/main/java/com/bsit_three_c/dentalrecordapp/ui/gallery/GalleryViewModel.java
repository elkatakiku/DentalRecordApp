package com.bsit_three_c.dentalrecordapp.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.add_patient.AddPatientDataSource;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;

public class GalleryViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private AddPatientDataSource dataSource;

    public GalleryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Add a patient");

        dataSource = new AddPatientDataSource();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void addPatient(Patient patient) {
        dataSource.addPatient(patient);
    }


}