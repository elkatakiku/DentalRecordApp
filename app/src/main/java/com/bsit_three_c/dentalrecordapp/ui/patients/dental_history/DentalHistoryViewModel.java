package com.bsit_three_c.dentalrecordapp.ui.patients.dental_history;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.adapter.DentalHistoryAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.listeners.ServicesEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DentalHistoryViewModel extends ViewModel {
    private static final String TAG = DentalHistoryViewModel.class.getSimpleName();
    // TODO: Implement the ViewModel

    private final ServiceRepository serviceRepository;
    private final ProcedureRepository procedureRepository;
    private final PatientRepository patientRepository;

    private final MutableLiveData<String> mPatientUid = new MutableLiveData<>();
    private final MutableLiveData<Patient> mPatient = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<DentalService>> mDentalServices = new MutableLiveData<>();

    private final ArrayList<DentalServiceOption> serviceOptions = new ArrayList<>();

    private final PatientRepository.PatientListener patientsListener = new PatientRepository.PatientListener(mPatient);

    public DentalHistoryViewModel() {
        this.serviceRepository = ServiceRepository.getInstance();
        this.procedureRepository = ProcedureRepository.getInstance();
        this.patientRepository = PatientRepository.getInstance();
    }

    public void loadServices() {
        Log.d(TAG, "loadServices: called");
        serviceRepository.getServicesPath().addListenerForSingleValueEvent(new ServicesEventListener(mDentalServices));
    }

    public LiveData<ArrayList<DentalService>> getmDentalServices() {
        return mDentalServices;
    }

    public void setServicesOptions(ArrayList<DentalService> dentalServices) {
        serviceRepository.setServicesOptions(dentalServices, serviceOptions);
    }

    public ArrayList<DentalServiceOption> getServiceOptions() {
        return serviceOptions;
    }

    public void loadProcedures(String patientUid) {
        patientRepository.getPath(patientUid).addValueEventListener(patientsListener);
    }

    public LiveData<Patient> getmPatient() {
        return mPatient;
    }

    public void loadProcedure(DentalHistoryAdapter adapter, Patient patient) {
        List<String> procedureKeys = patient.getDentalProcedures();

        for (String procedureKey : procedureKeys) {
            procedureRepository
                    .getPath(procedureKey)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Procedure procedure = snapshot.getValue(Procedure.class);

                            if (procedure != null) {
                                ProcedureRepository.initialize(procedure);
                                adapter.addItem(procedure);
                                adapter.notifyItemChanged(adapter.getItemCount());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

    }

    public void setmPatientUid(String patientUid) {
        mPatientUid.setValue(patientUid);
    }

    public LiveData<String> getmPatientUid() {
        return mPatientUid;
    }
}