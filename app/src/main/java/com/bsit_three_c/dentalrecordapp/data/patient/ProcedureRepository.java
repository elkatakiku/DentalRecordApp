package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;

import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.firebase.database.DataSnapshot;
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

    private double balance = 0;

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
                             int modeOfPayment,
                             String dentalAmount,
                             boolean isDownpayment,
                             String dentalPayment,
                             String dentalBalance) {

        Procedure procedure = createProcedure(patient, service, dentalDesc, dentalDate,
                modeOfPayment, dentalAmount, isDownpayment, dentalPayment, dentalBalance);
        databaseReference.child(procedure.getUid()).setValue(procedure);
    }

    public void removeProcedure(Patient patient, String operationUID, ArrayList<String> paymentKeys) {
        //  Remove in operations and patients operation keys
        databaseReference.child(operationUID).removeValue();
        //  Remove from patients keys
        MiddleGround.removeProcedureKey(patient, operationUID, paymentKeys);
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

    public DatabaseReference getBalance(Procedure procedure) {
        Log.d(TAG, "getBalance: returning balance");
        return databaseReference.child(procedure.getUid()).child(BALANCE);
    }

    private Procedure createProcedure(Patient patient,
                                      int service,
                                      String dentalDesc,
                                      Date dentalDate,
                                      int modeOfPayment,
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
                modeOfPayment,
                UIUtil.getDate(dentalDate)
        );

        MiddleGround.addNewProdecurePayment(patient, procedure, payment);

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
        Log.d(TAG, "updateBalance: total amount: " + procedure.getDentalBalance());
        Log.d(TAG, "updateBalance: amount: " + payment.getAmount());
        double newBalance = procedure.getDentalBalance() - payment.getAmount();
        Log.d(TAG, "updateBalance: new balance: " + newBalance);
        procedure.setDentalBalance(newBalance);
        databaseReference.child(procedure.getUid()).child(BALANCE).setValue(procedure.getDentalBalance());
    }

    public void updateProcedure(Procedure procedure) {
        databaseReference.child(procedure.getUid()).setValue(procedure);
    }

    private Procedure createProcedure(String uid,
                                      int service,
                                      String dentalDesc,
                                      String dentalDate,
                                      double dentalTotalAmount,
                                      boolean isDownpayment,
                                      double dentalBalance,
                                      ArrayList<String> paymentKeys) {
        return new Procedure(
                uid,
                service,
                dentalDesc,
                dentalDate,
                dentalTotalAmount,
                isDownpayment,
                dentalBalance,
                paymentKeys);
    }
}
