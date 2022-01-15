package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.repository.AppointmentRepository;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = AppointmentsAdapter.class.getSimpleName();

    private final AppointmentRepository appointmentRepository;

    private final Context context;
    private final List<Appointment> appointments;

    private List<DentalService> dentalServices;

    private AppointmentClickListener mAppointmentOnClickListener;
    private AlertDialog alertDialog;

    public interface AppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    public AppointmentsAdapter(Context context) {
        this.context = context;
        this.appointmentRepository = AppointmentRepository.getInstance();
        this.appointments = new ArrayList<>();
    }

    public void setDentalServices(List<DentalService> dentalServices) {
        this.dentalServices = dentalServices;
    }

    public void setList(List<Appointment> newList) {
        appointments.clear();
        appointments.addAll(newList);
    }

    public void addItem(Appointment appointment) {
        appointments.add(appointment);
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Appointment appointment = appointments.get(position);


        if (appointment.isDone()) {
            itemViewHolder.itemView.setBackgroundTintList(ColorStateList.valueOf(0xFF71D5E4));
            ((CardView) itemViewHolder.itemView).setCardElevation(3f);
        }
        else if (appointment.isPassed()) {
            itemViewHolder.itemView.setBackgroundTintList(ColorStateList.valueOf(0xFFA1DBF1));
            ((CardView) itemViewHolder.itemView).setCardElevation(2f);
        }
        else {
            itemViewHolder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            ((CardView) itemViewHolder.itemView).setCardElevation(3f);
            itemViewHolder.name.setTextColor(0xFF295859);
            itemViewHolder.text3.setTextColor(0xFF01B5BB);
            itemViewHolder.text4.setTextColor(0xFF01B5BB);
        }

        if (appointment.isDone() || appointment.isPassed()) {
            itemViewHolder.name.setTextColor(Color.WHITE);
            itemViewHolder.text3.setTextColor(Color.WHITE);
            itemViewHolder.text4.setTextColor(Color.WHITE);
        }


        Log.d(TAG, "onBindViewHolder: time stamp: " + appointment.getDateTime().getTime());

        itemViewHolder.display.setVisibility(View.GONE);
        UIUtil.setText(appointment.getPatient().getFullName(), itemViewHolder.name);
        UIUtil.setText(appointment.getPatient().getContactNumber(), itemViewHolder.text3);

        String schedule = "Appointment: " + DateUtil.getReadableDateTime(appointment.getDateTime());
        UIUtil.setText(schedule, itemViewHolder.text4);

        itemViewHolder.edit.setOnClickListener(v -> {
            // TODO:    Show Edit Patient dialog
            editAppointment(appointment);
        });

        itemViewHolder.delete.setOnClickListener(v -> {
            //  TODO:  Show alert dialog to confirm delete
            showDeleteDialog(appointment);
        });

        // Sets item on click listener
        itemViewHolder.itemView.setOnClickListener(v -> {
//            showAppointmentDialog(v, appointment);
            mAppointmentOnClickListener.onAppointmentClick(
                    appointments.get(holder.getAdapterPosition())
            );
        });
    }

    private void showDeleteDialog(Appointment appointment) {
        String title = "Appointment";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
                .setTitle(context.getString(R.string.delete_title, title))
                .setMessage(context.getString(R.string.delete_message) + " " + title.toLowerCase() + " of " + appointment.getPatient().getLastname() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    appointmentRepository.remove(appointment.getUid());
                })
                .setNegativeButton("No", (dialog, which) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void editAppointment(Appointment appointment) {
        Log.d(TAG, "editAppointment: sending appointment: " + appointment);
        BaseFormActivity.showAppointmentForm(context, appointment);
//        context.startActivity(new Intent(context, BaseFormActivity.class)
//                .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_APPOINTMENT)
//                .putExtra(BaseFormActivity.APPOINTMENT_KEY, appointment)
//        );
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void clearAll() {
        appointments.clear();
    }

    public void setmAppointmentOnClickListener(AppointmentClickListener mAppointmentOnClickListener) {
        this.mAppointmentOnClickListener = mAppointmentOnClickListener;
    }

}
