package com.bsit_three_c.dentalrecordapp.ui.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentHomeBinding;
import com.bsit_three_c.dentalrecordapp.util.Util;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ItemAdapter adapter;
    private boolean isOnline;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this, new HomeViewModelFactory()).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        Log.d(TAG, "onCreateView: start");

        homeViewModel.runTestInternet();
        homeViewModel.getIsOnline().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Log.d(TAG, "onChanged: isOnline value changed to true");
                    showRecyclerView();
                    if (homeViewModel.isRecordEmpty() && !homeViewModel.isPatientsLoaded()) {
                        Log.d(TAG, "onChanged: getting patients");
                        new LoadPatients().execute();
                    }
                    else {
                        Log.d(TAG, "onChanged: refreshing");
                        homeViewModel.refresh(adapter);
                    }
                } else {
                    Log.d(TAG, "onChanged: isOnline value changed to false");
                    if (homeViewModel.isRecordEmpty()) {
                        Log.d(TAG, "onChanged: empty records");
                        showError();
                    }
                    Util.showSnackBarInternetError(binding.getRoot());
                }
            }
        });

        homeViewModel.getIsPatientsGettingDone().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d(TAG, "onChanged: aBoolean: " + aBoolean);
                if (!aBoolean) {
                    Log.d(TAG, "onChanged: turning progress bar on");
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
                else {
                    Log.d(TAG, "onChanged: turning progress bar off");
                    binding.progressBar.setVisibility(View.GONE);
                }
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
        binding.recyclerView.setAdapter(adapter);

        if (homeViewModel.isPatientsLoaded()) {
            Log.d(TAG, "onCreateView: patients loaded");
            homeViewModel.initializePatients(adapter);
        }


        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(getActivity(), "onRefresh called", Toast.LENGTH_SHORT).show();
//            refresh();
            homeViewModel.runTestInternet();
            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        if (isOnline) homeViewModel.removeEventListener();
    }

    private void refresh() {
        if (!Util.isOnline() && homeViewModel.isRecordEmpty()) {
            Log.d(TAG, "refresh: Records are empty");
            showError();
            Util.showSnackBarInternetError(binding.getRoot());
        }
        else if (!Util.isOnline() && !homeViewModel.isRecordEmpty()) {
            Log.d(TAG, "refresh: not online and records are not empty");
            Util.showSnackBarInternetError(binding.getRoot());
        }
        else if (Util.isOnline() && homeViewModel.isRecordEmpty()) {
            Log.d(TAG, "refresh: is online and record is empty");
            new LoadPatients().execute();
            showRecyclerView();
        }
        else {
            Log.d(TAG, "refresh: online and records have values");
            showRecyclerView();
            homeViewModel.refresh(adapter);
        }
    }

    public void showRecyclerView() {
        Log.d(TAG, "showRecyclerView: showing recyclerview");
        binding.errorMsg.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
    }

    private void showError() {
        Log.d(TAG, "showError: showing error");
        binding.recyclerView.setVisibility(View.GONE);
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
            if (Util.isOnline()) {
                Log.d(TAG, "doInBackground: isOnline: " + Util.isOnline());
                homeViewModel.getRepository().setAdapterChange(adapter);
            }
            Log.d(TAG, "doInBackground: Exiting async");
            return null;
        }
    }

}