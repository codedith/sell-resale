package com.waseet.waseetapp.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.CommonResponse;
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

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back_arrow;
    private TextView toolbar_title;
    private EditText oldpassword_changepassword, newpassword_changepassword, confirmpassword_changepassword;
    private Button changepassword_button;
    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private DataValidations validations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);
     /*   Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        validations = new DataValidations();

        getInitUi();
        back_arrow.setOnClickListener(this);
        changepassword_button.setOnClickListener(this);
        toolbar_title.setText(R.string.change_password);

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCSrfToken();
        } else {
            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
        }

    }

    private void getInitUi() {
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        oldpassword_changepassword = findViewById(R.id.oldpassword_changepassword);
        newpassword_changepassword = findViewById(R.id.newpassword_changepassword);
        confirmpassword_changepassword = findViewById(R.id.confirmpassword_changepassword);
        changepassword_button = findViewById(R.id.changepassword_button);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

            case R.id.changepassword_button:
//                startActivity(new Intent(mContext, LoginActivity.class));
                if (validations.isMandatory(oldpassword_changepassword.getText().toString(), oldpassword_changepassword) &&
                        validations.isMandatory(newpassword_changepassword.getText().toString(), newpassword_changepassword) &&
                        validations.isMandatory(confirmpassword_changepassword.getText().toString(), confirmpassword_changepassword)) {
                    if (NetworkStatus.isNetworkAvailable(mContext)) {
                        postUserChangePassword();
                    } else {
                        Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                    }
                }
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

    private void postUserChangePassword() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CommonResponse> call = apiInterface.postChangePassword(appPreferencesShared.getLoginUserId(),
                ConfigurationData.crsfTokenValue, oldpassword_changepassword.getText().toString(),
                newpassword_changepassword.getText().toString(), confirmpassword_changepassword.getText().toString());
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(mContext, LoginActivity.class));
                            finish();
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
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}