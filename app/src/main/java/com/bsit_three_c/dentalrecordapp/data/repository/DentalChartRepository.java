package com.bsit_three_c.dentalrecordapp.data.repository;

import android.net.Uri;
import android.util.Log;

import com.bsit_three_c.dentalrecordapp.data.model.DentalChart;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class DentalChartRepository extends BaseRepository {
    private static final String TAG = DentalChartRepository.class.getSimpleName();

    private final FirebaseStorage firebaseStorage;
    private final StorageReference storageReference;

    private static volatile DentalChartRepository instance;

    public DentalChartRepository() {
        super(DENTAL_CHART_REFERENCE);
        this.firebaseStorage = FirebaseStorage.getInstance(FIREBASE_STORAGE_URL);
        this.storageReference = firebaseStorage.getReference(DENTAL_CHART_IMAGE_LOCATION);
    }

    public static DentalChartRepository getInstance() {
        if (instance == null) {
            instance = new DentalChartRepository();
        }
        return instance;
    }

    public Task<Uri> uploadImage(String dentalChartUid, byte[] imageByte) {

        String child = dentalChartUid + IMAGE_EXTENSION;

        StorageMetadata metadata = new StorageMetadata
                .Builder()
                .setCustomMetadata("dental chart", "dental chart image")
                .build();

        return storageReference.child(child).putBytes(imageByte, metadata).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            Log.d(TAG, "then: got URI");
            return storageReference.child(child).getDownloadUrl();
        });
    }

    public void add(DentalChart dentalChart) {
        databaseReference.setValue(dentalChart);
    }

    public void count(ValueEventListener countEmployees) {
        databaseReference.addListenerForSingleValueEvent(countEmployees);
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
}
