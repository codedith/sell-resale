package com.waseet.waseetapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.waseet.waseetapp.Adapters.CategoriesListAdapter;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CSRFTokenResponse;
import com.waseet.waseetapp.Models.Category;
import com.waseet.waseetapp.Models.CategoryListResponse;
import com.waseet.waseetapp.Models.UserInfoResponse;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.ConfigurationData;
import com.waseet.waseetapp.Utilities.NetworkStatus;

import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private ImageView mNavigation, search_key, user_Profile_Image;
    private NavigationView mDesign_navigation_view;
    private LinearLayout menuProfilesHeader;
    private ConstraintLayout headerProfile, headerProfileIfNotLogin;
    private FloatingActionButton addingPost_fab;
    private Button signInButton;
    private Context mContext;
    private CategoriesListAdapter categoriesListAdapter;
    private RecyclerView recyclerOfCategory;
    private TextView toolbar_title, plan_listNotFound, userEmailId, userPhoneNumber;
    private List<Category> data;
    private AppPreferencesShared appPreferencesShared;
    private UserInfoResponse registerLoginResponse;
    private EditText searing_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);
        loadLocale();
      /*  Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        setContentView(R.layout.activity_dashboard);

        getIUInitiatLayout();
        toolbar_title.setText(getResources().getString(R.string.categories));

        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open_drawer, R.string.close_drawer);
        mDrawer.addDrawerListener(mToggle);
        mToggle.setDrawerIndicatorEnabled(false);
        mNavigation.setOnClickListener(this);
        mDesign_navigation_view.setItemIconTintList(null);
//        search_key.setOnClickListener(this);
        addingPost_fab.setOnClickListener(this);

        mDesign_navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });

        search_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, SearchingActivity.class));
            }
        });

        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getAllCategoryList();
        } else {
            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
        }

        View header = mDesign_navigation_view.getHeaderView(0);
        headerProfile = (ConstraintLayout) header.findViewById(R.id.headerProfile);
        headerProfileIfNotLogin = (ConstraintLayout) header.findViewById(R.id.headerProfileIfNotLogin);
        user_Profile_Image = (ImageView) header.findViewById(R.id.user_Profile_Image);
        userEmailId = (TextView) header.findViewById(R.id.userEmailId);
        userPhoneNumber = (TextView) header.findViewById(R.id.userPhoneNumber);
        signInButton = (Button) header.findViewById(R.id.signInButton);

        mToggle.syncState();
        mDesign_navigation_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.my_PostsMenu) {
                    startActivity(new Intent(mContext, MyPostActivity.class));

                } else if (id == R.id.my_FavoritesMenu) {
                    startActivity(new Intent(mContext, MyFavourateActivity.class));

                } else if (id == R.id.language_Region_ChangeMenu) {
                    startActivity(new Intent(mContext, LanguageAndRegionActivity.class));

                } else if (id == R.id.helpMenu) {
                    startActivity(new Intent(mContext, HelpScreenActivity.class));

                } else if (id == R.id.Rrate_UsMenu) {
                    startActivity(new Intent(mContext, RatingActivity.class));

                } else if (id == R.id.terms_ConditionsMenu) {
                    startActivity(new Intent(mContext, TermConditionActivity.class));

                } else if (id == R.id.about_UsMenu) {
                    startActivity(new Intent(mContext, AboutUsActivity.class));

                } else if (id == R.id.notice_UsMenu) {
                    startActivity(new Intent(mContext, NotificationViewActivity.class));

                } else if (id == R.id.logout_Menu) {
                    appPreferencesShared.ClearPreferences();
                    startActivity(new Intent(mContext, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }

                DrawerLayout drawer = findViewById(R.id.drawer);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
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
                        } else {
                            Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
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
                        if (response.body().getSuccess() == true) {
                            registerLoginResponse = response.body();
                            Gson gson = new Gson();
                            ConfigurationData.userProfileDetails = gson.toJson(registerLoginResponse);
                            appPreferencesShared.setUserProfileDetails(ConfigurationData.userProfileDetails);

                            userEmailId.setText(response.body().getUserInfo().getEmail());
                            userPhoneNumber.setText(response.body().getUserInfo().getContact());
                            Picasso.with(mContext).load("http://theacademiz.com/classified/" + response.body().getUserInfo().getProfilePicture())
                                    .resize(200, 200).transform(new CropCircleTransformation())
                                    .placeholder(R.drawable.user_icon).into(user_Profile_Image);
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
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllCategoryList() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
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
                        if (response.body().getData() != null) {
                            data = response.body().getData();
                            recyclerOfCategory.setVisibility(View.VISIBLE);
                            plan_listNotFound.setVisibility(View.GONE);
                            setAllCategoryListLayout();
                        } else {
                            recyclerOfCategory.setVisibility(View.GONE);
                            plan_listNotFound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        recyclerOfCategory.setVisibility(View.GONE);
                        plan_listNotFound.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<CategoryListResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                recyclerOfCategory.setVisibility(View.GONE);
                plan_listNotFound.setVisibility(View.VISIBLE);

            }
        });
    }

    private void setAllCategoryListLayout() {
        categoriesListAdapter = new CategoriesListAdapter(mContext, data);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerOfCategory.setLayoutManager(gridLayoutManager);
        recyclerOfCategory.setAdapter(categoriesListAdapter);
        recyclerOfCategory.setHasFixedSize(true);

    }

    private void getIUInitiatLayout() {
        recyclerOfCategory = (RecyclerView) findViewById(R.id.recyclerOfCategory);
        mDrawer = findViewById(R.id.drawer);
        mNavigation = findViewById(R.id.navigation);
        mDesign_navigation_view = (NavigationView) findViewById(R.id.mDesign_navigation_view);
        headerProfile = (ConstraintLayout) findViewById(R.id.headerProfile);
        addingPost_fab = (FloatingActionButton) findViewById(R.id.addingPost_fab);
        search_key = findViewById(R.id.search_key);
        toolbar_title = findViewById(R.id.toolbar_title);
        plan_listNotFound = findViewById(R.id.plan_listNotFound);
        searing_key = (EditText) findViewById(R.id.searing_key);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        switch (id) {
            case R.id.search_Menu:
                Toast.makeText(mContext, "Searching is under Progress ", Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.navigation:
                mDrawer.openDrawer(GravityCompat.START);
                break;

            case R.id.addingPost_fab:
                startActivity(new Intent(mContext, AddPostScreenActivity.class));
                break;

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
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
        Locale myLocale = new Locale(appPreferencesShared.getLocale());
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

    @Override
    protected void onResume() {
        super.onResume();
        if (appPreferencesShared.getLanguageNavigation().equals("Language")) {
            appPreferencesShared.setLanguageNavigation("NoLang");
            finish();
            startActivity(getIntent());
        }
        if (!appPreferencesShared.getLoginUserId().isEmpty()) {
            headerProfile.setVisibility(View.VISIBLE);
            headerProfileIfNotLogin.setVisibility(View.GONE);
            headerProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(mContext, MyProfileActivity.class));
                }
            });
            if (NetworkStatus.isNetworkAvailable(mContext)) {
                getCSrfToken();
                getAllCategoryList();
                postUserProfileData();
            } else {
                Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
            }
        } else {
            headerProfile.setVisibility(View.GONE);
            headerProfileIfNotLogin.setVisibility(View.VISIBLE);
            appPreferencesShared.setLogInDirection("inside");
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
            });
        }
        if (NetworkStatus.isNetworkAvailable(mContext)) {
            getAllCategoryList();
        } else {
            Toast.makeText(mContext, R.string.please_check_your_connection, Toast.LENGTH_SHORT).show();
        }
        if (!appPreferencesShared.getLoginUserId().isEmpty()) {
            mDesign_navigation_view.getMenu().findItem(R.id.logout_Menu).setVisible(true);
        } else {
            mDesign_navigation_view.getMenu().findItem(R.id.logout_Menu).setVisible(false);
        }

        /*String oldLanguage = "language";
       String language = prefs.getString("languageToLoad", Locale.getDefault().getDisplayLanguage());
        if (!oldLanguage.equals(language)){
            finish();
            startActivity(getIntent());
        }*/



    }
}