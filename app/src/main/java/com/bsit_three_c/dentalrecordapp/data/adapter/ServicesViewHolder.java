package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;

public class ServicesViewHolder extends RecyclerView.ViewHolder {

    final TextView name;

    public ServicesViewHolder(@NonNull View itemView) {
        super(itemView);

        this.name = itemView.findViewById(R.id.tvServiceName);
    }

    public interface ItemOnClickListener {
        void onItemClick(DentalService service);
    }
}
