package com.waseet.waseetapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.waseet.waseetapp.Activities.ProductDetailActivity;
import com.waseet.waseetapp.Models.Favourites;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.ViewHolders.ProductListViewHolder;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class FavouriteListAdapter extends RecyclerView.Adapter<ProductListViewHolder> {

    private Context mContext;
    private List<Favourites> data;
    private AppPreferencesShared appPreferencesShared;

    public FavouriteListAdapter(Context mContext, List<Favourites> data) {
        this.mContext = mContext;
        this.data = data;
        appPreferencesShared = new AppPreferencesShared(mContext);

    }

    @NonNull
    @Override
    public ProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_list_card, parent, false);
        ProductListViewHolder productListViewHolder = new ProductListViewHolder(view);
        return productListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListViewHolder holder, int position) {
        ConfigurationData.productDetailsListId = data.get(position).getId();
        holder.product_title.setText(data.get(position).getTitle());
        holder.product_price.setText(data.get(position).getPrice());
        holder.location.setText(data.get(position).getLocation());
        holder.category.setText(data.get(position).getCategoryName());
        Picasso.with(mContext).load("http://theacademiz.com/classified/" + data.get(position).getImg1())
                .resize(200, 200).transform(new CropCircleTransformation())
                .placeholder(R.drawable.categories).into(holder.product_image);

        /*holder.like.setImageResource(like[position]);*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra("productDetailsListId", ConfigurationData.productDetailsListId);
                appPreferencesShared.setPosetdAdsId(data.get(position).getId());
                mContext.startActivity(intent);
//                mContext.startActivity(new Intent(mContext, ProductDetailActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
