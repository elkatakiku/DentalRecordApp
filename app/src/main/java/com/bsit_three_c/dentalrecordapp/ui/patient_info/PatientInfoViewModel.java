package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.patient.OperationRepository;

public class PatientInfoViewModel extends ViewModel {

    private OperationRepository repository;

    public PatientInfoViewModel(OperationRepository repository) {
        this.repository = repository;
    }

    public OperationRepository getRepository() {
        return repository;
    }

//    public LiveData<Boolean> isOperationsLoaded() {
//        return repository.getmIsOperationsLoaded();
//    }
}
