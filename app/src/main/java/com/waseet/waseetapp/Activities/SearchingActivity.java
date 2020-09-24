package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.waseet.waseetapp.Adapters.SearchAdapter;
import com.waseet.waseetapp.Interfaces.ApiInterface;
import com.waseet.waseetapp.Models.CategoryListResponse;
import com.waseet.waseetapp.Models.Product;
import com.waseet.waseetapp.Models.ProductListResponse;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.ApiClients;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;
import com.waseet.waseetapp.Utilities.NetworkStatus;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchingActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private ImageView back_arrow;
    private TextView toolbar_title;
    private LinearLayout searching;
    private RelativeLayout list;
    private EditText et_SearchingCategory;
    private RecyclerView categoryListData;
    private List<Product> data;
    //private SearchAdapter searchAdapter;
    FloatingActionButton filter;
    private TextView noresults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);
       /* Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/
        getInitiatLayoutUi();
        back_arrow.setOnClickListener(this);
        toolbar_title.setText(getResources().getString(R.string.search));
        filter.setOnClickListener(this);
//        if (NetworkStatus.isNetworkAvailable(mContext)) {
//            getSearchDataList("");
//        } else {
//            Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
//        }

        et_SearchingCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence search, int start, int count, int after) {
                if (NetworkStatus.isNetworkAvailable(mContext)) {
                    getSearchDataList(search.toString());

                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                    /*noresults.setVisibility(View.VISIBLE);
                    noresults.setText("No Search Results");*/
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (NetworkStatus.isNetworkAvailable(mContext)) {
                    getSearchDataList(s.toString());
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                   /* noresults.setVisibility(View.VISIBLE);
                    noresults.setText("No Search Results");*/
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (NetworkStatus.isNetworkAvailable(mContext)) {
                    getSearchDataList(s.toString());
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.please_check_your_connection), Toast.LENGTH_SHORT).show();
                   /* noresults.setVisibility(View.VISIBLE);
                    noresults.setText("No Search Results");*/
                }
            }
        });
    }

    private void getInitiatLayoutUi() {
        back_arrow = (ImageView) findViewById(R.id.back_arrow);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        searching = (LinearLayout) findViewById(R.id.searching);
        list = (RelativeLayout) findViewById(R.id.list);
        et_SearchingCategory = (EditText) findViewById(R.id.et_SearchingCategory);
        categoryListData = (RecyclerView) findViewById(R.id.categoryListData);
        filter = findViewById(R.id.filter);
        noresults = findViewById(R.id.noresults);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;
            case R.id.filter:
                startActivity(new Intent(mContext, FilterScreenActivity.class));
                break;

        }
    }

    private void getSearchDataList(String searchQuery) {
       /* final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
*/
        ApiInterface apiInterface = ApiClients.getRetrofit().create(ApiInterface.class);
        Call<ProductListResponse> responseCall = apiInterface.getSearchDataList(searchQuery);
        responseCall.enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equals(true)) {
                            data = response.body().getData();
                            if (!data.isEmpty()) {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                                categoryListData.setLayoutManager(linearLayoutManager);
                                categoryListData.setAdapter(new SearchAdapter(mContext, data));
                                categoryListData.setHasFixedSize(true);
                                //SearchAdapter.notifyDataSetChanged();
                                SearchAdapter searchAdapter = new SearchAdapter(mContext, data);
                                searchAdapter.notifyDataSetChanged();
                                categoryListData.setVisibility(View.VISIBLE);
                                noresults.setVisibility(View.GONE);
                            } else {
                                categoryListData.setVisibility(View.GONE);
                                noresults.setVisibility(View.VISIBLE);
                                noresults.setText("No Search Results");
                            }
                        } else {
                            Toast.makeText(mContext, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(mContext, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<ProductListResponse> call, Throwable t) {
               /* if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }*/
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*private void setSearchLayoutinRecycler() {


    }*/

    @Override
    protected void onResume() {
        super.onResume();

        if (appPreferencesShared.getIsFilter()) {
            appPreferencesShared.setIsFilter(false);
            Gson gson = new Gson();
            ProductListResponse categoryListResponse = gson.fromJson(appPreferencesShared.getFilterDetailsList(), ProductListResponse.class);

            if (categoryListResponse.getData() != null) {
                data = categoryListResponse.getData();

                if (!categoryListResponse.getData().isEmpty()) {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    categoryListData.setLayoutManager(linearLayoutManager);
                    categoryListData.setAdapter(new SearchAdapter(mContext, data));
                    categoryListData.setHasFixedSize(true);
                    //SearchAdapter.notifyDataSetChanged();
                    SearchAdapter searchAdapter = new SearchAdapter(mContext, data);
                    searchAdapter.notifyDataSetChanged();
                    categoryListData.setVisibility(View.VISIBLE);
                    noresults.setVisibility(View.GONE);
                } else {
                    categoryListData.setVisibility(View.GONE);
                    noresults.setVisibility(View.VISIBLE);
                }
            } else {
                categoryListData.setVisibility(View.GONE);
                noresults.setVisibility(View.VISIBLE);
            }
        }
    }
}