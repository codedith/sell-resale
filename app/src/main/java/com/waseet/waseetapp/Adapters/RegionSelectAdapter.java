package com.waseet.waseetapp.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waseet.waseetapp.R;
import com.waseet.waseetapp.ViewHolders.RegionSelectViewHolder;

public class RegionSelectAdapter extends RecyclerView.Adapter<RegionSelectViewHolder> {
    Context context;
    private String[] region_text;
    private Integer[] region_image;

    public RegionSelectAdapter(Context context, String[] region_text, Integer[] region_image) {
        this.context = context;
        this.region_text = region_text;
        this.region_image = region_image;
    }

    @NonNull
    @Override
    public RegionSelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.region_card, parent, false);
        return new RegionSelectViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RegionSelectViewHolder holder, int position) {
//       holder.region_image.setImageResource(region_image[position]);
//        holder.region_text.setText(region_text[position]);
//        if (holder.getPosition() == 0) {
//            holder.checked.setImageResource(R.drawable.checked);
//        } else {
//            holder.checked.setImageResource(R.drawable.unchecked);
//        }
        holder.region_text.setText(region_text[position]);
        holder.region_image.setImageResource(region_image[position]);


    }

    @Override
    public int getItemCount() {
        return region_text.length;
    }
}
