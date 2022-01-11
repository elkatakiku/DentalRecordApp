package com.bsit_three_c.dentalrecordapp.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.model.EmergencyContact;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class EmployeeRepository extends BaseRepository {

    private static final String TAG = EmployeeRepository.class.getSimpleName();

    private final DatabaseReference databaseReferenceEmergencyContact;
    private final FirebaseStorage firebaseStorage;
    private final StorageReference storageReference;

    private static final String LASTNAME_PATH = "lastname";

    private static volatile EmployeeRepository instance;

    public EmployeeRepository() {
        super(EMPLOYEES_REFERENCE);
        this.databaseReferenceEmergencyContact = database.getReference(FirebaseHelper.EMERGENCY_CONTACT_REFERENCE);
        this.firebaseStorage = FirebaseStorage.getInstance(FirebaseHelper.FIREBASE_STORAGE_URL);
        this.storageReference = firebaseStorage.getReference(FirebaseHelper.EMPLOYEE_DISPLAY_IMAGE_LOCATION);
    }

    public DatabaseReference getDatabaseReferenceEmergencyContact() {
        return databaseReferenceEmergencyContact;
    }

    public static EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepository();
        }
        return instance;
    }

    public void addEmployee(Employee employee) {
        databaseReference.child(employee.getUid()).setValue(employee);
    }

    public void addEmergencyContact(EmergencyContact emergencyContact) {
        databaseReferenceEmergencyContact.child(emergencyContact.getUid()).setValue(emergencyContact);
    }

    public Task<Uri> uploadDisplayImage(Employee employee, byte[] imageByte) {
        String child = employee.getUid() + FirebaseHelper.IMAGE_EXTENSION;

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("caption", employee.getLastname() + " display image")
                .build();

        return storageReference.child(child).putBytes(imageByte, metadata).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            Log.d(TAG, "then: got URI");
            return storageReference.child(child).getDownloadUrl();
        });
    }

    public Query getEmployeePathThroughAccount(String accountUid) {
        return databaseReference.orderByChild(AccountRepository.ACCOUNT_UID_PATH).equalTo(accountUid);
    }

    public DatabaseReference getContactPath(String emergencyContactUid) {
        return databaseReferenceEmergencyContact.child(emergencyContactUid);
    }

    public Query getEmployeesPath() {
        return databaseReference.orderByChild(LASTNAME_PATH);
    }

    public static void initialize(Employee employee) {
        Log.d(TAG, "initialize: initializing employee: " + employee);

        final String notAvailable = Checker.NOT_AVAILABLE;

        if (!Checker.isDataAvailable(employee.getDisplayImage())) {
            employee.setDisplayImage(notAvailable);
        }

        if (!Checker.isDataAvailable(employee.getFirstname())) {
            employee.setFirstname(notAvailable);
        }

        if (!Checker.isDataAvailable(employee.getLastname()))
            employee.setLastname(notAvailable);

        if (!Checker.isDataAvailable(employee.getMiddleInitial()))
            employee.setMiddleInitial(notAvailable);

        if (!Checker.isDataAvailable(employee.getSuffix()))
            employee.setSuffix(notAvailable);

        if (!Checker.isDataAvailable(employee.getDateOfBirth())) {
            employee.setDateOfBirth(notAvailable);
        }

        if (!Checker.isDataAvailable(employee.getAddress())) {
            employee.setAddress(notAvailable);
        }

        if (!Checker.isDataAvailable(employee.getAddress2ndPart())) {
            employee.setAddress2ndPart(notAvailable);
        }

        if (employee.getPhoneNumber() == null) {
            ArrayList<String> contact = new ArrayList<>();
            contact.add(FirebaseHelper.NEW_PATIENT);
            employee.setPhoneNumber(contact);
        }

        if (employee.getLastUpdated() == null)
            employee.setLastUpdated(new Date());

        if (!Checker.isDataAvailable(employee.getEspecialties())) {
            employee.setEspecialties(notAvailable);
        }
    }

    public void remove(Employee employee, LoggedInUser loggedInUser) {
        Task<Void> removeEmployee = databaseReference.child(employee.getUid()).removeValue();
        Task<Void> removeDisplayPicture = removeEmployee.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                //  Handle error
                throw Objects.requireNonNull(task.getException());
            }
            return storageReference.child(employee.getUid() + FirebaseHelper.IMAGE_EXTENSION).delete();
        });

        removeDisplayPicture
                .continueWithTask(task -> {
            if (!task.isSuccessful()) {
                //  Handle error
                throw Objects.requireNonNull(task.getException());
            }

            return databaseReferenceEmergencyContact.child(employee.getEmergencyContactUid()).removeValue();
        })
                .continueWith(task -> {
            if (!task.isSuccessful()) {
                //  Handle error
                throw Objects.requireNonNull(task.getException());
            }

            AccountRepository.getInstance().removeAccount(loggedInUser, employee.getAccountUid());
            return null;
        });
    }



    //  Initialize the LiveData
    public static class EmployeeListener implements ValueEventListener {

        private final MutableLiveData<Employee> mEmployee;
        private final EmployeeRepository employeeRepository;
        private final AccountRepository.AccountListener accountListener;
        private final EmployeeRepository.EmergencyContactListener emergencyContactListener;

        public EmployeeListener(MutableLiveData<Employee> mEmployee,
                                EmployeeRepository employeeRepository,
                                AccountRepository.AccountListener accountListener,
                                EmergencyContactListener emergencyContactListener) {

            this.mEmployee = mEmployee;
            this.employeeRepository = employeeRepository;
            this.accountListener = accountListener;
            this.emergencyContactListener = emergencyContactListener;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: getting employees");
            Employee employee = snapshot.getValue(Employee.class);

            if (employee != null) {
                mEmployee.setValue(employee);

                AccountRepository
                        .getInstance()
                        .getPath(employee.getAccountUid())
                        .addListenerForSingleValueEvent(accountListener);

                employeeRepository
                        .getDatabaseReferenceEmergencyContact()
                        .child(employee.getEmergencyContactUid())
                        .addValueEventListener(emergencyContactListener);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    public static class EmergencyContactListener implements ValueEventListener {

        private final MutableLiveData<EmergencyContact> mEmergencyContact;

        public EmergencyContactListener(MutableLiveData<EmergencyContact> mEmergencyContact) {
            this.mEmergencyContact = mEmergencyContact;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            EmergencyContact emergencyContact = snapshot.getValue(EmergencyContact.class);

            if (emergencyContact != null) {
                mEmergencyContact.setValue(emergencyContact);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    public void removeListeners(String employeeUid,
                                String emergencyContactUid,
                                EmployeeListener employeeListener,
                                EmergencyContactListener emergencyContactListener) {

        databaseReference.child(employeeUid).removeEventListener(employeeListener);
        databaseReferenceEmergencyContact.child(emergencyContactUid).removeEventListener(emergencyContactListener);
    }
}
