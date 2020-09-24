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

public class AboutUsActivity extends AppCompatActivity {
    private ImageView back_arrow;
    private TextView toolbar_title;
    AppPreferencesShared appPreferencesShared;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        mContext = this;

        appPreferencesShared = new AppPreferencesShared(mContext);

     /*   Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        getInitUi();
        toolbar_title.setText(getResources().getString(R.string.about_us));
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getInitUi() {
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
    }
}
