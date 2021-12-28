package com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_form;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.FirebaseHelper;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.interfaces.SpinnerState;
import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.Date;
import java.util.List;

public class AddPatientViewModel extends ViewModel implements TextChange, SpinnerState {
    private static final String TAG = AddPatientViewModel.class.getSimpleName();

    private final PatientRepository repository;
    private final MutableLiveData<FormState> addPatientFormState = new MutableLiveData<>();
    private final MutableLiveData<FormState> mFirstname = new MutableLiveData<>();
    private final MutableLiveData<FormState> mlastname = new MutableLiveData<>();
    private final MutableLiveData<FormState> mMiddleInitial = new MutableLiveData<>();
    private final MutableLiveData<FormState> mSuffix = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAddress = new MutableLiveData<>();
    private final MutableLiveData<FormState> mOccupation = new MutableLiveData<>();
    private final MutableLiveData<FormState> mCivilStatus = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAge = new MutableLiveData<>();
    private final MutableLiveData<FormState> mContact = new MutableLiveData<>();

    private static final String FIRSTNAME = "Firstname";
    private static final String LASTNAME = "Lastname";
    private static final String MIDDLE_INITIAL = "MI";
    private static final String SUFFIX = "Suffix";
    private static final String ADDRESS = "Address";
    private static final String OCCUPATION = "Occupation";
    private static final String CIVIL_STATUS = "Civil Status";
    private static final String AGE = "Age";
    private static final String CONTACT = "Contact";
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

    public LiveData<FormState> getmSuffix() {
        return mSuffix;
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

    public LiveData<FormState> getmContact() {
        return mContact;
    }

    public void setNumberState(String label, String input) {
        if (input.length() > 8) setState(label, R.string.invalid_tel_limit_number);
        else setState(label, VALID);
    }

    public Patient addPatient(String firstname,
                              String lastname,
                              String middleInitial,
                              String suffix,
                              String address,
                              List<String> contact,
                              int civilStatus,
                              int age,
                              String occupation) {

        return repository.add(
                UIUtil.capitalize(firstname.trim()),
                UIUtil.capitalize(lastname.trim()),
                UIUtil.capitalize(middleInitial.trim()),
                UIUtil.capitalize(suffix.trim()),
                address.trim(),
                contact,
                civilStatus,
                age,
                occupation.trim());
    }

    public Patient updatePatient(Patient patient,
                              String firstname,
                              String lastname,
                              String middleInitial,
                              String suffix,
                              String address,
                              List<String> contact,
                              int civilStatus,
                              int age,
                              String occupation) {

        patient.setFirstname(UIUtil.capitalize(firstname.trim()));
        patient.setLastname(UIUtil.capitalize(lastname.trim()));
        Log.d(TAG, "updatePatient: new MI: " + middleInitial);
        patient.setMiddleInitial(UIUtil.capitalize(middleInitial.trim()));
        patient.setSuffix(UIUtil.capitalize(suffix.trim()));
        patient.setAddress(UIUtil.capitalize(address.trim()));
        patient.setPhoneNumber(contact);
        if (contact.size() <= 0) contact.add(FirebaseHelper.NEW_PATIENT);
        patient.setCivilStatus(civilStatus);
        patient.setAge(age);
        patient.setOccupation(occupation);
        patient.setLastUpdated(new Date());

        repository.update(patient);
        return patient;
    }

    private boolean isLetterField(final String s) {
        boolean result = false;
        switch (s) {
            case FIRSTNAME: case LASTNAME: case MIDDLE_INITIAL: case OCCUPATION: case CIVIL_STATUS:
            case SUFFIX:
                result = true;
                break;
            case AGE: case CONTACT:
                result = false;
                break;
        }

        return result;
    }

//    private void setButtonState() {
//        if (Checker.isComplete(mFirstname, mlastname))
//            addPatientFormState.setValue(new FormState(true));
//        else addPatientFormState.setValue(new FormState(false));
//    }

    public boolean isStateValid() {
        return !(
                !(mMiddleInitial.getValue() == null || Checker.isNotNullAndValid(mMiddleInitial)) ||
                !(mSuffix.getValue() == null || Checker.isNotNullAndValid(mSuffix)) ||
                !(mAge.getValue() == null || Checker.isNotNullAndValid(mAge)) ||
                !(mOccupation.getValue() == null || Checker.isNotNullAndValid(mOccupation))
        );
    }

    @Override
    public void dataChanged(String label, String input) {
        boolean isInputNull = (input == null);

//        if ((!AGE.equals(label) && !OCCUPATION.equals(label)) && (isInputNull || input.isEmpty()))
        if (isInputNull || input.isEmpty()) {
            Log.d(TAG, "dataChanged: empty or null");
            setState(label, null);
        }
        else {
            if (ADDRESS.equals(label)) setState(label, VALID);

            else if (SUFFIX.equals(label)) {
                if (Checker.hasNumber(input)) {
                    Log.d(TAG, "dataChanged: has number");
                    setState(label, R.string.invalid_contains_number);
                }
                else {
                    Log.d(TAG, "dataChanged: data is valid");
                    setState(label, VALID);
                }
            }

            else if (Checker.containsSpecialCharacter(input))
                setState(label, R.string.invalid_contains_special_character);

            else if (isLetterField(label)) {
                if (Checker.hasNumber(input)) {
                    Log.d(TAG, "dataChanged: hasnumber");
                    setState(label, R.string.invalid_contains_number);
                }
                else if (MIDDLE_INITIAL.equals(label) && (input.length() > 1)) {
                    Log.d(TAG, "dataChanged: middle error");
                    setState(label, R.string.invalid_contains_more_than_one_character);
                }
                else {
                    Log.d(TAG, "dataChanged: data is valid");
                    setState(label, VALID);
                }
            }

            else if (!isLetterField(label)) {
                if (Checker.hasLetter(input)) setState(label, R.string.invalid_contains_letter);
                else if (AGE.equals(label) && (UIUtil.convertToInteger(input) < 0 || UIUtil.convertToInteger(input) > 150))
                    setState(label, R.string.invalid_too_old);
                else setState(label, VALID);

            }
        }
//        setButtonState();
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
            case AGE:
                mAge.setValue(field);
                break;
            case ADDRESS:
                mAddress.setValue(field);
                break;
            case CONTACT:
                mContact.setValue(field);
                break;
            case OCCUPATION:
                mOccupation.setValue(field);
                break;
            case CIVIL_STATUS:
                mCivilStatus.setValue(field);
                break;
        }
    }


    @Override
    public void beforeDataChange(String label, int after, String s) {
        if (MIDDLE_INITIAL.equals(label) && after > 1) setState(label, R.string.invalid_contains_more_than_one_character);
    }

    @Override
    public void setSpinnerState(String label, int pos) {
        Log.d(TAG, "setSpinnerState: spinner state change");
        if (pos == 0) setState(label, pos);
        else setState(label, VALID);
//        setButtonState();
    }



}
