package com.bsit_three_c.dentalrecordapp.ui.patients.procedure_form;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceOptionsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormProcedureBinding;
import com.bsit_three_c.dentalrecordapp.ui.dialog.AppointmentDialog;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.CustomItemSelectedListener;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProcedureFormFragment extends Fragment {
    private static final String TAG = ProcedureFormFragment.class.getSimpleName();

    public static final String PROCEDURE_RESULT = "ARG_PF_PROCEDURE_RESULT";
    public static final int  RESULT_CANCELED = 0x001ED945;
    public static final int  RESULT_OK = 0x001ED946;

    private static final String PATIENT_KEY = "ARG_PATIENT_KEY";
    private static final String APPOINTMENT_KEY = "ARG_APPOINTMENT_KEY";

    private FragmentFormProcedureBinding binding;
    private ProcedureFormViewModel viewModel;
    private Patient patient;
    private Appointment appointment;

    public static ProcedureFormFragment newInstance(Patient patient, Appointment appointment){
        ProcedureFormFragment procedureFormFragment = new ProcedureFormFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(PATIENT_KEY, patient);
        arguments.putParcelable(APPOINTMENT_KEY, appointment);
        procedureFormFragment.setArguments(arguments);
        return procedureFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFormProcedureBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(ProcedureFormViewModel.class);

        if (getArguments() != null) {
            appointment = getArguments().getParcelable(APPOINTMENT_KEY);
            if (appointment != null) {
                patient = (Patient) appointment.getPatient();
            } else {
                patient = getArguments().getParcelable(PATIENT_KEY);
            }
        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeServices();

        binding.spnrProcedureServices.setOnItemSelectedListener(
                new ServiceOptionsAdapter.selectorListener(binding.procedureServiceError));

        binding.btnProcedureConfirm.setOnClickListener(view1 -> addProcedure());

        setListeners();
        setObservers();

        //  Show ProgressNote and Balance field when downpayment checkbox is checked.
        binding.checkBoxDownpayment.setOnClickListener(v -> {
            CheckBox checkBox = (CheckBox) v;
            if (checkBox.isChecked()) {
                binding.layoutDownpayment.setVisibility(View.VISIBLE);
            }
            else{
                binding.layoutDownpayment.setVisibility(View.GONE);
            }
        });


        viewModel.getmError().observe(getViewLifecycleOwner(), integer -> {
            setFieldsEnabled(true);
            if (integer == Checker.VALID) {
                if (appointment != null) {
                    returnResult(new Intent().putExtra(PROCEDURE_RESULT, RESULT_OK)
                            .putExtra(AppointmentDialog.APPOINTMENT_KEY, appointment));
                } else {
                    requireActivity().onBackPressed();
                }
            } else {
                Snackbar.make(binding.btnProcedureConfirm, integer, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        if (appointment != null) {
            initializeFields(appointment);
        }
    }

    private void initializeServices() {
        List<DentalServiceOption> dentalServiceOptions = viewModel.getDentalServiceOptions();

        if (dentalServiceOptions.size() == 1) {
            Log.d(TAG, "initializeServices: dentalServiceOptions has only default");
            viewModel.loadServices();
        } else {
            Log.d(TAG, "initializeServices: dentalServiceOptions is not null");
            setServicesAdapter();
        }

        viewModel.getmDentalServices().observe(getViewLifecycleOwner(), services -> {
            Log.d(TAG, "initializeServices: services changed");
            if (services != null) {
                Log.d(TAG, "initializeServices: services: " + services);
                Log.d(TAG, "initializeServices: setting dental services options");
                viewModel.setServicesOptions((ArrayList<DentalService>) services, appointment);
                setServicesAdapter();
            }
        });
    }

    private void setServicesAdapter() {
        Log.d(TAG, "setServicesAdapter: setting adapter");
        ServiceOptionsAdapter serviceOptionsAdapter = new ServiceOptionsAdapter(requireContext(), 0,
                viewModel.getDentalServiceOptions(), binding.spnrProcedureServices);
        serviceOptionsAdapter.initializeSpinner();
        binding.spnrProcedureServices.setAdapter(serviceOptionsAdapter);
    }

    private void initializeFields(Appointment appointment) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(appointment.getDateTime());
        binding.procedureDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void addProcedure() {
        //  Get data from user input.
        Date date = DateUtil.getDate(binding.procedureDatePicker);
        List<String> service = UIUtil.getServiceUids(viewModel.getDentalServiceOptions());
        String dentalDesc = binding.editTxtDesc.getText().toString().trim();
        String dentalAmount = binding.editTxtAmount.getText().toString().trim();
        boolean isDownpayment = binding.checkBoxDownpayment.isChecked();
        String dentalPayment = binding.editTxtAPPayment.getText().toString().trim();
        String dentalBalance = binding.txtViewBalance.getText().toString().trim();

        Log.d(TAG, "onViewCreated: data: " + date +
                "\nservice: " + service +
                "\ndesc: " + dentalDesc +
                "\namount: " + dentalAmount +
                "\nis down: " + isDownpayment);

        Log.d(TAG, "onViewCreated: get service: " + service);
        Log.d(TAG, "onViewCreated: services state: " + viewModel.getDentalServiceOptions());
        Log.d(TAG, "onViewCreated: services selected: " + UIUtil.getServiceUids(viewModel.getDentalServiceOptions()));


        //  Error handling of user data input.
        if (ServiceOptionsAdapter.DEFAULT_OPTION.equals(service.get(0))) {
            binding.procedureServiceError.setVisibility(View.VISIBLE);
        }
        if (dentalAmount.isEmpty()) binding.editTxtAmount.setError(getString(R.string.invalid_empty_input));
        if (isDownpayment && dentalPayment.isEmpty()) binding.editTxtAPPayment.setError(getString(R.string.invalid_empty_input));

        if (!viewModel.isDataValid(isDownpayment, service)) return;

        setFieldsEnabled(false);;

        Log.d(TAG, "onViewCreated: Adding procedure");

        if (appointment != null && patient == null) {
            Log.d(TAG, "addProcedure: is appointment");
            returnResult(viewModel.createResultIntent(
                    appointment,
//                    service,
                    dentalDesc,
//                    date,
                    dentalAmount,
                    isDownpayment,
                    dentalPayment,
                    dentalBalance
            )
            .putExtra(AppointmentDialog.APPOINTMENT_KEY, appointment));
            return;
        } else if (appointment != null){
            Log.d(TAG, "addProcedure: adding procedure to patient");
            viewModel.uploadProcedure(
                    appointment,
                    dentalDesc,
                    dentalAmount,
                    isDownpayment,
                    dentalPayment,
                    dentalBalance);
            return;
        }

        Log.d(TAG, "addProcedure: adding procedure");

        if (isDownpayment) {
            viewModel.uploadProcedure(
                    patient,
                    service,
                    dentalDesc,
                    date,
                    dentalAmount,
                    isDownpayment,
                    dentalPayment,
                    dentalBalance);
        }
        else {
            viewModel.addProcedure(patient, service, dentalDesc, date, dentalAmount, isDownpayment);
        }

        Log.d(TAG, "onViewCreated: Done adding Procedure");

//        requireActivity().onBackPressed();
    }

    private void setFieldsEnabled(boolean enabled) {
        binding.procedureDatePicker.setEnabled(enabled);
        binding.spnrProcedureServices.setEnabled(enabled);
        binding.editTxtDesc.setEnabled(enabled);
        binding.editTxtAmount.setEnabled(enabled);
        binding.checkBoxDownpayment.setEnabled(enabled);
        binding.editTxtAPPayment.setEnabled(enabled);
        binding.btnProcedureConfirm.setEnabled(enabled);
    }

    private void setListeners() {
        //  EditText text listeners
        binding.editTxtDesc.addTextChangedListener(
                new CustomTextWatcher(viewModel, binding.txtViewDescLabel.getText().toString()));
        binding.editTxtAmount.addTextChangedListener(
                new CustomTextWatcher(viewModel, binding.txtViewAmountLabel.getText().toString()));
        binding.editTxtAPPayment.addTextChangedListener(
                new CustomTextWatcher(viewModel, binding.txtViewPaymentLabel.getText().toString()));

        //  Spinner item selected listeners
        binding.snprProcedureChoices.setOnItemSelectedListener(new CustomItemSelectedListener(binding.tvServicesLabel.getText().toString(), viewModel));
    }

    private void setObservers() {
        Resources resources = getResources();
        viewModel.getmAmount().observe(getViewLifecycleOwner(), new CustomObserver(binding.editTxtAmount, resources));
        viewModel.getmPayment().observe(getViewLifecycleOwner(), new CustomObserver(binding.editTxtAPPayment, resources));

        viewModel.getmBalance().observe(getViewLifecycleOwner(), formState -> {
            Log.d(TAG, "setObservers: balance changed");
            if (formState == null) return;

            Log.d(TAG, "setObservers: editing balance");

            if (formState.getMsgError() != null) {
                binding.txtViewBalance.setText(getString(formState.getMsgError()));
            }
            else {
                String balance = viewModel.getBalance().toString();
                binding.txtViewBalance.setText(balance);
            }
        });
    }

    private void returnResult(Intent result) {
        result.putExtra(LocalStorage.PATIENT_KEY, appointment.getPatient());
        requireActivity().setResult(Activity.RESULT_OK, result);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        viewModel.removeListeners();
    }

}