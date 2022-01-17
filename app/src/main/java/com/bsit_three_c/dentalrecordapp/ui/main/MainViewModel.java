package com.bsit_three_c.dentalrecordapp.ui.main;

import android.app.Activity;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.LoginRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class MainViewModel extends ViewModel {

    private final LoginRepository loginRepository;
    private final PatientRepository patientRepository;

    private final MutableLiveData<LoggedInUser> mLoggedInUser = new MutableLiveData<>();
    private final MutableLiveData<Patient> mPatient = new MutableLiveData<>();

    private final PatientRepository.PatientListener patientListener;

    public MainViewModel() {
        this.loginRepository = LoginRepository.getInstance();
        this.patientRepository = PatientRepository.getInstance();

        this.patientListener = new PatientRepository.PatientListener(mPatient);
    }

    public LiveData<LoggedInUser> getmLoggedInUser() {
        return mLoggedInUser;
    }

    public void setmLoggedInUser(LoggedInUser loggedInUser) {
        mLoggedInUser.setValue(loggedInUser);
    }

    public void loadPatient(String patientUid) {
        patientRepository
                .getPath(patientUid)
                .addValueEventListener(patientListener);
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

    public void removeListeners() {
        patientRepository.getPath(mPatient.getValue().getUid()).removeEventListener(patientListener);
    }
}
