package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class HistoryItemAdapter extends ArrayAdapter {
    private static final String TAG = HistoryItemAdapter.class.getSimpleName();

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private final List<Procedure> denstalHistory;

    public HistoryItemAdapter(@NonNull Context context, int resource, List<Procedure> dentalHistory) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.denstalHistory = dentalHistory;
    }

    public HistoryItemAdapter(@NonNull Context context, int resource) {
        this(context, resource, new ArrayList<>());
    }


    public void setItems(List<Procedure> dentalHistory) {
        this.denstalHistory.clear();
        this.denstalHistory.addAll(dentalHistory);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
            Log.d(TAG, "getView: converView is null");
        } else {
            Log.d(TAG, "getView: convertView tag: " + convertView.getTag());
            Log.d(TAG, "getView: convertView value: " + convertView);
            viewHolder = (ViewHolder) convertView.getTag();
            Log.d(TAG, "getView: convertView isn't null");
            Log.d(TAG, "getView: viewHolder: " + viewHolder);
        }

        Procedure currentRecord = denstalHistory.get(position);

        Log.d(TAG, "getView: current Record: " + currentRecord);

        Log.d(TAG, "getView: viewholder: " + viewHolder.toString());
        Log.d(TAG, "getView: txtView: " + viewHolder.txtDentalDesc);
        viewHolder.txtDentalDesc.setText(currentRecord.getDentalDesc());
        viewHolder.txtDentalDate.setText(DateUtil.getReadableDate(DateUtil.convertToDate(currentRecord.getDentalDate())));
        viewHolder.txtDentalAmount.setText(String.valueOf(currentRecord.getDentalTotalAmount()));
        viewHolder.txtFullyPaid.setTextColor(UIUtil.getCheckBoxColor(!currentRecord.isDownpayment()));

        return convertView;
    }

    @Override
    public int getCount() {
        return denstalHistory.size();
    }

    private static class ViewHolder {
        final TextView txtDentalDesc;
        final TextView txtDentalDate;
        final TextView txtDentalAmount;
        final TextView txtFullyPaid;

        public ViewHolder(View v) {
            this.txtDentalDesc = v.findViewById(R.id.txtDentalService);
            this.txtDentalDate = v.findViewById(R.id.txtDentalDate);
            this.txtDentalAmount = v.findViewById(R.id.txtDentalAmount);
            this.txtFullyPaid = v.findViewById(R.id.txtDentalFullyPaid);
        }
    }
}
