package com.bsit_three_c.dentalrecordapp.ui.employees.employee_form;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

import java.util.Arrays;

public class EmergencyContactFormFragment extends Fragment {
    private static final String TAG = EmergencyContactFormFragment.class.getSimpleName();

    private FragmentFormEmployee2Binding binding;
    private EmergencyContactFormViewModel viewModel;
    private EmployeeSharedViewModel sharedViewModel;

    private Employee employee;
//    private Account account;
    private EmergencyContact emergencyContact;
    private byte[] imageByte;

    boolean isEdit;

    private final Observer<Integer> errorObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer integer) {
            Log.d(TAG, "onChanged: data changed");
            if (integer != Checker.VALID && integer != 0) {
                Snackbar
                        .make(binding.btnEmployeeConfirm, integer, Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    };

    private final Observer<Boolean> addingEmployee = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {

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
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFormEmployee2Binding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(EmergencyContactFormViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(EmployeeSharedViewModel.class);

        sharedViewModel.getmEdit().observe(getViewLifecycleOwner(), aBoolean -> {
            isEdit = aBoolean;
            binding.layoutEmployeeAccountForm.setVisibility(View.GONE);
        });

        sharedViewModel.getmImageByte().observe(getViewLifecycleOwner(), bytes -> {
            if (bytes != null) {
                Log.d(TAG, "onChanged: sent bytes: " + Arrays.toString(bytes));
                imageByte = bytes;
            }
        });

        //  Listens to employee changed and creates new account if new employee.
        sharedViewModel.getmUpdatedEmployee().observe(getViewLifecycleOwner(), mEmployee -> {
            Log.d(TAG, "onChanged: employee sent: " + mEmployee);
            employee = mEmployee;
            if (employee.getAccountUid() == null || employee.getAccountUid().isEmpty()) {
                Log.d(TAG, "onCreateView: creating account for new employee");
                initializeAccountFields(employee.getEmail(), UIUtil.getDefaultPassword(employee));
//                sharedViewModel.setmAccount(createAccount(employee));
            }
        });

//        sharedViewModel.getmAccount().observe(getViewLifecycleOwner(), mAccount -> {
//            if (mAccount != null) {
//                Log.d(TAG, "onChanged: account sent: " + mAccount);
//                account = mAccount;
//                initializeAccountFields(mAccount);
//            }
//
//            Log.d(TAG, "onCreateView: got account: " + account);
//        });

        sharedViewModel.getmEmergencyContact().observe(getViewLifecycleOwner(), mEmergencyContact -> {
            Log.d(TAG, "onChanged: contact changed");
            if (mEmergencyContact != null) {
                Log.d(TAG, "onCreateView: sent contact: " + mEmergencyContact);
                emergencyContact = mEmergencyContact;
                initializeFields(mEmergencyContact);
            }

            Log.d(TAG, "onChanged: got contact: " + emergencyContact);
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getmError().observe(getViewLifecycleOwner(), errorObserver);
        viewModel.getAddEmployeeAttempt().observe(getViewLifecycleOwner(), addingEmployee);

        binding.btnEmployeeConfirm.setOnClickListener(view1 -> {
            binding.pbEmployee2Loading.setVisibility(View.VISIBLE);
            setFieldsEnabled(false);

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

//    private Account createAccount(Employee employeeData) {
//        return new Account(
//                employeeData.getEmail(),
//                Account.getDefaultPassword(employeeData.getLastname()),
//                Account.TYPE_EMPLOYEE,
//                employeeData.getAccountUid()
//        );
//    }

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

        Log.d(TAG, "onPause: called");

        if (isEdit) {
            sharedViewModel.setmEmergencyContact(createUpdatedEmergencyContact(emergencyContact));
        } else {
            sharedViewModel.setmEmergencyContact(createEmergencyContact());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView: called");
        binding = null;
        sharedViewModel.removeListeners(employee);
    }
}