package com.bsit_three_c.dentalrecordapp.ui.admin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.AdminRepository;
import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;
import com.bsit_three_c.dentalrecordapp.util.Checker;

import java.util.Date;
import java.util.List;

public class AdminFormViewModel extends ViewModel implements TextChange {
    private static final String TAG = AdminFormViewModel.class.getSimpleName();
    // TODO: Implement the ViewModel

    private final AdminRepository adminRepository;

    private final MutableLiveData<String> mUid;
    private final MutableLiveData<Person> mAdmin;

    private final MutableLiveData<Integer> mError;
    private final MutableLiveData<FormState> mFirstname;
    private final MutableLiveData<FormState> mlastname;
    private final MutableLiveData<FormState> mMiddleInitial;
    private final MutableLiveData<FormState> mSuffix;
    private final MutableLiveData<FormState> mContact;
    private final MutableLiveData<FormState> mEmail;

    private static final String FIRSTNAME = "Firstname";
    private static final String LASTNAME = "Lastname";
    private static final String MIDDLE_INITIAL = "MI";
    private static final String SUFFIX = "Suffix";
    private static final String CONTACT = "Contact Number";
    private static final String EMAIL = "Email Address";

    private final AdminRepository.AdminListener adminListener;

    public AdminFormViewModel() {
        this.adminRepository = AdminRepository.getInstance();

        this.mUid = new MutableLiveData<>();
        this.mAdmin = new MutableLiveData<>();

        this.mError = new MutableLiveData<>();
        this.mFirstname = new MutableLiveData<>();
        this.mlastname = new MutableLiveData<>();
        this.mMiddleInitial = new MutableLiveData<>();
        this.mSuffix = new MutableLiveData<>();
        this.mContact = new MutableLiveData<>();
        this.mEmail = new MutableLiveData<>();

        this.adminListener = new AdminRepository.AdminListener(mAdmin);
    }

    public void setmUid(String uid) {
        mUid.setValue(uid);
    }

    public LiveData<String> getmUid() {
        return mUid;
    }

    public void getAdmin() {
        adminRepository.getDatabaseReference().addListenerForSingleValueEvent(adminListener);
    }

    public LiveData<Person> getmAdmin() {
        return mAdmin;
    }

    public void addAdmin(
            String firstname,
            String lastname,
            String middleInitial,
            String suffix,
            String dateOfBirth,
            String address,
            List<String> contact,
            int civilStatus,
            int age,
            String email,
            LoggedInUser loggedInUser) {

        Person admin = new Person(
                adminRepository.getNewUid(),
                firstname,
                lastname,
                middleInitial,
                suffix,
                dateOfBirth,
                contact,
                address,
                civilStatus,
                age,
                new Date(),
                email
        );

        admin.setAccountUid(loggedInUser.getUserId());

        adminRepository.getDatabaseReference().setValue(admin).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                mError.setValue(R.string.an_error_occurred);
                return;
            }

            mError.setValue(Checker.VALID);
        });
    }

    public LiveData<Integer> getmError() {
        return mError;
    }

    @Override
    public void beforeDataChange(String input, int after, String s) {

    }

    @Override
    public void dataChanged(String label, String input) {
        Log.d(TAG, "dataChanged: label: " + label);
        Log.d(TAG, "dataChanged: data changed");
        if (Checker.isDataAvailable(input)) {

            if (SUFFIX.equals(label)) {
                if (Checker.hasNumber(input)) {
                    Log.d(TAG, "dataChanged: has number");
                    setState(label, R.string.invalid_contains_number);
                }
                else {
                    Log.d(TAG, "dataChanged: data is valid");
                    setState(label, Checker.VALID);
                }
            }

            else if (EMAIL.equals(label)) {
                Log.d(TAG, "dataChanged: label is email");
                if (Checker.isEmailValid(input)) {
                    setState(label, Checker.VALID);
                } else {
                    setState(label, R.string.invalid_email);
                }
                Log.d(TAG, "dataChanged: email state: " + mEmail.getValue());
            }

            else if (Checker.containsSpecialCharacter(input)) {
                setState(label, R.string.invalid_contains_special_character);
            }

            else if (isLetterField(label)) {
                if (Checker.hasNumber(input)) {
                    Log.d(TAG, "dataChanged: has number");
                    setState(label, R.string.invalid_contains_number);
                }
                else if (MIDDLE_INITIAL.equals(label) && (input.length() > 1)) {
                    Log.d(TAG, "dataChanged: middle error");
                    setState(label, R.string.invalid_contains_more_than_one_character);
                }
                else {
                    Log.d(TAG, "dataChanged: data is valid");
                    setState(label, Checker.VALID);
                }
            } else if (!isLetterField(label)) {
                if (Checker.hasLetter(input)) {
                    setState(label, R.string.invalid_contains_letter);
                } else {
                    setState(label, Checker.VALID);
                }
            }
        }
    }


    private boolean isLetterField(final String s) {
        boolean result = false;
        switch (s) {
            case FIRSTNAME: case LASTNAME: case MIDDLE_INITIAL: case SUFFIX:
                result = true;
                break;
            case CONTACT:
                result = false;
                break;
        }

        return result;
    }


    private void setState(final String label, final Integer msg) {
        FormState field;

        if (msg == null) field = null;
        else if (msg == -1) field = new FormState(true);
        else field = new FormState(msg);

        switch (label) {
            case FIRSTNAME:
                mFirstname.setValue(field);
                break;
            case LASTNAME:
                mlastname.setValue(field);
                break;
            case MIDDLE_INITIAL:
                mMiddleInitial.setValue(field);
                break;
            case SUFFIX:
                mSuffix.setValue(field);
                break;
            case CONTACT:
                mContact.setValue(field);
                break;
            case EMAIL:
                mEmail.setValue(field);
                break;
        }
    }

    public LiveData<FormState> getmFirstname() {
        return mFirstname;
    }

    public LiveData<FormState> getMlastname() {
        return mlastname;
    }

    public LiveData<FormState> getmMiddleInitial() {
        return mMiddleInitial;
    }

    public LiveData<FormState> getmSuffix() {
        return mSuffix;
    }

    public LiveData<FormState> getmContact() {
        return mContact;
    }

    public LiveData<FormState> getmEmail() {
        return mEmail;
    }
}