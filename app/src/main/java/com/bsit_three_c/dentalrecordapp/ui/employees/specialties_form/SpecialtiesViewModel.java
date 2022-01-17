package com.bsit_three_c.dentalrecordapp.ui.employees.specialties_form;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;

import java.util.List;

public class SpecialtiesViewModel extends ViewModel {

    private final EmployeeRepository employeeRepository;

    private final MutableLiveData<Employee> mEmployee;
    private final MutableLiveData<String> mEmployeeUid;
    private final MutableLiveData<Boolean> mIsDone;

    private final EmployeeRepository.EmployeeListener employeeListener;

    public SpecialtiesViewModel() {
        this.employeeRepository = EmployeeRepository.getInstance();
        this.mEmployee = new MutableLiveData<>();
        this.mEmployeeUid = new MutableLiveData<>();
        this.mIsDone = new MutableLiveData<>();
        this.employeeListener = new EmployeeRepository.EmployeeListener(mEmployee);
    }

    public void setmEmployeeUid(String employeeUid) {
        mEmployeeUid.setValue(employeeUid);
    }

    public LiveData<String> getmEmployeeUid() {
        return mEmployeeUid;
    }

    public void getEmployee(String employeeUid) {
        employeeRepository
                .getPath(employeeUid)
                .addListenerForSingleValueEvent(employeeListener);
    }

    public LiveData<Employee> getmEmployee() {
        return mEmployee;
    }

    public void updateEmployee(List<String> specialties) {
        Log.d("UPLOAD", "updateEmployee: updating employee");
        Employee employee = mEmployee.getValue();
        if (employee != null) {
            employee.setSpecialties(specialties);
            employeeRepository
                    .upload(employee)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mIsDone.setValue(true);
                        }
                    });
        }
    }

    public LiveData<Boolean> getmIsDone() {
        return mIsDone;
    }
}
