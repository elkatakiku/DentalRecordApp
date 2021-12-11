package com.bsit_three_c.dentalrecordapp.ui.users.employee.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentEmployeeServicesBinding;

public class EmployeeServicesFragment extends Fragment {

    private EmployeeServicesViewModel mViewModel;
    private FragmentEmployeeServicesBinding binding;

    public static EmployeeServicesFragment newInstance() {
        return new EmployeeServicesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employee_services, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EmployeeServicesViewModel.class);
        // TODO: Use the ViewModel
    }

}