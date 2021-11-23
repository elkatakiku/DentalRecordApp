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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientRepository {

    private static final String TAG = PatientRepository.class.getSimpleName();

//    private static final String TAG = "PatientDataSource";

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;
    private static final String FIREBASE_URL = "https://dental-record-app-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String USERS_REFERENCE = "patients";

    private static volatile PatientRepository instance;
//    private final PatientDataSource dataSource;
    private ValueEventListener valueEventListener;
    private ArrayList<Person> personArrayList;
    private boolean isPatientsLoaded = false;
    private final MutableLiveData<Boolean> isGettingPatientsDone = new MutableLiveData<>();

    private PatientRepository() {
//        this.dataSource = dataSource;
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(USERS_REFERENCE);
    }

    public static PatientRepository getInstance() {
        if (instance == null) {
//            instance = new PatientRepository(dataSource);
            instance = new PatientRepository();
        }
        return instance;
    }

    public ArrayList<Person> getPatients(DataSnapshot dataSnapshot) {
//        if (dataSnapshot != null && this.personArrayList == null) {
        if (dataSnapshot != null) {
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

    public void getPatients(ItemAdapter itemAdapter) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: data changed");
                isGettingPatientsDone.setValue(false);
                Log.d(TAG, "setAdapterChange: is getting patient done: " + isGettingPatientsDone.getValue());
//                itemAdapter.getItemCount();
                itemAdapter.clearAll();
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
//        dataSource.setValueListener(valueEventListener);
        databaseReference.addValueEventListener(valueEventListener);
    }

    public void addPatients(Patient patient) {
//        dataSource.addPatient(patient);
        databaseReference.push().setValue(patient);
    }

    public void updatePatient() {
        // TODO: Update Patients here
        // Use notifyItemRangeChanged or notifyItemChanged
    }

    public void removePatient() {
        // TODO: Remove Patients here
    }

    public void removeValueEventListener() {
//        dataSource.removeValueEventListener(valueEventListener);
        databaseReference.removeEventListener(valueEventListener);
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
