package com.bsit_three_c.dentalrecordapp.ui.patients.patient_form;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.BaseRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.interfaces.SpinnerState;
import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientFormViewModel extends ViewModel implements TextChange, SpinnerState {
    private final PatientRepository patientRepository;
    private final AccountRepository accountRepository;

    private final MutableLiveData<FormState> mFirstname = new MutableLiveData<>();
    private final MutableLiveData<FormState> mlastname = new MutableLiveData<>();
    private final MutableLiveData<FormState> mMiddleInitial = new MutableLiveData<>();
    private final MutableLiveData<FormState> mSuffix = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAddress = new MutableLiveData<>();
    private final MutableLiveData<FormState> mOccupation = new MutableLiveData<>();
    private final MutableLiveData<FormState> mCivilStatus = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAge = new MutableLiveData<>();
    private final MutableLiveData<FormState> mContact = new MutableLiveData<>();
    private final MutableLiveData<FormState> mEmail = new MutableLiveData<>();

    private final MutableLiveData<Account> mAccount = new MutableLiveData<>();
    private final MutableLiveData<Integer> mError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mCreateAttempt = new MutableLiveData<>();
    private final MutableLiveData<Patient> mPatient = new MutableLiveData<>();

    private static final String FIRSTNAME = "Firstname";
    private static final String LASTNAME = "Lastname";
    private static final String MIDDLE_INITIAL = "MI";
    private static final String SUFFIX = "Suffix";
    private static final String ADDRESS = "Address";
    private static final String OCCUPATION = "Occupation";
    private static final String CIVIL_STATUS = "Civil Status";
    private static final String AGE = "Age";
    private static final String CONTACT = "Contact";
    private static final String EMAIL = "Email";

    public PatientFormViewModel() {
        this.patientRepository = PatientRepository.getInstance();
        this.accountRepository = AccountRepository.getInstance();
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

    public LiveData<FormState> getmAge() {
        return mAge;
    }

    public LiveData<FormState> getmContact() {
        return mContact;
    }

    public LiveData<FormState> getmEmail() {
        return mEmail;
    }

    public void setmError(Integer integer) {
        mError.setValue(integer);
    }

    public LiveData<Integer> getmError() {
        return mError;
    }

    public void setmCreateAttempt(boolean bool) {
        mCreateAttempt.setValue(bool);
    }

    public LiveData<Boolean> getmCreateAttempt() {
        return mCreateAttempt;
    }

    public void setmPatient(Patient patient) {
        mPatient.setValue(patient);
    }

    public LiveData<Patient> getmPatient() {
        return mPatient;
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
        if (Checker.isDataAvailable(input)){
            if (ADDRESS.equals(label)) {
                setState(label, Checker.VALID);
            }

            else if (SUFFIX.equals(label)) {
                if (Checker.hasNumber(input)) {
                    setState(label, R.string.invalid_contains_number);
                }
                else {
                    setState(label, Checker.VALID);
                }
            }

            else if (EMAIL.equals(label)) {
                if (Checker.isEmailValid(input)) {
                    setState(label, Checker.VALID);
                } else {
                    setState(label, R.string.invalid_email);
                }
            }

            else if (Checker.containsSpecialCharacter(input)) {
                setState(label, R.string.invalid_contains_special_character);
            }

            else if (isLetterField(label)) {
                if (Checker.hasNumber(input)) {
                    setState(label, R.string.invalid_contains_number);
                }
                else if (MIDDLE_INITIAL.equals(label) && (input.length() > 1)) {
                    setState(label, R.string.invalid_contains_more_than_one_character);
                }
                else {
                    setState(label, Checker.VALID);
                }
            }

            else if (!isLetterField(label)) {
                if (Checker.hasLetter(input)) setState(label, R.string.invalid_contains_letter);
                else if (AGE.equals(label) && (UIUtil.convertToInteger(input) < 0 || UIUtil.convertToInteger(input) > 150))
                    setState(label, R.string.invalid_too_old);
                else setState(label, Checker.VALID);

            }
        }
    }

    @Override
    public void beforeDataChange(String label, int after, String s) {
        if (MIDDLE_INITIAL.equals(label) && after > 1) setState(label, R.string.invalid_contains_more_than_one_character);
    }

    @Override
    public void setSpinnerState(String label, int pos) {
        if (pos == 0) setState(label, pos);
        else setState(label, Checker.VALID);
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
            case EMAIL:
                mEmail.setValue(field);
                break;
        }
    }

    public void createAccount(String email, String password) {
        mAccount.setValue(new Account(
                email,
                password,
                Account.TYPE_PATIENT,
                patientRepository.getNewUid()
        ));
    }

    public LiveData<Account> getmAccount() {
        return mAccount;
    }

    public Patient convertToPatient(Person person) {
        return new Patient(
                person.getUid(),
                person.getFirstname(),
                person.getLastname(),
                person.getMiddleInitial(),
                person.getSuffix(),
                person.getPhoneNumber()
        );
    }

    public Patient createPatient(String firstname,
                                 String lastname,
                                 String middleInitial,
                                 String suffix,
                                 String dateOfBirth,
                                 String address,
                                 List<String> contact,
                                 int civilStatus,
                                 int age,
                                 String occupation) {

        return new Patient(
                patientRepository.getNewUid(),
                firstname,
                lastname,
                middleInitial,
                suffix,
                dateOfBirth,
                contact,
                address,
                civilStatus,
                age,
                occupation,
                DateUtil.convertToDate(DateUtil.getDate(new Date())),
                new ArrayList<>(),
                "email"
        );
    }

    public void addPatient(String firstname,
                           String lastname,
                           String middleInitial,
                           String suffix,
                           String dateOfBirth,
                           String address,
                           List<String> contact,
                           int civilStatus,
                           int age,
                           String occupation) {

        Patient patient = new Patient(
                patientRepository.getNewUid(),
                firstname,
                lastname,
                middleInitial,
                suffix,
                dateOfBirth,
                contact,
                address,
                civilStatus,
                age,
                occupation,
                DateUtil.convertToDate(DateUtil.getDate(new Date())),
                new ArrayList<>(),
                "email"
        );

        Account account = mAccount.getValue();

        if (account != null) {
                accountRepository.createNewAccount(account.getEmail(), account.getPassword()).addOnCompleteListener(createAccountTask -> {
                    if (!createAccountTask.isSuccessful()) {
                        mError.setValue(accountRepository.getCreateAccountError(createAccountTask));
                        mCreateAttempt.setValue(true);
                        return;
                    }

                    FirebaseUser user = createAccountTask.getResult().getUser();
                    if (user != null) {
                        account.setUid(user.getUid());
                        accountRepository.setUserIds(patient, account);
                        accountRepository
                                .uploadAccount(account, user.getUid())
                                .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                if (task.getException() != null) {
                                    task.getException().printStackTrace();
                                }
                                mError.setValue(R.string.an_error_occurred);
                                mCreateAttempt.setValue(true);
                                return;
                            }

                            patient.setEmail(account.getEmail());
                            patient.setAccountUid(account.getUid());
                            patientRepository.upload(patient)
                                    .addOnCompleteListener(uploadPatient -> {
                                        accountRepository.logout();
                                        mError.setValue(Checker.VALID);
                                        mCreateAttempt.setValue(true);
                                        mPatient.setValue(patient);
                            });
                        });
                    } else {
                        mError.setValue(R.string.an_error_occurred);
                        mCreateAttempt.setValue(true);
                    }
                });
        }
        else {
            patientRepository
                    .upload(patient)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            mError.setValue(R.string.an_error_occurred);
                            mCreateAttempt.setValue(true);
                            return;
                        }
                        mError.setValue(Checker.VALID);
                        mCreateAttempt.setValue(true);
                        mPatient.setValue(patient);
                    });
        }
    }

    public void updatePatient(Patient patient,
                              String firstname,
                              String lastname,
                              String middleInitial,
                              String suffix,
                              String dateOfBirth,
                              String address,
                              List<String> contact,
                              int civilStatus,
                              int age,
                              String occupation) {

        patient.setFirstname(UIUtil.capitalize(firstname.trim()));
        patient.setLastname(UIUtil.capitalize(lastname.trim()));
        patient.setMiddleInitial(UIUtil.capitalize(middleInitial.trim()));
        patient.setSuffix(UIUtil.capitalize(suffix.trim()));
        patient.setDateOfBirth(DateUtil.getDate(dateOfBirth));
        patient.setAddress(UIUtil.capitalize(address.trim()));
        patient.setPhoneNumber(contact);
        if (contact.size() <= 0) contact.add(BaseRepository.NEW_PATIENT);
        patient.setCivilStatus(civilStatus);
        patient.setAge(age);
        patient.setOccupation(occupation);
        patient.setLastUpdated(new Date());

        patientRepository
                .upload(patient)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        mError.setValue(R.string.an_error_occurred);
                        mCreateAttempt.setValue(true);
                        return;
                    }
                    mError.setValue(Checker.VALID);
                    mCreateAttempt.setValue(true);
                    mPatient.setValue(patient);
                });
    }
}
