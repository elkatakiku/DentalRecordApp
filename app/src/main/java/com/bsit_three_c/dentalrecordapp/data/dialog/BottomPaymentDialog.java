package com.bsit_three_c.dentalrecordapp.data.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsit_three_c.dentalrecordapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomPaymentDialog {

    private LayoutInflater layoutInflater;
    private final Context context;
    private BottomSheetDialog bottomSheetDialog;

    public BottomPaymentDialog(LayoutInflater layoutInflater, Context context) {
        this.layoutInflater = layoutInflater;
        this.context = context;
    }

//    public void createDialog(Payment payment, int position) {
    public void createDialog() {
        BottomPaymentDialog.ViewHolder viewHolder = new BottomPaymentDialog.ViewHolder(layoutInflater);

        bottomSheetDialog = new BottomSheetDialog(context);
        View view = layoutInflater.inflate(R.layout.bottom_payment, null, false);
//
//        LinearLayout paymentLayout = (LinearLayout) view.findViewById(R.id.paymentsList);
//
//        String amount = payment.getAmount().toString();
//        viewHolder.paymentAmount.setText(amount);
//        viewHolder.modeOfPayment.setText(payment.getModeOfPayment());
//        viewHolder.paymentDate.setText(payment.getPaymentDate());
//        viewHolder.cardView.setTag(position);
//
//        paymentLayout.addView(viewHolder.cardView);

        bottomSheetDialog.setContentView(view);
    }

    public void showDialog() {
        bottomSheetDialog.show();
        bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private static class ViewHolder {

        final LinearLayout linearLayoutRoot;
        final LinearLayout linearLayoutTitle;
        final Spinner modeOfPaymentDialog;
        final TextView paymentDialogAmount;
        final DatePicker paymentDialogDate;
        final TextView paymentDialogTitle;

        public ViewHolder(LayoutInflater layoutInflater) {
            this.linearLayoutRoot = (LinearLayout) layoutInflater.inflate(R.layout.bottom_payment, null);
            this.linearLayoutTitle = linearLayoutRoot.findViewById(R.id.linearPaymentTitle);
            this.paymentDialogTitle = linearLayoutTitle.findViewById(R.id.tvPaymentTitle);
            this.modeOfPaymentDialog = linearLayoutRoot.findViewById(R.id.sprPaymentModeOfPayment);
            this.paymentDialogAmount = linearLayoutRoot.findViewById(R.id.editTextPaymentAmount);
            this.paymentDialogDate = linearLayoutRoot.findViewById(R.id.snprPaymentDate);
        }
    }
}
