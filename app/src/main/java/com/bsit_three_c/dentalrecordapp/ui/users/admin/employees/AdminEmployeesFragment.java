package com.bsit_three_c.dentalrecordapp.ui.users.admin.employees;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentListEmployeesBinding;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.employee_form.EmployeeFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.view_employee.ViewEmployeeActivity;

public class AdminEmployeesFragment extends Fragment {
    private static final String TAG = AdminEmployeesFragment.class.getSimpleName();

    private AdminEmployeesViewModel mViewModel;
    private FragmentListEmployeesBinding binding;
    private ItemAdapter adapter;

    private final ActivityResultLauncher<Intent> toAddEmployeeResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        Log.d(TAG, "onActivityResult: success code: " + RESULT_OK);
        Log.d(TAG, "onActivityResult: resultcode: " + result.getResultCode());
        Log.d(TAG, "onActivityResult: data: " + result.getData());

        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Employee returnedData = result.getData().getParcelableExtra(getString(R.string.EMPLOYEE));

            Log.d(TAG, "onActivityResult: returned data: " + returnedData);

            startActivity(
                    new Intent(requireActivity(), ViewEmployeeActivity.class)
                            .putExtra(getString(R.string.EMPLOYEE), returnedData));
        }
    });

    public static AdminEmployeesFragment newInstance() {
        return new AdminEmployeesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: called");

        binding = FragmentListEmployeesBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(AdminEmployeesViewModel.class);

        adapter = new ItemAdapter(getActivity(), ItemAdapter.TYPE_EMPLOYEE);
        mViewModel.initializeEventListener(adapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: called");

        binding.fabAdminAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddEmployeeResult.launch(new Intent(requireActivity(), EmployeeFormActivity.class));
            }
        });

        binding.recyclerViewEmployees.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());

        binding.recyclerViewEmployees.setLayoutManager(manager);

        adapter.setmItemOnClickListener(person -> {
            //  TODO: Go to display employee info
            Employee employee = (Employee) person;
            Intent toViewEmployee = new Intent(requireActivity(), ViewEmployeeActivity.class);
            toViewEmployee.putExtra(getString(R.string.EMPLOYEE), employee);

            startActivity(toViewEmployee);
        });

        binding.recyclerViewEmployees.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: called");

        mViewModel.loadPatients();
    }
}