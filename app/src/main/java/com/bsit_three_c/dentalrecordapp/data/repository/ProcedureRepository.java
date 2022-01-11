package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.model.ProgressNote;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcedureRepository extends BaseRepository {

    private static final String TAG = ProcedureRepository.class.getSimpleName();

    private static volatile ProcedureRepository instance;

    public ProcedureRepository() {
        super(OPERATIONS_REFERENCE);
    }

    public static ProcedureRepository getInstance() {
        if (instance == null) {
            instance = new ProcedureRepository();
        }
        return instance;
    }

    public Task<Void> upload(Procedure procedure) {
        return databaseReference.child(procedure.getUid()).setValue(procedure);
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

    public static void initialize(Procedure procedure) {

        if (procedure.getServiceIds() == null || procedure.getServiceIds().size() == 0) {
            procedure.setServiceIds(new ArrayList<>());
        }
    }

    private Procedure createProcedure(Patient patient,
                                      List<String> service,
                                      String dentalDesc,
                                      Date dentalDate,
                                      String dentalAmount,
                                      boolean isDownpayment,
                                      String dentalPayment,
                                      String dentalBalance) {

        Procedure procedure = new Procedure(
                databaseReference.push().getKey(),
                service,
                dentalDesc,
                DateUtil.getDate(dentalDate),
                UIUtil.convertToDouble(dentalAmount),
                isDownpayment,
                UIUtil.convertToDouble(dentalBalance)
        );

        ProgressNote progressNote = new ProgressNote(
                databaseReference.push().getKey(),
                DateUtil.getDate(dentalDate),
                dentalDesc,
                UIUtil.convertToDouble(dentalPayment)
        );

        procedure.addPaymentKey(progressNote.getUid());
        ProgressNoteRepository.getInstance().upload(progressNote);
        PatientRepository.getInstance().addProcedureKey(patient, procedure.getUid());

        return procedure;
    }

//    public void addPaymentKey(Procedure procedure) {
//        databaseReference.child(procedure.getUid()).child(PAYMENT_KEYS).setValue(procedure.getPaymentKeys());
//    }

    public void updatePaymentKeys(Procedure procedure, String paymentUID) {
        ArrayList<String> keys = (ArrayList<String>) procedure.getPaymentKeys();

        if (keys.size() == 1) {

            keys.clear();
            keys.add(NEW_PATIENT);

        } else {

            int index = keys.indexOf(paymentUID);

            if (index == -1) {
                Log.e(TAG, "removeProcedureKey: no key found");
                return;
            }

            keys.remove(index);
        }

        ProgressNoteRepository.getInstance().remove(paymentUID);
        databaseReference.child(procedure.getUid()).child(PAYMENT_KEYS).setValue(keys);
    }

//    public void updateBalance(Procedure procedure, ProgressNote progressNote) {
//        double newBalance = procedure.getDentalBalance() - progressNote.getAmount();
//        procedure.setDentalBalance(newBalance);
//        databaseReference.child(procedure.getUid()).child(BALANCE).setValue(procedure.getDentalBalance());
//    }

//    public void updateBalance(Procedure procedure, double balance) {
//        databaseReference.child(procedure.getUid()).child(BALANCE).setValue(balance);
//    }

//    public void updateProcedure(Procedure procedure) {
//        Log.d(TAG, "updateProcedure: updating procedure: " + procedure.getUid());
//        Log.d(TAG, "updateProcedure: balance: " + procedure.getDentalBalance());
//
//        //  Update procedure
//        databaseReference.child(procedure.getUid()).setValue(procedure);
//    }

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
                        progressNoteRepository.remove(keys.get(pos));
                    }

                    remove(procedure.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
