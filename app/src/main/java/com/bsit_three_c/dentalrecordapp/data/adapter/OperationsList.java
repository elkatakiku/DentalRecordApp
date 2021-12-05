package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalProcedure;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.ui.dialog.BottomOperationsDialog;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.List;

public class OperationsList {
    private static final String TAG = OperationsList.class.getSimpleName();

    private final LinearLayout linearLayout;
    private final LayoutInflater layoutInflater;

    private List<DentalProcedure> dentalProcedures;
    private final Patient patient;

    private MutableLiveData<Boolean> hasProcedures = new MutableLiveData<>();

    private LifecycleOwner lifecycleOwner;

    public OperationsList(LinearLayout linearLayout, LayoutInflater layoutInflater, Patient patient, LifecycleOwner lifecycleOwner) {
        this.linearLayout = linearLayout;
        this.layoutInflater = layoutInflater;
        this.patient = patient;
        this.lifecycleOwner = lifecycleOwner;
    }

    private void addItem(DentalProcedure operation, int position) {
        ViewHolder viewHolder = new ViewHolder(layoutInflater);

        viewHolder.txtDentalDesc.setText(operation.getDentalDesc());
        viewHolder.txtDentalDate.setText(UIUtil.getReadableDate(UIUtil.stringToDate(operation.getDentalDate())));
        viewHolder.txtDentalAmount.setText(String.valueOf(operation.getDentalTotalAmount()));
        viewHolder.cbIsFullyPaid.setChecked(!operation.isDownpayment());
        viewHolder.txtDentalFullyPaid.setText(UIUtil.getPaymentStatus(operation.getDentalBalance()));
        viewHolder.txtDentalFullyPaid.setTextColor(UIUtil.getCheckBoxColor(operation.getDentalBalance()));

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v);
            }
        });

        viewHolder.cardView.setTag(position);

        linearLayout.addView(viewHolder.cardView);
    }

    public void addItem(DentalProcedure operation) {
        ViewHolder viewHolder = new ViewHolder(layoutInflater);

        viewHolder.txtDentalDesc.setText(operation.getDentalDesc());
        viewHolder.txtDentalDate.setText(UIUtil.getReadableDate(UIUtil.stringToDate(operation.getDentalDate())));
        viewHolder.txtDentalAmount.setText(String.valueOf(operation.getDentalTotalAmount()));
        viewHolder.cbIsFullyPaid.setChecked(!operation.isDownpayment());

        viewHolder.txtDentalFullyPaid.setText(UIUtil.getPaymentStatus(operation.isDownpayment()));
        viewHolder.txtDentalFullyPaid.setTextColor(UIUtil.getCheckBoxColor(!operation.isDownpayment()));

        viewHolder.cardView.setOnClickListener(v -> {
            onItemClick(v);
        });

        linearLayout.addView(viewHolder.cardView);
    }

    public void addItems(List<DentalProcedure> dentalProcedures) {
        if (dentalProcedures != null) {
            this.dentalProcedures = dentalProcedures;

            Log.d(TAG, "addItems: procedure array: " + dentalProcedures);
            Log.d(TAG, "addItems: list size: " + dentalProcedures.size());

            for (int position = 0; position < dentalProcedures.size(); position++) {
                if (dentalProcedures.get(position) != null) {
                    Log.d(TAG, "addItems: adding procedure: " + dentalProcedures.get(position).getDentalDesc());
                    addItem(dentalProcedures.get(position), position);
                }
            }
        }
    }

    private void clearItems() {

    }

    private boolean isListEmpty() {
        return dentalProcedures == null || dentalProcedures.isEmpty();
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

        DentalProcedure operation = null;
        if (position >= 0) {
            Log.d(TAG, "onItemClick: clicked operation: " + dentalProcedures.get(position));
            operation = dentalProcedures.get(position);
        }

        if (operation != null) {

            // Get payments transaction of selected operation
            BottomOperationsDialog bottomOperationsDialog = new BottomOperationsDialog(layoutInflater,
                    layoutInflater.getContext(), lifecycleOwner, patient);
            bottomOperationsDialog.createOperationDialog(operation);
            bottomOperationsDialog.showDialog();
        }
    }
}
