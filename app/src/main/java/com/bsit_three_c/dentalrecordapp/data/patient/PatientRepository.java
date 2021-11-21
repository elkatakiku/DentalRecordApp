package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientRepository {

    private static final String TAG = PatientRepository.class.getSimpleName();

    private static volatile PatientRepository instance;
    private final PatientDataSource dataSource;
    private ValueEventListener valueEventListener;
    private ArrayList<Person> personArrayList;
    private boolean isPatientsLoaded = false;
    private final MutableLiveData<Boolean> isGettingPatientsDone = new MutableLiveData<>();

    private PatientRepository(PatientDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static PatientRepository getInstance(PatientDataSource dataSource) {
        if (instance == null) {
            instance = new PatientRepository(dataSource);
        }
        return instance;
    }

    public ArrayList<Person> getPatients(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null && this.personArrayList == null) {
            this.personArrayList = new ArrayList<>();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                Patient patient = data.getValue(Patient.class);
                if (!isDuplicate(patient)) {
                    this.personArrayList.add(patient);
                    Log.d(TAG, "getPatients: Added new patient to array list");
                } else Log.d(TAG, "getPatients: Patient already in array list");
            }
        }

        isPatientsLoaded = true;
        return this.personArrayList;
    }

    public void setAdapterChange(ItemAdapter itemAdapter) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isGettingPatientsDone.setValue(false);
                Log.d(TAG, "setAdapterChange: is getting patient done: " + isGettingPatientsDone.getValue());
//                itemAdapter.getItemCount();
                itemAdapter.setItems(getPatients(snapshot));
                itemAdapter.notifyDataSetChanged();
//                itemAdapter.notifyItemRangeChanged();

                isGettingPatientsDone.setValue(true);
                Log.d(TAG, "setAdapterChange: is getting patient done: " + isGettingPatientsDone.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        Log.d(TAG, "setAdapterChange: event listener: " + valueEventListener);
        dataSource.setValueListener(valueEventListener);
    }

    public void updatePatient() {
        // TODO: Update Patients here
        // Use notifyItemRangeChanged or notifyItemChanged
    }

    public void removePatient() {
        // TODO: Remove Patients here
    }

    public void removeValueEventListener() {
        dataSource.removeValueEventListener(valueEventListener);
    }

    private boolean isDuplicate(Patient patient) {
        return personArrayList.contains(patient);
    }

    public ArrayList<Person> getPersonArrayList() {
        return personArrayList;
    }

    public boolean getIsPatientsLoaded() {
        return isPatientsLoaded;
    }

    public LiveData<Boolean> getIsGettingPatientsDone() {
        return isGettingPatientsDone;
    }
}
