package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
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

    public void remove(Patient patient, LoggedInUser loggedInUser, PatientsAdapterListener adapterListener) {
        // TODO: Remove Patients here
        ProcedureRepository repository = ProcedureRepository.getInstance();
        ArrayList<String> procedureKeys = patient.getDentalProcedures();

        for (String procedureKey : procedureKeys) {
            repository.removePaymentKeys(procedureKey);
        }

        AccountRepository accountRepository = AccountRepository.getInstance();
        accountRepository
                .getPath(patient.getAccountUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Account account = snapshot.getValue(Account.class);
                        if (account != null) {
                            accountRepository
                                    .signInWithEmail(account)
                                    .continueWith(task -> {
                                        if (task.isSuccessful() && task.getResult().getUser() != null) {
                                            task.getResult().getUser().delete();
                                            accountRepository
                                                    .getDatabaseReference()
                                                    .child(account.getUid())
                                                    .removeValue();
                                            accountRepository
                                                    .signInWithEmail(loggedInUser.getAccount())
                                                    .continueWith(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            remove(patient.getUid());
                                                            addListener(adapterListener);
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
//        databaseReference.child(patient.getUid()).removeValue();
    }

    private void addListener(ValueEventListener eventListener) {
        getPatientsPath().addValueEventListener(eventListener);
    }

    public void addProcedureKey(Patient patient, String procedureKey) {
        patient.addProcedure(procedureKey);
        databaseReference.child(patient.getUid()).child(DENTAL_PROCEDURES).setValue(patient.getDentalProcedures());
    }

    public Task<Void> removeProcedureKey(Patient patient, String procedureKey) {
        Log.d(TAG, "removeProcedureKey: removing procedure key");
        ArrayList<String> keys = patient.getDentalProcedures();
        Log.d(TAG, "removeProcedureKey: procedure key sent: " + procedureKey);

        for (String key : keys) {
            Log.d(TAG, "removeProcedureKey: key: " + key);
        }

        int index = keys.indexOf(procedureKey);
        if (index == -1) {
            Log.e(TAG, "removeProcedureKey: no key found");
            return null;
        }

        keys.remove(index);

        return databaseReference.child(patient.getUid()).child(DENTAL_PROCEDURES).setValue(keys);
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

        Log.d(TAG, "initialize: initializing patient: " + patient.getLastname());

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
            contact.add(NEW_PATIENT);
            patient.setPhoneNumber(contact);
        }

        if (!Checker.isDataAvailable(patient.getOccupation()))
            patient.setOccupation(Checker.NOT_AVAILABLE);

        if (patient.getDentalProcedures() == null) {
            patient.setDentalProcedures(new ArrayList<>());
        }

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
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    public static class PatientsAdapterListener implements ValueEventListener {

        private final ItemAdapter adapter;
        private final MutableLiveData<Boolean> hasPatients;

        public PatientsAdapterListener(ItemAdapter adapter, MutableLiveData<Boolean> hasPatients) {
            this.adapter = adapter;
            this.hasPatients = hasPatients;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: data changed in all patient list");
//            final ArrayList<Person> patients = new ArrayList<>();

            adapter.clearAll();
            adapter.notifyDataSetChanged();

            hasPatients.setValue(snapshot.getChildrenCount() > 0);

            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                Patient patient = dataSnapshot.getValue(Patient.class);

                if (patient == null) {
                    continue;
                }

                initialize(patient);
                Log.d(TAG, "onDataChange: adding patient: " + patient.getLastname());
                adapter.addItem(patient);
                adapter.notifyItemInserted(adapter.getItemCount());
            }
            adapter.initializeOrigList();
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
