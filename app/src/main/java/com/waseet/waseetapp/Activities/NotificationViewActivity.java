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

import com.waseet.waseetapp.Adapters.NotificationViewAdapter;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.Notification;
import com.waseet.waseetapp.Models.NotificationListResponse;
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

public class NotificationViewActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private RecyclerView recyclerOfNotification;
    private ImageView back_arrow;
    private TextView toolbar_title, notice_listNotFound;
    private NotificationViewAdapter notificationViewAdapter;
    private List<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

       /* Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        getIUIdInitLayout();

        back_arrow.setOnClickListener(this);
        toolbar_title.setText(getResources().getString(R.string.notifications));

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCSrfToken();
            postUserNotificationApi();
        } else {
            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void getIUIdInitLayout() {
        back_arrow = (ImageView) findViewById(R.id.back_arrow);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        recyclerOfNotification = (RecyclerView) findViewById(R.id.recyclerOfNotification);
        notice_listNotFound = (TextView) findViewById(R.id.notice_listNotFound);

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

    private void postUserNotificationApi() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<NotificationListResponse> responseCall = apiInterface.postUserNotificationApi(ConfigurationData.crsfTokenValue,
                /*appPreferencesShared.getLoginUserId()*/"45");
        responseCall.enqueue(new Callback<NotificationListResponse>() {
            @Override
            public void onResponse(Call<NotificationListResponse> call, Response<NotificationListResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            notifications = response.body().getNotifications();
                            recyclerOfNotification.setVisibility(View.VISIBLE);
                            notice_listNotFound.setVisibility(View.GONE);
                            setNotificationLayout();
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
//                            Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                            recyclerOfNotification.setVisibility(View.GONE);
                            notice_listNotFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        recyclerOfNotification.setVisibility(View.GONE);
                        notice_listNotFound.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<NotificationListResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                recyclerOfNotification.setVisibility(View.GONE);
                notice_listNotFound.setVisibility(View.VISIBLE);

            }
        });
    }

    private void setNotificationLayout() {
        notificationViewAdapter = new NotificationViewAdapter(mContext, notifications);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerOfNotification.setLayoutManager(linearLayoutManager);
        recyclerOfNotification.setAdapter(notificationViewAdapter);
        recyclerOfNotification.setHasFixedSize(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

        }
    }
}
