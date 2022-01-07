package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;

public class ServicesViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = ServicesViewHolder.class.getSimpleName();

    final TextView name;
    final ImageView display;

    public ServicesViewHolder(@NonNull View itemView, boolean isMenu) {
        super(itemView);

        if (isMenu) {
            Log.d(TAG, "ServicesViewHolder: is in menu");
            this.name = itemView.findViewById(R.id.ivMenuServiceName);
            this.display = itemView.findViewById(R.id.ivMenuServiceDisplay);
            Log.d(TAG, "ServicesViewHolder: name: " + name);
            Log.d(TAG, "ServicesViewHolder: display: " + display);
        } else {
            Log.d(TAG, "ServicesViewHolder: is in services");
            this.name = itemView.findViewById(R.id.tvServiceName);
            this.display = itemView.findViewById(R.id.imgServiceImage);
        }
    }

    public interface ItemOnClickListener {
        void onItemClick(DentalService service);
    }
}
