package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class ServiceOptionsAdapter extends ArrayAdapter<DentalServiceOption> {
    private static final String TAG = ServiceOptionsAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<DentalServiceOption> serviceOptionArrayList;
    private ServiceOptionsAdapter myAdapter;
    private boolean isFromView = false;
    private String[] servicesArray;
    private Spinner spinner;
    private DentalServiceOption titleServiceOptionItem;

    public ServiceOptionsAdapter(Context context, int resource, List<DentalServiceOption> objects, String[] servicesArray, Spinner spinner) {
        super(context, resource, objects);
        this.mContext = context;
        this.serviceOptionArrayList = (ArrayList<DentalServiceOption>) objects;
        this.myAdapter = this;
        this.servicesArray = servicesArray;
        this.spinner = spinner;

        this.titleServiceOptionItem = serviceOptionArrayList.get(0);
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
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.item_spinner_services, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(serviceOptionArrayList.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
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
            int getPosition = (Integer) buttonView.getTag();

            if (!isFromView) {
                serviceOptionArrayList.get(position).setSelected(isChecked);

                if (!Checker.hasItemChecked(serviceOptionArrayList)) {
                    Log.d(TAG, "getCustomView: has no item chekces");
                    titleServiceOptionItem.setTitle(servicesArray[0]);
                }
                else {
                    if (!getItem(position).equals(titleServiceOptionItem)) {
                        Log.d(TAG, "getCustomView: changing title");
                        DentalServiceOption currentServiceOption = getItem(position);
                        titleServiceOptionItem.setTitle(UIUtil.getServiceTitle(
                                titleServiceOptionItem.getTitle(),
                                currentServiceOption.getTitle(),
                                serviceOptionArrayList,
                                servicesArray[0],
                                servicesArray
                        ));
                        spinner.setAdapter(this);
                    }
                }

                Log.d(TAG, "getCustomView: is selected: " + getItem(position).isSelected());
            }
        });


        return convertView;
    }

    private static class ViewHolder {
        private final TextView mTextView;
        private final CheckBox mCheckBox;

        public ViewHolder(View view) {
            this.mTextView = view.findViewById(R.id.tvServiceLabel);
            this.mCheckBox = view.findViewById(R.id.cbService);
        }
    }
}
