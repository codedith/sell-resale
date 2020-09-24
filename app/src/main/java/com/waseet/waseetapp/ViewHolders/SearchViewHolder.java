package com.waseet.waseetapp.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waseet.waseetapp.R;

public class SearchViewHolder extends RecyclerView.ViewHolder {

    public ImageView product_image, like;
    public TextView product_title, product_price, location, category;

    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);

        product_image = (ImageView) itemView.findViewById(R.id.product_image);
        like = (ImageView) itemView.findViewById(R.id.like);
        product_title = (TextView) itemView.findViewById(R.id.product_title);
        product_price = (TextView) itemView.findViewById(R.id.product_price);
        location = (TextView) itemView.findViewById(R.id.location);
        category = (TextView) itemView.findViewById(R.id.category);
    }
}
