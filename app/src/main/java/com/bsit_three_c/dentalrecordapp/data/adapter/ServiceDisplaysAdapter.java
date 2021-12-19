package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ServiceDisplaysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ServiceDisplaysAdapter.class.getSimpleName();

    ArrayList<DentalService> dentalServices;
    private final Context context;

    private final ServiceRepository serviceRepository;
    private ServicesViewHolder.ItemOnClickListener mItemOnClickListener;

    private final Activity activity;

    public ServiceDisplaysAdapter(Context context) {
        this(context, null);
    }

    public ServiceDisplaysAdapter(Context context, Activity activity) {
        this.context = context;

        this.serviceRepository = ServiceRepository.getInstance();
        this.dentalServices = new ArrayList<>();
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: creating view holder");
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_services, parent, false);
        return new ServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder: binding");

        ServicesViewHolder servicesViewHolder = (ServicesViewHolder) holder;
        DentalService service = dentalServices.get(position);

        Log.d(TAG, "onBindViewHolder: current service: " + service);

        Glide
                .with(context)
                .load(Uri.parse(service.getDisplayImage()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(servicesViewHolder.display);

        servicesViewHolder.name.setText(service.getTitle());
        servicesViewHolder.display.setImageURI(Uri.parse(service.getDisplayImage()));

        servicesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemOnClickListener.onItemClick(dentalServices.get(holder.getAdapterPosition()));
            }
        });

        Log.d(TAG, "onBindViewHolder: uri: " + Uri.parse(service.getDisplayImage()));
    }

    @Override
    public int getItemCount() {
        return dentalServices.size();
    }

    public void clearAll() {
        dentalServices.clear();
    }

    public void addItem(DentalService service) {
        dentalServices.add(service);
    }

    public void addItems(ArrayList<DentalService> services) {
        clearAll();

        Log.d(TAG, "setItems: adding list");
        for (DentalService service : services) {
            Log.d(TAG, "addItems: service: " + service);
        }

        dentalServices.addAll(services);
    }

    public void setmItemOnClickListener(ServicesViewHolder.ItemOnClickListener mItemOnClickListener) {
        this.mItemOnClickListener = mItemOnClickListener;
    }

}
