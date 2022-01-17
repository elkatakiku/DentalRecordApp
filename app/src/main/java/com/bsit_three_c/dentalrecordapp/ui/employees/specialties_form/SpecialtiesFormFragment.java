package com.bsit_three_c.dentalrecordapp.ui.employees.specialties_form;

import android.app.AlertDialog;
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

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ListWithRemoveItemAdapter;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormSpecialtiesBinding;

public class SpecialtiesFormFragment extends Fragment {
    private static final String EMPLOYEE_UID = "ARG_SF_EMPLOYEE_UID";

    private SpecialtiesViewModel mViewModel;
    private FragmentFormSpecialtiesBinding binding;
    private ListWithRemoveItemAdapter specialtiesAdapter;

    private int specialtiesCount;

    public static SpecialtiesFormFragment newInstance(String employeeUid) {
        Bundle arguments = new Bundle();
        arguments.putString(EMPLOYEE_UID, employeeUid);
        SpecialtiesFormFragment specialtiesFormFragment = new SpecialtiesFormFragment();
        specialtiesFormFragment.setArguments(arguments);
        return specialtiesFormFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(SpecialtiesViewModel.class);
        binding = FragmentFormSpecialtiesBinding.inflate(inflater, container, false);
        specialtiesAdapter = new ListWithRemoveItemAdapter(requireContext(), R.layout.item_list_with_remove);
        binding.layoutEditSpecialtiesList.setAdapter(specialtiesAdapter);

        if (getArguments() != null) {
            mViewModel.setmEmployeeUid(getArguments().getString(EMPLOYEE_UID));
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getmEmployeeUid().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    mViewModel.getEmployee(s);
                } else {
                    requireActivity().finish();
                }
            }
        });

        mViewModel.getmEmployee().observe(getViewLifecycleOwner(), employee -> {
            if (employee != null) {
                specialtiesCount = employee.getSpecialties().size();
                specialtiesAdapter.clear();
                if (employee.getSpecialties() != null) {
                    for (String specialty : employee.getSpecialties()) {
                        addSpecialty(specialty);
                    }
                }
            } else {
                requireActivity().finish();
            }
        });

        binding.ibAddSpecialties.setOnClickListener(v -> {
            String specialty = binding.etSpecialty.getText().toString().trim();
            addSpecialty(specialty);
        });

        specialtiesAdapter.setListListener(count -> {
            Log.d("COUNT", "onListChangeListener: count: " + count);
            if (count > 0) {
                binding.tvNoSpecialties.setVisibility(View.GONE);
            } else {
                binding.tvNoSpecialties.setVisibility(View.VISIBLE);
            }
        });

        binding.btnSpecialtiesConfirm.setOnClickListener(v -> {
            mViewModel.updateEmployee(specialtiesAdapter.getList());
        });

        mViewModel.getmIsDone().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                requireActivity().finish();
            }
        });

        binding.btnSpecialtiesCancel.setOnClickListener(v -> {
            if (specialtiesCount != specialtiesAdapter.getCount()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Cancel")
                        .setMessage("Are you sure you want to go back? Any unsaved changes will be lost.")
                        .setPositiveButton("Ok", (dialog, which) -> requireActivity().finish())
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                builder.create().show();
            } else {
                requireActivity().finish();
            }
        });
    }

    private void addSpecialty(String specialty) {
        if (specialty.trim().isEmpty()) {
            binding.etSpecialty.setError(getString(R.string.invalid_empty_input));
            return;
        }

        specialtiesAdapter.add(specialty);
        specialtiesAdapter.notifyDataSetChanged();

        binding.etSpecialty.setText("");
    }
}