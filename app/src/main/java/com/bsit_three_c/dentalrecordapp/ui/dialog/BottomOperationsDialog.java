package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.PaymentList;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;
import com.bsit_three_c.dentalrecordapp.data.patient.PaymentRepository;
import com.bsit_three_c.dentalrecordapp.data.patient.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.ui.patient_info.PatientInfoFragment;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BottomOperationsDialog {
    private static final String TAG = BottomOperationsDialog.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    private final Context context;
    private BottomSheetDialog procedureDialog;

    private final PaymentRepository paymentRepository;
    private final ProcedureRepository procedureRepository;
    private Procedure operation;

    private final PatientInfoFragment lifecycleOwner;
    private final LinearLayout operationLayout;
    private final View view;

    private final MutableLiveData<Integer> paymentsCounter = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDoneLoading = new MutableLiveData<>();
    private final MutableLiveData<Double> mBalance = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOnlyOnePayment = new MutableLiveData<>();

    private Procedure procedure;
    private int totalPayments;
    private double totalPaid;
    private double totalAmount;

    private static final String FULLY_PAID = "Fully Paid";

    private Patient patient;

    public BottomOperationsDialog(LayoutInflater layoutInflater, Context context,
                                  PatientInfoFragment lifecycleOwner) {
        this.layoutInflater = layoutInflater;
        this.context = context;
        this.paymentRepository = PaymentRepository.getInstance();
        this.procedureRepository = ProcedureRepository.getInstance();
        this.view = layoutInflater.inflate(R.layout.bottom_operation_details, null, false);
        this.operationLayout = view.findViewById(R.id.linearLayoutOperation);
        this.lifecycleOwner = lifecycleOwner;
    }

    // Sets local field operator and readied the dialog
    public void createOperationDialog(Procedure procedure){
        this.procedure = procedure;
        isDoneLoading.setValue(false);
        this.operation = procedure;

        ViewHolder viewHolder = new ViewHolder(operationLayout);
        procedureDialog = new BottomSheetDialog(context);
        dialogDismissListener(procedureDialog);

        viewHolder.btnAddPayment.setOnClickListener(v -> {
            BottomPaymentDialog paymentDialog = new BottomPaymentDialog(layoutInflater, context, lifecycleOwner);
            paymentDialog.createDialog(procedure);
            paymentDialog.setPatient(patient);

            procedureDialog.dismiss();
            paymentDialog.showDialog();
        });

        viewHolder.iconClose.setOnClickListener(v -> procedureDialog.dismiss());

        String totalAmount = String.valueOf(procedure.getDentalTotalAmount());

        viewHolder.title.setText(UIUtil.getService(lifecycleOwner.getResources(), procedure.getService()));
        viewHolder.operationDesc.setText(procedure.getDentalDesc());
        viewHolder.operationDate.setText(procedure.getDentalDate());
        viewHolder.operationTotalAmount.setText(totalAmount);

        loadPayments(viewHolder.paymentsLayout);

        mBalance.observe(lifecycleOwner.getViewLifecycleOwner(), aDouble -> {
            String status = UIUtil.getPaymentStatus(aDouble);
            viewHolder.fullyPaid.setText(status);

            if (FULLY_PAID.equals(status)) {
                viewHolder.btnAddPayment.setVisibility(View.GONE);
                viewHolder.balanceLayout.setVisibility(View.GONE);
            }
            else viewHolder.btnAddPayment.setVisibility(View.VISIBLE);
        });

        mBalance.observe(lifecycleOwner.getViewLifecycleOwner(), aDouble -> {
            String balance = aDouble.toString();

            procedure.setDentalBalance(aDouble);
            viewHolder.balance.setText(balance);
        });

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProcedure();

            }
        });

        viewHolder.btnEdit.setOnClickListener(v -> {
            BottomEditOperationDialog editOperationDialog = new BottomEditOperationDialog(layoutInflater, context, lifecycleOwner);
            editOperationDialog.createOperationDialog(procedure);
            editOperationDialog.setPatient(patient);
            editOperationDialog.setProcedure(procedure);

            procedureDialog.dismiss();
            editOperationDialog.showDialog();
        });

        procedureDialog.setContentView(view);
    }

    //  Get payments from firebase then add them to payment layout
    private void loadPayments(LinearLayout paymentLayout) {

        ArrayList<String> paymentKeys = operation.getPaymentKeys();
        totalPayments = paymentKeys.size();

        ArrayList<Payment> paymentArrayList = new ArrayList<>();
        PaymentList paymentList = new PaymentList(paymentLayout, layoutInflater, lifecycleOwner, procedureDialog);
        paymentList.setProcedure(operation);
        paymentList.setPatient(patient);

        isOnlyOnePayment.observe(lifecycleOwner.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                paymentList.setOnlyOne(aBoolean);
            }
        });


        totalAmount = operation.getDentalTotalAmount();
        totalPaid = 0;

        for (int position = 0; position < paymentKeys.size(); position++) {
            int finalPosition = position;
            paymentRepository.getPayments(paymentKeys.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    isOnlyOnePayment.setValue(paymentKeys.size() == 1);

                    Payment payment = snapshot.getValue(Payment.class);

                    if (payment != null) {
                        Log.d(TAG, "onDataChange: payment: " + payment);
                        paymentArrayList.add(payment);
                        paymentsCounter.setValue(finalPosition +1);

                        totalPaid += payment.getAmount();
                        mBalance.setValue(totalAmount - totalPaid);

                        if (mBalance.getValue() != null)
                            procedureRepository.updateBalance(procedure, mBalance.getValue());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        Log.d(TAG, "loadPayments: paymentCounter value: " + paymentsCounter);

        paymentsCounter.observe(lifecycleOwner.getViewLifecycleOwner(), integer -> {
            Log.d(TAG, "loadPayments: integer: " + integer);
            Log.d(TAG, "loadPayments: total payments: " + totalPayments);
            if (integer == totalPayments) {
                paymentList.addItems(paymentArrayList);
            }
            isDoneLoading.setValue(true);

            Log.d(TAG, "loadPayments: paymentArray: " + paymentArrayList);
        });
    }

    private void removeProcedure() {
        procedureDialog.dismiss();
        Log.d(TAG, "removeProcedure: patient: " + patient);
        procedureRepository.removeProcedure(patient, operation.getUid(), operation.getPaymentKeys());
    }

    public void setPatient(Patient patient) {
        Log.d(TAG, "setPatient: patient: " + patient);
        this.patient = patient;
    }

    public void showDialog() {
        procedureDialog.show();
        procedureDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void dialogDismissListener(BottomSheetDialog dialog) {
        Log.d(TAG, "dialogDismissListener: Load procedures called");
        dialog.setOnDismissListener(dialog1 -> lifecycleOwner.loadProcedures());
    }

    private static class ViewHolder {

        final TextView title;
        final ImageView iconClose;
        final TextView operationDesc;
        final TextView operationTotalAmount;
        final TextView operationDate;
        final TextView fullyPaid;
        final LinearLayout paymentsLayout;
        final TextView balance;
        final Button btnDelete;
        final Button btnEdit;
        final Button btnAddPayment;
        final LinearLayout balanceLayout;


        public ViewHolder(View view) {
            this.title = view.findViewById(R.id.tvProcedureTitle);
            this.operationDesc = view.findViewById(R.id.tvOperationDescription);
            this.operationTotalAmount = view.findViewById(R.id.tvOperationTotalAmount);
            this.operationDate = view.findViewById(R.id.tvOperationDate);
            this.fullyPaid = view.findViewById(R.id.tvOperationFullyPaid);
            this.btnAddPayment = view.findViewById(R.id.btnOperationAddPayment);
            this.iconClose = view.findViewById(R.id.iconCloseEP);
            this.paymentsLayout = view.findViewById(R.id.paymentsList);
            this.balance = view.findViewById(R.id.tvBalance);
            this.btnDelete = view.findViewById(R.id.btnOperationDelete);
            this.btnEdit = view.findViewById(R.id.btnOperationEdit);
            this.balanceLayout = view.findViewById(R.id.operationBalanceLayout);
        }
    }
}
