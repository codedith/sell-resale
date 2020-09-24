package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;

import java.util.Locale;

public class HelpScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    private ImageView back_arrow;
    private TextView toolbar_title;
    private CardView email_us, call_us, whatsapp_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

        /*Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        getInitUi();
        toolbar_title.setText(R.string.help);
        whatsapp_share.setOnClickListener(this);
        back_arrow.setOnClickListener(this);
        call_us.setOnClickListener(this);
        email_us.setOnClickListener(this);
    }

    private void getInitUi() {
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);
        email_us = findViewById(R.id.email_us);
        call_us = findViewById(R.id.call_us);
        whatsapp_share = findViewById(R.id.whatsapp_share);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_arrow:
                onBackPressed();
                break;

            case R.id.whatsapp_share:
                onWhatsppClicked();
                break;
            case R.id.call_us:
                onPhoneCall();
                break;
            case R.id.email_us:
                onEmailClicked();
                break;
        }
    }

    private void onPhoneCall() {
        String posted_by = "123456788";
        String uri = "tel:" + posted_by.trim();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    private void onWhatsppClicked() {
        PackageManager pm = getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = getString(R.string.how_help_you);
            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, getString(R.string.share_with)));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(mContext, R.string.whatsapp_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    private void onEmailClicked() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"waseet@info.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Subject of Email");
        i.putExtra(Intent.EXTRA_TEXT, "How can We help You?");
        try {
            startActivity(Intent.createChooser(i, getString(R.string.send_mail)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, R.string.no_email_clients, Toast.LENGTH_SHORT).show();
        }
    }
}
