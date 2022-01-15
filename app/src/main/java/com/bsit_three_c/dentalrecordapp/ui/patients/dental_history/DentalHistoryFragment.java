package com.bsit_three_c.dentalrecordapp.ui.patients.dental_history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.DentalHistoryAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentListBinding;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class DentalHistoryFragment extends Fragment {
    private static final String TAG = DentalHistoryFragment.class.getSimpleName();

    public static final String PATIENT_KEY = "ARG_DH_PATIENT_KEY";

    private FragmentListBinding binding;
    private DentalHistoryViewModel mViewModel;
    private String patientUid;

    private final DentalHistoryAdapter.DentalHistoryClickListener dentalHistoryClickListener = new DentalHistoryAdapter.DentalHistoryClickListener() {
        @Override
        public void onAppointmentClick(DentalServiceOption dentalServiceOption) {

        }
    };

    public static DentalHistoryFragment newInstance(String patientUid) {
        Bundle arguments = new Bundle();
        arguments.putString(PATIENT_KEY, patientUid);
        DentalHistoryFragment dentalHistoryFragment = new DentalHistoryFragment();
        dentalHistoryFragment.setArguments(arguments);
        return dentalHistoryFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(DentalHistoryViewModel.class);
        binding = FragmentListBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            patientUid = getArguments().getString(PATIENT_KEY);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DentalHistoryAdapter adapter = new DentalHistoryAdapter(requireContext());
        adapter.setmAppointmentOnClickListener(dentalHistoryClickListener);

        binding.rvList.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        binding.rvList.setLayoutManager(manager);
        binding.rvList.setAdapter(adapter);

        mViewModel.getmPatientUid().observe(getViewLifecycleOwner(), s -> {
            Log.d(TAG, "onChanged: patient uid set: " + s);
            if (Checker.isDataAvailable(s)) {
                Log.d(TAG, "onChanged: loading services");
                binding.listProgressBar.setVisibility(View.VISIBLE);
                mViewModel.loadServices();
            } else {
                showEmptyList();
            }
        });

        mViewModel.getmDentalServices().observe(getViewLifecycleOwner(), dentalServices -> {
            Log.d(TAG, "onViewCreated: got dental services");
            if (dentalServices != null && !dentalServices.isEmpty()) {
                Log.d(TAG, "onViewCreated: has dental service");
                mViewModel.setServicesOptions(dentalServices);
                mViewModel.loadProcedures(patientUid);
            } else {
                Log.d(TAG, "onViewCreated: has no dental service");
            }
        });

        mViewModel.getmPatient().observe(getViewLifecycleOwner(), patient -> {
            if (patient != null) {
                Log.d(TAG, "onViewCreated: has patient");
                if (patient.getDentalProcedures().isEmpty()) {
                    Log.d(TAG, "onViewCreated: has no procedures");
                    showEmptyList();
                } else {
                    Log.d(TAG, "onViewCreated: load procedure");
                    mViewModel.loadProcedure(adapter, patient);
                }
            } else {
                Log.d(TAG, "onViewCreated: has no patient");
                showEmptyList();
            }
            binding.listProgressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    if (patientUid == null) {
        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(requireContext());
        if (loggedInUser != null) {
            patientUid = loggedInUser.getPerson().getUid();
        }
        Log.d(TAG, "onStart: logged in user: " + loggedInUser);
    }

    Log.d(TAG, "onStart: patient: " + patientUid);

    mViewModel.setmPatientUid(patientUid);
    }

    private void showEmptyList() {
        Log.d(TAG, "showEmptyList: called");
        binding.tvItemsWillShowHere.setVisibility(View.VISIBLE);
        binding.tvItemsWillShowHere.setText(getString(R.string.empty_list, "Dental history"));
    }
}