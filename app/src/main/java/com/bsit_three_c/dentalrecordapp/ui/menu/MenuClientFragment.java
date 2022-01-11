package com.bsit_three_c.dentalrecordapp.ui.menu;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.EmployeeDisplayAdapter;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceDisplaysAdapter;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServicesViewHolder;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentClientMenuBinding;
import com.bsit_three_c.dentalrecordapp.ui.appointments.appointment_form.AppointmentFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginActivity;
import com.bsit_three_c.dentalrecordapp.ui.services.view_service.ViewServiceActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.List;

public class MenuClientFragment extends Fragment {
    private static final String TAG = MenuClientFragment.class.getSimpleName();

    private MenuClientViewModel mViewModel;
    private FragmentClientMenuBinding binding;

    public static MenuClientFragment newInstance() {
        return new MenuClientFragment();
    }

    private final ActivityResultLauncher<Intent> loginActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent getIntent = result.getData();
                LoggedInUser loggedInUser = getIntent.getParcelableExtra(LocalStorage.LOGGED_IN_USER_KEY);

                Log.d(TAG, "onActivityResult: logged in user: " + loggedInUser);
                Log.d(TAG, "onActivityResult: is logged in user null: " + (loggedInUser == null));

                if (loggedInUser != null) {
                    Log.d(TAG, "onActivityResult: user logged in: " + loggedInUser.getDisplayName());
                    requireActivity().startActivity(UIUtil.redirectUser(requireContext(), loggedInUser.getType()));
                    requireActivity().finish();
                }
            }
        }
    });

    private final ActivityResultLauncher<Intent> registerActivty = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {

            }
        }
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentClientMenuBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(MenuClientViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ServiceDisplaysAdapter adapter = new ServiceDisplaysAdapter(requireActivity(), true);

//        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager manager = new GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false);
        binding.rvMenuServices.setHasFixedSize(true);
        binding.rvMenuServices.setLayoutManager(manager);
        adapter.setmItemOnClickListener(serviceOnClickListener);
        binding.rvMenuServices.setAdapter(adapter);

        EmployeeDisplayAdapter employeeDisplayAdapter = new EmployeeDisplayAdapter(requireContext());

        binding.rvMenuDoctorsDisplay.setHasFixedSize(true);
        binding.rvMenuDoctorsDisplay.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvMenuDoctorsDisplay.setAdapter(employeeDisplayAdapter);

        mViewModel.getmDentalServices().observe(getViewLifecycleOwner(), dentalServices -> {
            if (dentalServices != null) {
                binding.tvMenuServicesDefault.setVisibility(View.GONE);
                binding.pbMenuLoadingServices.setVisibility(View.GONE);
                adapter.addItems(dentalServices);
                adapter.notifyDataSetChanged();
            }
            else {
                binding.tvMenuServicesDefault.setVisibility(View.VISIBLE);
            }
        });

        mViewModel.getmEmployees().observe(getViewLifecycleOwner(), new Observer<List<Employee>>() {
            @Override
            public void onChanged(List<Employee> employees) {
                Log.d(TAG, "onChanged: employees changed");
                if (employees != null) {
                    binding.pbMenuLoadingDoctors.setVisibility(View.GONE);
                    binding.pbMenuLoadingDoctors.setVisibility(View.GONE);
                    employeeDisplayAdapter.addItems(employees);
                    employeeDisplayAdapter.notifyDataSetChanged();
                }
                else {
                    binding.pbMenuLoadingDoctors.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.btnUserAppointment.setOnClickListener(v ->
                registerActivty.launch(new Intent(requireActivity(), AppointmentFormActivity.class)));

        binding.cvViewServices.setOnClickListener(v ->
                NavHostFragment.findNavController(MenuClientFragment.this)
                .navigate(R.id.nav_user_service));

        binding.btnUserLogin.setOnClickListener(v ->
                loginActivity.launch(new Intent(requireActivity(), LoginActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.pbMenuLoadingServices.setVisibility(View.VISIBLE);
        mViewModel.loadData();
    }

    private final ServicesViewHolder.ItemOnClickListener serviceOnClickListener = service -> {
        requireActivity().startActivity(
                new Intent(requireContext(), ViewServiceActivity.class)
                        .putExtra(LocalStorage.PARCEL_KEY, service)
                        .putExtra(LocalStorage.IS_ADMIN, false));
    };

}