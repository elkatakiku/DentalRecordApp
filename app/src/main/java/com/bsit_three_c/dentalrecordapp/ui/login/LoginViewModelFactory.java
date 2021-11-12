package com.bsit_three_c.dentalrecordapp.ui.login;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.bsit_three_c.dentalrecordapp.SampleViewModel;
import com.bsit_three_c.dentalrecordapp.data.LoginDataSource;
import com.bsit_three_c.dentalrecordapp.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {
    
    private final LoginDataSource loginDataSource = new LoginDataSource();

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance(loginDataSource));
        } else if (modelClass.isAssignableFrom(SampleViewModel.class)){
            return (T) new SampleViewModel(LoginRepository.getInstance(loginDataSource));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}