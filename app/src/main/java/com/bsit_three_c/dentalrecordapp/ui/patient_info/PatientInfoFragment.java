package com.bsit_three_c.dentalrecordapp.ui.patient_info;

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
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.PatientViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentPatientInfoBinding;
import com.bsit_three_c.dentalrecordapp.util.Checker;

import java.util.Arrays;

public class PatientInfoFragment extends Fragment {
    private static final String TAG = PatientInfoFragment.class.getSimpleName();

    private FragmentPatientInfoBinding binding;
    private PatientInfoViewModel viewModel;
    private Patient patient;

    private OperationsList operationsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPatientInfoBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new PatientViewModelFactory()).get(PatientInfoViewModel.class);;

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: patient info created");

        FragmentActivity activity = getActivity();
        if (activity != null && viewModel.isPatientNull()) {
            patient = activity.getIntent().getParcelableExtra(getString(R.string.PATIENT));
            viewModel.setPatient(activity.getIntent().getParcelableExtra(getString(R.string.PATIENT)));
        }

        binding.buttonFirst.setOnClickListener(view1 -> {
            PatientInfoFragmentDirections.ActionFirst2FragmentToSecondFragment action =
                    PatientInfoFragmentDirections.actionFirst2FragmentToSecondFragment(patient);
            Navigation.findNavController(view1).navigate(action);
            onPause();
//                NavHostFragment.findNavController(PatientInfoFragment.this)
//                        .navigate(R.id.action_First2Fragment_to_SecondFragment);
        });


        operationsList = new OperationsList(binding.tryList, getLayoutInflater(), patient, this);
        viewModel.loadOperations(patient, this);

//        viewModel.getmProceduresCounter().observe(getViewLifecycleOwner(), integer -> {
//
//            if (integer <= 0) {
//                binding.textViewEmptyProcedures.setVisibility(View.VISIBLE);
//                binding.proceduresLoading.setVisibility(View.INVISIBLE);
//            }
//            else {
//                binding.textViewEmptyProcedures.setVisibility(View.GONE);
//                binding.proceduresLoading.setVisibility(View.GONE);
//            }
//
//            Log.d(TAG, "onViewCreated: integer: " + integer);
//            Log.d(TAG, "onViewCreated: array size: " + viewModel.getProcedureSize());
//
//            if (viewModel.getProcedureSize() == integer) {
//                operationsList.addItems(viewModel.getProcedureList());
//                for (DentalProcedure procedure :
//                        viewModel.getProcedureList()) { Log.d(TAG, "onViewCreated: procedure: " + procedure.getDentalDesc());
//                }
//
//            }
//        });
//
//        viewModel.getmOperations().observe(getViewLifecycleOwner(), dentalOperations -> {
//
//            Log.d(TAG, "onViewCreated: dental operations size: " + dentalOperations.length);
////            Log.d(TAG, "onViewCreated: dental operations empty: " + dentalOperations.isEmpty());
////            Log.d(TAG, "onViewCreated: dental operations null: " + (dentalOperations == null));
//
//            if (dentalOperations[0] != null) {
//                binding.textViewEmptyProcedures.setVisibility(View.VISIBLE);
//                binding.proceduresLoading.setVisibility(View.INVISIBLE);
//            }
//            else {
//                binding.textViewEmptyProcedures.setVisibility(View.GONE);
//                binding.proceduresLoading.setVisibility(View.GONE);
//            }
//
//            if (dentalOperations.length == viewModel.getProcedureSize()) {
//                Log.d(TAG, "onViewCreated: size == length");
//                operationsList.addItems(Arrays.asList(dentalOperations));
//            }
//            for (DentalProcedure operation : dentalOperations) {
//                Log.d(TAG, "onViewCreated: operatioh: " + operation.getDentalBalance());
//            }
//            for (int position = 0; position < dentalOperations.size(); position++) {
//                Log.d(TAG, "onViewCreated: operation desc: " + dentalOperations.get(position).getDentalDesc());
//            }
//        });

        viewModel.getmProceduresCounter().observe(getViewLifecycleOwner(), integer -> {
            Log.d(TAG, "onChanged: integer val: " + integer);
            Log.d(TAG, "onViewCreated: procedure array size: " + viewModel.getProcedureSize());

            //  Checks if there is no procedure
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
            }
        });

        viewModel.getmBalance().observe(getViewLifecycleOwner(), aDouble -> {
            String convertedBalance = String.valueOf(aDouble);
            binding.txtOperationsBalance.setText(convertedBalance);
        });

        displayInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
//        viewModel.getIsLoaded().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                Log.d(TAG, "onChanged: isDone: " + aBoolean);
//                if (aBoolean) operationsList.addItems(viewModel.getProcedureList());
//            }
//        });

        Log.d(TAG, "onResume: patient info resumed");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause: patient info paused");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        Log.d(TAG, "onDestroyView: patient info destroyed called");
    }

    private void displayInfo() {
        String notAvailable = "N/A";
        if (Checker.isDataAvailable(patient.getFirstname())) binding.txtViewPIFirstname.setText(patient.getFirstname());
        else binding.txtViewPIFirstname.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getLastname())) binding.txtViewPILastname.setText(patient.getLastname());
        else binding.txtViewPILastname.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getMiddleInitial())) binding.txtViewPIMiddleInitial.setText(patient.getMiddleInitial());
        else binding.txtViewPIMiddleInitial.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getAddress())) binding.txtViewPIAddress.setText(patient.getAddress());
        else binding.txtViewPIAddress.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getPhoneNumber())) binding.txtViewPITelephoneNumber.setText(patient.getPhoneNumber());
        else binding.txtViewPITelephoneNumber.setText(notAvailable);

        if (patient.getAge() > 0) binding.txtViewPIAge.setText(String.valueOf(patient.getAge()));
        else binding.txtViewPIAge.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getCivilStatus())) binding.txtViewPICivilStatus.setText(patient.getCivilStatus());
        else binding.txtViewPICivilStatus.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getOccupation())) binding.txtViewPIOccupation.setText(patient.getOccupation());
        else binding.txtViewPIOccupation.setText(notAvailable);
    }

}