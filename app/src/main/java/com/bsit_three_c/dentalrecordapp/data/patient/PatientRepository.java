package com.bsit_three_c.dentalrecordapp.data.patient;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
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


    private ArrayList<Person> personArrayList;
    private boolean isPatientsLoaded = false;
    private final MutableLiveData<Boolean> isGettingPatientsDone = new MutableLiveData<>();

    private ItemAdapter adapter;

    private PatientRepository() {
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference(USERS_REFERENCE);
    }

    public static PatientRepository getInstance() {
        if (instance == null) {
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

                }
                else Log.d(TAG, "getPatients: Patient already in array list");
            }
        }

        isPatientsLoaded = true;
        return this.personArrayList;
    }

    public void getPatients() {
        databaseReference.addValueEventListener(valueEventListener);
    }

//    public void setValueEventListener() {
//        databaseReference.addValueEventListener(valueEventListener);
//    }

    public void setAdapter(ItemAdapter adapter) {
        this.adapter = adapter;
    }

    private final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: data changed");

            isGettingPatientsDone.setValue(false);
            adapter.setItems(getPatients(snapshot));
            adapter.notifyDataSetChanged();

            isGettingPatientsDone.setValue(true);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


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
                UIUtil.stringToDate(UIUtil.getDate(new Date())),
                operationKeys
        );

        if (patientUID != null) databaseReference.child(patientUID).setValue(patient);
    }

    public void updatePatient() {
        // TODO: Update Patients here
        // Use notifyItemRangeChanged or notifyItemChanged
    }

    public void removePatient(Patient patient) {

        // TODO: Remove Patients here
        ProcedureRepository repository = ProcedureRepository.getInstance();
        ArrayList<String> procedureKeys = patient.getDentalProcedures();

        for (int pos = 0; pos < procedureKeys.size(); pos++) {
            repository.removePaymentKeys(procedureKeys.get(pos));
        }

        databaseReference.child(patient.getUid()).removeValue();
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
