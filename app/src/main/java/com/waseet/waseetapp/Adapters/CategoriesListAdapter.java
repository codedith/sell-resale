package com.waseet.waseetapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.waseet.waseetapp.Activities.SubCategoryScreenActivity;
import com.waseet.waseetapp.Models.Category;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.ViewHolders.CategoriesListViewHolder;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListViewHolder> {

    private Context mContext;
    private List<Category> data;
    private Category categoryList;
    private AppPreferencesShared appPreferencesShared;

    public CategoriesListAdapter(Context mContext, List<Category> data) {
        this.mContext = mContext;
        this.data = data;
        appPreferencesShared = new AppPreferencesShared(mContext);
    }

    @NonNull
    @Override
    public CategoriesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dashboard_category_item, parent, false);
        CategoriesListViewHolder categoriesListViewHolder = new CategoriesListViewHolder(view);
        return categoriesListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesListViewHolder holder, int position) {
        if (appPreferencesShared.getLocale().equals("ar")) {
            holder.CategoryName.setText(data.get(position).getNameAb());
            holder.numberOfCategory.setText(data.get(position).getDescriptionAb());
        }
        else {
            holder.CategoryName.setText(data.get(position).getName());
            holder.numberOfCategory.setText(data.get(position).getDescription());
        }
        Picasso.with(mContext).load("http://theacademiz.com/classified/" + data.get(position).getPicture())
                .resize(100, 100).transform(new CropCircleTransformation())
                .placeholder(R.drawable.categories).into(holder.categoryImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigurationData.slugData = data.get(position).getSlug();
                Intent intent = new Intent(mContext, SubCategoryScreenActivity.class);
                intent.putExtra("slug", ConfigurationData.slugData);
                appPreferencesShared.setCategory(ConfigurationData.slugData);
                appPreferencesShared.setMainCategoryName(data.get(position).getName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}