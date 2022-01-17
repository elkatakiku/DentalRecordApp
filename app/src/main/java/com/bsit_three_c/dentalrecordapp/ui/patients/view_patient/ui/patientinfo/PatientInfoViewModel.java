package com.bsit_three_c.dentalrecordapp.ui.patients.view_patient.ui.patientinfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.ProceduresList;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceOptionsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientInfoViewModel extends ViewModel {
    private final PatientRepository patientRepository;
    private final ProcedureRepository procedureRepository;
    private final ServiceRepository serviceRepository;

    private final MutableLiveData<Double> mBalance;
    private final MutableLiveData<ArrayList<DentalService>> mDentalServices;
    private final MutableLiveData<Patient> mPatient;
    private final MutableLiveData<Boolean> mHasProcedures;

    private final ArrayList<DentalServiceOption> serviceOptions;

    private final ValueEventListener servicesEventListener;
    private final ValueEventListener patientListener;

    private Patient patient;


    public PatientInfoViewModel(PatientRepository patientRepository, ProcedureRepository procedureRepository, ServiceRepository serviceRepository) {
        this.patientRepository = patientRepository;
        this.procedureRepository = procedureRepository;
        this.serviceRepository = serviceRepository;

        this.mBalance = new MutableLiveData<>();
        this.mDentalServices = new MutableLiveData<>();
        this.mPatient = new MutableLiveData<>();
         this.mHasProcedures = new MutableLiveData<>();

        this.serviceOptions = new ArrayList<>();

        this.servicesEventListener = new ServiceRepository.ServicesEventListener(mDentalServices);
        this.patientListener = new PatientRepository.PatientListener(mPatient);

        this.serviceOptions.add(new DentalServiceOption(ServiceOptionsAdapter.DEFAULT_OPTION, ServiceOptionsAdapter.DEFAULT_OPTION, false));
    }

    private void loadServices() {
        serviceRepository
                .getServicesPath()
                .addListenerForSingleValueEvent(servicesEventListener);
    }

    public LiveData<ArrayList<DentalService>> getmDentalServices() {
        return mDentalServices;
    }

    public void setServicesOptions(ArrayList<DentalService> dentalServices) {
        serviceRepository.setServicesOptions(dentalServices, serviceOptions);
    }

    public List<DentalServiceOption> getServiceOptions() {
        return serviceOptions;
    }

    public LiveData<Boolean> hasProcedures() {
        return mHasProcedures;
    }

    public void loadProcedures(Patient patient, ProceduresList list) {
        mHasProcedures.setValue(patient.getDentalProcedures().size() != 0);
        for (String key : patient.getDentalProcedures()) {
            procedureRepository
                    .getPath(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Procedure procedure = snapshot.getValue(Procedure.class);

                            if (procedure != null) {
                                ProcedureRepository.initialize(procedure);
                                list.addItem(procedure);

                                //  Computes balance
                                if (mBalance.getValue() != null)
                                    mBalance.setValue(mBalance.getValue() + procedure.getDentalBalance());
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    public void loadPatient(String patientUID) {
        loadServices();
        patientRepository
                .getPath(patientUID)
                .addValueEventListener(patientListener);
    }

    public MutableLiveData<Patient> getmPatient() {
        return mPatient;
    }

    public LiveData<Double> getmBalance() {
        return mBalance;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void removeListeners() {
        Patient patient = mPatient.getValue();

        if (patient != null) {
            patientRepository
                    .getPath(patient.getUid())
                    .removeEventListener(patientListener);
        }
    }
}
