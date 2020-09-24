package com.waseet.waseetapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waseet.waseetapp.Activities.PostListActivity;
import com.waseet.waseetapp.Models.SubCategory;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.ViewHolders.SubcategoryViewHolder;

import java.util.List;

public class SubcategoryAdapter extends RecyclerView.Adapter<SubcategoryViewHolder> {

    private Context mContext;
    private List<SubCategory> data;
    private AppPreferencesShared appPreferencesShared;

    public SubcategoryAdapter(Context mContext, List<SubCategory> data) {
        this.mContext = mContext;
        this.data = data;
        appPreferencesShared = new AppPreferencesShared(mContext);
    }

    @NonNull
    @Override
    public SubcategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.subcategory_layout, parent, false);
        SubcategoryViewHolder subcategoryViewHolder = new SubcategoryViewHolder(view);
        return subcategoryViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoryViewHolder holder, int position) {
        if (appPreferencesShared.getLocale().equals("ar")) {
            holder.subcategory.setText(data.get(position).getNameArabic()==null?"":data.get(position).getNameArabic().toString());
        }
        else {
            holder.subcategory.setText(data.get(position).getName());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigurationData.subCategoryData = data.get(position).getSlug();
                Intent intent = new Intent(mContext, PostListActivity.class);
                intent.putExtra("subCategoryName", ConfigurationData.subCategoryData);
                appPreferencesShared.setSubCategory(ConfigurationData.subCategoryData);
                appPreferencesShared.setSubCategoryName(data.get(position).getName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
