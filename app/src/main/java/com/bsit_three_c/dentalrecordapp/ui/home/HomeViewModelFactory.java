package com.bsit_three_c.dentalrecordapp.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.patient.PatientDataSource;
import com.bsit_three_c.dentalrecordapp.data.patient.PatientRepository;

class HomeViewModelFactory implements ViewModelProvider.Factory {

    private final PatientDataSource dataSource = new PatientDataSource();

    @NonNull
    @Override

    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        if (aClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(PatientRepository.getInstance(dataSource));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}