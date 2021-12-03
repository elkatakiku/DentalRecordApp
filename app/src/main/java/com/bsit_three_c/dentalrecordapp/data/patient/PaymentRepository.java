package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bsit_three_c.dentalrecordapp.data.adapter.PaymentList;
import com.bsit_three_c.dentalrecordapp.data.model.DentalOperation;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PaymentRepository {
    private static final String TAG = PaymentRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;
    private static final String FIREBASE_URL = "https://dental-record-app-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String PAYMENTS_REFERENCE = "payments";
    private static final String PATIENT_UID = "patient_uid";
    private static final String PAYMENT_RECORDS = "payment_records";
    private static final String TOTAL = "total";
    private static final String BALANCE = "balance";

    private ValueEventListener valueEventListener;

    private static volatile PaymentRepository instance;
    private ArrayList<Payment> dentalPayments;

    public PaymentRepository() {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(PAYMENTS_REFERENCE);
    }

    public static PaymentRepository getInstance() {
        if (instance == null) instance = new PaymentRepository();
        return instance;
    }

    public void getPayments(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            dentalPayments = new ArrayList<>();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
//                DentalOperation dentalOperation = data.getValue(DentalOperation.class);
                Payment payment = data.getValue(Payment.class);
                if (payment != null && !isDuplicate(payment)) {
                    Log.d(TAG, "getPayments: dentalPayment: " + payment);
                    this.dentalPayments.add(payment);
                }
            }
        }
    }

    public void getPayments(Patient patient, PaymentList paymentList) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Load Operatiohs from the database
                getPayments(snapshot);

                // Checks local field dentalOperations if empty
                if (!dentalPayments.isEmpty()) {
                    paymentList.addItems(dentalPayments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Catch error
            }
        };

        // IDK
        databaseReference.child(patient.getDentalHistoryUID())
                .child(PAYMENT_RECORDS)
                .addValueEventListener(valueEventListener);
    }

    public void addPayment(DentalOperation dentalOperation, String modeOfPayment, String dentalAmount, String dentalBalance) {

        // Checks if there is an existing child with a key of dentalOpearation's uid
        databaseReference.orderByChild(PAYMENTS_REFERENCE).equalTo(dentalOperation.getUid())
                .addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: checking if patient's dental key exists");
                DatabaseReference reference = databaseReference;

                if (!snapshot.exists()) {
                    Log.d(TAG, "onDataChange: dental operation not exist");
//                    databaseReference.child(patient.getDentalHistoryUID()).setValue(dentalOperation);
                    if (dentalOperation.getUid() != null) {
                        reference = databaseReference.child(dentalOperation.getUid());
//                        reference.child(PATIENT_UID).setValue(patient.getUid());
                        reference.child(TOTAL).setValue(dentalOperation.getDentalTotalAmount());
                        reference.child(BALANCE).setValue(dentalOperation.getDentalBalance());
                    }
                }
                else Log.d(TAG, "onDataChange: dental key doen't exist");

//                String operationUID = databaseReference.push().getKey();
                String paymentUID = databaseReference.push().getKey();
                Payment dentalPayment = new Payment(paymentUID, dentalOperation.getUid(), Double.parseDouble(dentalAmount), modeOfPayment, UIUtil.getCurrentDate());

//                DentalOperation dentalOperation = new DentalOperation(operationUID, dentalDesc,
//                        UIUtil.getDate(dentalDate), modeOfPayment, Double.parseDouble(dentalAmount), isFullyPaid,
//                        Double.parseDouble(dentalTotalAmount), Double.parseDouble(dentalBalance), paymentUID);

                Log.d(TAG, "onDataChange: payment added: " + dentalPayment);
                reference.child(PAYMENT_RECORDS).child(paymentUID).setValue(dentalPayment);
//                reference.child(dentalOperation.getUid()).setValue(dentalOperation);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isDuplicate(Payment payment) {
        return dentalPayments.contains(payment);
    }
}
