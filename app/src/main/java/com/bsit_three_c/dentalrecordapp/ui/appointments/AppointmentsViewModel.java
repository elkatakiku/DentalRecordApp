package com.bsit_three_c.dentalrecordapp.ui.appointments;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.model.ProgressNote;
import com.bsit_three_c.dentalrecordapp.data.repository.AppointmentRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProgressNoteRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AppointmentsViewModel extends ViewModel {
    private static final String TAG = AppointmentsViewModel.class.getSimpleName();
    // TODO: Implement the ViewModel

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;

    private final MutableLiveData<List<Appointment>> mAppointments;
    private final MutableLiveData<List<DentalService>> mServices;
    private final MutableLiveData<Integer> mError;
    private final MutableLiveData<Intent> mPatient;

    private final AppointmentRepository.AppointmentListener appointmentListener;
    private final ServiceRepository.ServicesListener servicesListener;

    public AppointmentsViewModel() {
        this.appointmentRepository = AppointmentRepository.getInstance();
        this.serviceRepository = ServiceRepository.getInstance();

        this.mAppointments = new MutableLiveData<>();
        this.mServices = new MutableLiveData<>();
        this.mError = new MutableLiveData<>();
        this.mPatient = new MutableLiveData<>();

        this.appointmentListener =  new AppointmentRepository.AppointmentListener(mAppointments);
        this.servicesListener = new ServiceRepository.ServicesListener(mServices);
    }

    public void loadAppointments() {
        appointmentRepository.getAppointmentsByLastname().addValueEventListener(appointmentListener);
    }

    public void loadServices() {
        serviceRepository.getServicesPath().addValueEventListener(servicesListener);
    }

    public LiveData<List<Appointment>> getmAppointments() {
        return mAppointments;
    }

    public LiveData<List<DentalService>> getmServices() {
        return mServices;
    }

    public void removeListeners() {
        appointmentRepository.getDatabaseReference().removeEventListener(appointmentListener);
        serviceRepository.getDatabaseReference().removeEventListener(servicesListener);
    }

    public void addPatient(Intent intent) {
        Patient patient = intent.getParcelableExtra(LocalStorage.PATIENT_KEY);
        if (patient != null) {
            PatientRepository
                    .getInstance()
                    .upload(patient)
                    .continueWith(task -> {
                        if (!task.isSuccessful()) {
                            mError.setValue(R.string.error_creating_patient);
                            return null;
                        }

                        Procedure procedure = intent.getParcelableExtra(LocalStorage.PROCEDURE_KEY);
                        if (procedure != null) {
                            ProcedureRepository
                                    .getInstance()
                                    .upload(procedure)
                                    .continueWith(task1 -> {
                                        if (!task.isSuccessful()) {
                                            mError.setValue(R.string.error_creating_procedure);
                                            return null;
                                        }

                                        ProgressNote progressNote = intent.getParcelableExtra(LocalStorage.PROGRESS_NOTE_KEY);
                                        if (progressNote != null) {
                                            ProgressNoteRepository
                                                    .getInstance()
                                                    .upload(progressNote)
                                                    .addOnCompleteListener(task2 -> {
                                                        if (!task2.isSuccessful()) {
                                                            mError.setValue(R.string.error_creating_progress_note);
                                                            return;
                                                        }

                                                        mError.setValue(Checker.VALID);
                                                    });
                                        }
                                        return null;
                                    });
                        }

                        return null;
                    });
        }
    }

    public LiveData<Integer> getmError() {
        return mError;
    }

    public void updateAppointment(Appointment appointment) {
        AppointmentRepository
                .getInstance()
                .upload(appointment);
    }

    public void setmPatient(Intent intent) {
        mPatient.setValue(intent);
    }

    public LiveData<Intent> getmPatient() {
        return mPatient;
    }

    public void addProcedure(Patient patient, Procedure procedure, ProgressNote progressNote) {
        patient.addProcedure(procedure.getUid());

        Log.d(TAG, "addProcedure: patient:  " + patient);
        Log.d(TAG, "addProcedure: procedure: " + procedure);
        Log.d(TAG, "addProcedure: progressnote: " + progressNote);

    }

    public void removeProcedure(String patientUid, String procedureUid) {
        PatientRepository
                .getInstance()
                .getPath(patientUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Patient patient = snapshot.getValue(Patient.class);

                        if (patient != null) {
                            PatientRepository
                                    .initialize(patient);

                            ProcedureRepository
                                    .getInstance()
                                    .removeProcedure(patient, procedureUid, patient.getDentalProcedures());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}