package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.model.ProgressNote;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class ProgressNoteRepository extends BaseRepository {
    private static final String TAG = ProgressNoteRepository.class.getSimpleName();

    private static volatile ProgressNoteRepository instance;

    public ProgressNoteRepository() {
        super(PAYMENTS_REFERENCE);
    }

    public static ProgressNoteRepository getInstance() {
        if (instance == null) {
            instance = new ProgressNoteRepository();
        }
        return instance;
    }

//    public DatabaseReference getProgressNote(String paymentUID) {
//        return databaseReference.child(paymentUID);
//    }

    public Task<Void> upload(ProgressNote progressNote) {
        return databaseReference.child(progressNote.getUid()).setValue(progressNote);
    }

    public void upload(Procedure procedure, String paidAmount, String date, String description) {
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
            procedure.setDentalBalance(procedure.getDentalBalance() - progressNote.getAmount());

            ProcedureRepository.getInstance().upload(procedure);

//            ProcedureRepository.getInstance().addPaymentKey(procedure);
//            ProcedureRepository.getInstance().updateBalance(procedure, progressNote);
        }
    }

    public void updateProgressNote(ProgressNote progressNote, Procedure procedure) {
        //  Update progressNote
        databaseReference.child(progressNote.getUid()).setValue(progressNote);

        //  Update procedure
        ProcedureRepository.getInstance().upload(procedure);
//        procedureRepository.updateProcedure(procedure);
    }

//    public void removePayment(Procedure procedure, String paymentUID) {
//        removePayment(paymentUID);
//        ProcedureRepository.getInstance().updatePaymentKeys(procedure, paymentUID);
//    }

    public void removeProgressNote(List<String> paymentKeys) {
        for (int position = 0; position < paymentKeys.size(); position++) {
            Log.d(TAG, "removePaymets: removing payment");
            remove(paymentKeys.get(position));
        }
    }
}
