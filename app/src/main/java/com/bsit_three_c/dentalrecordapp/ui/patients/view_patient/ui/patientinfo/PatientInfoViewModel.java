package com.bsit_three_c.dentalrecordapp.ui.patients.view_patient.ui.patientinfo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.ProceduresList;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceOptionsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.listeners.ServicesEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientInfoViewModel extends ViewModel {
    private static final String TAG = PatientInfoViewModel.class.getSimpleName();

    private final PatientRepository patientRepository;
    private final ProcedureRepository procedureRepository;
    private final ServiceRepository serviceRepository;
    private Patient patient;

    private final MutableLiveData<Double> mBalance = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mGotProcedures = new MutableLiveData<>();
    private final MutableLiveData<Integer> mProceduresCounter = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<DentalService>> mDentalServices = new MutableLiveData<>();

    private final ArrayList<DentalServiceOption> serviceOptions = new ArrayList<>();


    private int procedureSize;
    private int totalCount;
    private Procedure[] procedures;

    public PatientInfoViewModel(PatientRepository patientRepository, ProcedureRepository procedureRepository, ServiceRepository serviceRepository) {
        this.patientRepository = patientRepository;
        this.procedureRepository = procedureRepository;
        this.serviceRepository = serviceRepository;

        serviceOptions.add(new DentalServiceOption(ServiceOptionsAdapter.DEFAULT_OPTION, ServiceOptionsAdapter.DEFAULT_OPTION, false));
    }

    private void loadServices() {
        Log.d(TAG, "loadServices: called");
        serviceRepository.getServicesPath().addListenerForSingleValueEvent(new ServicesEventListener(mDentalServices));
    }

    public LiveData<ArrayList<DentalService>> getmDentalServices() {
        return mDentalServices;
    }

    public void setServicesOptions(ArrayList<DentalService> dentalServices) {
        serviceRepository.setServicesOptions(dentalServices, serviceOptions);
    }

    public List<DentalServiceOption> getServiceOptions() {
        return serviceOptions;
    }

    private final MutableLiveData<Boolean> mHasProcedures = new MutableLiveData<>();

    public LiveData<Boolean> hasProcedures() {
        return mHasProcedures;
    }

    public void loadProcedures(Patient patient, ProceduresList list) {
        Log.d(TAG, "loadProcedures: getting procedures");
        mHasProcedures.setValue(patient.getDentalProcedures().size() != 0);
        for (String key : patient.getDentalProcedures()) {
            Log.d(TAG, "loadProcedures: getting procedure key: " + key);
            procedureRepository
                    .getPath(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Procedure procedure = snapshot.getValue(Procedure.class);

                            if (procedure != null) {
                                Log.d(TAG, "onDataChange: adding procedure: " + procedure.getUid());
                                ProcedureRepository.initialize(procedure);
                                list.addItem(procedure);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    public void loadProcedure(Patient patient) {
        Log.d(TAG, "loadOperations: called");

        ArrayList<String> operationKeys = patient.getDentalProcedures();
        procedureSize = patient.getDentalProcedures().size();
        procedures = new Procedure[procedureSize];
        mBalance.setValue(0d);
        mProceduresCounter.setValue(0);
        totalCount = 0;

        //  Loop through the firebase children if it has any
        for (int position = 0; position < operationKeys.size(); position++) {

            int finalPosition = position;
            procedureRepository
                    .getPath(operationKeys.get(position))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "onDataChange: getting procedure");

                    Procedure procedure = snapshot.getValue(Procedure.class);

                    if (procedure != null) {
                        procedureRepository.initialize(procedure);

                        Log.d(TAG, "onDataChange: procedure: " + procedure);

                        //  Assigns the procedure in the array in their respective order
                        procedures[finalPosition] = procedure;

                        // Update the counter after adding the procedure to array
                        mProceduresCounter.setValue(totalCount + 1);
                        totalCount++;

                        //  Computes balance
                        if (mBalance.getValue() != null)
                            mBalance.setValue(mBalance.getValue() + procedure.getDentalBalance());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private final MutableLiveData<Patient> mPatient = new MutableLiveData<>();

    public void loadPatient(String patientUID) {
        Log.d(TAG, "loadPatient: called");
        loadServices();
        patientRepository.getPath(patientUID).addValueEventListener(new PatientRepository.PatientListener(mPatient));
    }

    public MutableLiveData<Patient> getmPatient() {
        return mPatient;
    }

    public LiveData<Double> getmBalance() {
        return mBalance;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public boolean isPatientNull() {
        return patient == null;
    }

    public int getProcedureSize() {
        return procedureSize;
    }

    public LiveData<Integer> getmProceduresCounter() {
        return mProceduresCounter;
    }

    public Procedure[] getProcedures() {
        return procedures;
    }
}
