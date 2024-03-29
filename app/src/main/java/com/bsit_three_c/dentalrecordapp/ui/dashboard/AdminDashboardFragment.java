package com.bsit_three_c.dentalrecordapp.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

        binding.layoutTotalPatients.setOnClickListener(sendUserToPatients);
        binding.cvAppointmentsCount.setOnClickListener(sendUserToAppointments);
        binding.adminDashboardServicesCard.setOnClickListener(sendUserToServices);
        binding.adminDashboardEmployeesCard.setOnClickListener(sendUserToEmployees);

        mViewModel.getPatientsCount().observe(getViewLifecycleOwner(), num ->
                binding.tvTotalPatient.setText(String.valueOf(num)));

        mViewModel.getServicesCount().observe(getViewLifecycleOwner(), num ->
                binding.tvServicesCount.setText(String.valueOf(num)));

        mViewModel.getEmployeesCount().observe(getViewLifecycleOwner(), aLong ->
                binding.tvEmployeesCount.setText(String.valueOf(aLong)));

        mViewModel.getmTodayCount().observe(getViewLifecycleOwner(), integer ->
                binding.tvAppointmentToday.setText(String.valueOf(integer)));

        mViewModel.getmUpComingCount().observe(getViewLifecycleOwner(), integer ->
                binding.tvAppointmentUpComing.setText(String.valueOf(integer)));

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: called");
        mViewModel.startCount();
    }

    private final View.OnClickListener sendUserToPatients = v ->
            NavHostFragment.findNavController(AdminDashboardFragment.this)
            .navigate(R.id.nav_patients);

    private final View.OnClickListener sendUserToServices = v ->
            NavHostFragment.findNavController(AdminDashboardFragment.this)
            .navigate(R.id.nav_service);

    private final View.OnClickListener sendUserToEmployees = v ->
            NavHostFragment.findNavController(AdminDashboardFragment.this)
            .navigate(R.id.nav_employees);

    private final View.OnClickListener sendUserToAppointments = v ->
            NavHostFragment.findNavController(AdminDashboardFragment.this)
            .navigate(R.id.nav_appointments);

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        mViewModel.removeListeners();
    }
}