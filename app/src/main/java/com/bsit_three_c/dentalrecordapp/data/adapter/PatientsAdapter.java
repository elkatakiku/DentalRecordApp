package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;

import java.util.ArrayList;
import java.util.List;

public class PatientsAdapter extends ArrayAdapter<Person> {
    private static final String TAG = PatientsAdapter.class.getSimpleName();

    public static final int TYPE_FIRSTNAME = 0x001ED9A9;
    public static final int TYPE_LASTNAME = 0x001ED9AA;

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private final List<Person> patients;
    private final List<Person> suggestions;
    private final int type;

    private OnNameClickListener nameClickListener;

    public PatientsAdapter(@NonNull Context context, int resource, List<Person> patients, int type) {
        super(context, resource);

        this.layoutInflater = LayoutInflater.from(context);
        this.layoutResource = resource;
        this.patients = patients;
        this.type = type;

        this.suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Person patient = suggestions.get(position);

        if (patient != null) {
            viewHolder.name.setText(suggestions.get(position).getFullName());
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Log.d(TAG, "getFilter: getting filter");
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        public String convertResultToString(Object resultValue) {
            Log.d(TAG, "convertResultToString: converting to string");
            String name = "";
            switch (type) {
                case TYPE_FIRSTNAME:
                    name = ((Patient) resultValue).getFirstname();
                    break;
                case TYPE_LASTNAME:
                    name = ((Patient) resultValue).getLastname();
                    break;
            }
            Log.d(TAG, "convertResultToString: converted string: " + name);
            return name;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Log.d(TAG, "performFiltering: filtering list");
            if (constraint != null) {
                Log.d(TAG, "performFiltering: searching for: " + constraint.toString());
                Log.d(TAG, "performFiltering: input is not null");
                suggestions.clear();
                for (Person patient : patients) {
                    Log.d(TAG, "performFiltering: searching person: " + patient.getFullName());
                    switch (type) {
                        case TYPE_FIRSTNAME:
                            Log.d(TAG, "performFiltering: checking if match: " + patient
                                    .getFirstname()
                                    .toLowerCase()
                                    .startsWith(constraint.toString().toLowerCase()));
                            if (patient
                                    .getFirstname()
                                    .toLowerCase()
                                    .startsWith(constraint.toString().toLowerCase())) {
                                Log.d(TAG, "performFiltering: adding patient: " + patient.getFirstname());
                                suggestions.add(patient);
                            }
                            break;
                        case TYPE_LASTNAME:
                            Log.d(TAG, "performFiltering: checking if match: " + patient
                                    .getLastname()
                                    .toLowerCase()
                                    .startsWith(constraint.toString().toLowerCase()));
                            if (patient
                                    .getLastname()
                                    .toLowerCase()
                                    .startsWith(constraint.toString().toLowerCase())) {
                                Log.d(TAG, "performFiltering: adding patient: " + patient.getLastname());
                                suggestions.add(patient);
                            }
                            break;
                    }
                }

                Log.d(TAG, "performFiltering: makin filter results");
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                Log.d(TAG, "performFiltering: returning results");
                return filterResults;
            } else {
                Log.d(TAG, "performFiltering: no input");
                return new FilterResults();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            List<Person> filteredList = (List<Person>) results.values;
            Log.d(TAG, "publishResults: changing results: " + filteredList);
            if (results != null && results.count > 0) {
                clear();
                for (Person c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

    public void setNameClickListener(OnNameClickListener nameClickListener) {
        this.nameClickListener = nameClickListener;
    }

    private static class ViewHolder {
        final TextView name;

        public ViewHolder(View view) {
            this.name = view.findViewById(android.R.id.text1);
        }
    }

    public interface OnNameClickListener {
        void OnClick(Patient patient);
    }
}
