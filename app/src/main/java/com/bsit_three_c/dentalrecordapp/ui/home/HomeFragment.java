package com.bsit_three_c.dentalrecordapp.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.bsit_three_c.dentalrecordapp.databinding.FragmentHomeBinding;
import com.bsit_three_c.dentalrecordapp.ui.add_patient.AddPatientActivity;
import com.bsit_three_c.dentalrecordapp.ui.patient_info.PatientActivity;
import com.bsit_three_c.dentalrecordapp.util.Internet;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ItemAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this, new PatientViewModelFactory()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        Log.d(TAG, "onCreateView: start");

//        homeViewModel.runInternetTest();
        Internet.getIsOnline().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                Log.d(TAG, "onChanged: isOnline value changed to true");
                showRecyclerView();
                if (homeViewModel.isRecordEmpty() && !homeViewModel.isPatientsLoaded()) {
                    Log.d(TAG, "onChanged: getting patients");
                    new LoadPatients().execute();
                } else {
                    Log.d(TAG, "onChanged: refreshing");
                    homeViewModel.refresh(adapter);
                }
            } else {
                Log.d(TAG, "onChanged: isOnline value changed to false");
                if (homeViewModel.isRecordEmpty()) {
                    Log.d(TAG, "onChanged: empty records");
                    showError();
                }
                Internet.showSnackBarInternetError(binding.getRoot());
            }
        });

        homeViewModel.getIsPatientsGettingDone().observe(getViewLifecycleOwner(), aBoolean -> {
            Log.d(TAG, "onChanged: aBoolean: " + aBoolean);
            if (!aBoolean) {
                Log.d(TAG, "onChanged: turning progress bar on");
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "onChanged: turning progress bar off");
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: Getting patients");

        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());

        binding.recyclerView.setLayoutManager(manager);

        adapter = new ItemAdapter(getActivity(), true);
        adapter.setmItemOnClickListener(itemOnClickListener);
        binding.recyclerView.setAdapter(adapter);

        if (homeViewModel.isPatientsLoaded()) {
            Log.d(TAG, "onCreateView: patients loaded");
            homeViewModel.initializePatients(adapter);
        }


        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
//            Toast.makeText(getActivity(), "onRefresh called", Toast.LENGTH_SHORT).show();
            homeViewModel.runInternetTest();
            homeViewModel.getRepository().getPatients(adapter);
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        binding.fabAddPatients.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddPatientActivity.class));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        if (Internet.getIsOnline().getValue() != null && Internet.getIsOnline().getValue())
            homeViewModel.removeEventListener();
    }

    public void showRecyclerView() {
        Log.d(TAG, "showRecyclerView: showing recyclerview");
        binding.errorMsg.setVisibility(View.GONE);
        binding.iconWarning.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    private void showError() {
        Log.d(TAG, "showError: showing error");
        binding.recyclerView.setVisibility(View.GONE);
        binding.iconWarning.setVisibility(View.VISIBLE);
        binding.errorMsg.setVisibility(View.VISIBLE);
    }


    private class LoadPatients extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Checking if online");
            if (Internet.isOnline()) {
                Log.d(TAG, "doInBackground: isOnline: " + Internet.isOnline());
                homeViewModel.getRepository().getPatients(adapter);
            }
            Log.d(TAG, "doInBackground: Exiting async");
            return null;
        }
    }

    private final ItemViewHolder.ItemOnClickListener itemOnClickListener = person -> {
        Log.d(TAG, "person value: : " + person.toString());
        Patient patient = (Patient) person;
        Log.d(TAG, "patient value: : " + patient.toString());
        Intent toPatient = new Intent(getActivity(), PatientActivity.class);
        toPatient.putExtra(getString(R.string.PATIENT), patient);
//        Snackbar.make(binding.getRoot(), person.getFirstname(), Snackbar.LENGTH_SHORT).show();
        startActivity(toPatient);
    };

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}