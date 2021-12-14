package com.bsit_three_c.dentalrecordapp.ui.users.admin.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.adapter.ItemAdapter;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAdminServicesBinding;

public class AdminServicesFragment extends Fragment {

    private AdminServicesViewModel mViewModel;
    private FragmentAdminServicesBinding binding;

    private ItemAdapter adapter;

    public static AdminServicesFragment newInstance() {
        return new AdminServicesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminServicesBinding.inflate(inflater, container, false);

        adapter = new ItemAdapter(getActivity(), true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AdminServicesViewModel.class);
        // TODO: Use the ViewModel
    }

}