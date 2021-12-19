package com.bsit_three_c.dentalrecordapp.ui.users.admin.services.add_services;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.CategoryAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAdminAddServiceBinding;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class AddServiceFragment extends Fragment {
    private static final String TAG = AddServiceFragment.class.getSimpleName();

    private FragmentAdminAddServiceBinding binding;
    private AddServiceViewModel viewModel;
    private CategoryAdapter categoryAdapter;

    private boolean isEdit;
    private boolean isImageChanged;
    private DentalService service;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminAddServiceBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(AddServiceViewModel.class);
        categoryAdapter = new CategoryAdapter(requireContext(), R.layout.item_category);

        binding.lvServiceCategory.setAdapter(categoryAdapter);

        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();
        service = (DentalService) requireActivity().getIntent().getParcelableExtra(LocalStorage.PARCEL_KEY);

        if (service != null) {
            isEdit = true;
            initializeFields();
        }
        else isEdit = false;
    }

    private void initializeFields() {

//        binding.addServiceLayout.setBackgroundColor(Color.WHITE);

        Glide
                .with(this)
                .load(Uri.parse(service.getDisplayImage()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imgDisplayPreview);

        binding.etAddServiceName.setText(service.getTitle());
        binding.etAddServiceDescription.setText(service.getDescription());
        binding.lvServiceCategory.setAdapter(categoryAdapter);

        categoryAdapter.clear();

        for (String category : service.getCategories()) {
            addCategory(category);
        }

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // handle the Choose Image button to trigger
        // the image chooser function
        binding.btnAddServiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        binding.btnAddServiceCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory(binding.etAddServiceCategory.getText().toString().trim());
            }
        });

        binding.btnAddService.setOnClickListener(view1 -> {

            Intent result = new Intent();

            if (isEdit) {
                viewModel.editService(
                        service,
                        binding.imgDisplayPreview,
                        binding.etAddServiceName.getText().toString(),
                        binding.etAddServiceDescription.getText().toString(),
                        (ArrayList<String>) categoryAdapter.getCategories(),
                        isImageChanged);
            }
            else {
                viewModel.addService(
                        binding.imgDisplayPreview,
                        binding.etAddServiceName.getText().toString(),
                        binding.etAddServiceDescription.getText().toString(),
                        (ArrayList<String>) categoryAdapter.getCategories());
            }

            // If successful, return to list or view the service
            requireActivity().setResult(RESULT_OK, result);
            requireActivity().finish();
        });
    }

    private void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        selectImage.launch(Intent.createChooser(i, "Select Picture"));

    }

    private void addCategory(final String categoryInput) {

        if (categoryInput.trim().isEmpty()) {
            binding.etAddServiceCategory.setError(getString(R.string.invalid_empty_input));
            return;
        }

        categoryAdapter.add(categoryInput);
        categoryAdapter.notifyDataSetChanged();

        binding.etAddServiceCategory.setText("");
    }

//    private void uploadPicture(ImageView displayImage) {
//        Bitmap capture = ((BitmapDrawable) displayImage.getDrawable()).getBitmap();
////                    Canvas captureCanvas = new Canvas(capture);
//
////                    binding.imgDisplayPreview.draw(captureCanvas);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        capture.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//
//        byte[] data = outputStream.toByteArray();
//        String path = "services_display_image/" + UUID.randomUUID() + ".png";
//
//        Log.d(TAG, "onActivityResult: storing to firebase");
//
//        StorageReference reference = firebaseStorage.getReference(path);
//
//        StorageMetadata metadata = new StorageMetadata.Builder()
//                .setCustomMetadata("caption", "TITLE HERE")
//                .build();
//
//        UploadTask uploadTask = reference.putBytes(data, metadata);
//
//        binding.btnAddService.setEnabled(false);
//
//        uploadTask.addOnCompleteListener(requireActivity(), new OnCompleteListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                if (task.isSuccessful()) {
//                    Snackbar.make(binding.btnAddService, "Added", Snackbar.LENGTH_SHORT).show();
//                }
//
//                else if (task.isCanceled()){
//                    Snackbar.make(binding.btnAddService, "Failed", Snackbar.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    private final ActivityResultLauncher<Intent> selectImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

            isImageChanged = true;
                // Get the url of the image from data
                Uri selectedImageUri = result.getData().getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    binding.imgDisplayPreview.setImageURI(selectedImageUri);
                }
            }
        }
    });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}