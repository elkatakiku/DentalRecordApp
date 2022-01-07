package com.bsit_three_c.dentalrecordapp.ui.employees;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class EmployeesViewModel extends ViewModel {
    private static final String TAG = EmployeesViewModel.class.getSimpleName();
    // TODO: Implement the ViewModel

    private final EmployeeRepository employeeRepository;

    private final MutableLiveData<Boolean> mGotEmployee = new MutableLiveData<>();

    private ItemAdapter adapter;

    public EmployeesViewModel(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

       public void initializeEventListener(ItemAdapter adapter) {
        this.adapter = adapter;
    }

    public void loadPatients() {
        Log.d(TAG, "loadPatients: loading patients");
        mGotEmployee.setValue(false);
        employeeRepository.getEmployeesPath().addValueEventListener(employeesListener);
    }

    private long count;
    private final ValueEventListener employeesListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: data changed in employee list");

            int counter = 0;

            if (adapter.getItemCount() != 0 && adapter.getItemCount() == count){
                adapter.clearAll();
            }

            for (DataSnapshot data : snapshot.getChildren()) {
                Employee employee = data.getValue(Employee.class);

                if (employee != null) {
                    if (!Checker.isDataAvailable(employee.getUid())) {
                        employee.setUid(data.getKey());
                    }
                    EmployeeRepository.initialize(employee);
                    adapter.addItem(employee);

                    counter++;
                }
            }

            count = counter;
            adapter.initializeOrigList();

            adapter.notifyDataSetChanged();
            mGotEmployee.setValue(true);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public LiveData<Boolean> getmGotEmployee() {
        return mGotEmployee;
    }
}