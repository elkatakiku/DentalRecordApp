package com.bsit_three_c.dentalrecordapp.data.repository.listeners;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ServicesEventListener implements ValueEventListener {
    private static final String TAG = ServicesEventListener.class.getSimpleName();

    private final MutableLiveData<ArrayList<DentalService>> mDentalServices;

    public ServicesEventListener(MutableLiveData<ArrayList<DentalService>> mDentalServices) {
        this.mDentalServices = mDentalServices;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        final ArrayList<DentalService> dentalServices = new ArrayList<>();

        Log.d(TAG, "onDataChange: snapshot count: " + snapshot.getChildrenCount());
        Log.d(TAG, "onDataChange: snapshot: " + snapshot);
        if (!(snapshot.getChildrenCount() <= 0)) {

            for (DataSnapshot data : snapshot.getChildren()) {
                DentalService service = data.getValue(DentalService.class);

                if (service == null) continue;

                ServiceRepository.initializeService(service);
                Log.d(TAG, "onDataChange: service: " + service);
                dentalServices.add(service);
            }
        }

        mDentalServices.setValue(dentalServices);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
