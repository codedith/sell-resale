package com.waseet.waseetapp.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.waseet.waseetapp.Activities.EditPostScreenActivity;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CommonResponse;
import com.waseet.waseetapp.Models.Product;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.Utilities.NetworkStatus;
import com.waseet.waseetapp.ViewHolders.AdsListViewHolder;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPostAdapter extends RecyclerView.Adapter<AdsListViewHolder> {

    private Context context;
    private List<Product> ads;
    private AppPreferencesShared appPreferencesShared;

    public MyPostAdapter(Context context, List<Product> ads) {
        this.context = context;
        this.ads = ads;
        appPreferencesShared = new AppPreferencesShared(context);

    }

    @NonNull
    @Override
    public AdsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ads_list_cards, parent, false);
        AdsListViewHolder adsListViewHolder = new AdsListViewHolder(view);
        return adsListViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull AdsListViewHolder holder, int position) {
        ConfigurationData.adsId = ads.get(position).getId();
        appPreferencesShared.setPosetdAdsId(ConfigurationData.adsId);
        holder.product_title.setText(ads.get(position).getTitle());
        holder.product_price.setText(ads.get(position).getPrice());
        holder.location.setText(ads.get(position).getLocation());
        holder.category.setText(ads.get(position).getCategoryName());
        Picasso.with(context).load("http://theacademiz.com/classified/" + ads.get(position).getImg1())
                .resize(200, 200).transform(new CropCircleTransformation())
                .placeholder(R.drawable.categories).into(holder.product_image);

        holder.editsKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigurationData.adsId = ads.get(position).getId();
                appPreferencesShared.setPosetdAdsId(ConfigurationData.adsId);
                Intent intent = new Intent(context, EditPostScreenActivity.class);
                intent.putExtra("adsId", ConfigurationData.adsId);
                context.startActivity(intent);
            }
        });

        holder.deleteKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigurationData.adsId = ads.get(position).getId();
                if (NetworkStatus.isNetworkAvailable(context)) {
                    postAdsDelete(appPreferencesShared.getCSrfToken(), appPreferencesShared.getLoginUserId(), ConfigurationData.adsId);
                } else {
                    Toast.makeText(context, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void postAdsDelete(String cSrfToken, String loginUserId, String adsId) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CommonResponse> responseCall = apiInterface.postAdsDelete(cSrfToken, loginUserId, adsId);
        responseCall.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            Toast.makeText(context, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(context, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(context, "Something went wrong !", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }
}
