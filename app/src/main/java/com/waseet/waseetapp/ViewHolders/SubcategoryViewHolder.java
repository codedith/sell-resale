package com.waseet.waseetapp.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waseet.waseetapp.R;

public class SubcategoryViewHolder extends RecyclerView.ViewHolder {

    public TextView subcategory;

    public SubcategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        subcategory = (TextView) itemView.findViewById(R.id.subcategory);

    }
}
