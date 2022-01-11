package com.bsit_three_c.dentalrecordapp.ui.employees.employee_form;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.EmergencyContact;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class EmergencyContactFormViewModel extends ViewModel {
    private static final String TAG = EmergencyContactFormViewModel.class.getSimpleName();

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    private final MutableLiveData<Boolean> AddingEmployeeAttempt = new MutableLiveData<>();
    private final MutableLiveData<Integer> mError = new MutableLiveData<>();

    public EmergencyContactFormViewModel(EmployeeRepository employeeRepository, AccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }

    public void addEmployee(
            Bundle arguments,
            String firstName,
            String lastname,
            String middleInitial,
            String suffix,
            String address1,
            String address2,
            String contactNumber) {

        createEmergencyContact(
//                arguments,
                firstName,
                lastname,
                middleInitial,
                suffix,
                address1,
                address2,
                contactNumber);

        LoggedInUser loggedInAccount = arguments.getParcelable(Account.LOGGED_ID);
        Employee employee = arguments.getParcelable(LocalStorage.EMPLOYEE_KEY);
        EmergencyContact emergencyContact = arguments.getParcelable(EmergencyContact.EMERGENCY_CONTACT_KEY);
        Account newAccount = arguments.getParcelable(Account.ACCOUNT_KEY);
        byte[] imageByte = arguments.getByteArray(LocalStorage.IMAGE_BYTE_KEY);

        Log.d(TAG, "addEmployee: account: " + newAccount);

        if (loggedInAccount == null || employee == null || emergencyContact == null || newAccount == null) {
            Log.d(TAG, "addEmployee: error data are null");
            mError.setValue(R.string.an_error_occurred);
            AddingEmployeeAttempt.setValue(true);
            return;
        }

        Log.d(TAG, "addEmployee: continuing add");
        boolean isEdit = arguments.getBoolean(LocalStorage.IS_EDIT);

        if (isEdit){
            Log.d(TAG, "addEmployee: is edit");
            uploadImage(imageByte, employee, emergencyContact, loggedInAccount);
        }
        else {
            Log.d(TAG, "addEmployee: new employee");
            accountRepository.createNewAccount(newAccount)
                    .addOnCompleteListener(new OnAddEmployeeComplete(loggedInAccount, employee,
                            emergencyContact, newAccount, imageByte));
        }
    }

    public void addEmployee(LoggedInUser loggedInUser, byte[] imageByte, Employee employee, Account account, EmergencyContact emergencyContact, boolean isEdit) {

        AddingEmployeeAttempt.setValue(false);

        Log.d(TAG, "addEmployee: sent data");
        Log.d(TAG, "addEmployee: image byte: " + Arrays.toString(imageByte));
        Log.d(TAG, "addEmployee: logged im user: " + loggedInUser);
        Log.d(TAG, "addEmployee: employee: " + employee);
        Log.d(TAG, "addEmployee: account: " + account);
        Log.d(TAG, "addEmployee: emergency contact: " + emergencyContact);
        Log.d(TAG, "addEmployee: is edit: " + isEdit);

        if (loggedInUser == null || employee == null || emergencyContact == null || account == null) {
            Log.d(TAG, "addEmployee: error data are null");
            mError.setValue(R.string.an_error_occurred);
            AddingEmployeeAttempt.setValue(true);
            return;
        }

        Log.d(TAG, "addEmployee: continuing add");

        if (isEdit){
            Log.d(TAG, "addEmployee: is edit");
            uploadImage(imageByte, employee, emergencyContact, loggedInUser);
        }
        else {
            Log.d(TAG, "addEmployee: new employee");
            accountRepository.createNewAccount(account)
                    .addOnCompleteListener(new OnAddEmployeeComplete(loggedInUser, employee,
                            emergencyContact, account, imageByte));
        }
    }

    public MutableLiveData<Boolean> getAddingEmployeeAttempt() {
        return AddingEmployeeAttempt;
    }

    public MutableLiveData<Integer> getmError() {
        return mError;
    }

    public EmergencyContact createEmergencyContact(
            String firstName,
            String lastname,
            String middleInitial,
            String suffix,
            String address1,
            String address2,
            String contactNumber) {

        return new EmergencyContact(
                firstName,
                lastname,
                middleInitial,
                suffix,
                UIUtil.createList(contactNumber),
                address1,
                address2
        );
    }

    public EmergencyContact updateEmergencyContact(
            EmergencyContact emergencyContact,
            String firstName,
            String lastname,
            String middleInitial,
            String suffix,
            String address1,
            String address2,
            String contactNumber) {

        emergencyContact.setFirstname(firstName);
        emergencyContact.setLastname(lastname);
        emergencyContact.setMiddleInitial(middleInitial);
        emergencyContact.setSuffix(suffix);
        emergencyContact.setAddress(address1);
        emergencyContact.setAddress2ndPart(address2);
        emergencyContact.setPhoneNumber(UIUtil.createList(contactNumber));

        return emergencyContact;
    }

    private class OnAddEmployeeComplete implements OnCompleteListener<AuthResult> {

        private final LoggedInUser loggedInAccount;
        private final Employee employee;
        private final EmergencyContact emergencyContact;
        private final Account newAccount;
        private final byte[] imageByte;

        public OnAddEmployeeComplete(LoggedInUser loggedInAccount, Employee employee,
                                     EmergencyContact emergencyContact, Account newAccount, byte[] imageByte) {
            this.loggedInAccount = loggedInAccount;
            this.employee = employee;
            this.emergencyContact = emergencyContact;
            this.newAccount = newAccount;
            this.imageByte = imageByte;
        }

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Log.d(TAG, "onComplete: attempting to create account");

            if (!task.isSuccessful() && task.getException() != null) {
                mError.setValue(accountRepository.getCreateAccountError(task));
                AddingEmployeeAttempt.setValue(true);
                Log.d(TAG, "onComplete: task is unsuccessful");
                return;
            }

            FirebaseUser user = task.getResult().getUser();
            if (user != null) {
                accountRepository.setUserIds(employee, newAccount, user.getUid());

                employee.setAccountUid(user.getUid());
            }

            String employeeUid = employeeRepository.getNewUid();
            employee.setUid(employeeUid);

            String emergencyContactUid = employeeRepository.getNewUid();
            employee.setEmergencyContactUid(emergencyContactUid);
            emergencyContact.setUid(emergencyContactUid);

            uploadImage(imageByte, employee, emergencyContact, loggedInAccount);
        }
    }

    private void uploadImage(byte[] imageByte, Employee employee,
                             EmergencyContact emergencyContact,LoggedInUser loggedInAccount) {
        Log.d(TAG, "uploadImage: uploading image");
        if (imageByte != null) {
            employeeRepository.uploadDisplayImage(employee, imageByte).addOnCompleteListener(uploadTask -> {

                if (uploadTask.isSuccessful()) {
                    Log.d(TAG, "onComplete: called");
                    employee.setDisplayImage(uploadTask.getResult().toString());

                    addEmployeeData(employee, emergencyContact, loggedInAccount);
                } else {
                    Log.e(TAG, "onComplete: error in adding employee");
                    mError.setValue(R.string.an_error_occurred);
                    AddingEmployeeAttempt.setValue(true);
                }
            });
        }
        else {
            addEmployeeData(employee, emergencyContact, loggedInAccount);
        }
    }

    private void addEmployeeData(Employee employee, EmergencyContact emergencyContact, LoggedInUser loggedInAccount) {
        employeeRepository.addEmployee(employee);

        employeeRepository.addEmergencyContact(emergencyContact);
        accountRepository.reLoginUser(loggedInAccount);

        mError.setValue(Checker.VALID);
        AddingEmployeeAttempt.setValue(true);
    }
}
