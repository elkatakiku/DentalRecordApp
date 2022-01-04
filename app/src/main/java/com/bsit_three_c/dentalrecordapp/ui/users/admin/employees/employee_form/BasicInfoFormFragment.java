package com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.employee_form;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ListWithRemoveItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.repository.FirebaseHelper;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormEmployee1Binding;
import com.bsit_three_c.dentalrecordapp.ui.dialog.DatePickerFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.CustomItemSelectedListener;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;

public class BasicInfoFormFragment extends Fragment {
    private static final String TAG = BasicInfoFormFragment.class.getSimpleName();

    private FragmentFormEmployee1Binding binding;
    private BasicInfoFormViewModel viewModel;

    private boolean isImageChanged;
    private ListWithRemoveItemAdapter numbersAdapter;

    private Bundle savedData;
    private boolean isEdit;

    private boolean toAdd;

    private final ActivityResultLauncher<Intent> selectImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                isImageChanged = true;
                // Get the url of the image from data
                Uri selectedImageUri = result.getData().getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    binding.ivEmployeeDisplay.setImageTintList(null);
                    binding.ivEmployeeDisplay.setImageURI(selectedImageUri);
                    viewModel.addImageUriToArguments(selectedImageUri.toString());

                    viewModel.convertImage(binding.ivEmployeeDisplay);

                    if (savedData != null) {
                        Employee employee = savedData.getParcelable(Employee.EMPLOYEE_KEY);
                        employee.setDisplayImage(selectedImageUri.toString());
                        savedData.putParcelable(Employee.EMPLOYEE_KEY, employee);
                    }
                }
            }
        }
        }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFormEmployee1Binding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(BasicInfoFormViewModel.class);
        numbersAdapter = new ListWithRemoveItemAdapter(requireContext(), R.layout.item_list_with_remove);

        binding.layoutEmployeeMobileNumbers.setAdapter(numbersAdapter);
        numbersAdapter.setListView(binding.layoutEmployeeMobileNumbers);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getmEmployee().observe(getViewLifecycleOwner(), employee -> {
            Log.d(TAG, "onChanged: employee data changed");
            if (employee != null) {
                Log.d(TAG, "onChanged: employee image: " + employee.getDisplayImage());
                isEdit = true;
                initializeFields(employee);
            }
            else {
                isEdit = false;
            }
        });

        viewModel.getmImage().observe(getViewLifecycleOwner(), bytes -> {
            Log.d(TAG, "onChanged: byte changed");
            Log.d(TAG, "onChanged: bytes: " + bytes);
            Log.d(TAG, "onChanged: to add: " + toAdd);
            if (bytes == null) {
                Snackbar
                        .make(binding.ivEmployeeDisplay, "Out of Memory. Pleas clear some memory to proceed.", Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                viewModel.createByteImage();
                if (toAdd) {
                    navigateToContactForm(viewModel.getArguments());
                }
            }
        });

        binding.btnEmployeeUpload.setOnClickListener(v -> LocalStorage.imageChooser(selectImage));

        binding.ibEmployeeCalendar.setOnClickListener(v -> showDatePickerDialog());

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

            if (!isImageChanged) {
                viewModel.convertImage(binding.ivEmployeeDisplay);
            }

            toAdd = true;
            Bundle employee = viewModel.createEmployeeData(
//                    binding.ivEmployeeDisplay,
                    firstname,
                    lastname,
                    middleInitial,
                    suffix,
                    jobTitle,
                    birthdate,
                    age,
                    numbersAdapter.getList(),
                    email,
                    address1,
                    address2,
                    civilStatusIndex
            );

            if (viewModel.getmImage().getValue() == null) {
                Log.d(TAG, "onViewCreated: no byte image");
                if (!viewModel.isConvertingImage()) {
                    Log.d(TAG, "onViewCreated: no one converting image");
                    viewModel.convertImage(binding.ivEmployeeDisplay);
                }
                return;
            }

            navigateToContactForm(employee);

        });

    }

    @Override
    public void onStart() {
        super.onStart();

        savedData = requireActivity().getIntent().getBundleExtra(getString(R.string.BUNDLE_KEY));
        if (savedData != null) {
            Log.d(TAG, "onStart: data saved");
        }

        Log.d(TAG, "onStart: data not saved");

        viewModel.setEmployee(requireActivity().getIntent().getParcelableExtra(getString(R.string.EMPLOYEE)));
    }

    private void navigateToContactForm(Bundle employee) {
        toAdd = false;
        if (savedData != null) {
            employee.putBundle(getString(R.string.BUNDLE_KEY), savedData);
        }

//            NavHostFragment.findNavController(BasicInfoFormFragment.this)
//                    .navigate(R.id.action_FirstFragment_to_Second2Fragment);
        binding.pbEmployee1Loading.setVisibility(View.GONE);
        Navigation
                .findNavController(binding.btnEmployeeNext)
                .navigate(BasicInfoFormFragmentDirections.actionFirstFragmentToSecond2Fragment(employee));
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: called");

        savedData = requireActivity().getIntent().getBundleExtra(getString(R.string.BUNDLE_KEY));
        if (savedData != null) {
            Log.d(TAG, "onResume: data saved");
            Log.d(TAG, "onResume: employee sent: " + savedData.getParcelable(Employee.EMPLOYEE_KEY));
            viewModel.setEmployee(savedData.getParcelable(Employee.EMPLOYEE_KEY));

            for (String key : savedData.keySet()) {
                Log.d(TAG, "onResume: key: " + key);
            }
        }

        Log.d(TAG, "onResume: data: " + savedData);
    }

    private void initializeFields(Employee employee) {
        Log.d(TAG, "initializeFields: initializing fields");

        if (Checker.isDataAvailable(employee.getDisplayImage())) {
            employee.loadDisplayImage(requireContext(), binding.ivEmployeeDisplay);
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

        if (Checker.isDataAvailable(employee.getDateOfBirth()) && !employee.getDateOfBirth().equals(Checker.NOT_AVAILABLE)) {
            String[] dateUnits = DateUtil.toStringArray(employee.getDateOfBirth());
            binding.tvEmployeeDay.setText(dateUnits[0]);
            binding.tvEmployeeMonth.setText(DateUtil.getMonthName(UIUtil.convertToInteger(dateUnits[1])));
            binding.tvEmployeeYear.setText(dateUnits[2]);
        }

        if (employee.getAge() >= 0)
            binding.etEmployeeAge.setText(String.valueOf(employee.getAge()));

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

    private void setListeners() {
        binding.etEmployeeFirstname.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmployeeFirstname.getHint().toString()));
        binding.etEmployeeLastname.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmployeeLastname.getHint().toString()));
        binding.etEmployeeMI.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmployeeMI.getHint().toString()));
        binding.etEmployeeSuffix.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmployeeSuffix.getHint().toString()));
        binding.etEmployeeAge.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmployeeAge.getHint().toString()));
        binding.etEmergencyAddress1.addTextChangedListener(new CustomTextWatcher(viewModel, binding.etEmergencyAddress1.getHint().toString()));
        binding.spnrEmployeeCivilStatus.setOnItemSelectedListener(new CustomItemSelectedListener((String) binding.spnrEmployeeCivilStatus.getTag(), viewModel));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}