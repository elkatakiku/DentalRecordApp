package com.bsit_three_c.dentalrecordapp.ui.dental_chart;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.DentalChart;
import com.bsit_three_c.dentalrecordapp.data.repository.DentalChartRepository;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Objects;

public class DentalChartViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private static final String TAG = DentalChartViewModel.class.getSimpleName();

    private final DentalChartRepository dentalChartRepository;

    private final MutableLiveData<byte[]> mImageByte = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mHasError = new MutableLiveData<>();
    private final MutableLiveData<DentalChart> mDentaChart = new MutableLiveData<>();

    public DentalChartViewModel(DentalChartRepository dentalChartRepository) {
        this.dentalChartRepository = dentalChartRepository;
    }

    public void setmImageByte(Bitmap resource) {
        mImageByte.setValue(UIUtil.getByteArray(resource));
    }

    public LiveData<byte[]> getmImageByte() {
        return mImageByte;
    }

    public void uploadImage() {
        Log.d(TAG, "uploadImage: upload image");
        if (mImageByte.getValue() != null) {
            String chartUid = dentalChartRepository.getNewUid();

            dentalChartRepository.uploadImage(chartUid, mImageByte.getValue()).continueWith(new Continuation<Uri, Object>() {
                @NonNull
                @Override
                public Object then(@NonNull Task<Uri> task) throws Exception {
                    if (!task.isSuccessful()) {
                        mHasError.setValue(true);
                        throw Objects.requireNonNull(task.getException());
                    }

                    dentalChartRepository.add(new DentalChart(chartUid, task.getResult().toString(), new Date()));
                    mHasError.setValue(false);
                    return null;
                }
            });
        }
    }

    public LiveData<Boolean> getmHasError() {
        return mHasError;
    }

    public void getChart() {
        dentalChartRepository.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DentalChart dentalChart = snapshot.getValue(DentalChart.class);

                if (dentalChart != null) {
                    mDentaChart.setValue(dentalChart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<DentalChart> getmDentaChart() {
        return mDentaChart;
    }
}