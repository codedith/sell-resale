package com.waseet.waseetapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.CommonResponse;
import com.waseet.waseetapp.Models.UserInfoResponse;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.Utilities.DataValidations;
import com.waseet.waseetapp.Utilities.NetworkStatus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.waseet.waseetapp.Utilities.ConfigurationData.IMAGE_DIRECTORY;

public class UpdateUserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private ImageView back_arrow, userProfile_update;
    private EditText firstName_update, lastName_update, email_update, phone_update, address_update;
    private Button submit_UpdateButton;
    private TextView toolbar_title;
    private DataValidations validations;
    private UserInfoResponse registerLoginResponse;

    private String[] appPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private int PERMISSIONS_REQUEST_CODE = 1024, GALLERY = 0, CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);

        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);
       /* Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/
        validations = new DataValidations();
        getInitUILayout();

        back_arrow.setOnClickListener(this);
        submit_UpdateButton.setOnClickListener(this);
        userProfile_update.setOnClickListener(this);

        Gson gsonUserProfile = new Gson();
        ConfigurationData.userProfileDetails = appPreferencesShared.getUserProfileDetails();
        registerLoginResponse = gsonUserProfile.fromJson(ConfigurationData.userProfileDetails, UserInfoResponse.class);

        if (appPreferencesShared.getIsEditUserProfile()) {
            toolbar_title.setText(getResources().getString(R.string.update_user_profile));
            firstName_update.setText(registerLoginResponse.getUserInfo().getFirstname());
            lastName_update.setText(registerLoginResponse.getUserInfo().getLastname());
            email_update.setText(registerLoginResponse.getUserInfo().getEmail());
            phone_update.setText(registerLoginResponse.getUserInfo().getContact());
            address_update.setText(registerLoginResponse.getUserInfo().getAddress());
            Picasso.with(mContext).load("http://theacademiz.com/classified/" + registerLoginResponse.getUserInfo().getProfilePicture())
                    .resize(250, 250).transform(new CropCircleTransformation())
                    .placeholder(R.drawable.user_icon).into(userProfile_update);

        } else {
            toolbar_title.setText(R.string.update_user_profile);
        }

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCSrfToken();
            postUserProfileData();
        } else {
            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
        }

        checkAndRequestPermissions();
    }

    private void getCSrfToken() {
        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CSRFTokenResponse> tokenRegisterLoginResponseCall = apiInterface.getCSrfToken();
        tokenRegisterLoginResponseCall.enqueue(new Callback<CSRFTokenResponse>() {
            @Override
            public void onResponse(Call<CSRFTokenResponse> call, Response<CSRFTokenResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        ConfigurationData.crsfTokenValue = response.body().getData().getCsrfTestName();
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

    private void getInitUILayout() {
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        back_arrow = (ImageView) findViewById(R.id.back_arrow);
        userProfile_update = (ImageView) findViewById(R.id.userProfile_update);
        firstName_update = (EditText) findViewById(R.id.firstName_update);
        lastName_update = (EditText) findViewById(R.id.lastName_update);
        email_update = (EditText) findViewById(R.id.email_update);
        phone_update = (EditText) findViewById(R.id.phone_update);
        address_update = (EditText) findViewById(R.id.address_update);
        submit_UpdateButton = (Button) findViewById(R.id.submit_UpdateButton);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

            case R.id.userProfile_update:
                showUploadDialogue();
                break;

            case R.id.submit_UpdateButton:
                if (validations.isMandatory(firstName_update.getText().toString(), firstName_update) &&
                        validations.isMandatory(lastName_update.getText().toString(), lastName_update) &&
                        validations.isEmailValid(email_update.getText().toString(), email_update) &&
                        validations.isMandatory(phone_update.getText().toString(), phone_update) &&
                        validations.isMandatory(address_update.getText().toString(), address_update)) {
                    if (NetworkStatus.isNetworkAvailable(mContext)) {
                        postUpdateUserData();
                    } else {
                        Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    private void showUploadDialogue() {
        String[] items = {getString(R.string.select_phto_gallery), getString(R.string.select_photo_camera)};
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(R.string.select_action)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                galleryIntent.setType("image/*");
                                startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.select_image)), GALLERY);
                                break;

                            case 1:
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA);
                                break;
                        }
                    }
                });
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    Uri contentURI = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), contentURI);
                        userProfile_update.setImageBitmap(bitmap);
                        ConfigurationData.uploadedImage = saveImage(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                userProfile_update.setImageBitmap(thumbnail);
                ConfigurationData.uploadedImage = saveImage(thumbnail);
            }
        }
    }

    private String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this, new String[]{f.getPath()}, new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void postUpdateUserData() {
        if (ConfigurationData.uploadedImage == null || ConfigurationData.uploadedImage.isEmpty()) {
            Toast.makeText(mContext, R.string.please_upload_an_image, Toast.LENGTH_SHORT).show();
            return;
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();

            File file = new File(ConfigurationData.uploadedImage);
            RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part userProfileImage = MultipartBody.Part.createFormData("profile_picture", file.getName(), requestFile);

            RequestBody crsfToken = RequestBody.create(MediaType.parse("text/plain"), ConfigurationData.crsfTokenValue);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getLoginUserId());
            RequestBody firstName = RequestBody.create(MediaType.parse("text/plain"), firstName_update.getText().toString());
            RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), lastName_update.getText().toString());
            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), email_update.getText().toString());
            RequestBody contact = RequestBody.create(MediaType.parse("text/plain"), phone_update.getText().toString());
            RequestBody address = RequestBody.create(MediaType.parse("text/plain"), address_update.getText().toString());
            RequestBody country = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getCountryId());
            RequestBody state = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getStateId());
            RequestBody city = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getCityId());

            ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
            Call<CommonResponse> loginResponseCall = apiInterface.postUpdateUserData(crsfToken, userId, userProfileImage,
                    firstName, lastName, email, contact, address, country, state, city);
            loginResponseCall.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    try {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess() == true) {
                                appPreferencesShared.setIsEditUserProfile(true);
                                Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
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

    private Boolean checkAndRequestPermissions() {
        ArrayList<String> listPermissionsNeeded = new ArrayList<String>();
        String[] permissions = new String[0];
        for (int i = 0; i < appPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(mContext, appPermissions[i].toString()) != PackageManager.PERMISSION_GRANTED)
                listPermissionsNeeded.add(appPermissions[i].toString());
            permissions = new String[listPermissionsNeeded.size()];
            for (int j = 0; j < listPermissionsNeeded.size(); j++) {
                permissions[j] = listPermissionsNeeded.get(j);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(((Activity) mContext), permissions, PERMISSIONS_REQUEST_CODE);
            return false;
        }

        return true;
    }

    private void postUserProfileData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<UserInfoResponse> responseCall = apiInterface.postUserProfileData(appPreferencesShared.getCSrfToken(),
                appPreferencesShared.getLoginUserId());
        responseCall.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            firstName_update.setText(response.body().getUserInfo().getFirstname());
                            lastName_update.setText(response.body().getUserInfo().getLastname());
                            email_update.setText(response.body().getUserInfo().getEmail());
                            phone_update.setText(response.body().getUserInfo().getContact());
                            address_update.setText(response.body().getUserInfo().getAddress());
                            Picasso.with(mContext).load("http://theacademiz.com/classified/" + response.body().getUserInfo().getProfilePicture())
                                    .resize(250, 250).transform(new CropCircleTransformation())
                                    .placeholder(R.drawable.user_icon).into(userProfile_update);
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
//                            Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(mContext, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
