package com.bsit_three_c.dentalrecordapp.ui.users.employee.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bsit_three_c.dentalrecordapp.R;

import com.bsit_three_c.dentalrecordapp.databinding.FragmentEmployeeMenuBinding;
import com.google.android.material.snackbar.Snackbar;

public class EmployeeMenuFragment extends Fragment {

    private FragmentEmployeeMenuBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentEmployeeMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAadasd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(EmployeeMenuFragment.this)
                        .navigate(R.id.action_First2Fragment_to_SecondFragment);
            }
        });

        binding.btnStaffPatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Go to patient", Snackbar.LENGTH_SHORT).show();


            }
        });

        binding.imgBtnService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getChildFragmentManager();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}