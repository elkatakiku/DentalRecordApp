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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAdminMenuBinding;
import com.bsit_three_c.dentalrecordapp.ui.dialog.CustomDialog;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginViewModelFactory;
import com.bsit_three_c.dentalrecordapp.ui.main.MainActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class MenuAdminFragment extends Fragment {
    private static final String TAG = MenuAdminFragment.class.getSimpleName();

    private MenuAdminViewModel mViewModel;
    private FragmentAdminMenuBinding binding;

    private CustomDialog customDialog;

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

        binding.bntMenuDashboard.setOnClickListener(v -> NavHostFragment.findNavController(MenuAdminFragment.this)
                .navigate(R.id.nav_dashboard));

        binding.btnMenuPatients.setOnClickListener(v -> {
            customDialog = new CustomDialog(requireActivity(), NavHostFragment.findNavController(MenuAdminFragment.this));
            customDialog.setTitle(LocalStorage.PATIENT_KEY);
            customDialog.show();
        });

        binding.btnMenuServices.setOnClickListener(v -> NavHostFragment.findNavController(MenuAdminFragment.this)
                .navigate(R.id.nav_service));

        binding.btnMenuEmployees.setOnClickListener(v -> {
            customDialog = new CustomDialog(requireActivity(), NavHostFragment.findNavController(MenuAdminFragment.this));
            customDialog.setTitle(LocalStorage.EMPLOYEE_KEY);
            customDialog.show();
        });

        binding.btnMenuAppointments.setOnClickListener(v -> {
            customDialog = new CustomDialog(requireActivity(), NavHostFragment.findNavController(MenuAdminFragment.this));
            customDialog.setTitle(LocalStorage.APPOINTMENT_KEY);
            customDialog.show();
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: starting admin menu");

        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(requireContext());

//        if (loggedInUser == null || loggedInUser.getType() != Account.TYPE_ADMIN) {
//            logout();
//            return;
//        }

        binding.tvAdminGreet.setText(getString(
                R.string.greet_admin,
                loggedInUser != null ? loggedInUser.getLastname() : "Admin"
        ));
    }

    private void logout() {
        mViewModel.logout(requireContext());

        startActivity(new Intent(requireContext(), MainActivity.class));
        requireActivity().finish();
    }

}