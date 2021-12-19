package com.bsit_three_c.dentalrecordapp.ui.users.admin.services;

import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceDisplaysAdapter;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;

public class AdminServicesViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private ServiceRepository serviceRepository;

    public AdminServicesViewModel(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public void initializeRepository(ServiceDisplaysAdapter adapter) {
        serviceRepository.setAdapter(adapter);
    }

    public void loadServices() {
        serviceRepository.getServices();
    }

    public void removeListeners() {
        serviceRepository.removeListeners();
    }
}