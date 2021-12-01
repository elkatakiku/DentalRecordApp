package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.form_state.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.patient.OperationRepository;

import java.util.ArrayList;

public class OperationViewModel extends ViewModel {
    private static final String TAG = OperationViewModel.class.getSimpleName();

    private final MutableLiveData<ArrayList<Person>> mPatientList = new MutableLiveData<>();
    private final OperationRepository repository;

    private final MutableLiveData<FormState> mDate = new MutableLiveData<>();
    private final MutableLiveData<FormState> mDescription = new MutableLiveData<>();
    private final MutableLiveData<FormState> mMOP = new MutableLiveData<>();
    private final MutableLiveData<FormState> mAmount = new MutableLiveData<>();

    public OperationViewModel(OperationRepository repository) {
        this.repository = repository;
    }

    public OperationRepository getRepository() {
        return repository;
    }


}
