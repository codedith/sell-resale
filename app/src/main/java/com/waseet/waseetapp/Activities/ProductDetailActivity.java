package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;
import com.waseet.waseetapp.Adapters.SlidingImageAdapter;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.AdsDetails;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.CategoryListResponse;
import com.waseet.waseetapp.Models.CommonResponse;
import com.waseet.waseetapp.Models.ImageList;
import com.waseet.waseetapp.Models.PostedImageResponse;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.Utilities.NetworkStatus;
import com.waseet.waseetapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_SHORT;

public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    private AppPreferencesShared appPreferencesShared;
    private ImageView like;

    private int[] myImageList = new int[]{R.drawable.alexa, R.drawable.alexa,
            R.drawable.alexa, R.drawable.alexa, R.drawable.alexa, R.drawable.alexa};

    private ImageView back_key, edit, ad_image;
    private TextView toolbar_title, categoryTitleName, categoryDescription, categoryPrice, categoryName, subcategroyName,
            categoryType, location;

    private List<ImageList> data = new ArrayList<>();
    /*private ArrayList<ImageModel> imageModelArrayList;*/
    RelativeLayout slider_layout;
    private Button chat, call;
    CategoryListResponse categoryListResponse;

    String smsNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

      /*  Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        data = new ArrayList<>();
        data = populateList();

        getInitUi();

        back_key.setOnClickListener(this);
        toolbar_title.setText(R.string.product_details);
        edit.setOnClickListener(this);
        like.setOnClickListener(this);
        chat.setOnClickListener(this);
        call.setOnClickListener(this);

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCSrfToken();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), LENGTH_SHORT).show();
        }

//        for (int i = 0; i < categoryListResponse.getData().size(); i++) {
//            categoryTitleName.setText("Ad title \n" + categoryListResponse.getData().get(i).getTitle());
//            categoryDescription.setText("Description \n" + categoryListResponse.getData().get(i).getDescription());
//            categoryPrice.setText("Price : " + categoryListResponse.getData().get(i).getPrice());
//            categoryName.setText("Category \n" + categoryListResponse.getData().get(i).getCategoryName());
//            subcategroyName.setText("Subcategory \n" + categoryListResponse.getData().get(i).getSubcategoryName());
//            categoryType.setText("Category type : " + categoryListResponse.getData().get(i).getCategoryName());
//            location.setText("Location : " + categoryListResponse.getData().get(i).getLocation());
//        }

        /*categoryTitleName.setText("Ad title \n" + categoryListResponse.getData().get(0).getTitle());
        categoryDescription.setText("Description \n" + categoryListResponse.getData().get(0).getDescription());
        categoryPrice.setText("Price : " + categoryListResponse.getData().get(0).getPrice());
        categoryName.setText("Category \n" + categoryListResponse.getData().get(0).getCategoryName());
        subcategroyName.setText("Subcategory \n" + categoryListResponse.getData().get(0).getSubcategoryName());
        categoryType.setText("Category type : " + categoryListResponse.getData().get(0).getCategoryName());
        location.setText("Location : " + categoryListResponse.getData().get(0).getLocation());*/

    }

    private void getInitUi() {
        back_key = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        categoryTitleName = findViewById(R.id.categoryTitleName);
        categoryDescription = findViewById(R.id.categoryDescription);
        categoryPrice = findViewById(R.id.categoryPrice);
        categoryName = findViewById(R.id.categoryName);
        subcategroyName = findViewById(R.id.subcategroyName);
        categoryType = findViewById(R.id.categoryType);
        location = findViewById(R.id.location);
        edit = findViewById(R.id.edit);
        like = findViewById(R.id.like);
        slider_layout = findViewById(R.id.slider_layout);
        ad_image = findViewById(R.id.ad_image);
        chat = findViewById(R.id.chat);
        call = findViewById(R.id.call);
    }

    private void getCSrfToken() {
        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CSRFTokenResponse> tokenRegisterLoginResponseCall = apiInterface.getCSrfToken();
        tokenRegisterLoginResponseCall.enqueue(new Callback<CSRFTokenResponse>() {
            @Override
            public void onResponse(Call<CSRFTokenResponse> call, Response<CSRFTokenResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            ConfigurationData.crsfTokenValue = response.body().getData().getCsrfTestName().toString();
                            appPreferencesShared.setCSrfToken(ConfigurationData.crsfTokenValue);
                            if (NetworkStatus.isNetworkAvailable(mContext)) {
                                postedAdsDetails();
                            } else {
                                Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<CSRFTokenResponse> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), LENGTH_SHORT).show();
            }
        });
    }

    private void postedAdsDetails() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<AdsDetails> responseCall = apiInterface.postedAdsDetails(ConfigurationData.crsfTokenValue,
                appPreferencesShared.getLoginUserId(), appPreferencesShared.getPosetdAdsId());
        responseCall.enqueue(new Callback<AdsDetails>() {
            @Override
            public void onResponse(Call<AdsDetails> call, Response<AdsDetails> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getAds() != null) {
                            smsNumber = response.body().getAds().getContact().toString();
                            categoryTitleName.setText("Ad title \n" + response.body().getAds().getTitle());
                            categoryDescription.setText("Description \n" + response.body().getAds().getDescription());
                            categoryPrice.setText("Price : " + response.body().getAds().getPrice());
                            categoryName.setText("Category \n" + response.body().getAds().getCategoryName());
                            subcategroyName.setText("Subcategory \n" + response.body().getAds().getSubcategoryName());
                            categoryType.setText("Category type : " + response.body().getAds().getCategoryName());
                            location.setText("Location : " + response.body().getAds().getLocation());
                            Picasso.with(mContext).load("http://theacademiz.com/classified/" + response.body().getAds().getImg1())
                                    .resize(100, 100).transform(new CropCircleTransformation())
                                    .placeholder(R.drawable.categories).into(ad_image);
                        } else {
                            categoryTitleName.setText("Ad title " + " ");
                            categoryDescription.setText("Description " + " ");
                            categoryPrice.setText("Price : " + " ");
                            categoryName.setText("Category " + " ");
                            subcategroyName.setText("Subcategory " + " ");
                            categoryType.setText("Category type : " + " ");
                            location.setText("Location : " + " ");
                        }
                        if (NetworkStatus.isNetworkAvailable(mContext)) {
                            getPostedAdsImages();
                        } else {
                            Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), LENGTH_SHORT).show();
                        }
                    } else {
                        categoryTitleName.setText("Ad title " + " ");
                        categoryDescription.setText("Description " + " ");
                        categoryPrice.setText("Price : " + " ");
                        categoryName.setText("Category " + " ");
                        subcategroyName.setText("Subcategory " + " ");
                        categoryType.setText("Category type : " + " ");
                        location.setText("Location : " + " ");
                        Toast.makeText(mContext, "Something went wrong !", LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<AdsDetails> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImageAdapter(getApplicationContext(), (ArrayList<ImageList>) data));
        CirclePageIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        NUM_PAGES = data.size();
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private ArrayList<ImageList> populateList() {
        ArrayList<ImageList> list = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            ImageList imageModel = new ImageList();
            /* imageModel.setImagePath(myImageList[i]);*/
            list.add(imageModel);
        }
        return list;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

            case R.id.edit:
                startActivity(new Intent(getApplicationContext(), EditPostScreenActivity.class));
                break;
            case R.id.like:
                if (appPreferencesShared.getLoginUserId().isEmpty()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setMessage(R.string.please_Login);
                    alertDialogBuilder.setPositiveButton(R.string.login,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    appPreferencesShared.setLogInDirection("inside");
                                    Intent intent = new Intent(mContext, LoginActivity.class);
                                    mContext.startActivity(intent);
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FAF8F8")));
                } else {
                    if (NetworkStatus.isNetworkAvailable(mContext)) {
                        getCSrfToken();
                        postAddFavoriteApi();
                    } else {
                        Toast.makeText(mContext, "Check Your Internet Connection", LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.chat:
                openWhatsApp();
                break;

            case R.id.call:
                onPhoneCall();
                break;

        }
    }

    private void postAddFavoriteApi() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CommonResponse> loginResponseCall = apiInterface.postAddFavoriteApi(ConfigurationData.crsfTokenValue,
                appPreferencesShared.getLoginUserId(), ConfigurationData.productDetailsListId);
        loginResponseCall.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                          /*  Intent intent = new Intent("custom-message");
                            intent.putExtra("response", true);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);*/
                            SavedLayout(true);
                            Toast.makeText(mContext, "Added to Favorite", LENGTH_SHORT).show();
                        } else {
                            /*Intent intent = new Intent("custom-message");
                            intent.putExtra("response", true);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);*/
                            SavedLayout(false);
                            Toast.makeText(mContext, "Not Added to Favorite", LENGTH_SHORT).show();
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(mContext, "Could not save it due to network issue", LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(mContext, "Could not save it due to network issue", LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), LENGTH_SHORT).show();
            }
        });
    }

    private void SavedLayout(boolean saved) {
        if (saved) {
            like.setImageResource(R.drawable.filledheart);
        } else {
            like.setImageResource(R.drawable.emptyheart);
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
                           /* no_List.setVisibility(View.GONE);
                            recycler_view.setVisibility(View.VISIBLE);
                            setLayoutOfAdsImageData();*/
                            if (data != null || !data.isEmpty()) {
                                init();
                                slider_layout.setVisibility(View.VISIBLE);
                                ad_image.setVisibility(View.GONE);
                            } else {
                                slider_layout.setVisibility(View.GONE);
                                ad_image.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            slider_layout.setVisibility(View.GONE);
                            ad_image.setVisibility(View.VISIBLE);
                           /* no_List.setVisibility(View.GONE);
                            recycler_view.setVisibility(View.GONE);*/
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        slider_layout.setVisibility(View.GONE);
                        ad_image.setVisibility(View.VISIBLE);
                        Toast.makeText(mContext, getResources().getString(R.string.something_went_wrong), LENGTH_SHORT).show();
                       /* no_List.setVisibility(View.GONE);
                        recycler_view.setVisibility(View.GONE);*/
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
                Toast.makeText(mContext, t.getMessage(), LENGTH_SHORT).show();
               /* no_List.setVisibility(View.VISIBLE);
                recycler_view.setVisibility(View.GONE);*/
            }
        });
    }

    private void onPhoneCall() {
      /*  String posted_by = "123456788";*/
        String uri = "tel:" + smsNumber.trim();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    private void openWhatsApp() {
        /* smsNumber = categoryListResponse.getAds().getContact().toString();*/
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {

            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");//phone number without "+" prefix

            startActivity(sendIntent);
        } else {
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            Toast.makeText(this, "WhatsApp not Installed",
                    LENGTH_SHORT).show();
            startActivity(goToMarket);
        }
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

}
