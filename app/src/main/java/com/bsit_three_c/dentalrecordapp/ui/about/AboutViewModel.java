package com.bsit_three_c.dentalrecordapp.ui.about;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Clinic;
import com.bsit_three_c.dentalrecordapp.data.repository.ClinicRepository;

public class AboutViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private final ClinicRepository clinicRepository;

    private final MutableLiveData<Clinic> mClinic;

    private final ClinicRepository.ClinicListener clinicListener;

    public AboutViewModel() {
        this.clinicRepository = (ClinicRepository) ClinicRepository.getInstance();
        this.mClinic = new MutableLiveData<>();
        this.clinicListener = new ClinicRepository.ClinicListener(mClinic);
    }

    public void getClinic() {
        clinicRepository
                .getDatabaseReference()
                .addValueEventListener(clinicListener);
    }

    public LiveData<Clinic> getmClinic() {
        return mClinic;
    }
}