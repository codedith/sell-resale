package com.waseet.waseetapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.waseet.waseetapp.Adapters.GridAdapter;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.CommonResponse;
import com.waseet.waseetapp.Models.ImageList;
import com.waseet.waseetapp.Models.PostedImageResponse;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.Utilities.NetworkStatus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.waseet.waseetapp.Utilities.ConfigurationData.IMAGE_DIRECTORY;

public class MoreImageUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private ImageView back_arrow, uploaded_image;
    private CardView upload_image_card;
    private TextView toolbar_title, no_List;
    private Button postImage;
    private RecyclerView recycler_view;
    private GridAdapter gridAdapter;
    private List<ImageList> data;

    private String[] appPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private int PERMISSIONS_REQUEST_CODE = 1024, GALLERY = 0, CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_image_upload);
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
        upload_image_card.setOnClickListener(this);
        postImage.setOnClickListener(this);
        toolbar_title.setText("Image Upload");

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCSrfToken();
            getPostedAdsImages();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
        }

        checkAndRequestPermissions();
    }

    private void getInitUi() {
        back_arrow = (ImageView) findViewById(R.id.back_arrow);
        uploaded_image = (ImageView) findViewById(R.id.uploaded_image);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        no_List = (TextView) findViewById(R.id.no_List);
        upload_image_card = (CardView) findViewById(R.id.upload_image_card);
        postImage = (Button) findViewById(R.id.postImage);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

            case R.id.postImage:
                finish();
                break;

            case R.id.upload_image_card:
                showUploadDialogue();
                break;
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
                        uploaded_image.setImageBitmap(bitmap);
//                        uploaded_image.setVisibility(View.VISIBLE);
//                        upload_image_card.setVisibility(View.GONE);
                        ConfigurationData.uploadedImageF = saveImage(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (NetworkStatus.isNetworkAvailable(mContext)) {
                    postMoreAdsImages();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                uploaded_image.setImageBitmap(thumbnail);
//                uploaded_image.setVisibility(View.VISIBLE);
//                upload_image_card.setVisibility(View.GONE);
                ConfigurationData.uploadedImageF = saveImage(thumbnail);
                if (NetworkStatus.isNetworkAvailable(mContext)) {
                    postMoreAdsImages();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                }
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
                            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                        }
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

    private void postMoreAdsImages() {
        if (ConfigurationData.uploadedImageF == null || ConfigurationData.uploadedImageF.isEmpty()) {
            Toast.makeText(mContext, R.string.please_upload_an_image, Toast.LENGTH_SHORT).show();
            return;
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage(getResources().getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();

            File fileF = new File(ConfigurationData.uploadedImageF);
            RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), fileF);
            MultipartBody.Part addImageFirst = MultipartBody.Part.createFormData("img_1", fileF.getName(), requestFile);

            RequestBody crsfToken = RequestBody.create(MediaType.parse("text/plain"), ConfigurationData.crsfTokenValue);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getLoginUserId());
            RequestBody imageApsId = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getPosetdAdsId());

            ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
            Call<CommonResponse> responseCall = apiInterface.postMoreAdsImages(crsfToken, userId, imageApsId, addImageFirst);
            responseCall.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    try {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (response.isSuccessful()) {
                            if (response.body().getSuccess().equals(true)) {
                                Toast.makeText(mContext, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                                if (NetworkStatus.isNetworkAvailable(mContext)) {
                                    getPostedAdsImages();
                                } else {
                                    Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                                }
//                                recreate();
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
                            Toast.makeText(mContext, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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

    private void getPostedAdsImages() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<PostedImageResponse> responseCall = apiInterface.getPostedAdsImages(ConfigurationData.crsfTokenValue,
                appPreferencesShared.getLoginUserId(), appPreferencesShared.getPosetdAdsId());

        responseCall.enqueue(new Callback<PostedImageResponse>() {
            @Override
            public void onResponse(Call<PostedImageResponse> call, Response<PostedImageResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            if (!data.isEmpty()) {
                                data.clear();
                            }
                            data = response.body().getData();
                            no_List.setVisibility(View.GONE);
                            recycler_view.setVisibility(View.VISIBLE);
                            setLayoutOfAdsImageData();
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            no_List.setVisibility(View.GONE);
                            recycler_view.setVisibility(View.GONE);
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(mContext, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        no_List.setVisibility(View.GONE);
                        recycler_view.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<PostedImageResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                no_List.setVisibility(View.VISIBLE);
                recycler_view.setVisibility(View.GONE);
            }
        });
    }

    private void setLayoutOfAdsImageData() {
        gridAdapter = new GridAdapter(mContext, data);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        recycler_view.setLayoutManager(gridLayoutManager);
        recycler_view.setAdapter(gridAdapter);
        recycler_view.setHasFixedSize(true);

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
}