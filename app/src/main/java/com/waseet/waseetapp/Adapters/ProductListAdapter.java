package com.waseet.waseetapp.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.waseet.waseetapp.Activities.LoginActivity;
import com.waseet.waseetapp.Activities.ProductDetailActivity;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.CommonResponse;
import com.waseet.waseetapp.Models.Product;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.Utilities.NetworkStatus;
import com.waseet.waseetapp.ViewHolders.ProductListViewHolder;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListViewHolder> {

    private Context mContext;
    private List<Product> data;
    private AppPreferencesShared appPreferencesShared;

    public ProductListAdapter(Context mContext, List<Product> data) {
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
        holder.product_price.setText(data.get(position).getPrice().toString());
        holder.location.setText(data.get(position).getLocation());
        holder.category.setText(data.get(position).getCategoryName());
        Picasso.with(mContext).load("http://theacademiz.com/classified/" + data.get(position).getImg1())
                .resize(200, 200).transform(new CropCircleTransformation())
                .placeholder(R.drawable.categories).into(holder.product_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProductDetailActivity.class);
                intent.putExtra("productDetailsListId", ConfigurationData.productDetailsListId);
                appPreferencesShared.setPosetdAdsId(data.get(position).getId());
                ;
                mContext.startActivity(intent);

            }
        });

        //this code is for like image
        /*if (data.get(position).getIsStatus()) {
            SavedLayout(true, holder, position);
        } else {
            SavedLayout(false, holder, position);
        }*/

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appPreferencesShared.getLoginUserId().isEmpty()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setMessage(R.string.please_Login);
                    alertDialogBuilder.setPositiveButton(R.string.login,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    appPreferencesShared.setLogInDirection("inside");
                                    Intent intent = new Intent(mContext, LoginActivity.class);
                                    mContext.startActivity(intent);
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FAF8F8")));

                } else {
                    if (NetworkStatus.isNetworkAvailable(mContext)) {
                        getCSrfToken();
                        postAddFavoriteApi();
                    } else {
                        Toast.makeText(mContext, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void getCSrfToken() {
        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CSRFTokenResponse> tokenRegisterLoginResponseCall = apiInterface.getCSrfToken();
        tokenRegisterLoginResponseCall.enqueue(new Callback<CSRFTokenResponse>() {
            @Override
            public void onResponse(Call<CSRFTokenResponse> call, Response<CSRFTokenResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        ConfigurationData.crsfTokenValue = response.body().getData().getCsrfTestName().toString();
                    } else {
                        Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<CSRFTokenResponse> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postAddFavoriteApi() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CommonResponse> loginResponseCall = apiInterface.postAddFavoriteApi(ConfigurationData.crsfTokenValue,
                appPreferencesShared.getLoginUserId(), ConfigurationData.productDetailsListId);
        loginResponseCall.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            Intent intent = new Intent("custom-message");
                            intent.putExtra("response", true);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent("custom-message");
                            intent.putExtra("response", true);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(mContext, "Could not save it due to network issue", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(mContext, "Could not save it due to network issue", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SavedLayout(boolean saved, ProductListViewHolder holder, int positon) {
        if (saved) {
            holder.like.setImageResource(R.drawable.filledheart);
        } else {
            holder.like.setImageResource(R.drawable.emptyheart);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
