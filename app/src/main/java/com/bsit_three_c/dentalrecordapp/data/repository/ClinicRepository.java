package com.bsit_three_c.dentalrecordapp.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.model.Clinic;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ClinicRepository extends BaseRepository {

    public ClinicRepository() {
        super(CLINIC_REFERENCE);
    }

    public static BaseRepository getInstance() {
        if (instance == null) {
            instance = new ClinicRepository();
        }
        return instance;
    }

    public Task<Void> upload(Clinic clinic) {
        return databaseReference.setValue(clinic);
    }

    public static void initialize(Clinic clinic) {

        if (!Checker.isDataAvailable(clinic.getName())) {
            clinic.setLocation(Checker.NOT_AVAILABLE);
        }

        if (!Checker.isDataAvailable(clinic.getLocation())) {
            clinic.setLocation(Checker.NOT_AVAILABLE);
        }

        if (clinic.getContact() == null) {
            clinic.setContact(new ArrayList<>());
        }

        if (clinic.getStartDay() == -1) {
            clinic.setStartDay(0);
        }

        if (clinic.getEndDay() == -1) {
            clinic.setEndDay(0);
        }

        if (clinic.getStartTime() == -1) {
            clinic.setStartTime(new Date().getTime());
        }

        if (clinic.getEndTime() == -1) {
            clinic.setEndTime(new Date().getTime());
        }
    }

    public static class ClinicListener implements ValueEventListener {

        private final MutableLiveData<Clinic> mClinic;

        public ClinicListener(MutableLiveData<Clinic> mClinic) {
            this.mClinic = mClinic;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Clinic clinic = snapshot.getValue(Clinic.class);

            if (clinic != null) {
                initialize(clinic);
            }
            mClinic.setValue(clinic);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }
}
