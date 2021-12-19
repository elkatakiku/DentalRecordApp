package com.bsit_three_c.dentalrecordapp.ui.users.admin.services.view_service;

import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;

public class ViewServicesViewModel extends ViewModel {

    private ServiceRepository serviceRepository;


    public ViewServicesViewModel(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }


}
