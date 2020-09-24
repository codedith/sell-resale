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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText fullname_signup, email_signup, phone_signup, password_signup, confipassword_signUp;
    Button signUp_signUpButton, login_signuppage;
    private ImageView back_arrow;
    private TextView toolbar_title;
    private Context mContext;
    private DataValidations validations;
    private CheckBox check_termCondition;
    private boolean checked = true;
    private AppPreferencesShared appPreferencesShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
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
        login_signuppage.setOnClickListener(this);
        signUp_signUpButton.setOnClickListener(this);

        toolbar_title.setText(R.string.sign_up);
        validations = new DataValidations();

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCSrfToken();
        } else {
            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
        }

        check_termCondition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checked = true;
                } else {
                    checked = false;
                }
            }
        });
    }

    private void getInitUi() {
        fullname_signup = findViewById(R.id.fullname_signup);
        email_signup = findViewById(R.id.email_signup);
        phone_signup = findViewById(R.id.phone_signup);
        password_signup = findViewById(R.id.password_signup);
        confipassword_signUp = findViewById(R.id.confipassword_signUp);
        signUp_signUpButton = findViewById(R.id.signUp_signUpButton);
        login_signuppage = findViewById(R.id.login_signuppage);
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        check_termCondition = (CheckBox) findViewById(R.id.check_termCondition);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

            case R.id.signUp_signUpButton:
                if (validations.isFullNameValid(fullname_signup.getText().toString(), fullname_signup) &&
                        validations.isEmailValid(email_signup.getText().toString(), email_signup) &&
                        validations.isMobileValid(phone_signup.getText().toString(), phone_signup) &&
                        validations.isMandatory(password_signup.getText().toString(), password_signup) &&
                        validations.isMandatory(confipassword_signUp.getText().toString(), confipassword_signUp) &&
                        checked) {
                    if (NetworkStatus.isNetworkAvailable(mContext)) {
                        postNewUserRegistration();
                    } else {
                        Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.login_signuppage:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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

    private void postNewUserRegistration() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CommonResponse> loginResponseCall = apiInterface.postUserRegistration(fullname_signup.getText().toString(),
                ConfigurationData.crsfTokenValue, email_signup.getText().toString(), phone_signup.getText().toString(),
                appPreferencesShared.getCountryId(), password_signup.getText().toString(), confipassword_signUp.getText().toString(),
                checked);
        loginResponseCall.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(mContext, LoginActivity.class));
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