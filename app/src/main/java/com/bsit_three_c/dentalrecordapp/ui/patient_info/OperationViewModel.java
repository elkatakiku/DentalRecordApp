package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.patient.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.interfaces.SpinnerState;
import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.Date;

public class OperationViewModel extends ViewModel implements TextChange, SpinnerState {
    private static final String TAG = OperationViewModel.class.getSimpleName();

    private final ProcedureRepository repository;

    private final MutableLiveData<FormState> mOperationState = new MutableLiveData<>();
    private final MutableLiveData<FormState> mDescription = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAmount = new MutableLiveData<>();
    private final MutableLiveData<FormState> mPayment = new MutableLiveData<>();
    private final MutableLiveData<FormState> mBalance = new MutableLiveData<>();
//    private final MutableLiveData<FormState> mModeOfPayment = new MutableLiveData<>();
    private final MutableLiveData<FormState> mServices = new MutableLiveData<>();
    private final MutableLiveData<Double> mBalanceAmount = new MutableLiveData<>();

    private double amount;
    private double payment;
    private Double balance;

    private static final String DESCRIPTION = "Description";
    private static final String AMOUNT = "Amount";
    private static final String PAYMENT = "Payment";
    private static final String BALANCE = "Balance";
    private static final String MODE_OF_PAYMENT = "Mode of Payment";
    private static final String SERVICE = "Services";
    private static final String DEFAULT_MODE_OF_PAYMENT = "Choose mode of payment…";
    private static final String DEFAULT_SERVICES = "Choose services…";
    private static final int VALID = -1;
    private boolean isDownpayment = false;

    public OperationViewModel(ProcedureRepository repository) {
        this.repository = repository;
    }

    public void addProcedure(Patient patient, int service, String dentalDesc, Date dentalDate,
//                             int modeOfPayment,
                             String dentalAmount, boolean isFullyPaid, String dentalPayment, String dentalBalance) {
        repository.addProcedure(
                patient,
                service,
                dentalDesc,
                dentalDate,
//                modeOfPayment,
                dentalAmount,
                isFullyPaid,
                dentalPayment,
                dentalBalance
        );
    }

    public void addProcedure(Patient patient, int service, String dentalDesc, Date dentalDate,
                             int modeOfPayment, String dentalAmount, boolean isFullyPaid) {
        addProcedure(
                patient,
                service,
                dentalDesc,
                dentalDate,
//                modeOfPayment,
                dentalAmount,
                isFullyPaid,
                dentalAmount,
                "0"
        );
    }

    public LiveData<FormState> getmOperationState() {
        return mOperationState;
    }

    public LiveData<FormState> getmDescription() {
        return mDescription;
    }

    public LiveData<FormState> getmAmount() {
        return mAmount;
    }

    public LiveData<FormState> getmPayment() {
        return mPayment;
    }

    public MutableLiveData<FormState> getmBalance() {
        return mBalance;
    }

    public Double getBalance() {
        return balance;
    }

//    public MutableLiveData<FormState> getmModeOfPayment() {
//        return mModeOfPayment;
//    }

    public MutableLiveData<FormState> getmServices() {
        return mServices;
    }

    public void setDownpayment(boolean downpayment) {
        isDownpayment = downpayment;
    }

    private boolean isInputEmpty(boolean isNull, String input) {
        return isNull || input.isEmpty();
    }

    private boolean isNumberField(String label) {
        return AMOUNT.equals(label) || PAYMENT.equals(label);
    }

    private void setBalance(String label, String input) {
        boolean hasAmount = Checker.isNotNullOrValid(mAmount);
        boolean hasPayment = Checker.isNotNullOrValid(mPayment);

        if (AMOUNT.equals(label) && hasAmount) amount = Double.parseDouble(input);

        if (PAYMENT.equals(label)) {
            if (hasPayment) payment = Double.parseDouble(input);
            else setState(BALANCE, R.string.invalid_input);
        }

        if (hasAmount && hasPayment) computeBalance();
    }

    private void computeBalance() {
        balance = amount - payment;

        if (balance < 0) setState(BALANCE, R.string.invalid_input);
        else {
            setState(BALANCE, VALID);
            mBalanceAmount.setValue(balance);
        }
    }

    @Override
    public void beforeDataChange(String label, int after, String input) {
        // Ignore this
    }

    @Override
    public void dataChanged(String label, String input) {
        boolean isNull = input == null;

        if (isInputEmpty(isNull, input)) {
            setState(label, R.string.invalid_empty_input);
            if (PAYMENT.equals(label)) setBalance(label, input);
        }
        else if (isNumberField(label)) {
            if (UIUtil.convertToDouble(input) == -1)
                setState(label, R.string.invalid_input);

            else if (Checker.isRepeated(input, "."))
                setState(label, R.string.invalid_contains_two_or_more_dots);

            else if (Checker.hasLetter(input))
                setState(label, R.string.invalid_contains_letter);

            else {
                setState(label, VALID);
                setBalance(label, input);
            }
        }
        else setState(label, VALID);

        setButtonState();
    }

    @Override
    public void setSpinnerState(final String label, int pos) {
        if (pos == 0) setState(label, pos);
        else setState(label, VALID);
        setButtonState();
    }

    public void setButtonState() {
        if (!isDownpayment && Checker.isComplete(mDescription, mServices,
//                mModeOfPayment,
                mAmount)) {
            mOperationState.setValue(new FormState(true));
        }
        else if (isDownpayment && Checker.isComplete(mDescription, mServices,
//                mModeOfPayment,
                mAmount, mPayment, mBalance)) {
            mOperationState.setValue(new FormState(true));
        }
        else mOperationState.setValue(new FormState(false));
    }

    private void setState(String label, int msg) {
        FormState field;

        if (msg == VALID) field = new FormState(true);
        else field = new FormState(msg);

        switch (label) {
            case DESCRIPTION:
                mDescription.setValue(field);
                break;
            case AMOUNT:
                mAmount.setValue(field);
                break;
            case PAYMENT:
                mPayment.setValue(field);
                break;
            case BALANCE:
                mBalance.setValue(field);
                break;
//            case MODE_OF_PAYMENT:
//                mModeOfPayment.setValue(field);
//                break;
            case SERVICE:
                mServices.setValue(field);
                break;
        }
    }
}
