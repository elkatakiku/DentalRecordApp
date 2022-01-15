package com.bsit_three_c.dentalrecordapp.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class AdminRepository extends BaseRepository {

    private static volatile AdminRepository instance;

    public AdminRepository() {
        super(ADMIN_REFERENCE);
    }

    public static AdminRepository getInstance() {
        if (instance == null) {
            instance = new AdminRepository();
        }
        return instance;
    }

    public Task<Void> upload(Person person) {
        return databaseReference.setValue(person);
    }

    public static void initialize(Person person) {

        if (!Checker.isDataAvailable(person.getFirstname()))
            person.setFirstname(Checker.NOT_AVAILABLE);

        if (!Checker.isDataAvailable(person.getLastname()))
            person.setLastname(Checker.NOT_AVAILABLE);

        if (!Checker.isDataAvailable(person.getMiddleInitial()))
            person.setMiddleInitial(Checker.NOT_AVAILABLE);

        if (!Checker.isDataAvailable(person.getSuffix()))
            person.setSuffix(Checker.NOT_AVAILABLE);

        if (!Checker.isDataAvailable(person.getDateOfBirth())) {
            person.setDateOfBirth(Checker.NOT_AVAILABLE);
        }

        if (!Checker.isDataAvailable(person.getAddress()))
            person.setAddress(Checker.NOT_AVAILABLE);

        if (person.getPhoneNumber() == null) {
            ArrayList<String> contact = new ArrayList<>();
            contact.add(NEW_PATIENT);
            person.setPhoneNumber(contact);
        }

        if (person.getLastUpdated() == null)
            person.setLastUpdated(new Date());

        if (!Checker.isDataAvailable(person.getEmail())) {
            person.setEmail(Checker.NOT_AVAILABLE);
        }
    }

    public static class AdminListener implements ValueEventListener {

        private final MutableLiveData<Person> mPerson;

        public AdminListener(MutableLiveData<Person> mPerson) {
            this.mPerson = mPerson;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Person person = snapshot.getValue(Person.class);

            if (person != null) {
                initialize(person);
            }

            mPerson.setValue(person);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }
}
