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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.waseet.waseetapp.Adapters.MyPostAdapter;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.MyAdsResponse;
import com.waseet.waseetapp.Models.Product;
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

public class MyPostActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private RecyclerView recyclerviewofAds;
    private ImageView back_arrow;
    private TextView toolbar_title, ads_listNotFound;
    private List<Product> ads;
    private MyPostAdapter adapter;
    RelativeLayout logged_in_layout;
    LinearLayout without_login_layout;
    Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

       /* Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/
        getInitui();
        back_arrow.setOnClickListener(this);
        login_button.setOnClickListener(this);
        toolbar_title.setText(R.string.my_posts);

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
                        appPreferencesShared.setCSrfToken(ConfigurationData.crsfTokenValue);

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

    private void postMyAds() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<MyAdsResponse> responseCall = apiInterface.postMyAds(ConfigurationData.crsfTokenValue,
                appPreferencesShared.getLoginUserId());
        responseCall.enqueue(new Callback<MyAdsResponse>() {
            @Override
            public void onResponse(Call<MyAdsResponse> call, Response<MyAdsResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            ads = response.body().getAds();

                            if (ads.isEmpty()) {
                                recyclerviewofAds.setVisibility(View.GONE);
                                ads_listNotFound.setVisibility(View.VISIBLE);
                            }
                            else {
                                recyclerviewofAds.setVisibility(View.VISIBLE);
                                ads_listNotFound.setVisibility(View.GONE);
                                setAdsLayout();
                            }
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
//                            Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                            recyclerviewofAds.setVisibility(View.GONE);
                            ads_listNotFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        recyclerviewofAds.setVisibility(View.GONE);
                        ads_listNotFound.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<MyAdsResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                recyclerviewofAds.setVisibility(View.GONE);
                ads_listNotFound.setVisibility(View.VISIBLE);

            }
        });
    }

    private void setAdsLayout() {
        adapter = new MyPostAdapter(mContext, ads);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerviewofAds.setLayoutManager(linearLayoutManager);
        recyclerviewofAds.setAdapter(adapter);
        recyclerviewofAds.setHasFixedSize(true);

    }

    private void getInitui() {
        recyclerviewofAds = findViewById(R.id.recyclerviewofAds);
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        ads_listNotFound = findViewById(R.id.ads_listNotFound);
        logged_in_layout = findViewById(R.id.logged_in_layout);
        without_login_layout = findViewById(R.id.without_login_layout);
        login_button = findViewById(R.id.login_button);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

            case R.id.login_button:
                appPreferencesShared.setLogInDirection("inside");
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appPreferencesShared.getLoginUserId().isEmpty()) {
            logged_in_layout.setVisibility(View.GONE);
            without_login_layout.setVisibility(View.VISIBLE);
        } else {
            logged_in_layout.setVisibility(View.VISIBLE);
            without_login_layout.setVisibility(View.GONE);
            if (NetworkStatus.isNetworkAvailable(mContext)) {
                getCSrfToken();
                postMyAds();
            } else {
                Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
