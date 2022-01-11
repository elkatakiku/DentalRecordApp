package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PatientRepository extends BaseRepository {
    private static final String TAG = PatientRepository.class.getSimpleName();

    private static volatile PatientRepository instance;

    public PatientRepository() {
        super(PATIENTS_REFERENCE);
    }

    public static PatientRepository getInstance() {
        if (instance == null) {
            instance = new PatientRepository();
        }
        return instance;
    }

    public Query getPatientsPath() {
        return databaseReference.orderByChild("lastname");
    }

    public Query getPatientsByAccount(String accountUid) {
        return databaseReference.orderByChild(AccountRepository.ACCOUNT_UID_PATH).equalTo(accountUid);
    }

    public Task<Void> upload(Patient patient) {
        return databaseReference.child(patient.getUid()).setValue(patient);
    }

    public void remove(Patient patient, LoggedInUser loggedInUser) {
        // TODO: Remove Patients here
        ProcedureRepository repository = ProcedureRepository.getInstance();
        ArrayList<String> procedureKeys = patient.getDentalProcedures();

        for (int pos = 0; pos < procedureKeys.size(); pos++) {
            repository.removePaymentKeys(procedureKeys.get(pos));
        }

        AccountRepository.getInstance().removeAccount(loggedInUser, patient.getAccountUid());
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

    public void checkInFile(MutableLiveData<Boolean> mIsInFile, String patientUid) {
        getPath(patientUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mIsInFile.setValue(snapshot.getValue(Patient.class) != null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void initialize(Patient patient) {

        Log.d(TAG, "initialize: initializing patient: " + patient);

        if (!Checker.isDataAvailable(patient.getFirstname()))
            patient.setFirstname(Checker.NOT_AVAILABLE);

        if (!Checker.isDataAvailable(patient.getLastname()))
            patient.setLastname(Checker.NOT_AVAILABLE);

        if (!Checker.isDataAvailable(patient.getMiddleInitial()))
            patient.setMiddleInitial(Checker.NOT_AVAILABLE);

        if (!Checker.isDataAvailable(patient.getSuffix()))
            patient.setSuffix(Checker.NOT_AVAILABLE);

        if (!Checker.isDataAvailable(patient.getDateOfBirth())) {
            patient.setDateOfBirth(Checker.NOT_AVAILABLE);
        }

        if (!Checker.isDataAvailable(patient.getAddress()))
            patient.setAddress(Checker.NOT_AVAILABLE);

        if (patient.getPhoneNumber() == null) {
            ArrayList<String> contact = new ArrayList<>();
            contact.add(FirebaseHelper.NEW_PATIENT);
            patient.setPhoneNumber(contact);
        }

        if (!Checker.isDataAvailable(patient.getOccupation()))
            patient.setOccupation(Checker.NOT_AVAILABLE);

        if (patient.getDentalProcedures() == null)
            patient.setDentalProcedures(new ArrayList<>());

        if (patient.getLastUpdated() == null)
            patient.setLastUpdated(new Date());

        if (!Checker.isDataAvailable(patient.getEmail())) {
            patient.setEmail(Checker.NOT_AVAILABLE);
        }
    }

    public static class PatientsListener implements ValueEventListener {

        private final MutableLiveData<List<Person>> mPatients;

        public PatientsListener(MutableLiveData<List<Person>> mPatients) {
            this.mPatients = mPatients;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: data changed in all patient list");
            final ArrayList<Person> patients = new ArrayList<>();

            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                Patient patient = dataSnapshot.getValue(Patient.class);

                if (patient == null) {
                    continue;
                }

                initialize(patient);
                patients.add(patient);
            }

            mPatients.setValue(patients);

//            getPatients(snapshot);
//            int counter = 0;
//
//            if (itemCount != 0 && itemCount == count){
//                Log.d(TAG, "getPatients: true");
//                adapter.clearAll();
//            }
//
//            this.personArrayList = new ArrayList<>();
//            for (DataSnapshot data : dataSnapshot.getChildren()) {
//                Patient patient = data.getValue(Patient.class);
//
//                if (patient != null && !isDuplicate(patient)) {
//
//                    patient.setUid(data.getKey());
//                    initialize(patient);
//                    adapter.addItem(patient);
//
//                    counter++;
//
//                }
//            }
//
//            adapter.initializeOrigList();
//
//            count = counter;
//            isPatientsLoaded = true;
//
//            isGettingPatientsDone.setValue(true);
//            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    public static class PatientListener implements ValueEventListener {

        private final MutableLiveData<Patient> mPatient;

        public PatientListener(MutableLiveData<Patient> mPatient) {
            this.mPatient = mPatient;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Patient patient = snapshot.getValue(Patient.class);

            if (patient != null) {
                initialize(patient);
            }

            mPatient.setValue(patient);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }
}
