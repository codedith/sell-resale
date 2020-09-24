package com.waseet.waseetapp.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waseet.waseetapp.R;

public class RegionSelectViewHolder extends RecyclerView.ViewHolder {
    public ImageView region_image;
    public TextView region_text;
    public ImageView checked;
    public LinearLayout region_ll;

    public RegionSelectViewHolder(@NonNull View itemView) {
        super(itemView);
        region_image = itemView.findViewById(R.id.region_image);
        region_text = itemView.findViewById(R.id.region_text);
        checked = itemView.findViewById(R.id.checked);
        region_ll = itemView.findViewById(R.id.region_ll);

    }
}
