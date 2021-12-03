package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.dialog.BottomOperationsDialog;
import com.bsit_three_c.dentalrecordapp.data.model.DentalOperation;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.util.ArrayList;

public class OperationsList {
    private static final String TAG = OperationsList.class.getSimpleName();

    private final LinearLayout linearLayout;
    private final LayoutInflater layoutInflater;

    public OperationsList(LinearLayout linearLayout, LayoutInflater layoutInflater) {
        this.linearLayout = linearLayout;
        this.layoutInflater = layoutInflater;
    }

    private void addItem(DentalOperation operation, int position) {
        ViewHolder viewHolder = new ViewHolder(layoutInflater);

        viewHolder.txtDentalDesc.setText(operation.getDentalDesc());
        try {
            viewHolder.txtDentalDate.setText(UIUtil.getReadableDate(UIUtil.stringToDate(operation.getDentalDate())));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "getView: error parsing");
        }
        viewHolder.txtDentalAmount.setText(String.valueOf(operation.getDentalAmount()));
        viewHolder.cbIsFullyPaid.setChecked(!operation.isDownpayment());

        viewHolder.cbIsFullyPaid.setBackgroundTintList(UIUtil.getCheckBoxColor(!operation.isDownpayment()));
        viewHolder.txtDentalFullyPaid.setTextColor(UIUtil.getCheckBoxColor(!operation.isDownpayment()));

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, viewHolder.txtDentalDesc.getText().toString(), Snackbar.LENGTH_SHORT).show();
                onItemClick(v);
            }
        });



        viewHolder.cardView.setTag(position);

        linearLayout.addView(viewHolder.cardView);
    }

    public void addItems(ArrayList<DentalOperation> dentalOperations) {
//        for (DentalOperation item : dentalOperations) {
//            addItem(item);
//        }
        for (int position = 0; position < dentalOperations.size(); position++) {
            addItem(dentalOperations.get(position), position);
        }
    }

    private static class ViewHolder {
        final CardView cardView;
        final TextView txtDentalDesc;
        final TextView txtDentalDate;
        final TextView txtDentalAmount;
        final TextView txtDentalFullyPaid;
        final CheckBox cbIsFullyPaid;

        public ViewHolder(LayoutInflater layoutInflater) {
            this.cardView = (CardView) layoutInflater.inflate(R.layout.item_dental_history, null);
            this.txtDentalDesc = cardView.findViewById(R.id.txtDentalDesc);
            this.txtDentalDate = cardView.findViewById(R.id.txtDentalDate);
            this.txtDentalAmount = cardView.findViewById(R.id.txtDentalAmount);
            this.txtDentalFullyPaid = cardView.findViewById(R.id.txtDentalFullyPaid);
            this.cbIsFullyPaid = cardView.findViewById(R.id.cbFullyPaid);
        }
    }

    private void onItemClick(View v) {
        // Show bottom dialogue
        // Pass the object: use the position to get the correspoding object
        int position = -1;
        if (v.getTag() instanceof Integer)
            position = (Integer) v.getTag();

        BottomOperationsDialog bottomOperationsDialog = new BottomOperationsDialog(layoutInflater, layoutInflater.getContext());
        bottomOperationsDialog.addItem(new Payment("UID", "oUID", 500.0, "Credit", "today"), 0);
        bottomOperationsDialog.showDialog();
    }
}
