package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ItemAdapter.class.getSimpleName();
    ArrayList<Person> personArrayList;
    private final Context context;
    private final boolean isPatient;

    public ItemAdapter(Context context, boolean isPatient) {
        this.context = context;
        this.personArrayList = new ArrayList<>();
        this.isPatient = isPatient;
    }

    public void setItems(ArrayList<Person> list) {
//        Log.d(TAG, "setItems: setting items");
        personArrayList.addAll(list);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: creating view holder");
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder: Binding");
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Person person = personArrayList.get(position);
        String name = person.getLastname() + ", " + person.getFirstname() + " MI: " + person.getMiddlename();

        // Set image
//        itemViewHolder.imageView.setImageResource();
        itemViewHolder.name.setText(name);

        if (isPatient) {
//            Log.d(TAG, "onBindViewHolder: Patient found");
            Patient patient = (Patient) person;
            String address = "Address here: " + patient.getAddress();

//            Log.d(TAG, "onBindViewHolder: patient address:" + patient.getAddress());
            itemViewHolder.text2.setText(address);
            itemViewHolder.text3.setText(patient.getPhoneNumber());
        } else {
//            Log.d(TAG, "onBindViewHolder: Account found");
            Account account = (Account) person;
            String accountType = account.isAdmin() ? "Admin" : "Non Admin";
            itemViewHolder.text2.setText(account.getEmail());
            itemViewHolder.text3.setText(accountType);
        }
    }

    @Override
    public int getItemCount() {
        return personArrayList.size();
    }

    public void clearAll() {
        personArrayList.clear();
    }
}
