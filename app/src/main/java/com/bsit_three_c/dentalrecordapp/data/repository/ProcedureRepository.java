package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.model.ProgressNote;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcedureRepository {

    private static final String TAG = ProcedureRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;

    private static volatile ProcedureRepository instance;
    private ArrayList<Procedure> procedures;

    public ProcedureRepository() {
        this.database = FirebaseDatabase.getInstance(FirebaseHelper.FIREBASE_URL);
        this.databaseReference = database.getReference(FirebaseHelper.OPERATIONS_REFERENCE);
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
                             List<String> service,
                             String dentalDesc,
                             Date dentalDate,
                             String dentalAmount,
                             boolean isDownpayment,
                             String dentalPayment,
                             String dentalBalance) {

        Procedure procedure = createProcedure(patient, service, dentalDesc, dentalDate,
                dentalAmount, isDownpayment, dentalPayment, dentalBalance);
        databaseReference.child(procedure.getUid()).setValue(procedure);
    }

    public void removeProcedure(Patient patient, String operationUID, List<String> paymentKeys) {
        databaseReference.child(operationUID).removeValue();
        PatientRepository.getInstance().removeProcedureKey(patient, operationUID);
        ProgressNoteRepository.getInstance().removeProgressNote(paymentKeys);
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

    public void initializeProcedure(Procedure procedure) {

        if (procedure.getServiceIds() == null || procedure.getServiceIds().size() == 0) {
            procedure.setServiceIds(new ArrayList<>());
        }
    }

    private boolean isDuplicate(Procedure procedure) {
        return this.procedures.contains(procedure);
    }

    private Procedure createProcedure(Patient patient,
                                      List<String> service,
                                      String dentalDesc,
                                      Date dentalDate,
                                      String dentalAmount,
                                      boolean isDownpayment,
                                      String dentalPayment,
                                      String dentalBalance) {

        //  Create operation and progressNote UID
        String operationUID = databaseReference.push().getKey();
        String paymentUID = databaseReference.push().getKey();

        //  Create new Dental Operation
        Procedure procedure = new Procedure(
                operationUID,
                service,
                dentalDesc,
                DateUtil.getDate(dentalDate),
                UIUtil.convertToDouble(dentalAmount),
                isDownpayment,
                UIUtil.convertToDouble(dentalBalance)
        );

        ProgressNote progressNote = new ProgressNote(
                paymentUID,
                DateUtil.getDate(dentalDate),
                dentalDesc,
                UIUtil.convertToDouble(dentalPayment)
        );

        procedure.addPaymentKey(progressNote.getUid());
        ProgressNoteRepository.getInstance().addProgressNote(procedure, progressNote);
        PatientRepository.getInstance().addProcedureKey(patient, procedure.getUid());

        return procedure;
    }

    public void addPaymentKey(Procedure procedure) {
        databaseReference.child(procedure.getUid()).child(FirebaseHelper.PAYMENT_KEYS).setValue(procedure.getPaymentKeys());
    }

    public void updatePaymentKeys(Procedure procedure, String paymentUID) {
        ArrayList<String> keys = (ArrayList<String>) procedure.getPaymentKeys();

        if (keys.size() == 1) {

            keys.clear();
            keys.add(FirebaseHelper.NEW_PATIENT);

        } else {

            int index = keys.indexOf(paymentUID);

            if (index == -1) {
                Log.e(TAG, "removeProcedureKey: no key found");
                return;
            }

            keys.remove(index);
        }

        ProgressNoteRepository.getInstance().removeProgressNote(paymentUID);
        databaseReference.child(procedure.getUid()).child(FirebaseHelper.PAYMENT_KEYS).setValue(keys);
    }

    public void updateBalance(Procedure procedure, ProgressNote progressNote) {
        double newBalance = procedure.getDentalBalance() - progressNote.getAmount();
        procedure.setDentalBalance(newBalance);
        databaseReference.child(procedure.getUid()).child(FirebaseHelper.BALANCE).setValue(procedure.getDentalBalance());
    }

    public void updateBalance(Procedure procedure, double balance) {
        databaseReference.child(procedure.getUid()).child(FirebaseHelper.BALANCE).setValue(balance);
    }

    public void updateProcedure(Procedure procedure) {
        Log.d(TAG, "updateProcedure: updating procedure: " + procedure.getUid());
        Log.d(TAG, "updateProcedure: balance: " + procedure.getDentalBalance());

        //  Update procedure
        databaseReference.child(procedure.getUid()).setValue(procedure);
    }

    public void removePaymentKeys(String procedureUID) {

        ProgressNoteRepository progressNoteRepository = ProgressNoteRepository.getInstance();

        //  Remove payments of procedure
        databaseReference.child(procedureUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Procedure procedure = snapshot.getValue(Procedure.class);

                if (procedure != null) {
                    ArrayList<String> keys = (ArrayList<String>) procedure.getPaymentKeys();
                    int keySize = keys.size();

                    for (int pos = 0; pos <keySize; pos++) {
                        progressNoteRepository.removeProgressNote(keys.get(pos));
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
