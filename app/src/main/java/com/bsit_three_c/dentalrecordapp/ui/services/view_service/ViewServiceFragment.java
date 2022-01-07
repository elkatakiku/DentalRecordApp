package com.bsit_three_c.dentalrecordapp.ui.services.view_service;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAdminViewServiceBinding;
import com.bsit_three_c.dentalrecordapp.ui.services.AdminServicesViewModel;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class ViewServiceFragment extends Fragment {

    private static final String TAG = ViewServiceFragment.class.getSimpleName();

    private FragmentAdminViewServiceBinding binding;
    private AdminServicesViewModel viewModel;

    private DentalService service;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        service = (DentalService) requireActivity().getIntent().getParcelableExtra(LocalStorage.PARCEL_KEY);

        binding = FragmentAdminViewServiceBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(AdminServicesViewModel.class);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvServiceViewDescription.setText(service.getDescription());

        StringBuilder categories = new StringBuilder();

        if (service.getCategories().size() <= 0) {
            binding.listServiceViewCategories.setVisibility(View.GONE);
            binding.tvNoProcedure.setVisibility(View.VISIBLE);
        } else {
            binding.listServiceViewCategories.setVisibility(View.VISIBLE);
            binding.tvNoProcedure.setVisibility(View.GONE);

            for (String category : service.getCategories()) {
                categories.append(category).append("\n");
            }
        }

        binding.listServiceViewCategories.setText(categories.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}