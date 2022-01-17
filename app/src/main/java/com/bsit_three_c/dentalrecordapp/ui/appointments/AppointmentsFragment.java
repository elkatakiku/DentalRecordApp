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
import com.bsit_three_c.dentalrecordapp.databinding.FragmentListBinding;
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
    private FragmentListBinding binding;
    private String patientUid;

    public static AppointmentsFragment newInstance() {
        return new AppointmentsFragment();
    }

    public static AppointmentsFragment newInstance(String patientUid) {
        Bundle arguments = new Bundle();
        Log.d(TAG, "newInstance: passed uid: " + patientUid);
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
                appointment1.setDone(false);
                mViewModel.updateAppointment(appointment1);
            } else {
                if (isNewPatient) {
                    toAddPatientResult.launch(
                            BaseFormActivity.getPatientFormIntent(requireContext(), appointment1)
                    );
                } else {
                    toAddProcedureResult.launch(
                            BaseFormActivity.getProcedureFormIntent(requireContext(), appointment1)
                    );
                }
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
            }
        }
    });

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

        binding = FragmentListBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(AppointmentsViewModel.class);
        if (getArguments() != null) {
            Log.d(TAG, "onCreateView: getting uid");
            patientUid = getArguments().getString(PATIENT_KEY);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fabListAdd.setImageResource(R.drawable.ic_baseline_add_card_24);

        AppointmentsAdapter adapter = new AppointmentsAdapter(requireContext());
        adapter.setmAppointmentOnClickListener(itemOnClickListener);

        binding.rvList.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        binding.rvList.setLayoutManager(manager);
        binding.rvList.setAdapter(adapter);

        mViewModel.getmServices().observe(getViewLifecycleOwner(), dentalServices -> {
            if (dentalServices != null) {
                adapter.setDentalServices(dentalServices);
                if (patientUid == null) {
                    mViewModel.loadAppointments();
                    binding.fabListAdd.setOnClickListener(v ->
                            toAddAppointmentResult.launch(
                                    BaseFormActivity.getAppointmentFormIntent(requireContext())
                            ));
                } else {
                    binding.fabListAdd.setOnClickListener(v ->
                            toAddAppointmentResult.launch(
                                    BaseFormActivity.getAppointmentFormIntent(requireContext(), patientUid)
                            ));
                    mViewModel.loadAppointments(patientUid);
                }
            }
        });

        mViewModel.getmPatientAppointments().observe(getViewLifecycleOwner(), appointments -> {
            binding.listSwipeRefreshLayout.setRefreshing(false);
            if (appointments != null) {
                adapter.setList(appointments);
                adapter.notifyDataSetChanged();
            } else {
                binding.tvItemsWillShowHere.setText(getString(R.string.empty_list, "Your appointments"));
            }
        });

        mViewModel.getmAppointments().observe(getViewLifecycleOwner(), appointments -> {
            binding.listSwipeRefreshLayout.setRefreshing(false);
            if (appointments != null) {
                adapter.setList(appointments);
                adapter.notifyDataSetChanged();
            } else {
                binding.tvItemsWillShowHere.setText(getString(R.string.empty_list, "Appointments"));
            }
        });

        mViewModel.getmError().observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                if (integer != Checker.VALID) {
                    Snackbar
                            .make(binding.fabListAdd, integer, Snackbar.LENGTH_SHORT)
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

        binding.listSwipeRefreshLayout.setOnRefreshListener(() -> mViewModel.loadServices());
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