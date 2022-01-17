package com.bsit_three_c.dentalrecordapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.AdminRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.LoginRepository;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.google.firebase.database.ValueEventListener;

public class MainAdminViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private final LoginRepository loginRepository;
    private final EmployeeRepository employeeRepository;
    private final AdminRepository adminRepository;

    private final MutableLiveData<Employee> mEmployee;
    private final MutableLiveData<Person> mAdmin;
    private final MutableLiveData<LoggedInUser> mLoggedInUser = new MutableLiveData<>();

    private final ValueEventListener employeeListener;
    private final ValueEventListener adminListener;

    private boolean isLoggedIn = false;

    public MainAdminViewModel(LoginRepository repository) {
        this.loginRepository = repository;

        this.employeeRepository = EmployeeRepository.getInstance();
        this.adminRepository = AdminRepository.getInstance();
        this.mEmployee = new MutableLiveData<>();
        this.mAdmin = new MutableLiveData<>();
        this.employeeListener = new EmployeeRepository.EmployeeListener(mEmployee);
        this.adminListener = new AdminRepository.AdminListener(mAdmin);
    }

    public void logout() {
        isLoggedIn = false;
        loginRepository.logout();
    }

    public LiveData<LoggedInUser> getmLoggedInUser() {
        return mLoggedInUser;
    }

    public void setmLoggedInUser(LoggedInUser loggedInUser) {
        mLoggedInUser.setValue(loggedInUser);
    }

    public void getEmployee(String employeeUid) {
        employeeRepository
                .getPath(employeeUid)
                .addValueEventListener(employeeListener);
    }

    public LiveData<Employee> getmEmployee() {
        return mEmployee;
    }

    public void getAdmin() {
        adminRepository
                .getDatabaseReference()
                .addValueEventListener(adminListener);
    }

    public LiveData<Person> getmAdmin() {
        return mAdmin;
    }

    public void removeListeners() {
        if (mEmployee.getValue() != null) {
            employeeRepository
                    .getPath(mEmployee.getValue().getUid())
                    .removeEventListener(employeeListener);
        }
        if (getmAdmin().getValue() != null) {
            adminRepository
                    .getDatabaseReference()
                    .removeEventListener(adminListener);
        }
    }
}