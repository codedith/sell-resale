package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.waseet.waseetapp.BuildConfig;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;

import java.util.Locale;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageView back_arrow;
    private TextView toolbar_title;
    private ImageView charPlace, icSprite;
    private TextView titleRate, resultRate;
    private RatingBar rateStar;
    private String answerValue;
    private Animation charanim, anisprite;
    AppPreferencesShared appPreferencesShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

        /*Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        getUIInitLayout();

      /*  back_arrow.setOnClickListener(this);
        toolbar_title.setText("Rate Us");*/

        charanim = AnimationUtils.loadAnimation(mContext, R.anim.charanim);
        anisprite = AnimationUtils.loadAnimation(mContext, R.anim.anisprite);

        charPlace.startAnimation(charanim);
        icSprite.startAnimation(anisprite);

        rateStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                answerValue = String.valueOf((int) (rateStar.getRating()));
                if (answerValue.equals("1")) {
                    charPlace.setImageResource(R.drawable.rate_us);
                    charPlace.startAnimation(charanim);
                    icSprite.animate().alpha(0).setDuration(300).start();
                    resultRate.setText(getResources().getString(R.string.good));

                    Uri data = Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                    Intent gotoMarket = new Intent(Intent.ACTION_VIEW, data);
                    startActivity(gotoMarket);

                } else if (answerValue.equals("2")) {
                    charPlace.setImageResource(R.drawable.rate_us);
                    charPlace.startAnimation(charanim);
                    icSprite.animate().alpha(0).setDuration(300).start();
                    resultRate.setText(R.string.some_good);

                    Uri data = Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                    Intent gotoMarket = new Intent(Intent.ACTION_VIEW, data);
                    startActivity(gotoMarket);

                } else if (answerValue.equals("3")) {
                    charPlace.setImageResource(R.drawable.rate_us);
                    charPlace.startAnimation(charanim);
                    icSprite.animate().alpha(0).setDuration(300).start();
                    resultRate.setText(R.string.very_good);

                    Uri data = Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                    Intent gotoMarket = new Intent(Intent.ACTION_VIEW, data);
                    startActivity(gotoMarket);

                } else if (answerValue.equals("4")) {
                    charPlace.setImageResource(R.drawable.rate_us);
                    charPlace.startAnimation(charanim);
                    icSprite.animate().alpha(1).setDuration(300).start();
                    icSprite.startAnimation(anisprite);
                    resultRate.setText(R.string.good_job);

                    Uri data = Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                    Intent gotoMarket = new Intent(Intent.ACTION_VIEW, data);
                    startActivity(gotoMarket);

                } else if (answerValue.equals("5")) {
                    charPlace.setImageResource(R.drawable.rate_us);
                    charPlace.startAnimation(charanim);
                    icSprite.animate().alpha(1).setDuration(300).start();
                    icSprite.startAnimation(anisprite);
                    resultRate.setText(R.string.awesome);

                    Uri data = Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                    Intent gotoMarket = new Intent(Intent.ACTION_VIEW, data);
                    startActivity(gotoMarket);

                } else {
                    Toast.makeText(mContext, R.string.no_points, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUIInitLayout() {
//        back_arrow = findViewById(R.id.back_arrow);
//        toolbar_title = findViewById(R.id.toolbar_title);
        charPlace = (ImageView) findViewById(R.id.charPlace);
        icSprite = (ImageView) findViewById(R.id.icSprite);
        titleRate = (TextView) findViewById(R.id.titleRate);
        resultRate = (TextView) findViewById(R.id.resultRate);
        rateStar = (RatingBar) findViewById(R.id.rateStar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.back_arrow:
                onBackPressed();
                break;*/
        }
    }
}
