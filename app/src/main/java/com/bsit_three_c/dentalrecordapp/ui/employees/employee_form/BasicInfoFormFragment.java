package com.bsit_three_c.dentalrecordapp.ui.employees.employee_form;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ListWithRemoveItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.repository.FirebaseHelper;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormEmployee1Binding;
import com.bsit_three_c.dentalrecordapp.ui.dialog.DatePickerFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class BasicInfoFormFragment extends Fragment {
    private static final String TAG = BasicInfoFormFragment.class.getSimpleName();

    private FragmentFormEmployee1Binding binding;
    private EmployeeSharedViewModel sharedViewModel;
    private ListWithRemoveItemAdapter numbersAdapter;

    private Employee employee;
    private boolean isEdit;

    private final ActivityResultLauncher<Intent> selectImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                // Get the url of the image from data
                Uri selectedImageUri = result.getData().getData();

                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    binding.ivEmployeeDisplay.setImageTintList(null);

                    Glide
                            .with(requireContext())
                            .asBitmap()
                            .load(selectedImageUri)
                            .apply(new RequestOptions().override(500, 500))
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    binding.ivEmployeeDisplay.setImageBitmap(resource);
                                    Log.d(TAG, "onResourceReady: bitmap resource: " + resource);

                                    sharedViewModel.setmStringUri(selectedImageUri.toString());
                                    sharedViewModel.setmImageByte(UIUtil.getByteArray(resource));
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    Log.d(TAG, "onLoadCleared: called");
                                }
                            });
                }
            }
        }
        }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFormEmployee1Binding.inflate(inflater, container, false);
        numbersAdapter = new ListWithRemoveItemAdapter(requireContext(), R.layout.item_list_with_remove);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(EmployeeSharedViewModel.class);

        binding.layoutEmployeeMobileNumbers.setAdapter(numbersAdapter);
        numbersAdapter.setListView(binding.layoutEmployeeMobileNumbers);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel.getmEmployee().observe(getViewLifecycleOwner(), mEmployee -> {
            Log.d(TAG, "onChanged: employee data changed");
            if (mEmployee != null) {
                employee = mEmployee;
                initializeFields(employee);
            }
        });

        sharedViewModel.getmEdit().observe(getViewLifecycleOwner(), aBoolean ->
                isEdit = aBoolean);

        binding.btnEmployeeUpload.setOnClickListener(v ->
                LocalStorage.imageChooser(selectImage));

        binding.ibEmployeeCalendar.setOnClickListener(v ->
                showDatePickerDialog());

        binding.btnEmployeeAddNumber.setOnClickListener(v -> {
            String inputNumber = UIUtil.getFormattedContactNumber(
                    binding.etEmployeeNumber,
                    binding.spnrEmployeeNumberMode,
                    requireContext().getResources()
            );

            if (inputNumber != null) {
                addMobileNumber(inputNumber);
            }
        });

        binding.btnEmployeeNext.setOnClickListener(v -> {

            binding.pbEmployee1Loading.setVisibility(View.VISIBLE);
            binding.btnEmployeeNext.setEnabled(false);

            // Imageview for display image: binding.displayImage
            String firstname = binding.etEmployeeFirstname.getText().toString().trim();
            String lastname = binding.etEmployeeLastname.getText().toString().trim();
            String middleInitial = binding.etEmployeeMI.getText().toString().trim();
            String suffix = binding.etEmployeeSuffix.getText().toString().trim();
            int jobTitle = binding.spnrEmployeePosition.getSelectedItemPosition();

            String birthdate = DateUtil.getDate(
                    binding.tvEmployeeDay.getText().toString().trim(),
                    binding.tvEmployeeMonth.getText().toString().trim(),
                    binding.tvEmployeeYear.getText().toString().trim()
            );

            String age = binding.etEmployeeAge.getText().toString().trim();
            String email = binding.etEmployeeEmail.getText().toString().trim();
            String address1 = binding.etEmergencyAddress1.getText().toString().trim();
            String address2 = binding.etEmergencyAddress2.getText().toString().trim();
            String civilStatus = binding.spnrEmployeeCivilStatus.getSelectedItem().toString().trim();
            int civilStatusIndex = binding.spnrEmployeeCivilStatus.getSelectedItemPosition();

            Log.d(TAG, "onClick: data inputted: " +
                    "\nfirstname: " + firstname +
                    "\nlastname: " + lastname +
                    "\nmiddle initial: " + middleInitial +
                    "\nsuffix: " + suffix +
                    "\njob: " + jobTitle +
                    "\nbday: " + birthdate +
                    "\nage: " + age +
                    "\ncontact: " + numbersAdapter.getList() +
                    "\nemail: " + email +
                    "\naddress: " + (address1 + " " + address2) +
                    "\ncivis status: " + civilStatus
            );

            if (isEdit) {
                employee = sharedViewModel.createEmployee(
                        employee,
                        firstname,
                        lastname,
                        middleInitial,
                        suffix,
                        jobTitle,
                        birthdate,
                        UIUtil.convertToInteger(age),
                        numbersAdapter.getList(),
                        email,
                        address1,
                        address2,
                        civilStatusIndex
                );
            }
            else {
                employee = new Employee(
                        firstname,
                        lastname,
                        middleInitial,
                        suffix,
                        jobTitle,
                        birthdate,
                        UIUtil.convertToInteger(age),
                        numbersAdapter.getList(),
                        email,
                        address1,
                        address2,
                        civilStatusIndex
                );
            }

            navigateToContactForm(employee);

        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Employee tempEmployee = (requireActivity().getIntent().getParcelableExtra(getString(R.string.EMPLOYEE)));
        if (tempEmployee != null && sharedViewModel.getmEmployee().getValue() == null) {
            Log.d(TAG, "onStart:EmployeeUid setting employee uid");
            sharedViewModel.setmEdit(true);
            sharedViewModel.loadEmployee(tempEmployee.getUid());
        }
    }

    private void navigateToContactForm(Employee employee) {
        setFieldsEnabled(false);

        Log.d(TAG, "navigateToContactForm: saving employee: " + employee);

        sharedViewModel.setmEmployee(employee);
        Navigation
                .findNavController(binding.btnEmployeeNext)
                .navigate(BasicInfoFormFragmentDirections.actionFirstFragmentToSecond2Fragment(employee));

        binding.pbEmployee1Loading.setVisibility(View.GONE);
    }

    private void initializeFields(Employee employee) {
        Log.d(TAG, "initializeFields: initializing fields");
        Log.d(TAG, "initializeFields: employee: " + employee);

        if (Checker.isDataAvailable(employee.getDisplayImage())) {
            employee.loadDisplayImage(requireContext(), binding.ivEmployeeDisplay);
        }
        else if (sharedViewModel.getmStringUri().getValue() != null) {
            UIUtil.loadDisplayImage(
                    requireContext(),
                    binding.ivEmployeeDisplay,
                    sharedViewModel.getmStringUri().getValue(),
                    R.drawable.ic_baseline_person_24
            );
        }

        if (Checker.isDataAvailable(employee.getFirstname())) {
            binding.etEmployeeFirstname.setText(employee.getFirstname());
        }

        if (Checker.isDataAvailable(employee.getLastname())) {
            binding.etEmployeeLastname.setText(employee.getLastname());
        }

        if (Checker.isDataAvailable(employee.getMiddleInitial())) {
            binding.etEmployeeMI.setText(employee.getMiddleInitial());
        }

        if (Checker.isDataAvailable(employee.getSuffix())) {
            binding.etEmployeeSuffix.setText(employee.getSuffix());
        }

        if (Checker.isNotDefault(employee.getJobTitle())) {
            binding.spnrEmployeePosition.setSelection(employee.getJobTitle());
        }

        if (Checker.isDataAvailable(employee.getDateOfBirth()) && !employee.getDateOfBirth().equals(Checker.NOT_AVAILABLE)) {
            String[] dateUnits = DateUtil.toStringArray(employee.getDateOfBirth());
            binding.tvEmployeeDay.setText(dateUnits[0]);
            binding.tvEmployeeMonth.setText(DateUtil.getMonthName(UIUtil.convertToInteger(dateUnits[1])));
            binding.tvEmployeeYear.setText(dateUnits[2]);
        }

        if (employee.getAge() >= 0)
            binding.etEmployeeAge.setText(String.valueOf(employee.getAge()));


        numbersAdapter.clear();
        if (employee.getPhoneNumber() != null) {
            for (String number : employee.getPhoneNumber()) {
                if (employee.getPhoneNumber().get(0).equals(FirebaseHelper.NEW_PATIENT)) {
                    break;
                }
                addMobileNumber(number);
            }
        }

        if (Checker.isDataAvailable(employee.getEmail())) {
            binding.etEmployeeEmail.setText(employee.getEmail());
        }

        if (Checker.isDataAvailable(employee.getAddress()))
            binding.etEmergencyAddress1.setText(employee.getAddress());

        if (Checker.isDataAvailable(employee.getAddress2ndPart()))
            binding.etEmergencyAddress2.setText(employee.getAddress2ndPart());

        if (employee.getCivilStatus() > 0)
            binding.spnrEmployeeCivilStatus.setSelection(employee.getCivilStatus());
    }

    private void setFieldsEnabled(boolean enabled) {
        binding.btnEmployeeUpload.setEnabled(enabled);
        binding.etEmployeeFirstname.setEnabled(enabled);
        binding.etEmployeeLastname.setEnabled(enabled);
        binding.etEmployeeMI.setEnabled(enabled);
        binding.etEmployeeSuffix.setEnabled(enabled);
        binding.spnrEmployeePosition.setEnabled(enabled);
        binding.employeeDatePicker.setEnabled(enabled);
        binding.ibEmployeeCalendar.setEnabled(enabled);
        binding.etEmployeeNumber.setEnabled(enabled);
        binding.btnEmployeeAddNumber.setEnabled(enabled);
        binding.etEmployeeEmail.setEnabled(enabled);
        binding.etEmergencyAddress1.setEnabled(enabled);
        binding.etEmergencyAddress2.setEnabled(enabled);
        binding.spnrEmployeeCivilStatus.setEnabled(enabled);
        binding.btnEmployeeNext.setEnabled(enabled);
    }

    private void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment(
                binding.tvEmployeeMonth,
                binding.tvEmployeeDay,
                binding.tvEmployeeYear,
                binding.etEmployeeAge
        );

        datePickerFragment.showDatePickerDialog(
                datePickerFragment,
                DateUtil.getMonthNumber(binding.tvEmployeeMonth.getText().toString()),
                getChildFragmentManager()
        );
    }

    private void addMobileNumber(String number) {
        if (number.trim().isEmpty()) {
            binding.etEmployeeNumber.setError(getString(R.string.invalid_empty_input));
            return;
        }

        numbersAdapter.add(number);
        numbersAdapter.notifyDataSetChanged();

        binding.etEmployeeNumber.setText("");
    }

//    private void setListeners() {
//        binding.etEmployeeFirstname.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmployeeFirstname.getHint().toString()));
//        binding.etEmployeeLastname.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmployeeLastname.getHint().toString()));
//        binding.etEmployeeMI.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmployeeMI.getHint().toString()));
//        binding.etEmployeeSuffix.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmployeeSuffix.getHint().toString()));
//        binding.etEmployeeAge.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmployeeAge.getHint().toString()));
//        binding.etEmergencyAddress1.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmergencyAddress1.getHint().toString()));
//        binding.spnrEmployeeCivilStatus.setOnItemSelectedListener(new CustomItemSelectedListener((String) binding.spnrEmployeeCivilStatus.getTag(), viewModel));
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}