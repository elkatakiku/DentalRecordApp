package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.ProgressNote;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.ui.dialog.BottomProgressNoteFormDialog;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_info.PatientInfoFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class ProgressNoteList {
    private static final String TAG = ProgressNoteList.class.getSimpleName();

    private final LinearLayout linearLayout;
    private final LayoutInflater layoutInflater;
    private final PatientInfoFragment lifecycleOwner;
    private final BottomSheetDialog operationsDialog;
    private BottomProgressNoteFormDialog paymentDialog;
    private Procedure procedure;
    private boolean isOnlyOne;
    private Patient patient;

    public ProgressNoteList(LinearLayout linearLayout, LayoutInflater layoutInflater,
                            PatientInfoFragment lifecycleOwner, BottomSheetDialog operationsDialog) {
        this.linearLayout = linearLayout;
        this.layoutInflater = layoutInflater;
        this.lifecycleOwner = lifecycleOwner;
        this.operationsDialog = operationsDialog;
    }

    public void addItem(ProgressNote progressNote) {
        initializeProgressNote(progressNote);

        Log.d(TAG, "addItem: progress note after initializing: " + progressNote);

        ViewHolder viewHolder = new ViewHolder(layoutInflater);

        String amount = progressNote.getAmount().toString();
        viewHolder.paymentDate.setText(progressNote.getDate());
        viewHolder.desciption.setText(progressNote.getDescription());
        viewHolder.paymentAmount.setText(amount);

        viewHolder.cardView.setOnClickListener(v -> {
            createDialog(layoutInflater, layoutInflater.getContext(), lifecycleOwner, progressNote);
            paymentDialog.showDialog();
            operationsDialog.dismiss();
        });

        linearLayout.addView(viewHolder.cardView);
    }

    public void createDialog(LayoutInflater layoutInflater, Context context,
                             PatientInfoFragment lifecycleOwner, ProgressNote progressNote) {

        //  Create progressNote dialog edit/delete style
        this.paymentDialog = new BottomProgressNoteFormDialog(layoutInflater, context, lifecycleOwner, true);
        paymentDialog.setProcedure(procedure);
        paymentDialog.setOnlyOne(isOnlyOne);
        paymentDialog.setPatient(patient);
        paymentDialog.createDialog(progressNote);
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public void setOnlyOne(boolean isOnlyOne) {
        this.isOnlyOne = isOnlyOne;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void addItems(ArrayList<ProgressNote> progressNotes) {
        for (ProgressNote item : progressNotes) {
            addItem(item);
        }
    }

    private void initializeProgressNote(ProgressNote progressNote) {
        if (!Checker.isDataAvailable(progressNote.getDate())) progressNote.setDate(Checker.NOT_AVAILABLE);
        if (!Checker.isDataAvailable(progressNote.getDescription())) progressNote.setDescription(Checker.NOT_AVAILABLE);
    }

    private static class ViewHolder {

        final CardView cardView;
        final TextView paymentAmount;
        final TextView desciption;
        final TextView paymentDate;

        public ViewHolder(LayoutInflater layoutInflater) {
            this.cardView = (CardView) layoutInflater.inflate(R.layout.item_progress_card, null);
            this.paymentAmount = cardView.findViewById(R.id.tvProgressAmount);
            this.desciption = cardView.findViewById(R.id.tvProgressDesc);
            this.paymentDate = cardView.findViewById(R.id.tvProgressDate);
        }
    }
}
