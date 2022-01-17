package com.bsit_three_c.dentalrecordapp.ui.patients.patient_form;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ListWithRemoveItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.BaseRepository;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormPatientBinding;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.dialog.DatePickerFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.ContactNumber;
import com.bsit_three_c.dentalrecordapp.util.CustomItemSelectedListener;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;

public class PatientFormFragment extends Fragment {
    private static final String PATIENT_KEY = "ARG_PF_PATIENT_KEY";
    private static final String APPOINTMENT_KEY = "ARG_PF_APPOINTMENT_KEY";

    private FragmentFormPatientBinding binding;
    private PatientFormViewModel viewModel;
    private ListWithRemoveItemAdapter numbersAdapter;

    private Appointment appointment;
    private Patient patient;
    private Account account;
    private boolean isEdit;

    private final ActivityResultLauncher<Intent> toAddProcedureResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            requireActivity().setResult(RESULT_OK, result.getData());
            requireActivity().finish();
        }
    });

    public static PatientFormFragment newInstance(Patient patient, Appointment appointment) {
        Bundle argument = new Bundle();
        argument.putParcelable(PATIENT_KEY, patient);
        argument.putParcelable(APPOINTMENT_KEY, appointment);
        PatientFormFragment patientFormFragment = new PatientFormFragment();
        patientFormFragment.setArguments(argument);
        return patientFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        binding = FragmentFormPatientBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(PatientFormViewModel.class);
        numbersAdapter = new ListWithRemoveItemAdapter(requireContext(), R.layout.item_list_with_remove);

        if (getArguments() != null) {
            patient = getArguments().getParcelable(PATIENT_KEY);
            appointment = getArguments().getParcelable(APPOINTMENT_KEY);
        }

        binding.lvPatientMobileNumbers.setAdapter(numbersAdapter);
        numbersAdapter.setListView(binding.lvPatientMobileNumbers);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getmAccount().observe(getViewLifecycleOwner(), mAccount -> {
            if (mAccount != null){
                account = mAccount;
                binding.etPatientAge.setEnabled(false);
                binding.etPatientFormEmail.setText(account.getEmail());
                binding.etPatientFormEmail.setEnabled(false);
            }
        });

        viewModel.getmError().observe(getViewLifecycleOwner(), integer -> {
            if (integer != null && integer != Checker.VALID) {
                Snackbar
                        .make(binding.btnAddPatient, integer, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        viewModel.getmCreateAttempt().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.pbLoadingAddPatient.setVisibility(View.GONE);
                setFieldsEnabled(true);
            }
        });

        viewModel.getmPatient().observe(getViewLifecycleOwner(), patient -> {
            if (patient != null && viewModel.getmError().getValue() != null && viewModel.getmError().getValue() == Checker.VALID) {
                    returnResult(new Intent().putExtra(LocalStorage.UPDATED_PATIENT_KEY, patient));
            }
        });

        binding.layoutPatientDatePicker.setOnClickListener(v -> showDatePickerDialog());

        binding.ibPatientCalendar.setOnClickListener(v -> showDatePickerDialog());

        binding.btnPatientAddNumber.setOnClickListener(v -> {
            String inputNumber = ContactNumber.getFormattedContactNumber(
                    binding.etPatientNumber,
                    binding.spnrPatientNumberMode,
                    requireContext().getResources()
            );
            if (inputNumber != null) {
                addMobileNumber(inputNumber);
            }
        });

        binding.btnAddPatient.setOnClickListener(btn -> {
            String firstname = binding.eTxtFirstname.getText().toString().trim();
            String lastname = binding.eTxtLastname.getText().toString().trim();
            String middleInitial = binding.eTxtMiddleInitial.getText().toString().trim();
            String suffix = binding.eTxtSuffix.getText().toString().trim();

            String dateOfBirth = DateUtil.getDate(
                    binding.tvPatientDay.getText().toString().trim(),
                    binding.tvPatientMonth.getText().toString().trim(),
                    binding.tvPatientYear.getText().toString().trim()
            );

            String address = binding.eTxtAddress.getText().toString().trim();
            int age = UIUtil.convertToInteger(binding.etPatientAge.getText().toString().trim());
            int civilStatus = binding.spnrCivilStatus.getSelectedItemPosition();
            String occupation = binding.eTxtOccupation.getText().toString().trim();

            boolean isInputValid = true;

            if (!Checker.isDataAvailable(firstname)) {
                binding.eTxtFirstname.setError(getString(R.string.invalid_empty_input));
                isInputValid = false;
            }

            if (!Checker.isDataAvailable(lastname)) {
                binding.eTxtLastname.setError(getString(R.string.invalid_empty_input));
                isInputValid = false;
            }

            if (!isInputValid || !viewModel.isStateValid()) {
                return;
            }

            setFieldsEnabled(false);
            binding.pbLoadingAddPatient.setVisibility(View.VISIBLE);

            if (appointment != null) {
                appointment.setPatient(viewModel.createPatient(
                        firstname,
                        lastname,
                        middleInitial,
                        suffix,
                        dateOfBirth,
                        address,
                        numbersAdapter.getList(),
                        civilStatus,
                        age,
                        occupation
                ));
                toAddProcedureResult.launch(
                        BaseFormActivity.getProcedureFormIntent(requireContext(), appointment));
                return;
            }

            if (isEdit) {
                viewModel.updatePatient(
                        patient,
                        firstname,
                        lastname,
                        middleInitial,
                        suffix,
                        dateOfBirth,
                        address,
                        numbersAdapter.getList(),
                        civilStatus,
                        age,
                        occupation
                );
            } else {
                viewModel.addPatient(
                        firstname,
                        lastname,
                        middleInitial,
                        suffix,
                        dateOfBirth,
                        address,
                        numbersAdapter.getList(),
                        civilStatus,
                        age,
                        occupation
                );
            }
        });

        setListeners();
        setObservers();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (appointment != null) {
            patient = viewModel.convertToPatient(appointment.getPatient());
        }

        if (patient != null) {
            isEdit = true;
            initializeFields(patient);
        }
        else
            isEdit = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.setmError(null);
        viewModel.setmCreateAttempt(true);
        viewModel.setmPatient(null);
    }

    private void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment(
                binding.tvPatientMonth,
                binding.tvPatientDay,
                binding.tvPatientYear,
                binding.etPatientAge
        );

        datePickerFragment.setMaxDateToday();
        datePickerFragment.showDatePickerDialog(
                datePickerFragment,
                DateUtil.getMonthNumber(binding.tvPatientMonth.getText().toString()),
                getChildFragmentManager(),
                DatePickerFragment.BIRTH_DATE_TITLE
        );
    }

    private void addMobileNumber(String number) {
        if (number.trim().isEmpty()) {
            binding.etPatientNumber.setError(getString(R.string.invalid_empty_input));
            return;
        }

        numbersAdapter.add(number);
        numbersAdapter.notifyDataSetChanged();

        binding.etPatientNumber.setText("");
    }

    private void returnResult(Intent result) {
        requireActivity().setResult(Activity.RESULT_OK, result);
        requireActivity().finish();
    }

    private void setObservers() {
        Resources resources = getResources();
        viewModel.getmFirstname().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtFirstname, resources));
        viewModel.getMlastname().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtLastname, resources));
        viewModel.getmMiddleInitial().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtMiddleInitial, resources));
        viewModel.getmSuffix().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtSuffix, resources));
        viewModel.getmAge().observe(getViewLifecycleOwner(), new CustomObserver(binding.etPatientAge, resources));
        viewModel.getmAddress().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtAddress, resources));
        viewModel.getmOccupation().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtOccupation, resources));

        viewModel.getmContact().observe(getViewLifecycleOwner(),
                new CustomObserver(binding.etPatientNumber, resources)
                        .setBtn(binding.btnPatientAddNumber));
    }

    private void setListeners() {
        binding.eTxtFirstname.addTextChangedListener(new CustomTextWatcher(viewModel, binding.eTxtFirstname.getHint().toString()));
        binding.eTxtLastname.addTextChangedListener(new CustomTextWatcher(viewModel, binding.eTxtLastname.getHint().toString()));
        binding.eTxtMiddleInitial.addTextChangedListener(new CustomTextWatcher(viewModel, binding.eTxtMiddleInitial.getHint().toString()));
        binding.eTxtSuffix.addTextChangedListener(new CustomTextWatcher(viewModel, binding.eTxtSuffix.getHint().toString()));
        binding.etPatientAge.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etPatientAge.getHint().toString()));
        binding.eTxtAddress.addTextChangedListener(new CustomTextWatcher(viewModel, binding.eTxtAddress.getHint().toString()));
        binding.eTxtOccupation.addTextChangedListener(new CustomTextWatcher(viewModel, binding.eTxtOccupation.getHint().toString()));
        binding.spnrCivilStatus.setOnItemSelectedListener(new CustomItemSelectedListener((String) binding.spnrCivilStatus.getTag(), viewModel));
    }

    private void initializeFields(Patient patient) {
        UIUtil.setField(patient.getFirstname(), binding.eTxtFirstname);
        UIUtil.setField(patient.getLastname(), binding.eTxtLastname);
        UIUtil.setField(patient.getMiddleInitial(), binding.eTxtMiddleInitial);
        UIUtil.setField(patient.getSuffix(), binding.eTxtSuffix);
        UIUtil.setDateFields(
                patient.getDateOfBirth(),
                binding.tvPatientDay,
                binding.tvPatientMonth,
                binding.tvPatientYear
        );
        UIUtil.setField(patient.getAddress(), binding.eTxtAddress);

        if (Checker.isDataAvailable(patient.getDateOfBirth())) {
            binding.etPatientAge.setEnabled(false);
        }

        if (Checker.isDataAvailable(patient.getEmail())) {
            binding.etPatientFormEmail.setText(patient.getEmail());
            binding.etPatientFormEmail.setEnabled(false);
        }

        if (patient.getAge() >= 0)
            binding.etPatientAge.setText(String.valueOf(patient.getAge()));

        numbersAdapter.clear();
        if (patient.getPhoneNumber() != null) {
            for (String number : patient.getPhoneNumber()) {
                if (patient.getPhoneNumber().get(0).equals(BaseRepository.NEW_PATIENT)) {
                    break;
                }
                addMobileNumber(number);
            }
        }

        UIUtil.setField(patient.getOccupation(), binding.eTxtOccupation);

        if (patient.getCivilStatus() > 0)
            binding.spnrCivilStatus.setSelection(patient.getCivilStatus());
    }

    private void setFieldsEnabled(boolean enabled) {
        binding.eTxtFirstname.setEnabled(enabled);
        binding.eTxtLastname.setEnabled(enabled);
        binding.eTxtMiddleInitial.setEnabled(enabled);
        binding.eTxtSuffix.setEnabled(enabled);
        binding.layoutPatientDatePicker.setEnabled(enabled);
        binding.ibPatientCalendar.setEnabled(enabled);
        binding.etPatientAge.setEnabled(enabled);
        binding.spnrPatientNumberMode.setEnabled(enabled);
        binding.etPatientNumber.setEnabled(enabled);
        binding.btnPatientAddNumber.setEnabled(enabled);
        binding.eTxtAddress.setEnabled(enabled);
        binding.spnrCivilStatus.setEnabled(enabled);
        binding.btnAddPatient.setEnabled(enabled);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}