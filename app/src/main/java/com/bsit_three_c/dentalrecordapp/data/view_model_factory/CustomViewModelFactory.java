package com.bsit_three_c.dentalrecordapp.data.view_model_factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.DentalChartRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.ui.dashboard.AdminDashboardViewModel;
import com.bsit_three_c.dentalrecordapp.ui.dental_chart.DentalChartViewModel;
import com.bsit_three_c.dentalrecordapp.ui.employees.EmployeesViewModel;
import com.bsit_three_c.dentalrecordapp.ui.employees.employee_form.EmergencyContactFormViewModel;
import com.bsit_three_c.dentalrecordapp.ui.employees.view_employee.EmployeeInfoViewModel;
import com.bsit_three_c.dentalrecordapp.ui.patients.PatientsViewModel;
import com.bsit_three_c.dentalrecordapp.ui.patients.view_patient.ui.patientinfo.PatientInfoViewModel;
import com.bsit_three_c.dentalrecordapp.ui.patients.procedure_form.ui.procedureform.ProcedureFormViewModel;
import com.bsit_three_c.dentalrecordapp.ui.services.AdminServicesViewModel;
import com.bsit_three_c.dentalrecordapp.ui.services.services_form.ServiceFormViewModel;

public class CustomViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        if (aClass.isAssignableFrom(PatientsViewModel.class)) {
            return (T) new PatientsViewModel(PatientRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(ProcedureFormViewModel.class)) {
            return (T) new ProcedureFormViewModel(
                    ProcedureRepository.getInstance(),
                    ServiceRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(PatientInfoViewModel.class)) {
            return (T) new PatientInfoViewModel(
                    PatientRepository.getInstance(),
                    ProcedureRepository.getInstance(),
                    ServiceRepository.getInstance());
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
        else if (aClass.isAssignableFrom(EmergencyContactFormViewModel.class)) {
            return (T) new EmergencyContactFormViewModel(
                    EmployeeRepository.getInstance(),
                    AccountRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(EmployeesViewModel.class)) {
            return (T) new EmployeesViewModel(EmployeeRepository.getInstance());
        }
        else if (aClass.isAssignableFrom(EmployeeInfoViewModel.class)) {
            return (T) new EmployeeInfoViewModel(
                    EmployeeRepository.getInstance(),
                    AccountRepository.getInstance()
            );
        }
        else if (aClass.isAssignableFrom(DentalChartViewModel.class)) {
            return (T) new DentalChartViewModel(DentalChartRepository.getInstance());
        }
        else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}