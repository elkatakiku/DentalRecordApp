package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.HistoryItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.adapter.OperationsList;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.PatientViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentPatientInfoBinding;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PatientInfoFragment extends Fragment {
    private static final String TAG = PatientInfoFragment.class.getSimpleName();

    private FragmentPatientInfoBinding binding;
    private PatientInfoViewModel viewModel;
    private Patient patient;
    private HistoryItemAdapter itemAdapter;

    private BottomSheetDialog bottomSheetDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPatientInfoBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this, new PatientViewModelFactory()).get(PatientInfoViewModel.class);;
        itemAdapter = new HistoryItemAdapter(requireActivity(), R.layout.item_dental_history);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = getActivity();
        if (activity != null) {
//            Log.d(TAG, "onViewCreated: patient key: " + getString(R.string.PATIENT));
//            Log.d(TAG, "onViewCreated: has person in intent: " + activity.getIntent().hasExtra(getString(R.string.PATIENT)));
//            Log.d(TAG, "onViewCreated: intent value: " + activity.getIntent().getParcelableExtra(getString(R.string.PATIENT)));
            patient = activity.getIntent().getParcelableExtra(getString(R.string.PATIENT));
        }
//        else Snackbar.make(binding.getRoot(), "activity is null", Snackbar.LENGTH_SHORT).show();

        binding.buttonFirst.setOnClickListener(view1 -> {
//            Log.d(TAG, "onClick: patient to be passed: " + patient);
            PatientInfoFragmentDirections.ActionFirst2FragmentToSecondFragment action =
                    PatientInfoFragmentDirections.actionFirst2FragmentToSecondFragment(patient);
            Navigation.findNavController(view1).navigate(action);
//                NavHostFragment.findNavController(PatientInfoFragment.this)
//                        .navigate(R.id.action_First2Fragment_to_SecondFragment);
        });



//        ArrayList<DentalOperation> list = new ArrayList<>();
//        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));
//        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, false));
//        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));
//        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, false));
//        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, false));
//        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, false));
//        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));
//        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));
//        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));
//        list.add(new DentalOperation("Bunot ngalangala", "Aug 2, 2021", 6453, true));
//
//
////        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_civil_status, list);
//        HistoryItemAdapter adapter = new HistoryItemAdapter(requireActivity(), R.layout.item_dental_history, list);
//
//        binding.listOperations.setAdapter(adapter);
//
//        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
//        CardView operationItem = (CardView) layoutInflater.inflate(R.layout.item_dental_history, null);
//        TextView txtDentalDesc = operationItem.findViewById(R.id.txtDentalDesc);
//        TextView txtDentalDate = operationItem.findViewById(R.id.txtDentalDate);
//        TextView txtDentalAmount = operationItem.findViewById(R.id.txtDentalAmount);
//        TextView txtDentalFullyPaid = operationItem.findViewById(R.id.txtDentalFullyPaid);
//        CheckBox cbIsFullyPaid = operationItem.findViewById(R.id.cbFullyPaid);
//
//        txtDentalDesc.setText("Bunot as usual");
//        txtDentalDate.setText("Kamakailan langx");
//        txtDentalAmount.setText("500");
//        cbIsFullyPaid.setChecked(true);

//        binding.tryList.addView(operationItem);

        OperationsList operationsList = new OperationsList(binding.tryList, getLayoutInflater());
        viewModel.loadOperations(patient, operationsList);

        displayInfo();

        String balance = String.valueOf(viewModel.getBalance());
        binding.txtOperationsBalance.setText(balance);
//        binding.listOperations.setAdapter(itemAdapter);
//        viewModel.loadOperations(patient, itemAdapter, binding.listOperations);

//        bottomSheetDialog = new BottomSheetDialog(requireActivity());
//        createDialog();

//        binding.listOperations.setOnItemClickListener((parent, view12, position, id) -> {
//            Log.d(TAG, "onItemClick: showing dialog");
//            Snackbar.make(view12, "ANU NA", Snackbar.LENGTH_SHORT).show();
//            bottomSheetDialog.show();
//        });


//        bottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

//    private void createDialog() {
//        Log.d(TAG, "createDialog: creating dialog");
//        View view = getLayoutInflater().inflate(R.layout.bottom_operation_details, null, false);
//        LinearLayout paymentLayout = (LinearLayout) view.findViewById(R.id.paymentsList);
//
//        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.item_payment_card, null);
//        TextView modeOfPayment = cardView.findViewById(R.id.txtViewPaymentMOD);
//        TextView paymentAmount = cardView.findViewById(R.id.txtViewPaymentAmount);
//        TextView paymentDate = cardView.findViewById(R.id.textViewPaymentDate);
//        modeOfPayment.setText("Debit");
//        paymentAmount.setText("500.50");
//        paymentDate.setText(UIUtil.getCurrentDate());
//
//        CardView cardView1 = (CardView) getLayoutInflater().inflate(R.layout.item_payment_card, null);
//        TextView modeOfPayment1 = cardView1.findViewById(R.id.txtViewPaymentMOD);
//        TextView paymentAmount1 = cardView1.findViewById(R.id.txtViewPaymentAmount);
//        TextView paymentDate1 = cardView1.findViewById(R.id.textViewPaymentDate);
//        modeOfPayment1.setText("Debit");
//        paymentAmount1.setText("640.50");
//        paymentDate1.setText(UIUtil.getCurrentDate());
//
//        CardView cardView2 = (CardView) getLayoutInflater().inflate(R.layout.item_payment_card, null);
//        TextView modeOfPayment2 = cardView2.findViewById(R.id.txtViewPaymentMOD);
//        TextView paymentAmount2 = cardView2.findViewById(R.id.txtViewPaymentAmount);
//        TextView paymentDate2 = cardView2.findViewById(R.id.textViewPaymentDate);
//        modeOfPayment2.setText("Debit");
//        paymentAmount2.setText("840.50");
//        paymentDate2.setText(UIUtil.getCurrentDate());
//
//        paymentLayout.addView(cardView);
//        paymentLayout.addView(cardView1);
//        paymentLayout.addView(cardView2);
//
//        bottomSheetDialog.setContentView(view);
//    }

    @Override
    public void onResume() {
        super.onResume();
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void displayInfo() {
        String notAvailable = "N/A";
        if (Checker.isDataAvailable(patient.getFirstname())) binding.txtViewPIFirstname.setText(patient.getFirstname());
        else binding.txtViewPIFirstname.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getLastname())) binding.txtViewPILastname.setText(patient.getLastname());
        else binding.txtViewPILastname.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getMiddleInitial())) binding.txtViewPIMiddleInitial.setText(patient.getMiddleInitial());
        else binding.txtViewPIMiddleInitial.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getAddress())) binding.txtViewPIAddress.setText(patient.getAddress());
        else binding.txtViewPIAddress.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getPhoneNumber())) binding.txtViewPITelephoneNumber.setText(patient.getPhoneNumber());
        else binding.txtViewPITelephoneNumber.setText(notAvailable);

        if (patient.getAge() > 0) binding.txtViewPIAge.setText(String.valueOf(patient.getAge()));
        else binding.txtViewPIAge.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getCivilStatus())) binding.txtViewPICivilStatus.setText(patient.getCivilStatus());
        else binding.txtViewPICivilStatus.setText(notAvailable);

        if (Checker.isDataAvailable(patient.getOccupation())) binding.txtViewPIOccupation.setText(patient.getOccupation());
        else binding.txtViewPIOccupation.setText(notAvailable);
    }

}