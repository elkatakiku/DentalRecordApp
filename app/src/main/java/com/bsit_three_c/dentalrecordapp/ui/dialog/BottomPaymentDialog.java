package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.app.AlertDialog;
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
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Payment;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.repository.PaymentRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_info.PatientInfoFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;

public class BottomPaymentDialog {
    private static final String TAG = BottomPaymentDialog.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    private final Context context;
    private final View view;
    private final PatientInfoFragment lifecycleOwner;
    private BottomSheetDialog paymentDialog;

    private final PaymentRepository paymentRepository;
    private final ProcedureRepository procedureRepository;
    private Procedure procedure;
    private Patient patient;

    private final MutableLiveData<FormState> mState = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAmountState = new MutableLiveData<>();
    private final MutableLiveData<FormState> mBalanceState = new MutableLiveData<>();

    private AlertDialog alertDialog;

    private final boolean isEdit;
    private boolean isOnlyOne;
    private double balance;

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
        this.paymentRepository = PaymentRepository.getInstance();
        this.procedureRepository = ProcedureRepository.getInstance();
    }

    //  Used to create a dialog to add payment
    public void createDialog(Procedure operation) {
        this.procedure = operation;

        ViewHolder viewHolder = new ViewHolder(view);
        paymentDialog = new BottomSheetDialog(context);
        BottomDialog.setBackgroundColorTransparent(paymentDialog);

        viewHolder.btnPaymentConfirm.setEnabled(false);

        viewHolder.btnPaymentConfirm.setOnClickListener(v -> {

            // Add payment to operation
            String date = UIUtil.getDate(UIUtil.getDate(viewHolder.paymentDialogDate));
            String amount = viewHolder.paymentDialogAmount.getText().toString();

            paymentRepository.addPayment(operation, amount, date);
            paymentDialog.dismiss();

        });

        viewHolder.paymentDialogAmount.addTextChangedListener(textWatcher);

        setObservers(viewHolder);
        setCloseIcon(viewHolder);
        setDialogDismissListener(paymentDialog);

        paymentDialog.setContentView(view);
    }

    //  Used to create a dialog to edit payment
    public void createDialog(Payment payment) {
        ViewHolder viewHolder = new ViewHolder(view);
        paymentDialog = new BottomSheetDialog(context);
        BottomDialog.setBackgroundColorTransparent(paymentDialog);

        Date oldDate = UIUtil.stringToDate(payment.getPaymentDate());
        int day = Integer.parseInt(UIUtil.getDateUnits(oldDate)[0]);
        int month = Integer.parseInt(UIUtil.getDateUnits(oldDate)[1]) - 1;
        int year = Integer.parseInt(UIUtil.getDateUnits(oldDate)[2]);
        String oldAmount = String.valueOf(payment.getAmount());

        viewHolder.paymentDialogAmount.addTextChangedListener(textWatcher);

        viewHolder.paymentDialogTitle.setText(isEdit ? "Edit Payment" : "Add Payment");
        viewHolder.paymentDialogDate.updateDate(year, month, day);
        viewHolder.paymentDialogAmount.setText(oldAmount);

        mBalanceState.observe(lifecycleOwner.getViewLifecycleOwner(), new Observer<FormState>() {
            @Override
            public void onChanged(FormState formState) {
                if (formState == null) return;

                if (formState.getMsgError() != null) {
                    viewHolder.balance.setText(oldAmount);
                }
            }
        });

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
            paymentRepository.updatePayment(payment, procedure);

            paymentDialog.dismiss();

        });
    }

    private void setCloseIcon(ViewHolder viewHolder) {
        viewHolder.close.setOnClickListener(v -> {
            Log.d(TAG, "onClick: closing dialog");
            paymentDialog.dismiss();
            Log.d(TAG, "onClick: payment dialog closed");
        });
    }

    private void setBtnDelete(ViewHolder viewHolder, String paymentUID) {
        if (!isOnlyOne) viewHolder.btnDelete.setVisibility(View.VISIBLE);
        else viewHolder.btnDelete.setVisibility(View.GONE);

        viewHolder.btnDelete.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder
                    .setTitle(R.string.delete_title)
                    .setMessage(context.getString(R.string.delete_message) + " payment?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Snackbar.make(v, "Delete payment", Snackbar.LENGTH_SHORT).show();
                        procedureRepository.updatePaymentKeys(procedure, paymentUID);
                        paymentDialog.dismiss();
                    })
                    .setNegativeButton("No", (dialog, which) -> alertDialog.dismiss());
            alertDialog = builder.create();
            alertDialog.show();

            //  Add value payment UID to delete specific payment

//            paymentRepository.removePayment(procedure, paymentUID);

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

    private void dataChanged(String input) {

        if (input == null || input.isEmpty())
            mAmountState.setValue(new FormState(R.string.invalid_empty_input));
        else if (Checker.isRepeated(input, "."))
            mAmountState.setValue(new FormState(R.string.invalid_contains_two_or_more_dots));
        else if (UIUtil.convertToDouble(input) == -1)
            mAmountState.setValue(new FormState(R.string.invalid_input));
        else if (procedure.getPaymentKeys().size() <= 1 && (procedure.getDentalTotalAmount() - UIUtil.convertToDouble(input) >= 0))
            mAmountState.setValue(new FormState(true));
        else if (Checker.isFullyPaid(input, procedure.getDentalBalance()))
            mAmountState.setValue(new FormState(R.string.invalid_fully_paid));
        else mAmountState.setValue(new FormState(true));

        setBalance(input);

    }

    private void setButtonState() {
//        if (Checker.isComplete(mAmount, mModeOfPayment)) mState.setValue(new FormState(true));
        if (Checker.isComplete(mAmountState)) mState.setValue(new FormState(true));
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
            dataChanged(s.toString());
            setButtonState();
        }
    };

    private void setBalance(String input) {
        boolean hasAmount = Checker.isNotNullAndValid(mAmountState);

        //  Set event trigger function for input

        //  Balance - Amount
        double amount = UIUtil.convertToDouble(input);
        double balance = procedure.getDentalBalance();

        //  Update balance output

        this.balance = balance - amount;

        if (this.balance < 0) {
            mBalanceState.setValue(new FormState(R.string.invalid_input));
        }
    }

    private void setObservers(ViewHolder viewHolder) {
        mAmountState.observe(lifecycleOwner.getViewLifecycleOwner(),
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
        final LinearLayout layoutOptionButtons;

        final TextView paymentDialogTitle;
        final TextView balance;

        final ImageView close;

        final DatePicker paymentDialogDate;

        final EditText description;
        final EditText paymentDialogAmount;

        final Button btnPaymentConfirm;
        final Button btnEdit;
        final Button btnDelete;

        public ViewHolder(View view) {
            this.linearLayoutRoot = view.findViewById(R.id.linearLayoutPayment);
            this.linearLayoutTitle = view.findViewById(R.id.linearPaymentTitle);
            this.layoutOptionButtons = view.findViewById(R.id.layoutEditDeletePayment);

            this.paymentDialogTitle = view.findViewById(R.id.tvPaymentTitle);
            this.balance = view.findViewById(R.id.tvPaymentBalance);

            this.close = view.findViewById(R.id.iconPaymentClose);

            this.paymentDialogDate  = view.findViewById(R.id.snprPaymentDate);

            this.description = view.findViewById(R.id.etPaymentDescription);
            this.paymentDialogAmount = view.findViewById(R.id.editTextPaymentAmount);

            this.btnPaymentConfirm = view.findViewById(R.id.btnPaymentConfirm);
            this.btnEdit = view.findViewById(R.id.btnPaymentEdit);
            this.btnDelete = view.findViewById(R.id.btnPaymentDelete);
        }
    }
}
