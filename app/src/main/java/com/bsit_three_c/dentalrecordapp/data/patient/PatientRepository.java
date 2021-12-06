package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class PatientRepository {

    private static final String TAG = PatientRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;

    private static final String FIREBASE_URL = "https://dental-record-app-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String USERS_REFERENCE = "patients";
    private static final String DENTAL_PROCEDURES = "dentalProcedures";
    public static final String NEW_PATIENT = "New patient";

    private static volatile PatientRepository instance;

    private ValueEventListener valueEventListener;
    private ArrayList<Person> personArrayList;
    private boolean isPatientsLoaded = false;
    private final MutableLiveData<Boolean> isGettingPatientsDone = new MutableLiveData<>();

    private PatientRepository() {
//        this.dataSource = dataSource;
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(USERS_REFERENCE);
    }

    public static PatientRepository getInstance() {
        if (instance == null) {
//            instance = new PatientRepository(dataSource);
            instance = new PatientRepository();
        }
        return instance;
    }

    private ArrayList<Person> getPatients(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            this.personArrayList = new ArrayList<>();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                Patient patient = data.getValue(Patient.class);
                if (patient != null && !isDuplicate(patient)) {
                    patient.setUid(data.getKey());
                    this.personArrayList.add(patient);
                    Log.d(TAG, "getPatients: Added new patient to array list");
                    Log.d(TAG, "getPatients: patient: " + patient.getFirstname());
                }
                else Log.d(TAG, "getPatients: Patient already in array list");
            }
        }

        isPatientsLoaded = true;
        return this.personArrayList;
    }

    public void getPatients(ItemAdapter itemAdapter) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: data changed");
                isGettingPatientsDone.setValue(false);
                Log.d(TAG, "setAdapterChange: is getting patient done: " + isGettingPatientsDone.getValue());
//                itemAdapter.getItemCount();
//                itemAdapter.clearAll();
                itemAdapter.setItems(getPatients(snapshot));
                itemAdapter.notifyDataSetChanged();
//                itemAdapter.notifyItemRangeChanged();

                isGettingPatientsDone.setValue(true);
                Log.d(TAG, "setAdapterChange: is getting patient done: " + isGettingPatientsDone.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        Log.d(TAG, "setAdapterChange: event listener: " + valueEventListener);
//        dataSource.setValueListener(valueEventListener);
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
    }

//    public boolean addPatients(Patient patient) {
////        dataSource.addPatient(patient);
//        String keyUID = databaseReference.push().getKey();
//        if (keyUID != null) {
//            patient.setUid(keyUID);
//            patient.setDentalHistoryUID(databaseReference.push().getKey());
//            Log.d(TAG, "addPatients: patient: " + patient);
//            databaseReference.child(keyUID).setValue(patient);
//            return true;
//        }
//        return false;
//    }

    public void addPatient(String firstname, String lastname, String middleInitial, String address,
                           String phoneNumber, int civilStatus, int age, String occupation) {
        String patientUID = databaseReference.push().getKey();

        ArrayList<String> operationKeys = new ArrayList<>();
        operationKeys.add(NEW_PATIENT);

        Patient patient = new Patient(
                patientUID,
                firstname,
                lastname,
                middleInitial,
                phoneNumber,
                address,
                civilStatus,
                age,
                occupation,
                new Date(),
                operationKeys
        );

        if (patientUID != null) databaseReference.child(patientUID).setValue(patient);
    }

    public void updatePatient() {
        // TODO: Update Patients here
        // Use notifyItemRangeChanged or notifyItemChanged
    }

    public void removePatient() {
        // TODO: Remove Patients here
    }

    public void addProcedureKey(Patient patient, String procedureKey) {
        patient.addProcedure(procedureKey);
        databaseReference.child(patient.getUid()).child(DENTAL_PROCEDURES).setValue(patient.getDentalProcedures());
    }

    public void removeProcedureKey(Patient patient, String procedureKey) {
        ArrayList<String> keys = patient.getDentalProcedures();

        if (keys.size() == 1) {

            keys.clear();
            keys.add(NEW_PATIENT);

        } else {

            int index = keys.indexOf(procedureKey);
            if (index == -1) {
                Log.e(TAG, "removeProcedureKey: no key found");
                return;
            }

            keys.remove(index);
        }

        databaseReference.child(patient.getUid()).child(DENTAL_PROCEDURES).setValue(keys);
    }

    public void removeValueEventListener() {
//        dataSource.removeValueEventListener(valueEventListener);
        databaseReference.removeEventListener(valueEventListener);
    }

    private boolean isDuplicate(Patient patient) {
        return personArrayList.contains(patient);
    }

    public ArrayList<Person> getPersonArrayList() {
        return personArrayList;
    }

    public boolean isPatientsLoaded() {
        return isPatientsLoaded;
    }

    public LiveData<Boolean> isGettingPatientsDone() {
        return isGettingPatientsDone;
    }
}
