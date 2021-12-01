package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalOperation;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.List;

public class HistoryItemAdapter extends ArrayAdapter {
    private static final String TAG = HistoryItemAdapter.class.getSimpleName();

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<DentalOperation> denstalHistory;

    public HistoryItemAdapter(@NonNull Context context, int resource, List<DentalOperation> dentalHistory) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.denstalHistory = dentalHistory;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DentalOperation currentRecord = denstalHistory.get(position);

        viewHolder.txtDentalDesc.setText(currentRecord.getDentalDesc());
        viewHolder.txtDentalDate.setText(currentRecord.getDentalDate());
        viewHolder.txtDentalAmount.setText(String.valueOf(currentRecord.getDentalAmount()));
        viewHolder.isFullyPaid.setChecked(currentRecord.isFullyPaid());

        viewHolder.isFullyPaid.setBackgroundTintList(UIUtil.getCheckBoxColor(currentRecord.isFullyPaid()));
        viewHolder.txtFullyPaid.setTextColor(UIUtil.getCheckBoxColor(currentRecord.isFullyPaid()));

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
        final CheckBox isFullyPaid;

        public ViewHolder(View v) {
            this.txtDentalDesc = v.findViewById(R.id.txtDentalDesc);
            this.txtDentalDate = v.findViewById(R.id.txtDentalDate);
            this.txtDentalAmount = v.findViewById(R.id.txtDentalAmount);
            this.txtFullyPaid = v.findViewById(R.id.txtFullyPaid);
            this.isFullyPaid = v.findViewById(R.id.cbFullyPaid);
        }
    }
}
