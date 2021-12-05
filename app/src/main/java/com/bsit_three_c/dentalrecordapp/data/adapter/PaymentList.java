package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalProcedure;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;
import com.bsit_three_c.dentalrecordapp.ui.dialog.BottomPaymentDialog;
import com.bsit_three_c.dentalrecordapp.ui.patient_info.PatientInfoFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class PaymentList {
    private static final String TAG = PaymentList.class.getSimpleName();

    private final LinearLayout linearLayout;
    private final LayoutInflater layoutInflater;
    private final PatientInfoFragment lifecycleOwner;
    private final BottomSheetDialog operationsDialog;
    private BottomPaymentDialog paymentDialog;
    private DentalProcedure operation;

    public PaymentList(LinearLayout linearLayout, LayoutInflater layoutInflater,
                       PatientInfoFragment lifecycleOwner, BottomSheetDialog operationsDialog) {
        this.linearLayout = linearLayout;
        this.layoutInflater = layoutInflater;
        this.lifecycleOwner = lifecycleOwner;
        this.operationsDialog = operationsDialog;
    }

    private void addItem(Payment payment) {
        ViewHolder viewHolder = new ViewHolder(layoutInflater);

        String amount = payment.getAmount().toString();
        viewHolder.paymentAmount.setText(amount);
        viewHolder.modeOfPayment.setText(payment.getModeOfPayment());
        viewHolder.paymentDate.setText(payment.getPaymentDate());

        viewHolder.cardView.setOnClickListener(v -> {
            createDialog(layoutInflater, layoutInflater.getContext(), lifecycleOwner, payment);
            paymentDialog.showDialog();
            operationsDialog.dismiss();
        });

        linearLayout.addView(viewHolder.cardView);
    }

    public void createDialog(LayoutInflater layoutInflater, Context context,
                             PatientInfoFragment lifecycleOwner, Payment payment) {

        //  Create payment dialog edit/delete style
        this.paymentDialog = new BottomPaymentDialog(layoutInflater, context, lifecycleOwner, true);
        paymentDialog.setOperation(operation);
        paymentDialog.createDialog(payment);

//        paymentDialog.showDialog();
//        operationsDialog.dismiss();
    }

    public void setOperation(DentalProcedure operation) {
        this.operation = operation;
    }

    public void addItems(ArrayList<Payment> payments) {
        for (Payment item : payments) {
            addItem(item);
        }
    }

    private static class ViewHolder {

        final CardView cardView;
        final TextView paymentAmount;
        final TextView modeOfPayment;
        final TextView paymentDate;

        public ViewHolder(LayoutInflater layoutInflater) {
            this.cardView = (CardView) layoutInflater.inflate(R.layout.item_payment_card, null);
            this.paymentAmount = cardView.findViewById(R.id.txtViewPaymentAmount);
            this.modeOfPayment = cardView.findViewById(R.id.txtViewPaymentMOD);
            this.paymentDate = cardView.findViewById(R.id.textViewPaymentDate);
        }
    }
}
