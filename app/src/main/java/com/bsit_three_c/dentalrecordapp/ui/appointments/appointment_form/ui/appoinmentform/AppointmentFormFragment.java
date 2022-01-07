package com.bsit_three_c.dentalrecordapp.ui.appointments.appointment_form.ui.appoinmentform;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;

public class AppointmentFormFragment extends Fragment {

    private AppoinmentFormViewModel mViewModel;

    public static AppointmentFormFragment newInstance() {
        return new AppointmentFormFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(AppoinmentFormViewModel.class);

        return inflater.inflate(R.layout.fragment_form_appointment, container, false);
    }
}