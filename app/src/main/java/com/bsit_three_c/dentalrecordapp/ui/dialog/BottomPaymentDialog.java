package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.patient.PaymentRepository;
import com.bsit_three_c.dentalrecordapp.ui.patient_info.PatientInfoFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Date;

public class BottomPaymentDialog {
    private static final String TAG = BottomPaymentDialog.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    private final Context context;
    private final View view;
    private final PatientInfoFragment lifecycleOwner;
    private BottomSheetDialog paymentDialog;

    private final PaymentRepository repository;
    private Procedure procedure;
    private Patient patient;

    private final MutableLiveData<FormState> mAmount = new MutableLiveData<>();
    private final MutableLiveData<FormState> mState = new MutableLiveData<>();

    private final boolean isEdit;
    private boolean isOnlyOne;

    //  Used to create dialog to add payment
    public BottomPaymentDialog(LayoutInflater layoutInflater, Context context, PatientInfoFragment lifecycleOwner) {
       this(layoutInflater, context, lifecycleOwner, false);
    }

    //  Used to create dialog to edit payment
    public BottomPaymentDialog(LayoutInflater layoutInflater, Context context, PatientInfoFragment lifecycleOwner, boolean isEditPayment) {

        this.layoutInflater = layoutInflater;
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.isEdit = isEditPayment;

        this.view = layoutInflater.inflate(R.layout.bottom_payment, null, false);
        this.repository = PaymentRepository.getInstance();
    }

    //  Used to create a dialog to add payment
    public void createDialog(Procedure operation) {
        this.procedure = operation;

        ViewHolder viewHolder = new ViewHolder(view);
        paymentDialog = new BottomSheetDialog(context);

        viewHolder.btnPaymentConfirm.setEnabled(false);

        viewHolder.btnPaymentConfirm.setOnClickListener(v -> {

            // Add payment to operation
            String date = UIUtil.getDate(UIUtil.getDate(viewHolder.paymentDialogDate));
            int modeOfPayment = viewHolder.modeOfPaymentDialog.getSelectedItemPosition();
            String amount = viewHolder.paymentDialogAmount.getText().toString();

            repository.addPayment(operation,
//                    modeOfPayment,
                    amount, date);
            paymentDialog.dismiss();

        });

        viewHolder.paymentDialogAmount.addTextChangedListener(textWatcher);

//        viewHolder.modeOfPaymentDialog.setOnItemSelectedListener(new CustomItemSelectedListener("None", this));

        setObservers(viewHolder);
        setCloseIcon(viewHolder);
        setDialogDismissListener(paymentDialog);

        paymentDialog.setContentView(view);
    }

    //  Used to create a dialog to edit payment
    public void createDialog(Payment payment) {
        ViewHolder viewHolder = new ViewHolder(view);
        paymentDialog = new BottomSheetDialog(context);

        Date oldDate = UIUtil.stringToDate(payment.getPaymentDate());
        int day = Integer.parseInt(UIUtil.getDateUnits(oldDate)[0]);
        int month = Integer.parseInt(UIUtil.getDateUnits(oldDate)[1]) - 1;
        int year = Integer.parseInt(UIUtil.getDateUnits(oldDate)[2]);
        String oldAmount = String.valueOf(payment.getAmount());

        viewHolder.paymentDialogAmount.addTextChangedListener(textWatcher);

        viewHolder.paymentDialogTitle.setText(isEdit ? "Edit Payment" : "Add Payment");
        viewHolder.paymentDialogDate.updateDate(year, month, day);
        viewHolder.paymentDialogAmount.setText(oldAmount);

        setObservers(viewHolder);
        setBtnConfirm(viewHolder, payment);
        setCloseIcon(viewHolder);
        setBtnDelete(viewHolder, payment.getUid());
        setDialogDismissListener(paymentDialog);

        paymentDialog.setContentView(view);
    }

    private void setBtnConfirm(ViewHolder viewHolder, Payment payment) {

        viewHolder.btnPaymentConfirm.setOnClickListener(v -> {

            // Update payment
            String date = UIUtil.getDate(UIUtil.getDate(viewHolder.paymentDialogDate));
            String amount = viewHolder.paymentDialogAmount.getText().toString();
            double convertedAmount = UIUtil.convertToDouble(amount);

            double newBalance = 0d;
            Log.d(TAG, "setBtnConfirm: size: " + procedure.getPaymentKeys().size());
            if (procedure.getPaymentKeys().size() >= 1) {
                Log.d(TAG, "setBtnConfirm: this is only 1 payment");
                newBalance = procedure.getDentalBalance() - convertedAmount;
            }
            else {
                Log.d(TAG, "setBtnConfirm: this is not");
                newBalance = procedure.getDentalTotalAmount() - convertedAmount;
            }


            payment.setPaymentDate(date);
            payment.setAmount(convertedAmount);

            procedure.setDentalBalance(newBalance);

            //  Update payment
            repository.updatePayment(payment, procedure);

            paymentDialog.dismiss();

        });
    }

    private void setCloseIcon(ViewHolder viewHolder) {
        viewHolder.iconClosePaymentDialog.setOnClickListener(v -> {
            Log.d(TAG, "onClick: closing dialog");
            paymentDialog.dismiss();
            Log.d(TAG, "onClick: payment dialog closed");
        });
    }

    private void setBtnDelete(ViewHolder viewHolder, String paymentUID) {
        if (!isOnlyOne) viewHolder.btnDelete.setVisibility(View.VISIBLE);
        else viewHolder.btnDelete.setVisibility(View.GONE);

        viewHolder.btnDelete.setOnClickListener(v -> {

            //  Add value payment UID to delete specific payment
            repository.removePayment(procedure, paymentUID);
            paymentDialog.dismiss();
        });
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setProcedure(Procedure procedure) {
        if (this.procedure == null) this.procedure = procedure;
    }

    public void setOnlyOne(boolean onlyOne) {
        isOnlyOne = onlyOne;
    }

    private void setDialogDismissListener(BottomSheetDialog dialog) {
        dialog.setOnDismissListener(dialog1 -> {
            lifecycleOwner.loadProcedures();

            BottomOperationsDialog operationsDialog = new BottomOperationsDialog(layoutInflater, context, lifecycleOwner);
            operationsDialog.setPatient(patient);
            operationsDialog.createOperationDialog(procedure);
            operationsDialog.showDialog();
        });
    }

    private void setAmountState(String input) {

        if (input == null || input.isEmpty())
            mAmount.setValue(new FormState(R.string.invalid_empty_input));
        else if (Checker.isRepeated(input, "."))
            mAmount.setValue(new FormState(R.string.invalid_contains_two_or_more_dots));
        else if (UIUtil.convertToDouble(input) == -1)
            mAmount.setValue(new FormState(R.string.invalid_input));
        else if (procedure.getPaymentKeys().size() <= 1 && (procedure.getDentalTotalAmount() - UIUtil.convertToDouble(input) >= 0))
            mAmount.setValue(new FormState(true));
        else if (Checker.isFullyPaid(input, procedure.getDentalBalance()))
            mAmount.setValue(new FormState(R.string.invalid_fully_paid));
        else mAmount.setValue(new FormState(true));

    }

    private void setButtonState() {
//        if (Checker.isComplete(mAmount, mModeOfPayment)) mState.setValue(new FormState(true));
        if (Checker.isComplete(mAmount)) mState.setValue(new FormState(true));
        else mState.setValue(new FormState(R.string.invalid_input));
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setAmountState(s.toString());
            setButtonState();
        }
    };

    private void setObservers(ViewHolder viewHolder) {
        mAmount.observe(lifecycleOwner.getViewLifecycleOwner(),
                new CustomObserver(viewHolder.paymentDialogAmount, lifecycleOwner.getResources()));
        mState.observe(lifecycleOwner.getViewLifecycleOwner(),
                new CustomObserver.ObserverButton(viewHolder.btnPaymentConfirm));
    }

    public void showDialog() {
        BottomDialog.showDialog(paymentDialog);
    }

    private static class ViewHolder {

        final LinearLayout linearLayoutRoot;
        final LinearLayout linearLayoutTitle;
        final ImageView iconClosePaymentDialog;
        final TextView paymentDialogTitle;
        final DatePicker paymentDialogDate;
        final Spinner modeOfPaymentDialog;
        final EditText paymentDialogAmount;
        final Button btnPaymentConfirm;

        final LinearLayout layoutOptionButtons;
        final Button btnEdit;
        final Button btnDelete;

        public ViewHolder(View view) {
            this.linearLayoutRoot = view.findViewById(R.id.linearLayoutPayment);
            this.linearLayoutTitle = view.findViewById(R.id.linearPaymentTitle);
            this.paymentDialogTitle = view.findViewById(R.id.tvPaymentTitle);
            this.modeOfPaymentDialog = view.findViewById(R.id.sprPaymentModeOfPayment);
            this.paymentDialogAmount = view.findViewById(R.id.editTextPaymentAmount);
            this.paymentDialogDate = view.findViewById(R.id.snprPaymentDate);
            this.btnPaymentConfirm = view.findViewById(R.id.btnPaymentConfirm);
            this.iconClosePaymentDialog = view.findViewById(R.id.iconPaymentClose);

            this.layoutOptionButtons = view.findViewById(R.id.layoutEditDeletePayment);
            this.btnEdit = view.findViewById(R.id.btnPaymentEdit);
            this.btnDelete = view.findViewById(R.id.btnPaymentDelete);
        }
    }
}
