package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.AppointmentRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.ui.appointments.appointment_form.AppointmentFormActivity;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = AppointmentsAdapter.class.getSimpleName();

    private final AppointmentRepository appointmentRepository;

    private final Context context;
    private final List<Appointment> appointments;

    private ArrayList<DentalService> dentalServices;

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

    public void setDentalServices(ArrayList<DentalService> dentalServices) {
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
//            itemViewHolder.name.setTextColor(Color.WHITE);
//            itemViewHolder.text3.setTextColor(Color.WHITE);
//            itemViewHolder.text4.setTextColor(Color.WHITE);
        }
        else if (appointment.isPassed()) {
            itemViewHolder.itemView.setBackgroundTintList(ColorStateList.valueOf(0xFFA1DBF1));
            ((CardView) itemViewHolder.itemView).setCardElevation(2f);
//            itemViewHolder.name.setTextColor(Color.WHITE);
//            itemViewHolder.text3.setTextColor(Color.WHITE);
//            itemViewHolder.text4.setTextColor(Color.WHITE);
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
        context.startActivity(new Intent(context, AppointmentFormActivity.class)
                .putExtra(
                        context.getString(R.string.APPOINTMENT_KEY),
                        appointment));
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

    public static class DialogHolder {

        public final TextView name;
        public final TextView contact;
        public final TextView date;
        public final TextView time;
        public final TextView service;
        public final TextView note;
        public final ImageButton btnClose;
        public final ImageButton btnDone;

        public DialogHolder(View dialogView) {
            this.name = dialogView.findViewById(R.id.tvAppointmentName);;
            this.contact = dialogView.findViewById(R.id.tvAppointmentContact);;
            this.date = dialogView.findViewById(R.id.tvAppointmentDate);;
            this.time = dialogView.findViewById(R.id.tvAppointmentTime);;
            this.service = dialogView.findViewById(R.id.tvAppointmentProcedure);;
            this.note = dialogView.findViewById(R.id.tvAppointmentNotes);;
            this.btnClose = dialogView.findViewById(R.id.ibEditAppointment);;
            this.btnDone = dialogView.findViewById(R.id.ibAppointmentDone);;
        }
    }

    private void showAppointmentDialog(View view, Appointment appointment) {
        final View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_appointment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        final TextView name = dialogView.findViewById(R.id.tvAppointmentName);
        final TextView contact = dialogView.findViewById(R.id.tvAppointmentContact);
        final TextView date = dialogView.findViewById(R.id.tvAppointmentDate);
        final TextView time = dialogView.findViewById(R.id.tvAppointmentTime);
        final TextView service = dialogView.findViewById(R.id.tvAppointmentProcedure);
        final TextView note = dialogView.findViewById(R.id.tvAppointmentNotes);
        final ImageButton btnClose = dialogView.findViewById(R.id.ibEditAppointment);
        final ImageButton btnDone = dialogView.findViewById(R.id.ibAppointmentDone);

        name.setText(appointment.getPatient().getFullName());
        contact.setText(appointment.getPatient().getContactNumber());
        date.setText(DateUtil.getReadableDate(appointment.getDateTime()));
        time.setText(DateUtil.getReadableTime(appointment.getDateTime()));
        service.setText(TextUtils.join("\n", UIUtil.getServicesList(dentalServices, appointment.getProcedure().getServiceIds())));
        note.setText(appointment.getComments());
        btnDone.setImageResource(appointment.isDone() ? R.drawable.ic_baseline_radio_button_unchecked_24 : R.drawable.ic_baseline_check_24);


        final AlertDialog appointmentDialog = builder.create();

        btnClose.setOnClickListener(v -> {
            appointmentDialog.dismiss();
        });

        btnDone.setOnClickListener(v -> {
            if (appointment.isPassed()) {
                Snackbar.make(v, "Appointment date has passed.", Snackbar.LENGTH_SHORT)
                        .show();
                return;
            }

            PatientRepository.getInstance().getPath(appointment.getPatient().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Patient patient = snapshot.getValue(Patient.class);

                    if (appointment.isDone()) {
                        appointment.setDone(false);
                    } else {
                        if (patient == null) {

                        }
                        appointment.setDone(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            Log.d(TAG, "showAppointmentDialog: button done clicked");
//            if (appointment.isDone()) {
//                appointment.setDone(false);
//
//            } else {
//

//                if (appointment.getPatient().getUid().isEmpty()) {
//
//                }
//                appointment.setDone(true);
//            }

            //  TODO: remove or add patient accordingly
            appointmentRepository.upload(appointment);
            appointmentDialog.dismiss();
        });

        appointmentDialog.show();
    }

}
