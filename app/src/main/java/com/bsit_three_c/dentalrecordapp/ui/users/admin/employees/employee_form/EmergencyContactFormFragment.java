package com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.employee_form;

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
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.EmergencyContact;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormEmployee2Binding;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;

public class EmergencyContactFormFragment extends Fragment {
    private static final String TAG = EmergencyContactFormFragment.class.getSimpleName();

    private FragmentFormEmployee2Binding binding;
    private EmergenyContactFormViewModel viewModel;

    private Bundle arguments;
    private Employee employee;
    private Account account;
    private EmergencyContact emergencyContact;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: saved state: " + savedInstanceState);

        binding = FragmentFormEmployee2Binding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(EmergenyContactFormViewModel.class);

        arguments = EmergencyContactFormFragmentArgs.fromBundle(getArguments()).getEmployee();

        byte[] compressedImage = arguments.getByteArray(LocalStorage.IMAGE_BYTE_KEY);
        employee = arguments.getParcelable(LocalStorage.EMPLOYEE_KEY);
        Bundle savedState = arguments.getParcelable(getString(R.string.BUNDLE_KEY));

        if (compressedImage == null && employee == null) {
            requireActivity().onBackPressed();
            return binding.getRoot();
        }

        if (savedState != null) {
            emergencyContact = savedState.getParcelable(EmergencyContact.EMERGENCY_CONTACT_KEY);
        }

        createAccount(employee);

        arguments.putParcelable(Account.ACCOUNT_KEY, account);
        arguments.putParcelable(Account.LOGGED_ID, LocalStorage.getLoggedInUser(requireContext()));

        return binding.getRoot();
    }

    private void createAccount(Employee employeeData) {
        account = new Account(
                employeeData.getEmail(),
                Account.getDefaultPassword(employeeData.getLastname()),
                Account.TYPE_EMPLOYEE,
                employeeData.getAccountUid()
        );
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AccountRepository.getInstance().getmError().observe(getViewLifecycleOwner(), errorObserver);
        EmployeeRepository.getInstance().getIsDoneAddingEmployee().observe(getViewLifecycleOwner(), addingEmployee);

        binding.etEmployeeAccountEmail.setText(account.getEmail());
        binding.etEmployeeAccountPassword.setText(account.getPassword());

        binding.btnEmployeeConfirm.setOnClickListener(view1 -> {
            binding.pbEmployee2Loading.setVisibility(View.VISIBLE);
            
            String[] data = getUserInput();

            String firstName = data[0];
            String lastname= data[1];
            String middleInitial = data[2];
            String suffix = data[3];
            String address1 = data[4];
            String address2 = data[5];
            String contactNumber = data[6];

            Log.d(TAG, "onClick: user input: " +
                    "\nfirstname: " + firstName +
                    "\nlastname: " + lastname +
                    "\nmiddle initial: " + middleInitial +
                    "\nsuffix: " + suffix +
                    "\naddress: " + address1 + " " + address2 +
                    "\ncontact number: " + contactNumber);

            viewModel.addEmployee(
                    arguments,
                    firstName,
                    lastname,
                    middleInitial,
                    suffix,
                    address1,
                    address2,
                    contactNumber
            );
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        if (emergencyContact != null) {
            initializeFields();
        }
    }

    private String[] getUserInput() {
        String[] inputtedData = new String[7];
        inputtedData[0] = binding.etEmergencyFirstname.getText().toString().trim();
        inputtedData[1] = binding.etEmergencyLastname.getText().toString().trim();
        inputtedData[2] = binding.etEmergencyMI.getText().toString().trim();
        inputtedData[3] = binding.etEmergencySuffix.getText().toString().trim();
        inputtedData[4] = binding.etEmergencyAddress1.getText().toString().trim();
        inputtedData[5] = binding.etEmergencyAddress2.getText().toString().trim();
        inputtedData[6] = binding.etEmergencyNumber.getText().toString().trim();

        return inputtedData;
    }

    private void initializeFields() {
        UIUtil.setField(emergencyContact.getFirstname(), binding.etEmergencyFirstname);
        UIUtil.setField(emergencyContact.getLastname(), binding.etEmergencyLastname);
        UIUtil.setField(emergencyContact.getMiddleInitial(), binding.etEmergencyMI);
        UIUtil.setField(emergencyContact.getSuffix(), binding.etEmergencySuffix);
        UIUtil.setField(emergencyContact.getAddress(), binding.etEmergencyAddress1);
        UIUtil.setField(emergencyContact.getAddress2ndPart(), binding.etEmergencyAddress2);
        UIUtil.setField(emergencyContact.getContactNumber(), binding.etEmergencyNumber);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause: called");

        Log.d(TAG, "onPause: saving data");
        requireActivity().getIntent().putExtra(getString(R.string.BUNDLE_KEY), saveState());
    }

    private Bundle saveState() {
        Bundle saveState = new Bundle();

        String[] data = getUserInput();
        viewModel.addEmergencyContact(
                saveState,
                data[0],
                data[1],
                data[2],
                data[3],
                data[4],
                data[5],
                data[6]
        );

        saveState.putParcelable(Employee.EMPLOYEE_KEY, employee);

        Log.d(TAG, "saveState: saved state: " + saveState);

        return saveState;
    }

    private final Observer<Integer> errorObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer integer) {
            Log.d(TAG, "onChanged: data changed");
            if (integer != -1 && integer != 0) {
                Snackbar
                        .make(binding.btnEmployeeConfirm, integer, Snackbar.LENGTH_SHORT)
                        .show();
            }
//            else if (integer == -1){
//                requireActivity()
//                        .setResult(
//                                Activity.RESULT_OK,
//                                new Intent().putExtra(getString(R.string.EMPLOYEE), employee.getUid())
//                        );
//
//                requireActivity().finish();
//            }
        }
    };

    private Observer<Boolean> addingEmployee = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
            if (aBoolean) {
                binding.pbEmployee2Loading.setVisibility(View.GONE);
                requireActivity()
                        .setResult(
                                Activity.RESULT_OK,
                                new Intent().putExtra(getString(R.string.EMPLOYEE), employee)
                        );

                requireActivity().finish();
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView: called");

        AccountRepository.getInstance().resetmError();
        binding = null;
        viewModel = null;
    }
}