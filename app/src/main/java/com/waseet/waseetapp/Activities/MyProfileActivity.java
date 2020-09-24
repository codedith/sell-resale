package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.waseet.waseetapp.Models.UserInfoResponse;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;

import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back_arrow, edit_key;
    private TextView toolbar_title;
    private ImageView profile_image;
    private TextView user_name, user_email, user_phone_number;
    private Button changepassword;
    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private UserInfoResponse registerLoginResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

      /*  Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
*/
        getInitUi();
        back_arrow.setOnClickListener(this);
        edit_key.setVisibility(View.VISIBLE);
        edit_key.setOnClickListener(this);
        changepassword.setOnClickListener(this);
        toolbar_title.setText(R.string.my_profile);


        Gson gsonUserProfile = new Gson();
        ConfigurationData.userProfileDetails = appPreferencesShared.getUserProfileDetails();
        registerLoginResponse = gsonUserProfile.fromJson(ConfigurationData.userProfileDetails, UserInfoResponse.class);

        user_name.setText(registerLoginResponse.getUserInfo().getUsername());
        user_email.setText(registerLoginResponse.getUserInfo().getEmail());
        user_phone_number.setText(registerLoginResponse.getUserInfo().getContact());
        Picasso.with(mContext).load("http://theacademiz.com/classified/" + registerLoginResponse.getUserInfo().getProfilePicture())
                .resize(200, 200).transform(new CropCircleTransformation())
                .placeholder(R.drawable.user_icon).into(profile_image);

    }

    private void getInitUi() {
        back_arrow = findViewById(R.id.back_arrow);
        edit_key = findViewById(R.id.edit_key);
        toolbar_title = findViewById(R.id.toolbar_title);
        profile_image = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.user_email);
        user_phone_number = findViewById(R.id.user_phone_number);
        changepassword = findViewById(R.id.changepassword);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

            case R.id.changepassword:
                startActivity(new Intent(mContext, ChangePasswordActivity.class));
                break;

            case R.id.edit_key:
                startActivity(new Intent(mContext, UpdateUserProfileActivity.class));
                break;

        }
    }
}