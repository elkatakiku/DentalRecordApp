package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import android.widget.ListView;

import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.HistoryItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.adapter.OperationsList;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.patient.OperationRepository;

public class PatientInfoViewModel extends ViewModel {

    private OperationRepository repository;

    public PatientInfoViewModel(OperationRepository repository) {
        this.repository = repository;
    }

//    public OperationRepository getRepository() {
//        return repository;
//    }

    public void loadOperations(Patient patient, OperationsList operationsList) {
        repository.getOperations(patient, operationsList);
    }

    public void loadOperations(Patient patient, HistoryItemAdapter itemAdapter, ListView listOperations) {
        repository.getOperations(patient, itemAdapter, listOperations);
    }

    public double getBalance() {
        return repository.getBalance();
    }

//    public LiveData<Boolean> isOperationsLoaded() {
//        return repository.getmIsOperationsLoaded();
//    }
}
