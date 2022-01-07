package com.bsit_three_c.dentalrecordapp.ui.dental_chart;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.model.DentalChart;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentDentalChartBinding;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;

public class DentalChartFragment extends Fragment {
    private static final String TAG = DentalChartFragment.class.getSimpleName();

    private DentalChartViewModel mViewModel;
    private FragmentDentalChartBinding binding;

    private boolean isImageChanged;
    private boolean isDoneConvertingImage;
    private boolean toUpload;

    public static DentalChartFragment newInstance() {
        return new DentalChartFragment();
    }

    private final ActivityResultLauncher<Intent> selectImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                // Get the url of the image from data
                Uri selectedImageUri = result.getData().getData();

                isImageChanged = null != selectedImageUri;
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
                                    binding.ivDentalChart.setImageBitmap(resource);
                                    Log.d(TAG, "onResourceReady: bitmap resource: " + resource);

                                    mViewModel.setmImageByte(resource);
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentDentalChartBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(DentalChartViewModel.class);
        mViewModel.getChart();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getmDentaChart().observe(getViewLifecycleOwner(), new Observer<DentalChart>() {
            @Override
            public void onChanged(DentalChart dentalChart) {
                if (dentalChart != null) {
                    initializeChart(dentalChart);
                }
            }
        });

        mViewModel.getmImageByte().observe(getViewLifecycleOwner(), bytes -> {
            isDoneConvertingImage = bytes != null;

            if (isImageChanged && isDoneConvertingImage && toUpload) {
                mViewModel.uploadImage();
            }
        });

        binding.ibEditDentalChart.setOnClickListener(v -> LocalStorage.imageChooser(selectImage));

        binding.ibUploadDentalChart.setOnClickListener(v -> {
            binding.pbUploading.setVisibility(View.VISIBLE);
            binding.ibUploadDentalChart.setEnabled(false);
            binding.ibEditDentalChart.setEnabled(false);
            toUpload = true;

            uploadImage();
        });

        mViewModel.getmHasError().observe(getViewLifecycleOwner(), aBoolean -> {
            Log.d(TAG, "onViewCreated: error changed");
            if (!aBoolean) {
                Snackbar
                        .make(binding.ivDentalChart, "Image uploaded successfully.", Snackbar.LENGTH_SHORT)
                        .show();
            }
            else {
                Snackbar
                        .make(binding.ivDentalChart, "An error occurred while uploading the image.", Snackbar.LENGTH_SHORT)
                        .show();
            }

            isImageChanged = false;
            toUpload = false;
            isDoneConvertingImage = false;

            binding.pbUploading.setVisibility(View.GONE);
            binding.ibUploadDentalChart.setEnabled(true);
            binding.ibEditDentalChart.setEnabled(true);
        });
    }

    private void initializeChart(DentalChart dentalChart) {
        if (dentalChart.getUri() != null) {
            Glide
                    .with(requireContext())
                    .load(Uri.parse(dentalChart.getUri()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivDentalChart);
        }
    }

    private void uploadImage() {
        if (isImageChanged && isDoneConvertingImage) {
            mViewModel.uploadImage();
        }
        else {
            Snackbar
                    .make(binding.ivDentalChart, "Choose an image to upload", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}