package com.waseet.waseetapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waseet.waseetapp.R;
import com.waseet.waseetapp.ViewHolders.GridViewHolder;

public class EditPostAdapter extends RecyclerView.Adapter<GridViewHolder> {
    Context context;
    Integer[] image;

    public EditPostAdapter(Context context, Integer[] image) {
        this.context = context;
        this.image = image;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_card, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return image.length;
    }
}
