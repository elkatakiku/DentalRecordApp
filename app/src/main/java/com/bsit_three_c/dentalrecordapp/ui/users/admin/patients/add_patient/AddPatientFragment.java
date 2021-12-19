package com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.add_patient;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAddPatientBinding;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_info.PatientActivity;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.CustomItemSelectedListener;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

public class AddPatientFragment extends Fragment {
    private static final String TAG = AddPatientFragment.class.getSimpleName();

    private FragmentAddPatientBinding binding;
    private AddPatientViewModel basicViewModel;

    private boolean isEdit;
    private Patient patient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddPatientBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        basicViewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(AddPatientViewModel.class);

        binding.btnAddPatient.setEnabled(false);

        binding.btnAddPatient.setOnClickListener(view1 -> {
            String firstname = binding.eTxtFirstname.getText().toString();
            String lastname = binding.eTxtLastname.getText().toString();
            String middleInitial = binding.eTxtMiddleInitial.getText().toString();
            String suffix = binding.eTxtSuffix.getText().toString();
            String address = binding.eTxtAddress.getText().toString();
            String phoneNumber = binding.eTxtPhoneNumber.getText().toString();
            int age = UIUtil.convertToInteger(binding.eTxtAge.getText().toString());
            int civilStatus = binding.spnrCivilStatus.getSelectedItemPosition();
            String occupation = binding.eTxtOccupation.getText().toString();

            Intent intentResult = new Intent(requireActivity(), PatientActivity.class);

            if (isEdit) {

                intentResult.putExtra(getString(R.string.PATIENT), basicViewModel.updatePatient(
                        patient,
                        firstname,
                        lastname,
                        middleInitial,
                        suffix,
                        address,
                        phoneNumber,
                        civilStatus,
                        age,
                        occupation
                ));
            }
            else {
                intentResult.putExtra(getString(R.string.PATIENT), basicViewModel.addPatient(
                        firstname,
                        lastname,
                        middleInitial,
                        suffix,
                        address,
                        phoneNumber,
                        civilStatus,
                        age,
                        occupation
                ));
            }

            requireActivity().setResult(Activity.RESULT_OK, intentResult);
            requireActivity().finish();


        });

        setListeners();
        setObservers();
    }

    private void setObservers() {
        Resources resources = getResources();
        basicViewModel.getmFirstname().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtFirstname, resources));
        basicViewModel.getMlastname().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtLastname, resources));
        basicViewModel.getmMiddleInitial().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtMiddleInitial, resources));
        basicViewModel.getmAge().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtAge, resources));
        basicViewModel.getmAddress().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtAddress, resources));
        basicViewModel.getmPhoneNumber().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtPhoneNumber, resources));
        basicViewModel.getmOccupation().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtOccupation, resources));
        basicViewModel.getAddPatientFormState().observe(getViewLifecycleOwner(), new CustomObserver.ObserverButton(binding.btnAddPatient));
    }

    private void setListeners() {
        binding.eTxtFirstname.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelFirstname.getText().toString()));
        binding.eTxtLastname.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelLastname.getText().toString()));
        binding.eTxtMiddleInitial.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.eTxtMiddleInitial.getHint().toString()));
        binding.eTxtAge.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.eTxtAge.getHint().toString()));
        binding.eTxtAddress.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelAddress.getText().toString()));
        binding.eTxtPhoneNumber.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelTelephone.getText().toString()));
        binding.eTxtOccupation.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelOccupation.getText().toString()));
        binding.spnrCivilStatus.setOnItemSelectedListener(new CustomItemSelectedListener(binding.labelCivilStatus.getText().toString(), basicViewModel));

        binding.cbMIS.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) binding.layoutMIS.setVisibility(View.VISIBLE);
            else binding.layoutMIS.setVisibility(View.GONE);

        });
    }

    @Override
    public void onStart() {
        super.onStart();

        patient = requireActivity().getIntent().getParcelableExtra(requireContext().getString(R.string.PATIENT));

        if (patient != null) {
            isEdit = true;
            initializeFields(patient);
        }
        else
            isEdit = false;
    }

    private void initializeFields(Patient patient) {
        binding.eTxtFirstname.setText(patient.getFirstname());
        binding.eTxtLastname.setText(patient.getLastname());

        if (Checker.isDataAvailable(patient.getMiddleInitial())) {
            binding.cbMIS.setChecked(true);
            binding.eTxtMiddleInitial.setText(patient.getMiddleInitial());
        }

        if (Checker.isDataAvailable(patient.getSuffix())) {
            binding.cbMIS.setChecked(true);
            binding.eTxtSuffix.setText(patient.getSuffix());
        }

        Log.d(TAG, "initializeFields: address: " + patient.getAddress());
        if (Checker.isDataAvailable(patient.getAddress()))
            binding.eTxtAddress.setText(patient.getAddress());

        if (patient.getAge() >= 0)
            binding.eTxtAge.setText(String.valueOf(patient.getAge()));

        if (Checker.isDataAvailable(patient.getPhoneNumber()))
            binding.eTxtPhoneNumber.setText(patient.getPhoneNumber());

        if (Checker.isDataAvailable(patient.getOccupation()))
            binding.eTxtOccupation.setText(patient.getOccupation());

        if (patient.getCivilStatus() > 0)
            binding.spnrCivilStatus.setSelection(patient.getCivilStatus());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}