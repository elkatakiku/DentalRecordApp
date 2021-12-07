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
import com.bsit_three_c.dentalrecordapp.util.CustomItemSelectedListener;
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

        setListeners();
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

    private void setListeners() {
        binding.eTxtFirstname.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelFirstname));
        binding.eTxtLastname.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelLastname));
        binding.eTxtMiddleInitial.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelMiddle));
        binding.eTxtAge.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelAge));
        binding.eTxtAddress.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelAddress));
        binding.eTxtPhoneNumber.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelTelephone));
        binding.eTxtOccupation.addTextChangedListener(new CustomTextWatcher(basicViewModel, binding.labelOccupation));
        binding.spnrCivilStatus.setOnItemSelectedListener(new CustomItemSelectedListener(binding.labelCivilStatus.getText().toString(), basicViewModel));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}