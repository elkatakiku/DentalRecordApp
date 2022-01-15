package com.bsit_three_c.dentalrecordapp.ui.menu;

import android.content.Intent;
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
import androidx.navigation.fragment.NavHostFragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAdminMenuBinding;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.dialog.PopUpOptionDialog;
import com.bsit_three_c.dentalrecordapp.ui.employees.employee_form.EmployeeFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginViewModelFactory;
import com.bsit_three_c.dentalrecordapp.ui.main.MainActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class MenuAdminFragment extends Fragment {
    private static final String TAG = MenuAdminFragment.class.getSimpleName();

    private MenuAdminViewModel mViewModel;
    private FragmentAdminMenuBinding binding;

    public static MenuAdminFragment newInstance() {
        return new MenuAdminFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAdminMenuBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(MenuAdminViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getmLoggedInUser().observe(getViewLifecycleOwner(), new Observer<LoggedInUser>() {
            @Override
            public void onChanged(LoggedInUser loggedInUser) {
                if (loggedInUser == null || loggedInUser.getType() == Account.TYPE_PATIENT) {
                    logout();
                    return;
                } else if (loggedInUser.getType() == Account.TYPE_ADMIN){
                    binding.llAdminMenu.setVisibility(View.VISIBLE);
                    binding.llEmployeeMenu.setVisibility(View.GONE);
                    initializeAdminMenu();
                } else if (loggedInUser.getType() == Account.TYPE_EMPLOYEE) {
                    binding.llAdminMenu.setVisibility(View.GONE);
                    binding.llEmployeeMenu.setVisibility(View.VISIBLE);
                    initializeEmployeeMenu();
                }

                binding.tvAdminGreet.setText(getString(
                        R.string.greet_admin,
                        loggedInUser.getLastname()
                ));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: starting admin menu");

        mViewModel.setmLoggedInUser(LocalStorage.getLoggedInUser(requireContext()));
//
//        if (loggedInUser == null || loggedInUser.getType() != Account.TYPE_ADMIN || loggedInUser.getType() != Account.TYPE_EMPLOYEE) {
//            logout();
//            return;
//        } else {
//            binding.llEmployeeMenu.setVisibility(View.GONE);
//            binding.llAdminMenu.setVisibility(View.VISIBLE);
//            binding.tvAdminGreet.setText(getString(
//                    R.string.greet_admin,
//                    loggedInUser.getLastname()
//            ));
//        }
//
//        switch (loggedInUser.getType()) {
//            case Account.TYPE_ADMIN:
//                break;
//            case Account.TYPE_EMPLOYEE:
//                break;
//        }
    }

    private void initializeAdminMenu() {
        binding.bntMenuDashboard.setOnClickListener(v ->
                NavHostFragment
                        .findNavController(MenuAdminFragment.this)
                        .navigate(R.id.nav_dashboard));

        binding.btnMenuPatients.setOnClickListener(v -> {
            PopUpOptionDialog dialog = PopUpOptionDialog.newInstance(
                    "Patients",
                    "Patient Form",
                    "Patients List",
                    R.drawable.ic_baseline_person_add_24,
                    R.drawable.ic_baseline_view_list_24
            );
            dialog.setOnButtonsClickListener(new PopUpOptionDialog.OnButtonsClickListener() {
                @Override
                public void onButton1Click() {
                    requireActivity().startActivity(
                            BaseFormActivity.getPatientFormIntent(requireContext())
                    );

                }

                @Override
                public void onButton2Click() {
                    NavHostFragment
                            .findNavController(MenuAdminFragment.this)
                            .navigate(R.id.nav_patients);
                }
            });
            dialog.show(getChildFragmentManager(), null);
        });

        binding.btnMenuServices.setOnClickListener(v -> {
            PopUpOptionDialog dialog = PopUpOptionDialog.newInstance(
                    "Services",
                    "Service Form",
                    "Services List",
                    R.drawable.ic_baseline_medical_services_24,
                    R.drawable.ic_baseline_view_list_24
            );
            dialog.setOnButtonsClickListener(new PopUpOptionDialog.OnButtonsClickListener() {
                @Override
                public void onButton1Click() {
                    BaseFormActivity.showServiceForm(requireContext());
                }

                @Override
                public void onButton2Click() {
                    NavHostFragment
                            .findNavController(MenuAdminFragment.this)
                            .navigate(R.id.nav_service);
                }
            });
            dialog.show(getChildFragmentManager(), null);

        });

        binding.btnMenuEmployees.setOnClickListener(v -> {
            PopUpOptionDialog dialog = PopUpOptionDialog.newInstance(
                    "Employees",
                    "Employee Form",
                    "Employees List",
                    R.drawable.ic_baseline_group_add_24,
                    R.drawable.ic_baseline_view_list_24
            );
            dialog.setOnButtonsClickListener(new PopUpOptionDialog.OnButtonsClickListener() {
                @Override
                public void onButton1Click() {
                    EmployeeFormActivity.showEmployeeForm(requireContext());
                }

                @Override
                public void onButton2Click() {
                    NavHostFragment
                            .findNavController(MenuAdminFragment.this)
                            .navigate(R.id.nav_employees);
                }
            });
            dialog.show(getChildFragmentManager(), null);
        });

        binding.btnMenuAppointments.setOnClickListener(v -> {
            PopUpOptionDialog dialog = PopUpOptionDialog.newInstance(
                    "Appointments",
                    "Appointment Form",
                    "Appointments List",
                    R.drawable.ic_baseline_calendar_today_24,
                    R.drawable.ic_baseline_view_list_24
            );
            dialog.setOnButtonsClickListener(new PopUpOptionDialog.OnButtonsClickListener() {
                @Override
                public void onButton1Click() {
                    BaseFormActivity.showAppointmentForm(requireContext());
                }

                @Override
                public void onButton2Click() {
                    NavHostFragment
                            .findNavController(MenuAdminFragment.this)
                            .navigate(R.id.nav_appointments);
                }
            });
            dialog.show(getChildFragmentManager(), null);
        });
    }

    private void initializeEmployeeMenu() {
        binding.cbMenuEmployeePatients.setOnClickListener(v ->
                NavHostFragment
                .findNavController(MenuAdminFragment.this)
                .navigate(R.id.nav_patients));

        binding.cbMenuEmployeeServices.setOnClickListener(v ->
                NavHostFragment
                .findNavController(MenuAdminFragment.this)
                .navigate(R.id.nav_service));

        binding.cbMenuEmployeeAppointments.setOnClickListener(v ->
                NavHostFragment
                .findNavController(MenuAdminFragment.this)
                .navigate(R.id.nav_appointments));
    }

    private void logout() {
        mViewModel.logout(requireContext());

        startActivity(new Intent(requireContext(), MainActivity.class));
        requireActivity().finish();
    }

}