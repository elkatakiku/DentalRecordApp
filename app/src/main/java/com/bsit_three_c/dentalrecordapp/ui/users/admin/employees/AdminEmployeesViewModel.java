package com.bsit_three_c.dentalrecordapp.ui.users.admin.employees;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;

public class AdminEmployeesViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private final EmployeeRepository employeeRepository;

    public AdminEmployeesViewModel(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public void initializeEventListener(ItemAdapter adapter) {
        employeeRepository.setAdapter(adapter);
    }

    public void loadPatients() {
        employeeRepository.getEmployees();
    }

    public LiveData<Boolean> isGettingEmployeesDone() {
        return employeeRepository.getIsDoneAddingEmployee();
    }
}