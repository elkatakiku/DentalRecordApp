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

import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.PatientViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAddOperationBinding;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.Date;

public class OperationFragment extends Fragment {
    private static final String TAG = OperationFragment.class.getSimpleName();

    private FragmentAddOperationBinding binding;
    private OperationViewModel viewModel;
    private Patient patient;

//    private boolean isDownpayment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddOperationBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new PatientViewModelFactory()).get(OperationViewModel.class);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        patient = OperationFragmentArgs.fromBundle(getArguments()).getItemPatient();
        Log.d(TAG, "onViewCreated: got patient: " + patient);

        binding.btnAddOperation.setOnClickListener(view1 -> {

            Date date = UIUtil.getDate(binding.datePicker);
            String dentalDesc = binding.editTxtDesc.getText().toString();
            String modeOfPayment = binding.spnrModeOfPayment.getSelectedItem().toString();
            String dentalAmount = binding.editTxtAmount.getText().toString();
            boolean isDownpayment = binding.checkBoxDownpayment.isChecked();
            Log.d(TAG, "onViewCreated: calling viewmodel");

            Log.d(TAG, "onViewCreated: date: " + date.toString());
            Log.d(TAG, "onViewCreated: dentaldate: " + UIUtil.getDate(date));

            if (isDownpayment) {
                String dentalTotalAmount = binding.editTxtTotalAmount.getText().toString();
                String dentalBalance = binding.txtViewBalance.getText().toString();
                viewModel.addProcedure(patient, dentalDesc, date, modeOfPayment, dentalAmount, isDownpayment, dentalTotalAmount, dentalBalance);
            }
            else {
                viewModel.addProcedure(patient, dentalDesc, date, modeOfPayment, dentalAmount, isDownpayment);
            }

            requireActivity().onBackPressed();
        });

        setTextChangedListener();
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

    }

    private void setTextChangedListener() {
        binding.editTxtDesc.addTextChangedListener(new CustomTextWatcher(viewModel, binding.txtViewDescLabel));
        binding.editTxtAmount.addTextChangedListener(new CustomTextWatcher(viewModel, binding.txtViewAmountLabel));
        binding.editTxtTotalAmount.addTextChangedListener(new CustomTextWatcher(viewModel, binding.txtViewTotalAmountLabel));
    }

    private void setObservers() {
        Resources resources = getResources();
        viewModel.getmDescription().observe(getViewLifecycleOwner(), new CustomObserver(binding.editTxtDesc, resources));
        viewModel.getmAmount().observe(getViewLifecycleOwner(), new CustomObserver(binding.editTxtAmount, resources));
        viewModel.getmOperationState().observe(getViewLifecycleOwner(), new CustomObserver.ObserverButton(binding.btnAddOperation));
        viewModel.getmTotalAmount().observe(getViewLifecycleOwner(), new CustomObserver(binding.editTxtTotalAmount, resources));
        viewModel.getmBalance().observe(getViewLifecycleOwner(), formState -> {
            if (formState == null) return;

            if (formState.getMsgError() != null) binding.txtViewBalance.setText(getString(formState.getMsgError()));
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