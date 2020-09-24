package com.waseet.waseetapp.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waseet.waseetapp.R;

public class CategoriesListViewHolder extends RecyclerView.ViewHolder {

    public ImageView categoryImage;
    public TextView CategoryName, numberOfCategory;

    public CategoriesListViewHolder(@NonNull View itemView) {
        super(itemView);

        categoryImage = (ImageView) itemView.findViewById(R.id.categoryImage);
        CategoryName = (TextView) itemView.findViewById(R.id.CategoryName);
        numberOfCategory = (TextView) itemView.findViewById(R.id.numberOfCategory);

    }
}
