package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_info.PatientInfoFragment;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Date;

public class BottomEditOperationDialog {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private BottomSheetDialog editOperationDialog;

    private final View view;

    private final PatientInfoFragment lifecycleOwner;
    private final ProcedureRepository procedureRepository;

    private Patient patient;
    private Procedure procedure;

    public BottomEditOperationDialog(LayoutInflater layoutInflater, Context context, PatientInfoFragment lifecycleOwner) {
        this.layoutInflater = layoutInflater;
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;

        this.view = layoutInflater.inflate(R.layout.bottom_edit_operation, null);
        this.procedureRepository = ProcedureRepository.getInstance();
    }

    public void createOperationDialog(Procedure procedure){

        ViewHolder viewHolder = new ViewHolder(view);
        editOperationDialog = new BottomSheetDialog(context);
        BottomDialog.setBackgroundColorTransparent(editOperationDialog);
        dialogDismissListener(editOperationDialog);

        Date oldDate = UIUtil.stringToDate(procedure.getDentalDate());
        int day = Integer.parseInt(UIUtil.getDateUnits(oldDate)[0]);
        int month = Integer.parseInt(UIUtil.getDateUnits(oldDate)[1]) - 1;
        int year = Integer.parseInt(UIUtil.getDateUnits(oldDate)[2]);

        String oldAmount = String.valueOf(procedure.getDentalTotalAmount());

        viewHolder.date.updateDate(year, month, day);
        viewHolder.service.setSelection(procedure.getService());
        viewHolder.description.setText(procedure.getDentalDesc());
        viewHolder.amount.setText(oldAmount);

        viewHolder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newDate = UIUtil.getDate(UIUtil.getDate(viewHolder.date));
                double newAmount = UIUtil.convertToDouble(viewHolder.amount.getText().toString());


                procedure.setDentalDate(newDate);
                procedure.setService(viewHolder.service.getSelectedItemPosition());
                procedure.setDentalDesc(viewHolder.description.getText().toString());
                procedure.setDentalTotalAmount(newAmount);

                procedureRepository.updateProcedure(procedure);
                editOperationDialog.dismiss();
            }
        });

        viewHolder.colse.setOnClickListener(v -> editOperationDialog.dismiss());

        editOperationDialog.setContentView(view);
    }

    private void dialogDismissListener(BottomSheetDialog editOperationDialog) {
        editOperationDialog.setOnDismissListener(dialog -> {
            lifecycleOwner.loadProcedures();

            BottomOperationsDialog operationsDialog = new BottomOperationsDialog(layoutInflater, context, lifecycleOwner);
            operationsDialog.setPatient(patient);
            operationsDialog.createOperationDialog(procedure);
            operationsDialog.showDialog();
        });
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public void showDialog() {
        BottomDialog.showDialog(editOperationDialog);
    }

    private static class ViewHolder {

        final DatePicker date;
        final Spinner service;
        final TextView description;
        final TextView amount;
        final Button btnConfirm;
        final ImageView colse;

        public ViewHolder(View view) {
            this.date = view.findViewById(R.id.snprEPDate);
            this.service = view.findViewById(R.id.spnrEPService);
            this.description = view.findViewById(R.id.edtEPDesc);
            this.amount = view.findViewById(R.id.editEPTotalAmount);
            this.btnConfirm = view.findViewById(R.id.btnEPConfirm);
            this.colse = view.findViewById(R.id.iconEPClose);
        }
    }
}
