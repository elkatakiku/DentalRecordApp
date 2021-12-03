package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.patient.OperationRepository;
import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;
import com.bsit_three_c.dentalrecordapp.util.Checker;

import java.util.ArrayList;
import java.util.Date;

public class OperationViewModel extends ViewModel implements TextChange {
    private static final String TAG = OperationViewModel.class.getSimpleName();

    private final MutableLiveData<ArrayList<Person>> mPatientList = new MutableLiveData<>();
    private final OperationRepository repository;

    private final MutableLiveData<FormState> mOperationState = new MutableLiveData<>();
    private final MutableLiveData<FormState> mDate = new MutableLiveData<>();
    private final MutableLiveData<FormState> mDescription = new MutableLiveData<>();
    private final MutableLiveData<FormState> mMOP = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAmount = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsDownpayment = new MutableLiveData<>();
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
    private boolean hasDot = false;

    public OperationViewModel(OperationRepository repository) {
        this.repository = repository;
    }

    public OperationRepository getRepository() {
        return repository;
    }

    public void addOperation(Patient patient, String dentalDesc, Date dentalDate, String modeOfPayment, String dentalAmount,
                             boolean isFullyPaid, String dentalTotalAmount, String dentalBalance) {
        repository.addOperation(patient, dentalDesc, dentalDate, modeOfPayment, dentalAmount, isFullyPaid, dentalTotalAmount, dentalBalance);
    }

    public void addOperation(Patient patient, String dentalDesc, Date dentalDate, String modeOfPayment, String dentalAmount, boolean isFullyPaid) {
        addOperation(patient, dentalDesc, dentalDate, modeOfPayment, dentalAmount, isFullyPaid, dentalAmount, "0");
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

    public LiveData<Boolean> getmIsDownpayment() {
        return mIsDownpayment;
    }

    public void setmIsDownpayment(boolean bool) {
        mIsDownpayment.setValue(bool);
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

    @Override
    public void beforeDataChange(String label, int after, String input) {
//        Log.d(TAG, "beforeDataChange: input: " + input);
//        if (input.contains(".")) {
//            Log.d(TAG, "beforeDataChange: contains dot");
//            setState(label, R.string.invalid_contains_two_or_more_dots);
//            Log.d(TAG, "beforeDataChange: mAmount value: " + mAmount.getValue());
//        }
//        else {
//            hasDot = true;
//            Log.d(TAG, "beforeDataChange: not contains dot");
//            setState(label, VALID);
//        }

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



//                if (TOTAL_AMOUNT.equals(label) && mAmount.getValue().isDataValid() && mTotalAmount.getValue().isDataValid())

//                }
            }
        }
        else setState(label, VALID);

//        else if (AMOUNT.equals(label) && Checker.isRepeated(input, ".")) setState(label, R.string.invalid_contains_two_or_more_dots);
//        else if (AMOUNT.equals(label) && Checker.hasLetter(input)) setState(label, R.string.invalid_contains_letter);

//        Log.d(TAG, "dataChanged: set state of " + label);

//        if (DESCRIPTION.equals(label) && mDescription.getValue() != null) {
//            Log.d(TAG, "dataChanged: state of description changed");
//            Log.d(TAG, "dataChanged: mDesc: " + mDescription.getValue());
//        }
//        else if (AMOUNT.equals(label) && mAmount.getValue() != null) {
//            Log.d(TAG, "dataChanged: state of amount changed");
//            Log.d(TAG, "dataChanged: mAmount: " + mAmount.getValue());
//        }

        if (Checker.isComplete(mDescription, mAmount)) {
//            Log.d(TAG, "dataChanged: iscomplete");
            mOperationState.setValue(new FormState(true));
//            Log.d(TAG, "dataChanged: operation state: " + mOperationState.getValue());
        }
        else {
//            Log.d(TAG, "dataChanged: is incomplete");
            mOperationState.setValue(new FormState(false));
//            Log.d(TAG, "dataChanged: operation state: " + mOperationState.getValue());
        }
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

//    public Double getBalanceAmount(String totalAmount, String downpayment) {
//        return Double.parseDouble(totalAmount) - Double.parseDouble(downpayment);
//    }
}
