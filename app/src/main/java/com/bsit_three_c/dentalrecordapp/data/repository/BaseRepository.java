package com.bsit_three_c.dentalrecordapp.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BaseRepository {

    public static final String FIREBASE_URL = "https://dental-record-app-default-rtdb.asia-southeast1.firebasedatabase.app";
    public static final String PAYMENTS_REFERENCE = "payments";
    public static final String PATIENTS_REFERENCE = "patients";
    public static final String OPERATIONS_REFERENCE = "operations";
    public static final String SERVICES_REFERENCE = "services";
    public static final String USERS_REFERENCE = "users";
    public static final String ADMINS_REFERENCE = "admins";
    public static final String EMPLOYEES_REFERENCE = "employees";
    public static final String EMERGENCY_CONTACT_REFERENCE = "emergency_contacts";
    public static final String CLIENTS_REFERENCE = "clients";
    public static final String ACCOUNTS_REFERENCE = "accounts";
    public static final String DENTAL_CHART_REFERENCE = "dental_chart";
    public static final String APPOINTMENTS_REFERENCE = "appointments";

    public static final String PATIENT_UID = "patient_uid";
    public static final String DENTAL_PROCEDURES = "dentalProcedures";
    public static final String DENTAL_HISTORY = "dental_records";
    public static final String PAYMENT_KEYS = "paymentKeys";
    public static final String BALANCE = "dentalBalance";
    public static final String TOTAL = "total";
    public static final String NEW_PATIENT = "New patient";
    public static final String PAYMENT_RECORDS = "payment_records";

    public static final String TYPE_ADMIN = "Admin";
    public static final String TYPE_EMPLOYEE = "Employee";
    public static final String TYPE_CLIENT = "Client";

    public static final String FIREBASE_STORAGE_URL = "gs://dental-record-app.appspot.com";
    public static final String SERVICES_DISPLAY_IMAGE_LOCATION = "services_display_image/";
    public static final String EMPLOYEE_DISPLAY_IMAGE_LOCATION = "employee_display_image/";
    public static final String DENTAL_CHART_IMAGE_LOCATION = "dental_chart_image/";

    public static final String IMAGE_EXTENSION = ".png";

    protected final FirebaseDatabase database;
    protected final DatabaseReference databaseReference;

    public BaseRepository(String reference) {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(reference);
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public String getNewUid() {
        return databaseReference.push().getKey();
    }

    public DatabaseReference getPath(String uid) {
        return databaseReference.child(uid);
    }

    public void remove(String uid) {
        databaseReference.child(uid).removeValue();
    }

    public static class CountChildren implements ValueEventListener {

        private final MutableLiveData<Long> counter;

        public CountChildren(MutableLiveData<Long> counter) {
            this.counter = counter;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            counter.setValue(snapshot.getChildrenCount());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }
}
