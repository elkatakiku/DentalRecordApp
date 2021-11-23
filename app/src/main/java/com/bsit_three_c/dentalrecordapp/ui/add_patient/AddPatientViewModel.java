package com.bsit_three_c.dentalrecordapp.ui.add_patient;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.patient.PatientRepository;

public class AddPatientViewModel extends ViewModel {
    private static final String TAG = AddPatientViewModel.class.getSimpleName();

    private final PatientRepository repository;
    private final MutableLiveData<AddPatientFormState> addPatientFormState = new MutableLiveData<>();
    private final MutableLiveData<AddPatientFormState> mFirstname = new MutableLiveData<>();
    private final MutableLiveData<AddPatientFormState> mlastname = new MutableLiveData<>();
    private final MutableLiveData<AddPatientFormState> mMiddleInitial = new MutableLiveData<>();
    private final MutableLiveData<AddPatientFormState> mAddress = new MutableLiveData<>();
    private final MutableLiveData<AddPatientFormState> mOccupation = new MutableLiveData<>();
    private final MutableLiveData<AddPatientFormState> mCivilStatus = new MutableLiveData<>();
    private final MutableLiveData<AddPatientFormState> mAge = new MutableLiveData<>();
    private final MutableLiveData<AddPatientFormState> mPhoneNumber = new MutableLiveData<>();

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

    public MutableLiveData<AddPatientFormState> getAddPatientFormState() {
        return addPatientFormState;
    }

    public MutableLiveData<AddPatientFormState> getmFirstname() {
        return mFirstname;
    }

    public MutableLiveData<AddPatientFormState> getMlastname() {
        return mlastname;
    }

    public MutableLiveData<AddPatientFormState> getmMiddleInitial() {
        return mMiddleInitial;
    }

    public MutableLiveData<AddPatientFormState> getmAddress() {
        return mAddress;
    }

    public MutableLiveData<AddPatientFormState> getmOccupation() {
        return mOccupation;
    }

    public MutableLiveData<AddPatientFormState> getmCivilStatus() {
        return mCivilStatus;
    }

    public MutableLiveData<AddPatientFormState> getmAge() {
        return mAge;
    }

    public MutableLiveData<AddPatientFormState> getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void addPatient(String firstname, String lastname, String middleInitial, String address,
                           String phoneNumber, String civilStatus, int age, String occupation) {

        Patient newPatient = new Patient(firstname, lastname, middleInitial, address, phoneNumber,
                                            civilStatus, age, occupation);
        repository.addPatients(newPatient);
    }

    public void dataChanged(String label, String input) {
        Log.d(TAG, "dataChanged: Data cnanged");
        Log.d(TAG, "dataChanged: input == null || input.isEmpty(): " + (input == null || input.isEmpty()));
        Log.d(TAG, "dataChanged: isLetterField(label):" + isLetterField(label));
        if (input == null || input.isEmpty()) setState(label, R.string.invalid_empty_input);
        else if (isLetterField(label)) {
            if (hasNumber(input)) setState(label, R.string.invalid_contains_number);
            else setState(label, VALID);
        }
        else if (ADDRESS.equals(label)) setState(label, VALID);
        else if (!isLetterField(label)) {
            if (hasLetter(input)) setState(label, R.string.invalid_contains_letter);
            else setState(label, VALID);
        }
//        if (isLetterFieldValid(input)) addPatientFormState.setValue(new AddPatientFormState(true));
//        else addPatientFormState.setValue(new AddPatientFormState(R.string.invalid_input));
    }

    private boolean hasNumber(String s) {
        boolean result = false;
        Log.d(TAG, "hasNumber: checking letters if contains number");
        for (char c : s.toCharArray()) {
            Log.d(TAG, "hasNumber: c: " + c);
            if (Character.isDigit(c)) result = true;
        }
        return result;
    }

    private boolean hasLetter(String s) {
        boolean result = false;

        for (char c : s.toCharArray())
            if (Character.isLetter(c)) result = true;

        return result;
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
        AddPatientFormState field;

        if (msg == -1) {
            field = new AddPatientFormState(true);
        } else {
            field = new AddPatientFormState(msg);
        }
        Log.d(TAG, "setState: has error: " + (field.getMsgError() != null));
        Log.d(TAG, "setState: no error: " + field.isDataValid());
        switch (label) {
            case FIRSTNAME:
//                Log.d(TAG, "setState: setting state: " + field.isDataValid() + " " + field.getMsgError().toString());
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
