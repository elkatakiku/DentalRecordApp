package com.bsit_three_c.dentalrecordapp.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.repository.LoginRepository;
import com.bsit_three_c.dentalrecordapp.ui.main.MainAdminViewModel;
import com.bsit_three_c.dentalrecordapp.ui.menu.MenuAdminViewModel;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class LoginViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance());
        } else if (modelClass.isAssignableFrom(MainAdminViewModel.class)) {
            return (T) new MainAdminViewModel(LoginRepository.getInstance());
        }
        else if ((modelClass.isAssignableFrom(MenuAdminViewModel.class))) {
            return (T) new MenuAdminViewModel(LoginRepository.getInstance());
        }
        else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}