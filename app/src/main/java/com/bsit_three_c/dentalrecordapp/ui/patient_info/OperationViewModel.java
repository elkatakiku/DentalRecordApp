package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.patient.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;
import com.bsit_three_c.dentalrecordapp.util.Checker;

import java.util.Date;

public class OperationViewModel extends ViewModel implements TextChange {
    private static final String TAG = OperationViewModel.class.getSimpleName();

    private final ProcedureRepository repository;

    private final MutableLiveData<FormState> mOperationState = new MutableLiveData<>();
    private final MutableLiveData<FormState> mDescription = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAmount = new MutableLiveData<>();
    private final MutableLiveData<FormState> mTotalAmount = new MutableLiveData<>();
    private final MutableLiveData<FormState> mBalance = new MutableLiveData<>();
    private final MutableLiveData<Double> mBalanceAmount = new MutableLiveData<>();

    private double amount;
    private double totalAmount;
    private Double balance;

    private static final String DESCRIPTION = "Description";
    private static final String AMOUNT = "Amount";
    private static final String TOTAL_AMOUNT = "Total Amount";
    private static final String BALANCE = "Balance";
    private static final int VALID = -1;
    private boolean isDownpayment = false;

    public OperationViewModel(ProcedureRepository repository) {
        this.repository = repository;
    }

    public void addProcedure(Patient patient, String dentalDesc, Date dentalDate, String modeOfPayment,
                             String dentalAmount, boolean isFullyPaid, String dentalTotalAmount, String dentalBalance) {
        repository.addProcedure(
                patient,
                dentalDesc,
                dentalDate,
                modeOfPayment,
                dentalAmount,
                isFullyPaid,
                dentalTotalAmount,
                dentalBalance
        );
    }

    public void addProcedure(Patient patient, String dentalDesc, Date dentalDate, String modeOfPayment,
                             String dentalPaidAmount, boolean isFullyPaid) {
        addProcedure(
                patient,
                dentalDesc,
                dentalDate,
                modeOfPayment,
                dentalPaidAmount,
                isFullyPaid,
                dentalPaidAmount,
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

    public LiveData<FormState> getmTotalAmount() {
        return mTotalAmount;
    }

    public LiveData<Double> getmBalanceAmount() {
        return mBalanceAmount;
    }

    public MutableLiveData<FormState> getmBalance() {
        return mBalance;
    }

    public Double getBalance() {
        return balance;
    }

    public boolean isDownpayment() {
        return isDownpayment;
    }

    public void setDownpayment(boolean downpayment) {
        isDownpayment = downpayment;
    }

    @Override
    public void beforeDataChange(String label, int after, String input) {
        // Ignore this
    }

    @Override
    public void dataChanged(String label, String input) {
        Log.d(TAG, "dataChanged: called");
        boolean isNull = input == null;

        if (isNull || input.isEmpty()) setState(label, R.string.invalid_empty_input);
        else if (AMOUNT.equals(label) || TOTAL_AMOUNT.equals(label)) {
            if (Checker.isRepeated(input, ".")) setState(label, R.string.invalid_contains_two_or_more_dots);
            else if (Checker.hasLetter(input)) setState(label, R.string.invalid_contains_letter);
            else {
                setState(label, VALID);

                boolean hasAmount = Checker.isNotNullOrValid(mAmount);
                boolean hasTotalAmount = Checker.isNotNullOrValid(mTotalAmount);

                if (AMOUNT.equals(label) && hasAmount) {
                    Log.d(TAG, "dataChanged: has amount");
                    amount = Double.parseDouble(input);
                    Log.d(TAG, "dataChanged: amount value: " + amount);
                }
                if (TOTAL_AMOUNT.equals(label)) {
                    Log.d(TAG, "dataChanged: has total amount");
                    if (hasTotalAmount) {
                        totalAmount = Double.parseDouble(input);
                        Log.d(TAG, "dataChanged: total amount value: " + totalAmount);
                    }

                    if (hasAmount && hasTotalAmount) {
                        Log.d(TAG, "dataChanged: has amount and total amount");
                        Log.d(TAG, "dataChanged: amount: " + amount + "\ntotal amount: " + totalAmount);
                        balance = totalAmount - amount;
                        Log.d(TAG, "dataChanged: bal value: " + balance);

                        if (balance < 0) {
                            Log.d(TAG, "dataChanged: bal is less than 0");
                            setState(BALANCE, R.string.invalid_input);
                            Log.d(TAG, "dataChanged: balance state: " + mBalance.getValue());
                        }
                        else {
                            Log.d(TAG, "dataChanged: bal is greater than 0");
                            setState(BALANCE, VALID);
                            mBalanceAmount.setValue(balance);
                            Log.d(TAG, "dataChanged: " + mBalanceAmount.getValue());
                        }
                    }
                }
            }
        }
        else setState(label, VALID);

        setButtonState();
    }

    public void setButtonState() {
        if (!isDownpayment && Checker.isComplete(mDescription, mAmount)) {
            mOperationState.setValue(new FormState(true));
        }
        else if (isDownpayment && Checker.isComplete(mDescription, mAmount, mTotalAmount, mBalance)) {
            mOperationState.setValue(new FormState(true));
        }
        else mOperationState.setValue(new FormState(false));
    }

    private void setState(String label, int msg) {
        FormState field;

        if (msg == -1) field = new FormState(true);
        else field = new FormState(msg);

        switch (label) {
            case DESCRIPTION:
                mDescription.setValue(field);
                break;
            case AMOUNT:
                mAmount.setValue(field);
                break;
            case TOTAL_AMOUNT:
                mTotalAmount.setValue(field);
                break;
            case BALANCE:
                mBalance.setValue(field);
        }
    }
}
