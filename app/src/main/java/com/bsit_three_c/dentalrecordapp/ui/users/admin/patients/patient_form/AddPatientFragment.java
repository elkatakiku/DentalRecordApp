package com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_form;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ListWithRemoveItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.FirebaseHelper;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAddPatientBinding;
import com.bsit_three_c.dentalrecordapp.ui.dialog.DatePickerFragment;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_info.PatientActivity;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.CustomItemSelectedListener;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

public class AddPatientFragment extends Fragment {
    private static final String TAG = AddPatientFragment.class.getSimpleName();

    private FragmentAddPatientBinding binding;
    private AddPatientViewModel basicViewModel;
    private ListWithRemoveItemAdapter numbersAdapter;

    private boolean isEdit;
    private Patient patient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddPatientBinding.inflate(inflater, container, false);
        basicViewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(AddPatientViewModel.class);
        numbersAdapter = new ListWithRemoveItemAdapter(requireContext(), R.layout.item_category);

        binding.lvPatientMobileNumbers.setAdapter(numbersAdapter);
        numbersAdapter.setListView(binding.lvPatientMobileNumbers);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ibPatientCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        binding.btnPatientAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputNumber = binding.etPatientNumber.getText().toString();
                final int mode = binding.spnrPatientNumberMode.getSelectedItemPosition();
                
                switch (mode) {
                    case 0:
                        if (inputNumber.length() != 10 && inputNumber.length() != 7) {
                            binding.etPatientNumber.setError(getString(R.string.invalid_tel_limit_number));
                            return;
                        }

                        inputNumber = formatTelephoneNumber(inputNumber);
                        break;

                    case 1:
                        if (inputNumber.length() != 10) {
                            binding.etPatientNumber.setError(getString(R.string.invalid_cel_limit_number));
                            return;
                        }

                        String code = binding.spnrPatientNumberMode.getSelectedItem().toString();
                        inputNumber = formatMobileNumber(code, inputNumber);

                        break;
                }

                addMobileNumber(inputNumber);
            }
        });

        binding.btnAddPatient.setOnClickListener(btn -> {
            String firstname = binding.eTxtFirstname.getText().toString().trim();
            String lastname = binding.eTxtLastname.getText().toString().trim();
            String middleInitial = binding.eTxtMiddleInitial.getText().toString().trim();
            String suffix = binding.eTxtSuffix.getText().toString().trim();
            String address = binding.eTxtAddress.getText().toString().trim();
            int age = UIUtil.convertToInteger(binding.etPatientAge.getText().toString().trim());
            int civilStatus = binding.spnrCivilStatus.getSelectedItemPosition();
            String occupation = binding.eTxtOccupation.getText().toString().trim();

            Log.d(TAG, "onViewCreated: info: " +
                    "\nfirstname: " + firstname +
                    "\nlastname: " + lastname +
                    "\nMI: " + middleInitial +
                    "\nsuffix: " + suffix +
                    "\naddress: " + address +
                    "\nmobile number: " + numbersAdapter.getList() +
                    "\nage: " + age +
                    "\ncivil status: " + civilStatus +
                    "\noccupation: " + occupation);

            Log.d(TAG, "onViewCreated: labels:" +
                    "\nfirtname label: " + binding.eTxtFirstname.getHint().toString());

            boolean isInputValid = true;

            if (!Checker.isDataAvailable(firstname)) {
                binding.eTxtFirstname.setError(getString(R.string.invalid_empty_input));
                isInputValid = false;
            }

            if (!Checker.isDataAvailable(lastname)) {
                binding.eTxtLastname.setError(getString(R.string.invalid_empty_input));
                isInputValid = false;
            }
            Log.d(TAG, "onViewCreated: is input valid reverse: " + !isInputValid);
            Log.d(TAG, "onViewCreated: is input valid: " + isInputValid);
            if (!isInputValid || !basicViewModel.isStateValid()) return;
            Log.d(TAG, "onViewCreated: valid: " + basicViewModel.isStateValid());

            Intent intentResult = new Intent(requireActivity(), PatientActivity.class);

            if (isEdit) {
                intentResult.putExtra(LocalStorage.UPDATED_PATIENT_KEY, basicViewModel.updatePatient(
                        patient,
                        firstname,
                        lastname,
                        middleInitial,
                        suffix,
                        address,
                        numbersAdapter.getList(),
                        civilStatus,
                        age,
                        occupation
                ));
            }
            else {
                intentResult.putExtra(LocalStorage.UPDATED_PATIENT_KEY, basicViewModel.addPatient(
                        firstname,
                        lastname,
                        middleInitial,
                        suffix,
                        address,
                        numbersAdapter.getList(),
                        civilStatus,
                        age,
                        occupation
                ));
            }

            Log.d(TAG, "onViewCreated: intent: " + intentResult);
            Log.d(TAG, "onViewCreated: intent patient: " + intentResult.getParcelableExtra(LocalStorage.UPDATED_PATIENT_KEY));
            Log.d(TAG, "onViewCreated: intent patient key: " + LocalStorage.UPDATED_PATIENT_KEY);

            requireActivity().setResult(Activity.RESULT_OK, intentResult);
            requireActivity().finish();

        });

        setListeners();
        setObservers();
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

    private void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment(
                binding.tvPatientMonth,
                binding.tvPatientDay,
                binding.tvPatientYear,
                binding.etPatientAge
        );

        datePickerFragment.showDatePickerDialog(
                datePickerFragment,
                UIUtil.getMonthNumber(binding.tvPatientMonth.getText().toString()),
                getChildFragmentManager()
        );
    }

    //  Formats string to telephone number format
    private String formatTelephoneNumber(final String number) {
        String firstPart = number.substring(0, 3);
        String contactNumber = number;

        if (number.length() == 10) {
            String secondPart = number.substring(3, 6);
            String lastPart = number.substring(5, 10);

            contactNumber = firstPart + "-" + secondPart + "-" + lastPart;
        }
        else if (number.length() == 7) {
            contactNumber =  firstPart + "-" + number.substring(3, 7);
        }

        return contactNumber;
    }

    //  Formats string to philippine mobile number format
    private String formatMobileNumber(String code, String number) {
        String firstPart = number.substring(0, 3);
        String secondPart = number.substring(3, 6);
        String lastPart = number.substring(5, 10);

        return "(" + code + ") " + firstPart + "-" + secondPart + "-" + lastPart;
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

    private void setObservers() {
        Resources resources = getResources();
        basicViewModel.getmFirstname().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtFirstname, resources));
        basicViewModel.getMlastname().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtLastname, resources));
        basicViewModel.getmMiddleInitial().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtMiddleInitial, resources));
        basicViewModel.getmSuffix().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtSuffix, resources));
        basicViewModel.getmAge().observe(getViewLifecycleOwner(), new CustomObserver(binding.etPatientAge, resources));
        basicViewModel.getmAddress().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtAddress, resources));
        basicViewModel.getmOccupation().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtOccupation, resources));

        basicViewModel.getAddPatientFormState().observe(getViewLifecycleOwner(), new Observer<FormState>() {
            @Override
            public void onChanged(FormState formState) {

            }
        });

        basicViewModel.getmContact().observe(getViewLifecycleOwner(),
                new CustomObserver(binding.etPatientNumber, resources)
                        .setBtn(binding.btnPatientAddNumber));
    }

    private void setListeners() {
        binding.eTxtFirstname.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.eTxtFirstname.getHint().toString()));
        binding.eTxtLastname.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.eTxtLastname.getHint().toString()));
        binding.eTxtMiddleInitial.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.eTxtMiddleInitial.getHint().toString()));
        binding.eTxtSuffix.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.eTxtSuffix.getHint().toString()));
        binding.etPatientAge.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.etPatientAge.getHint().toString()));
        binding.eTxtAddress.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.eTxtAddress.getHint().toString()));
        binding.eTxtOccupation.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.eTxtOccupation.getHint().toString()));
        binding.spnrCivilStatus.setOnItemSelectedListener(new CustomItemSelectedListener((String) binding.spnrCivilStatus.getTag(), basicViewModel));
    }

    private void initializeFields(Patient patient) {
        binding.eTxtFirstname.setText(patient.getFirstname());
        binding.eTxtLastname.setText(patient.getLastname());

        if (Checker.isDataAvailable(patient.getMiddleInitial())) {
            binding.eTxtMiddleInitial.setText(patient.getMiddleInitial());
        }

        if (Checker.isDataAvailable(patient.getSuffix())) {
            binding.eTxtSuffix.setText(patient.getSuffix());
        }

        Log.d(TAG, "initializeFields: address: " + patient.getAddress());
        if (Checker.isDataAvailable(patient.getAddress()))
            binding.eTxtAddress.setText(patient.getAddress());

        if (patient.getAge() >= 0)
            binding.etPatientAge.setText(String.valueOf(patient.getAge()));

        if (patient.getPhoneNumber() != null) {
            for (String number : patient.getPhoneNumber()) {
                if (patient.getPhoneNumber().get(0).equals(FirebaseHelper.NEW_PATIENT)) {
                    break;
                }
                addMobileNumber(number);
            }
        }

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