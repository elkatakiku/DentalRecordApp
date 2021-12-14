package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PatientInfoViewModel extends ViewModel {
    private static final String TAG = PatientInfoViewModel.class.getSimpleName();

    private ProcedureRepository repository;
    private Patient patient;

    private final MutableLiveData<Double> mBalance = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoaded = new MutableLiveData<>();

    private final MutableLiveData<Integer> mProceduresCounter = new MutableLiveData<>();


    private int procedureSize;
    private int totalCount;
//    private ArrayList<Procedure> procedureList;
    private Procedure[] procedures;

    public PatientInfoViewModel(ProcedureRepository repository) {
        this.repository = repository;
    }

    public void loadOperations(Patient patient, LifecycleOwner lifecycleOwner) {
        Log.d(TAG, "loadOperations: called");

        ArrayList<String> operationKeys = patient.getDentalProcedures();
        procedureSize = patient.getDentalProcedures().size();
//        procedureList = new ArrayList<>(procedureSize);
        procedures = new Procedure[procedureSize];
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

                    Procedure procedure = snapshot.getValue(Procedure.class);

                    if (procedure != null) {

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

        mProceduresCounter.observe(lifecycleOwner, integer -> {
            if (integer == procedureSize)
                isLoaded.setValue(true);
            else isLoaded.setValue(false);
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

    public Procedure[] getProcedures() {
        return procedures;
    }
}
