package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.FirebaseHelper;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.employee_form.EmployeeFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_form.AddPatientActivity;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ItemAdapter.class.getSimpleName();

    public static final int TYPE_PATIENT = 0x001ED6ED;
    public static final int TYPE_EMPLOYEE = 0x001ED6EE;

    ArrayList<Person> personArrayList;
    private final Context context;
    private final int type;

    private final PatientRepository patientRepository;
    private final EmployeeRepository employeeRepository;

    private ItemViewHolder.ItemOnClickListener mItemOnClickListener;
    private AlertDialog alertDialog;

    public ItemAdapter(Context context, int isPatient) {
        this.context = context;
        this.personArrayList = new ArrayList<>();
        this.type = isPatient;

        this.patientRepository = PatientRepository.getInstance();
        this.employeeRepository = EmployeeRepository.getInstance();
    }

    public void setItems(ArrayList<Person> list) {
        personArrayList.clear();
        personArrayList.addAll(list);
    }

    public void addItem(Person person) {
        personArrayList.add(person);
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
        Person person = personArrayList.get(position);

        initializeView(itemViewHolder, person);

        itemViewHolder.delete.setOnClickListener(v -> {
            //  TODO:  Show alert dialog to confirm delete
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder
                    .setTitle(R.string.delete_title)
                    .setMessage(context.getString(R.string.delete_message) + " " + person.getLastname())
                    .setPositiveButton("Yes", (dialog, which) -> {
                        switch (type) {
                            case TYPE_PATIENT:
                                patientRepository.remove((Patient) person);
                                break;
                            case TYPE_EMPLOYEE:
                                employeeRepository.remove((Employee) person, context);
                                break;
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> alertDialog.dismiss());
            alertDialog = builder.create();
            alertDialog.show();
        });

        itemViewHolder.edit.setOnClickListener(v -> {
            // TODO:    Show Edit Patient dialog
            switch (type) {
                case TYPE_PATIENT:
                    context.startActivity(new Intent(context, AddPatientActivity.class)
                            .putExtra(context.getString(R.string.PATIENT), personArrayList.get(holder.getAdapterPosition())));
                    break;

                case TYPE_EMPLOYEE:
                    context.startActivity(
                            new Intent(context, EmployeeFormActivity.class)
                            .putExtra(
                                    context.getString(R.string.EMPLOYEE),
                                    personArrayList.get(holder.getAdapterPosition()))
                    );
                    break;
            }
        });

        // Sets item on click listener
        itemViewHolder.itemView.setOnClickListener(v -> {
            mItemOnClickListener.onItemClick(personArrayList.get(holder.getAdapterPosition()));
        });

    }

    private void initializeView(ItemViewHolder itemViewHolder, Person person) {

        String name = person.getLastname() + ", " + person.getFirstname();

        //  Adds middle initial to the name.
        if (!Checker.isNotAvailable(person.getMiddleInitial())) {
            name += " " + person.getMiddleInitial() + '.';
        }

        //  Adds suffix to the name.
        if (Checker.isDataAvailable(person.getSuffix()) ) {
            if (!person.getSuffix().contains(".")) {    //  Checks if the suffix already contained period.
                person.setSuffix(person.getSuffix() + '.');
            }
            name += " " + person.getSuffix();
        }

        itemViewHolder.name.setText(name);

        switch (type) {
            case TYPE_PATIENT:
                itemViewHolder.imageView.setVisibility(View.GONE);

                Patient patient = (Patient) person;

                String patientContactNumber = patient.getPhoneNumber().get(0);
                itemViewHolder.text3.setText(patientContactNumber.equals(FirebaseHelper.NEW_PATIENT) ? "N/A" : patientContactNumber);

                String patientLastUpdated = context.getString(R.string.last_update) + " " + DateUtil.getDate(patient.getLastUpdated());
                itemViewHolder.text4.setText(patientLastUpdated);
                break;

            case TYPE_EMPLOYEE:
                itemViewHolder.imageView.setVisibility(View.VISIBLE);

                Employee employee = (Employee) person;

                Glide
                        .with(context)
                        .load(Uri.parse(employee.getDisplayImage()))
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemViewHolder.imageView);

                String employeePosition = employee.getJobTitle(context.getResources());
                itemViewHolder.text3.setText(employeePosition);

                String employeeLastUpdated = context.getString(R.string.last_update) + " " + DateUtil.getDate(employee.getLastUpdated());
                itemViewHolder.text4.setText(employeeLastUpdated);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return personArrayList.size();
    }

    public void clearAll() {
        personArrayList.clear();
    }

    public void setmItemOnClickListener(ItemViewHolder.ItemOnClickListener mItemOnClickListener) {
        this.mItemOnClickListener = mItemOnClickListener;
    }
}
