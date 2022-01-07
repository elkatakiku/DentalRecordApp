package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.model.ProgressNote;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProgressNoteRepository {
    private static final String TAG = ProgressNoteRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;

    private static volatile ProgressNoteRepository instance;

    public ProgressNoteRepository() {
        this.database = FirebaseDatabase.getInstance(FirebaseHelper.FIREBASE_URL);
        this.databaseReference = database.getReference(FirebaseHelper.PAYMENTS_REFERENCE);
    }

    public static ProgressNoteRepository getInstance() {
        if (instance == null) instance = new ProgressNoteRepository();
        return instance;
    }

    public DatabaseReference getProgressNote(String paymentUID) {
        return databaseReference.child(paymentUID);
    }

    public void addProgressNote(Procedure procedure, ProgressNote progressNote) {
        databaseReference.child(progressNote.getUid()).setValue(progressNote);
        //  Update operation's balance
    }

    public void addProgressNote(Procedure procedure, String paidAmount, String date, String description) {
        double convertedPaidAmount = Double.parseDouble(paidAmount);
        String progressNoteId = databaseReference.push().getKey();

        Log.d(TAG, "addPayment: paymentUID: " + (progressNoteId != null));

        if (progressNoteId != null) {

            ProgressNote progressNote = new ProgressNote(
                    progressNoteId,
                    date,
                    description,
                    convertedPaidAmount
            );
            databaseReference.child(progressNoteId).setValue(progressNote);

            procedure.addPaymentKey(progressNote.getUid());
            ProcedureRepository.getInstance().addPaymentKey(procedure);
            ProcedureRepository.getInstance().updateBalance(procedure, progressNote);
        }
    }

    public void updateProgressNote(ProgressNote progressNote, Procedure procedure) {
        ProcedureRepository procedureRepository = ProcedureRepository.getInstance();

        //  Update progressNote
        databaseReference.child(progressNote.getUid()).setValue(progressNote);

        //  Update procedure
        procedureRepository.updateProcedure(procedure);
    }

//    public void removePayment(Procedure procedure, String paymentUID) {
//        removePayment(paymentUID);
//        ProcedureRepository.getInstance().updatePaymentKeys(procedure, paymentUID);
//    }

    public void removeProgressNote(String paymentUID) {
        databaseReference.child(paymentUID).removeValue();
    }

    public void removeProgressNote(List<String> paymentKeys) {
        for (int position = 0; position < paymentKeys.size(); position++) {
            Log.d(TAG, "removePaymets: removing payment");
            removeProgressNote(paymentKeys.get(position));
        }
    }
}
