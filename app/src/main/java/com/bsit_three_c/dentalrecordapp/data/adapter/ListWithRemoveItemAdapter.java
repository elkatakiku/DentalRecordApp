package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

public class ListWithRemoveItemAdapter extends ArrayAdapter<String> {

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private final List<String> list;
    private ListView listView;
    private ListListener listListener;

    public ListWithRemoveItemAdapter(@NonNull Context context, int resource, List<String> list) {
        super(context, resource);

        this.layoutInflater = LayoutInflater.from(context);
        this.layoutResource = resource;
        this.list = list;
    }

    public ListWithRemoveItemAdapter(@NonNull Context context, int resource) {
        this(context, resource, new ArrayList<>());
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    public void add(String object) {
        list.add(object);

        if (listView != null) UIUtil.setListViewHeightBasedOnItems(listView);
        if (listListener != null) {
            listListener.onListChangeListener(list.size());
        }
    }

    public void addAll(List<String> items) {
        list.clear();
        list.addAll(items);
    }

    public void clear() {
        list.clear();
    }

    public void remove(String category) {
        list.remove(category);
        if (listListener != null) {
            listListener.onListChangeListener(list.size());
        }
    }

    public List<String> getList() {
        return list;
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

        viewHolder.name.setText(list.get(position));
        viewHolder.delete.setOnClickListener(v -> {
            list.remove(position);
            if (listView != null) UIUtil.setListViewHeightBasedOnItems(listView);
            if (listListener != null) {
                listListener.onListChangeListener(list.size());
            }

            notifyDataSetChanged();
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void setListListener(ListListener listListener) {
        this.listListener = listListener;
    }

    private static class ViewHolder {
        
        final TextView name;
        final ImageButton delete;

        public ViewHolder(View view) {
            this.name = view.findViewById(R.id.tvItemCategoryName);
            this.delete = view.findViewById(R.id.btnItemCategoryDelete);
        }
    }

    public interface ListListener {
        void onListChangeListener(int count);
    }
}
