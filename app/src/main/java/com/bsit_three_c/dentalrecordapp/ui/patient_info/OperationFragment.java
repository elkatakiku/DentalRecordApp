package com.bsit_three_c.dentalrecordapp.ui.patient_info;

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
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.ServiceOption;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.PatientViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAddOperationBinding;
import com.bsit_three_c.dentalrecordapp.util.CustomItemSelectedListener;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.ArrayList;
import java.util.Date;

public class OperationFragment extends Fragment {
    private static final String TAG = OperationFragment.class.getSimpleName();

    private FragmentAddOperationBinding binding;
    private OperationViewModel viewModel;
    private Patient patient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddOperationBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new PatientViewModelFactory()).get(OperationViewModel.class);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        patient = OperationFragmentArgs.fromBundle(getArguments()).getItemPatient();

        binding.btnAddOperation.setOnClickListener(view1 -> {

            Date date = UIUtil.getDate(binding.datePicker);
            int service = binding.snprProcedureChoices.getSelectedItemPosition();
            String dentalDesc = binding.editTxtDesc.getText().toString();
            int modeOfPayment = binding.spnrModeOfPayment.getSelectedItemPosition();
            String dentalAmount = binding.editTxtAmount.getText().toString();
            boolean isDownpayment = binding.checkBoxDownpayment.isChecked();

            if (isDownpayment) {
                String dentalPayment = binding.editTxtAPPayment.getText().toString();
                String dentalBalance = binding.txtViewBalance.getText().toString();
                viewModel.addProcedure(
                        patient,
                        service,
                        dentalDesc,
                        date,
//                        modeOfPayment,
                        dentalAmount,
                        isDownpayment,
                        dentalPayment,
                        dentalBalance);
            }
            else {
                viewModel.addProcedure(patient, service, dentalDesc, date, modeOfPayment, dentalAmount, isDownpayment);
            }

            requireActivity().onBackPressed();
        });

        setListeners();
        setObservers();

        binding.checkBoxDownpayment.setOnClickListener(v -> {
            CheckBox checkBox = (CheckBox) v;
            if (checkBox.isChecked()) {
                binding.layoutDownpayment.setVisibility(View.VISIBLE);
                viewModel.setDownpayment(true);
                viewModel.setButtonState();
            }
            else{
                binding.layoutDownpayment.setVisibility(View.GONE);
                viewModel.setDownpayment(false);
                viewModel.setButtonState();
            }
        });

        final String[] servicesArray = getResources().getStringArray(R.array.services_array);
        ArrayList<ServiceOption> serviceOptions = new ArrayList<>();

        for (int i = 0; i < servicesArray.length; i++) {
            serviceOptions.add(new ServiceOption(servicesArray[i], i, false));
        }

        ServiceAdapter serviceAdapter = new ServiceAdapter(getContext(), 0, serviceOptions,
                servicesArray, binding.sprTry);
        binding.sprTry.setAdapter(serviceAdapter);

        Log.d(TAG, "onViewCreated: " + binding.sprTry.getItemAtPosition(0).toString());
        ServiceOption serviceOption = (ServiceOption) binding.sprTry.getItemAtPosition(0);
    }

    private void setListeners() {
        //  EditText text listeners
        binding.editTxtDesc.addTextChangedListener(new CustomTextWatcher(viewModel, binding.txtViewDescLabel.getText().toString()));
        binding.editTxtAmount.addTextChangedListener(new CustomTextWatcher(viewModel, binding.txtViewAmountLabel.getText().toString()));
        binding.editTxtAPPayment.addTextChangedListener(new CustomTextWatcher(viewModel, binding.txtViewPaymentLabel.getText().toString()));

        //  Spinner item selected listeners
        binding.snprProcedureChoices.setOnItemSelectedListener(new CustomItemSelectedListener(binding.tvServicesLabel.getText().toString(), viewModel));
    }

    private void setObservers() {
        Resources resources = getResources();
        viewModel.getmAmount().observe(getViewLifecycleOwner(), new CustomObserver(binding.editTxtAmount, resources));
        viewModel.getmOperationState().observe(getViewLifecycleOwner(), new CustomObserver.ObserverButton(binding.btnAddOperation));
        viewModel.getmPayment().observe(getViewLifecycleOwner(), new CustomObserver(binding.editTxtAPPayment, resources));

        viewModel.getmBalance().observe(getViewLifecycleOwner(), formState -> {
            if (formState == null) return;

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
    }

}