package com.bsit_three_c.dentalrecordapp.ui.users.admin.dashboard;

import android.os.Bundle;
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

    private AdminDashboardViewModel mViewModel;
    private FragmentAdminDashboardBinding binding;

    public static AdminDashboardFragment newInstance() {
        return new AdminDashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(AdminDashboardViewModel.class);
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);

        mViewModel.countPatients();

        binding.adminDashboardPatientsHeader.setOnClickListener(sendUserToPatients);
        binding.adminDashboardAppointmentHeader.setOnClickListener(sendUserToAppointments);
        binding.adminDashboardServicesCard.setOnClickListener(sendUserToServices);
        binding.adminDashboardEmployeesCard.setOnClickListener(sendUserToEmployees);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getPatientsCount().observe(getViewLifecycleOwner(), new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                binding.tvTotalPatient.setText(String.valueOf(aLong));
            }
        });

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

}