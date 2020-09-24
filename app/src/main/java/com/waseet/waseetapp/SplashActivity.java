package com.waseet.waseetapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.waseet.waseetapp.Activities.DashboardActivity;
import com.waseet.waseetapp.Activities.LanguageAndRegionActivity;
import com.waseet.waseetapp.Activities.LoginActivity;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.Utilities.NetworkStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private Context mContext;
    private Animation animation;
    private ImageView imageFirst, imageSecond, imageThird;
    private AppPreferencesShared appPreferencesShared;
    Handler mDelayHandler;
    int SPLASH_DELAY = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);
        getIUInitiateLayout();

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCSrfToken();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
        }

        animation = AnimationUtils.loadAnimation(mContext, R.anim.push_down);
        imageFirst.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(mContext, R.anim.push_right);
        imageSecond.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(mContext, R.anim.push_up);
        imageThird.setAnimation(animation);

        mDelayHandler = new Handler();
        mDelayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (appPreferencesShared.getIsLanguageSelected() && appPreferencesShared.getLogInDirection().equals("inside")) {
                    intent = new Intent(mContext, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else if (!appPreferencesShared.getIsLanguageSelected() && appPreferencesShared.getLogInDirection().equals("start")) {
                    intent = new Intent(mContext, LanguageAndRegionActivity.class);
                    startActivity(intent);
                    finish();
                } else if (appPreferencesShared.getIsLanguageSelected() && appPreferencesShared.getLogInDirection().equals("start")) {
                    intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_DELAY);
    }

    private void getCSrfToken() {
        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CSRFTokenResponse> call = apiInterface.getCSrfToken();
        call.enqueue(new Callback<CSRFTokenResponse>() {
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
                        Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
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

    private void getIUInitiateLayout() {
        imageFirst = (ImageView) findViewById(R.id.imageFirst);
        imageSecond = (ImageView) findViewById(R.id.imageSecond);
        imageThird = (ImageView) findViewById(R.id.imageThird);

    }
}
