package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bsit_three_c.dentalrecordapp.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends ArrayAdapter {

    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<String> categories;

    public CategoryAdapter(@NonNull Context context, int resource, List<String> categories) {
        super(context, resource);

        this.layoutInflater = LayoutInflater.from(context);
        this.layoutResource = resource;
        this.categories = categories;
    }

    public CategoryAdapter(@NonNull Context context, int resource) {
        this(context, resource, new ArrayList<>());
    }

    public void add(String object) {
        categories.add(object);
    }

    public void addAll(List<String> items) {
        categories.clear();
        categories.addAll(items);
    }

    public void clear() {
        categories.clear();
    }

    public void remove(String category) {
        categories.remove(category);
    }

    public List<String> getCategories() {
        return categories;
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

        viewHolder.name.setText(categories.get(position));
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categories.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    private static class ViewHolder {
        
        final TextView name;
        final ImageButton delete;

        public ViewHolder(View view) {
            this.name = view.findViewById(R.id.tvItemCategoryName);
            this.delete = view.findViewById(R.id.btnItemCategoryDelete);
        }
    }
}
