package com.waseet.waseetapp.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waseet.waseetapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdsListViewHolder extends RecyclerView.ViewHolder {

    public ImageView editsKey, deleteKey;
    public CircleImageView product_image;
    public TextView product_title, product_price, location, category;

    public AdsListViewHolder(@NonNull View itemView) {
        super(itemView);

        product_image = (CircleImageView) itemView.findViewById(R.id.product_image);
        editsKey = (ImageView) itemView.findViewById(R.id.editsKey);
        deleteKey = (ImageView) itemView.findViewById(R.id.deleteKey);
        product_title = (TextView) itemView.findViewById(R.id.product_title);
        product_price = (TextView) itemView.findViewById(R.id.product_price);
        location = (TextView) itemView.findViewById(R.id.location);
        category = (TextView) itemView.findViewById(R.id.category);

    }
}
