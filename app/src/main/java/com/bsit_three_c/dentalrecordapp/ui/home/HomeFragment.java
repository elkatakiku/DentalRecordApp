package com.bsit_three_c.dentalrecordapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.add_patient.AddPatientDataSource;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    private ArrayList<Patient> patients;
    private ArrayAdapter<Patient> arrayAdapter;

    private final AddPatientDataSource addPatientDataSource = new AddPatientDataSource();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: called");
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        patients = new ArrayList<>(5);

        for (Patient patient : patients) {
            Log.d(TAG, "onViewCreated: patients objects");
            Log.d(TAG, "onViewCreated: " + patient.toString());
        }

        ArrayAdapter<Patient> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.patient_item, patients);
        binding.listViewPatients.setAdapter(arrayAdapter);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: called");

        Log.d(TAG, "onViewCreated: patients is " + patients.isEmpty());
        for (Patient patient : patients) {
            Log.d(TAG, "onViewCreated: patients objects");
            Log.d(TAG, "onViewCreated: " + patient.toString());
        }
        this.arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.patient_item, patients);
        binding.listViewPatients.setAdapter(arrayAdapter);

        addPatientDataSource.getDatabaseReference().addValueEventListener(getPatients());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        addPatientDataSource.getDatabaseReference().removeEventListener(getPatients());

    }

    private ValueEventListener getPatients() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: getting data");
                    String uid = data.getKey();
                    String firstname = String.valueOf(data.child(getString(R.string.FIRSTNAME)).getValue());
                    String lastname = String.valueOf(data.child(getString(R.string.LASTNAME)).getValue());
                    String email = String.valueOf(data.child(getString(R.string.EMAIL)).getValue());
                    String phoneNumber = String.valueOf(data.child(getString(R.string.PHONE_NUMBER)).getValue());

                    Log.d(TAG, "onDataChange: sample data:" + firstname + " " + lastname);
                    Patient patient = new Patient(firstname, lastname, email, phoneNumber, uid);

                    Log.d(TAG, "onDataChange: patient object: " + patient.toString());

                    Log.d(TAG, "onDataChange: adding to patients");
                    patients.add(patient);
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }
}