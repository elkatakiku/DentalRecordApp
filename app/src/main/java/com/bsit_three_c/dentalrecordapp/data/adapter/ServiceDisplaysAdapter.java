package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.ArrayList;

public class ServiceDisplaysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ServiceDisplaysAdapter.class.getSimpleName();

    ArrayList<DentalService> dentalServices;
    private final Context context;

    private ServicesViewHolder.ItemOnClickListener mItemOnClickListener;

    private final boolean isMenu;

    public ServiceDisplaysAdapter(Context context, boolean isMenu) {
        this.context = context;
        this.isMenu = isMenu;
        this.dentalServices = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: creating view holder");
        View view;

        if (isMenu) {
            view = LayoutInflater.from(context).inflate(R.layout.item_service_display_card, parent, false);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.item_admin_services, parent, false);
        }

        Log.d(TAG, "onCreateViewHolder: view: " + view);

        return new ServicesViewHolder(view, isMenu);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder: binding");

        ServicesViewHolder servicesViewHolder = (ServicesViewHolder) holder;
        DentalService service = dentalServices.get(position);

        Log.d(TAG, "onBindViewHolder: current service: " + service);

        UIUtil.loadDisplayImage(
                context,
                servicesViewHolder.display,
                service.getDisplayImage(),
                R.drawable.ic_baseline_image_24
        );

        servicesViewHolder.name.setText(service.getTitle());

        servicesViewHolder.itemView.setOnClickListener(v ->
                mItemOnClickListener.onItemClick(dentalServices.get(holder.getAdapterPosition())));
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
