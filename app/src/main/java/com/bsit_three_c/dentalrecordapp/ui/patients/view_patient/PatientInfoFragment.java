package com.bsit_three_c.dentalrecordapp.ui.patients.view_patient;

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
import com.bsit_three_c.dentalrecordapp.data.adapter.ProceduresList;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentViewPatientBinding;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.Arrays;

public class PatientInfoFragment extends Fragment {
    private static final String TAG = PatientInfoFragment.class.getSimpleName();

    private FragmentViewPatientBinding binding;
    private PatientInfoViewModel viewModel;
    private Patient patient;

    private ProceduresList proceduresList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentViewPatientBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(PatientInfoViewModel.class);

        Log.d(TAG, "onCreateView: on create views");

        FragmentActivity activity = requireActivity();
        if (viewModel.isPatientNull()) {
            Log.d(TAG, "onCreateView: has patient");
            patient = activity.getIntent().getParcelableExtra(getString(R.string.PATIENT));
            viewModel.setPatient(patient);
        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: patient info created");

        viewModel.getmPatient().observe(getViewLifecycleOwner(), patient -> {
            Log.d(TAG, "onStart: live data changed in patient info");
            if (patient != null) {
                PatientInfoFragment.this.patient = patient;
                Log.d(TAG, "onStart: updated patient not null");
                initializeFields(patient);
                Log.d(TAG, "onStart: updated patient: " + patient);
            }
        });

        viewModel.getmDentalServices().observe(getViewLifecycleOwner(), dentalServices -> {
            Log.d(TAG, "onViewCreated: dental services changed");
            if (dentalServices != null) {
                viewModel.setServicesOptions(dentalServices);
                loadProcedures();
            }
        });

        viewModel.getmProceduresCounter().observe(getViewLifecycleOwner(), integer -> {

            Log.d(TAG, "onViewCreated: integer: " + integer);
            Log.d(TAG, "onViewCreated: list size: " + viewModel.getProcedureSize());

            //  Checks if there are no procedure.
            if (integer <= 0) {
                binding.textViewEmptyProcedures.setVisibility(View.VISIBLE);
                binding.proceduresLoading.setVisibility(View.INVISIBLE);
            }
            else {
                binding.textViewEmptyProcedures.setVisibility(View.GONE);
                binding.proceduresLoading.setVisibility(View.GONE);
            }

            //  Checks if procedures has been loaded from the firebase.
            if (integer == viewModel.getProcedureSize()) {
                Log.d(TAG, "onViewCreated: adding procedures: " + Arrays.toString(viewModel.getProcedures()));
                proceduresList.addItems(Arrays.asList(viewModel.getProcedures()));
            } else {
                proceduresList.clearItems();
            }
        });

        binding.btnNewProcedure.setOnClickListener(view1 -> {
            PatientInfoFragmentDirections.ActionFirst2FragmentToSecondFragment action =
                    PatientInfoFragmentDirections.actionFirst2FragmentToSecondFragment(patient);
            Navigation.findNavController(view1).navigate(action);
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

        initializeFields(patient);
    }

    @Override
    public void onResume() {
        super.onResume();

//        String patientUId = requireActivity().getIntent().getStringExtra(FirebaseHelper.PATIENT_UID);
        Log.d(TAG, "onResume: patient: " + patient);
        if (patient != null) {
            Log.d(TAG, "onResume: patient uid: " + patient.getUid());
            viewModel.loadPatient(patient.getUid());
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: called");
    }

    public void loadProcedures() {
        Log.d(TAG, "loadProcedures: called");
        proceduresList = new ProceduresList(binding.tryList, patient, this, viewModel.getServiceOptions());
        viewModel.loadOperations(patient);
    }

    private void initializeFields(Patient patient) {
        Log.d(TAG, "displayInfo: displaying info: " + patient);
        String notAvailable = "N/A";

        Log.d(TAG, "displayInfo: firstname: " + patient.getFirstname());
        UIUtil.setText(patient.getFirstname(), binding.txtViewPIFirstname);
        UIUtil.setText(patient.getLastname(), binding.txtViewPILastname);
        UIUtil.setText(patient.getMiddleInitial(), binding.txtViewPIMiddleInitial);
        UIUtil.setText(patient.getSuffix(), binding.tvPatientSuffix);
        UIUtil.setText(patient.getAddress(), binding.txtViewPIAddress);
        UIUtil.setText(patient.getOccupation(), binding.txtViewPIOccupation);
        UIUtil.setText(patient.getBirthdate(), binding.tvPatientBirthdate);
        UIUtil.setText(patient.getContactNumber(), binding.txtViewPITelephoneNumber);
        UIUtil.setText(patient.getAge(), binding.txtViewPIAge);
        UIUtil.setText(patient.getCivilStatus(getResources()), binding.txtViewPICivilStatus);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        Log.d(TAG, "onDestroyView: patient info destroyed called");
    }

}