package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalProcedure;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;
import com.bsit_three_c.dentalrecordapp.data.patient.PaymentRepository;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Date;

public class BottomPaymentDialog {
    private static final String TAG = BottomPaymentDialog.class.getSimpleName();

    private LayoutInflater layoutInflater;
    private final Context context;
    private BottomSheetDialog paymentDialog;

    private final View view;
    private final LinearLayout paymentLayout;

    private final PaymentRepository repository;
    private DentalProcedure operation;
    private LifecycleOwner lifecycleOwner;

    private static final String MODE_DEBIT = "Debit";
    private static final String MODE_CREDIT = "Credit";

    private Patient patient;

    public BottomPaymentDialog(LayoutInflater layoutInflater, Context context, LifecycleOwner lifecycleOwner) {
        this.layoutInflater = layoutInflater;
        this.context = context;

        this.view = layoutInflater.inflate(R.layout.bottom_payment, null, false);
        this.paymentLayout = view.findViewById(R.id.paymentsList);
        this.repository = PaymentRepository.getInstance();
        this.lifecycleOwner = lifecycleOwner;
    }

//    public void createDialog(Payment payment, int position) {
    public void createDialog(DentalProcedure operation) {
        this.operation = operation;

        ViewHolder viewHolder = new ViewHolder(view);
        paymentDialog = new BottomSheetDialog(context);

        viewHolder.btnPaymentConfirm.setOnClickListener(v -> {

            // Add payment to operation
            String date = UIUtil.getDate(UIUtil.getDate(viewHolder.paymentDialogDate));
            String modeOfPayment = viewHolder.modeOfPaymentDialog.getSelectedItem().toString();
            String amount = viewHolder.paymentDialogAmount.getText().toString();

            Log.d(TAG, "onClick: date: " + date);
            Log.d(TAG, "onClick: modeOfPayment: " + modeOfPayment);
            Log.d(TAG, "onClick: amount: " + amount);

            repository.addPayment(operation, modeOfPayment, amount, date);

//            BottomOperationsDialog operationsDialog = new BottomOperationsDialog(layoutInflater, context, lifecycleOwner);
//            operationsDialog.createOperationDialog(operation);

            paymentDialog.dismiss();
//            operationsDialog.showDialog();

        });

        setCloseIcon(viewHolder);
        setDialogDismissListener(paymentDialog);

        paymentDialog.setContentView(view);
    }

    public void createDialog(Payment payment) {
        ViewHolder viewHolder = new ViewHolder(view);
        paymentDialog = new BottomSheetDialog(context);

        Date oldDate = UIUtil.stringToDate(payment.getPaymentDate());
        int day = Integer.parseInt(UIUtil.getDateUnits(oldDate)[0]);
        int month = Integer.parseInt(UIUtil.getDateUnits(oldDate)[1]) - 1;
        int year = Integer.parseInt(UIUtil.getDateUnits(oldDate)[2]);
        String oldAmount = String.valueOf(payment.getAmount());

        viewHolder.paymentDialogDate.updateDate(year, month, day);
        viewHolder.modeOfPaymentDialog.setSelection(getPosition(payment.getModeOfPayment()));
        viewHolder.paymentDialogAmount.setText(oldAmount);

        setBtnConfirm(viewHolder);
        setCloseIcon(viewHolder);
        setBtnDelete(viewHolder);
        setDialogDismissListener(paymentDialog);

        paymentDialog.setContentView(view);
    }

    private void setBtnConfirm(ViewHolder viewHolder) {
        viewHolder.btnPaymentConfirm.setOnClickListener(v -> {

            // Add payment to operation
            String date = UIUtil.getDate(UIUtil.getDate(viewHolder.paymentDialogDate));
            String modeOfPayment = viewHolder.modeOfPaymentDialog.getSelectedItem().toString();
            String amount = viewHolder.paymentDialogAmount.getText().toString();

            repository.addPayment(operation, modeOfPayment, amount, date);

            BottomOperationsDialog operationsDialog = new BottomOperationsDialog(layoutInflater, context, lifecycleOwner);
            operationsDialog.createOperationDialog(operation);

            paymentDialog.dismiss();
            operationsDialog.showDialog();

        });
    }

    private void setCloseIcon(ViewHolder viewHolder) {
        viewHolder.iconClosePaymentDialog.setOnClickListener(v -> {
            Log.d(TAG, "onClick: closing dialog");
            paymentDialog.dismiss();
            Log.d(TAG, "onClick: payment dialog closed");
        });
    }

    private void setBtnDelete(ViewHolder viewHolder) {
        viewHolder.btnDelete.setVisibility(View.VISIBLE);
        viewHolder.btnDelete.setOnClickListener(v -> {

            //  Add value payment UID to delete specific payment
            repository.removePayment();
        });
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setOperation(DentalProcedure operation) {
        if (this.operation == null) this.operation = operation;
    }

    private void setDialogDismissListener(BottomSheetDialog dialog) {
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                BottomOperationsDialog operationsDialog = new BottomOperationsDialog(layoutInflater, context, lifecycleOwner, patient);
                operationsDialog.createOperationDialog(operation);
                operationsDialog.showDialog();
            }
        });
    }

    private int getPosition(final String mode) {
        switch (mode) {
            case MODE_DEBIT:
                return 0;
            case MODE_CREDIT:
                return 1;
        }
        return -1;
    }

    private void removePayment(ArrayList<String> procedureKeys) {


    }

    private void showOptionButtons(LinearLayout buttonsLayout, boolean bool) {
        buttonsLayout.setVisibility(bool ? View.VISIBLE : View.GONE);
    }

    public void showDialog() {
        paymentDialog.show();
        paymentDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private static class ViewHolder {

        final LinearLayout linearLayoutRoot;
        final LinearLayout linearLayoutTitle;
        final ImageView iconClosePaymentDialog;
        final TextView paymentDialogTitle;
        final DatePicker paymentDialogDate;
        final Spinner modeOfPaymentDialog;
        final TextView paymentDialogAmount;
        final Button btnPaymentConfirm;

        final LinearLayout layoutOptionButtons;
        final Button btnEdit;
        final Button btnDelete;

        public ViewHolder(View view) {
            this.linearLayoutRoot = view.findViewById(R.id.linearLayoutPayment);
            this.linearLayoutTitle = view.findViewById(R.id.linearPaymentTitle);
            this.paymentDialogTitle = view.findViewById(R.id.tvPaymentTitle);
            this.modeOfPaymentDialog = view.findViewById(R.id.sprPaymentModeOfPayment);
            this.paymentDialogAmount = view.findViewById(R.id.editTextPaymentAmount);
            this.paymentDialogDate = view.findViewById(R.id.snprPaymentDate);
            this.btnPaymentConfirm = view.findViewById(R.id.btnPaymentConfirm);
            this.iconClosePaymentDialog = view.findViewById(R.id.iconPaymentClose);

            this.layoutOptionButtons = view.findViewById(R.id.layoutEditDeletePayment);
            this.btnEdit = view.findViewById(R.id.btnPaymentEdit);
            this.btnDelete = view.findViewById(R.id.btnPaymentDelete);
        }
    }
}
