package com.bsit_three_c.dentalrecordapp.data.repository;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.EmergencyContact;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class EmployeeRepository {

    private static final String TAG = EmployeeRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReferenceEmployee;
    private final DatabaseReference databaseReferenceEmergencyContact;
    private final FirebaseStorage firebaseStorage;
    private final StorageReference storageReference;

    private static volatile EmployeeRepository instance;

    private ArrayList<Person> personArrayList;
    private boolean isEmployeesLoaded = false;
    private final MutableLiveData<Boolean> isGettingEmployeesDone = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDoneAddingEmployee = new MutableLiveData<>();
    private final MutableLiveData<Employee> mEmployee = new MutableLiveData<>();
    private final MutableLiveData<Account> mAccount = new MutableLiveData<>();
    private final MutableLiveData<EmergencyContact> mEmergencyContact = new MutableLiveData<>();

    private final FirebaseHelper.CountChildren countedChildren = new FirebaseHelper.CountChildren();;
    private ItemAdapter adapter;
    private long count;

    public EmployeeRepository() {
        this.database = FirebaseDatabase.getInstance(FirebaseHelper.FIREBASE_URL);
        this.databaseReferenceEmployee = database.getReference(FirebaseHelper.EMPLOYEES_REFERENCE);
        this.databaseReferenceEmergencyContact = database.getReference(FirebaseHelper.EMERGENCY_CONTACT_REFERENCE);
        this.firebaseStorage = FirebaseStorage.getInstance(FirebaseHelper.FIREBASE_STORAGE_URL);
        this.storageReference = firebaseStorage.getReference(FirebaseHelper.EMPLOYEE_DISPLAY_IMAGE_LOCATION);
    }

    public static EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepository();
        }
        return instance;
    }

    public String getNewUid() {
        return databaseReferenceEmployee.push().getKey();
    }

    public void addEmployee(Employee employee) {
        databaseReferenceEmployee.child(employee.getUid()).setValue(employee);
    }

    public void addEmergencyContact(EmergencyContact emergencyContact) {
        databaseReferenceEmergencyContact.child(emergencyContact.getUid()).setValue(emergencyContact);
    }


    public void addEmployee(Bundle employeeBundle) {
        isDoneAddingEmployee.setValue(false);
//        isUploadDone.setValue(false);

        Employee employee = employeeBundle.getParcelable(LocalStorage.EMPLOYEE_KEY);

        String employeeUid = getNewUid();
        String child = employeeUid + FirebaseHelper.IMAGE_EXTENSION;

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("caption", employee.getLastname() + " display image")
                .build();

        byte[] data = employeeBundle.getByteArray(LocalStorage.IMAGE_BYTE_KEY);
        UploadTask uploadTask = storageReference.child(child).putBytes(data, metadata);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Log.d(TAG, "onComplete: done uploading");
//                isUploadDone.setValue(true);
            }
        });

        Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            Log.d(TAG, "then: got URI");
            return storageReference.child(child).getDownloadUrl();
        });

        getDownloadUriTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String emergencyContactUid = getNewUid();
                String accountUid = getNewUid();

                LoggedInUser loggedInAccount = employeeBundle.getParcelable(Account.LOGGED_ID);
                Account account = employeeBundle.getParcelable(Account.ACCOUNT_KEY);
                if (account != null && loggedInAccount != null) {
                    account.setUid(accountUid);
                    AccountRepository.getInstance().createNewAccount(loggedInAccount, account).addOnSuccessListener(authResult -> {

                        Log.d(TAG, "onSuccess: called");
                        employee.setUid(employeeUid);
                        employee.setDisplayImage(task.getResult().toString());
                        employee.setEmergencyContactUid(emergencyContactUid);
                        employee.setAccountUid(accountUid);
                        addEmployee(employee);

                        EmergencyContact emergencyContact = employeeBundle.getParcelable(EmergencyContact.EMERGENCY_CONTACT_KEY);
                        if (emergencyContact != null) {
                            emergencyContact.setUid(emergencyContactUid);
                            addEmergencyContact(emergencyContact);

                            isDoneAddingEmployee.setValue(true);
                        }
                    });
                }
            }
        });
    }

    public LiveData<Boolean> getIsDoneAddingEmployee() {
        return isDoneAddingEmployee;
    }

    //  Retrieving employees from firebase

    public void setAdapter(ItemAdapter adapter) {
        this.adapter = adapter;
    }

    public void loadEmployee(String employeeUid) {
        databaseReferenceEmployee.child(employeeUid).addValueEventListener(employeeListener);
    }

    public LiveData<Employee> getmEmployee() {
        return mEmployee;
    }

    public LiveData<Account> getmAccount() {
        return mAccount;
    }

    public LiveData<EmergencyContact> getmEmergencyContact() {
        return mEmergencyContact;
    }

    public void getEmployees() {
        isGettingEmployeesDone.setValue(false);
        databaseReferenceEmployee.orderByChild("lastname").addValueEventListener(employeesListener);
    }

    private boolean isDuplicate(Employee employee) {
        return personArrayList.contains(employee);
    }

    private void initialize(Employee employee) {

        Log.d(TAG, "initialize: initializing patient: " + employee);

        final String notAvailable = "N/A";

        if (!Checker.isDataAvailable(employee.getFirstname()))
            employee.setFirstname(notAvailable);

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
    }

    public void remove(Employee employee, Context context) {
        Task<Void> removeEmployee = databaseReferenceEmployee.child(employee.getUid()).removeValue();
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

            LoggedInUser loggedInAccount = LocalStorage.getLoggedInUser(context);
            AccountRepository.getInstance().removeAccount(loggedInAccount, employee.getAccountUid());
            return null;
        });
    }


    private final FirebaseHelper.CountChildren countEmployees = new FirebaseHelper.CountChildren();

    public void countEmployees() {
        databaseReferenceEmployee.addListenerForSingleValueEvent(countEmployees);
    }

    public LiveData<Long> getEmployeesCount() {
        return countEmployees.getCount();
    }

    //  Listeners
    private String contactUid = "";

    //  Initialize the LiveDatas
    private final ValueEventListener employeeListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: getting employees");
            Employee employee = snapshot.getValue(Employee.class);

            if (employee != null) {
                mEmployee.setValue(employee);
                contactUid = employee.getEmergencyContactUid();

                AccountRepository.getInstance().loadAccount(employee.getAccountUid());
                databaseReferenceEmergencyContact
                        .child(contactUid)
                        .addValueEventListener(emergencyContactListener);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private final ValueEventListener emergencyContactListener = new ValueEventListener() {
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
    };

    private final ValueEventListener employeesListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: data changed in employee list");

            int counter = 0;

            if (adapter.getItemCount() != 0 && adapter.getItemCount() == count){
                adapter.clearAll();
            }

            EmployeeRepository.this.personArrayList = new ArrayList<>();
            for (DataSnapshot data : snapshot.getChildren()) {
                Employee employee = data.getValue(Employee.class);

                if (employee != null && !isDuplicate(employee)) {

                    employee.setUid(data.getKey());
                    initialize(employee);
                    adapter.addItem(employee);

                    counter++;

                }
            }

            count = counter;

            adapter.notifyDataSetChanged();
            isGettingEmployeesDone.setValue(true);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public LiveData<Boolean> getIsGettingEmployeesDone() {
        return isGettingEmployeesDone;
    }

    public void removeListeners(String employeeUid) {
        databaseReferenceEmployee.child(employeeUid).removeEventListener(employeeListener);
        databaseReferenceEmergencyContact.child(contactUid).addValueEventListener(emergencyContactListener);
        databaseReferenceEmployee.removeEventListener(employeesListener);
    }
}
