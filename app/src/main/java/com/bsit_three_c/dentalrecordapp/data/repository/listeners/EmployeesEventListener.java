package com.bsit_three_c.dentalrecordapp.data.repository.listeners;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmployeesEventListener implements ValueEventListener {
    private static final String TAG = EmployeesEventListener.class.getSimpleName();

    private final MutableLiveData<ArrayList<Employee>> mEmployees;

    public EmployeesEventListener(MutableLiveData<ArrayList<Employee>> mEmployees) {
        this.mEmployees = mEmployees;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        final ArrayList<Employee> employees = new ArrayList<>();

        Log.d(TAG, "onDataChange: employee snapshot count: " + snapshot.getChildrenCount());
        Log.d(TAG, "onDataChange: employee snapshot: " + snapshot);
        if (!(snapshot.getChildrenCount() <= 0)) {

            for (DataSnapshot data : snapshot.getChildren()) {
                Employee employee = data.getValue(Employee.class);

                if (employee == null) continue;

                EmployeeRepository.initialize(employee);
                Log.d(TAG, "onDataChange: employee: " + employee);
                employees.add(employee);
            }
        }

        mEmployees.setValue(employees);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
