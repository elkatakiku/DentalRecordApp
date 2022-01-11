package com.bsit_three_c.dentalrecordapp.ui.appointments;

import android.content.Intent;

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

import java.util.ArrayList;

public class AppointmentsViewModel extends ViewModel {
    private static final String TAG = AppointmentsViewModel.class.getSimpleName();
    // TODO: Implement the ViewModel

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;

    private final MutableLiveData<ArrayList<Appointment>> mAppointments = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<DentalService>> mServices = new MutableLiveData<>();
    private final MutableLiveData<Integer> mError = new MutableLiveData<>();

    private final AppointmentRepository.AppointmentListener appointmentListener =  new AppointmentRepository.AppointmentListener(mAppointments);
    private final ServiceRepository.ServicesListener servicesListener = new ServiceRepository.ServicesListener(mServices);

    public AppointmentsViewModel() {
        this.appointmentRepository = AppointmentRepository.getInstance();
        this.serviceRepository = ServiceRepository.getInstance();
    }

    public void loadAppointments() {
        appointmentRepository.getAppointmentsByLastname().addValueEventListener(appointmentListener);
    }

    public void loadServices() {
        serviceRepository.getServicesPath().addValueEventListener(servicesListener);
    }

    public LiveData<ArrayList<Appointment>> getmAppointments() {
        return mAppointments;
    }

    public LiveData<ArrayList<DentalService>> getmServices() {
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
}