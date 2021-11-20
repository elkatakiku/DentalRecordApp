package com.bsit_three_c.dentalrecordapp.data.add_patient;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddPatientDataSource {

    private static final String TAG = "AddPatientDataSource";

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;
    private final String FIREBASE_URL = "https://dental-record-app-default-rtdb.asia-southeast1.firebasedatabase.app";
    private final String USERS_REFERENCE = "patients";

    public AddPatientDataSource() {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(USERS_REFERENCE);
    }

    public FirebaseDatabase getDatabase() {
        return database;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void addPatient(Patient patient) {
        Log.d(TAG, "addPatient: Starting add patient task");
        databaseReference.push().setValue(patient);
        Log.d(TAG, "addPatient: exiting addpatient method");
    }

    public void getPatients(ItemAdapter adapter) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Patient patient = (Patient) data.getValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setValueListener(ValueEventListener listener) {
        databaseReference.addValueEventListener(listener);
    }

    public void removeValueEventListener(ValueEventListener listener) {
        databaseReference.removeEventListener(listener);
    }
}
