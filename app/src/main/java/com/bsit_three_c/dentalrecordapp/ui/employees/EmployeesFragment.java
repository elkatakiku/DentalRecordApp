package com.bsit_three_c.dentalrecordapp.ui.employees;

import static android.app.Activity.RESULT_OK;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.adapter.SearchVIewAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentListEmployeesBinding;
import com.bsit_three_c.dentalrecordapp.ui.employees.employee_form.EmployeeFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.employees.view_employee.ViewEmployeeActivity;

public class EmployeesFragment extends Fragment {
    private static final String TAG = EmployeesFragment.class.getSimpleName();

    private EmployeesViewModel mViewModel;
    private FragmentListEmployeesBinding binding;
    private ItemAdapter adapter;

    private SearchView searchView = null;

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

    public static EmployeesFragment newInstance() {
        return new EmployeesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: called");

        binding = FragmentListEmployeesBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(EmployeesViewModel.class);

        adapter = new ItemAdapter(getActivity(), ItemAdapter.TYPE_EMPLOYEE);
        mViewModel.initializeEventListener(adapter);

        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.menu_search).setVisible(true);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = SearchVIewAdapter.newInstance(requireActivity(), adapter, searchItem)
                .setSearchableInfo(searchManager)
                .setListeners()
                .getSearchView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                // Not implemented here
                searchView.setIconified(false);
                return false;

            case R.id.menu_profile:
                Log.d(TAG, "onOptionsItemSelected: fragment code called");
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: called");

        mViewModel.getmGotEmployee().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                Log.d(TAG, "onChanged: turning off loading");
                binding.swipeRefreshLayoutEmployee.setRefreshing(false);
                binding.progressBarEmployee.setVisibility(View.GONE);
            }
        });

        binding.swipeRefreshLayoutEmployee.setOnRefreshListener(() -> {
            if (searchView != null && searchView.isIconified()) {
                binding.swipeRefreshLayoutEmployee.setRefreshing(true);
                mViewModel.loadPatients();
            } else {
                binding.swipeRefreshLayoutEmployee.setRefreshing(false);
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

            Log.d(TAG, "onViewCreated: sending employee: " + employee);
            startActivity(toViewEmployee);
        });

        binding.recyclerViewEmployees.setAdapter(adapter);

        binding.fabAdminAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddEmployeeResult.launch(new Intent(requireActivity(), EmployeeFormActivity.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: called");

        mViewModel.loadPatients();
    }
}