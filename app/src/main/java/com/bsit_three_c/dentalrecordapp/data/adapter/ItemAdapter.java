package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ItemAdapter.class.getSimpleName();

    ArrayList<Person> personArrayList;
    private final Context context;
    private final boolean isPatient;

    private final PatientRepository patientRepository;

    private ItemViewHolder.ItemOnClickListener mItemOnClickListener;
    private AlertDialog alertDialog;

    public ItemAdapter(Context context, boolean isPatient) {
        this.context = context;
        this.personArrayList = new ArrayList<>();
        this.isPatient = isPatient;

        this.patientRepository = PatientRepository.getInstance();
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
        String name = person.getLastname() + ", " + person.getFirstname();

        if (!Checker.isNotAvailable(person.getMiddleInitial()))
            name += " " + person.getMiddleInitial() + ".";

        itemViewHolder.name.setText(name);

        if (isPatient) {
            Patient patient = (Patient) person;
            String address = patient.getAddress();
            String lastUpdated = context.getString(R.string.last_update) + " " + UIUtil.getDate(patient.getLastUpdated());

            itemViewHolder.text2.setText(address);
            itemViewHolder.text3.setText(patient.getPhoneNumber());
            itemViewHolder.text4.setText(lastUpdated);


        } else {
            Account account = (Account) person;
//            String accountType = account.isAdmin() ? "Admin" : "Non Admin";
            itemViewHolder.text2.setText(account.getEmail());
//            itemViewHolder.text3.setText(accountType);
        }

        itemViewHolder.delete.setOnClickListener(v -> {
            //  TODO:  Show alert dialog to confirm delete

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder
                    .setTitle(R.string.delete_title)
                    .setMessage(context.getString(R.string.delete_message) + " " + person.getLastname())
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (isPatient) patientRepository.remove((Patient) person);
                        else Snackbar.make(v, "Delete user", Snackbar.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> alertDialog.dismiss());
            alertDialog = builder.create();
            alertDialog.show();
        });

        itemViewHolder.edit.setOnClickListener(v -> {
            Snackbar.make(v, "Edit", Snackbar.LENGTH_SHORT).show();
            // TODO:    Show Edit Patient dialog
        });

        // Sets item on click listener
        itemViewHolder.itemView.setOnClickListener(v ->
                mItemOnClickListener.onItemClick(personArrayList.get(holder.getAdapterPosition())));

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
