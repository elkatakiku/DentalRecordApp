package com.bsit_three_c.dentalrecordapp.ui.appointments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.AppointmentsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentListAppointmentsBinding;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.dialog.AppointmentDialog;
import com.bsit_three_c.dentalrecordapp.ui.dialog.SuccessDialogFragment;
import com.bsit_three_c.dentalrecordapp.ui.patients.procedure_form.ProcedureFormFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.material.snackbar.Snackbar;

public class AppointmentsFragment extends Fragment {
    private static final String TAG = AppointmentsFragment.class.getSimpleName();

    public static final String PATIENT_KEY = "ARG_AF_PATIENT_KEY";
    public static final String RESULT_KEY = "APPOINTMENT_RESULT_KEY";

    private AppointmentsViewModel mViewModel;
    private FragmentListAppointmentsBinding binding;
    private String patientUid;

    public static AppointmentsFragment newInstance() {
        return new AppointmentsFragment();
    }

    public static AppointmentsFragment newInstance(String patientUid) {
        Bundle arguments = new Bundle();
        arguments.putString(PATIENT_KEY, patientUid);
        AppointmentsFragment dentalHistoryFragment = new AppointmentsFragment();
        dentalHistoryFragment.setArguments(arguments);
        return dentalHistoryFragment;
    }

    private final AppointmentsAdapter.AppointmentClickListener itemOnClickListener = this::showAppointmentDialog;

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

    private final ActivityResultLauncher<Intent> toAddProcedureResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {

                Log.d(TAG, "onActivityResult: success code: " + RESULT_OK);
                Log.d(TAG, "onActivityResult: result code: " + result.getResultCode());
                Log.d(TAG, "onActivityResult: data: " + result.getData());

                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    //  TODO: get is adding patient success
                    Log.d(TAG, "add procedure: to patient");

                    for (String key : result.getData().getExtras().keySet()) {
                        Log.d(TAG, "bundle: key: " + key);
                    }

                    int resultCode = result.getData().getIntExtra(ProcedureFormFragment.PROCEDURE_RESULT, -1);
                    Appointment appointment = result.getData().getParcelableExtra(AppointmentDialog.APPOINTMENT_KEY);

                    if (appointment != null) {
                        appointment.setDone(true);
                        mViewModel.updateAppointment(appointment);
                    }

                    if (resultCode == ProcedureFormFragment.RESULT_OK) {
                        showSuccessDialog();
                    }
                }
            });

    private void showAppointmentDialog(Appointment appointment) {
        AppointmentDialog appointmentDialog = new AppointmentDialog(
                mViewModel.getmServices().getValue(),
                appointment
        );

        appointmentDialog.setOnDoneClickListener((appointment1, isNewPatient) -> {
            if (appointment1.isDone()) {
                //  TODO: remove patient if new patient
                //  TODO: remove procedure added if patient is already in file
                Log.d(TAG, "showAppointmentDialog: removing procedure: " + appointment1.getProcedure());
                mViewModel.removeProcedure(appointment1.getPatient().getUid(), appointment1.getProcedure().getUid());
//                ProcedureRepository
//                        .getInstance()
//                        .removeProcedure(
//                                appointment1.getPatient(),
//                                appointment1.getProcedure().getUid(),
//                                appointment1.getProcedure().getPaymentKeys()
//                        );
//                    procedureRepository.removeProcedure(patient, operation.getUid(), operation.getPaymentKeys());

                appointment1.setDone(false);
                mViewModel.updateAppointment(appointment1);
            } else {
                if (isNewPatient) {
                    toAddPatientResult.launch(
                            BaseFormActivity.getPatientFormIntent(requireContext(), appointment1)
//                            new Intent(requireContext(), BaseFormActivity.class)
//                                    .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_PATIENT)
//                                    .putExtra(BaseFormActivity.APPOINTMENT_KEY, appointment1)
                    );
                } else {
                    toAddProcedureResult.launch(
                            BaseFormActivity.getProcedureFormIntent(requireContext(), appointment1)
//                            new Intent(requireContext(), BaseFormActivity.class)
//                                    .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_PROCEDURE)
//                            .putExtra(BaseFormActivity.APPOINTMENT_KEY, appointment1)
                    );
                }
//                    appointment.setDone(true);
            }
        });

        appointmentDialog.show(getChildFragmentManager(), null);
    }

    private final ActivityResultLauncher<Intent> toAddAppointmentResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {

        Log.d(TAG, "onActivityResult: success code: " + RESULT_OK);
        Log.d(TAG, "onActivityResult: result code: " + result.getResultCode());
        Log.d(TAG, "onActivityResult: data: " + result.getData());

        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            Appointment appointment = result.getData().getParcelableExtra(RESULT_KEY);
            if (appointment != null) {
                showAppointmentDialog(appointment);
//                showAppointmentDialog(binding.fabAddAppointment, appointment);
            }
        }
    });

//    private void showAppointmentDialog(View view, Appointment appointment) {
//        final View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_appointment, null);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        builder.setView(dialogView);
//
//        AppointmentDialog.DialogHolder dialogHolder = new AppointmentDialog.DialogHolder(dialogView);
//
//        dialogHolder.name.setText(appointment.getPatient().getFullName());
//        dialogHolder.contact.setText(appointment.getPatient().getContactNumber());
//        dialogHolder.date.setText(DateUtil.getReadableDate(appointment.getDateTime()));
//        dialogHolder.time.setText(DateUtil.getReadableTime(appointment.getDateTime()));
//        dialogHolder.service.setText(TextUtils.join("\n",
//                UIUtil.getServicesList(mViewModel.getmServices().getValue(), appointment.getProcedure().getServiceIds())));
//        dialogHolder.note.setText(appointment.getComments());
//        dialogHolder.btnDone.setImageResource(appointment.isDone() ? R.drawable.ic_baseline_radio_button_unchecked_24 : R.drawable.ic_baseline_check_24);
//
//        final AlertDialog appointmentDialog = builder.create();
//
//        dialogHolder.btnClose.setOnClickListener(v -> {
//            appointmentDialog.dismiss();
//        });
//
//        dialogHolder.btnDone.setOnClickListener(v -> {
//            if (appointment.isPassed()) {
//                Snackbar.make(v, "Appointment date has passed.", Snackbar.LENGTH_SHORT)
//                        .show();
//                return;
//            }
//
//            PatientRepository
//                    .getInstance()
//                    .getPath(appointment.getPatient().getUid())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    Patient patient = snapshot.getValue(Patient.class);
//
//                    if (appointment.isDone()) {
//                        appointment.setDone(false);
//                        //  TODO: remove patient data if it is added
//                    } else {
//                        if (patient == null) {
//                            toAddPatientResult.launch(
//                                    new Intent(requireContext(), BaseFormActivity.class)
//                                            .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_PATIENT)
//                                            .putExtra(BaseFormActivity.APPOINTMENT_KEY, appointment)
////                                    new Intent(requireContext(), PatientFormActivity.class)
////                                            .putExtra(LocalStorage.APPOINTMENT_KEY, appointment);
//                            );
//                        }
//                        appointment.setDone(true);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//            Log.d(TAG, "showAppointmentDialog: button done clicked");
////            if (appointment.isDone()) {
////                appointment.setDone(false);
////
////            } else {
////
//
////                if (appointment.getPatient().getUid().isEmpty()) {
////
////                }
////                appointment.setDone(true);
////            }
//
//            //  TODO: remove or add patient accordingly
////            mViewModel.updateAppointment(appointment);
//            appointmentDialog.dismiss();
//        });
//
//        appointmentDialog.show();
//    }

    private void showSuccessDialog() {
        DialogFragment dialogFragment = new SuccessDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(SuccessDialogFragment.ICON_KEY, R.drawable.ic_baseline_check_24);
        arguments.putString(SuccessDialogFragment.TITLE_KEY, "Success");
        arguments.putString(SuccessDialogFragment.MESSAGE_KEY, "Added procedure to patient successfully.");
        dialogFragment.setArguments(arguments);
        dialogFragment.show(getChildFragmentManager(), null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentListAppointmentsBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(AppointmentsViewModel.class);
        if (getArguments() != null) {
            patientUid = getArguments().getString(PATIENT_KEY);
        }

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

        mViewModel.getmError().observe(getViewLifecycleOwner(), integer -> {
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
                    ((TextView) dialogView.findViewById(R.id.tvSuccessMessage)).setText("Appointment is successfully attended.");
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        binding.fabAddAppointment.setOnClickListener(v ->
                toAddAppointmentResult.launch(
                        BaseFormActivity.getAppointmentFormIntent(requireContext())
                ));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (patientUid == null) {
            mViewModel.loadServices();
        } else {
            Log.d(TAG, "onResume: Load patient's history");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mViewModel.removeListeners();
        binding = null;
        mViewModel = null;
    }
}