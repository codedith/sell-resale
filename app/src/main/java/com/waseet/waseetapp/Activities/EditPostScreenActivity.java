package com.waseet.waseetapp.Activities;

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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.AdsDetails;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.CategoryListResponse;
import com.waseet.waseetapp.Models.CommonResponse;
import com.waseet.waseetapp.Models.CountryListResponse;
import com.waseet.waseetapp.Models.CityListResponse;
import com.waseet.waseetapp.Models.SubCategoryListResponse;
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
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.waseet.waseetapp.Utilities.ConfigurationData.IMAGE_DIRECTORY;

public class EditPostScreenActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private ImageView back_arrow, uploaded_image;
    private CardView upload_image_card;
    private TextView toolbar_title;
    private EditText price, address, title, description, adsTags;
    private RadioGroup radioGroup;
    private RadioButton negotiable, non_negotiable;
    private Button edit;
    private RecyclerView recycler_view;
    private Spinner category_spinner, subcategory_spinner, country_spinner, state_spinner, city_spinner;
    private LinearLayout subcategory_layout;

    private String[] categoryNameArray, subacategoryNameArray;
    private List<String> CategoryIdList = new ArrayList<>();
    private List<String> CategorySlugIdList = new ArrayList<>();
    private List<String> SubCategoryIdList = new ArrayList<>();
    private String categoryName, categoryId, subcategoryName, subcategoryId;
    private Double lati = 77.0638423, longi = 28.6013047;

    private String[] countryNameArray;
    private String[] stateNameArray;
    private String[] cityNameArray;
    private String countryName = "Select", stateName = "Select", cityName = "Select", countryId, stateid, cityId, old_image = "", image_selection="";
    private List<String> CountryIdList = new ArrayList<>();
    private List<String> StateIdList = new ArrayList<>();
    private List<String> CityIdlist = new ArrayList<>();

    private DataValidations validations;

    private String[] appPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private int PERMISSIONS_REQUEST_CODE = 1024, GALLERY = 0, CAMERA = 1;

    private PopupWindow mPopupWindow;
    private RelativeLayout login_Popup;
    private TextView no_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post_screen);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

/*
        Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
*/

        getInitUi();

        validations = new DataValidations();

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCSrfToken();
            postedAdsDetails();
            getCategoryList();
            getCountryList();
        } else {
            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
        }

        back_arrow.setOnClickListener(this);
        upload_image_card.setOnClickListener(this);
        uploaded_image.setOnClickListener(this);
        edit.setOnClickListener(this);
        toolbar_title.setText(R.string.add_post);

        checkAndRequestPermissions();
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int options = radioGroup.getCheckedRadioButtonId();
                switch (options) {
                    case R.id.negotiable:
                        if (negotiable.isChecked()) {
                            ConfigurationData.selectedAnswers = true;
                        } else {
                            ConfigurationData.selectedAnswers = false;
                        }
                        break;

                    case R.id.non_negotiable:
                        if (non_negotiable.isChecked()) {
                            ConfigurationData.selectedAnswers = true;
                        } else {
                            ConfigurationData.selectedAnswers = false;
                        }
                        break;
                }
            }
        });
    }

    private void getInitUi() {
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        recycler_view = findViewById(R.id.recycler_view);
        category_spinner = findViewById(R.id.category_spinner);
        subcategory_spinner = findViewById(R.id.subcategory_spinner);
        country_spinner = findViewById(R.id.country_spinner);
        state_spinner = findViewById(R.id.state_spinner);
        city_spinner = findViewById(R.id.city_spinner);
        subcategory_layout = findViewById(R.id.subcategory_layout);
        edit = (Button) findViewById(R.id.edit);
        price = (EditText) findViewById(R.id.price);
        address = (EditText) findViewById(R.id.address);
        adsTags = (EditText) findViewById(R.id.adsTags);
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        negotiable = (RadioButton) findViewById(R.id.negotiable);
        non_negotiable = (RadioButton) findViewById(R.id.non_negotiable);
        uploaded_image = (ImageView) findViewById(R.id.uploaded_image);
        upload_image_card = (CardView) findViewById(R.id.upload_image_card);
        login_Popup = (RelativeLayout) findViewById(R.id.login_Popup);
        no_List = findViewById(R.id.no_List);

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
                        Toast.makeText(mContext, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
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

    private void getCountryList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CountryListResponse> countryListDataCall = apiInterface.getCounrtyList();
        countryListDataCall.enqueue(new Callback<CountryListResponse>() {
            @Override
            public void onResponse(Call<CountryListResponse> call, Response<CountryListResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                countryNameArray = new String[response.body().getData().size()];
                                for (int i = 0; i < response.body().getData().size(); i++) {
                                    CountryIdList.add(response.body().getData().get(i).getId());
                                    if (appPreferencesShared.getLocale().equals("ar")) {
                                        countryNameArray[i] = response.body().getData().get(i).getNameArabic();
                                    }
                                    else {
                                        countryNameArray[i] = response.body().getData().get(i).getName();
                                    }
                                }
                                ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_layout_without_color, countryNameArray);
                                country_spinner.setAdapter(countryAdapter);
                                country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        countryId = CountryIdList.get(i);
                                        countryName = countryNameArray[i];
                                        state_spinner.setEnabled(true);
                                        if (NetworkStatus.isNetworkAvailable(mContext)) {
                                            getCityList();
                                        } else {
                                            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            } else {
                                Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CountryListResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void getStateListMethod() {
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage(getResources().getString(R.string.please_wait));
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
//        Call<CountryListResponse> stateListDataCall = apiInterface.getCountryStateList(countryId);
//        stateListDataCall.enqueue(new Callback<CountryListResponse>() {
//            @Override
//            public void onResponse(Call<CountryListResponse> call, Response<CountryListResponse> response) {
//                try {
//                    if (progressDialog.isShowing()) {
//                        progressDialog.dismiss();
//                    }
//                    if (response.isSuccessful()) {
//                        if (response.body() != null) {
//                            if (response.body().getData() != null) {
//                                stateNameArray = new String[response.body().getData().size()];
//                                for (int i = 0; i < response.body().getData().size(); i++) {
//                                    StateIdList.add(response.body().getData().get(i).getId());
//                                    stateNameArray[i] = response.body().getData().get(i).getName();
//
//                                }
//                                ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_layout_without_color, stateNameArray);
//                                state_spinner.setAdapter(stateAdapter);
//                                state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                    @Override
//                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                                        stateid = StateIdList.get(i);
//                                        stateName = stateNameArray[i];
//                                        city_spinner.setEnabled(true);
//                                        if (NetworkStatus.isNetworkAvailable(mContext)) {
//                                            getStateCityList();
//                                        } else {
//                                            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                                    }
//                                });
//                            } else {
//                                Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } catch (Exception e) {
//                    if (progressDialog.isShowing()) {
//                        progressDialog.dismiss();
//                    }
//                    Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CountryListResponse> call, Throwable t) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void getCityList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CityListResponse> listResponseCall = apiInterface.getStateCityList(countryId);
        listResponseCall.enqueue(new Callback<CityListResponse>() {
            @Override
            public void onResponse(Call<CityListResponse> call, Response<CityListResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                cityNameArray = new String[response.body().getData().size()];
                                for (int i = 0; i < response.body().getData().size(); i++) {
                                    CityIdlist.add(response.body().getData().get(i).getId());
                                    if (appPreferencesShared.getLocale().equals("ar")) {
                                        cityNameArray[i] = response.body().getData().get(i).getNameArabic();
                                    }
                                    else {
                                        cityNameArray[i] = response.body().getData().get(i).getName();
                                    }
                                }
                                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_layout_without_color, cityNameArray);
                                city_spinner.setAdapter(cityAdapter);
                                city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        cityId = CityIdlist.get(i);
                                        cityName = cityNameArray[i];
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            } else {
                                Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<CityListResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCategoryList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CategoryListResponse> categoryListResponseCall = apiInterface.getAllCategoryList();
        categoryListResponseCall.enqueue(new Callback<CategoryListResponse>() {
            @Override
            public void onResponse(Call<CategoryListResponse> call, Response<CategoryListResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            categoryNameArray = new String[response.body().getData().size()];
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                CategoryIdList.add(response.body().getData().get(i).getId());
                                CategorySlugIdList.add(response.body().getData().get(i).getSlug());
                                if (appPreferencesShared.getLocale().equals("ar")) {
                                    categoryNameArray[i] = response.body().getData().get(i).getNameAb();
                                }
                                else {
                                    categoryNameArray[i] = response.body().getData().get(i).getName();
                                }
                            }
                            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_layout_without_color, categoryNameArray);
                            category_spinner.setAdapter(categoryAdapter);
                            category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    categoryId = CategoryIdList.get(i);
                                    ConfigurationData.slugData = CategorySlugIdList.get(i);
                                    categoryName = categoryNameArray[i];
                                    appPreferencesShared.setCategoryId(categoryId);
                                    if (NetworkStatus.isNetworkAvailable(mContext)) {
                                        subcategory_layout.setVisibility(View.VISIBLE);
                                        getSubCategoryList();
                                    } else {
                                        Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        } else {
                            Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryListResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void getSubCategoryList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<SubCategoryListResponse> subcategoryCall = apiInterface.getAllSubcategoriesList(ConfigurationData.slugData);
        subcategoryCall.enqueue(new Callback<SubCategoryListResponse>() {
            @Override
            public void onResponse(Call<SubCategoryListResponse> call, Response<SubCategoryListResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            if (response.body().getData() != null) {
                                subacategoryNameArray = new String[response.body().getData().size()];
                                for (int i = 0; i < response.body().getData().size(); i++) {
                                    SubCategoryIdList.add(response.body().getData().get(i).getId());
                                    if (appPreferencesShared.getLocale().equals("ar")) {
                                        subacategoryNameArray[i] = response.body().getData().get(i).getNameArabic()==null?"":
                                                response.body().getData().get(i).getNameArabic().toString();
                                    }
                                    else {
                                        subacategoryNameArray[i] = response.body().getData().get(i).getName();
                                    }
                                }
                                ArrayAdapter<String> subcategoryAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_layout_without_color, subacategoryNameArray);
                                subcategory_spinner.setAdapter(subcategoryAdapter);
                                subcategory_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        subcategoryId = SubCategoryIdList.get(i);
                                        subcategoryName = subacategoryNameArray[i];
                                        appPreferencesShared.setSubCategoryId(subcategoryId);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            } else {
                                Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SubCategoryListResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

            case R.id.uploaded_image:
                showUploadDialogue();
                break;

            case R.id.edit:
                if (appPreferencesShared.getLoginUserId().isEmpty()) {
               /*     LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View customView = inflater.inflate(R.layout.login_popup_msg, null);
                    mPopupWindow = new PopupWindow(customView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

                    TextView loginText = (TextView) customView.findViewById(R.id.loginText);
                    Button login_Please = (Button) customView.findViewById(R.id.login_Please);
                    login_Please.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            appPreferencesShared.setLogInDirection("inside");
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            startActivity(intent);
                            mPopupWindow.dismiss();
                        }
                    });
                    mPopupWindow.showAtLocation(login_Popup, Gravity.CENTER, 0, 0);*/

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setCancelable(false);
                    alertDialogBuilder.setMessage(getResources().getString(R.string.please_Login));
                    alertDialogBuilder.setPositiveButton(getResources().getString(R.string.login),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    appPreferencesShared.setLogInDirection("inside");
                                    Intent intent = new Intent(mContext, LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FAF8F8")));
                } else {
                    if (validations.isSpinnerValid(categoryName, mContext, "Select Category") &&
                            validations.isSpinnerValid(subcategoryName, mContext, "Select Subcategory") &&
                            validations.isMandatory(price.getText().toString(), price) &&
                            ConfigurationData.selectedAnswers &&
                            validations.isSpinnerValid(countryName, mContext, "Select Country") &&
                            validations.isSpinnerValid(stateName, mContext, "Select State") &&
                            validations.isSpinnerValid(cityName, mContext, "Select City") &&
                            validations.isMandatory(address.getText().toString(), address) &&
                            validations.isMandatory(title.getText().toString(), title) &&
                            validations.descriptionSize(description.getText().toString(), description) &&
                            validations.isMandatory(adsTags.getText().toString(), adsTags)) {
                        if (NetworkStatus.isNetworkAvailable(mContext)) {
                            postAddEditAds();
                        } else {
                            Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
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
            } else if (requestCode == CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                uploaded_image.setImageBitmap(thumbnail);
//                uploaded_image.setVisibility(View.VISIBLE);
//                upload_image_card.setVisibility(View.GONE);
                ConfigurationData.uploadedImageF = saveImage(thumbnail);
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
                            title.setText(response.body().getAds().getTitle());
                            description.setText(response.body().getAds().getDescription());
                            price.setText(response.body().getAds().getPrice());
                            address.setText(response.body().getAds().getLocation());
                            adsTags.setText(response.body().getAds().getTags());
                            categoryName = response.body().getAds().getCategoryName();
                            subcategoryName = response.body().getAds().getSubcategoryName();
                            old_image = "http://theacademiz.com/classified/" + response.body().getAds().getImg1();
                            Picasso.with(mContext).load("http://theacademiz.com/classified/" + response.body().getAds().getImg1())
                                    .placeholder(R.drawable.categories).into(uploaded_image);
                        } else {
                            title.setText("");
                            description.setText("");
                            price.setText("");
                        }
                    } else {
                        title.setText("");
                        description.setText("");
                        price.setText("");
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
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postAddEditAds() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestBody requestFile;
        MultipartBody.Part addImageFirst;

        if (ConfigurationData.uploadedImageF.isEmpty()) {
            requestFile = RequestBody.create(MediaType.parse("text/plain"), "");
            addImageFirst = MultipartBody.Part.createFormData("img_1", "", requestFile);
        }
        else {
            File fileF = new File(ConfigurationData.uploadedImageF);
            requestFile = RequestBody.create(MediaType.parse("*/*"), fileF);
            addImageFirst = MultipartBody.Part.createFormData("img_1", fileF.getName(), requestFile);
        }

        RequestBody crsfToken = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getCSrfToken());
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getLoginUserId());
        RequestBody adsId = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getPosetdAdsId());
        RequestBody categoryId = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getCategoryId());
        RequestBody subCategoryId = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getSubCategoryId());
        RequestBody prices = RequestBody.create(MediaType.parse("text/plain"), price.getText().toString());
        RequestBody adsNegotiable = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(ConfigurationData.selectedAnswers));
        RequestBody country = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getCountryId());
        RequestBody state = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getStateId());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), appPreferencesShared.getCityId());
        RequestBody addresss = RequestBody.create(MediaType.parse("text/plain"), address.getText().toString());
        RequestBody addressLati = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(lati));
        RequestBody addressLongi = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(longi));
        RequestBody addTitle = RequestBody.create(MediaType.parse("text/plain"), title.getText().toString());
        RequestBody adsTag = RequestBody.create(MediaType.parse("text/plain"), adsTags.getText().toString());
        RequestBody adsDescription = RequestBody.create(MediaType.parse("text/plain"), description.getText().toString());
        RequestBody old_image_1 = RequestBody.create(MediaType.parse("text/plain"), old_image);

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<CommonResponse> responseCall = apiInterface.postAddEditAds(crsfToken, userId, adsId, categoryId, subCategoryId,
                prices, country, state, city, addTitle, adsNegotiable, addresss, addressLongi, addressLati, adsTag, adsDescription,
                old_image_1, addImageFirst);

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
                            Intent intent = new Intent(mContext, MoreImageUploadActivity.class);
                            startActivity(intent);
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