package com.bsit_three_c.dentalrecordapp.ui.main;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.LoginRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainViewModel extends ViewModel {

    private final LoginRepository loginRepository;
    private final PatientRepository patientRepository;

    private final MutableLiveData<LoggedInUser> mLoggedInUser = new MutableLiveData<>();
    private final MutableLiveData<Patient> mPatient = new MutableLiveData<>();

    public MainViewModel() {
        this.loginRepository = LoginRepository.getInstance();
        this.patientRepository = PatientRepository.getInstance();
    }

    public LiveData<LoggedInUser> getmLoggedInUser() {
        return mLoggedInUser;
    }

    public void setmLoggedInUser(LoggedInUser loggedInUser) {
        mLoggedInUser.setValue(loggedInUser);
    }

    public void loadPatient(String patientUid) {
        patientRepository.getPath(patientUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Patient patient = snapshot.getValue(Patient.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public MutableLiveData<Patient> getmPatient() {
        return mPatient;
    }

    public void logout(Activity activity) {
        LocalStorage.clearSavedUser(activity.getApplicationContext());
        loginRepository.logout();
        activity.startActivity(new Intent(activity, MainActivity.class));
        activity.finish();
    }
}
