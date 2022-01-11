package com.bsit_three_c.dentalrecordapp.ui.employees.view_employee;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.EmergencyContact;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;

public class EmployeeInfoViewModel extends ViewModel {

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    private final MutableLiveData<Employee> mEMployee = new MutableLiveData<>();
    private final MutableLiveData<Account> mAccount = new MutableLiveData<>();
    private final MutableLiveData<EmergencyContact> mEmergencyContact = new MutableLiveData<>();

    private final AccountRepository.AccountListener accountListener;
    private final EmployeeRepository.EmergencyContactListener emergencyContactListener;
    private final EmployeeRepository.EmployeeListener employeeListener;

    public EmployeeInfoViewModel(EmployeeRepository employeeRepository, AccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;

        this.accountListener = new AccountRepository.AccountListener(mAccount);
        this.emergencyContactListener = new EmployeeRepository.EmergencyContactListener(mEmergencyContact);
        this.employeeListener = new EmployeeRepository.EmployeeListener(
                mEMployee,
                employeeRepository,
                accountListener,
                emergencyContactListener
        );
    }

    public void loadEmployeeData(String employeeUid) {
        employeeRepository
                .getPath(employeeUid)
                .addValueEventListener(employeeListener);
    }

    public LiveData<Employee> getmEmployee() {
        return mEMployee;
    }

    public LiveData<Account> getmAccount() {
        return mAccount;
    }

    public LiveData<EmergencyContact> getmEmergencyContact() {
        return mEmergencyContact;
    }

    public void removeListeners() {
        if (mEMployee.getValue() != null && mAccount.getValue() != null && mEmergencyContact.getValue() != null) {
            employeeRepository.removeListeners(
                    mEMployee.getValue().getUid(),
                    mEmergencyContact.getValue().getUid(),
                    employeeListener,
                    emergencyContactListener
            );
            accountRepository.removeListener(mAccount.getValue().getUid(), accountListener);
        }
    }
}
