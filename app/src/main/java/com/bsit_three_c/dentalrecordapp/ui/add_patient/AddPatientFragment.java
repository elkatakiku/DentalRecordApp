package com.bsit_three_c.dentalrecordapp.ui.add_patient;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.view_model_factory.PatientViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAddPatientBinding;

public class AddPatientFragment extends Fragment {
    private static final String TAG = AddPatientFragment.class.getSimpleName();

    private FragmentAddPatientBinding binding;
    private AddPatientViewModel basicViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAddPatientBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        basicViewModel = new ViewModelProvider(this, new PatientViewModelFactory()).get(AddPatientViewModel.class);

        binding.btnAddPatient.setOnClickListener(view1 -> {
            String firstname = binding.eTxtFirstname.getText().toString();
            String lastname = binding.eTxtLastname.getText().toString();
            String middieInitial = binding.eTxtMiddleInitial.getText().toString();
            String address = binding.eTxtAddress.getText().toString();
            String phoneNumber = binding.eTxtPhoneNumber.getText().toString();
            int age = Integer.parseInt(binding.eTxtAge.getText().toString());
            String civilStatus = binding.spnrCivilStatus.getSelectedItem().toString();
            String occupation = binding.eTxtOccupation.getText().toString();


            basicViewModel.addPatient(firstname, lastname, middieInitial, address,
                    phoneNumber, civilStatus, age, occupation);
        });

//                NavHostFragment.findNavController(AddPatientFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment));

//        String[] status = {"Married", "Single", "Divorced"};
//        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_civil_status, status);

//        Spinner civilStatus = binding.spnrCivilStatus;
//        civilStatus.setAdapter(adapter);

        binding.eTxtFirstname.addTextChangedListener(new CustomTextWatcher(binding.labelFirstname));
        binding.eTxtLastname.addTextChangedListener(new CustomTextWatcher(binding.labelLastname));
        binding.eTxtMiddleInitial.addTextChangedListener(new CustomTextWatcher(binding.labelMiddle));
        binding.eTxtAge.addTextChangedListener(new CustomTextWatcher(binding.labelAge));
        binding.eTxtAddress.addTextChangedListener(new CustomTextWatcher(binding.labelAddress));
        binding.eTxtPhoneNumber.addTextChangedListener(new CustomTextWatcher(binding.eTxtPhoneNumber));
        binding.eTxtOccupation.addTextChangedListener(new CustomTextWatcher(binding.labelOccupation));

        Resources resources = getResources();
        basicViewModel.getmFirstname().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtFirstname, resources));
        basicViewModel.getMlastname().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtLastname, resources));
        basicViewModel.getmMiddleInitial().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtMiddleInitial, resources));
        basicViewModel.getmAge().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtAge, resources));
        basicViewModel.getmAddress().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtAddress, resources));
        basicViewModel.getmPhoneNumber().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtPhoneNumber, resources));
        basicViewModel.getmOccupation().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtOccupation, resources));


//        basicViewModel.getAddPatientFormState().observe(getViewLifecycleOwner(), addPatientFormState -> {
//            if (addPatientFormState == null) return;
//
//            binding.btnAddPatient.setEnabled(addPatientFormState.isDataValid());
//            if (addPatientFormState.getMsgError() != null) {
//                Snackbar.make(binding.getRoot(), getResources().getString(addPatientFormState.getMsgError()), Snackbar.LENGTH_SHORT).show();
//            }
//        });




    }

    private void setTextObservers() {

//        basicViewModel.getmFirstname().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtFirstname, getResources()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class CustomTextWatcher implements TextWatcher {

        private final TextView label;

        public CustomTextWatcher(TextView label) {
            this.label = label;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, "afterTextChanged: s value:" + s.toString());
            Log.d(TAG, "afterTextChanged: label: " + label.getText().toString());
            if (s != null) basicViewModel.dataChanged(label.getText().toString(), s.toString());
        }
    }

    private static class CustomObserver implements Observer<AddPatientFormState> {

        final Resources res;
        final EditText editText;

        public CustomObserver(EditText editText, Resources res) {
            this.editText = editText;
            this.res = res;
        }

        @Override
        public void onChanged(AddPatientFormState addPatientFormState) {
            if (addPatientFormState == null) return;

            if (addPatientFormState.getMsgError() != null)
                editText.setError(res.getString(addPatientFormState.getMsgError()));
        }
    }
}