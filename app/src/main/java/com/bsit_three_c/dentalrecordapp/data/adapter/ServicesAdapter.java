package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;

import java.util.ArrayList;

public class ServicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    ArrayList<DentalService> dentalServices;
    private final Context context;

    private final ServiceRepository serviceRepository;

    public ServicesAdapter(Context context) {
        this.context = context;

        this.serviceRepository = ServiceRepository.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_services, parent, false);
        return new ServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ServicesViewHolder servicesViewHolder = (ServicesViewHolder) holder;
        DentalService service = dentalServices.get(position);

        servicesViewHolder.name.setText(service.getTitle());
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
        dentalServices.addAll(services);
    }
}
