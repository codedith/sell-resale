package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.LoginResponse;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.Utilities.DataValidations;
import com.waseet.waseetapp.Utilities.NetworkStatus;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText signIn_email, signIn_password;
    private Button signIn_signInButton, sign_up_loginpage;
    private TextView skip, forgot_password;
    private AppPreferencesShared appPreferencesShared;
    private Context mContext;
    private ImageView back_arrow;
    private TextView toolbar_title;
    private DataValidations validations;
    private LinearLayout or_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

      /*  Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        getInitUi();

        if (appPreferencesShared.getLogInDirection().equals("start")) {
            or_layout.setVisibility(View.VISIBLE);
            skip.setVisibility(View.VISIBLE);
        } else {
            or_layout.setVisibility(View.GONE);
            skip.setVisibility(View.GONE);
        }

        back_arrow.setVisibility(View.GONE);
        signIn_signInButton.setOnClickListener(this);
        forgot_password.setOnClickListener(this);
        sign_up_loginpage.setOnClickListener(this);
        skip.setOnClickListener(this);
        toolbar_title.setText(getResources().getText(R.string.login));

        validations = new DataValidations();

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCSrfToken();
        } else {
            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void getInitUi() {
        signIn_email = findViewById(R.id.signIn_email);
        signIn_password = findViewById(R.id.signIn_password);
        signIn_signInButton = findViewById(R.id.signIn_signInButton);
        sign_up_loginpage = findViewById(R.id.sign_up_loginpage);
        skip = findViewById(R.id.skip);
        forgot_password = findViewById(R.id.forgot_password);
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        or_layout = findViewById(R.id.or_layout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signIn_signInButton:
               /* Intent intent = new Intent(mContext, DashboardActivity.class);
                startActivity(intent);*/
                if (validations.isEmailValid(signIn_email.getText().toString(), signIn_email) &&
                        validations.isMandatory(signIn_password.getText().toString(), signIn_password)) {
                    if (NetworkStatus.isNetworkAvailable(mContext)) {
                        postUserLogin();
                    } else {
                        Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.forgot_password:
                Intent intent1 = new Intent(mContext, ForgotPasswordActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;

            case R.id.sign_up_loginpage:
                startActivity(new Intent(mContext, SignUpActivity.class));
                finish();
                break;

            case R.id.skip:
                appPreferencesShared.setLoginUserId("");
                appPreferencesShared.setLogInDirection("inside");
                startActivity(new Intent(mContext, DashboardActivity.class));
                finish();
                break;

        }
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

    private void postUserLogin() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<LoginResponse> loginResponseCall = apiInterface.postUserLogin(ConfigurationData.crsfTokenValue,
                signIn_email.getText().toString(), signIn_password.getText().toString());
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            appPreferencesShared.setLoginUserId(response.body().getUserData().getUserId());
                            if (appPreferencesShared.getLogInDirection().equals("start")) {
                                appPreferencesShared.setLogInDirection("inside");
                                Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext, DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                appPreferencesShared.setLogInDirection("inside");
                                finish();
                            }
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}