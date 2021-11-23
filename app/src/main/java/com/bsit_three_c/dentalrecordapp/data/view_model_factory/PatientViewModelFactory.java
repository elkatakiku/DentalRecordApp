package com.bsit_three_c.dentalrecordapp.data.view_model_factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.patient.PatientDataSource;
import com.bsit_three_c.dentalrecordapp.data.patient.PatientRepository;
import com.bsit_three_c.dentalrecordapp.ui.add_patient.AddPatientViewModel;
import com.bsit_three_c.dentalrecordapp.ui.home.HomeViewModel;

public class PatientViewModelFactory implements ViewModelProvider.Factory {

    private final PatientDataSource dataSource = new PatientDataSource();

    @NonNull
    @Override

    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        if (aClass.isAssignableFrom(HomeViewModel.class)) {
//            return (T) new HomeViewModel(PatientRepository.getInstance(dataSource));
            return (T) new HomeViewModel(PatientRepository.getInstance());

        }
        else if (aClass.isAssignableFrom(AddPatientViewModel.class)) {
            return (T) new AddPatientViewModel(PatientRepository.getInstance());
        }
        else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}