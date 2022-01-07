package com.bsit_three_c.dentalrecordapp.ui.dashboard;

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
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAdminDashboardBinding;

public class AdminDashboardFragment extends Fragment {
    private static final String TAG = AdminDashboardFragment.class.getSimpleName();

    private AdminDashboardViewModel mViewModel;
    private FragmentAdminDashboardBinding binding;

    public static AdminDashboardFragment newInstance() {
        return new AdminDashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(AdminDashboardViewModel.class);
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.adminDashboardPatientsHeader.setOnClickListener(sendUserToPatients);
        binding.adminDashboardAppointmentHeader.setOnClickListener(sendUserToAppointments);
        binding.adminDashboardServicesCard.setOnClickListener(sendUserToServices);
        binding.adminDashboardEmployeesCard.setOnClickListener(sendUserToEmployees);

        mViewModel.getPatientsCount().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long num) {
                binding.tvTotalPatient.setText(String.valueOf(num));
            }
        });

        mViewModel.getServicesCount().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long num) {
                binding.tvServicesCount.setText(String.valueOf(num));
            }
        });

        mViewModel.getEmployeesCount().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                binding.tvEmployeesCount.setText(String.valueOf(aLong));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: called");
        mViewModel.startCount();
    }

    private final View.OnClickListener sendUserToPatients = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NavHostFragment.findNavController(AdminDashboardFragment.this)
                    .navigate(R.id.nav_patients);
        }
    };

    private final View.OnClickListener sendUserToServices = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NavHostFragment.findNavController(AdminDashboardFragment.this)
                    .navigate(R.id.nav_service);
        }
    };

    private final View.OnClickListener sendUserToEmployees = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NavHostFragment.findNavController(AdminDashboardFragment.this)
                    .navigate(R.id.nav_employees);
        }
    };

    private final View.OnClickListener sendUserToAppointments = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NavHostFragment.findNavController(AdminDashboardFragment.this)
                    .navigate(R.id.nav_appointments);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        mViewModel.removeListeners();
    }
}