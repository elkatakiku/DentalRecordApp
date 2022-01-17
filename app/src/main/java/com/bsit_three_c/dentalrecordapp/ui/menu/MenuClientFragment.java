package com.bsit_three_c.dentalrecordapp.ui.menu;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.EmployeeDisplayAdapter;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceDisplaysAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentClientMenuBinding;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.dialog.ServiceDialogFragment;
import com.bsit_three_c.dentalrecordapp.ui.dialog.SuccessDialogFragment;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginActivity;
import com.bsit_three_c.dentalrecordapp.ui.main.MainAdminActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.List;

public class MenuClientFragment extends Fragment {
    private static final String TAG = MenuClientFragment.class.getSimpleName();

    private MenuClientViewModel mViewModel;
    private FragmentClientMenuBinding binding;

    public static MenuClientFragment newInstance() {
        return new MenuClientFragment();
    }

    private final ActivityResultLauncher<Intent> loginActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent getIntent = result.getData();
                LoggedInUser loggedInUser = getIntent.getParcelableExtra(LocalStorage.LOGGED_IN_USER_KEY);

                Log.d(TAG, "onActivityResult: logged in user: " + loggedInUser);
                Log.d(TAG, "onActivityResult: is logged in user null: " + (loggedInUser == null));

                if (loggedInUser != null) {
                    LocalStorage.saveLoggedInUser(requireContext(), loggedInUser);
                    Log.d(TAG, "onActivityResult: user logged in: " + loggedInUser.getDisplayName());
                    if (loggedInUser.getType() != Account.TYPE_PATIENT) {
                        requireActivity().startActivity(new Intent(requireContext(), MainAdminActivity.class));
                        requireActivity().finish();
                    } else {
                        String greet = loggedInUser.getPerson().getLastname() + "!";
                        binding.tvPatientHomeLastname.setText(greet);
                    }
                }
            }
        }
    });

    private final ActivityResultLauncher<Intent> registerActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                DialogFragment dialogFragment = new SuccessDialogFragment();
                Bundle argument = new Bundle();
                argument.putInt(SuccessDialogFragment.ICON_KEY, R.drawable.ic_baseline_check_24);
                argument.putString(SuccessDialogFragment.TITLE_KEY, "Success");
                argument.putString(SuccessDialogFragment.MESSAGE_KEY, "Account successfully created.");
                dialogFragment.setArguments(argument);
                dialogFragment.show(getChildFragmentManager(), null);
            }
        }
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentClientMenuBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(MenuClientViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ServiceDisplaysAdapter adapter = new ServiceDisplaysAdapter(requireActivity(), true);

//        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager manager = new GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false);
        binding.rvMenuServices.setHasFixedSize(true);
        binding.rvMenuServices.setLayoutManager(manager);
        adapter.setmItemOnClickListener(service -> {
            DialogFragment serviceDialog = new ServiceDialogFragment(service);
            serviceDialog.show(getChildFragmentManager(), null);
        });
        binding.rvMenuServices.setAdapter(adapter);

        EmployeeDisplayAdapter employeeDisplayAdapter = new EmployeeDisplayAdapter(requireContext());
        binding.rvMenuDoctorsDisplay.setHasFixedSize(true);
        binding.rvMenuDoctorsDisplay.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvMenuDoctorsDisplay.setAdapter(employeeDisplayAdapter);

        mViewModel.getmDentalServices().observe(getViewLifecycleOwner(), dentalServices -> {
            if (dentalServices != null) {
                binding.tvMenuServicesDefault.setVisibility(View.GONE);
                binding.pbMenuLoadingServices.setVisibility(View.GONE);
                adapter.addItems(dentalServices);
                adapter.notifyDataSetChanged();
            }
            else {
                binding.tvMenuServicesDefault.setVisibility(View.VISIBLE);
            }
        });

        mViewModel.getmEmployees().observe(getViewLifecycleOwner(), (Observer<List<Employee>>) employees -> {
            Log.d(TAG, "onChanged: employees changed");
            if (employees != null) {
                binding.pbMenuLoadingDoctors.setVisibility(View.GONE);
                binding.pbMenuLoadingDoctors.setVisibility(View.GONE);
                employeeDisplayAdapter.addItems(employees);
                employeeDisplayAdapter.notifyDataSetChanged();
            }
            else {
                binding.pbMenuLoadingDoctors.setVisibility(View.VISIBLE);
            }
        });

        mViewModel.getmCLinic().observe(getViewLifecycleOwner(), clinic -> {
            if (clinic != null) {
                UIUtil.setText(clinic.getName(), binding.tvMenuClinicName);
                UIUtil.setText(clinic.getContactNumber(), binding.tvMenuClinicContact);
                UIUtil.setText(clinic.getLocation(), binding.tvMenuClinicLocation);
            }
        });

        mViewModel.getmPatient().observe(getViewLifecycleOwner(), patient -> {
            if (patient != null) {
                binding.tvPatientHomeLastname.setText(patient.getLastname() + "!");
                binding.btnMenuLogin.setVisibility(View.GONE);
                binding.btnMenuRegister.setVisibility(View.GONE);
            }
        });

        binding.btnUserAppointment.setOnClickListener(v -> {
            if (mViewModel.getmPatient().getValue() != null) {
                Log.d(TAG, "onViewCreated: has patient");
                registerActivity
                        .launch(BaseFormActivity.getAppointmentFormIntent(
                                requireContext(),
                                mViewModel.getmPatient().getValue().getUid()));
            } else {
                registerActivity
                        .launch(BaseFormActivity.getAppointmentFormIntent(requireContext()));
            }
        }
        );

        binding.btnMenuLogin.setOnClickListener(v ->
                loginActivity.launch(new Intent(requireActivity(), LoginActivity.class)));

        binding.btnMenuRegister.setOnClickListener(v ->
                registerActivity.launch(new Intent(requireActivity(), BaseFormActivity.class)
                        .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_REGISTRATION)));
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.pbMenuLoadingServices.setVisibility(View.VISIBLE);
        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(requireContext());
        if (loggedInUser != null) {
            mViewModel.getPatient(loggedInUser.getAccount().getUserUid());
        }
        mViewModel.loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mViewModel.removeListeners();
    }
}