package com.bsit_three_c.dentalrecordapp.data.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Person;

public class ItemViewHolder extends RecyclerView.ViewHolder{

    final TextView name;
    final TextView text2;
    final TextView text3;
    final TextView text4;
    final ImageView imageView;
    final ImageView delete;
    final ImageView edit;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        this.name = itemView.findViewById(R.id.txtViewName);
        this.text2 = itemView.findViewById(R.id.txtView2);
        this.text3 = itemView.findViewById(R.id.txtView3);
        this.text4 = itemView.findViewById(R.id.txtView4);
        this.imageView = itemView.findViewById(R.id.imgViewPicture);
        this.delete = itemView.findViewById(R.id.btnPatientDelete);
        this.edit = itemView.findViewById(R.id.btnPatientEdit);
    }
    
    public interface ItemOnClickListener {
        void onItemClick(Person person);
    }
}
