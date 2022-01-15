package com.bsit_three_c.dentalrecordapp.ui.services;

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

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceDisplaysAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentListBinding;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class ServicesFragment extends Fragment {
    private static final String TAG = ServicesFragment.class.getSimpleName();

    private ServicesViewModel mViewModel;
    private FragmentListBinding binding;
    private ServiceDisplaysAdapter adapter;

    public static ServicesFragment newInstance() {
        return new ServicesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(ServicesViewModel.class);

        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(requireContext());
        if (loggedInUser == null || loggedInUser.getType() != Account.TYPE_ADMIN) {
            binding.fabListAdd.setVisibility(View.GONE);
            adapter = new ServiceDisplaysAdapter(requireActivity(), false, false);
        } else {
            adapter = new ServiceDisplaysAdapter(requireActivity(), false, true);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: called");



        binding.rvList.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());

        binding.rvList.setLayoutManager(manager);

//        adapter.setmItemOnClickListener(itemOnClickListener);
        binding.rvList.setAdapter(adapter);

        binding.fabListAdd.setOnClickListener(v -> {
            Intent toAddService = new Intent(requireContext(), BaseFormActivity.class)
                    .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_SERVICE);
//                toAddPatientResult.launch(toAddPatient);
            startActivity(toAddService);
        });

        mViewModel.getmServices().observe(getViewLifecycleOwner(), dentalServices -> {
            if (dentalServices != null) {
                adapter.addItems(dentalServices);
                adapter.notifyDataSetChanged();
            } else {
                binding.tvItemsWillShowHere.setText(getString(R.string.empty_list, "Services"));
            }

            binding.listSwipeRefreshLayout.setRefreshing(false);
        });

        binding.listSwipeRefreshLayout.setOnRefreshListener(() ->
                mViewModel.loadServices());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        mViewModel.loadServices();
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

//    private final ServicesViewHolder.ItemOnClickListener itemOnClickListener = service -> {
//        CustomDialog customDialog = new CustomDialog(requireActivity());
//        customDialog.setTitle(LocalStorage.SERVICE_KEY);
//        customDialog.setParcel(service);
//        customDialog.show();
//    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.removeListeners();
        binding = null;
    }
}