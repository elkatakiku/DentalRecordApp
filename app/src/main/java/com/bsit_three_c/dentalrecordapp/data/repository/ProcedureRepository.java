package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

    public void removeProcedure(Patient patient, String procedureUid, List<String> paymentKeys) {
        Log.d(TAG, "removeProcedure: deleting procedure");
        remove(procedureUid)
                .continueWith(task -> {
                    Log.d(TAG, "removeProcedure: done remove procedure attempt");
                    if (task.isSuccessful()) {
                        Log.d(TAG, "removeProcedure: success in deleting procedure");
                        Task<Void> removeProcedureKey = PatientRepository
                                .getInstance()
                                .removeProcedureKey(patient, procedureUid);
                        Log.d(TAG, "removeProcedure: removing procedure key");
                        if (removeProcedureKey != null) {
                            removeProcedureKey.continueWith(task1 -> {
                                ProgressNoteRepository.getInstance().removeProgressNote(paymentKeys);
                                return null;
                            });
                        }
                    }
                    return null;
                });

    }

    public static void initialize(Procedure procedure) {

        if (procedure.getServiceIds() == null || procedure.getServiceIds().size() == 0) {
            procedure.setServiceIds(new ArrayList<>());
        }

        if (procedure.getPaymentKeys() == null) {
            procedure.setPaymentKeys(new ArrayList<>());
        }
    }

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

    public static class ProcedureListener implements ValueEventListener {

        private final MutableLiveData<Procedure> mProcedure;

        public ProcedureListener(MutableLiveData<Procedure> mProcedure) {
            this.mProcedure = mProcedure;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Procedure procedure = snapshot.getValue(Procedure.class);

            if (procedure != null) {
                initialize(procedure);
            }

            mProcedure.setValue(procedure);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    public static class ProceduresListener implements ValueEventListener {

        private final MutableLiveData<List<Procedure>> mProcedures;

        public ProceduresListener(MutableLiveData<List<Procedure>> mProcedures) {
            this.mProcedures = mProcedures;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            final ArrayList<Procedure> procedures = new ArrayList<>();
            for (DataSnapshot data : snapshot.getChildren()) {
                Procedure procedure = data.getValue(Procedure.class);

                if (procedure == null) {
                    continue;
                }

                initialize(procedure);
                procedures.add(procedure);
            }

            mProcedures.setValue(procedures);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }
}
