package com.bsit_three_c.dentalrecordapp.data.repository;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ServiceStorageRepository {

    private final FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private final String IMAGE_EXTENSION = ".png";

    private static volatile ServiceStorageRepository instance;

    public ServiceStorageRepository() {
        firebaseStorage = FirebaseStorage.getInstance("gs://dental-record-app.appspot.com");
        storageReference = firebaseStorage.getReference("services_display_image/");
    }

    public static ServiceStorageRepository getInstance() {
        if (instance == null) {
            instance = new ServiceStorageRepository();
        }

        return instance;
    }

    public void uploadImage(ImageView displayImage) {
        Bitmap capture = ((BitmapDrawable) displayImage.getDrawable()).getBitmap();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        capture.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        byte[] data = outputStream.toByteArray();
        String path = "services_display_image/" + UUID.randomUUID() + IMAGE_EXTENSION;

        String child = UUID.randomUUID() + IMAGE_EXTENSION;

//        StorageReference reference = firebaseStorage.getReference(path);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("caption", "TITLE HERE")
                .build();

        UploadTask uploadTask = storageReference.child(child).putBytes(data, metadata);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(displayImage, "Added", Snackbar.LENGTH_SHORT).show();
                }

                else if (task.isCanceled()){
                    Snackbar.make(displayImage, "Failed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}
