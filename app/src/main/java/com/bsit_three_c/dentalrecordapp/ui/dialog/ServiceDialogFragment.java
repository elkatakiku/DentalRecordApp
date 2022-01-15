package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

public class ServiceDialogFragment extends DialogFragment {

    private final DentalService dentalService;

    public ServiceDialogFragment(DentalService dentalService) {
        this.dentalService = dentalService;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_service, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        ServiceViewHolder serviceViewHolder = new ServiceViewHolder(dialogView);

        serviceViewHolder.delete.setVisibility(View.GONE);
        serviceViewHolder.edit.setVisibility(View.GONE);
        UIUtil.loadDisplayImage(requireContext(), serviceViewHolder.display, dentalService.getDisplayImage(), R.drawable.ic_baseline_image_24);
        UIUtil.setText(dentalService.getTitle(), serviceViewHolder.name);
        UIUtil.setText("\t" + dentalService.getDescription(), serviceViewHolder.desc);

        final AlertDialog serviceDialog = builder.create();

        serviceViewHolder.close.setOnClickListener(v -> serviceDialog.dismiss());

        return serviceDialog;
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {

        public final CardView cardView;
        public final ImageView delete;
        public final ImageView edit;
        public final ImageView close;
        public final ImageView display;
        public final TextView name;
        public final TextView desc;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.cvDialogServiceDisplay);
            this.delete = itemView.findViewById(R.id.ivDialogServiceDelete);
            this.edit = itemView.findViewById(R.id.ivDialogServiceEdit);
            this.close = itemView.findViewById(R.id.ivDialogServiceClose);
            this.display = itemView.findViewById(R.id.ivDialogServiceDisplay);
            this.name = itemView.findViewById(R.id.tvDialogServiceName);
            this.desc = itemView.findViewById(R.id.tvDialogServiceDescription);
        }
    }

}
