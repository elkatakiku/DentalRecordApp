package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.BaseRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.employees.employee_form.EmployeeFormActivity;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final String TAG = ItemAdapter.class.getSimpleName();

    public static final int TYPE_PATIENT = 0x001ED6ED;
    public static final int TYPE_EMPLOYEE = 0x001ED6EE;

    private List<Person> personList;
    private final Context context;
    private final int type;

    private final PatientRepository patientRepository;
    private final EmployeeRepository employeeRepository;

    private PatientRepository.PatientsAdapterListener patientsAdapterListener;

    private ItemViewHolder.ItemOnClickListener mItemOnClickListener;
    private AlertDialog alertDialog;

    private List<Person> personListFiltered;
    private String title;

    public ItemAdapter(Context context, int type) {
        this.context = context;
        this.personList = new ArrayList<>();
        this.type = type;

        this.patientRepository = PatientRepository.getInstance();
        this.employeeRepository = EmployeeRepository.getInstance();

        this.personListFiltered =  new ArrayList<>();
    }

    public void setPatientsAdapterListener(PatientRepository.PatientsAdapterListener patientsAdapterListener) {
        this.patientsAdapterListener = patientsAdapterListener;
    }

    public void setItems(List<Person> list) {
        Log.d(TAG, "setItems: called");
        personList.clear();
        personList.addAll(list);
    }

    public void addItem(Person person) {
        personList.add(person);
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
        Person person = personList.get(position);

        initializeView(itemViewHolder, person);

        itemViewHolder.edit.setOnClickListener(v -> {
            // TODO:    Show Edit Patient dialog
            editPerson(holder);
        });

        itemViewHolder.delete.setOnClickListener(v -> {
            //  TODO:  Show alert dialog to confirm delete
            showDeleteDialog(itemViewHolder, position);
        });

        // Sets item on click listener
        itemViewHolder.itemView.setOnClickListener(v -> {
            mItemOnClickListener.onItemClick(personList.get(holder.getAdapterPosition()));
        });

    }

    private void initializeView(ItemViewHolder itemViewHolder, Person person) {

        itemViewHolder.name.setText(person.getFullName());

        switch (type) {
            case TYPE_PATIENT:
                title = "Patient";
                itemViewHolder.display.setVisibility(View.GONE);

                Patient patient = (Patient) person;

                String patientContactNumber = patient.getPhoneNumber().get(0);
                itemViewHolder.text3.setText(patientContactNumber.equals(BaseRepository.NEW_PATIENT) ? "N/A" : patientContactNumber);

                String patientLastUpdated = context.getString(R.string.last_update) + " " + DateUtil.getDate(patient.getLastUpdated());
                itemViewHolder.text4.setText(patientLastUpdated);
                break;

            case TYPE_EMPLOYEE:
                title = "Employee";
                itemViewHolder.display.setVisibility(View.VISIBLE);

                Employee employee = (Employee) person;

                Glide
                        .with(context)
                        .load(Checker.isDataAvailable(employee.getDisplayImage()) ? Uri.parse(employee.getDisplayImage()) : R.drawable.ic_baseline_person_24)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(itemViewHolder.display);

                String employeePosition = employee.getJobTitle(context.getResources());
                itemViewHolder.text3.setText(employeePosition);

                String employeeLastUpdated = context.getString(R.string.last_update) + " " + DateUtil.getDate(employee.getLastUpdated());
                itemViewHolder.text4.setText(employeeLastUpdated);
                break;
        }
    }

    private void showDeleteDialog(ItemViewHolder itemViewHolder, int position) {
        Person person = personList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
                .setTitle(context.getString(R.string.delete_title, title))
                .setMessage(context.getString(R.string.delete_message) + " " + title.toLowerCase() + ": " + person.getLastname() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {

//                    itemViewHolder.itemView.setBackgroundTintList(ColorStateList.valueOf(0xFFA1DBF1));
//                    ((CardView) itemViewHolder.itemView).setCardElevation(0f);
//                    itemViewHolder.itemView.setEnabled(false);

                    LoggedInUser loggedInAccount = LocalStorage.getLoggedInUser(context);

                    switch (type) {
                        case TYPE_PATIENT:
                            Log.d(TAG, "onBindViewHolder: patient to be delete: " + person);
                            if (Checker.isDataAvailable(person.getAccountUid())) {
                                patientRepository.remove((Patient) person, loggedInAccount, patientsAdapterListener);
                            } else {
                                patientRepository.remove(person.getUid());
                            }
                            break;
                        case TYPE_EMPLOYEE:
                            Log.d(TAG, "showDeleteDialog: employee to be removed: " + person);
                            employeeRepository.remove(loggedInAccount, (Employee) person);
                            break;
                    }
//                    notifyItemRemoved(position);
                })
                .setNegativeButton("No", (dialog, which) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void editPerson(RecyclerView.ViewHolder holder) {
        switch (type) {
            case TYPE_PATIENT:
                context.startActivity(
                        BaseFormActivity.getPatientFormIntent(context, (Patient) personList.get(holder.getAdapterPosition()))
                );
//                context.startActivity(new Intent(context, BaseFormActivity.class)
//                        .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_PATIENT)
//                        .putExtra(BaseFormActivity.PATIENT_KEY, personList.get(holder.getAdapterPosition())));
                break;

            case TYPE_EMPLOYEE:
                EmployeeFormActivity.showEmployeeForm(context, (Employee) personList.get(holder.getAdapterPosition()));
                break;
        }
    }

    public void setmItemOnClickListener(ItemViewHolder.ItemOnClickListener mItemOnClickListener) {
        this.mItemOnClickListener = mItemOnClickListener;
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public void clearAll() {
        personList.clear();
    }

    private List<Person> origList;

    public void initializeOrigList() {
        this.origList = personList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                Log.d(TAG, "performFiltering: perform filtering");
                String charString = charSequence.toString();
                Log.d(TAG, "performFiltering: looking for string: " + charString);

                if (charString.isEmpty()) {
                    Log.d(TAG, "performFiltering: string is empty");
                    personListFiltered = origList;
                } else {
                    Log.d(TAG, "performFiltering: string is not empty");
                    List<Person> filteredList = new ArrayList<>();
                    for (Person row : origList) {
                        Log.d(TAG, "performFiltering: iterating through list");
                        Log.d(TAG, "performFiltering: person: " + row);
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        Log.d(TAG, "performFiltering: has string in name: " + row.getFullName().toLowerCase().contains(charString.toLowerCase()));
                        Log.d(TAG, "performFiltering: has string in number: " + row.getContactNumber().contains(charSequence));
                        if (row.getFullName().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getContactNumber().contains(charSequence)) {
                            Log.d(TAG, "performFiltering: adding person to filtered list");
                            filteredList.add(row);
                        }
                    }

                    Log.d(TAG, "performFiltering: initializing filtered list");
                    Log.d(TAG, "performFiltering: filtered list: " + filteredList);
                    personListFiltered = filteredList;
                    Log.d(TAG, "performFiltering: field filtered list: " + personListFiltered);
                }

                Log.d(TAG, "performFiltering: creating filter results");
                FilterResults filterResults = new FilterResults();
                filterResults.values = personListFiltered;
                Log.d(TAG, "performFiltering: results value: " + filterResults.values);
                Log.d(TAG, "performFiltering: returning results");
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                Log.d(TAG, "publishResults: publishing results");
                Log.d(TAG, "publishResults: results: " + filterResults);
                personList = (List<Person>) filterResults.values;
                Log.d(TAG, "publishResults: setting list to filtered list");
                Log.d(TAG, "publishResults: updated list: " + personList);
                notifyDataSetChanged();
            }
        };
    }
}
