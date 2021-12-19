package com.bsit_three_c.dentalrecordapp.ui.users.admin.services.view_service;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.CategoryAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAdminAddServiceBinding;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class EditServiceFragment extends Fragment {

    private FragmentAdminAddServiceBinding binding;
    private DentalService service;
    private CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminAddServiceBinding.inflate(inflater, container, false);
        service = (DentalService) requireActivity().getIntent().getParcelableExtra(LocalStorage.PARCEL_KEY);

        categoryAdapter = new CategoryAdapter(requireContext(), R.layout.item_category);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




    }

    @Override
    public void onStart() {
        super.onStart();

        binding.addServiceLayout.setBackgroundColor(Color.WHITE);

        Glide
                .with(this)
                .load(Uri.parse(service.getDisplayImage()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgDisplayPreview);

        binding.etAddServiceName.setText(service.getTitle());
        binding.etAddServiceDescription.setText(service.getDescription());
        binding.lvServiceCategory.setAdapter(categoryAdapter);



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ((ViewServiceActivity) requireActivity()).getFloatingActionButton().setVisibility(View.VISIBLE);
        binding = null;
    }

}