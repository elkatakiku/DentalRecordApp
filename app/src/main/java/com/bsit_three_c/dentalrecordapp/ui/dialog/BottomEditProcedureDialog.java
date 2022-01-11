package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceOptionsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.ui.patients.view_patient.ui.patientinfo.PatientInfoFragment;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BottomEditProcedureDialog {
    private static final String TAG = BottomEditProcedureDialog.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    private final Context context;
    private BottomSheetDialog editOperationDialog;
    private final List<DentalServiceOption> dentalServiceOptions;

    private final View view;

    private final PatientInfoFragment lifecycleOwner;
    private final ProcedureRepository procedureRepository;

    private Patient patient;
    private Procedure procedure;

    private List<DentalServiceOption> serviceOptions = new ArrayList<>();

    public BottomEditProcedureDialog(LayoutInflater layoutInflater, Context context,
                                     PatientInfoFragment lifecycleOwner, List<DentalServiceOption> dentalServiceOptions) {
        this.layoutInflater = layoutInflater;
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.dentalServiceOptions = dentalServiceOptions;

        this.view = layoutInflater.inflate(R.layout.bottom_procedure_edit, null);
        this.procedureRepository = ProcedureRepository.getInstance();

        serviceOptions.add(new DentalServiceOption(ServiceOptionsAdapter.DEFAULT_OPTION, ServiceOptionsAdapter.DEFAULT_OPTION, false));
    }

    public void createOperationDialog(Procedure procedure){

        ViewHolder viewHolder = new ViewHolder(view);
        editOperationDialog = new BottomSheetDialog(context);
        BottomDialog.setBackgroundColorTransparent(editOperationDialog);
        dialogDismissListener(editOperationDialog);

        Date oldDate = DateUtil.convertToDate(procedure.getDentalDate());
        int day = Integer.parseInt(DateUtil.toStringArray(oldDate)[0]);
        int month = Integer.parseInt(DateUtil.toStringArray(oldDate)[1]) - 1;
        int year = Integer.parseInt(DateUtil.toStringArray(oldDate)[2]);

        String oldAmount = String.valueOf(procedure.getDentalTotalAmount());

        viewHolder.date.updateDate(year, month, day);
        initializeServices(viewHolder, procedure.getServiceIds());
        viewHolder.description.setText(procedure.getDentalDesc());
        viewHolder.amount.setText(oldAmount);

        viewHolder.btnConfirm.setOnClickListener(v -> {
            String newDate = DateUtil.getDate(DateUtil.getDate(viewHolder.date));
            double newAmount = UIUtil.convertToDouble(viewHolder.amount.getText().toString());


            procedure.setDentalDate(newDate);
            procedure.setServiceIds(UIUtil.getServiceUids(serviceOptions));
            procedure.setDentalDesc(viewHolder.description.getText().toString());
            procedure.setDentalTotalAmount(newAmount);

            procedureRepository.upload(procedure);
            editOperationDialog.dismiss();
        });

        viewHolder.colse.setOnClickListener(v -> editOperationDialog.dismiss());

        editOperationDialog.setContentView(view);
    }

    private void initializeServices(ViewHolder viewHolder, List<String> services) {
        Log.d(TAG, "initializeServices: services: " + services);

        for (int i = 0; i < dentalServiceOptions.size(); i++) {

            serviceOptions.add(new DentalServiceOption(
                    dentalServiceOptions.get(i).getServiceUID(),
                    dentalServiceOptions.get(i).getTitle(),
                    false)
            );
        }

        Log.d(TAG, "initializeServices: service options: " + serviceOptions);

        for (String serviceUid : services) {
            DentalServiceOption currentService = getSelectedServices(serviceOptions, serviceUid);
            if (currentService != null) {
                currentService.setSelected(true);
            }
        }

        Log.d(TAG, "initializeServices: services presented: " + serviceOptions);

        ServiceOptionsAdapter serviceOptionsAdapter = new ServiceOptionsAdapter(lifecycleOwner.getContext(),
                0, serviceOptions, viewHolder.service);
        serviceOptionsAdapter.initializeSpinner();
        viewHolder.service.setAdapter(serviceOptionsAdapter);
    }

    private DentalServiceOption getSelectedServices(List<DentalServiceOption> dentalServiceOptions, String serviceUid) {
        DentalServiceOption service = null;

        for (DentalServiceOption option : dentalServiceOptions) {
            if (option.getServiceUID().equals(serviceUid)) {
                service = option;
                break;
            }
        }

        return service;
    }

    private void dialogDismissListener(BottomSheetDialog editOperationDialog) {
        editOperationDialog.setOnDismissListener(dialog -> {
            lifecycleOwner.loadProcedures();

            BottomOperationsDialog operationsDialog = new BottomOperationsDialog(layoutInflater, context, lifecycleOwner, dentalServiceOptions);
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
