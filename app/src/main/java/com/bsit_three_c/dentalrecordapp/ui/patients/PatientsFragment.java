package com.bsit_three_c.dentalrecordapp.ui.patients;

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
import com.bsit_three_c.dentalrecordapp.data.adapter.ItemViewHolder;
import com.bsit_three_c.dentalrecordapp.data.adapter.SearchVIewAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentListPatientsBinding;
import com.bsit_three_c.dentalrecordapp.ui.patients.patient_form.AddPatientActivity;
import com.bsit_three_c.dentalrecordapp.ui.patients.view_patient.PatientActivity;
import com.bsit_three_c.dentalrecordapp.util.Internet;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class PatientsFragment extends Fragment {
    private static final String TAG = "PatientsFragment";

    private PatientsViewModel patientsViewModel;
    private FragmentListPatientsBinding binding;
    private ItemAdapter adapter;

    private SearchView searchView = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        patientsViewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(PatientsViewModel.class);
        binding = FragmentListPatientsBinding.inflate(inflater, container, false);

        adapter = new ItemAdapter(getActivity(), ItemAdapter.TYPE_PATIENT);
        patientsViewModel.initializeEventListener(adapter);

        requireActivity().getActionBar();

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

            if (isDone) {
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
            }
            else {
                binding.progressBar.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "onCreateView: progress bar: " + binding.progressBar.getVisibility());
        });

        setHasOptionsMenu(true);

        return binding.getRoot();
    }


//    private SearchView.OnQueryTextListener queryTextListener;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_search).setVisible(true);
//        menu.clear();
//        inflater.inflate(R.menu.main, menu);

        Log.d(TAG, "onCreateOptionsMenu: called");

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = SearchVIewAdapter.newInstance(requireActivity(), adapter, searchItem)
                .setSearchableInfo(searchManager)
                .setListeners()
                .getSearchView();

//        if (searchItem != null) {
//            searchView = (SearchView) searchItem.getActionView();
//            searchView.setQueryHint("Search patients here");
//            searchView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_field, requireActivity().getTheme()));
//            searchView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.tint_blue_green)));
//        }
//        if (searchView != null && searchItem != null) {
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
//
//            queryTextListener = new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    adapter.getFilter().filter(newText);
//                    Log.i("onQueryTextChange", newText);
//
//                    return false;
//                }
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    Log.i("onQueryTextSubmit", query);
//                    adapter.getFilter().filter(query);
//                    searchView.clearFocus();
//                    return false;
//                }
//            };
//            searchView.setOnQueryTextListener(queryTextListener);
//            searchView.setOnCloseListener(() -> {
//                searchView.setQuery("", false);
//                searchItem.collapseActionView();
////                searchView.setIconified(true);
//                return false;
//            });
//        }

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                // Not implemented here
//                searchView.performClick();
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

        binding.progressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "onViewCreated: is called");

        binding.recyclerViewPatients.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());

        binding.recyclerViewPatients.setLayoutManager(manager);

        adapter.setmItemOnClickListener(itemOnClickListener);
        binding.recyclerViewPatients.setAdapter(adapter);

        if (patientsViewModel.isPatientsLoaded()) patientsViewModel.initializePatients(adapter);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
//            Log.d(TAG, "onViewCreated: is search view iconified: " + searchView.isIconified());
            if (searchView != null && searchView.isIconified()) {
                patientsViewModel.runInternetTest();
                patientsViewModel.loadPatients();
            } else {
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        binding.fabAddPatients.setOnClickListener(v -> {
            toAddPatientResult.launch(new Intent(getActivity(), AddPatientActivity.class));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        patientsViewModel.runInternetTest();
        patientsViewModel.loadPatients();
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
        Intent toPatient = new Intent(requireActivity(), PatientActivity.class);

        toPatient.putExtra(getString(R.string.PATIENT), patient);
        startActivity(toPatient);
    };

    private final ActivityResultLauncher<Intent> toAddPatientResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        Log.d(TAG, "onActivityResult: success code: " + RESULT_OK);
        Log.d(TAG, "onActivityResult: resultcode: " + result.getResultCode());
        Log.d(TAG, "onActivityResult: data: " + result.getData());

        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Patient patient  = result.getData().getParcelableExtra(LocalStorage.UPDATED_PATIENT_KEY);
            Log.d(TAG, "patient: : " + patient);

            startActivity(
                    new Intent(requireActivity(), PatientActivity.class)
                            .putExtra(getString(R.string.PATIENT), patient));
        }
    });

    @Override
    public void onPause() {
        super.onPause();

        if (Internet.getIsOnline().getValue() != null && Internet.getIsOnline().getValue())
            patientsViewModel.removeEventListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        Log.d(TAG, "onDestroyView: destroying view");
        if (Internet.getIsOnline().getValue() != null && Internet.getIsOnline().getValue()) {
            patientsViewModel.removeEventListener();
        }
    }
}