package com.bsit_three_c.dentalrecordapp.ui.appointments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.databinding.FragmentListAppointmentsBinding;
import com.bsit_three_c.dentalrecordapp.ui.appointments.appointment_form.AppointmentActivity;

public class AppointmentsFragment extends Fragment {
    private static final String TAG = AppointmentsFragment.class.getSimpleName();

    private AppointmentsViewModel mViewModel;
    private FragmentListAppointmentsBinding binding;

    public static AppointmentsFragment newInstance() {
        return new AppointmentsFragment();
    }

    private final ActivityResultLauncher<Intent> toAddAppointmentResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {

        Log.d(TAG, "onActivityResult: success code: " + RESULT_OK);
        Log.d(TAG, "onActivityResult: resultcode: " + result.getResultCode());
        Log.d(TAG, "onActivityResult: data: " + result.getData());

        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//            Employee returnedData = result.getData().getParcelableExtra(getString(R.string.EMPLOYEE));
//
//            Log.d(TAG, "onActivityResult: returned data: " + returnedData);
//
//            startActivity(
//                    new Intent(requireActivity(), ViewEmployeeActivity.class)
//                            .putExtra(getString(R.string.EMPLOYEE), returnedData));
        }
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentListAppointmentsBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(AppointmentsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fabAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddAppointmentResult.launch(new Intent(requireActivity(), AppointmentActivity.class));
            }
        });
    }
}