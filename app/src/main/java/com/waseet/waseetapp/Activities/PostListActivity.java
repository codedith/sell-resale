package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.waseet.waseetapp.Adapters.ProductListAdapter;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.CategoryListResponse;
import com.waseet.waseetapp.Models.Product;
import com.waseet.waseetapp.Models.ProductListResponse;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.Utilities.NetworkStatus;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerviewProduct;
    private ImageView back_arrow;
    private TextView toolbar_title, plan_listNotFound;
    private FloatingActionButton filter;
    private Context mContext;
    private List<Product> data;
    private ProductListAdapter productListAdapter;
    private AppPreferencesShared appPreferencesShared;
    private CategoryListResponse categoryListResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

    /*    Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        getInitUi();
        back_arrow.setOnClickListener(this);
        toolbar_title.setText(appPreferencesShared.getSubCategoryName());
        filter.setOnClickListener(this);

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCSrfToken();
            getAllAddedCategoryList();
        } else {
            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
        }

        ConfigurationData.subCategoryData = getIntent().getStringExtra("subCategoryName");
    }

    private void getCSrfToken() {
        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CSRFTokenResponse> tokenRegisterLoginResponseCall = apiInterface.getCSrfToken();
        tokenRegisterLoginResponseCall.enqueue(new Callback<CSRFTokenResponse>() {
            @Override
            public void onResponse(Call<CSRFTokenResponse> call, Response<CSRFTokenResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            ConfigurationData.crsfTokenValue = response.body().getData().getCsrfTestName().toString();
                            appPreferencesShared.setCSrfToken(ConfigurationData.crsfTokenValue);
                        } else {
                            Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<CSRFTokenResponse> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void getAllAddedCategoryList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<ProductListResponse> responseCall = apiInterface.getAllAddedCategoryList(appPreferencesShared.getCategory(),
                appPreferencesShared.getSubCategory());
        responseCall.enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getData() != null) {
                            data = response.body().getData();

                            if (!response.body().getData().isEmpty()) {
                                recyclerviewProduct.setVisibility(View.VISIBLE);
                                plan_listNotFound.setVisibility(View.GONE);
                                setAllAddedCategoryListLayout();
                            } else {
                                recyclerviewProduct.setVisibility(View.GONE);
                                plan_listNotFound.setVisibility(View.VISIBLE);
                            }
                        } else {
                            recyclerviewProduct.setVisibility(View.GONE);
                            plan_listNotFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        recyclerviewProduct.setVisibility(View.GONE);
                        plan_listNotFound.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                recyclerviewProduct.setVisibility(View.GONE);
                plan_listNotFound.setVisibility(View.VISIBLE);

            }
        });
    }

    private void setAllAddedCategoryListLayout() {
        productListAdapter = new ProductListAdapter(mContext, data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerviewProduct.setLayoutManager(linearLayoutManager);
        recyclerviewProduct.setAdapter(productListAdapter);
        recyclerviewProduct.setHasFixedSize(true);

    }

    private void getInitUi() {
        recyclerviewProduct = findViewById(R.id.recyclerviewProduct);
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        plan_listNotFound = findViewById(R.id.plan_listNotFound);
        filter = findViewById(R.id.filter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

            case R.id.filter:
                startActivity(new Intent(mContext, FilterScreenActivity.class));
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (appPreferencesShared.getIsFilter()) {
            appPreferencesShared.setIsFilter(false);
            Gson gson = new Gson();
            ProductListResponse categoryListResponse = gson.fromJson(appPreferencesShared.getFilterDetailsList(), ProductListResponse.class);

            if (categoryListResponse.getData() != null) {
                data = categoryListResponse.getData();

                if (!categoryListResponse.getData().isEmpty()) {
                    recyclerviewProduct.setVisibility(View.VISIBLE);
                    plan_listNotFound.setVisibility(View.GONE);
                    setAllAddedCategoryListLayout();
                } else {
                    recyclerviewProduct.setVisibility(View.GONE);
                    plan_listNotFound.setVisibility(View.VISIBLE);
                }
            } else {
                recyclerviewProduct.setVisibility(View.GONE);
                plan_listNotFound.setVisibility(View.VISIBLE);
            }
        }
    }
}
