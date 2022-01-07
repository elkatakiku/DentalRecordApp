package com.bsit_three_c.dentalrecordapp.ui.services.services_form;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class ServiceFormViewModel extends ViewModel {

    private static final String TAG = ServiceFormViewModel.class.getSimpleName();

    private ServiceRepository serviceRepository;

    private final MutableLiveData<Boolean> mUploadAttempt = new MutableLiveData<>();
    private final MutableLiveData<Integer> mError = new MutableLiveData<>();
    private final MutableLiveData<byte[]> mImageByte = new MutableLiveData<>();

    public static final int VALID = -1;

    public ServiceFormViewModel(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public void addService(DentalService dentalService) {
        mUploadAttempt.setValue(false);

        dentalService.setServiceUID(serviceRepository.getNewUid());

        if (mImageByte.getValue() != null) {

            serviceRepository.uploadDisplayImage(dentalService, mImageByte.getValue()).continueWith(new Continuation<Uri, Object>() {
                @NonNull
                @Override
                public Object then(@NonNull Task<Uri> task) throws Exception {
                    if (task.isSuccessful()) {

                        Log.d(TAG, "onComplete: success getting URI");
                        dentalService.setDisplayImage(task.getResult().toString());
                        serviceRepository.addService(dentalService);

                        mError.setValue(VALID);
                    }
                    else {
                        mError.setValue(R.string.an_error_occurred);
                    }

                    return null;
                }
            });
        } else {
            serviceRepository.addService(dentalService);
        }

        mUploadAttempt.setValue(true);
    }

    public DentalService editService(
            DentalService service,
            String title,
            String description,
            ArrayList<String> categories) {

        service.setTitle(UIUtil.capitalize(title));
        service.setDescription(description);
        service.setCategories(categories);

        return service;

//        serviceRepository.updateService(service, displayImage, isImageChanged);
    }

    public LiveData<byte[]> getmImageByte() {
        return mImageByte;
    }

    public void setmImageByte(byte[] bytes) {
        mImageByte.setValue(bytes);
    }

    public LiveData<Boolean> getmUploadAttempt() {
        return mUploadAttempt;
    }

    public LiveData<Integer> getmError() {
        return mError;
    }
}
