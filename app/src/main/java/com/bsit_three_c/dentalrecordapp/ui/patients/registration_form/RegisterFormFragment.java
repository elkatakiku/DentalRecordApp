package com.bsit_three_c.dentalrecordapp.ui.patients.registration_form;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormRegisterBinding;
import com.bsit_three_c.dentalrecordapp.ui.patients.patient_form.PatientFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.patients.patient_form.PatientFormViewModel;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;

public class RegisterFormFragment extends Fragment {
    private PatientFormViewModel viewModel;
    private FragmentFormRegisterBinding binding;

    public static RegisterFormFragment newInstance() {
        return new RegisterFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFormRegisterBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(PatientFormViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnRegisterNext.setOnClickListener(v -> {
            String email = binding.etPatientEmail.getText().toString().trim();
            String password = binding.etPatientPassword.getText().toString().trim();
            String repeatPassword = binding.etPatientRepeatPassword.getText().toString().trim();

            if (!isInputValid(email, password, repeatPassword)) {
                return;
            }

            viewModel.createAccount(email, password);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, PatientFormFragment.newInstance(null, null))
                    .addToBackStack(null)
                    .commit();
        });

        setListeners();
        setObservers();
    }

    private boolean isInputValid(String email, String password, String repeatPassword) {
        boolean isValid = true;
        if (email.isEmpty()) {
            binding.etPatientEmail.setError(getString(R.string.invalid_email));
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.etPatientPassword.setError(getString(R.string.invalid_empty_input));
            isValid = false;
        }

        if (password.length() < 6) {
            binding.etPatientPassword.setError(getString(R.string.invalid_password));
            isValid = false;
        }

        if (!password.equals(repeatPassword)) {
            binding.etPatientRepeatPassword.setError(getString(R.string.password_not_match));
            isValid = false;
        }

        return isValid;
    }

    private void setListeners() {
        binding.etPatientEmail.addTextChangedListener(
                new CustomTextWatcher(viewModel, binding.etPatientEmailLabel.getText().toString().trim()));
    }

    private void setObservers() {
        viewModel.getmEmail().observe(getViewLifecycleOwner(), new CustomObserver(
                binding.etPatientEmail, getResources()));
    }

}