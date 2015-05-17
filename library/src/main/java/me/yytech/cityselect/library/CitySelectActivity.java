package me.yytech.cityselect.library;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;


public class CitySelectActivity extends ActionBarActivity implements CityListView.OnCitySelect {

    public static final String RESULT_CITY = "cityname";
    private CityListView mLvCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_select);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLvCity = (CityListView) findViewById(R.id.vCity);
        mLvCity.setOnCitySelect(this);
    }

    public static void startCitySelect(Activity pActivity, int requestCode) {
        Intent intent = new Intent(pActivity, CitySelectActivity.class);
        pActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelect(String city) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_CITY, city);
        setResult(RESULT_OK, intent);
    }
}
