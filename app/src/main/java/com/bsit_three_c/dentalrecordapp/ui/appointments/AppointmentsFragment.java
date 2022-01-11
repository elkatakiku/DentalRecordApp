package com.bsit_three_c.dentalrecordapp.ui.appointments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.AppointmentsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentListAppointmentsBinding;
import com.bsit_three_c.dentalrecordapp.ui.appointments.appointment_form.AppointmentFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.patients.patient_form.PatientFormActivity;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AppointmentsFragment extends Fragment {
    private static final String TAG = AppointmentsFragment.class.getSimpleName();

    public static final String RESULT_KEY = "APPOINTMENT_RESULT_KEY";

    private AppointmentsViewModel mViewModel;
    private FragmentListAppointmentsBinding binding;

    public static AppointmentsFragment newInstance() {
        return new AppointmentsFragment();
    }

    private final AppointmentsAdapter.AppointmentClickListener itemOnClickListener = new AppointmentsAdapter.AppointmentClickListener() {
        @Override
        public void onAppointmentClick(Appointment appointment) {
            Snackbar
                    .make(binding.fabAddAppointment, "Show Appointment Dialog", Snackbar.LENGTH_SHORT)
                    .show();
            showAppointmentDialog(binding.fabAddAppointment, appointment);
        }
    };

    private final ActivityResultLauncher<Intent> toAddAppointmentResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {

        Log.d(TAG, "onActivityResult: success code: " + RESULT_OK);
        Log.d(TAG, "onActivityResult: result code: " + result.getResultCode());
        Log.d(TAG, "onActivityResult: data: " + result.getData());

        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Appointment appointment = result.getData().getParcelableExtra(RESULT_KEY);
            if (appointment != null) {
                showAppointmentDialog(binding.fabAddAppointment, appointment);
            }
        }
    });

    private final ActivityResultLauncher<Intent> toAddPatientResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {

                Log.d(TAG, "onActivityResult: success code: " + RESULT_OK);
                Log.d(TAG, "onActivityResult: result code: " + result.getResultCode());
                Log.d(TAG, "onActivityResult: data: " + result.getData());

                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    //  TODO: get is adding patient success
                    mViewModel.addPatient(result.getData());
                }
            });

    private void showAppointmentDialog(View view, Appointment appointment) {
        final View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_appointment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AppointmentsAdapter.DialogHolder dialogHolder = new AppointmentsAdapter.DialogHolder(dialogView);

        dialogHolder.name.setText(appointment.getPatient().getFullName());
        dialogHolder.contact.setText(appointment.getPatient().getContactNumber());
        dialogHolder.date.setText(DateUtil.getReadableDate(appointment.getDateTime()));
        dialogHolder.time.setText(DateUtil.getReadableTime(appointment.getDateTime()));
        dialogHolder.service.setText(TextUtils.join("\n",
                UIUtil.getServicesList(mViewModel.getmServices().getValue(), appointment.getProcedure().getServiceIds())));
        dialogHolder.note.setText(appointment.getComments());
        dialogHolder.btnDone.setImageResource(appointment.isDone() ? R.drawable.ic_baseline_radio_button_unchecked_24 : R.drawable.ic_baseline_check_24);

        final AlertDialog appointmentDialog = builder.create();

        dialogHolder.btnClose.setOnClickListener(v -> {
            appointmentDialog.dismiss();
        });

        dialogHolder.btnDone.setOnClickListener(v -> {
            if (appointment.isPassed()) {
                Snackbar.make(v, "Appointment date has passed.", Snackbar.LENGTH_SHORT)
                        .show();
                return;
            }

            PatientRepository
                    .getInstance()
                    .getPath(appointment.getPatient().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Patient patient = snapshot.getValue(Patient.class);

                    if (appointment.isDone()) {
                        appointment.setDone(false);
                        //  TODO: remove patient data if it is added
                    } else {
                        if (patient == null) {
                            toAddPatientResult.launch(
                                    new Intent(requireContext(), PatientFormActivity.class)
                                            .putExtra(LocalStorage.APPOINTMENT_KEY, appointment)
                            );
                        }
                        appointment.setDone(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Log.d(TAG, "showAppointmentDialog: button done clicked");
//            if (appointment.isDone()) {
//                appointment.setDone(false);
//
//            } else {
//

//                if (appointment.getPatient().getUid().isEmpty()) {
//
//                }
//                appointment.setDone(true);
//            }

            //  TODO: remove or add patient accordingly
//            mViewModel.updateAppointment(appointment);
            appointmentDialog.dismiss();
        });

        appointmentDialog.show();
    }

    private void showSuccessDialog() {

    }

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


        AppointmentsAdapter adapter = new AppointmentsAdapter(requireContext());
        adapter.setmAppointmentOnClickListener(itemOnClickListener);

        binding.rvAppointmentList.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        binding.rvAppointmentList.setLayoutManager(manager);
        binding.rvAppointmentList.setAdapter(adapter);

        mViewModel.getmServices().observe(getViewLifecycleOwner(), dentalServices -> {
            if (dentalServices != null) {
                adapter.setDentalServices(dentalServices);
                mViewModel.loadAppointments();
            }
        });

        mViewModel.getmAppointments().observe(getViewLifecycleOwner(), appointments -> {
            if (appointments != null) {
                adapter.setList(appointments);
                adapter.notifyDataSetChanged();
            }
        });

        mViewModel.getmError().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null) {
                    if (integer != Checker.VALID) {
                        Snackbar
                                .make(binding.fabAddAppointment, integer, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                    else {
                        //  Show a dialog that confirms success
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        final View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_appointment, null);
                        ((TextView) dialogView.findViewById(R.id.tvAppointmentSuccess)).setText("Appointment is successfully attended.");
                        builder.setView(dialogView);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            }
        });

        binding.fabAddAppointment.setOnClickListener(v ->
                toAddAppointmentResult.launch(new Intent(requireActivity(), AppointmentFormActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();

        mViewModel.loadServices();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mViewModel.removeListeners();
        binding = null;
        mViewModel = null;
    }
}