package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.DentalProcedure;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.patient.ProcedureRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientInfoViewModel extends ViewModel {
    private static final String TAG = PatientInfoViewModel.class.getSimpleName();

    private ProcedureRepository repository;
    private Patient patient;

//    private final MutableLiveData<DentalProcedure[]> mOperations = new MutableLiveData<>();

    private final MutableLiveData<Double> mBalance = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoaded = new MutableLiveData<>();

    private final MutableLiveData<Integer> mProceduresCounter = new MutableLiveData<>();


    private int procedureSize;
    private int totalCount;
    private ArrayList<DentalProcedure> procedureList;
    private DentalProcedure[] procedures;

    public PatientInfoViewModel(ProcedureRepository repository) {
        this.repository = repository;
//        this.mBalance.setValue(0d);
    }

    public void loadOperations(Patient patient, LifecycleOwner lifecycleOwner) {

        ArrayList<String> operationKeys = patient.getDentalProcedures();
        procedureSize = patient.getDentalProcedures().size();

        procedureList = new ArrayList<>(procedureSize);
//        Log.d(TAG, "loadOperations: keys: " + operationKeys);

        procedures = new DentalProcedure[procedureSize];
//        Log.d(TAG, "loadOperations: procedure size: " + procedureSize);
//        Log.d(TAG, "loadOperations: list size: " + procedures.length);

        isLoaded.setValue(false);

        mBalance.setValue(0d);

        mProceduresCounter.setValue(0);
        totalCount = 0;

        //  Loop through the firebase children if it has any
        for (int position = 0; position < operationKeys.size(); position++) {

            int finalPosition = position;
            repository.getProcedures(operationKeys.get(position))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DentalProcedure procedure = snapshot.getValue(DentalProcedure.class);

                    if (procedure != null) {

                        //  Assignes the procedure in the array in their respective order
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

        mProceduresCounter.observe(lifecycleOwner, integer -> {
            if (integer == procedureSize)
                isLoaded.setValue(true);
            else isLoaded.setValue(false);
            Log.d(TAG, "loadOperations: integer and size: " + (integer == procedureSize));
        });
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

    public DentalProcedure[] getProcedures() {
        return procedures;
    }
}
