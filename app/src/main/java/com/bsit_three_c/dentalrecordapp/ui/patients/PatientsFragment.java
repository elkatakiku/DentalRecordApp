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
import com.bsit_three_c.dentalrecordapp.databinding.FragmentListBinding;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.dialog.PopUpOptionDialog;
import com.bsit_three_c.dentalrecordapp.ui.patients.view_patient.PatientActivity;
import com.bsit_three_c.dentalrecordapp.util.Internet;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class PatientsFragment extends Fragment {
    private static final String TAG = PatientsFragment.class.getSimpleName();

    private static final String PATIENT_FILTER = "ARG_PF_PATIENT_FILTER_KEY";

    public static final int PATIENT_TODAY = 0x001ED9A9;

    private PatientsViewModel patientsViewModel;
    private FragmentListBinding binding;
    private ItemAdapter adapter;

    private SearchView searchView = null;

    public static PatientsFragment newInstance(int filter) {
        Bundle arguments = new Bundle();
        arguments.putInt(PATIENT_FILTER, filter);
        PatientsFragment patientsFragment = new PatientsFragment();
        patientsFragment.setArguments(arguments);
        return patientsFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        patientsViewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(PatientsViewModel.class);
        binding = FragmentListBinding.inflate(inflater, container, false);

        adapter = new ItemAdapter(requireContext(), ItemAdapter.TYPE_PATIENT);

        requireActivity().getActionBar();

        if (Internet.getOldInternet().isDone()) {
            Internet.getInstance().execute();
        }

        Internet.getIsOnline().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                showRecyclerView();
                adapter.notifyDataSetChanged();
            } else {
                Internet.showSnackBarInternetError(binding.getRoot());
                showError();
            }
        });

        patientsViewModel.setPatientsAdapterListener(adapter);

        patientsViewModel.getHasPatient().observe(getViewLifecycleOwner(), aBoolean -> {
            binding.listProgressBar.setVisibility(View.GONE);
            binding.listSwipeRefreshLayout.setRefreshing(false);
            binding.tvItemsWillShowHere.setVisibility(aBoolean ? View.GONE : View.VISIBLE);
            adapter.setPatientsAdapterListener(patientsViewModel.getPatientsAdapterListener());
        });

        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.menu_search).setVisible(true);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchVIewAdapter searchVIewAdapter = SearchVIewAdapter.newInstance(requireActivity(), adapter, searchItem)
                .setSearchableInfo(searchManager)
                .setListeners();
        searchView = searchVIewAdapter.getSearchView();
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

        binding.listProgressBar.setVisibility(View.VISIBLE);

        Log.d(TAG, "onViewCreated: is called");

        binding.rvList.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());

        binding.rvList.setLayoutManager(manager);

        adapter.setmItemOnClickListener(itemOnClickListener);
        binding.rvList.setAdapter(adapter);

        binding.listSwipeRefreshLayout.setOnRefreshListener(() -> {
//            Log.d(TAG, "onViewCreated: is search view iconified: " + searchView.isIconified());
            if (searchView != null && searchView.isIconified()) {
                patientsViewModel.runInternetTest();
                patientsViewModel.loadPatients();
            } else {
                binding.listSwipeRefreshLayout.setRefreshing(false);
            }
        });

        binding.fabListAdd.setOnClickListener(v -> {
            toAddPatientResult.launch(new Intent(requireContext(), BaseFormActivity.class)
                    .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_PATIENT));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        patientsViewModel.runInternetTest();

        if (searchView != null && !searchView.isIconified()) {
            return;
        }

        if (getArguments() != null) {
            patientsViewModel.loadPatients();
        }


        patientsViewModel.loadPatients();
    }

    public void showRecyclerView() {
        Log.d(TAG, "showRecyclerView: showing recyclerview");
        binding.listErrorMsg.setVisibility(View.GONE);
        binding.listIconWarning.setVisibility(View.GONE);
        binding.rvList.setVisibility(View.VISIBLE);
    }

    private void showError() {
        Log.d(TAG, "showError: showing error");
        binding.rvList.setVisibility(View.GONE);
        binding.listIconWarning.setVisibility(View.VISIBLE);
        binding.listErrorMsg.setVisibility(View.VISIBLE);
    }

    private final ItemViewHolder.ItemOnClickListener itemOnClickListener = person -> {
        Patient patient = (Patient) person;

        PopUpOptionDialog dialog = PopUpOptionDialog.newInstance(
                person.getFullName(),
                "View Information",
                "Create Appointment",
                R.drawable.ic_baseline_info_24,
                R.drawable.ic_baseline_add_card_24
        );
        dialog.setOnButtonsClickListener(new PopUpOptionDialog.OnButtonsClickListener() {
            @Override
            public void onButton1Click() {
                PatientActivity.startPatientActivity(requireContext(), patient);
            }

            @Override
            public void onButton2Click() {
                Log.d(TAG, "onButton2Click: sending patient uid: " + patient.getUid());
                BaseFormActivity.showAppointmentForm(requireContext(), patient.getUid());
            }
        });
        dialog.show(getChildFragmentManager(), null);
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
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        Log.d(TAG, "onDestroyView: destroying view");
        if (Internet.getIsOnline().getValue() != null && Internet.getIsOnline().getValue()) {
            patientsViewModel.removeEventListener();
        }
    }
}