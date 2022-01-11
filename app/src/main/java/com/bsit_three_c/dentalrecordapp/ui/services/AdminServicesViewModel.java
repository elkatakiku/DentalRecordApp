package com.bsit_three_c.dentalrecordapp.ui.services;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;

import java.util.ArrayList;

public class AdminServicesViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static final String TAG = AdminServicesViewModel.class.getSimpleName();

    private final ServiceRepository serviceRepository;

    private final MutableLiveData<ArrayList<DentalService>> mServices = new MutableLiveData<>();
    private final ServiceRepository.ServicesListener servicesListener = new ServiceRepository.ServicesListener(mServices);

    public AdminServicesViewModel(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

//    public void initializeRepository(ServiceDisplaysAdapter adapter) {
//        serviceRepository.setAdapter(adapter);
//    }

    public void loadServices() {
        Log.d(TAG, "loadServices: called");
//        serviceRepository.getServices();
        serviceRepository.getServicesPath().addValueEventListener(servicesListener);
    }

    public LiveData<ArrayList<DentalService>> getmServices() {
        return mServices;
    }

    public void removeListeners() {
        serviceRepository.getDatabaseReference().removeEventListener(servicesListener);
    }
}