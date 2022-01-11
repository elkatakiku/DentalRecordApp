package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;

import java.util.ArrayList;
import java.util.List;

public class ServiceOptionsAdapter extends ArrayAdapter<DentalServiceOption> {
    private static final String TAG = ServiceOptionsAdapter.class.getSimpleName();

    public static final String DEFAULT_OPTION = "Choose service option…";

    private final Context mContext;
    private final List<DentalServiceOption> serviceOptionArrayList;
    private boolean isFromView = false;
    private final Spinner spinner;
    private final DentalServiceOption titleServiceOptionItem;
    private boolean isEdit;

    public ServiceOptionsAdapter(Context context, int resource, List<DentalServiceOption> objects, Spinner spinner) {
        super(context, resource, objects);
        this.mContext = context;
        this.serviceOptionArrayList = objects;
        this.spinner = spinner;

        this.titleServiceOptionItem = serviceOptionArrayList.get(0);
    }

    public ServiceOptionsAdapter(Context context, int resource, List<DentalServiceOption> objects, Spinner spinner, boolean isEdit) {
        super(context, resource, objects);
        this.mContext = context;
        this.serviceOptionArrayList = objects;
        this.spinner = spinner;

        this.titleServiceOptionItem = serviceOptionArrayList.get(0);
        this.isEdit = isEdit;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_spinner_services, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(serviceOptionArrayList.get(position).getTitle());

        // To check weather checked event fire from getView() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(serviceOptionArrayList.get(position).isSelected());
        isFromView = false;

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }


        holder.mTextView.setOnClickListener(v -> {
            holder.mCheckBox.setChecked(!holder.mCheckBox.isChecked());
            spinner.performClick();
        });



        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (!isFromView) {
                Log.d(TAG, "getCustomView: selected service: " + serviceOptionArrayList.get(position));
                if (position != 0) {
                    serviceOptionArrayList.get(position).setSelected(isChecked);
                }

                if (!hasItemChecked(serviceOptionArrayList)) {
                    Log.d(TAG, "getCustomView: has no item checks");
                    titleServiceOptionItem.setTitle(DEFAULT_OPTION);
                    Log.d(TAG, "getCustomView: default title: " + DEFAULT_OPTION);
                }
                else {
                    if (!getItem(position).equals(titleServiceOptionItem)) {
                        Log.d(TAG, "getCustomView: changing title");
                        Log.d(TAG, "getCustomView: item position of selected service: " + position);
                        titleServiceOptionItem.setTitle(getServiceTitle(
                                titleServiceOptionItem.getTitle(),
                                getItem(position),
                                serviceOptionArrayList
                        ));
                    }
                }

                spinner.setAdapter(this);

                Log.d(TAG, "getCustomView: is selected: " + getItem(position).isSelected());
            }
        });

        return convertView;
    }

    public void initializeSpinner() {
        if (serviceOptionArrayList != null && serviceOptionArrayList.size() > 0) {
            Log.d(TAG, "initializeSpinner: services: " + serviceOptionArrayList);

            StringBuilder newTitle = new StringBuilder();

            for (DentalServiceOption option : serviceOptionArrayList) {
                Log.d(TAG, "initializeSpinner: service: " + option);
                if (option.isSelected()) {
                    newTitle.append(option.getTitle()).append(" | ");
                }
            }

            int index = newTitle.lastIndexOf(" | ");
            if (index != -1) {
                newTitle.delete(index, newTitle.capacity());
                titleServiceOptionItem.setTitle(newTitle.toString());
            }
        }
    }

    private boolean hasItemChecked(List<DentalServiceOption> serviceOptions) {
        for (DentalServiceOption serviceOption : serviceOptions) {
            if (serviceOption.isSelected()) {
                Log.d(TAG, "hasItemChecked: service option: " + serviceOption);
                return true;
            }
        }

        return false;
    }

    private String getServiceTitle(String selectedTitles,
                                   DentalServiceOption selectedServiceOption,
                                   List<DentalServiceOption> serviceOptions) {
        final String selectedTitle = selectedServiceOption.getTitle();
        if (selectedTitles.equals(DEFAULT_OPTION)) {
            Log.d(TAG, "getServiceTitle: title is default");
            return selectedTitle;
        }

        Log.d(TAG, "getServiceTitle: selected titles: " + selectedTitles);
        Log.d(TAG, "getServiceTitle: selected title: " + selectedTitle);

        StringBuilder newTitle = new StringBuilder();

        String[] titles = selectedTitles.split(" \\| ");
        boolean inTitle = false;

        for (String title : titles) {
            Log.d(TAG, "getServiceTitle: selected title is default: " + selectedTitle.equals(title));
            if (selectedTitle.equals(title) && !(selectedTitle.equals(DEFAULT_OPTION))) {
                inTitle = true;

                Log.d(TAG, "getServiceTitle: title: " + title);

                DentalServiceOption gotSelectedService = getServiceOption(selectedServiceOption.getServiceUID(), serviceOptions);
                if (gotSelectedService != null) {
                    gotSelectedService.setSelected(false);
                }

                continue;

            }

            newTitle.append(title).append(" | ");
        }

        Log.d(TAG, "getServiceTitle: new title: " + newTitle.toString());
        Log.d(TAG, "getServiceTitle: new title length: " + newTitle.length());

        if (newTitle.length() == 0) {
            newTitle.append(DEFAULT_OPTION);
            serviceOptions.get(0).setSelected(false);
        }
        else if (!inTitle) {
            newTitle.append(selectedTitle);
        }
        else {
            int index = newTitle.lastIndexOf(" | ");
            if (index != -1) {
                newTitle.delete(index, newTitle.capacity());
            }
        }

        Log.d(TAG, "getServiceTitle: new title: " + newTitle.toString());

        return newTitle.toString();
    }

    private DentalServiceOption getServiceOption(String serviceUid, List<DentalServiceOption> dentalServiceOptions) {
        for (DentalServiceOption service :
                dentalServiceOptions) {
            if (serviceUid.equals(service.getServiceUID())){
                return service;
            }
        }

        return null;
    }

    public void clearAll() {
        serviceOptionArrayList.clear();
    }

    public void addItem(DentalServiceOption service) {
        serviceOptionArrayList.add(service);
    }

    public void addItems(ArrayList<DentalServiceOption> services) {
        clearAll();

        for (DentalServiceOption service : services) {
            Log.d(TAG, "addItems: service: " + service);
        }

        serviceOptionArrayList.addAll(services);
    }

    private static class ViewHolder {
        private final TextView mTextView;
        private final CheckBox mCheckBox;

        public ViewHolder(View view) {
            this.mTextView = view.findViewById(R.id.tvServiceLabel);
            this.mCheckBox = view.findViewById(R.id.cbService);
        }
    }

    public static class selectorListener implements AdapterView.OnItemSelectedListener {

        private final LinearLayout layoutError;

        public selectorListener(LinearLayout layoutError) {
            this.layoutError = layoutError;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            layoutError.setVisibility(View.GONE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
