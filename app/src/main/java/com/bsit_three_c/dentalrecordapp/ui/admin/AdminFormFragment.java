package com.bsit_three_c.dentalrecordapp.ui.admin;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ListWithRemoveItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.BaseRepository;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormAdminBinding;
import com.bsit_three_c.dentalrecordapp.ui.dialog.DatePickerFragment;
import com.bsit_three_c.dentalrecordapp.ui.profile.ui.profile_patient.ProfileFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.ContactNumber;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;

public class AdminFormFragment extends Fragment {
    private static final String TAG = AdminFormFragment.class.getSimpleName();

    public static final String ADMIN_UID_KEY = "ARG_AF_ADMIN_UID_KEY";

    private AdminFormViewModel mViewModel;
    private FragmentFormAdminBinding binding;
    private ListWithRemoveItemAdapter numbersAdapter;

    private LoggedInUser loggedInUser;

    public static AdminFormFragment newInstance(String adminUid) {
        Bundle arguments = new Bundle();
        arguments.putString(ADMIN_UID_KEY, adminUid);
        AdminFormFragment adminFormFragment = new AdminFormFragment();
        adminFormFragment.setArguments(arguments);
        return adminFormFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(AdminFormViewModel.class);
        binding = FragmentFormAdminBinding.inflate(inflater, container, false);
        numbersAdapter = new ListWithRemoveItemAdapter(requireContext(), R.layout.item_list_with_remove);
        binding.lvAdminMobileNumbers.setAdapter(numbersAdapter);
        numbersAdapter.setListView(binding.lvAdminMobileNumbers);

        if (getArguments() != null) {
            mViewModel.setmUid(getArguments().getString(ADMIN_UID_KEY));
        }

        this.loggedInUser = LocalStorage.getLoggedInUser(requireContext());
        if (loggedInUser == null) {
            returnResult(Activity.RESULT_CANCELED, new Intent());
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getmUid().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == null || s.isEmpty()) {
                    returnResult(Activity.RESULT_CANCELED, new Intent());
                } else {
                    mViewModel.getAdmin();
                }
            }
        });

        mViewModel.getmAdmin().observe(getViewLifecycleOwner(), person -> {
            if (person != null) {
                initializeFields(person);
            }
            else {
                returnResult(Activity.RESULT_CANCELED, new Intent());
            }
        });

        mViewModel.getmError().observe(getViewLifecycleOwner(), integer -> {
            if (integer == Checker.VALID) {
                binding.pbLoadingAddAdmin.setVisibility(View.GONE);
                setFieldsEnabled(true);
                returnResult(Activity.RESULT_OK, new Intent()
                        .putExtra(ProfileFragment.UID_KEY, mViewModel.getmUid().getValue()));
            }

            else {
                Log.d(TAG, "onChanged: has an error");
                Snackbar
                        .make(binding.btnAddAdmin, integer, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        binding.layoutAdminDatePicker.setOnClickListener(v -> showDatePickerDialog());

        binding.ibAdminCalendar.setOnClickListener(v -> showDatePickerDialog());

        binding.btnAdminAddNumber.setOnClickListener(v -> {
            String inputNumber = ContactNumber.getFormattedContactNumber(
                    binding.etAdminNumber,
                    binding.spnrAdminNumberMode,
                    requireContext().getResources()
            );
            if (inputNumber != null) {
                addMobileNumber(inputNumber);
            }
        });

        binding.btnAddAdmin.setOnClickListener(btn -> {
            String firstname = binding.etAdminFirstname.getText().toString().trim();
            String lastname = binding.etAdminLastname.getText().toString().trim();
            String middleInitial = binding.etAdminMiddleInitial.getText().toString().trim();
            String suffix = binding.etAdminSuffix.getText().toString().trim();

            String dateOfBirth = DateUtil.getDate(
                    binding.tvAdminDay.getText().toString().trim(),
                    binding.tvAdminMonth.getText().toString().trim(),
                    binding.tvAdminYear.getText().toString().trim()
            );

            String address = binding.etAdminAddress.getText().toString().trim();
            String email = binding.etAdminFormEmail.getText().toString().trim();
            int age = UIUtil.convertToInteger(binding.etAdminAge.getText().toString().trim());
            int civilStatus = binding.spnrAdminCivilStatus.getSelectedItemPosition();

            boolean isInputValid = true;

            if (!Checker.isDataAvailable(firstname)) {
                binding.etAdminFirstname.setError(getString(R.string.invalid_empty_input));
                isInputValid = false;
            }

            if (!Checker.isDataAvailable(lastname)) {
                binding.etAdminLastname.setError(getString(R.string.invalid_empty_input));
                isInputValid = false;
            }

            if (!isInputValid) {
                return;
            }

            setFieldsEnabled(false);
            binding.pbLoadingAddAdmin.setVisibility(View.VISIBLE);

            mViewModel.addAdmin(
                    firstname,
                    lastname,
                    middleInitial,
                    suffix,
                    dateOfBirth,
                    address,
                    numbersAdapter.getList(),
                    civilStatus,
                    age,
                    email,
                    loggedInUser
            );
        });

        setListeners();
        setObservers();
    }

    private void initializeFields(Person admin) {
        UIUtil.setField(admin.getFirstname(), binding.etAdminFirstname);
        UIUtil.setField(admin.getLastname(), binding.etAdminLastname);
        UIUtil.setField(admin.getMiddleInitial(), binding.etAdminMiddleInitial);
        UIUtil.setField(admin.getSuffix(), binding.etAdminSuffix);

        UIUtil.setField(admin.getAddress(), binding.etAdminAddress);
        UIUtil.setField(admin.getEmail(), binding.etAdminFormEmail);
        binding.etAdminFormEmail.setEnabled(false);

        UIUtil.setDateFields(
                admin.getDateOfBirth(),
                binding.tvAdminDay,
                binding.tvAdminMonth,
                binding.tvAdminYear
        );

        if (admin.getAge() >= 0) {
            binding.etAdminAge.setText(String.valueOf(admin.getAge()));
        }

        numbersAdapter.clear();
        if (admin.getPhoneNumber() != null) {
            for (String number : admin.getPhoneNumber()) {
                if (admin.getPhoneNumber().get(0).equals(BaseRepository.NEW_PATIENT)) {
                    break;
                }
                addMobileNumber(number);
            }
        }

        if (admin.getCivilStatus() > 0) {
            binding.spnrAdminCivilStatus.setSelection(admin.getCivilStatus());
        }
    }

    private void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment(
                binding.tvAdminMonth,
                binding.tvAdminDay,
                binding.tvAdminYear,
                binding.etAdminAge
        );

        datePickerFragment.setMaxDateToday();
        datePickerFragment.showDatePickerDialog(
                datePickerFragment,
                DateUtil.getMonthNumber(binding.tvAdminMonth.getText().toString()),
                getChildFragmentManager(),
                DatePickerFragment.BIRTH_DATE_TITLE
        );
    }

    private void addMobileNumber(String number) {
        if (number.trim().isEmpty()) {
            binding.etAdminNumber.setError(getString(R.string.invalid_empty_input));
            return;
        }

        numbersAdapter.add(number);
        numbersAdapter.notifyDataSetChanged();

        binding.etAdminNumber.setText("");
    }

    private void setObservers() {
        Resources resources = getResources();
        mViewModel.getmFirstname().observe(getViewLifecycleOwner(), new CustomObserver(binding.etAdminFirstname, resources));
        mViewModel.getMlastname().observe(getViewLifecycleOwner(), new CustomObserver(binding.etAdminLastname, resources));
        mViewModel.getmMiddleInitial().observe(getViewLifecycleOwner(), new CustomObserver(binding.etAdminMiddleInitial, resources));
        mViewModel.getmSuffix().observe(getViewLifecycleOwner(), new CustomObserver(binding.etAdminSuffix, resources));
        mViewModel.getmContact().observe(getViewLifecycleOwner(), new CustomObserver(binding.etAdminNumber, resources));
        mViewModel.getmEmail().observe(getViewLifecycleOwner(), new CustomObserver(
                binding.etAdminFormEmail, resources));
    }


    private void setListeners() {
        binding.etAdminFirstname.addTextChangedListener(
                new CustomTextWatcher(mViewModel, binding.etAdminFirstname.getHint().toString().trim()));
        binding.etAdminLastname.addTextChangedListener(
                new CustomTextWatcher(mViewModel, binding.etAdminLastname.getHint().toString().trim()));
        binding.etAdminMiddleInitial.addTextChangedListener(
                new CustomTextWatcher(mViewModel, binding.etAdminMiddleInitial.getHint().toString().trim()));
        binding.etAdminSuffix.addTextChangedListener(
                new CustomTextWatcher(mViewModel, binding.etAdminSuffix.getHint().toString().trim()));
        binding.etAdminNumber.addTextChangedListener(
                new CustomTextWatcher(mViewModel, binding.etAdminNumber.getHint().toString().trim()));
        binding.etAdminFormEmail.addTextChangedListener(
                new CustomTextWatcher(mViewModel, binding.etAdminFormEmail.getHint().toString().trim()));
    }


    private void setFieldsEnabled(boolean enabled) {
        binding.etAdminFirstname.setEnabled(enabled);
        binding.etAdminLastname.setEnabled(enabled);
        binding.etAdminMiddleInitial.setEnabled(enabled);
        binding.etAdminSuffix.setEnabled(enabled);
        binding.layoutAdminDatePicker.setEnabled(enabled);
        binding.ibAdminCalendar.setEnabled(enabled);
        binding.etAdminAge.setEnabled(enabled);
        binding.spnrAdminNumberMode.setEnabled(enabled);
        binding.etAdminNumber.setEnabled(enabled);
        binding.btnAdminAddNumber.setEnabled(enabled);
        binding.etAdminAddress.setEnabled(enabled);
        binding.spnrAdminCivilStatus.setEnabled(enabled);
        binding.btnAddAdmin.setEnabled(enabled);
    }

    private void returnResult(int activityResult, Intent intentResult) {
        requireActivity().setResult(activityResult, intentResult);
        requireActivity().finish();
    }
}