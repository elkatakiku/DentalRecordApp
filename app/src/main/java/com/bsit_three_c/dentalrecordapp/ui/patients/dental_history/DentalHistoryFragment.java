package com.bsit_three_c.dentalrecordapp.ui.patients.dental_history;

import android.os.Bundle;
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
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentListBinding;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class DentalHistoryFragment extends Fragment {
    public static final String PATIENT_KEY = "ARG_DH_PATIENT_KEY";

    private FragmentListBinding binding;
    private DentalHistoryViewModel mViewModel;
    private String patientUid;

    private final DentalHistoryAdapter.DentalHistoryClickListener dentalHistoryClickListener = new DentalHistoryAdapter.DentalHistoryClickListener() {
        @Override
        public void onAppointmentClick(Procedure procedure) {

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

        binding.fabListAdd.setVisibility(View.GONE);

        DentalHistoryAdapter adapter = new DentalHistoryAdapter(requireContext());
        adapter.setmAppointmentOnClickListener(dentalHistoryClickListener);

        binding.rvList.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        binding.rvList.setLayoutManager(manager);
        binding.rvList.setAdapter(adapter);

        mViewModel.getmPatientUid().observe(getViewLifecycleOwner(), s -> {
            if (Checker.isDataAvailable(s)) {
                binding.listProgressBar.setVisibility(View.VISIBLE);
                mViewModel.loadServices();
            } else {
                showEmptyList();
            }
        });

        mViewModel.getmDentalServices().observe(getViewLifecycleOwner(), dentalServices -> {
            if (dentalServices != null && !dentalServices.isEmpty()) {
                mViewModel.setServicesOptions(dentalServices);
                adapter.setDentalServiceOptions(mViewModel.getServiceOptions());
                mViewModel.getPatient(patientUid);
            }
        });

        mViewModel.getmPatient().observe(getViewLifecycleOwner(), patient -> {
            binding.listSwipeRefreshLayout.setRefreshing(false);
            if (patient != null) {
                if (patient.getDentalProcedures().isEmpty()) {
                    showEmptyList();
                } else {
                    mViewModel.loadProcedure(adapter, patient);
                }
            } else {
                showEmptyList();
            }
            binding.listProgressBar.setVisibility(View.GONE);
        });

        binding.listSwipeRefreshLayout.setOnRefreshListener(() -> mViewModel.setmPatientUid(patientUid));
    }

    @Override
    public void onStart() {
        super.onStart();

    if (patientUid == null) {
        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(requireContext());
        if (loggedInUser != null) {
            patientUid = loggedInUser.getPerson().getUid();
        }
    }

    mViewModel.setmPatientUid(patientUid);
    }

    private void showEmptyList() {
        binding.tvItemsWillShowHere.setVisibility(View.VISIBLE);
        binding.tvItemsWillShowHere.setText(getString(R.string.empty_list, "Dental history"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        mViewModel.removeListeners();
    }
}