package com.bsit_three_c.dentalrecordapp.data.repository;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceDisplaysAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ServiceRepository {

    private static final String TAG = ServiceRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;

    private final FirebaseStorage firebaseStorage;
    private final StorageReference storageReference;

    private static volatile ServiceRepository instance;

    private ArrayList<DentalService> dentalServices;
    private ServiceDisplaysAdapter adapter;

    public ServiceRepository() {
        this.database = FirebaseDatabase.getInstance(FirebaseHelper.FIREBASE_URL);
        this.databaseReference = database.getReference(FirebaseHelper.SERVICES_REFERENCE);

        firebaseStorage = FirebaseStorage.getInstance(FirebaseHelper.FIREBASE_STORAGE_URL);
        storageReference = firebaseStorage.getReference(FirebaseHelper.SERVICES_DISPLAY_IMAGE_LOCATION);
    }

    public static ServiceRepository getInstance() {
        if (instance == null) instance = new ServiceRepository();
        return instance;
    }

    public ServiceDisplaysAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ServiceDisplaysAdapter adapter) {
        this.adapter = adapter;
    }

    public void addService(DentalService service) {
        databaseReference.child(service.getServiceUID()).setValue(service);
    }

    public void removeService(DentalService service) {
        databaseReference.child(service.getServiceUID()).removeValue();

        Log.d(TAG, "removeService: service uid: " + service.getServiceUID());

        storageReference.child(service.getServiceUID() + ".png").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) Log.d(TAG, "onComplete: deleted");

                Log.d(TAG, "onComplete: done deleting");
            }
        });
    }

    public void updateService(DentalService service) {
        databaseReference.child(service.getServiceUID()).setValue(service);
    }

    public void updateService(DentalService service, ImageView displayImage, boolean isImageChanged) {

        if (isImageChanged) {
            Bitmap capture = ((BitmapDrawable) displayImage.getDrawable()).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            capture.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            byte[] data = outputStream.toByteArray();

            String child = service.getServiceUID() + FirebaseHelper.IMAGE_EXTENSION;

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setCustomMetadata("caption", service.getTitle() + " display image")
                    .build();

            UploadTask uploadTask = storageReference.child(child).putBytes(data, metadata);

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Log.d(TAG, "onComplete: done uploading");
//                    isUploadDone.setValue(true);
                }
            });

            Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                Log.d(TAG, "then: got URI");
                return storageReference.child(child).getDownloadUrl();
            });

            getDownloadUriTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    Log.d(TAG, "onComplete: success getting URI");
                    service.setDisplayImage(task.getResult().toString());
                    addService(service);
                }
            });
        }
        else {
            updateService(service);
        }
    }

    public void getServices() {
        databaseReference.orderByChild("title").addValueEventListener(servicesEventListener);
    }

    private final ValueEventListener servicesEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ServiceRepository.this.dentalServices = new ArrayList<>();

            Log.d(TAG, "onDataChange: snapshot count: " + snapshot.getChildrenCount());
            Log.d(TAG, "onDataChange: snapshot: " + snapshot);
            if (!(snapshot.getChildrenCount() <= 0)) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    DentalService service = data.getValue(DentalService.class);

                    if (service == null) continue;

                    initializeService(service);
                    dentalServices.add(service);

                }
            }

            adapter.addItems(dentalServices);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public void addService(ImageView displayImage, MutableLiveData<Boolean> isUploadDone, DentalService service) {
        isUploadDone.setValue(false);

//        Bitmap capture = ((BitmapDrawable) displayImage.getDrawable()).getBitmap();
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//        capture.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//
//        byte[] data = outputStream.toByteArray();

        byte[] data = UIUtil.getOutputStreamImage(displayImage).toByteArray();

        String serviceUID = UUID.randomUUID().toString();
        String child = serviceUID + FirebaseHelper.IMAGE_EXTENSION;

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("caption", "TITLE HERE")
                .build();

        UploadTask uploadTask = storageReference.child(child).putBytes(data, metadata);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Log.d(TAG, "onComplete: done uploading");
                isUploadDone.setValue(true);
            }
        });

        Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            Log.d(TAG, "then: got URI");
            return storageReference.child(child).getDownloadUrl();
        });

        getDownloadUriTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                Log.d(TAG, "onComplete: success getting URI");
                service.setServiceUID(serviceUID);
                service.setDisplayImage(task.getResult().toString());
                addService(service);
            }
        });
    }

    public void removeListeners() {
        databaseReference.removeEventListener(servicesEventListener);
        databaseReference.removeEventListener(countServices);
    }

    private void initializeService(DentalService service) {
        if (!Checker.isDataAvailable(service.getTitle()))
            service.setTitle("N/A");
        if (!Checker.isDataAvailable(service.getDescription()))
            service.setDescription("N/A");
        if (service.getCategories() == null)
            service.setCategories(new ArrayList<>());
    }

    private final FirebaseHelper.CountChildren countServices = new FirebaseHelper.CountChildren();

    public void countServices() {
        databaseReference.addListenerForSingleValueEvent(countServices);
    }

    public LiveData<Long> getServicesCount() {
        return countServices.getCount();
    }
}
