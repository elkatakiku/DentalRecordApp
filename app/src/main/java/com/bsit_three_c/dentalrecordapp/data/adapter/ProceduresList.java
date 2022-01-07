package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.ui.dialog.BottomOperationsDialog;
import com.bsit_three_c.dentalrecordapp.ui.patients.view_patient.PatientInfoFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.List;

public class ProceduresList {
    private static final String TAG = ProceduresList.class.getSimpleName();

    private final LinearLayout linearLayout;

    private List<Procedure> procedures;
    private final Patient patient;
    private final List<DentalServiceOption> dentalServiceOptions;

    private final PatientInfoFragment lifecycleOwner;

    public ProceduresList(LinearLayout linearLayout, Patient patient,
                          PatientInfoFragment lifecycleOwner, List<DentalServiceOption> dentalServiceOptions) {
        this.linearLayout = linearLayout;
        this.patient = patient;
        this.lifecycleOwner = lifecycleOwner;
        this.dentalServiceOptions = dentalServiceOptions;
    }

    private void addItem(Procedure procedure, int position) {
        ViewHolder viewHolder = new ViewHolder(lifecycleOwner.getLayoutInflater());
        initializeProcedure(procedure);

        //  Edit text in dental service
        viewHolder.txtDentalService.setText(UIUtil.getServiceTitle(procedure.getServiceIds(), dentalServiceOptions));
        viewHolder.txtDentalDate.setText(DateUtil.getReadableDate(DateUtil.convertToDate(procedure.getDentalDate())));
        viewHolder.txtDentalAmount.setText(String.valueOf(procedure.getDentalTotalAmount()));
        viewHolder.txtDentalFullyPaid.setText(UIUtil.getPaymentStatus(procedure.getDentalBalance()));
        viewHolder.txtDentalFullyPaid.setTextColor(UIUtil.getCheckBoxColor(procedure.getDentalBalance()));

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

    private void initializeProcedure(Procedure procedure) {
        if (!Checker.isDataAvailable(procedure.getDentalDesc())) procedure.setDentalDesc(Checker.NOT_AVAILABLE);
        if (!Checker.isDataAvailable(procedure.getDentalDate())) procedure.setDentalDate(Checker.NOT_AVAILABLE);
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
            BottomOperationsDialog bottomOperationsDialog = new BottomOperationsDialog(lifecycleOwner.getLayoutInflater(),
                    lifecycleOwner.getContext(), lifecycleOwner, dentalServiceOptions);
            bottomOperationsDialog.setPatient(patient);
            bottomOperationsDialog.createOperationDialog(procedure);
            bottomOperationsDialog.showDialog();
        }
    }
}