package com.bsit_three_c.dentalrecordapp.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.model.Account;
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
        this.databaseReferenceEmergencyContact = database.getReference(EMERGENCY_CONTACT_REFERENCE);
        this.firebaseStorage = FirebaseStorage.getInstance(FIREBASE_STORAGE_URL);
        this.storageReference = firebaseStorage.getReference(EMPLOYEE_DISPLAY_IMAGE_LOCATION);
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
        String child = employee.getUid() + IMAGE_EXTENSION;

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

    public DatabaseReference getEmergencyContactPath(String emergencyContactUid) {
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
            contact.add(NEW_PATIENT);
            employee.setPhoneNumber(contact);
        }

        if (employee.getLastUpdated() == null)
            employee.setLastUpdated(new Date());

        if (!Checker.isDataAvailable(employee.getEspecialties())) {
            employee.setEspecialties(notAvailable);
        }
    }

    public static void initializeEmergencyContact(EmergencyContact emergencyContact) {
        final String notAvailable = Checker.NOT_AVAILABLE;

        if (!Checker.isDataAvailable(emergencyContact.getFirstname())) {
            emergencyContact.setFirstname(notAvailable);
        }

        if (!Checker.isDataAvailable(emergencyContact.getLastname()))
            emergencyContact.setLastname(notAvailable);

        if (!Checker.isDataAvailable(emergencyContact.getMiddleInitial()))
            emergencyContact.setMiddleInitial(notAvailable);

        if (!Checker.isDataAvailable(emergencyContact.getSuffix()))
            emergencyContact.setSuffix(notAvailable);


        if (!Checker.isDataAvailable(emergencyContact.getAddress())) {
            emergencyContact.setAddress(notAvailable);
        }

        if (!Checker.isDataAvailable(emergencyContact.getAddress2ndPart())) {
            emergencyContact.setAddress2ndPart(notAvailable);
        }

        if (emergencyContact.getPhoneNumber() == null) {
            ArrayList<String> contact = new ArrayList<>();
            contact.add(NEW_PATIENT);
            emergencyContact.setPhoneNumber(contact);
        }

        if (emergencyContact.getLastUpdated() == null)
            emergencyContact.setLastUpdated(new Date());
    }

    public void remove(LoggedInUser loggedInUser, Employee employee) {
        Log.d(TAG, "remove: removing employee data: " + employee);
        AccountRepository accountRepository = AccountRepository.getInstance();
        accountRepository
                .getPath(employee.getAccountUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange: data changed in account");
                        Account account = snapshot.getValue(Account.class);

                        if (account != null) {
                            Log.d(TAG, "onDataChange: got account: " + account);
                            Log.d(TAG, "onDataChange: signing with the account");
                            accountRepository
                                    .signInWithEmail(account)
                                    .continueWith(task -> {
                                        Log.d(TAG, "onDataChange: sign in attempt done");
                                        if (task.isSuccessful() && task.getResult().getUser() != null) {
                                            Log.d(TAG, "onDataChange: success signing in");
                                            Log.d(TAG, "onDataChange: deleting account");
                                            accountRepository.remove(account.getUid());
                                            Log.d(TAG, "onDataChange: deleting user");
                                            task.getResult()
                                                    .getUser()
                                                    .delete()
                                                    .continueWith(task1 -> {
                                                        Log.d(TAG, "onDataChange: done attempt deleting user");
                                                        if (task1.isSuccessful()) {
                                                            Log.d(TAG, "onDataChange: success in deleting user");
                                                            Log.d(TAG, "onDataChange: signing the logged in user");
                                                            accountRepository
                                                                    .signInWithEmail(loggedInUser.getAccount())
                                                                    .continueWith(task11 -> {
                                                                        Log.d(TAG, "onDataChange: done attempt signing in");
                                                                        if (task.isSuccessful() && task.getResult().getUser() != null) {
                                                                            Log.d(TAG, "onDataChange: removing employee");
                                                                            removeDisplayImage(employee);
                                                                        }
                                                                        return null;
                                                                    });
                                                        }
                                                        return null;
                                                    });
                                        }
                                        return null;
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void removeDisplayImage(Employee employee) {
        Log.d(TAG, "remove: removing employee continuation");
        Log.d(TAG, "remove: to be removed: " + employee);
        Log.d(TAG, "remove: deleting display image");
        if (Checker.isDataAvailable(employee.getDisplayImage())) {
            Log.d(TAG, "removeDisplayImage: has display image");
            storageReference
                    .child(employee.getUid() + IMAGE_EXTENSION)
                    .delete()
                    .continueWith(task -> {
                        Log.d(TAG, "remove: done image delete attempt");
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remove: success in deleting image");
                            removeEmergencyContact(employee);
                        }
                        return null;
                    });
        } else {
            Log.d(TAG, "removeDisplayImage: has no display image");
            removeEmergencyContact(employee);
        }
//        Task<Void> removeEmployee = remove(employee.getUid());
//
//        Task<Void> removeDisplayPicture = removeEmployee.continueWithTask(task -> {
//            if (!task.isSuccessful()) {
//                //  Handle error
//                throw Objects.requireNonNull(task.getException());
//            }
//            return storageReference.child(employee.getUid() + IMAGE_EXTENSION).delete();
//        });
//
//        removeDisplayPicture
//                .continueWithTask(task -> {
//                    if (!task.isSuccessful()) {
//                        //  Handle error
//                        throw Objects.requireNonNull(task.getException());
//                    }
//
//                    return databaseReferenceEmergencyContact.child(employee.getEmergencyContactUid()).removeValue();
//                });
    }

    private void removeEmergencyContact(Employee employee) {
        Log.d(TAG, "remove: deleting emergency contact");
        databaseReferenceEmergencyContact
                .child(employee.getEmergencyContactUid())
                .removeValue()
                .continueWith(task1 -> {
                    Log.d(TAG, "remove: done removing emergency contact");
                    if (task1.isSuccessful()) {
                        Log.d(TAG, "remove: removing contact success");
                        removeEmployee(employee);
                    }
                    return null;
                });
    }

    private void removeEmployee(Employee employee) {
        Log.d(TAG, "remove: deleting employee");
        remove(employee.getUid())
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "onComplete: deleting employee attempt done");
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: deleting employee success");
                    }
                });
    }

    public void remove(Employee employee, LoggedInUser loggedInUser) {
        Task<Void> removeEmployee = databaseReference.child(employee.getUid()).removeValue();
        Task<Void> removeDisplayPicture = removeEmployee.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                //  Handle error
                throw Objects.requireNonNull(task.getException());
            }
            return storageReference.child(employee.getUid() + IMAGE_EXTENSION).delete();
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

            AccountRepository
                    .getInstance()
                    .removeAccount(loggedInUser, employee.getAccountUid());
            return null;
        });
    }



    //  Initialize the LiveData
    public static class EmployeeListener implements ValueEventListener {

        private final MutableLiveData<Employee> mEmployee;

        public EmployeeListener(MutableLiveData<Employee> mEmployee) {
            this.mEmployee = mEmployee;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: getting employees");
            Employee employee = snapshot.getValue(Employee.class);

            if (employee != null) {
                initialize(employee);
                mEmployee.setValue(employee);
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
            Log.d(TAG, "onDataChange: getting emergency contact");
            EmergencyContact emergencyContact = snapshot.getValue(EmergencyContact.class);

            if (emergencyContact != null) {
                initializeEmergencyContact(emergencyContact);
                mEmergencyContact.setValue(emergencyContact);
            }

            Log.d(TAG, "onDataChange: contact: " + emergencyContact);
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
