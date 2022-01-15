package com.bsit_three_c.dentalrecordapp.ui.services.services_form;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormServiceBinding;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;

public class ServiceFormFragment extends Fragment {
    private static final String TAG = ServiceFormFragment.class.getSimpleName();

    private static final String SERVICE_KEY = "ARG_SF_SERVICE_KEY";

    private FragmentFormServiceBinding binding;
    private ServiceFormViewModel viewModel;
//    private ListWithRemoveItemAdapter listWithRemoveItemAdapter;

//    private boolean isEdit;
    private boolean isImageChanged;
//    private DentalService service;

    private final ActivityResultLauncher<Intent> selectImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                isImageChanged = true;
                // Get the url of the image from data
                Uri selectedImageUri = result.getData().getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout

                    Glide
                            .with(requireContext())
                            .asBitmap()
                            .load(selectedImageUri)
                            .apply(new RequestOptions().override(500, 500))
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    binding.imgDisplayPreview.setImageBitmap(resource);
                                    viewModel.setmImageByte(UIUtil.getByteArray(resource));
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    Log.d(TAG, "onLoadCleared: called");
                                }
                            });
                }
            }
        }
    });

    public static ServiceFormFragment newInstance(DentalService service) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(SERVICE_KEY, service);
        ServiceFormFragment serviceFormFragment = new ServiceFormFragment();
        serviceFormFragment.setArguments(arguments);
        return serviceFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFormServiceBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(ServiceFormViewModel.class);

        if (getArguments() != null) {
            viewModel.setmDentalService(getArguments().getParcelable(SERVICE_KEY));
        }

//        listWithRemoveItemAdapter = new ListWithRemoveItemAdapter(requireContext(), R.layout.item_list_with_remove);

//        binding.lvServiceCategory.setAdapter(listWithRemoveItemAdapter);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getmDentalService().observe(getViewLifecycleOwner(), service -> {
            if (service != null) {
//                isEdit = true;
                initializeFields(service);
            }
//            else isEdit = false;
        });

        viewModel.getmUploadAttempt().observe(getViewLifecycleOwner(), uploadStatus -> {

            Log.d(TAG, "onChanged: upload attempt status: " + uploadStatus);

            if (uploadStatus && viewModel.getmError().getValue() != null) {
                if (viewModel.getmError().getValue() == ServiceFormViewModel.VALID) {
                    returnResult();
                }
                else {
                    Snackbar
                            .make(binding.btnAddService, viewModel.getmError().getValue(), Snackbar.LENGTH_SHORT)
                            .show();
                }

                setFieldsEnabled(true);
            }
        });

        // handle the Choose Image button to trigger
        // the image chooser function
        binding.btnAddServiceImage.setOnClickListener(v ->
                LocalStorage.imageChooser(selectImage));

//        binding.btnAddServiceCategory.setOnClickListener(v ->
//                addCategory(binding.etAddServiceCategory.getText().toString().trim()));

        binding.btnAddService.setOnClickListener(view1 -> {

            String name = binding.etAddServiceName.getText().toString().trim();
            String desc = binding.etAddServiceDescription.getText().toString().trim();
//            ArrayList<String> categories = (ArrayList<String>) listWithRemoveItemAdapter.getList();

            DentalService dentalService;

            if (name.isEmpty()) {
                binding.etAddServiceName.setError(getString(R.string.invalid_empty_input));
                return;
            }

            setFieldsEnabled(false);

//            if (isEdit) {
                if (viewModel.getmDentalService().getValue() != null) {
                    dentalService = viewModel.editService(viewModel.getmDentalService().getValue(), name, desc);
                } else {
                    dentalService = new DentalService(UIUtil.capitalize(name), desc);
                }
//                        , categories);
//            }
//            else {
//                        , categories);
//            }

            viewModel.addService(dentalService);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initializeFields(DentalService dentalService) {

        Glide
                .with(this)
                .load(Uri.parse(dentalService.getDisplayImage()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgDisplayPreview);

        binding.etAddServiceName.setText(dentalService.getTitle());
        binding.etAddServiceDescription.setText(dentalService.getDescription());
//        binding.lvServiceCategory.setAdapter(listWithRemoveItemAdapter);

//        listWithRemoveItemAdapter.clear();

//        for (String category : service.getCategories()) {
//            addCategory(category);
//        }

    }

    private void setFieldsEnabled(boolean enabled) {
        Log.d(TAG, "setFieldsEnabled: setting fields enabled: " + enabled);
        binding.pbUploadLoading.setVisibility(enabled ? View.GONE : View.VISIBLE);
        binding.btnAddServiceImage.setEnabled(enabled);
        binding.etAddServiceName.setEnabled(enabled);
        binding.etAddServiceDescription.setEnabled(enabled);
//        binding.etAddServiceCategory.setEnabled(enabled);
//        binding.btnAddServiceCategory.setEnabled(enabled);
        binding.btnAddService.setEnabled(enabled);
    }

//    private void addCategory(final String categoryInput) {
//
//        if (categoryInput.trim().isEmpty()) {
//            binding.etAddServiceCategory.setError(getString(R.string.invalid_empty_input));
//            return;
//        }
//
//        listWithRemoveItemAdapter.add(UIUtil.capitalize(categoryInput));
//        listWithRemoveItemAdapter.notifyDataSetChanged();
//
//        binding.etAddServiceCategory.setText("");
//    }

    private void returnResult() {
        requireActivity().setResult(RESULT_OK, new Intent());
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}