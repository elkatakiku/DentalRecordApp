package com.bsit_three_c.dentalrecordapp.ui.users.admin.services.services_form;

import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.ArrayList;

public class ServiceFormViewModel extends ViewModel {

    private static final String TAG = ServiceFormViewModel.class.getSimpleName();

    private ServiceRepository serviceRepository;

    private MutableLiveData<Boolean> isDoneUploading = new MutableLiveData<>();

    public ServiceFormViewModel(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public MutableLiveData<Boolean> getIsDoneUploading() {
        return isDoneUploading;
    }

    public void addService(ImageView displayImage, String title, String description, ArrayList<String> categories) {
        serviceRepository.addService(displayImage, isDoneUploading, new DentalService(UIUtil.capitalize(title), description, categories));
    }

    public DentalService editService(
            DentalService service,
            ImageView displayImage,
            String title,
            String description,
            ArrayList<String> categories,
            boolean isImageChanged) {

        service.setTitle(UIUtil.capitalize(title));
        service.setDescription(description);
        service.setCategories(categories);

        serviceRepository.updateService(service, displayImage, isImageChanged);
        return service;
    }


}
