package com.bsit_three_c.dentalrecordapp.ui.employees.employee_form;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.EmergencyContact;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;
import com.bsit_three_c.dentalrecordapp.util.Checker;

import java.util.List;

public class EmployeeSharedViewModel extends ViewModel implements TextChange {
    private static final String TAG = EmployeeSharedViewModel.class.getSimpleName();

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    private final MutableLiveData<String> mStringUri = new MutableLiveData<>();
    private final MutableLiveData<byte[]> mImageByte = new MutableLiveData<>();
    private final MutableLiveData<Employee> mEmployee = new MutableLiveData<>();
    private final MutableLiveData<Employee> mUpdatedEmployee;
    private final MutableLiveData<Account> mAccount = new MutableLiveData<>();
    private final MutableLiveData<EmergencyContact> mEmergencyContact = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mEdit = new MutableLiveData<>();

    private final MutableLiveData<FormState> mEmail = new MutableLiveData<>();
    private final MutableLiveData<FormState> mFirstname = new MutableLiveData<>();
    private final MutableLiveData<FormState> mlastname = new MutableLiveData<>();
    private final MutableLiveData<FormState> mMiddleInitial = new MutableLiveData<>();
    private final MutableLiveData<FormState> mSuffix = new MutableLiveData<>();
    private final MutableLiveData<FormState> mContact = new MutableLiveData<>();

    private static final String FIRSTNAME = "Firstname";
    private static final String LASTNAME = "Lastname";
    private static final String MIDDLE_INITIAL = "MI";
    private static final String SUFFIX = "Suffix";
    private static final String CONTACT = "Contact Number";
    private static final String EMAIL = "Email Address";

    private final EmployeeRepository.EmployeeListener employeeListener;
    private final AccountRepository.AccountListener accountListener;
    private final EmployeeRepository.EmergencyContactListener emergencyContactListener;

    public EmployeeSharedViewModel() {
        this.employeeRepository = EmployeeRepository.getInstance();
        this.accountRepository = AccountRepository.getInstance();

        this.mUpdatedEmployee = new MutableLiveData<>();

        this.employeeListener = new EmployeeRepository.EmployeeListener(mEmployee);
        this.accountListener = new AccountRepository.AccountListener(mAccount);
        this.emergencyContactListener = new EmployeeRepository.EmergencyContactListener(mEmergencyContact);
    }

    public void loadEmployee(String employeeUid) {
        Log.d(TAG, "loadEmployee: load employee");
        employeeRepository
                .getPath(employeeUid)
                .addListenerForSingleValueEvent(employeeListener);
    }

    public void loadContact(Employee employee) {
        Log.d(TAG, "loadContact: loading contact");
        employeeRepository
                .getEmergencyContactPath(employee.getEmergencyContactUid())
                .addListenerForSingleValueEvent(emergencyContactListener);
    }

    public void loadAccount(Employee employee) {
        Log.d(TAG, "loadAccount: getting account");
        accountRepository
                .getPath(employee.getAccountUid())
                .addListenerForSingleValueEvent(accountListener);
    }

    public Employee updateEmployee(
            Employee employee,
            String firstname,
            String lastname,
            String middleInitial,
            String suffix,
            int jobTitle,
            String dateOfBirth,
            int age,
            List<String> phoneNumber,
            String email,
            String address1stPart,
            String address2ndPart,
            int civilStatus) {

        employee.setFirstname(firstname);
        employee.setLastname(lastname);
        employee.setMiddleInitial(middleInitial);
        employee.setSuffix(suffix);
        employee.setJobTitle(jobTitle);
        employee.setDateOfBirth(dateOfBirth);
        employee.setAge(age);
        employee.setPhoneNumber(phoneNumber);
        employee.setEmail(email);
        employee.setAddress(address1stPart);
        employee.setAddress2ndPart(address2ndPart);
        employee.setCivilStatus(civilStatus);

        mEmployee.setValue(employee);

        return employee;
    }

    public Employee createNewEmployee(String firstname,
                                      String lastname,
                                      String middleInitial,
                                      String suffix,
                                      int jobTitle,
                                      String dateOfBirth,
                                      int age,
                                      List<String> contactNumbers,
                                      String email,
                                      String address1stPart,
                                      String address2ndPart,
                                      int civilStatus) {

        return new Employee(
                employeeRepository.getNewUid(),
                firstname,
                lastname,
                middleInitial,
                suffix,
                jobTitle,
                dateOfBirth,
                age,
                contactNumbers,
                email,
                address1stPart,
                address2ndPart,
                civilStatus
        );
    }

    public void setmImageByte(byte[] imageByte) {
        mImageByte.setValue(imageByte);
    }

    public LiveData<byte[]> getmImageByte() {
        return mImageByte;
    }

    public LiveData<Employee> getmEmployee() {
        return mEmployee;
    }

    public void setmUpdatedEmployee(Employee employee) {
        mUpdatedEmployee.setValue(employee);
    }

    public LiveData<Employee> getmUpdatedEmployee() {
        return mUpdatedEmployee;
    }

    public void setmAccount(Account account) {
        Log.d(TAG, "setmAccount: setting account");
        mAccount.setValue(account);
    }

    public LiveData<Account> getmAccount() {
        return mAccount;
    }

    public void setmEmergencyContact(EmergencyContact emergencyContact) {
        Log.d(TAG, "setmEmergencyContact: setting contact");
        mEmergencyContact.setValue(emergencyContact);
    }

    public LiveData<EmergencyContact> getmEmergencyContact() {
        return mEmergencyContact;
    }

    public void setmStringUri(String uri) {
        mStringUri.setValue(uri);
    }

    public LiveData<String> getmStringUri() {
        return mStringUri;
    }

    public void setmEdit(boolean isEdit) {
        mEdit.setValue(isEdit);
    }

    public LiveData<Boolean> getmEdit() {
        return mEdit;
    }

    public void removeListeners(Employee employee) {
        employeeRepository.getPath(employee.getUid()).removeEventListener(employeeListener);
        employeeRepository.getEmergencyContactPath(employee.getEmergencyContactUid()).removeEventListener(emergencyContactListener);
        accountRepository.getPath(employee.getAccountUid()).removeEventListener(accountListener);
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

    public LiveData<FormState> getmEmail() {
        return mEmail;
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
}
