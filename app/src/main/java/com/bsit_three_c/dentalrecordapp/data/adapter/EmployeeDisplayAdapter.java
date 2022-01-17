package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDisplayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Employee> employees;
    private final Context context;

    public EmployeeDisplayAdapter(Context context) {
        this.context = context;
        employees = new ArrayList<>();
    }

    public void addItems(List<Employee> list) {
        employees.clear();
        employees.addAll(list);
    }

    public void addItem(Employee person) {
        employees.add(person);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctors_display, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DoctorViewHolder viewHolder = (DoctorViewHolder) holder;
        Employee employee = employees.get(position);

        Glide
                .with(context)
                .load(Checker.isDataAvailable(employee.getDisplayImage()) ? Uri.parse(employee.getDisplayImage()) : R.drawable.ic_baseline_person_24)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.display);

        UIUtil.setText(employee.getFullName(), viewHolder.name);
        UIUtil.setText(employee.getJobTitle(context.getResources()), viewHolder.job);
        if (employee.getSpecialties().isEmpty()) {
            viewHolder.description.setText("");
        } else {
            UIUtil.setText("Specializes in:\n" + TextUtils.join(", ", employee.getSpecialties()), viewHolder.description);
        }

    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public void clearAll() {
        employees.clear();
    }


    private static class DoctorViewHolder extends RecyclerView.ViewHolder {

        ImageView display;
        TextView name;
        TextView job;
        TextView description;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);

            display = itemView.findViewById(R.id.ivMenuDoctorDisplay);
            name = itemView.findViewById(R.id.tvMenuDoctorName);
            job = itemView.findViewById(R.id.tvMenuDoctorJobTitle);
            description = itemView.findViewById(R.id.tvMenuDoctorDescription);
        }
    }
}
