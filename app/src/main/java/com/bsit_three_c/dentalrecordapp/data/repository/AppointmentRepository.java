package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AppointmentRepository extends BaseRepository {

    private static volatile AppointmentRepository instance;

    public AppointmentRepository() {
        super(APPOINTMENTS_REFERENCE);
    }

    public static AppointmentRepository getInstance() {
        if (instance == null) {
            instance = new AppointmentRepository();
        }
        return instance;
    }

    public Query getAppointmentsByLastname() {
        return databaseReference.orderByChild("timeStamp");
    }

    public static void initialize(Appointment appointment) {
        if (appointment.getPatient() == null) {
            AppointmentRepository.getInstance().remove(appointment.getUid());
        }

        if (appointment.getProcedure() == null) {
            AppointmentRepository.getInstance().remove(appointment.getUid());
        }

        if (appointment.getDateTime() == null) {
            appointment.setDateTime(new Date());
        }

        if (!Checker.isDataAvailable(appointment.getComments())) {
            appointment.setComments(Checker.NOT_AVAILABLE);
        }

        if (appointment.getTimeStamp() == -1L) {
            appointment.setTimeStamp(appointment.getDateTime().getTime());
        }

        appointment.setPassed(DateUtil.datePassed(appointment.getDateTime()));
    }

    public Task<Void> upload(Appointment appointment) {
        return databaseReference.child(appointment.getUid()).setValue(appointment);
    }

    public static class AppointmentListener implements ValueEventListener {

        private final MutableLiveData<List<Appointment>> mAppointment;

        public AppointmentListener(MutableLiveData<List<Appointment>> mAppointment) {
            this.mAppointment = mAppointment;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            final ArrayList<Appointment> appointments = new ArrayList<>();

            for (DataSnapshot data : snapshot.getChildren()) {
                Appointment appointment = data.getValue(Appointment.class);

                if (appointment == null) {
                    return;
                }

                Log.d("APPOINTMENT", "onDataChange: initializing appointment");

                initialize(appointment);
                PatientRepository.initialize(appointment.getPatient());
                ProcedureRepository.initialize(appointment.getProcedure());
                appointments.add(appointment);
            }

            Collections.reverse(appointments);
            mAppointment.setValue(appointments);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    public static class CountAppointment implements ValueEventListener {

        private final MutableLiveData<Integer> mTodayCount;
        private final MutableLiveData<Integer> mUpComingCount;

        public CountAppointment(MutableLiveData<Integer> mTodayCount, MutableLiveData<Integer> mUpComingCount) {
            this.mTodayCount = mTodayCount;
            this.mUpComingCount = mUpComingCount;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            int todayCount = 0;

            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                Appointment appointment = dataSnapshot.getValue(Appointment.class);

                if (appointment == null) {
                    return;
                }

                AppointmentRepository.initialize(appointment);
                if (DateUtil.isToday(appointment.getDateTime()) && appointment.isDone()) {
                    todayCount++;
                    mTodayCount.setValue(todayCount);
                }
            }

            mUpComingCount.setValue((int) snapshot.getChildrenCount() - todayCount);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }
}
