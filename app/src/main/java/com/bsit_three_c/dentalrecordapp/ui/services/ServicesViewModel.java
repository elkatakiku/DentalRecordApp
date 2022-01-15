package com.bsit_three_c.dentalrecordapp.ui.services;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;

import java.util.List;

public class ServicesViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static final String TAG = ServicesViewModel.class.getSimpleName();

    private final ServiceRepository serviceRepository;

    private final MutableLiveData<List<DentalService>> mServices = new MutableLiveData<>();
    private final ServiceRepository.ServicesListener servicesListener = new ServiceRepository.ServicesListener(mServices);

    public ServicesViewModel(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public void loadServices() {
        Log.d(TAG, "loadServices: called");
//        serviceRepository.getServices();
        serviceRepository.getServicesPath().addValueEventListener(servicesListener);
    }

    public LiveData<List<DentalService>> getmServices() {
        return mServices;
    }

    public void removeListeners() {
        serviceRepository.getDatabaseReference().removeEventListener(servicesListener);
    }
}