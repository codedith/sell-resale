package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;

import java.util.Locale;

public class TermConditionActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private ImageView back_arrow;
    private TextView toolbar_title;
    private TextView tc_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_condition);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

   /*     Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        getInitUi();
        toolbar_title.setText(R.string.terms_and_conditions);
        back_arrow.setOnClickListener(this);
    }

    private void getInitUi() {
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        tc_text = findViewById(R.id.tc_text);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;
        }
    }
}
