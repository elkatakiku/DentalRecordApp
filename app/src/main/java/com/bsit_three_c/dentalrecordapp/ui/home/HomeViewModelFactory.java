package com.bsit_three_c.dentalrecordapp.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.add_patient.AddPatientDataSource;
import com.bsit_three_c.dentalrecordapp.data.add_patient.AddPatientRepository;

class HomeViewModelFactory implements ViewModelProvider.Factory {

    private final AddPatientDataSource dataSource = new AddPatientDataSource();

    @NonNull
    @Override

    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        if (aClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(AddPatientRepository.getInstance(dataSource));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}