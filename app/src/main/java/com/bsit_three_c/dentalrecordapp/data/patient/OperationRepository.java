package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bsit_three_c.dentalrecordapp.data.adapter.HistoryItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalOperation;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OperationRepository {

    private static final String TAG = OperationRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;
    private static final String FIREBASE_URL = "https://dental-record-app-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String OPERATIONS_REFERENCE = "operations";
    private static final String PATIENT_UID = "patient_uid";

    private static volatile OperationRepository instance;
    private ArrayList<DentalOperation> dentalOperations;

    public OperationRepository() {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(OPERATIONS_REFERENCE);
    }


    public static OperationRepository getInstance() {
        if (instance == null) instance = new OperationRepository();
        return instance;
    }

    private final ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void getOperations(DataSnapshot dataSnapshot) {

    }

    public void getOperations(HistoryItemAdapter itemAdapter) {

    }

    public String addOperationList(Patient patient, DentalOperation dentalOperation) {
        String key = databaseReference.push().getKey();
        if (patient.getDentalHistoryUID() == null && key != null) {
            databaseReference.child(key).setValue(dentalOperation);
        }


        return key;
    }

    public void addOperation(Patient patient, DentalOperation dentalOperation) {
        Log.d(TAG, "addOperation: getting dental history uid");
        databaseReference.child(patient.getDentalHistoryUID());
//        String dentalKey = ;
//        Log.d(TAG, "addOperation: new dental history uid : " + dentalKey);
        databaseReference.orderByChild(OPERATIONS_REFERENCE).equalTo(patient.getDentalHistoryUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: checking if patient's dental key exists");
                DatabaseReference reference = databaseReference;
                reference = databaseReference.child(patient.getDentalHistoryUID());
                if (!snapshot.exists()) {
                    Log.d(TAG, "onDataChange: dental exist");
//                    databaseReference.child(patient.getDentalHistoryUID()).setValue(dentalOperation);
                    if (patient.getDentalHistoryUID() != null) {
                        reference.child(PATIENT_UID).setValue(patient.getUid());
                    }
                }
                else {
                    Log.d(TAG, "onDataChange: dental key doen't exist");
                }

                String operationUID = databaseReference.push().getKey();
                dentalOperation.setUid(operationUID);

                Log.d(TAG, "onDataChange: dental added: " + dentalOperation);
                reference.child(dentalOperation.getUid()).setValue(dentalOperation);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
