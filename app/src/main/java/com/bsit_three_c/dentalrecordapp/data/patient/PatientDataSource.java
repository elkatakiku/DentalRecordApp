package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;

import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PatientDataSource {

    private static final String TAG = "PatientDataSource";

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;
    private static final String FIREBASE_URL = "https://dental-record-app-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String USERS_REFERENCE = "patients";

    public PatientDataSource() {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(USERS_REFERENCE);
    }

    public void addPatient(Patient patient) {
        Log.d(TAG, "addPatient: Starting add patient task");
        databaseReference.push().setValue(patient);
        Log.d(TAG, "addPatient: exiting addPatient method");
    }

//    public void getPatients(ItemAdapter adapter) {
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    Patient patient = (Patient) data.getValue();
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    public void setValueListener(ValueEventListener listener) {
        databaseReference.addValueEventListener(listener);
    }

    public void removeValueEventListener(ValueEventListener listener) {
        databaseReference.removeEventListener(listener);
    }
}
