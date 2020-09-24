package com.waseet.waseetapp.ViewHolders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.waseet.waseetapp.R;

public class GridViewHolder extends RecyclerView.ViewHolder {

    public ImageView image, deleteAdsImage;
    public CardView adsImageId;

    public GridViewHolder(@NonNull View itemView) {
        super(itemView);

        adsImageId = (CardView) itemView.findViewById(R.id.adsImageId);
        image = (ImageView) itemView.findViewById(R.id.image);
        deleteAdsImage = (ImageView) itemView.findViewById(R.id.deleteAdsImage);
    }
}
