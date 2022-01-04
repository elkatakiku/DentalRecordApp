package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientRepository {

    private static final String TAG = PatientRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;

    private static volatile PatientRepository instance;

    private ArrayList<Person> personArrayList;
    private boolean isPatientsLoaded = false;
    private final MutableLiveData<Boolean> isGettingPatientsDone = new MutableLiveData<>();
    private final MutableLiveData<Patient> mPatient = new MutableLiveData<>();

    private final FirebaseHelper.CountChildren countedChildren = new FirebaseHelper.CountChildren();;
    private ItemAdapter adapter;
    private long count;

    private PatientRepository() {
        this.database = FirebaseDatabase.getInstance(FirebaseHelper.FIREBASE_URL);
        this.databaseReference = database.getReference(FirebaseHelper.PATIENTS_REFERENCE);
    }

    public static PatientRepository getInstance() {
        if (instance == null) {
            instance = new PatientRepository();
        }
        return instance;
    }

    public void loadPatient(String patientUID) {
        Log.d(TAG, "loadPatient: adding listener");
        databaseReference.child(patientUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: data changed in patient");

                Patient gotPatient = snapshot.getValue(Patient.class);

                if (gotPatient != null) {
                    Log.d(TAG, "onDataChange: has patient");
                    mPatient.setValue(gotPatient);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPatients(DataSnapshot dataSnapshot) {

        int counter = 0;

        if (adapter.getItemCount() != 0 && adapter.getItemCount() == count){
            adapter.clearAll();
        }

        if (dataSnapshot != null) {
            this.personArrayList = new ArrayList<>();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                Patient patient = data.getValue(Patient.class);

                if (patient != null && !isDuplicate(patient)) {

                    patient.setUid(data.getKey());
                    initialize(patient);
                    adapter.addItem(patient);

                    counter++;

                }
            }
        }


        count = counter;
        isPatientsLoaded = true;
    }

    public LiveData<Patient> getPatient() {
        Log.d(TAG, "getPatient: called");
        return mPatient;
    }

    public void setAdapter(ItemAdapter adapter) {
        this.adapter = adapter;
    }

    public void getPatients() {
        databaseReference.orderByChild("lastname").addValueEventListener(valueEventListener);
    }

    private final ValueEventListener valueEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: data changed in all patient list");
            isGettingPatientsDone.setValue(false);

            getPatients(snapshot);

            isGettingPatientsDone.setValue(true);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    public Patient add(String firstname,
                    String lastname,
                    String middleInitial,
                    String suffix,
                    String dateOfBirth,
                    String address,
                    List<String> phoneNumber,
                    int civilStatus,
                    int age,
                    String occupation) {
        String patientUID = databaseReference.push().getKey();

        ArrayList<String> operationKeys = new ArrayList<>();
        operationKeys.add(FirebaseHelper.NEW_PATIENT);

        if (phoneNumber.size() <= 0) phoneNumber.add(FirebaseHelper.NEW_PATIENT);

        Patient patient = new Patient(
                patientUID,
                firstname,
                lastname,
                middleInitial,
                suffix,
                dateOfBirth,
                phoneNumber,
                address,
                civilStatus,
                age,
                occupation,
                DateUtil.convertToDate(DateUtil.getDate(new Date())),
                operationKeys,
                "email"
        );

        if (patientUID != null) {
            databaseReference.child(patientUID).setValue(patient);
            return patient;
        }

        return null;
    }

    public void update(Patient patient) {
        // TODO: Update Patients here
        // Use notifyItemRangeChanged or notifyItemChanged
        databaseReference.child(patient.getUid()).setValue(patient);

    }

    public void remove(Patient patient) {

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
        databaseReference.child(patient.getUid()).child(FirebaseHelper.DENTAL_PROCEDURES).setValue(patient.getDentalProcedures());
    }

    public void removeProcedureKey(Patient patient, String procedureKey) {

        ArrayList<String> keys = patient.getDentalProcedures();

            int index = keys.indexOf(procedureKey);
            if (index == -1) {
                Log.e(TAG, "removeProcedureKey: no key found");
                return;
            }

            keys.remove(index);

        databaseReference.child(patient.getUid()).child(FirebaseHelper.DENTAL_PROCEDURES).setValue(keys);
    }

    public void countPatients() {
        databaseReference.addValueEventListener(countedChildren);
    }

    public LiveData<Long> getPatientCount() {
        return countedChildren.getCount();
    }

    private void initialize(Patient patient) {

        Log.d(TAG, "initialize: initializing patient: " + patient);

        final String notAvailable = "N/A";

        if (!Checker.isDataAvailable(patient.getFirstname()))
            patient.setFirstname(notAvailable);

        if (!Checker.isDataAvailable(patient.getLastname()))
            patient.setLastname(notAvailable);

        if (!Checker.isDataAvailable(patient.getMiddleInitial()))
            patient.setMiddleInitial(notAvailable);

        if (!Checker.isDataAvailable(patient.getSuffix()))
            patient.setSuffix(notAvailable);

        if (!Checker.isDataAvailable(patient.getDateOfBirth())) {
            patient.setDateOfBirth(notAvailable);
        }

        if (!Checker.isDataAvailable(patient.getAddress()))
            patient.setAddress(notAvailable);

        if (patient.getPhoneNumber() == null) {
            ArrayList<String> contact = new ArrayList<>();
            contact.add(FirebaseHelper.NEW_PATIENT);
            patient.setPhoneNumber(contact);
        }

        if (!Checker.isDataAvailable(patient.getOccupation()))
            patient.setOccupation(notAvailable);

        if (patient.getDentalProcedures() == null)
            patient.setDentalProcedures(new ArrayList<>());

        if (patient.getLastUpdated() == null)
            patient.setLastUpdated(new Date());

        if (!Checker.isDataAvailable(patient.getEmail())) {
            patient.setEmail(Person.NOT_AVAILABLE);
        }
    }

    public void removeValueEventListener() {
        databaseReference.removeEventListener(valueEventListener);
        databaseReference.removeEventListener(countedChildren);
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
