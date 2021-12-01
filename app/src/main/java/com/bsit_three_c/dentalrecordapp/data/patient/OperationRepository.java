package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.bsit_three_c.dentalrecordapp.data.adapter.HistoryItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalOperation;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class OperationRepository {

    private static final String TAG = OperationRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;
    private static final String FIREBASE_URL = "https://dental-record-app-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String OPERATIONS_REFERENCE = "operations";
    private static final String PATIENT_UID = "patient_uid";
    private static final String DENTAL_HISTORY = "dental_records";

    private ValueEventListener valueEventListener;

    private static volatile OperationRepository instance;
    private ArrayList<DentalOperation> dentalOperations;
//    private MutableLiveData<Boolean> mIsOperationsLoaded = new MutableLiveData<>();

    public OperationRepository() {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(OPERATIONS_REFERENCE);
    }


    public static OperationRepository getInstance() {
        if (instance == null) instance = new OperationRepository();
        return instance;
    }

    private ArrayList<DentalOperation> getOperations(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            dentalOperations = new ArrayList<>();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                DentalOperation dentalOperation = data.getValue(DentalOperation.class);
                if (dentalOperation != null && !isDuplicate(dentalOperation)) {
                    Log.d(TAG, "getOperations: dentalOp: " + dentalOperation);
                    this.dentalOperations.add(dentalOperation);
                }
            }
            return dentalOperations;
        }

        return null;
    }

    public void getOperations(Patient patient, HistoryItemAdapter itemAdapter, ListView listView) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mIsOperationsLoaded.setValue(false);
                itemAdapter.clear();
                getOperations(snapshot);
                if (!dentalOperations.isEmpty()) {
                    itemAdapter.setItems(dentalOperations);
                    itemAdapter.notifyDataSetChanged();
                }
//                mIsOperationsLoaded.setValue(true);
                UIUtil.setListViewHeightBasedOnItems(listView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        databaseReference.child(patient.getDentalHistoryUID())
                .child(DENTAL_HISTORY)
                .addValueEventListener(valueEventListener);
    }

    public String addOperationList(Patient patient, DentalOperation dentalOperation) {
        String key = databaseReference.push().getKey();
        if (patient.getDentalHistoryUID() == null && key != null) {
            databaseReference.child(key).setValue(dentalOperation);
        }


        return key;
    }

    public void addOperation(Patient patient, String dentalDesc, Date dentalDate, String modeOfPayment, String dentalAmount, boolean isFullyPaid) {
//        Log.d(TAG, "addOperation: getting dental history uid");
//        databaseReference.child(patient.getDentalHistoryUID());
//        String dentalKey = ;
//        Log.d(TAG, "addOperation: new dental history uid : " + dentalKey);
        databaseReference.orderByChild(OPERATIONS_REFERENCE).equalTo(patient.getDentalHistoryUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: checking if patient's dental key exists");
                DatabaseReference reference = databaseReference;

                if (!snapshot.exists()) {
                    Log.d(TAG, "onDataChange: dental operation not exist");
//                    databaseReference.child(patient.getDentalHistoryUID()).setValue(dentalOperation);
                    if (patient.getDentalHistoryUID() != null) {
                        reference = databaseReference.child(patient.getDentalHistoryUID());
                        reference.child(PATIENT_UID).setValue(patient.getUid());
                    }
                }
                else {
                    Log.d(TAG, "onDataChange: dental key doen't exist");
                }

                String operationUID = databaseReference.push().getKey();
                DentalOperation dentalOperation = new DentalOperation(operationUID, dentalDesc,
                        UIUtil.getDate(dentalDate), modeOfPayment, Double.parseDouble(dentalAmount), isFullyPaid);

                Log.d(TAG, "onDataChange: dental added: " + dentalOperation);
                reference.child(DENTAL_HISTORY).push().setValue(dentalOperation);
//                reference.child(dentalOperation.getUid()).setValue(dentalOperation);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isDuplicate(DentalOperation dentalOperation) {
        return dentalOperations.contains(dentalOperation);
    }
//
//    public LiveData<Boolean> getmIsOperationsLoaded() {
//        return mIsOperationsLoaded;
//    }
}
