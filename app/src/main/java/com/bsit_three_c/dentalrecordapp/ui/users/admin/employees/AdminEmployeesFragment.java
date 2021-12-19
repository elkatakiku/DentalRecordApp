package com.bsit_three_c.dentalrecordapp.ui.users.admin.employees;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.databinding.FragmentAdminEmployeesBinding;

public class AdminEmployeesFragment extends Fragment {

    private AdminEmployeesViewModel mViewModel;
    private FragmentAdminEmployeesBinding binding;

    public static AdminEmployeesFragment newInstance() {
        return new AdminEmployeesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAdminEmployeesBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AdminEmployeesViewModel.class);
        // TODO: Use the ViewModel
    }

}