package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.waseet.waseetapp.Adapters.SubcategoryAdapter;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CategoryListResponse;
import com.waseet.waseetapp.Models.SubCategory;
import com.waseet.waseetapp.Models.SubCategoryListResponse;
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

public class SubCategoryScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back_arrow;
    private TextView toolbar_title, plan_listNotFound;
    private RecyclerView recyclerview;

    private Context mContext;
    private List<SubCategory> data;
    private SubcategoryAdapter subcategoryAdapter;
    private AppPreferencesShared appPreferencesShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_screen);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

       /* Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        getInitUi();

        back_arrow.setOnClickListener(this);
        toolbar_title.setText(appPreferencesShared.getMainCategoryName());

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getAllSubcategoriesList();
        } else {
            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
        }

        ConfigurationData.slugData = getIntent().getStringExtra("slug");

    }

    private void getAllSubcategoriesList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<SubCategoryListResponse> listResponseCall = apiInterface.getAllSubcategoriesList(ConfigurationData.slugData);
        listResponseCall.enqueue(new Callback<SubCategoryListResponse>() {
            @Override
            public void onResponse(Call<SubCategoryListResponse> call, Response<SubCategoryListResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getData() != null) {
                            data = response.body().getData();
                            recyclerview.setVisibility(View.VISIBLE);
                            plan_listNotFound.setVisibility(View.GONE);
                            setAllSubCategoryListLayout();
                        } else {
                            recyclerview.setVisibility(View.GONE);
                            plan_listNotFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        recyclerview.setVisibility(View.GONE);
                        plan_listNotFound.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<SubCategoryListResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                recyclerview.setVisibility(View.GONE);
                plan_listNotFound.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setAllSubCategoryListLayout() {
        subcategoryAdapter = new SubcategoryAdapter(mContext, data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(linearLayoutManager);
        recyclerview.setAdapter(subcategoryAdapter);
        recyclerview.setHasFixedSize(true);

    }

    private void getInitUi() {
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        plan_listNotFound = findViewById(R.id.plan_listNotFound);
        recyclerview = findViewById(R.id.recyclerview);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;
        }
    }
}
