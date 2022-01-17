package com.bsit_three_c.dentalrecordapp.ui.menu;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.AdminRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.LoginRepository;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.firebase.database.ValueEventListener;

public class MenuAdminViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final LoginRepository loginRepository;
    private final EmployeeRepository employeeRepository;
    private final AdminRepository adminRepository;

    private final MutableLiveData<Employee> mEmployee;
    private final MutableLiveData<Person> mAdmin;

    private final ValueEventListener employeeListener;

    private final MutableLiveData<LoggedInUser> mLoggedInUser;
    private final ValueEventListener adminListener;

    public MenuAdminViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        this.adminRepository = AdminRepository.getInstance();

        this.mLoggedInUser = new MutableLiveData<>();
        this.mAdmin = new MutableLiveData<>();

        this.employeeRepository = EmployeeRepository.getInstance();
        this.adminListener = new AdminRepository.AdminListener(mAdmin);

        this.mEmployee = new MutableLiveData<>();

        this.employeeListener = new EmployeeRepository.EmployeeListener(mEmployee);
    }

    public void setmLoggedInUser(LoggedInUser loggedInUser) {
        mLoggedInUser.setValue(loggedInUser);
    }

    public LiveData<LoggedInUser> getmLoggedInUser() {
        return mLoggedInUser;
    }

    public void logout(Context context) {
        LocalStorage.clearSavedUser(context);
        loginRepository.logout();
    }

    public void getEmployee(String employeeUid) {
        employeeRepository
                .getPath(employeeUid)
                .addValueEventListener(employeeListener);
    }

    public void getAdmin() {
        adminRepository
                .getDatabaseReference()
                .addValueEventListener(adminListener);
    }

    public LiveData<Person> getmAdmin() {
        return mAdmin;
    }

    public LiveData<Employee> getmEmployee() {
        return mEmployee;
    }

    public void removeListeners() {
        if (mEmployee.getValue() != null) {
            employeeRepository
                    .getPath(mEmployee.getValue().getUid())
                    .removeEventListener(employeeListener);
        }

        if (mAdmin.getValue() != null) {
            adminRepository
                    .getDatabaseReference()
                    .removeEventListener(adminListener);
        }
    }
}