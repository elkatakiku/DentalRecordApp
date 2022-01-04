package com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.employee_form;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.EmergencyContact;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;

import java.util.ArrayList;

public class EmergenyContactFormViewModel extends ViewModel {

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private int mError;

    public EmergenyContactFormViewModel(EmployeeRepository employeeRepository, AccountRepository accountRepository) {
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

        addEmergencyContact(
                arguments,
                firstName,
                lastname,
                middleInitial,
                suffix,
                address1,
                address2,
                contactNumber);

        employeeRepository.addEmployee(arguments);
    }

    public void addEmergencyContact(
            Bundle arguments,
            String firstName,
            String lastname,
            String middleInitial,
            String suffix,
            String address1,
            String address2,
            String contactNumber) {

        ArrayList<String> contact = new ArrayList<>(1);
        contact.add(contactNumber);

        EmergencyContact emergencyContact = new EmergencyContact(
                firstName,
                lastname,
                middleInitial,
                suffix,
                contact,
                address1,
                address2
        );

        arguments.putParcelable(EmergencyContact.EMERGENCY_CONTACT_KEY, emergencyContact);
    }

    public LiveData<Integer> getmError() {
        return accountRepository.getmError();
    }

    public void resetmError() {
        accountRepository.resetmError();
    }

}
