package com.bsit_three_c.dentalrecordapp.data.view_model_factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.dashboard.AdminDashboardViewModel;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.AdminEmployeesViewModel;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.employee_form.BasicInfoFormViewModel;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.employee_form.EmergenyContactFormViewModel;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.view_employee.EmployeeInfoViewModel;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.PatientsViewModel;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_form.AddPatientViewModel;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.view_patient.PatientInfoViewModel;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.view_patient.ProcedureFormViewModel;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.services.AdminServicesViewModel;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.services.services_form.ServiceFormViewModel;

public class CustomViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        if (aClass.isAssignableFrom(PatientsViewModel.class)) {
            return (T) new PatientsViewModel(PatientRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(AddPatientViewModel.class)) {
            return (T) new AddPatientViewModel(PatientRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(ProcedureFormViewModel.class)) {
            return (T) new ProcedureFormViewModel(ProcedureRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(PatientInfoViewModel.class)) {
            return (T) new PatientInfoViewModel(PatientRepository.getInstance(), ProcedureRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(AdminDashboardViewModel.class)) {
            return (T) new AdminDashboardViewModel(
                    PatientRepository.getInstance(),
                    ServiceRepository.getInstance(),
                    EmployeeRepository.getInstance()
            );
        }
        else if (aClass.isAssignableFrom(ServiceFormViewModel.class)) {
            return (T) new ServiceFormViewModel(ServiceRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(AdminServicesViewModel.class)) {
            return (T) new AdminServicesViewModel(ServiceRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(BasicInfoFormViewModel.class)) {
            return (T) new BasicInfoFormViewModel(EmployeeRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(EmergenyContactFormViewModel.class)) {
            return (T) new EmergenyContactFormViewModel(
                    EmployeeRepository.getInstance(),
                    AccountRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(AdminEmployeesViewModel.class)) {
            return (T) new AdminEmployeesViewModel(EmployeeRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(EmployeeInfoViewModel.class)) {
            return (T) new EmployeeInfoViewModel(
                    EmployeeRepository.getInstance(),
                    AccountRepository.getInstance()
            );
        }
        else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}