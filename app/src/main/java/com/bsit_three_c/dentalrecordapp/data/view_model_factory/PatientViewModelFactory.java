package com.bsit_three_c.dentalrecordapp.data.view_model_factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.ui.add_patient.AddPatientViewModel;
import com.bsit_three_c.dentalrecordapp.ui.patient_info.OperationViewModel;
import com.bsit_three_c.dentalrecordapp.ui.patient_info.PatientInfoViewModel;
import com.bsit_three_c.dentalrecordapp.ui.patients.PatientsViewModel;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.dashboard.AdminDashboardViewModel;

public class PatientViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override

    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        if (aClass.isAssignableFrom(PatientsViewModel.class)) {
            return (T) new PatientsViewModel(PatientRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(AddPatientViewModel.class)) {
            return (T) new AddPatientViewModel(PatientRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(OperationViewModel.class)) {
            return (T) new OperationViewModel(ProcedureRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(PatientInfoViewModel.class)) {
            return (T) new PatientInfoViewModel(ProcedureRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(AdminDashboardViewModel.class)) {
            return (T) new AdminDashboardViewModel(PatientRepository.getInstance());
        }
        else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}