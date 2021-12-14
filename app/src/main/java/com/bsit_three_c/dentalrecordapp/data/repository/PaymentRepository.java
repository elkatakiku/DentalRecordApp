package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import com.bsit_three_c.dentalrecordapp.data.model.Payment;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class PaymentRepository {
    private static final String TAG = PaymentRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;

    private static volatile PaymentRepository instance;

    public PaymentRepository() {
        this.database = FirebaseDatabase.getInstance(FirebaseHelper.FIREBASE_URL);
        this.databaseReference = database.getReference(FirebaseHelper.PAYMENTS_REFERENCE);
    }

    public static PaymentRepository getInstance() {
        if (instance == null) instance = new PaymentRepository();
        return instance;
    }

    public DatabaseReference getPayments(String paymentUID) {
        return databaseReference.child(paymentUID);
    }

    public void addPayment(Procedure procedure, Payment payment) {
        databaseReference.child(payment.getUid()).setValue(payment);
        //  Update operation's balance
    }

    public void addPayment(Procedure procedure, String paidAmount, String date) {
        double convertedPaidAmount = Double.parseDouble(paidAmount);
        String paymentUID = databaseReference.push().getKey();

        Log.d(TAG, "addPayment: paymentUID: " + (paymentUID != null));

        if (paymentUID != null) {

            Payment payment = new Payment(paymentUID, convertedPaidAmount, date);
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

//    public void removePayment(Procedure procedure, String paymentUID) {
//        removePayment(paymentUID);
//        ProcedureRepository.getInstance().updatePaymentKeys(procedure, paymentUID);
//    }

    public void removePayment(String paymentUID) {
        databaseReference.child(paymentUID).removeValue();
    }

    public void removePayments(ArrayList<String> paymentKeys) {
        for (int position = 0; position < paymentKeys.size(); position++) {
            Log.d(TAG, "removePaymets: removing payment");
            removePayment(paymentKeys.get(position));
        }
    }
}
