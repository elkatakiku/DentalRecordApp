package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bsit_three_c.dentalrecordapp.data.adapter.PaymentList;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;
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

    private static volatile PaymentRepository instance;
    private ArrayList<Payment> dentalPayments;

//    private final ProcedureRepository procedureRepository;

    private ValueEventListener valueEventListener;

    public PaymentRepository() {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(PAYMENTS_REFERENCE);
//        this.procedureRepository = ProcedureRepository.getInstance();
    }

    public static PaymentRepository getInstance() {
        if (instance == null) instance = new PaymentRepository();
        return instance;
    }

    public void getPayments(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            dentalPayments = new ArrayList<>();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
//                Procedure dentalOperation = data.getValue(Procedure.class);
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

//        // IDK
//        databaseReference.child(patient.getDentalHistoryUID())
//                .child(PAYMENT_RECORDS)
//                .addValueEventListener(valueEventListener);
    }

    public DatabaseReference getPayments(String paymentUID) {
        return databaseReference.child(paymentUID);
    }

    public void addPayment(Procedure procedure, Payment payment) {
        databaseReference.child(payment.getUid()).setValue(payment);
        //  Update operation's balance
    }

    public void addPayment(Procedure procedure,
//                           int modeOfPayment,
                           String paidAmount, String date) {
        double convertedPaidAmount = Double.parseDouble(paidAmount);
        String paymentUID = databaseReference.push().getKey();

        Log.d(TAG, "addPayment: paymentUID: " + (paymentUID != null));

        if (paymentUID != null) {

            Payment payment = new Payment(paymentUID, convertedPaidAmount,
//                    modeOfPayment,
                    date);
            databaseReference.child(paymentUID).setValue(payment);

            procedure.addPaymentKey(payment.getUid());
            ProcedureRepository.getInstance().addPaymentKey(procedure);
            ProcedureRepository.getInstance().updateBalance(procedure, payment);
        }
    }

    public void updatePayment(Payment payment, Procedure procedure) {
        ProcedureRepository procedureRepository = ProcedureRepository.getInstance();

        //  Update payment
        databaseReference.child(payment.getUid()).setValue(payment);

        //  Update procedure
        procedureRepository.updateProcedure(procedure);
    }

    public void removePayment(Procedure procedure, String paymentUID) {
        removePayment(paymentUID);
        ProcedureRepository.getInstance().updatePaymentKeys(procedure, paymentUID);
    }

    public void removePayment(String paymentUID) {
        databaseReference.child(paymentUID).removeValue();
    }

    public void removePayments(ArrayList<String> paymentKeys) {
        for (int position = 0; position < paymentKeys.size(); position++) {
            Log.d(TAG, "removePaymets: removing payment");
            removePayment(paymentKeys.get(position));
        }
    }

    //  For testing only
    public void removePayment() {
        databaseReference.removeValue();
    }

    private boolean isDuplicate(Payment payment) {
        return dentalPayments.contains(payment);
    }
}
