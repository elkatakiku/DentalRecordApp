package com.bsit_three_c.dentalrecordapp.data.repository.listeners;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class PatientListener implements ValueEventListener {
    private static final String TAG = PatientListener.class.getSimpleName();

    private final MutableLiveData<Patient> mPatient;

    public PatientListener(MutableLiveData<Patient> mPatient) {
        this.mPatient = mPatient;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        Log.d(TAG, "onDataChange: data changed in patient");

        Patient gotPatient = snapshot.getValue(Patient.class);

        if (gotPatient != null) {
            Log.d(TAG, "onDataChange: has patient");
            PatientRepository.initialize(gotPatient);
            mPatient.setValue(gotPatient);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
