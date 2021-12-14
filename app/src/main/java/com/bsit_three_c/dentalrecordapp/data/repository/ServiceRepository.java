package com.bsit_three_c.dentalrecordapp.data.repository;

import androidx.annotation.NonNull;

import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ServiceRepository {

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;

    private static volatile ServiceRepository instance;

    private ArrayList<DentalService> dentalServices;

    public ServiceRepository() {
        this.database = FirebaseDatabase.getInstance(FirebaseHelper.FIREBASE_URL);
        this.databaseReference = database.getReference(FirebaseHelper.SERVICES_REFERENCE);
    }

    public static ServiceRepository getInstance() {
        if (instance == null) instance = new ServiceRepository();
        return instance;
    }

    public void addService(DentalService service) {
        databaseReference.child(service.getServiceUID()).setValue(service);
    }

    public void removeService(DentalService service) {
        databaseReference.child(service.getServiceUID()).removeValue();
    }

    public void updateService(DentalService service) {
        databaseReference.child(service.getServiceUID()).setValue(service);
    }

    public void getServices() {
        databaseReference.addValueEventListener(servicesEventListener);
    }

    private ValueEventListener servicesEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (!(snapshot.getChildrenCount() <= 0)) {

                ServiceRepository.this.dentalServices = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    DentalService service = data.getValue(DentalService.class);

                    if (service == null) continue;


                    dentalServices.add(service);

                }

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void initializeService(DentalService service) {

        if (Checker.isDataAvailable(service.getServiceUID())) {

        }
    }
}
