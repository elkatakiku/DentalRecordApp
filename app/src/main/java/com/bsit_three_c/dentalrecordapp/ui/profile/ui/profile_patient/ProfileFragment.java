package com.bsit_three_c.dentalrecordapp.ui.profile.ui.profile_patient;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.Clinic;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentProfileBinding;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseListActivity;
import com.bsit_three_c.dentalrecordapp.ui.employees.employee_form.EmployeeFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.profile.BaseProfileActivity;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ProfileFragment extends Fragment {
    private static final String TAG = ProfileFragment.class.getSimpleName();

    public static final String UID_KEY = "ARG_PP_UID_KEY";
    public static final String PROFILE_KEY = "ARG_PP_PROFILE_KEY";

    public static final String EDIT_INFO_KEY = "ARG_PP_EDIT_INFO_KEY";
    public static final String EDIT_ACCOUNT_KEY = "ARG_PP_EDIT_ACCOUNT_KEY";

    private ProfileViewModel mViewModel;
    private FragmentProfileBinding binding;

    private int profile;

    private final ActivityResultLauncher<Intent> editResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Log.d(TAG, "has result: " + result.getData());
            String resultUid = result.getData().getStringExtra(EDIT_INFO_KEY);
            if (resultUid != null) {
                Log.d(TAG, "setting uid: ");
                mViewModel.setUid(resultUid);
            }
        }
    });

    private final ActivityResultLauncher<Intent> editEmail = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

        }



//                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                    Log.d(TAG, "has result: " + result.getData());
//                    String resultUid = result.getData().getStringExtra(EDIT_INFO_KEY);
//                    if (resultUid != null) {
//                        Log.d(TAG, "setting uid: ");
//                        mViewModel.setUid(resultUid);
//                    }
//                }
    });

    public static ProfileFragment newInstance(String uid, int profile) {
        Bundle arguments = new Bundle();
        arguments.putString(UID_KEY, uid);
        arguments.putInt(PROFILE_KEY, profile);
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(arguments);
        return profileFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        if (getArguments() != null) {
            mViewModel.setUid(getArguments().getString(UID_KEY));
            this.profile = getArguments().getInt(PROFILE_KEY, -1);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getmUid().observe(getViewLifecycleOwner(), s -> {
            if (s != null) {
                switch (profile) {
                    case BaseProfileActivity.PROFILE_PATIENT:
                        mViewModel.getPatient(s);
                        break;
                    case BaseProfileActivity.PROFILE_EMPLOYEE:
                        mViewModel.getEmployee(s);
                        break;
                    case BaseProfileActivity.PROFILE_ADMIN:
                        mViewModel.getAdmin();
                        break;
                    default:
                        goBack();
                        break;
                }
            } else {
                goBack();
            }
        });

        mViewModel.getmPatient().observe(getViewLifecycleOwner(), patient -> {
            if (patient != null) {
                if (patient.getAccountUid() == null) {
                    goBack();
                } else {
                    initializeInfoFields(patient);
                    mViewModel.getAccount(patient.getAccountUid());
                }
            }
        });

        mViewModel.getmAdmin().observe(getViewLifecycleOwner(), person -> {
            if (person == null) {
                goBack();
            } else {
                initializeInfoFields(person);
                mViewModel.getAccount(person.getAccountUid());
            }
        });

        mViewModel.getmClinic().observe(getViewLifecycleOwner(), clinic -> {
            if (clinic != null) {
                initializeClinic(clinic);
            }
        });

        mViewModel.getmEmployee().observe(getViewLifecycleOwner(), employee -> {
            if (employee == null) {
                goBack();
            } else {
                initializeInfoFields(employee);
                mViewModel.getAccount(employee.getAccountUid());
            }
        });

        mViewModel.getmAccount().observe(getViewLifecycleOwner(), account -> {
            if (account == null) {
                requireActivity().finish();
            } else {
                initializeAccount(account);
            }
        });

        binding.tvEditAccountEmail.setOnClickListener(v -> {
            editResult.launch(new Intent(requireContext(), BaseFormActivity.class)
                    .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_UPDATE_EMAIL));
        });

        binding.tvEditAccountPassword.setOnClickListener(v ->
                editResult.launch(new Intent(requireContext(), BaseFormActivity.class)
                .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_UPDATE_PASSWORD)));
    }

    private void initializeInfoFields(Person person) {
        UIUtil.setText(person.getFullName(), binding.tvProfileName);
        UIUtil.setText(person.getFirstname(), binding.tvProfileFirstname);
        UIUtil.setText(person.getLastname(), binding.tvProfileLastname);
        UIUtil.setText(person.getMiddleInitial(), binding.tvProfileMiddleInitial);
        UIUtil.setText(person.getSuffix(), binding.tvProfileSuffix);
        UIUtil.setText(person.getBirthdate(), binding.tvProfileBirthdate);
        UIUtil.setText(person.getAge(), binding.tvProfileAge);
        UIUtil.setText(person.getContactNumber(), binding.tvProfileContactNumber);
        UIUtil.setText(person.getEmail(), binding.tvProfileEmail);
        UIUtil.setText(person.getCivilStatus(getResources()), binding.tvProfileCivilStatus);

        if (person instanceof Patient) {
            initializePatientsFields((Patient) person);
        }
        else if (person instanceof Employee) {
            initializeEmployeeFields((Employee) person);
        } else {
            initializeAdminFields(person);
        }
    }

    private void initializePatientsFields(Patient patient) {
        binding.tvProfileType.setText(requireContext().getString(R.string.PATIENT));
        binding.tvProfileJobTitleLabel.setVisibility(View.GONE);
        binding.tvProfileJobTitle.setVisibility(View.GONE);
        binding.tvProfileOccupationLabel.setVisibility(View.VISIBLE);
        binding.tvProfileOccupation.setVisibility(View.VISIBLE);
        binding.btnProfileDentalHistory.setVisibility(View.VISIBLE);
        binding.btnProfileAppointments.setVisibility(View.VISIBLE);
        binding.btnProfileAccountHistory.setVisibility(View.GONE);
        binding.llClinicInfo.setVisibility(View.GONE);

        UIUtil.setText(patient.getAddress(), binding.tvProfileAddress);
        UIUtil.setText(patient.getOccupation(), binding.tvProfileOccupation);

        binding.tvEditInfo.setOnClickListener(v -> {
            editResult.launch(new Intent(requireContext(), BaseFormActivity.class)
            .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_PATIENT)
            .putExtra(BaseFormActivity.PATIENT_KEY, patient)
            .putExtra(BaseFormActivity.APPOINTMENT_KEY, (Parcelable) null));
        });

        binding.btnProfileDentalHistory.setOnClickListener(v ->
                requireActivity().startActivity(new Intent(requireActivity(), BaseListActivity.class)
                .putExtra(BaseListActivity.LIST_KEY, BaseListActivity.DENTAL_HISTORY_LIST)
                .putExtra(getString(R.string.USER_UID_KEY), mViewModel.getmUid().getValue())));

        binding.btnProfileAppointments.setOnClickListener(v ->
                requireActivity().startActivity(new Intent(requireActivity(), BaseListActivity.class)
                .putExtra(BaseListActivity.LIST_KEY, BaseListActivity.APPOINTMENT_LIST)
                .putExtra(getString(R.string.USER_UID_KEY), mViewModel.getmUid().getValue())));
    }

    private void initializeEmployeeFields(Employee employee) {
        binding.tvProfileType.setText(employee.getJobTitle(getResources()));
        binding.tvProfileJobTitleLabel.setVisibility(View.VISIBLE);
        binding.tvProfileJobTitle.setVisibility(View.VISIBLE);
        binding.tvProfileOccupationLabel.setVisibility(View.GONE);
        binding.tvProfileOccupation.setVisibility(View.GONE);
        binding.btnProfileDentalHistory.setVisibility(View.GONE);
        binding.btnProfileAppointments.setVisibility(View.GONE);
        binding.btnProfileAccountHistory.setVisibility(View.VISIBLE);
        binding.llClinicInfo.setVisibility(View.GONE);

        Glide
                .with(requireContext())
                .load(Uri.parse(employee.getDisplayImage()))
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivProfileDisplayImage);

        UIUtil.setText(employee.getJobTitle(getResources()), binding.tvProfileJobTitle);
        UIUtil.setText(employee.getFullAddress(), binding.tvProfileAddress);

        binding.tvEditInfo.setOnClickListener(v ->
                editResult.launch(EmployeeFormActivity.getEmployeeForm(requireContext(), employee))
        );
//                editResult.launch(new Intent(requireContext(), EmployeeFormActivity.class)
//                        .putExtra(BasicInfoFormFragment.EMPLOYEE_KEY, employee)));

        binding.btnProfileAccountHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().startActivity(new Intent(requireActivity(), BaseListActivity.class)
                        .putExtra(BaseListActivity.LIST_KEY, BaseListActivity.ACCOUNT_HISTORY_LIST)
                        .putExtra(getString(R.string.USER_UID_KEY), mViewModel.getmUid().getValue())
                );
            }
        });

    }

    private void initializeAdminFields(Person person) {
        mViewModel.getClinic();
        binding.tvProfileType.setText(requireContext().getString(R.string.ADMIN));
        binding.tvProfileJobTitleLabel.setVisibility(View.GONE);
        binding.tvProfileJobTitle.setVisibility(View.GONE);
        binding.tvProfileOccupationLabel.setVisibility(View.GONE);
        binding.tvProfileOccupation.setVisibility(View.GONE);
        binding.btnProfileDentalHistory.setVisibility(View.GONE);
        binding.btnProfileAppointments.setVisibility(View.GONE);
        binding.btnProfileAccountHistory.setVisibility(View.GONE);
        binding.llClinicInfo.setVisibility(View.VISIBLE);
        binding.layoutButtons.setVisibility(View.GONE);

        UIUtil.setText(person.getAddress(), binding.tvProfileAddress);

        binding.tvEditInfo.setOnClickListener(v -> {
            editResult.launch(
                    new Intent(requireContext(), BaseFormActivity.class)
                            .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_ADMIN)
                            .putExtra(BaseFormActivity.USER_ID_KEY, person.getUid())
            );
        });

        binding.tvEditClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editResult.launch(
                        new Intent(requireContext(), BaseFormActivity.class)
                        .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_CLINIC)
                );
            }
        });
    }

    private void initializeClinic(Clinic clinic) {
        UIUtil.setText(clinic.getName(), binding.tvProfileClinicName);
        UIUtil.setText(clinic.getLocation(), binding.tvProfileClinicLocation);
        UIUtil.setText(clinic.getContactNumber(), binding.tvClinicContactNumber);

        String clinicHours = clinic.getStartDay(getResources()) +" - " + clinic.getEndDay(getResources());
        binding.tvProfileClinicClinicHours.setText(clinicHours);

        String startTime = DateUtil.getReadableTime(DateUtil.getDate(clinic.getStartTime()));
        String endTime = DateUtil.getReadableTime(DateUtil.getDate(clinic.getEndTime()));
        binding.tvProfileClinicOperatingTime.setText(startTime + " - " + endTime);
    }

    private void initializeAccount(Account account) {
        UIUtil.setText(account.getEmail(), binding.tvProfileAccountEmail);
        UIUtil.setText(UIUtil.getPasswordText(account.getPassword()), binding.tvProfilePassword);
    }

    private void goBack() {
        requireActivity().finish();
    }
}