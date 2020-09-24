package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFToken;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.CategoryListResponse;
import com.waseet.waseetapp.Models.CountryListResponse;
import com.waseet.waseetapp.Models.CityListResponse;
import com.waseet.waseetapp.Models.Product;
import com.waseet.waseetapp.Models.ProductListResponse;
import com.waseet.waseetapp.Models.SubCategoryListResponse;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.Utilities.DataValidations;
import com.waseet.waseetapp.Utilities.NetworkStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterScreenActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private ImageView back_arrow;
    private TextView toolbar_title, datePublished_text, relvaanceTv, lowest_Price, highest_Price;
    private Spinner category_spinner, subcategory_spinner, country_spinner, state_spinner, city_spinner;
    private EditText et_lowestPirce, et_highestPrice;
    private Button bt_Submit;
    private CardView date_Published, relevents, lowest_Prices, high_prieces;
    private LinearLayout subcategory_layout;

    private String[] categoryNameArray, subacategoryNameArray;
    private List<String> CategoryIdList = new ArrayList<>();
    private List<String> CategorySlugIdList = new ArrayList<>();
    private List<String> SubCategoryIdList = new ArrayList<>();
    private List<String> SubCategorySlugIdList = new ArrayList<>();
    private String categoryName, categoryId, subcategoryNaame, subcategoryId;
    private Double lati = 77.0638423, longi = 28.6013047;

    private String[] countryNameArray;
    private String[] stateNameArray;
    private String[] cityNameArray;
    private String categorySpinner, countryName = "Select", stateName = "Select", cityName = "Select", countryId, stateid, cityId;
    private List<String> CountryIdList = new ArrayList<>();
    private List<String> StateIdList = new ArrayList<>();
    private List<String> CityIdlist = new ArrayList<>();
    private DataValidations validations;
    private List<Product> data;
    private ProductListResponse categoryListResponse;
    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_screen);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

      /*  Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        validations = new DataValidations();

        getInitUi();
        /*setupSwipeRefresh();*/
        refresh.setOnRefreshListener(this);
        toolbar_title.setText(R.string.apply_filters);
        back_arrow.setOnClickListener(this);
        bt_Submit.setOnClickListener(this);

        state_spinner.setEnabled(false);
        city_spinner.setEnabled(false);
        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCSrfToken();
            getCategoryList();
            getCountryList();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private void getInitUi() {
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        datePublished_text = (TextView) findViewById(R.id.datePublished_text);
        relvaanceTv = (TextView) findViewById(R.id.relvaanceTv);
        lowest_Price = (TextView) findViewById(R.id.lowest_Price);
        highest_Price = (TextView) findViewById(R.id.highest_Price);
        et_lowestPirce = (EditText) findViewById(R.id.et_lowestPirce);
        et_highestPrice = (EditText) findViewById(R.id.et_highestPrice);
        bt_Submit = (Button) findViewById(R.id.bt_Submit);
        category_spinner = (Spinner) findViewById(R.id.category_spinner);
        subcategory_spinner = (Spinner) findViewById(R.id.subcategory_spinner);
        country_spinner = (Spinner) findViewById(R.id.country_spinner);
        state_spinner = (Spinner) findViewById(R.id.state_spinner);
        city_spinner = (Spinner) findViewById(R.id.city_spinner);
        subcategory_layout = (LinearLayout) findViewById(R.id.subcategory_layout);
        refresh = findViewById(R.id.refresh);

    }

    private void setupSwipeRefresh() {
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new CountDownTimer(1000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        refresh.setRefreshing(false);
                    }
                }.start();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

            case R.id.bt_Submit:
                if (validations.isSpinnerValid(categoryName, mContext, "Select Category") &&
                        validations.isSpinnerValid(subcategoryNaame, mContext, "Select Subcategory") &&
                        validations.isMandatory(et_lowestPirce.getText().toString(), et_lowestPirce) &&
                        validations.isMandatory(et_highestPrice.getText().toString(), et_highestPrice) &&
                        validations.isSpinnerValid(countryName, mContext, "Select Country") &&
                        validations.isSpinnerValid(stateName, mContext, "Select State") &&
                        validations.isSpinnerValid(cityName, mContext, "Select City"))
                    if (NetworkStatus.isNetworkAvailable(mContext)) {
                        getFilteredCategoryList();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                    }
                break;
        }
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
                                    appPreferencesShared.setCategory(ConfigurationData.slugData);
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
                                    SubCategorySlugIdList.add(response.body().getData().get(i).getSlug());
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
                                        ConfigurationData.subCategoryData = SubCategorySlugIdList.get(i);
                                        subcategoryNaame = subacategoryNameArray[i];
                                        appPreferencesShared.setSubCategory(ConfigurationData.subCategoryData);
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
        Call<CityListResponse> listResponseCall = apiInterface.getStateCityList(stateid);
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

    private void getFilteredCategoryList() {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<ProductListResponse> responseCall = apiInterface.getFilteredCategoryList(ConfigurationData.slugData,
                ConfigurationData.subCategoryData, et_lowestPirce.getText().toString(), et_highestPrice.getText().toString(),
                appPreferencesShared.getCountryId(), appPreferencesShared.getStateId(),
                appPreferencesShared.getCityId(), "");

        responseCall.enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            categoryListResponse = response.body();
                            data = response.body().getData();

                            Gson gson = new Gson();
                            ConfigurationData.filterDetailsList = gson.toJson(categoryListResponse);
                            appPreferencesShared.setFilterDetailsList(ConfigurationData.filterDetailsList);

                            appPreferencesShared.setIsFilter(true);
                            finish();

//                            Intent intent = new Intent(mContext, )

                        } else {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(mContext, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(false);
                refresh.setColorSchemeColors(getResources().getColor(R.color.purple));
            }
        }, 1000);
    }
}