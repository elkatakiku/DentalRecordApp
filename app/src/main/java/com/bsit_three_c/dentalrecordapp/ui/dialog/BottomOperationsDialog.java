package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ProgressNoteList;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.model.ProgressNote;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProgressNoteRepository;
import com.bsit_three_c.dentalrecordapp.ui.patients.view_patient.ui.patientinfo.PatientInfoFragment;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BottomOperationsDialog {
    private static final String TAG = BottomOperationsDialog.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    private final Context context;
    private BottomSheetDialog procedureDialog;

    private final ProgressNoteRepository progressNoteRepository;
    private final ProcedureRepository procedureRepository;
    private Procedure operation;

    private final PatientInfoFragment lifecycleOwner;
    private final LinearLayout operationLayout;
    private final View view;
    private final List<DentalServiceOption> dentalServiceOptions;

    private final MutableLiveData<Integer> paymentsCounter = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDoneLoading = new MutableLiveData<>();
    private final MutableLiveData<Double> mBalance = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOnlyOnePayment = new MutableLiveData<>();

    private Procedure procedure;
    private int totalPayments;
    private double totalPaid;
    private double totalAmount;
    private boolean isFullyPaid;

    private AlertDialog alertDialog;

    private static final String FULLY_PAID = "Fully Paid";

    private Patient patient;

    public BottomOperationsDialog(LayoutInflater layoutInflater, Context context,
                                  PatientInfoFragment lifecycleOwner, List<DentalServiceOption> dentalServiceOptions) {
        this.layoutInflater = layoutInflater;
        this.context = context;
        this.progressNoteRepository = ProgressNoteRepository.getInstance();
        this.procedureRepository = ProcedureRepository.getInstance();
        this.view = layoutInflater.inflate(R.layout.bottom_procedure_details, null, false);
        this.operationLayout = view.findViewById(R.id.linearLayoutOperation);
        this.lifecycleOwner = lifecycleOwner;
        this.dentalServiceOptions = dentalServiceOptions;
    }

    // Sets local field operator and readied the dialog
    public void createOperationDialog(Procedure procedure){
        this.procedure = procedure;
        isDoneLoading.setValue(false);
        this.operation = procedure;

        ViewHolder viewHolder = new ViewHolder(operationLayout);
        procedureDialog = new BottomSheetDialog(context);
        BottomDialog.setBackgroundColorTransparent(procedureDialog);
        dialogDismissListener(procedureDialog);

        viewHolder.btnNewNote.setOnClickListener(v -> {
            BottomProgressNoteFormDialog paymentDialog = new BottomProgressNoteFormDialog(layoutInflater, context, lifecycleOwner, isFullyPaid, dentalServiceOptions);
            paymentDialog.createDialog(procedure);
            paymentDialog.setPatient(patient);

            procedureDialog.dismiss();
            paymentDialog.showDialog();
        });

        viewHolder.iconClose.setOnClickListener(v -> procedureDialog.dismiss());

        String totalAmount = String.valueOf(procedure.getDentalTotalAmount());
        viewHolder.title.setText(UIUtil.getServiceOptionsTitle(procedure.getServiceIds(), dentalServiceOptions));
        viewHolder.operationDesc.setText(procedure.getDentalDesc());
        viewHolder.operationDate.setText(procedure.getDentalDate());
        viewHolder.operationTotalAmount.setText(totalAmount);

        loadPayments(viewHolder.paymentsLayout);

        mBalance.observe(lifecycleOwner.getViewLifecycleOwner(), aDouble -> {
            String status = UIUtil.getPaymentStatus(aDouble);
            viewHolder.fullyPaid.setText(status);

            if (FULLY_PAID.equals(status)) {
                isFullyPaid = true;
                viewHolder.balanceLayout.setVisibility(View.GONE);
            }
            else {
                isFullyPaid = false;
                viewHolder.btnNewNote.setVisibility(View.VISIBLE);
            }
        });

        mBalance.observe(lifecycleOwner.getViewLifecycleOwner(), aDouble -> {
            String balance = aDouble.toString();

            procedure.setDentalBalance(aDouble);
            viewHolder.balance.setText(balance);
        });

        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder
                        .setTitle(context.getString(R.string.delete_title, "Procedure"))
                        .setMessage(context.getString(R.string.delete_message) + " procedure: " + UIUtil.getServiceOptionsTitle(procedure.getServiceIds(), dentalServiceOptions))
                        .setPositiveButton("Yes", (dialog, which) -> {
                            removeProcedure();
                        })
                        .setNegativeButton("No", (dialog, which) -> alertDialog.dismiss());
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        viewHolder.btnEdit.setOnClickListener(v -> {
            BottomEditProcedureDialog editOperationDialog = new BottomEditProcedureDialog(layoutInflater, context, lifecycleOwner, dentalServiceOptions);
            editOperationDialog.createOperationDialog(procedure);
            editOperationDialog.setPatient(patient);
            editOperationDialog.setProcedure(procedure);

            procedureDialog.dismiss();
            editOperationDialog.showDialog();
        });

        procedureDialog.setContentView(view);
    }

    //  Get payments from firebase then add them to payment layout
    private void loadPayments(LinearLayout paymentLayout) {

        ArrayList<String> paymentKeys = (ArrayList<String>) operation.getPaymentKeys();
        totalPayments = paymentKeys.size();

        ArrayList<ProgressNote> progressNoteArrayList = new ArrayList<>();
        ProgressNoteList progressNoteList = new ProgressNoteList(paymentLayout, layoutInflater, lifecycleOwner, procedureDialog, dentalServiceOptions);
        progressNoteList.setProcedure(operation);
        progressNoteList.setPatient(patient);

        isOnlyOnePayment.observe(lifecycleOwner.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                progressNoteList.setOnlyOne(aBoolean);
            }
        });


        totalAmount = operation.getDentalTotalAmount();
        totalPaid = 0;

        for (int position = 0; position < paymentKeys.size(); position++) {
            int finalPosition = position;
            progressNoteRepository.getPath(paymentKeys.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    isOnlyOnePayment.setValue(paymentKeys.size() == 1);

                    ProgressNote progressNote = snapshot.getValue(ProgressNote.class);

                    if (progressNote != null) {
                        Log.d(TAG, "onDataChange: progressNote: " + progressNote);
                        progressNoteArrayList.add(progressNote);
                        paymentsCounter.setValue(finalPosition +1);

                        totalPaid += progressNote.getAmount();
                        mBalance.setValue(totalAmount - totalPaid);

                        if (mBalance.getValue() != null){
                            procedure.setDentalBalance(mBalance.getValue());
                            procedureRepository.upload(procedure);
//                            procedureRepository.updateBalance(procedure, mBalance.getValue());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        Log.d(TAG, "loadPayments: paymentCounter value: " + paymentsCounter);

        paymentsCounter.observe(lifecycleOwner.getViewLifecycleOwner(), integer -> {
            Log.d(TAG, "loadPayments: integer: " + integer);
            Log.d(TAG, "loadPayments: total payments: " + totalPayments);
            if (integer == totalPayments) {
                Log.d(TAG, "loadPayments: adding item");
                for (ProgressNote item : progressNoteArrayList) {
                    progressNoteList.addItem(item);
                }
//                progressNoteList.addItems(progressNoteArrayList);
            }
            isDoneLoading.setValue(true);

            Log.d(TAG, "loadPayments: paymentArray: " + progressNoteArrayList);
        });
    }

    private void removeProcedure() {
        procedureDialog.dismiss();
        Log.d(TAG, "removeProcedure: patient: " + patient);
        procedureRepository.removeProcedure(patient, operation.getUid(), operation.getPaymentKeys());
    }

    public void setPatient(Patient patient) {
        Log.d(TAG, "setPatient: patient: " + patient);
        this.patient = patient;
    }

    public void showDialog() {
        procedureDialog.show();
        procedureDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void dialogDismissListener(BottomSheetDialog dialog) {
        Log.d(TAG, "dialogDismissListener: Load procedures called");
        dialog.setOnDismissListener(dialog1 -> lifecycleOwner.loadProcedures());
    }

    private static class ViewHolder {

        final TextView title;
        final ImageView iconClose;
        final TextView operationDesc;
        final TextView operationTotalAmount;
        final TextView operationDate;
        final TextView fullyPaid;
        final LinearLayout paymentsLayout;
        final TextView balance;
        final Button btnDelete;
        final Button btnEdit;
        final Button btnNewNote;
        final LinearLayout balanceLayout;


        public ViewHolder(View view) {
            this.title = view.findViewById(R.id.tvProcedureTitle);
            this.operationDesc = view.findViewById(R.id.tvOperationDescription);
            this.operationTotalAmount = view.findViewById(R.id.tvOperationTotalAmount);
            this.operationDate = view.findViewById(R.id.tvOperationDate);
            this.fullyPaid = view.findViewById(R.id.tvOperationFullyPaid);
            this.btnNewNote = view.findViewById(R.id.btnOperationNewNote);
            this.iconClose = view.findViewById(R.id.iconCloseEP);
            this.paymentsLayout = view.findViewById(R.id.progressNotesList);
            this.balance = view.findViewById(R.id.tvBalance);
            this.btnDelete = view.findViewById(R.id.btnOperationDelete);
            this.btnEdit = view.findViewById(R.id.btnOperationEdit);
            this.balanceLayout = view.findViewById(R.id.operationBalanceLayout);
        }
    }
}
