package com.bsit_three_c.dentalrecordapp.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentGalleryBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGallery;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSavePatient.setOnClickListener(v -> {
            savePatients();
            Snackbar.make(v, "Patients added to database", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void savePatients() {
        final ArrayList<String> firstname = new ArrayList<>(5);
        firstname.add("Eli");
        firstname.add("Jeng");
        firstname.add("Eper");
        firstname.add("Bana");
        firstname.add("Gale");

        final ArrayList<String> lastname = new ArrayList<>(5);
        lastname.add("Lamzon");
        lastname.add("Albaciete");
        lastname.add("Adaza");
        lastname.add("Magno");
        lastname.add("Fernandez");

        final ArrayList<String> email = new ArrayList<>(5);
        email.add("email1@email.com");
        email.add("email2@email.com");
        email.add("email3@email.com");
        email.add("email4@email.com");
        email.add("email5@email.com");

        final ArrayList<String> number = new ArrayList<>(5);
        number.add("09123548697");
        number.add("09123548766");
        number.add("09482346159");
        number.add("09187456321");
        number.add("09483625189");

        for (int i = 0; i < 5; i++) {
            Patient patient = new Patient(firstname.get(i), lastname.get(i),email.get(i), number.get(i));
            galleryViewModel.addPatient(patient);
        }
    }
}