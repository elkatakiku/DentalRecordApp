package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;

import com.bsit_three_c.dentalrecordapp.data.model.DentalProcedure;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;

import java.util.ArrayList;

class MiddleGround {
    private static final String TAG = "MiddleGround";

    private static final ProcedureRepository procedureRepository = ProcedureRepository.getInstance();
    private static final PaymentRepository paymentRepository = PaymentRepository.getInstance();
    private static final PatientRepository patientRepository = PatientRepository.getInstance();

    public static void addPaymentKey(DentalProcedure procedure, Payment payment) {
        procedure.addPaymentKey(payment.getUid());
        procedureRepository.addPaymentKey(procedure);
        procedureRepository.updateBalance(procedure, payment);
    }

    public static void addNewProdecurePayment(Patient patient, DentalProcedure procedure, Payment payment) {
        Log.d(TAG, "addPayment: addPayment");

        procedure.addPaymentKey(payment.getUid());
        paymentRepository.addPayment(procedure, payment);

        Log.d(TAG, "addPayment: calling addProcedureKey");

        patientRepository.addProcedureKey(patient, procedure.getUid());
    }

    public static void removeProcedureKey(Patient patient, String procedureKey, ArrayList<String> paymentKeys) {
        patientRepository.removeProcedureKey(patient, procedureKey);

        for (int position = 0; position < paymentKeys.size(); position++) {
            paymentRepository.removePayment(paymentKeys.get(position));
        }
    }

    public static void updateProcedurePaymentKeys(DentalProcedure procedure, String paymentUID) {
        procedureRepository.updatePaymentKeys(procedure, paymentUID);
    }
}
