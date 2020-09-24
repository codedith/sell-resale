package com.waseet.waseetapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CountryListResponse;
import com.waseet.waseetapp.Models.CityListResponse;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.DataValidations;
import com.waseet.waseetapp.Utilities.NetworkStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LanguageAndRegionActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    //    private RecyclerView select_region_card;
    private TextView toolbar_title;
    private ImageView back_arrow;
    private Button submit_button;
    CardView english_card, arabic_card;
    TextView english_text, arabic_text;
    private Spinner country_spinner, state_spinner, city_spinner;
    // String currentLanguage = "ar", currentLang;
    private AppPreferencesShared appPreferencesShared;
    private Context mContext;
    private String[] countryNameArray;
    private String[] stateNameArray;
    private String[] cityNameArray;
    private String countryName = "Select", stateName = "Select", cityName = "Select", countryId, stateid, cityId;
    private List<String> CountryIdList = new ArrayList<>();
    private List<String> StateIdList = new ArrayList<>();
    private List<String> CityIdlist = new ArrayList<>();

    DataValidations dataValidations;
//    String country = "Select";
//    String state = "Select";
//    String city = "Select";

    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* changeLang(appPreferencesShared.getLocale());*/
        setContentView(R.layout.activity_language_and_region);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);
        loadLocale();
        dataValidations = new DataValidations();
        ///currentLanguage = appPreferencesShared.getLocale();
        getInitUi();
        refresh.setOnRefreshListener(this);
        /*setupSwipeRefresh();*/

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getCountryList();
        } else {
            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
        }

        toolbar_title.setText(R.string.language_and_region_select);
        toolbar_title.setTextColor(getResources().getColor(R.color.white));
        back_arrow.setVisibility(View.GONE);
        arabic_card.setOnClickListener(this);
        english_card.setOnClickListener(this);
        submit_button.setOnClickListener(this);

        if (english_card.isSelected()) {
            english_text.setTextColor(getResources().getColor(R.color.white));
            english_card.setCardBackgroundColor(getResources().getColor(R.color.purple));
        } else if (arabic_card.isSelected()) {
            arabic_text.setTextColor(getResources().getColor(R.color.white));
            arabic_card.setCardBackgroundColor(getResources().getColor(R.color.purple));
        }
    }

    private void getInitUi() {
        // select_region_card = findViewById(R.id.select_region_card);
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        submit_button = findViewById(R.id.submit_button);
        english_card = findViewById(R.id.english_card);
        arabic_card = findViewById(R.id.arabic_card);
        english_text = findViewById(R.id.english_text);
        arabic_text = findViewById(R.id.arabic_text);
        country_spinner = findViewById(R.id.country_spinner);
        state_spinner = findViewById(R.id.state_spinner);
        city_spinner = findViewById(R.id.city_spinner);
        refresh = findViewById(R.id.refresh);

    }

    /*private void setupSwipeRefresh() {
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
    }*/

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.english_card) {
            changeLang("en");
            appPreferencesShared.setLocale("en");
            /* recreate();*/
            startActivity(getIntent());
            finish();

        } else if (id == R.id.arabic_card) {
            changeLang("ar");
            appPreferencesShared.setLocale("ar");

            /*recreate();*/
            startActivity(getIntent());
            finish();

        } else if (id == R.id.submit_button) {
            if (dataValidations.isSpinnerValid(countryName, mContext, getString(R.string.select_country)) &&
                    dataValidations.isSpinnerValid(cityName, mContext, getString(R.string.select_city))) {
                appPreferencesShared.setIsLanguageSelected(true);
                if (appPreferencesShared.getLogInDirection().equals("start")) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                    finish();
                } else {
                   /* loadLocale();*/
                    appPreferencesShared.setLanguageNavigation("Language");
                    finish();
                }
            }
        }
        return;
    }

  /*  private void setLocale(String localeName) {
//        appPreferencesShared.setLocale(localeName);
        // Locale myLocale = new Locale(localeName);
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        //  configuration.locale = myLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(new Locale(localeName)); // API 17+ only.
        } else {
            configuration.locale = new Locale(localeName);
        }
        resources.updateConfiguration(configuration, displayMetrics);
//        recreate(); //if we comment this then language translation is done after onResume is called


    }*/

    /*private void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
//        Locale myLocale = new Locale(lang);
//        appPreferencesShared.setLocale(lang);
//        Locale.setDefault(myLocale);
//        android.content.res.Configuration config = new android.content.res.Configuration();
//        config.locale = myLocale;
//        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        Locale myLocale = new Locale(lang);
        appPreferencesShared.setLocale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }*/

    @Override
    protected void onResume() {
        super.onResume();

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
                                    } else {
                                        countryNameArray[i] = response.body().getData().get(i).getName();
                                    }
                                    appPreferencesShared.setCountryId(response.body().getData().get(i).getId());
                                }
                                ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_layout, countryNameArray);
                                country_spinner.setAdapter(countryAdapter);
                                country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        countryId = CountryIdList.get(i);
                                        countryName = countryNameArray[i];
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
//                                    appPreferencesShared.setStateId(response.body().getData().get(i).getId());
//                                }
//                                ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_layout, stateNameArray);
//                                state_spinner.setAdapter(stateAdapter);
//                                state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                                    @Override
//                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                                        stateid = StateIdList.get(i);
//                                        stateName = stateNameArray[i];
//                                        if (NetworkStatus.isNetworkAvailable(mContext)) {
//                                            getStateCityList();
//                                        } else {
//                                            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
//                                        }
//
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
                                    } else {
                                        cityNameArray[i] = response.body().getData().get(i).getName();
                                    }
                                    appPreferencesShared.setCityId(response.body().getData().get(i).getId());
                                }
                                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_layout, cityNameArray);
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

    public void loadLocale() {  //for loading the same language that user selects before exits
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        changeLang(language);
    }

    private void changeLang(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    } //for change the language

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
    }
}