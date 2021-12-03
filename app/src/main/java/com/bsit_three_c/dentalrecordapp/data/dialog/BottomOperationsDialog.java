package com.bsit_three_c.dentalrecordapp.data.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class BottomOperationsDialog {

    private LayoutInflater layoutInflater;
    private final Context context;
    private BottomSheetDialog bottomSheetDialog;

    public BottomOperationsDialog(LayoutInflater layoutInflater, Context context) {
        this.layoutInflater = layoutInflater;
        this.context = context;
    }

    public void addItem(Payment payment, int position) {
        ViewHolder viewHolder = new ViewHolder(layoutInflater);

        bottomSheetDialog = new BottomSheetDialog(context);
        View view = layoutInflater.inflate(R.layout.bottom_operation_details, null, false);

        view.findViewById(R.id.btnOperationAddPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomPaymentDialog bottomPaymentDialog = new BottomPaymentDialog(layoutInflater, context);
                bottomPaymentDialog.createDialog();

                bottomSheetDialog.dismiss();
                bottomPaymentDialog.showDialog();
            }
        });

        LinearLayout paymentLayout = (LinearLayout) view.findViewById(R.id.paymentsList);

        String amount = payment.getAmount().toString();
        viewHolder.paymentAmount.setText(amount);
        viewHolder.modeOfPayment.setText(payment.getModeOfPayment());
        viewHolder.paymentDate.setText(payment.getPaymentDate());
        viewHolder.cardView.setTag(position);

        paymentLayout.addView(viewHolder.cardView);

        bottomSheetDialog.setContentView(view);

//        binding.listOperations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
//                bottomSheetDialog.show();
//            }
//        });

//        bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public void addItems(ArrayList<Payment> payments) {
        for (int position = 0; position < payments.size(); position++) {
            addItem(payments.get(position), position);
        }
    }

    public void showDialog() {
        bottomSheetDialog.show();
        bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private static class ViewHolder {

        final CardView cardView;
        final TextView modeOfPayment;
        final TextView paymentAmount;
        final TextView paymentDate;

        public ViewHolder(LayoutInflater layoutInflater) {
            this.cardView = (CardView) layoutInflater.inflate(R.layout.item_payment_card, null);
            this.modeOfPayment = cardView.findViewById(R.id.txtViewPaymentMOD);
            this.paymentAmount = cardView.findViewById(R.id.txtViewPaymentAmount);
            this.paymentDate = cardView.findViewById(R.id.textViewPaymentDate);
        }
    }
}
