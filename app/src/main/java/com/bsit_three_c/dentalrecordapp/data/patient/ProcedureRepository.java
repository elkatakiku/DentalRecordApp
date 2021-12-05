package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;

import com.bsit_three_c.dentalrecordapp.data.model.DentalProcedure;
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

    private ValueEventListener valueEventListener;

    private static volatile ProcedureRepository instance;
    private ArrayList<DentalProcedure> dentalProcedures;

//    private final PaymentRepository paymentRepository;
//    private final PatientRepository patientRepository;

    private double balance = 0;

    public ProcedureRepository() {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(OPERATIONS_REFERENCE);
//        this.paymentRepository = PaymentRepository.getInstance();
//        this.patientRepository = PatientRepository.getInstance();
    }


    public static ProcedureRepository getInstance() {
        if (instance == null) instance = new ProcedureRepository();
        return instance;
    }

    private void getOperations(DataSnapshot dataSnapshot) {
        // Checks if dataSnapshot is null
        if (dataSnapshot != null) {

            // Create an arrayList to store the data in the dataSnapshot
            dentalProcedures = new ArrayList<>();

            // Loop through the snapshot and store the data in the created arrayList
            for (DataSnapshot data : dataSnapshot.getChildren()) {

                // Convert data to DentalProcedure object
                DentalProcedure dentalProcedure = data.getValue(DentalProcedure.class);

                // Checks if dentalOpeation is null and is not a duplicate in the arrayList
                if (dentalProcedure != null && !isDuplicate(dentalProcedure)) {
//                    Log.d(TAG, "getOperations: dentalOp: " + dentalProcedure);
                    this.dentalProcedures.add(dentalProcedure);
                }
            }
        }
    }

//    public void getOperations(Patient patient, HistoryItemAdapter itemAdapter, ListView listView, TextView tvBalance) {
//        valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                mIsOperationsLoaded.setValue(false);
//
//                // Initialize dentalProcedures ArrayList
//                getOperations(snapshot);
//                if (!dentalProcedures.isEmpty()) {
//                    itemAdapter.setItems(dentalProcedures);
//                    itemAdapter.notifyDataSetChanged();
//                }
////                mIsOperationsLoaded.setValue(true);
//                UIUtil.setListViewHeightBasedOnItems(listView);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//
//        databaseReference.child(patient.getDentalHistoryUID())
//                .child(DENTAL_HISTORY)
//                .addValueEventListener(valueEventListener);
//    }

//    public void getOperations(Patient patient, OperationsList operationsList, TextView tvBalance) {
//        valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                // Load Operatiohs from the database
//                getOperations(snapshot);
//
//                // Checks local field dentalProcedures if empty
//                if (!dentalProcedures.isEmpty()) {
//
//                    //  Add item to linearLayout
//                    operationsList.addItems(dentalProcedures);
//
//                    // Compute balance
//                    for (DentalProcedure operation : dentalProcedures) {
//                        Log.d(TAG, "onDataChange: current operation: " + operation);
//                        Log.d(TAG, "onDataChange: current balance: " + balance);
//                        balance += operation.getDentalBalance();
//
//                        Log.d(TAG, "onDataChange: updated balance: " + balance);
//                    }
//
//                }
//
//                // Setting balance to text
//                String txtBalance = String.valueOf(balance);
//                tvBalance.setText(txtBalance);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//
//        databaseReference.child(patient.getDentalHistoryUID())
//                .child(DENTAL_HISTORY)
//                .addValueEventListener(valueEventListener);
//    }

//
//    public String addOperationList(Patient patient, DentalProcedure dentalOperation) {
//        String key = databaseReference.push().getKey();
//        if (patient.getDentalHistoryUID() == null && key != null) {
//            databaseReference.child(key).setValue(dentalOperation);
//        }
//
//
//        return key;
//    }
//
//    public void addOperation(Patient patient,
//                             String dentalDesc,
//                             Date dentalDate,
//                             String modeOfPayment,
//                             String dentalAmount,
//                             boolean isDownpayment,
//                             String dentalTotalAmount,
//                             String dentalBalance) {
//
//        // Checks if operation uid exists
//        databaseReference.orderByChild(OPERATIONS_REFERENCE).equalTo(patient.getDentalHistoryUID()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d(TAG, "onDataChange: checking if patient's dental key exists");
//                DatabaseReference reference = databaseReference;
//
//                if (!snapshot.exists()) {
//                    Log.d(TAG, "onDataChange: dental operation not exist");
////                    databaseReference.child(patient.getDentalHistoryUID()).setValue(dentalOperation);
//                    if (patient.getDentalHistoryUID() != null) {
//                        reference = databaseReference.child(patient.getDentalHistoryUID());
//                        reference.child(PATIENT_UID).setValue(patient.getUid());
//                    }
//                }
//                else {
//                    Log.d(TAG, "onDataChange: dental key exist");
//                }
//
//                //  Creates dentalOperation
//                DentalProcedure dentalOperation = createOperation(dentalDesc, dentalDate, modeOfPayment, dentalAmount, isDownpayment, dentalTotalAmount, dentalBalance);
//
//                Log.d(TAG, "onDataChange: dental added: " + dentalOperation);
//                reference.child(DENTAL_HISTORY).push().setValue(dentalOperation);
////                reference.child(dentalOperation.getUid()).setValue(dentalOperation);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    public void addProcedure(Patient patient,
                             String dentalDesc,
                             Date dentalDate,
                             String modeOfPayment,
                             String dentalPaidAmount,
                             boolean isDownpayment,
                             String dentalTotalAmount,
                             String dentalBalance) {

        DentalProcedure dentalProcedure = createOperation(patient, dentalDesc, dentalDate, modeOfPayment, dentalPaidAmount, isDownpayment, dentalTotalAmount, dentalBalance);
        databaseReference.child(dentalProcedure.getUid()).setValue(dentalProcedure);
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

    private boolean isDuplicate(DentalProcedure dentalProcedure) {
        return this.dentalProcedures.contains(dentalProcedure);
    }

    public DatabaseReference getBalance(DentalProcedure procedure) {
        Log.d(TAG, "getBalance: returning balance");
        return databaseReference.child(procedure.getUid()).child(BALANCE);
    }

    private DentalProcedure createOperation(Patient patient,
                                            String dentalDesc,
                                            Date dentalDate,
                                            String modeOfPayment,
                                            String dentalAmount,
                                            boolean isDownpayment,
                                            String dentalTotalAmount,
                                            String dentalBalance) {

        //  Create operation and payment UID
        String operationUID = databaseReference.push().getKey();
        String paymentUID = databaseReference.push().getKey();

        //  Create new Dental Operation
        Log.d(TAG, "onDataChange: dentalTotalAmount value: " + dentalTotalAmount);
        DentalProcedure dentalProcedure = new DentalProcedure(
                operationUID,
                dentalDesc,
                UIUtil.getDate(dentalDate),
                Double.parseDouble(dentalTotalAmount),
                isDownpayment,
                Double.parseDouble(dentalBalance)
        );

        Payment payment = new Payment(
                paymentUID,
                Double.parseDouble(dentalAmount),
                modeOfPayment,
                UIUtil.getDate(dentalDate)
        );

        MiddleGround.addPayment(patient, dentalProcedure, payment);

        return dentalProcedure;
    }

    public void addPaymentKey(DentalProcedure procedure) {
        databaseReference.child(procedure.getUid()).child(PAYMENT_KEYS).setValue(procedure.getPaymentKeys());
    }

    public void updatePaymentKeys(DentalProcedure operation, String paymentUID) {
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

    public void updateBalance(DentalProcedure procedure, Payment payment) {
        Log.d(TAG, "updateBalance: total amount: " + procedure.getDentalBalance());
        Log.d(TAG, "updateBalance: amount: " + payment.getAmount());
        double newBalance = procedure.getDentalBalance() - payment.getAmount();
        Log.d(TAG, "updateBalance: new balance: " + newBalance);
        procedure.setDentalBalance(newBalance);
        databaseReference.child(procedure.getUid()).child(BALANCE).setValue(procedure.getDentalBalance());
    }
}
