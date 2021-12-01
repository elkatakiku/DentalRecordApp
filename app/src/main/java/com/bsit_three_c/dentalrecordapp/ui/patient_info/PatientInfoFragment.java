package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.HistoryItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalOperation;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentPatientInfoBinding;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class PatientInfoFragment extends Fragment {
    private static final String TAG = PatientInfoFragment.class.getSimpleName();

    private FragmentPatientInfoBinding binding;
    private Patient patient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPatientInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity != null) {
//            Log.d(TAG, "onViewCreated: patient key: " + getString(R.string.PATIENT));
            Log.d(TAG, "onViewCreated: has person in intent: " + activity.getIntent().hasExtra(getString(R.string.PATIENT)));
            Log.d(TAG, "onViewCreated: intent value: " + activity.getIntent().getParcelableExtra(getString(R.string.PATIENT)));
            patient = activity.getIntent().getParcelableExtra(getString(R.string.PATIENT));
        }
        else Snackbar.make(binding.getRoot(), "activity is null", Snackbar.LENGTH_SHORT).show();

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: patient to be passed: " + patient);
                PatientInfoFragmentDirections.ActionFirst2FragmentToSecondFragment action =
                        PatientInfoFragmentDirections.actionFirst2FragmentToSecondFragment(patient);
                Navigation.findNavController(view).navigate(action);
//                NavHostFragment.findNavController(PatientInfoFragment.this)
//                        .navigate(R.id.action_First2Fragment_to_SecondFragment);
            }
        });

        displayInfo();

        ArrayList<DentalOperation> list = new ArrayList<>();
        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));
        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, false));
        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));
        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, false));
        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, false));
        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, false));
        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));
        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));
        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));
        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_civil_status, list);
        HistoryItemAdapter adapter = new HistoryItemAdapter(requireActivity(), R.layout.item_dental_history, list);

        binding.listOperations.setAdapter(adapter);

        UIUtil.setListViewHeightBasedOnItems(binding.listOperations);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void displayInfo() {
        String notAvailable = "N/A";
        if (patient.getFirstname() != null && patient.getFirstname().isEmpty()) binding.txtViewPIFirstname.setText(patient.getFirstname());
        else binding.txtViewPIFirstname.setText(notAvailable);

        if (patient.getLastname() != null) binding.txtViewPILastname.setText(patient.getLastname());
        else binding.txtViewPILastname.setText(notAvailable);

        if (patient.getMiddleInitial() != null) binding.txtViewPIMiddleInitial.setText(patient.getMiddleInitial());
        else binding.txtViewPIMiddleInitial.setText(notAvailable);

        if (patient.getAddress() != null) binding.txtViewPIAddress.setText(patient.getAddress());
        else binding.txtViewPIAddress.setText(notAvailable);

        if (patient.getPhoneNumber() != null) binding.txtViewPITelephoneNumber.setText(patient.getPhoneNumber());
        else binding.txtViewPITelephoneNumber.setText(notAvailable);

        if (patient.getAge() > 0) binding.txtViewPIAge.setText(String.valueOf(patient.getAge()));
        else binding.txtViewPIAge.setText(notAvailable);

        if (patient.getCivilStatus() != null) binding.txtViewPICivilStatus.setText(patient.getCivilStatus());
        else binding.txtViewPICivilStatus.setText(notAvailable);

        if (patient.getOccupation() != null) binding.txtViewPIOccupation.setText(patient.getOccupation());
        else binding.txtViewPIOccupation.setText(notAvailable);
    }

}