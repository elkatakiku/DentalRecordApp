package com.bsit_three_c.dentalrecordapp.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceOptionsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ServiceRepository extends BaseRepository {

    private static final String TAG = ServiceRepository.class.getSimpleName();

    private final FirebaseStorage firebaseStorage;
    private final StorageReference storageReference;

    private static volatile ServiceRepository instance;

//    private ServiceDisplaysAdapter adapter;

    public ServiceRepository() {
        super(SERVICES_REFERENCE);

        this.firebaseStorage = FirebaseStorage.getInstance(FIREBASE_STORAGE_URL);
        this.storageReference = firebaseStorage.getReference(SERVICES_DISPLAY_IMAGE_LOCATION);
    }

    public static ServiceRepository getInstance() {
        if (instance == null) instance = new ServiceRepository();
        return instance;
    }

//    public void setAdapter(ServiceDisplaysAdapter adapter) {
//        this.adapter = adapter;
//    }

    public void upload(DentalService service) {
        databaseReference.child(service.getServiceUID()).setValue(service);
    }

    public void removeService(DentalService service) {
        databaseReference.child(service.getServiceUID()).removeValue();
        storageReference.child(service.getServiceUID() + ".png").delete();
    }

//    public void getServices() {
//        databaseReference.orderByChild("title").addValueEventListener(servicesEventListener);
//    }

    public Query getServicesPath() {
        return databaseReference.orderByChild("title");
    }

    public String getNewUid() {
        return UUID.randomUUID().toString();
    }

    public Task<Uri> uploadDisplayImage(DentalService dentalService, byte[] imageByte) {
        String child = dentalService.getServiceUID() + IMAGE_EXTENSION;

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata(dentalService.getTitle(), dentalService.getTitle() + " display image")
                .build();

        return storageReference.child(child).putBytes(imageByte, metadata).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            Log.d(TAG, "then: got URI");
            return storageReference.child(child).getDownloadUrl();
        });
    }

    public static void initializeService(DentalService service) {
        if (!Checker.isDataAvailable(service.getTitle()))
            service.setTitle("N/A");
        if (!Checker.isDataAvailable(service.getDescription()))
            service.setDescription("N/A");
        if (service.getCategories() == null)
            service.setCategories(new ArrayList<>());
    }

    public void setServicesOptions(ArrayList<DentalService> dentalServices, ArrayList<DentalServiceOption> serviceOptions) {
        serviceOptions.clear();
        serviceOptions.add(new DentalServiceOption(ServiceOptionsAdapter.DEFAULT_OPTION, ServiceOptionsAdapter.DEFAULT_OPTION, false));
        for (DentalService dentalService : dentalServices) {
            serviceOptions.add(
                    new DentalServiceOption(
                            dentalService.getServiceUID(),
                            dentalService.getTitle(),
                            false));
        }

        Log.d(TAG, "setServicesOptions: services options: " + serviceOptions);
    }

    public void setServicesOptions(List<DentalService> dentalServices,
                                   List<DentalServiceOption> serviceOptions,
                                   List<String> servicesUid) {
        serviceOptions.clear();
        serviceOptions.add(new DentalServiceOption(ServiceOptionsAdapter.DEFAULT_OPTION, ServiceOptionsAdapter.DEFAULT_OPTION, false));

        for (DentalService dentalService : dentalServices) {
            serviceOptions.add(new DentalServiceOption(
                    dentalService.getServiceUID(),
                    dentalService.getTitle(),
                    UIUtil.isServiceSelected(servicesUid, dentalService.getServiceUID())));
        }

        Log.d(TAG, "setServicesOptions: services options: " + serviceOptions);
    }

    public static class ServicesListener implements ValueEventListener {

        private final MutableLiveData<List<DentalService>> mDentalServices;

        public ServicesListener(MutableLiveData<List<DentalService>> mDentalServices) {
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
                    dentalServices.add(service);
                }
            }

            mDentalServices.setValue(dentalServices);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }
}
