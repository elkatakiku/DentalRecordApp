package com.bsit_three_c.dentalrecordapp.data.view_model_factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.patient.OperationRepository;
import com.bsit_three_c.dentalrecordapp.data.patient.PatientRepository;
import com.bsit_three_c.dentalrecordapp.ui.add_patient.AddPatientViewModel;
import com.bsit_three_c.dentalrecordapp.ui.home.HomeViewModel;
import com.bsit_three_c.dentalrecordapp.ui.patient_info.OperationViewModel;

public class PatientViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override

    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        if (aClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(PatientRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(AddPatientViewModel.class)) {
            return (T) new AddPatientViewModel(PatientRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(OperationViewModel.class)) {
            return (T) new OperationViewModel(OperationRepository.getInstance());
        }
        else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}