package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.waseet.waseetapp.Adapters.FavouriteListAdapter;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.CategoryListResponse;
import com.waseet.waseetapp.Models.Favourites;
import com.waseet.waseetapp.Models.FavouritesListResponse;
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

public class MyFavourateActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private RecyclerView recyclerviewFavourity;
    private ImageView back_arrow;
    private TextView toolbar_title, plan_listNotFound;
    private List<Favourites> data;
    RelativeLayout logged_in_layout;
    LinearLayout without_login_layout;
    Button login_button;
    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favourate);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

        /*Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        getInitUi();
        setupSwipeRefresh();
        back_arrow.setOnClickListener(this);
        login_button.setOnClickListener(this);
        toolbar_title.setText(R.string.my_favorites);

    }

    private void setupSwipeRefresh() {
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CountDownTimer(1000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        refresh.setRefreshing(false);
                    }
                }.start();
            }
        });
    }

    private void getInitUi() {
        recyclerviewFavourity = findViewById(R.id.recyclerviewFavourity);
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        plan_listNotFound = findViewById(R.id.plan_listNotFound);
        logged_in_layout = findViewById(R.id.logged_in_layout);
        without_login_layout = findViewById(R.id.without_login_layout);
        login_button = findViewById(R.id.login_button);
        refresh = findViewById(R.id.refresh);
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

    private void postMyFavouriteAds() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<FavouritesListResponse> categoryListResponseCall = apiInterface.postMyFavouriteAds(ConfigurationData.crsfTokenValue,
                appPreferencesShared.getLoginUserId());
        categoryListResponseCall.enqueue(new Callback<FavouritesListResponse>() {
            @Override
            public void onResponse(Call<FavouritesListResponse> call, Response<FavouritesListResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess() == true) {
                            data = response.body().getData();

                            recyclerviewFavourity.setVisibility(View.VISIBLE);
                            plan_listNotFound.setVisibility(View.GONE);
                            setLayoutofCategoryFavourityList();

                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            recyclerviewFavourity.setVisibility(View.GONE);
                            plan_listNotFound.setVisibility(View.VISIBLE);
                            Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        recyclerviewFavourity.setVisibility(View.GONE);
                        plan_listNotFound.setVisibility(View.VISIBLE);
                        Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<FavouritesListResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                recyclerviewFavourity.setVisibility(View.GONE);
                plan_listNotFound.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLayoutofCategoryFavourityList() {
        FavouriteListAdapter adapter = new FavouriteListAdapter(mContext, data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerviewFavourity.setLayoutManager(linearLayoutManager);
        recyclerviewFavourity.setAdapter(adapter);
        recyclerviewFavourity.setHasFixedSize(true);

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
                postMyFavouriteAds();
            } else {
                Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
