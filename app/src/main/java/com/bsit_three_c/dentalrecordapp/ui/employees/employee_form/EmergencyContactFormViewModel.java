package com.bsit_three_c.dentalrecordapp.ui.employees.employee_form;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
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
import com.bsit_three_c.dentalrecordapp.util.ContactNumber;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class EmergencyContactFormViewModel extends ViewModel {
    private static final String TAG = EmergencyContactFormViewModel.class.getSimpleName();

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    private final MutableLiveData<String> mEmergencyUid;
    private final MutableLiveData<EmergencyContact> mEmergencyContact;
    private final MutableLiveData<Boolean> addEmployeeAttempt = new MutableLiveData<>();
    private final MutableLiveData<Integer> mError = new MutableLiveData<>();

    private final ValueEventListener emergencyListener;


    public EmergencyContactFormViewModel(EmployeeRepository employeeRepository, AccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;

        this.mEmergencyUid = new MutableLiveData<>();
        this.mEmergencyContact = new MutableLiveData<>();

        this.emergencyListener = new EmployeeRepository.EmergencyContactListener(mEmergencyContact);
    }

    public void setmEmergencyUid(String uid) {
        mEmergencyUid.setValue(uid);
    }

    public LiveData<String> getmEmergencyUid() {
        return mEmergencyUid;
    }

    public void getEmergency(String uid) {
        employeeRepository
                .getEmergencyContactPath(uid)
                .addListenerForSingleValueEvent(emergencyListener);
    }

    public LiveData<EmergencyContact> getmEmergencyContact() {
        return mEmergencyContact;
    }

    public boolean isUpdate() {
        return mEmergencyContact.getValue() != null;
    }

    public void uploadEmergency(EmergencyContact emergencyContact) {
        employeeRepository
                .uploadEmergencyContact(emergencyContact)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mError.setValue(Checker.VALID);
                } else {
                    mError.setValue(R.string.an_error_occurred);
                }

                addEmployeeAttempt.setValue(true);
            }
        });
    }

    public void removeListeners() {
        EmergencyContact emergencyContact = mEmergencyContact.getValue();
        if (emergencyContact != null) {
            employeeRepository
                    .getPath(emergencyContact.getUid())
                    .removeEventListener(emergencyListener);
        }
    }

    public void uploadEmployee(LoggedInUser loggedInUser, byte[] imageByte,
                               Employee employee, String email, String password,
                               EmergencyContact emergencyContact, boolean isEdit) {

        addEmployeeAttempt.setValue(false);

        Log.d(TAG, "addEmployee: sent data");
        Log.d(TAG, "addEmployee: image byte: " + Arrays.toString(imageByte));
        Log.d(TAG, "addEmployee: logged im user: " + loggedInUser);
        Log.d(TAG, "addEmployee: employee: " + employee);
        Log.d(TAG, "addEmployee: email: " + email);
        Log.d(TAG, "addEmployee: password: " + password);
        Log.d(TAG, "addEmployee: emergency contact: " + emergencyContact);
        Log.d(TAG, "addEmployee: is edit: " + isEdit);

        if (loggedInUser == null || employee == null || emergencyContact == null || email == null || password == null) {
            Log.d(TAG, "addEmployee: error data are null");
            mError.setValue(R.string.an_error_occurred);
            addEmployeeAttempt.setValue(true);
            return;
        }

        Log.d(TAG, "addEmployee: continuing add");

        if (isEdit){
            Log.d(TAG, "addEmployee: is edit");
            uploadImage(imageByte, employee, emergencyContact, loggedInUser);
        }
        else {
            Log.d(TAG, "addEmployee: new employee");
            Task<AuthResult> addAccountTask = accountRepository.createNewAccount(email, password);
            if (addAccountTask != null) {
                addAccountTask
                        .addOnCompleteListener(new OnAddEmployeeComplete(loggedInUser, employee,
                                emergencyContact, email, password, imageByte));
            } else {
                mError.setValue(R.string.invalid_empty_input);
                addEmployeeAttempt.setValue(true);
            }
        }
    }

    public MutableLiveData<Boolean> getAddEmployeeAttempt() {
        return addEmployeeAttempt;
    }

    public MutableLiveData<Integer> getmError() {
        return mError;
    }

    public EmergencyContact createEmergencyContact(
            Employee employee,
            String firstName,
            String lastname,
            String middleInitial,
            String suffix,
            String address1,
            String address2,
            String contactNumber) {

        EmergencyContact emergencyContact = new EmergencyContact(
                employeeRepository.getNewUid(),
                firstName,
                lastname,
                middleInitial,
                suffix,
                ContactNumber.createList(contactNumber),
                address1,
                address2
        );
        employee.setEmergencyContactUid(emergencyContact.getUid());

        return emergencyContact;
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
        emergencyContact.setPhoneNumber(ContactNumber.createList(contactNumber));

        return emergencyContact;
    }

    private class OnAddEmployeeComplete implements OnCompleteListener<AuthResult> {

        private final LoggedInUser loggedInAccount;
        private final Employee employee;
        private final EmergencyContact emergencyContact;
        private final String email;
        private final String password;
        private final byte[] imageByte;

        public OnAddEmployeeComplete(LoggedInUser loggedInAccount, Employee employee,
                                     EmergencyContact emergencyContact,
                                     String email, String password, byte[] imageByte) {
            this.loggedInAccount = loggedInAccount;
            this.employee = employee;
            this.emergencyContact = emergencyContact;
            this.email = email;
            this.password = password;
            this.imageByte = imageByte;
        }

        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Log.d(TAG, "onComplete: attempting to create account");

            if (!task.isSuccessful() && task.getException() != null) {
                mError.setValue(accountRepository.getCreateAccountError(task));
                addEmployeeAttempt.setValue(true);
                Log.d(TAG, "onComplete: task is unsuccessful");
                return;
            }

            FirebaseUser user = task.getResult().getUser();
            if (user != null) {
                Account account = new Account(
                        user.getUid(),
                        email,
                        password,
                        Account.TYPE_EMPLOYEE,
                        employee.getUid()
                );
                employee.setAccountUid(account.getUid());
                accountRepository.uploadAccount(account);
            }

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
                    addEmployeeAttempt.setValue(true);
                }
            });
        }
        else {
            addEmployeeData(employee, emergencyContact, loggedInAccount);
        }
    }

    private void addEmployeeData(Employee employee, EmergencyContact emergencyContact, LoggedInUser loggedInAccount) {
        employeeRepository.upload(employee);

        employeeRepository.uploadEmergencyContact(emergencyContact);
        accountRepository.reLoginUser(loggedInAccount);

        mError.setValue(Checker.VALID);
        addEmployeeAttempt.setValue(true);
    }
}
