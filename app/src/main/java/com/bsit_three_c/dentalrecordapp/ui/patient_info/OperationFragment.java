package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.PatientViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAddOperationBinding;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

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
        Log.d(TAG, "onViewCreated: got patient: " + patient);

        binding.buttonSecond.setOnClickListener(view1 -> {

            Date date = UIUtil.getDate(binding.datePicker);
            String dentalDesc = binding.editTxtDesc.getText().toString();
            String modeOfPayment = binding.spnrModeOfPayment.getSelectedItem().toString();
            String dentalAmount = binding.editTxtAmount.getText().toString();
            boolean isDownpayment = binding.checkBoxDownpayment.isChecked();
            Log.d(TAG, "onViewCreated: calling viewmodel");

            Log.d(TAG, "onViewCreated: date: " + date.toString());
            Log.d(TAG, "onViewCreated: dentaldate: " + UIUtil.getDate(date));

            viewModel.addOperation(patient, dentalDesc, date, modeOfPayment, dentalAmount, isDownpayment);

            requireActivity().onBackPressed();

//            viewModel.getRepository().addOperation(pat);
//            Log.d(TAG, "onClick: date Day: " + binding.datePicker.getDayOfMonth());
//            Log.d(TAG, "onClick: date Month: " + (binding.datePicker.getMonth()+1));
//            Log.d(TAG, "onClick: date Year: " + binding.datePicker.getYear());
//            Log.d(TAG, "onClick: date Month Letters: " + DateFormatSymbols.getInstance().getMonths()[binding.datePicker.getMonth()]);
//
////                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//            Log.d(TAG, "onClick: dateformat: " + SimpleDateFormat.getDateInstance());
//
////            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//            try {
//                Date date = dateFormat.parse("1/12/2021");
//                Log.d(TAG, "onViewCreated: date: " + date);
//
//                Log.d(TAG, "onViewCreated: dateString: " + dateFormat.format(date));
//                String[] dateUnit = dateFormat.format(date).split("/");
//                Log.d(TAG, "onViewCreated: split day: " + dateUnit[0]);
//                Log.d(TAG, "onViewCreated: split month: " + dateUnit[1]);
//                Log.d(TAG, "onViewCreated: split year: " + dateUnit[2]);
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//                NavHostFragment.findNavController(OperationFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_First2Fragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}