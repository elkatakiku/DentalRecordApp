package com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_info;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.OperationsList;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentPatientInfoBinding;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.Arrays;

public class PatientInfoFragment extends Fragment {
    private static final String TAG = PatientInfoFragment.class.getSimpleName();

    private FragmentPatientInfoBinding binding;
    private PatientInfoViewModel viewModel;
    private Patient patient;

    private OperationsList operationsList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPatientInfoBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(PatientInfoViewModel.class);

        Log.d(TAG, "onCreateView: on create views");

        FragmentActivity activity = getActivity();
        if (activity != null && viewModel.isPatientNull()) {
            patient = activity.getIntent().getParcelableExtra(getString(R.string.PATIENT));
            viewModel.setPatient(patient);
        }
        else requireActivity().finish();

        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: called");
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: patient info created");

        binding.btnNewProcedure.setOnClickListener(view1 -> {
            PatientInfoFragmentDirections.ActionFirst2FragmentToSecondFragment action =
                    PatientInfoFragmentDirections.actionFirst2FragmentToSecondFragment(patient);
            Navigation.findNavController(view1).navigate(action);
            onPause();
        });

        viewModel.getmBalance().observe(getViewLifecycleOwner(), aDouble -> {
            if (aDouble <= 0) {
                binding.txtOperationsBalance.setText(UIUtil.getPaymentStatus(aDouble));
                binding.balanceLayoutPatientInfo.setVisibility(View.GONE);
            } else {
                binding.balanceLayoutPatientInfo.setVisibility(View.VISIBLE);
                String convertedBalance = String.valueOf(aDouble);
                binding.txtOperationsBalance.setText(convertedBalance);
            }

        });

        displayInfo();
        loadProcedures();
    }

    public void loadProcedures() {
        operationsList = new OperationsList(binding.tryList, getLayoutInflater(), patient, this);
        viewModel.loadOperations(patient, this);

        viewModel.getmProceduresCounter().observe(getViewLifecycleOwner(), integer -> {

            //  Checks if there are no procedure
            if (integer <= 0) {
                binding.textViewEmptyProcedures.setVisibility(View.VISIBLE);
                binding.proceduresLoading.setVisibility(View.INVISIBLE);
            }
            else {
                binding.textViewEmptyProcedures.setVisibility(View.GONE);
                binding.proceduresLoading.setVisibility(View.GONE);
            }

            //  Checks if procedures has been loaded from the firebase
            if (integer == viewModel.getProcedureSize()) {
                operationsList.addItems(Arrays.asList(viewModel.getProcedures()));
            } else operationsList.clearItems();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: patient info resumed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        Log.d(TAG, "onDestroyView: patient info destroyed called");
    }

    private void displayInfo() {
        String notAvailable = "N/A";

        if (Checker.isDataAvailable(patient.getFirstname()))
            binding.txtViewPIFirstname.setText(patient.getFirstname());
        else
            binding.txtViewPIFirstname.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getLastname()))
            binding.txtViewPILastname.setText(patient.getLastname());
        else
            binding.txtViewPILastname.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getMiddleInitial()))
            binding.txtViewPIMiddleInitial.setText(patient.getMiddleInitial());
        else
            binding.txtViewPIMiddleInitial.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getAddress()))
            binding.txtViewPIAddress.setText(patient.getAddress());
        else
            binding.txtViewPIAddress.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getPhoneNumber()))
            binding.txtViewPITelephoneNumber.setText(patient.getPhoneNumber());
        else
            binding.txtViewPITelephoneNumber.setText(notAvailable);

        if (patient.getAge() > 0) binding.txtViewPIAge.setText(String.valueOf(patient.getAge()));
        else
            binding.txtViewPIAge.setText(notAvailable);

        Log.d(TAG, "displayInfo: civil: " + patient.getCivilStatus());
        if (Checker.isNotDefault(patient.getCivilStatus()))
            binding.txtViewPICivilStatus.setText(UIUtil.getCivilStatus(getResources(), patient.getCivilStatus()));
        else
            binding.txtViewPICivilStatus.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getOccupation()))
            binding.txtViewPIOccupation.setText(patient.getOccupation());
        else
            binding.txtViewPIOccupation.setText(notAvailable);
    }

}