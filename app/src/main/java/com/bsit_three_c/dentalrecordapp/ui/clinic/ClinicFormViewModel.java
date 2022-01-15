package com.bsit_three_c.dentalrecordapp.ui.clinic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Clinic;
import com.bsit_three_c.dentalrecordapp.data.repository.ClinicRepository;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;

import java.util.List;

public class ClinicFormViewModel extends ViewModel {
    private static final String TAG = ClinicFormViewModel.class.getSimpleName();
    // TODO: Implement the ViewModel

    private final ClinicRepository clinicRepository;

    private final MutableLiveData<Clinic> mClinic;
    private final MutableLiveData<Integer> mError;

    private final ClinicRepository.ClinicListener clinicListener;

    public ClinicFormViewModel() {
        this.clinicRepository = (ClinicRepository) ClinicRepository.getInstance();

        this.mClinic = new MutableLiveData<>();
        this.mError = new MutableLiveData<>();

        this.clinicListener = new ClinicRepository.ClinicListener(mClinic);
    }

    public void getClinic() {
        clinicRepository.getDatabaseReference().addValueEventListener(clinicListener);
    }

    public LiveData<Clinic> getmClinic() {
        return mClinic;
    }

    public void uploadClinic(
            String name,
            String location,
            List<String> contact,
            int from,
            int to,
            String startTIme,
            String endTime) {

        Clinic clinic = new Clinic(
                clinicRepository.getNewUid(),
                name,
                location,
                contact,
                from,
                to,
                DateUtil.getTimeStamp(DateUtil.convertToTime(startTIme)),
                DateUtil.getTimeStamp(DateUtil.convertToTime(endTime))
        );

        clinicRepository
                .upload(clinic)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        mError.setValue(R.string.unsuccessfull_update_data);
                        return;
                    }

                    mError.setValue(Checker.VALID);
                });

    }

    public LiveData<Integer> getmError() {
        return mError;
    }

    public void removeListeners() {
        clinicRepository.getDatabaseReference().removeEventListener(clinicListener);
    }
}