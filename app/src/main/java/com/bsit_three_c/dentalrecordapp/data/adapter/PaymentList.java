package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;

import java.util.ArrayList;

public class PaymentList {
    private static final String TAG = PaymentList.class.getSimpleName();

    private final LinearLayout linearLayout;
    private final LayoutInflater layoutInflater;

    public PaymentList(LinearLayout linearLayout, LayoutInflater layoutInflater) {
        this.linearLayout = linearLayout;
        this.layoutInflater = layoutInflater;
    }

    private void addItem(Payment payment) {
        PaymentList.ViewHolder viewHolder = new PaymentList.ViewHolder(layoutInflater);

//        viewHolder.txtDentalDesc.setText(operation.getDentalDesc());
//        try {
//            viewHolder.txtDentalDate.setText(UIUtil.getReadableDate(UIUtil.stringToDate(operation.getDentalDate())));
//        } catch (ParseException e) {
//            e.printStackTrace();
//            Log.d(TAG, "getView: error parsing");
//        }
//        viewHolder.txtDentalAmount.setText(String.valueOf(operation.getDentalAmount()));
//        viewHolder.cbIsFullyPaid.setChecked(!operation.isDownpayment());
//
//        viewHolder.cbIsFullyPaid.setBackgroundTintList(UIUtil.getCheckBoxColor(!operation.isDownpayment()));
//        viewHolder.txtDentalFullyPaid.setTextColor(UIUtil.getCheckBoxColor(!operation.isDownpayment()));

        String amount = payment.getAmount().toString();
        viewHolder.paymentAmount.setText(amount);
        viewHolder.modeOfPayment.setText(payment.getModeOfPayment());
        viewHolder.paymentDate.setText(payment.getPaymentDate());

        linearLayout.addView(viewHolder.cardView);
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
