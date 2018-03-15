package com.tahahamdan.develop4android.agrisenceweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 100;
    private AdView mAdView;
    private LinearLayout resultLayout,noLocationLayout;
    private TextView infoTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultLayout = (LinearLayout) findViewById(R.id.resultLayout);
        noLocationLayout = (LinearLayout) findViewById(R.id.noLocationLayout);
        infoTxt = (TextView) findViewById(R.id.infoTxt);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitle("Weather HT");
        registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_AVAILABLE"));

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(this);
        if (SP.contains("location"))
        {
            noLocationLayout.setVisibility(View.GONE);
            resultLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            infoTxt.setText(Html.fromHtml("<b>" + "No Location selected" + "</b>" +  "<br />" +
                    "<small>" + "Select a location from top menu using" + "</small>" + "<br />" +
                    "<small>" + "'Choose location' option!" + "</small>"));
            noLocationLayout.setVisibility(View.VISIBLE);
            resultLayout.setVisibility(View.GONE);

        }

        ScreenUtility utility = new ScreenUtility(this);
        if (utility.getShortestWidth() < 720) {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            adapter= setupViewPager(viewPager);
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        }
        mAdView = (AdView) findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                mAdView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);

            }

            @Override
            public void onAdLeftApplication() {

            }

            @Override
            public void onAdOpened() {

            }
        });



    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateData();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manage_location:
                try {
                    Intent intent =new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                    Toast.makeText(getApplicationContext(), "Google Play services is not installed, updated, or enabled!", Toast.LENGTH_SHORT).show();
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                    Toast.makeText(getApplicationContext(), "Google Play Services is not Available!", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.settings:
            Intent i = new Intent(this, MyPreferencesActivity.class);
            startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }


    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("location",place.getName().toString()).commit();
                PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("latitude",String.valueOf(place.getLatLng().latitude)).commit();
                PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("longitude",String.valueOf(place.getLatLng().longitude)).commit();
                noLocationLayout.setVisibility(View.GONE);
                resultLayout.setVisibility(View.VISIBLE);
                UpdateData();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                // TODO: Handle the error.
                Toast.makeText(getApplicationContext(),"Some error occured while requesting the data!", Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void UpdateData()
    {
        fragment_now nowFragment = (fragment_now) getSupportFragmentManager().findFragmentById(R.id.nowFragment);

        if (nowFragment != null)//Two pane main activity
        {
            nowFragment.update();
            fragment_daily dialyFragment = (fragment_daily) getSupportFragmentManager().findFragmentById(R.id.dailyFragment);
            if(dialyFragment!=null)
                dialyFragment.update();
        }
        else {//"single pane main"
            Fragment fragmentNow = adapter.getItem(0);
            if(fragmentNow!=null)
                ((fragment_now) fragmentNow).update();

            Fragment fragmentDaily = adapter.getItem(1);
            if(fragmentDaily!=null)
                ((fragment_daily) fragmentDaily).update();

        }
    }

    private ViewPagerAdapter setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new fragment_now(), "Now");
        adapter.addFragment(new fragment_daily(), "Forecast");
        viewPager.setAdapter(adapter);
        return adapter;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
