package com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.view_patient;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.OperationsList;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.FirebaseHelper;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentPatientInfoBinding;
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

        displayInfo(patient);
        loadProcedures();
    }

    @Override
    public void onResume() {
        super.onResume();

//        for (String key : requireActivity().getIntent().getExtras().keySet()) {
//            Log.d(TAG, "onResume: key: " + key);
//        }

        String patientUId = requireActivity().getIntent().getStringExtra(FirebaseHelper.PATIENT_UID);
        if (patientUId != null) {
            Log.d(TAG, "onResume: patient uid: " + patientUId);
            viewModel.loadPatient(patient.getUid());
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: called");

        viewModel.getPatientDB().observe(getViewLifecycleOwner(), new Observer<Patient>() {
            @Override
            public void onChanged(Patient patient) {
                Log.d(TAG, "onStart: live data changed in patient info");
                if (patient != null) {
                    PatientInfoFragment.this.patient = patient;
                    Log.d(TAG, "onStart: updated patient not null");
                    displayInfo(patient);
                    Log.d(TAG, "onStart: updated patient: " + patient);
                }
            }
        });
    }

    public void loadProcedures() {
        Log.d(TAG, "loadProcedures: called");
        operationsList = new OperationsList(binding.tryList, patient, this);
        viewModel.loadOperations(patient, this);

        viewModel.getmProceduresCounter().observe(getViewLifecycleOwner(), integer -> {

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
                operationsList.addItems(Arrays.asList(viewModel.getProcedures()));
            } else operationsList.clearItems();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        Log.d(TAG, "onDestroyView: patient info destroyed called");
    }

    private void displayInfo(Patient patient) {
        Log.d(TAG, "displayInfo: displaying info: " + patient);
        String notAvailable = "N/A";

        Log.d(TAG, "displayInfo: firstname: " + patient.getFirstname());
        UIUtil.setText(patient.getFirstname(), binding.txtViewPIFirstname);
        UIUtil.setText(patient.getLastname(), binding.txtViewPILastname);
        UIUtil.setText(patient.getMiddleInitial(), binding.txtViewPIMiddleInitial);
        UIUtil.setText(patient.getSuffix(), binding.tvPatientSuffix);
        UIUtil.setText(patient.getAddress(), binding.txtViewPIAddress);
        UIUtil.setText(patient.getOccupation(), binding.txtViewPIOccupation);

        binding.tvPatientBirthdate.setText(patient.getBirthdate());
        binding.txtViewPITelephoneNumber.setText(patient.getContactNumber());

//        List<String > contactNumber = patient.getPhoneNumber();
//        if (contactNumber.size() > 0) {
//            StringBuilder builder = new StringBuilder();
//
//            if (contactNumber.get(0).equals(FirebaseHelper.NEW_PATIENT)) {
//                builder.append(Checker.NOT_AVAILABLE);
//            } else {
//                for (String number : contactNumber) {
//                    builder.append(number).append("\n");
//                }
//                builder.deleteCharAt(builder.length()-1);
//            }
//
//            binding.txtViewPITelephoneNumber.setText(builder.toString());
//        }
//        else
//            binding.txtViewPITelephoneNumber.setText(notAvailable);

        if (patient.getAge() > 0) binding.txtViewPIAge.setText(String.valueOf(patient.getAge()));
        else
            binding.txtViewPIAge.setText(notAvailable);

        binding.txtViewPICivilStatus.setText(patient.getCivilStatus(getResources()));

//        if (Checker.isNotDefault(patient.getCivilStatus()))
//            binding.txtViewPICivilStatus.setText(patient.getCivilStatus(getResources()));
//        else
//            binding.txtViewPICivilStatus.setText(notAvailable);
    }

}