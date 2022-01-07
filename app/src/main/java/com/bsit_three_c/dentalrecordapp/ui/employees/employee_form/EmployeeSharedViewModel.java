package com.bsit_three_c.dentalrecordapp.ui.employees.employee_form;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.EmergencyContact;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EmployeeSharedViewModel extends ViewModel {
    private static final String TAG = EmployeeSharedViewModel.class.getSimpleName();

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    private final MutableLiveData<String> mStringUri = new MutableLiveData<>();
    private final MutableLiveData<byte[]> mImageByte = new MutableLiveData<>();
    private final MutableLiveData<Employee> mEmployee = new MutableLiveData<>();
    private final MutableLiveData<Account> mAccount = new MutableLiveData<>();
    private final MutableLiveData<EmergencyContact> mEmergencyContact = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mEdit = new MutableLiveData<>();

    private final ValueEventListener employeeListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: getting employees");
            Employee employee = snapshot.getValue(Employee.class);

            if (employee != null) {
                mEmployee.setValue(employee);

                loadAccount(employee);
                loadContact(employee);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final ValueEventListener contactListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: getting employees");
            EmergencyContact contact = snapshot.getValue(EmergencyContact.class);

            if (contact != null) {
                mEmergencyContact.setValue(contact);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final ValueEventListener accountListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Account account = snapshot.getValue(Account.class);

            if (account != null) {
                Log.d(TAG, "onDataChange: account retrieved: " + account);
                mAccount.setValue(account);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public EmployeeSharedViewModel() {
        this.employeeRepository = EmployeeRepository.getInstance();
        this.accountRepository = AccountRepository.getInstance();
    }

    public void loadEmployee(String employeeUid) {
        Log.d(TAG, "loadEmployee: load employee");
        employeeRepository.getEmployeePath(employeeUid).addListenerForSingleValueEvent(employeeListener);
    }

    private void loadContact(Employee employee) {
        employeeRepository.getContactPath(employee.getEmergencyContactUid()).addListenerForSingleValueEvent(contactListener);
    }

    private void loadAccount(Employee employee) {
        accountRepository.getAccountPath(employee.getAccountUid()).addListenerForSingleValueEvent(accountListener);
    }

    public Employee createEmployee(
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

    public void setmImageByte(byte[] imageByte) {
        mImageByte.setValue(imageByte);
    }

    public LiveData<byte[]> getmImageByte() {
        return mImageByte;
    }

    public void setmEmployee(Employee employee) {
        mEmployee.setValue(employee);
    }

    public LiveData<Employee> getmEmployee() {
        return mEmployee;
    }

    public void setmAccount(Account account) {
        mAccount.setValue(account);
    }

    public LiveData<Account> getmAccount() {
        return mAccount;
    }

    public void setmEmergencyContact(EmergencyContact emergencyContact) {
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
        employeeRepository.getEmployeePath(employee.getUid()).removeEventListener(employeeListener);
        employeeRepository.getContactPath(employee.getEmergencyContactUid()).removeEventListener(contactListener);
        accountRepository.getAccountPath(employee.getAccountUid()).removeEventListener(accountListener);
    }
}
