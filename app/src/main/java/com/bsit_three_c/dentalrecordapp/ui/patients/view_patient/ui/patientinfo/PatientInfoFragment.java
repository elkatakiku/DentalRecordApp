package com.bsit_three_c.dentalrecordapp.ui.patients.view_patient.ui.patientinfo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ProceduresList;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentViewPatientBinding;
import com.bsit_three_c.dentalrecordapp.ui.patients.procedure_form.ProcedureFormFragment;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

public class PatientInfoFragment extends Fragment {
    private static final String TAG = PatientInfoFragment.class.getSimpleName();

    private static final String PATIENT_KEY = "ARG_PI_PATIENT_KEY";

    private FragmentViewPatientBinding binding;
    private PatientInfoViewModel viewModel;
    private Patient patient;

    private ProceduresList proceduresList;

    public static PatientInfoFragment newInstance(Patient patient) {
        PatientInfoFragment patientInfoFragment = new PatientInfoFragment();
        Bundle argument = new Bundle();
        argument.putParcelable(PATIENT_KEY, patient);
        patientInfoFragment.setArguments(argument);
        return patientInfoFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentViewPatientBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(PatientInfoViewModel.class);

        if (getArguments() != null) {
            patient = getArguments().getParcelable(PATIENT_KEY);
            viewModel.setPatient(patient);
        }
        Log.d(TAG, "onCreateView: patient passed: " + patient);

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

        viewModel.hasProcedures().observe(getViewLifecycleOwner(), aBoolean -> {
            if (!aBoolean) {
                binding.textViewEmptyProcedures.setVisibility(View.VISIBLE);
            } else {
                binding.textViewEmptyProcedures.setVisibility(View.GONE);
            }
        });
//
//        viewModel.getmProceduresCounter().observe(getViewLifecycleOwner(), integer -> {
//
//            Log.d(TAG, "onViewCreated: integer: " + integer);
//            Log.d(TAG, "onViewCreated: list size: " + viewModel.getProcedureSize());
//
//            //  Checks if there are no procedure.
//            if (integer <= 0) {
//                binding.textViewEmptyProcedures.setVisibility(View.VISIBLE);
//                binding.proceduresLoading.setVisibility(View.INVISIBLE);
//            }
//            else {
//                binding.textViewEmptyProcedures.setVisibility(View.GONE);
//                binding.proceduresLoading.setVisibility(View.GONE);
//            }
//
//            //  Checks if procedures has been loaded from the firebase.
//            if (integer == viewModel.getProcedureSize()) {
//                Log.d(TAG, "onViewCreated: adding procedures: " + Arrays.toString(viewModel.getProcedures()));
////                proceduresList.addItems(Arrays.asList(viewModel.getProcedures()));
//            } else {
//                proceduresList.clearItems();
//            }
//        });

        binding.btnNewProcedure.setOnClickListener(view1 -> {
            //  Go to procedure form by navigation
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_patient, ProcedureFormFragment.newInstance(patient, null))
                    .addToBackStack(null)
                    .commit();
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

        Log.d(TAG, "onResume: patient: " + patient);
        if (patient != null) {
            Log.d(TAG, "onResume: patient uid: " + patient.getUid());
            viewModel.loadPatient(patient.getUid());
        }

    }

    public void loadProcedures() {
        Log.d(TAG, "loadProcedures: called");
        proceduresList = new ProceduresList(binding.llProceduresList, patient, this, viewModel.getServiceOptions());
//        viewModel.loadProcedure(patient);
//        viewModel.set
        viewModel.loadProcedures(patient, proceduresList);
    }

    private void initializeFields(Patient patient) {
        Log.d(TAG, "displayInfo: displaying info: " + patient);
        String notAvailable = "N/A";

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