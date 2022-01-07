package com.bsit_three_c.dentalrecordapp.ui.patients.view_patient;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceOptionsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormProcedureBinding;
import com.bsit_three_c.dentalrecordapp.util.CustomItemSelectedListener;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.Date;
import java.util.List;

public class ProcedureFormFragment extends Fragment {
    private static final String TAG = ProcedureFormFragment.class.getSimpleName();

    private FragmentFormProcedureBinding binding;
    private ProcedureFormViewModel viewModel;
    private Patient patient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFormProcedureBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(ProcedureFormViewModel.class);
        patient = ProcedureFormFragmentArgs.fromBundle(getArguments()).getItemPatient();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeServices();

        binding.spnrProcedureServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.procedureServiceError.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnAddOperation.setOnClickListener(view1 -> addProcedure());

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
                viewModel.setServicesOptions(services);
                setServicesAdapter();
            }
        });
    }

    private void setServicesAdapter() {
        Log.d(TAG, "setServicesAdapter: setting adapter");
        ServiceOptionsAdapter serviceOptionsAdapter = new ServiceOptionsAdapter(requireContext(), 0,
                viewModel.getDentalServiceOptions(), binding.spnrProcedureServices);
        binding.spnrProcedureServices.setAdapter(serviceOptionsAdapter);
    }

    private void addProcedure() {
        //  Get data from user input.
        Date date = DateUtil.getDate(binding.datePicker);
        List<String> service = UIUtil.getServices(viewModel.getDentalServiceOptions());
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
        Log.d(TAG, "onViewCreated: services selected: " + UIUtil.getServices(viewModel.getDentalServiceOptions()));


        //  Error handling of user data input.
        if (ServiceOptionsAdapter.DEFAULT_OPTION.equals(service.get(0))) {
            binding.procedureServiceError.setVisibility(View.VISIBLE);
        }
        if (dentalAmount.isEmpty()) binding.editTxtAmount.setError(getString(R.string.invalid_empty_input));
        if (isDownpayment && dentalPayment.isEmpty()) binding.editTxtAPPayment.setError(getString(R.string.invalid_empty_input));

        if (!viewModel.isDataValid(isDownpayment, service)) return;

        Log.d(TAG, "onViewCreated: Adding procedure");

        if (isDownpayment) {
            viewModel.addProcedure(
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

        requireActivity().onBackPressed();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        viewModel.removeListeners();
    }

}