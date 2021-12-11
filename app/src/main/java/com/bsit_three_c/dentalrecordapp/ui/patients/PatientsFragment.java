package com.bsit_three_c.dentalrecordapp.ui.patients;

import android.content.Intent;
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
import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.adapter.ItemViewHolder;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.PatientViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentPatientsBinding;
import com.bsit_three_c.dentalrecordapp.ui.add_patient.AddPatientActivity;
import com.bsit_three_c.dentalrecordapp.ui.patient_info.PatientActivity;
import com.bsit_three_c.dentalrecordapp.util.Internet;

public class PatientsFragment extends Fragment {
    private static final String TAG = "PatientsFragment";

    private PatientsViewModel patientsViewModel;
    private FragmentPatientsBinding binding;
    private ItemAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        patientsViewModel = new ViewModelProvider(this, new PatientViewModelFactory()).get(PatientsViewModel.class);
        binding = FragmentPatientsBinding.inflate(inflater, container, false);

        adapter = new ItemAdapter(getActivity(), true);
        patientsViewModel.initializeEventListener(adapter);

        if (Internet.getOldInternet().isDone()) {
            Internet.getInstance().execute();
        }

        Internet.getIsOnline().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                showRecyclerView();
                patientsViewModel.refresh(adapter);
            } else {
                if (patientsViewModel.isRecordEmpty()) showError();
                Internet.showSnackBarInternetError(binding.getRoot());
            }
        });

        patientsViewModel.getIsPatientsGettingDone().observe(getViewLifecycleOwner(), isDone -> {
            Log.d(TAG, "onCreateView: is done: " + isDone);
//            if (!isDone) binding.progressBar.setVisibility(View.VISIBLE);
//            else
            if (isDone) binding.progressBar.setVisibility(View.GONE);
            else binding.progressBar.setVisibility(View.VISIBLE);

            Log.d(TAG, "onCreateView: progress bar: " + binding.progressBar.getVisibility());
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.progressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "onViewCreated: is called");

        binding.recyclerViewPatients.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());

        binding.recyclerViewPatients.setLayoutManager(manager);

        adapter.setmItemOnClickListener(itemOnClickListener);
        binding.recyclerViewPatients.setAdapter(adapter);

        if (patientsViewModel.isPatientsLoaded()) patientsViewModel.initializePatients(adapter);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            patientsViewModel.runInternetTest();
            patientsViewModel.loadPatients();
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        binding.fabAddPatients.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddPatientActivity.class));
        });
    }

    public void showRecyclerView() {
        Log.d(TAG, "showRecyclerView: showing recyclerview");
        binding.errorMsg.setVisibility(View.GONE);
        binding.iconWarning.setVisibility(View.GONE);
        binding.recyclerViewPatients.setVisibility(View.VISIBLE);
    }

    private void showError() {
        Log.d(TAG, "showError: showing error");
        binding.recyclerViewPatients.setVisibility(View.GONE);
        binding.iconWarning.setVisibility(View.VISIBLE);
        binding.errorMsg.setVisibility(View.VISIBLE);
    }

    private final ItemViewHolder.ItemOnClickListener itemOnClickListener = person -> {
        Patient patient = (Patient) person;
        Intent toPatient = new Intent(getActivity(), PatientActivity.class);

        toPatient.putExtra(getString(R.string.PATIENT), patient);
        startActivity(toPatient);
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        patientsViewModel.runInternetTest();
        patientsViewModel.loadPatients();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "onPause: paused called");
        if (Internet.getIsOnline().getValue() != null && Internet.getIsOnline().getValue())
            patientsViewModel.removeEventListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        Log.d(TAG, "onDestroyView: destroying view");
        if (Internet.getIsOnline().getValue() != null && Internet.getIsOnline().getValue())
            patientsViewModel.removeEventListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroyView: destroying view");
        if (Internet.getIsOnline().getValue() != null && Internet.getIsOnline().getValue())
            patientsViewModel.removeEventListener();

    }
}