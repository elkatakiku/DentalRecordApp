package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ProcedureRepository {

    private static final String TAG = ProcedureRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;
    private static final String FIREBASE_URL = "https://dental-record-app-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String OPERATIONS_REFERENCE = "operations";
    private static final String PATIENT_UID = "patient_uid";
    private static final String DENTAL_HISTORY = "dental_records";
    private static final String PAYMENT_KEYS = "paymentKeys";
    private static final String BALANCE = "dentalBalance";

    private static volatile ProcedureRepository instance;
    private ArrayList<Procedure> procedures;

    public ProcedureRepository() {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(OPERATIONS_REFERENCE);
    }


    public static ProcedureRepository getInstance() {
        if (instance == null) instance = new ProcedureRepository();
        return instance;
    }

    private void getOperations(DataSnapshot dataSnapshot) {
        // Checks if dataSnapshot is null
        if (dataSnapshot != null) {

            // Create an arrayList to store the data in the dataSnapshot
            procedures = new ArrayList<>();

            // Loop through the snapshot and store the data in the created arrayList
            for (DataSnapshot data : dataSnapshot.getChildren()) {

                // Convert data to Procedure object
                Procedure procedure = data.getValue(Procedure.class);

                // Checks if dentalOpeation is null and is not a duplicate in the arrayList
                if (procedure != null && !isDuplicate(procedure)) {
//                    Log.d(TAG, "getOperations: dentalOp: " + procedure);
                    this.procedures.add(procedure);
                }
            }
        }
    }
    public void addProcedure(Patient patient,
                             int service,
                             String dentalDesc,
                             Date dentalDate,
//                             int modeOfPayment,
                             String dentalAmount,
                             boolean isDownpayment,
                             String dentalPayment,
                             String dentalBalance) {

        Procedure procedure = createProcedure(patient, service, dentalDesc, dentalDate,
//                modeOfPayment,
                dentalAmount, isDownpayment, dentalPayment, dentalBalance);
        databaseReference.child(procedure.getUid()).setValue(procedure);
    }

    public void removeProcedure(Patient patient, String operationUID, ArrayList<String> paymentKeys) {
        databaseReference.child(operationUID).removeValue();
        PatientRepository.getInstance().removeProcedureKey(patient, operationUID);
        PaymentRepository.getInstance().removePayments(paymentKeys);
    }

    public void removeProcedure(String procedureUID) {
        databaseReference.child(procedureUID).removeValue();
    }

    public void removeListener(String operationUID, ValueEventListener eventListener) {
        databaseReference.child(operationUID).removeEventListener(eventListener);
    }

    public DatabaseReference getProcedures(String operationUID) {
        return databaseReference.child(operationUID);
    }

    private boolean isDuplicate(Procedure procedure) {
        return this.procedures.contains(procedure);
    }

    private Procedure createProcedure(Patient patient,
                                      int service,
                                      String dentalDesc,
                                      Date dentalDate,
//                                      int modeOfPayment,
                                      String dentalAmount,
                                      boolean isDownpayment,
                                      String dentalPayment,
                                      String dentalBalance) {

        //  Create operation and payment UID
        String operationUID = databaseReference.push().getKey();
        String paymentUID = databaseReference.push().getKey();

        //  Create new Dental Operation
        Procedure procedure = new Procedure(
                operationUID,
                service,
                dentalDesc,
                UIUtil.getDate(dentalDate),
                Double.parseDouble(dentalAmount),
                isDownpayment,
                Double.parseDouble(dentalBalance)
        );

        Payment payment = new Payment(
                paymentUID,
                Double.parseDouble(dentalPayment),
//                modeOfPayment,
                UIUtil.getDate(dentalDate)
        );

        procedure.addPaymentKey(payment.getUid());
        PaymentRepository.getInstance().addPayment(procedure, payment);
        PatientRepository.getInstance().addProcedureKey(patient, procedure.getUid());

        return procedure;
    }

    public void addPaymentKey(Procedure procedure) {
        databaseReference.child(procedure.getUid()).child(PAYMENT_KEYS).setValue(procedure.getPaymentKeys());
    }

    public void updatePaymentKeys(Procedure operation, String paymentUID) {
        ArrayList<String> keys = operation.getPaymentKeys();

        if (keys.size() == 1) {

            keys.clear();
            keys.add(PatientRepository.NEW_PATIENT);

        } else {

            int index = keys.indexOf(paymentUID);

            if (index == -1) {
                Log.e(TAG, "removeProcedureKey: no key found");
                return;
            }

            keys.remove(index);
        }

        databaseReference.child(operation.getUid()).child(PAYMENT_KEYS).setValue(keys);
    }

    public void updateBalance(Procedure procedure, Payment payment) {
        double newBalance = procedure.getDentalBalance() - payment.getAmount();
        procedure.setDentalBalance(newBalance);
        databaseReference.child(procedure.getUid()).child(BALANCE).setValue(procedure.getDentalBalance());
    }

    public void updateBalance(Procedure procedure, double balance) {
        databaseReference.child(procedure.getUid()).child(BALANCE).setValue(balance);
    }

    public void updateProcedure(Procedure procedure) {
        Log.d(TAG, "updateProcedure: updating procedure: " + procedure.getUid());
        Log.d(TAG, "updateProcedure: balance: " + procedure.getDentalBalance());
        databaseReference.child(procedure.getUid()).setValue(procedure);
    }

    public void removePaymentKeys(String procedureUID) {

        PaymentRepository paymentRepository = PaymentRepository.getInstance();

        //  Remove payments of procedure
        databaseReference.child(procedureUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: snapshot count: " + (snapshot.getChildrenCount()));
                Procedure procedure = snapshot.getValue(Procedure.class);

                if (procedure != null) {
                    ArrayList<String> keys = procedure.getPaymentKeys();
                    int keySize = keys.size();

                    for (int pos = 0; pos <keySize; pos++) {
                        paymentRepository.removePayment(keys.get(pos));
                    }

                    removeProcedure(procedure.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
