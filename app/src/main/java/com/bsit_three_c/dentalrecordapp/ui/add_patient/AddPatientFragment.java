package com.bsit_three_c.dentalrecordapp.ui.add_patient;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.view_model_factory.PatientViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAddPatientBinding;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;

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

        binding.btnAddPatient.setEnabled(false);

        binding.btnAddPatient.setOnClickListener(view1 -> {
            String firstname = binding.eTxtFirstname.getText().toString();
            String lastname = binding.eTxtLastname.getText().toString();
            String middieInitial = binding.eTxtMiddleInitial.getText().toString();
            String address = binding.eTxtAddress.getText().toString();
            String phoneNumber = binding.eTxtPhoneNumber.getText().toString();
            int age = Integer.parseInt(binding.eTxtAge.getText().toString());
            int civilStatus = binding.spnrCivilStatus.getSelectedItemPosition();
            String occupation = binding.eTxtOccupation.getText().toString();


            basicViewModel.addPatient(firstname, lastname, middieInitial, address,
                    phoneNumber, civilStatus, age, occupation);

            FragmentActivity activity = getActivity();
            if (activity != null) {
                String activityName = activity.getClass().getSimpleName();
                if (activityName.equals("AddPatientActivity")) activity.finish();
                else if (activityName.equals("MainActivity")) getActivity().onBackPressed();
            }


        });

//                NavHostFragment.findNavController(AddPatientFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment));

//        String[] status = {"Married", "Single", "Divorced"};
//        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_civil_status, status);

//        Spinner civilStatus = binding.spnrCivilStatus;
//        civilStatus.setAdapter(adapter);


//        basicViewModel.getAddPatientFormState().observe(getViewLifecycleOwner(), addPatientFormState -> {
//            if (addPatientFormState == null) return;
//
//            binding.btnAddPatient.setEnabled(addPatientFormState.isDataValid());
//            if (addPatientFormState.getMsgError() != null) {
//                Snackbar.make(binding.getRoot(), getResources().getString(addPatientFormState.getMsgError()), Snackbar.LENGTH_SHORT).show();
//            }
//        });

        setTextChangedListener();
        setObservers();
    }

    private void setObservers() {
        Resources resources = getResources();
        basicViewModel.getmFirstname().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtFirstname, resources));
        basicViewModel.getMlastname().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtLastname, resources));
        basicViewModel.getmMiddleInitial().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtMiddleInitial, resources));
        basicViewModel.getmAge().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtAge, resources));
        basicViewModel.getmAddress().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtAddress, resources));
        basicViewModel.getmPhoneNumber().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtPhoneNumber, resources));
        basicViewModel.getmOccupation().observe(getViewLifecycleOwner(), new CustomObserver(binding.eTxtOccupation, resources));
        basicViewModel.getAddPatientFormState().observe(getViewLifecycleOwner(), new CustomObserver.ObserverButton(binding.btnAddPatient));
    }

    private void setTextChangedListener() {
        binding.eTxtFirstname.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelFirstname));
        binding.eTxtLastname.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelLastname));
        binding.eTxtMiddleInitial.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelMiddle));
        binding.eTxtAge.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelAge));
        binding.eTxtAddress.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelAddress));
        binding.eTxtPhoneNumber.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelTelephone));
        binding.eTxtOccupation.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelOccupation));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    private class CustomTextWatcher implements TextWatcher {
//
//        private final TextView label;
//
//        public CustomTextWatcher(TextView label) {
//            this.label = label;
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            basicViewModel.beforeDataChange(label.getText().toString(), after);
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            Log.d(TAG, "afterTextChanged: s value:" + s.toString());
//            Log.d(TAG, "afterTextChanged: label: " + label.getText().toString());
//            basicViewModel.dataChanged(label.getText().toString(), s.toString());
//        }
//    }

//    private static class CustomObserver implements Observer<FormState> {
//
//        final Resources res;
//        final EditText editText;
//
//        public CustomObserver(EditText editText, Resources res) {
//            this.editText = editText;
//            this.res = res;
//        }
//
//        @Override
//        public void onChanged(FormState formState) {
//            if (formState == null) return;
//
//            if (formState.getMsgError() != null)
//                editText.setError(res.getString(formState.getMsgError()));
//        }
//    }
}