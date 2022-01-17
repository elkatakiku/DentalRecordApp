package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class DentalHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final Context context;
    private final List<Procedure> procedures;
    private final List<DentalServiceOption> dentalServiceOptions;
    private DentalHistoryClickListener mDentalHistoryClickListener;

    public interface DentalHistoryClickListener {
        void onAppointmentClick(Procedure procedure);
    }

    public DentalHistoryAdapter(Context context) {
        this.context = context;
        this.procedures = new ArrayList<>();
        this.dentalServiceOptions = new ArrayList<>();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public void setItems(List<Procedure> procedures) {
        this.procedures.clear();
        this.procedures.addAll(procedures);
    }

    public void addItem(Procedure procedure) {
        Log.d("ADD", "addItem: adding procedure: " + procedure);
        this.procedures.add(procedure);
    }

    public void setDentalServiceOptions(List<DentalServiceOption> dentalServiceOptions) {
        this.dentalServiceOptions.clear();
        this.dentalServiceOptions.addAll(dentalServiceOptions);
    }

    public void clearAll() {
        this.dentalServiceOptions.clear();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dental_history, parent, false);
        return new DentalHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("DENTAL HISTORY VIEW", "onBindViewHolder: setting dental history view");

        DentalHistoryViewHolder itemViewHolder = (DentalHistoryViewHolder) holder;
        Procedure procedure = procedures.get(position);

        Log.d("SERVICES", "onBindViewHolder: dental services ids: " + procedure.getServiceIds());
        Log.d("SERVICES", "onBindViewHolder: dental services: " + dentalServiceOptions);

        itemViewHolder.service.setText(UIUtil.getServiceOptionsTitle(procedure.getServiceIds(), dentalServiceOptions));
        itemViewHolder.date.setText(DateUtil.getReadableDate(DateUtil.convertToDate(procedure.getDentalDate())));
        itemViewHolder.amount.setText(String.valueOf(procedure.getDentalTotalAmount()));
        itemViewHolder.isPaid.setText(UIUtil.getPaymentStatus(procedure.getDentalBalance()));
        itemViewHolder.isPaid.setTextColor(UIUtil.getCheckBoxColor(procedure.getDentalBalance()));

        itemViewHolder.itemView.setOnClickListener(v -> mDentalHistoryClickListener.onAppointmentClick(
                procedures.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return procedures.size();
    }

    public void setmAppointmentOnClickListener(DentalHistoryClickListener dentalHistoryClickListener) {
        this.mDentalHistoryClickListener = dentalHistoryClickListener;
    }

    private static class DentalHistoryViewHolder extends RecyclerView.ViewHolder{

        final TextView service;
        final TextView date;
        final TextView amount;
        final TextView isPaid;

        public DentalHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            this.service = itemView.findViewById(R.id.txtDentalService);
            this.date = itemView.findViewById(R.id.txtDentalDate);
            this.amount = itemView.findViewById(R.id.txtDentalAmount);
            this.isPaid = itemView.findViewById(R.id.txtDentalFullyPaid);

        }
    }


}
