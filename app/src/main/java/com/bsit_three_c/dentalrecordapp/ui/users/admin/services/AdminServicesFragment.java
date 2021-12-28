package com.bsit_three_c.dentalrecordapp.ui.users.admin.services;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bsit_three_c.dentalrecordapp.MainActivity;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceDisplaysAdapter;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServicesViewHolder;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAdminServicesBinding;
import com.bsit_three_c.dentalrecordapp.ui.dialog.CustomDialog;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.services.services_form.ServiceFormActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class AdminServicesFragment extends Fragment {
    private static final String TAG = AdminServicesFragment.class.getSimpleName();

    private AdminServicesViewModel mViewModel;
    private FragmentAdminServicesBinding binding;

    public static AdminServicesFragment newInstance() {
        return new AdminServicesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminServicesBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(AdminServicesViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: called");

        ServiceDisplaysAdapter adapter = new ServiceDisplaysAdapter(requireActivity());

        binding.rvServices.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());

        binding.rvServices.setLayoutManager(manager);

        adapter.setmItemOnClickListener(itemOnClickListener);
        binding.rvServices.setAdapter(adapter);

        mViewModel.initializeRepository(adapter);
        mViewModel.loadServices();

        binding.fabAddService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAddPatient = new Intent(requireActivity(), ServiceFormActivity.class);
//                toAddPatientResult.launch(toAddPatient);
                startActivity(toAddPatient);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();


        if (requireActivity() instanceof MainActivity) {
            binding.fabAddService.setVisibility(View.GONE);
        }
    }

    private final ActivityResultLauncher<Intent> toAddPatientResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                startActivity(
//                        new Intent(requireActivity(), ViewServiceActivity.class)
//                        .putExtra()
//                );
//                Snackbar.make(binding.getRoot(), "Got result", Snackbar.LENGTH_SHORT).show();
            }
        }
    });

    private final ServicesViewHolder.ItemOnClickListener itemOnClickListener = service -> {
        CustomDialog customDialog = new CustomDialog(requireActivity());
        customDialog.setTitle(LocalStorage.DIALOG_SERVICE);
        customDialog.setParcel(service);
        customDialog.show();
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.removeListeners();
        binding = null;
    }
}