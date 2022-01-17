package com.bsit_three_c.dentalrecordapp.ui.employees.employee_form;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.EmergencyContact;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormEmployee2Binding;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;

public class EmergencyContactFormFragment extends Fragment {

    private static final String EMERGENCY_ID_KEY = "ARG_ACF_EMERGENCY_ID_KEY";

    private FragmentFormEmployee2Binding binding;
    private EmergencyContactFormViewModel viewModel;
    private EmployeeSharedViewModel sharedViewModel;

    private Employee employee;
    private EmergencyContact emergencyContact;
    private byte[] imageByte;

    boolean isEdit;

    public static EmergencyContactFormFragment newInstance(String emergencyUid) {
        Bundle arguments = new Bundle();
        arguments.putString(EMERGENCY_ID_KEY, emergencyUid);
        EmergencyContactFormFragment formFragment = new EmergencyContactFormFragment();
        formFragment.setArguments(arguments);
        return formFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFormEmployee2Binding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(EmergencyContactFormViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(EmployeeSharedViewModel.class);

        if (getArguments() != null) {
            viewModel.setmEmergencyUid(getArguments().getString(EMERGENCY_ID_KEY));
        }

        sharedViewModel.getmEdit().observe(getViewLifecycleOwner(), aBoolean -> {
            isEdit = aBoolean;
            binding.layoutEmployeeAccountForm.setVisibility(View.GONE);
        });

        sharedViewModel.getmImageByte().observe(getViewLifecycleOwner(), bytes -> {
            if (bytes != null) {
                imageByte = bytes;
            }
        });

        //  Listens to employee changed and creates new account if new employee.
        sharedViewModel.getmUpdatedEmployee().observe(getViewLifecycleOwner(), mEmployee -> {
            employee = mEmployee;
            if (employee.getAccountUid() == null || employee.getAccountUid().isEmpty()) {
                initializeAccountFields(employee.getEmail(), UIUtil.getDefaultPassword(employee));
            }
        });

        sharedViewModel.getmEmergencyContact().observe(getViewLifecycleOwner(), mEmergencyContact -> {
            if (mEmergencyContact != null) {
                emergencyContact = mEmergencyContact;
                initializeFields(mEmergencyContact);
            }
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getmEmergencyUid().observe(getViewLifecycleOwner(), s -> {
            if (s != null && !s.isEmpty()) {
                binding.layoutEmployeeAccountForm.setVisibility(View.GONE);
                viewModel.getEmergency(s);
            }
        });

        viewModel.getmEmergencyContact().observe(getViewLifecycleOwner(), emergencyContact -> {
            if (emergencyContact != null) {
                initializeFields(emergencyContact);
            } else {
                requireActivity().finish();
            }
        });

        viewModel.getmError().observe(getViewLifecycleOwner(), integer -> {
            if (integer != Checker.VALID && integer != 0) {
                Snackbar
                        .make(binding.btnEmployeeConfirm, integer, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        viewModel.getAddEmployeeAttempt().observe(getViewLifecycleOwner(), aBoolean -> {

            if (aBoolean) {
                if (viewModel.getmError().getValue() != null && viewModel.getmError().getValue() == Checker.VALID) {
                    requireActivity()
                            .setResult(
                                    Activity.RESULT_OK,
                                    new Intent().putExtra(getString(R.string.EMPLOYEE), employee)
                            );

                    requireActivity().finish();
                }

                binding.pbEmployee2Loading.setVisibility(View.GONE);
                setFieldsEnabled(true);
            }
        });

        binding.btnEmployeeConfirm.setOnClickListener(view1 -> {
            binding.pbEmployee2Loading.setVisibility(View.VISIBLE);
            setFieldsEnabled(false);

            if (viewModel.getmEmergencyContact().getValue() != null) {
                viewModel.uploadEmergency(createUpdatedEmergencyContact(viewModel.getmEmergencyContact().getValue()));
                return;
            }

            if (isEdit) {
                emergencyContact = createUpdatedEmergencyContact(emergencyContact);
            } else {
                emergencyContact = createEmergencyContact();
            }

            viewModel.uploadEmployee(
                    LocalStorage.getLoggedInUser(requireContext()),
                    imageByte,
                    employee,
                    binding.etEmployeeAccountEmail.getText().toString().trim(),
                    binding.etEmployeeAccountPassword.getText().toString().trim(),
                    emergencyContact,
                    isEdit
            );
        });
    }

    private void initializeAccountFields(String email, String password) {
        UIUtil.setField(email, binding.etEmployeeAccountEmail);
        UIUtil.setField(password, binding.etEmployeeAccountPassword);
    }

    private void initializeFields(EmergencyContact emergencyContactData) {
        UIUtil.setField(emergencyContactData.getFirstname(), binding.etEmergencyFirstname);
        UIUtil.setField(emergencyContactData.getLastname(), binding.etEmergencyLastname);
        UIUtil.setField(emergencyContactData.getMiddleInitial(), binding.etEmergencyMI);
        UIUtil.setField(emergencyContactData.getSuffix(), binding.etEmergencySuffix);
        UIUtil.setField(emergencyContactData.getAddress(), binding.etEmergencyAddress1);
        UIUtil.setField(emergencyContactData.getAddress2ndPart(), binding.etEmergencyAddress2);
        UIUtil.setField(emergencyContactData.getContactNumber(), binding.etEmergencyNumber);
    }

    private EmergencyContact createEmergencyContact() {
        return viewModel.createEmergencyContact(
                employee,
                binding.etEmergencyFirstname.getText().toString().trim(),
                binding.etEmergencyLastname.getText().toString().trim(),
                binding.etEmergencyMI.getText().toString().trim(),
                binding.etEmergencySuffix.getText().toString().trim(),
                binding.etEmergencyAddress1.getText().toString().trim(),
                binding.etEmergencyAddress2.getText().toString().trim(),
                binding.etEmergencyNumber.getText().toString().trim()
        );
    }

    private EmergencyContact createUpdatedEmergencyContact(EmergencyContact emergencyContact) {
        return viewModel.updateEmergencyContact(
                emergencyContact,
                binding.etEmergencyFirstname.getText().toString().trim(),
                binding.etEmergencyLastname.getText().toString().trim(),
                binding.etEmergencyMI.getText().toString().trim(),
                binding.etEmergencySuffix.getText().toString().trim(),
                binding.etEmergencyAddress1.getText().toString().trim(),
                binding.etEmergencyAddress2.getText().toString().trim(),
                binding.etEmergencyNumber.getText().toString().trim()
        );
    }

    private void setFieldsEnabled(boolean enabled) {
        binding.etEmployeeAccountEmail.setEnabled(enabled);
        binding.etEmployeeAccountPassword.setEnabled(enabled);
        binding.etEmergencyFirstname.setEnabled(enabled);
        binding.etEmergencyLastname.setEnabled(enabled);
        binding.etEmergencyMI.setEnabled(enabled);
        binding.etEmergencySuffix.setEnabled(enabled);
        binding.etEmergencyAddress1.setEnabled(enabled);
        binding.etEmergencyAddress2.setEnabled(enabled);
        binding.etEmergencyNumber.setEnabled(enabled);
        binding.btnEmployeeConfirm.setEnabled(enabled);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!viewModel.isUpdate()) {
            if (isEdit) {
                sharedViewModel.setmEmergencyContact(createUpdatedEmergencyContact(emergencyContact));
            } else {
                sharedViewModel.setmEmergencyContact(createEmergencyContact());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        if (viewModel.isUpdate()) {
            viewModel.removeListeners();
        } else {
            sharedViewModel.removeListeners(employee);
        }
    }
}