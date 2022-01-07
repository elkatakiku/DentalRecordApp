package com.bsit_three_c.dentalrecordapp.ui.menu;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceDisplaysAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.listeners.ServicesEventListener;

import java.util.ArrayList;

public class MenuClientViewModel extends ViewModel {
    private static final String TAG = MenuClientViewModel.class.getSimpleName();
    // TODO: Implement the ViewModel

    private final ServiceRepository serviceRepository;

    private final MutableLiveData<ArrayList<DentalService>> mDentalServices = new MutableLiveData<>();

    public MenuClientViewModel() {
        this.serviceRepository = ServiceRepository.getInstance();
    }

    public void initializeRepository(ServiceDisplaysAdapter adapter) {
        serviceRepository.setAdapter(adapter);
    }

    public void loadServices() {
        Log.d(TAG, "loadServices: called");
        serviceRepository.getServices();
        serviceRepository.getServicesPath().addValueEventListener(new ServicesEventListener(mDentalServices));
    }

    public LiveData<ArrayList<DentalService>> getmDentalServices() {
        return mDentalServices;
    }
}