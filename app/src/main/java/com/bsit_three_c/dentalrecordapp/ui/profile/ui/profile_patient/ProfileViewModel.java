package com.bsit_three_c.dentalrecordapp.ui.profile.ui.profile_patient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.Clinic;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.AdminRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ClinicRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;

public class ProfileViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private final AccountRepository accountRepository;
    private final AdminRepository adminRepository;
    private final ClinicRepository clinicRepository;
    private final EmployeeRepository employeeRepository;
    private final PatientRepository patientRepository;

    private final MutableLiveData<String> mUid;
    private final MutableLiveData<Account> mAccount;
    private final MutableLiveData<Person> mAdmin;
    private final MutableLiveData<Clinic> mClinic;
    private final MutableLiveData<Employee> mEmployee;
    private final MutableLiveData<Patient> mPatient;

    private final AccountRepository.AccountListener accountListener;
    private final AdminRepository.AdminListener adminListener;
    private final ClinicRepository.ClinicListener clinicListener;
    private final EmployeeRepository.EmployeeListener employeeListener;
    private final PatientRepository.PatientListener patientListener;

    public ProfileViewModel() {
        this.accountRepository = AccountRepository.getInstance();
        this.adminRepository = AdminRepository.getInstance();
        this.clinicRepository = (ClinicRepository) ClinicRepository.getInstance();
        this.patientRepository = PatientRepository.getInstance();
        this.employeeRepository = EmployeeRepository.getInstance();

        this.mUid = new MutableLiveData<>();
        this.mAccount = new MutableLiveData<>();
        this.mAdmin = new MutableLiveData<>();
        this.mClinic = new MutableLiveData<>();
        this.mEmployee = new MutableLiveData<>();
        this.mPatient = new MutableLiveData<>();

        this.accountListener = new AccountRepository.AccountListener(mAccount);
        this.adminListener = new AdminRepository.AdminListener(mAdmin);
        this.clinicListener = new ClinicRepository.ClinicListener(mClinic);
        this.employeeListener = new EmployeeRepository.EmployeeListener(mEmployee);
        this.patientListener = new PatientRepository.PatientListener(mPatient);
    }

    public void getPatient(String uid) {
        patientRepository.getPath(uid).addValueEventListener(patientListener);
    }

    public LiveData<Patient> getmPatient() {
        return mPatient;
    }

    public void getAdmin() {
        adminRepository.getDatabaseReference().addValueEventListener(adminListener);
    }

    public LiveData<Person> getmAdmin() {
        return mAdmin;
    }

    public void getClinic() {
        clinicRepository.getDatabaseReference().addValueEventListener(clinicListener);
    }

    public LiveData<Clinic> getmClinic() {
        return mClinic;
    }

    public void getEmployee(String uid) {
        employeeRepository.getPath(uid).addValueEventListener(employeeListener);
    }

    public LiveData<Employee> getmEmployee() {
        return mEmployee;
    }

    public void getAccount(String uid) {
        accountRepository.getPath(uid).addValueEventListener(accountListener);
    }

    public LiveData<Account> getmAccount() {
        return mAccount;
    }

    public void setUid(String uid) {
        mUid.setValue(uid);
    }

    public LiveData<String> getmUid() {
        return mUid;
    }
}