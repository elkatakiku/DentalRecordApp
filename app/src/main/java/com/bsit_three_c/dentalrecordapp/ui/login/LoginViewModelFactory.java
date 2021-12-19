package com.bsit_three_c.dentalrecordapp.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.MainViewModel;
import com.bsit_three_c.dentalrecordapp.data.login.LoginDataSource;
import com.bsit_three_c.dentalrecordapp.data.login.LoginRepository;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.MainAdminViewModel;

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
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(LoginRepository.getInstance(loginDataSource));
        } else if (modelClass.isAssignableFrom(MainAdminViewModel.class)) {
            return (T) new MainAdminViewModel(LoginRepository.getInstance(loginDataSource));
        }
        else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}