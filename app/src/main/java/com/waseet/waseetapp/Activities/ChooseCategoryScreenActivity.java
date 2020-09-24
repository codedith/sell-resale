package com.waseet.waseetapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.waseet.waseetapp.Adapters.ExpandableListAdapter;
import com.waseet.waseetapp.R;
import com.waseet.waseetapp.Utilities.AppPreferencesShared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChooseCategoryScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private AppPreferencesShared appPreferencesShared;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<Integer> lbListheaderImage;
    private ImageView back_arrow;
    private TextView toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category_screen);
        mContext = this;
        appPreferencesShared = new AppPreferencesShared(mContext);

     /*   Locale myLocale = new Locale(appPreferencesShared.getLocale());
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);*/

        prepareListData();
        getInitUi();
        back_arrow.setOnClickListener(this);
        toolbar_title.setText(R.string.choose_category); //choose category karna hai
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expListView.setIndicatorBounds(width - px, width);
        } else {
            expListView.setIndicatorBoundsRelative(width - px, width);
        }

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, lbListheaderImage);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return false;
            }
        });
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
//                Toast.makeText(getApplicationContext(), listDataHeader.get(i) + " Expanded", Toast.LENGTH_SHORT).show();
            }
        });
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
//                Toast.makeText(getApplicationContext(),
//                        listDataHeader.get(i) + " Collapsed", Toast.LENGTH_SHORT).show();
            }
        });
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
//                Toast.makeText(
//                        getApplicationContext(),
//                        listDataHeader.get(i)
//                                + " : "
//                                + listDataChild.get(
//                                listDataHeader.get(i)).get(
//                                i1), Toast.LENGTH_SHORT)
//                        .show();
                return false;
            }
        });
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        lbListheaderImage = new ArrayList<Integer>();
        listDataChild = new HashMap<String, List<String>>();

        lbListheaderImage.add(R.drawable.category);
        lbListheaderImage.add(R.drawable.category);
        lbListheaderImage.add(R.drawable.category);


        // Adding child data
        listDataHeader.add("category1");
        listDataHeader.add("category2");
        listDataHeader.add("category3");

        // Adding child data
        List<String> category1 = new ArrayList<String>();
        category1.add("SubCategory 1");
        category1.add("SubCategory 1");
        category1.add("SubCategory 1");
        category1.add("SubCategory1");
        category1.add("SubCategory1");
        category1.add("SubCategory1");
        category1.add("SubCategory1");

        List<String> category2 = new ArrayList<String>();
        category2.add("SubCategory2");
        category2.add("SubCategory2");
        category2.add("SubCategory2");
        category2.add("SubCategory2");
        category2.add("SubCategory2");
        category2.add("SubCategory2");

        List<String> category3 = new ArrayList<String>();
        category3.add("SubCategory3");
        category3.add("SubCategory2");
        category3.add("SubCategory2");
        category3.add("SubCategory2");
        category3.add("SubCategory2");

        listDataChild.put(listDataHeader.get(0), category1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), category2);
        listDataChild.put(listDataHeader.get(2), category3);
    }

    private void getInitUi() {
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        back_arrow = findViewById(R.id.back_arrow);
        toolbar_title = findViewById(R.id.toolbar_title);

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
