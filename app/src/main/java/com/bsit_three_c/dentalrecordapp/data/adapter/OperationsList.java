package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.ui.dialog.BottomOperationsDialog;
import com.bsit_three_c.dentalrecordapp.ui.patient_info.PatientInfoFragment;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.List;

public class OperationsList {
    private static final String TAG = OperationsList.class.getSimpleName();

    private final LinearLayout linearLayout;
    private final LayoutInflater layoutInflater;

    private List<Procedure> procedures;
    private final Patient patient;

    private final PatientInfoFragment lifecycleOwner;

    public OperationsList(LinearLayout linearLayout, LayoutInflater layoutInflater, Patient patient,
                          PatientInfoFragment lifecycleOwner) {
        this.linearLayout = linearLayout;
        this.layoutInflater = layoutInflater;
        this.patient = patient;
        this.lifecycleOwner = lifecycleOwner;
    }

    private void addItem(Procedure operation, int position) {
        ViewHolder viewHolder = new ViewHolder(layoutInflater);

        viewHolder.txtDentalService.setText(UIUtil.getService(lifecycleOwner.getResources(), operation.getService()));
        viewHolder.txtDentalDate.setText(UIUtil.getReadableDate(UIUtil.stringToDate(operation.getDentalDate())));
        viewHolder.txtDentalAmount.setText(String.valueOf(operation.getDentalTotalAmount()));
        viewHolder.txtDentalFullyPaid.setText(UIUtil.getPaymentStatus(operation.getDentalBalance()));
        viewHolder.txtDentalFullyPaid.setTextColor(UIUtil.getCheckBoxColor(operation.getDentalBalance()));

        viewHolder.cardView.setOnClickListener(this::onItemClick);

        viewHolder.cardView.setTag(position);

        linearLayout.addView(viewHolder.cardView);
    }

    public void addItems(List<Procedure> procedures) {

        clearItems();

        if (procedures != null) {
            this.procedures = procedures;

            for (int position = 0; position < procedures.size(); position++) {
                if (procedures.get(position) != null)
                    addItem(procedures.get(position), position);
            }
        }

    }

    public void clearItems() {

        //  Remove views from index 2 upwards
        while (linearLayout.getChildCount() > 2) {
            linearLayout.removeViewAt(linearLayout.getChildCount()-1);
        }

    }

    private static class ViewHolder {
        final CardView cardView;
        final TextView txtDentalService;
        final TextView txtDentalDate;
        final TextView txtDentalAmount;
        final TextView txtDentalFullyPaid;

        public ViewHolder(LayoutInflater layoutInflater) {
            this.cardView = (CardView) layoutInflater.inflate(R.layout.item_dental_history, null);
            this.txtDentalService = cardView.findViewById(R.id.txtDentalService);
            this.txtDentalDate = cardView.findViewById(R.id.txtDentalDate);
            this.txtDentalAmount = cardView.findViewById(R.id.txtDentalAmount);
            this.txtDentalFullyPaid = cardView.findViewById(R.id.txtDentalFullyPaid);
        }
    }

    private void onItemClick(View v) {
        // Show bottom dialogue
        // Pass the object: use the position to get the correspoding object
        int position = -1;
        if (v.getTag() instanceof Integer)
            position = (Integer) v.getTag();

        Procedure procedure = null;
        if (position >= 0) {
            Log.d(TAG, "onItemClick: clicked operation: " + procedures.get(position));
            procedure = procedures.get(position);
        }

        if (procedure != null) {

            // Get payments transaction of selected operation
            BottomOperationsDialog bottomOperationsDialog = new BottomOperationsDialog(layoutInflater,
                    layoutInflater.getContext(), lifecycleOwner);
            bottomOperationsDialog.setPatient(patient);
            bottomOperationsDialog.createOperationDialog(procedure);
            bottomOperationsDialog.showDialog();
        }
    }
}
