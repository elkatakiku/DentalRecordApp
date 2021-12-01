package com.bsit_three_c.dentalrecordapp.ui.add_patient;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.form_state.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.patient.PatientRepository;
import com.bsit_three_c.dentalrecordapp.util.Checker;

public class AddPatientViewModel extends ViewModel {
    private static final String TAG = AddPatientViewModel.class.getSimpleName();

    private final PatientRepository repository;
    private final MutableLiveData<FormState> addPatientFormState = new MutableLiveData<>();
    private final MutableLiveData<FormState> mFirstname = new MutableLiveData<>();
    private final MutableLiveData<FormState> mlastname = new MutableLiveData<>();
    private final MutableLiveData<FormState> mMiddleInitial = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAddress = new MutableLiveData<>();
    private final MutableLiveData<FormState> mOccupation = new MutableLiveData<>();
    private final MutableLiveData<FormState> mCivilStatus = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAge = new MutableLiveData<>();
    private final MutableLiveData<FormState> mPhoneNumber = new MutableLiveData<>();

    private static final String LETTER_FIELD = "LetterField";
    private static final String NUMBER_FIELD = "NumberField";
    private static final String FIRSTNAME = "Firstname";
    private static final String LASTNAME = "Lastname";
    private static final String MIDDLE_INITIAL = "Middle Initial";
    private static final String ADDRESS = "Address";
    private static final String OCCUPATION = "Occupation";
    private static final String CIVIL_STATUS = "Civil Status";
    private static final String AGE = "Age";
    private static final String PHONE_NUMBER = "Telephone";
    private static final int VALID = -1;


    public AddPatientViewModel(PatientRepository repository) {
        this.repository = repository;
    }

    public LiveData<FormState> getAddPatientFormState() {
        return addPatientFormState;
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

    public LiveData<FormState> getmAddress() {
        return mAddress;
    }

    public LiveData<FormState> getmOccupation() {
        return mOccupation;
    }

    public LiveData<FormState> getmCivilStatus() {
        return mCivilStatus;
    }

    public LiveData<FormState> getmAge() {
        return mAge;
    }

    public LiveData<FormState> getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void addPatient(String firstname, String lastname, String middleInitial, String address,
                           String phoneNumber, String civilStatus, int age, String occupation) {

        Patient newPatient = new Patient(firstname, lastname, middleInitial, address, phoneNumber,
                                            civilStatus, age, occupation);
        repository.addPatients(newPatient);
    }

    public void dataChanged(String label, String input) {
        Log.d(TAG, "dataChanged: setting field: " + label);
        Log.d(TAG, "dataChanged: isComplete: " + Checker.isIncomplete(mFirstname, mlastname, mAge, mAddress, mPhoneNumber));
        if (input == null || input.isEmpty()) setState(label, R.string.invalid_empty_input);
        else if (isLetterField(label)) {
            if (Checker.hasNumber(input)) setState(label, R.string.invalid_contains_number);
            else setState(label, VALID);
        }
        else if (ADDRESS.equals(label)) setState(label, VALID);
        else if (!isLetterField(label)) {
            Log.d(TAG, "dataChanged: setting field > isLetterField: " + label);
            if (Checker.hasLetter(input)) {
                Log.d(TAG, "dataChanged: setting field > hasLetter: " + label);
                setState(label, R.string.invalid_contains_letter);
            }
            else setState(label, VALID);
        }

        if (Checker.isIncomplete(mFirstname, mlastname, mAge, mAddress, mPhoneNumber)) addPatientFormState.setValue(new FormState(true));
        else addPatientFormState.setValue(new FormState(false));
    }

    private boolean isLetterField(final String s) {
        boolean result = false;
        switch (s) {
            case FIRSTNAME: case LASTNAME: case MIDDLE_INITIAL: case OCCUPATION: case CIVIL_STATUS:
                result = true;
                break;
            case AGE: case PHONE_NUMBER:
                result = false;
                break;
        }

        return result;
    }

    private void setState(final String label, final Integer msg) {
        FormState field;

        if (msg == -1) {
            field = new FormState(true);
        } else {
            field = new FormState(msg);
        }
        Log.d(TAG, "setState: has error: " + (field.getMsgError() != null));
        Log.d(TAG, "setState: no error: " + field.isDataValid());
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
            case AGE:
                mAge.setValue(field);
                break;
            case ADDRESS:
                mAddress.setValue(field);
                break;
            case PHONE_NUMBER:
                mPhoneNumber.setValue(field);
                break;
            case OCCUPATION:
                mOccupation.setValue(field);
                break;
            case CIVIL_STATUS:
                mCivilStatus.setValue(field);
                break;
        }
    }

}
