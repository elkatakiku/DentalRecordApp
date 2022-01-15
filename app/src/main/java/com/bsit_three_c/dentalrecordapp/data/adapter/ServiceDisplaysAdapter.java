package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.dialog.ServiceDialogFragment;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class ServiceDisplaysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ServiceDisplaysAdapter.class.getSimpleName();

    ArrayList<DentalService> dentalServices;
    private final Context context;

    private ServicesViewHolder.ItemOnClickListener mItemOnClickListener;

    private final boolean isInMenu;
    private final boolean isAdmin;
    private AlertDialog deleteDialog;

    public ServiceDisplaysAdapter(Context context, boolean isInMenu, boolean isAdmin) {
        this.context = context;
        this.isInMenu = isInMenu;
        this.isAdmin = isAdmin;
        this.dentalServices = new ArrayList<>();
    }

    public ServiceDisplaysAdapter(Context context, boolean isInMenu) {
        this(context, isInMenu, false);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: creating view holder");
        View view;
        RecyclerView.ViewHolder viewHolder;

        if (isInMenu) {
            view = LayoutInflater.from(context).inflate(R.layout.item_service_display_card, parent, false);
            viewHolder = new ServicesViewHolder(view, isInMenu);
        }
        else {
//            view = LayoutInflater.from(context).inflate(R.layout.item_admin_services, parent, false);
            view = LayoutInflater.from(context).inflate(R.layout.dialog_service, parent, false);
            viewHolder = new ServiceDialogFragment.ServiceViewHolder(view);
        }

        Log.d(TAG, "onCreateViewHolder: view: " + view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DentalService service = dentalServices.get(position);

        if (isInMenu) {
            ServicesViewHolder servicesViewHolder = (ServicesViewHolder) holder;

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
        } else {
            ServiceDialogFragment.ServiceViewHolder serviceViewHolder = (ServiceDialogFragment.ServiceViewHolder) holder;

            if (!isAdmin) {
                serviceViewHolder.delete.setVisibility(View.GONE);
                serviceViewHolder.edit.setVisibility(View.GONE);
            }

            serviceViewHolder.close.setVisibility(View.GONE);
            UIUtil.loadDisplayImage(context, serviceViewHolder.display, service.getDisplayImage(), R.drawable.ic_baseline_image_24);
            UIUtil.setText(service.getTitle(), serviceViewHolder.name);
            UIUtil.setText("\t" + service.getDescription(), serviceViewHolder.desc);

            serviceViewHolder.delete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder
                        .setTitle(context.getString(R.string.delete_title, "Service"))
                        .setMessage(context.getString(R.string.delete_message) + " this service?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            ServiceRepository.getInstance().removeService(service);
                        })
                        .setNegativeButton("No", (dialog, which) -> deleteDialog.dismiss());
                deleteDialog = builder.create();
                deleteDialog.show();
            });

            serviceViewHolder.edit.setOnClickListener(v -> {
                //  TODO: start activity on result
                context.startActivity(
                        new Intent(context, BaseFormActivity.class)
                                .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_SERVICE)
                                .putExtra(BaseFormActivity.SERVICE_KEY, service));
            });
        }
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

    public void addItems(List<DentalService> services) {
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
