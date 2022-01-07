package com.bsit_three_c.dentalrecordapp.ui.employees.view_employee;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.EmergencyContact;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;

public class EmployeeInfoViewModel extends ViewModel {

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    public EmployeeInfoViewModel(EmployeeRepository employeeRepository, AccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }

    public void loadEmployeeData(String employeeUid) {
        employeeRepository.loadEmployee(employeeUid);
    }

    public LiveData<Employee> getmEmployee() {
        return employeeRepository.getmEmployee();
    }

    public LiveData<Account> getmAccount() {
        return accountRepository.getmAccount();
    }

    public LiveData<EmergencyContact> getmEmergencyContact() {
        return employeeRepository.getmEmergencyContact();
    }

    public void removeListeners(String employeeUid) {
        employeeRepository.removeListeners(employeeUid);
    }
}
