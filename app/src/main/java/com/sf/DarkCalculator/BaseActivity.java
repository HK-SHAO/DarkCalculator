package com.sf.DarkCalculator;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {


    public static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public Resources getResources() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Resources res = super.getResources();
        Configuration config = res.getConfiguration();
        config.fontScale = preferences.getFloat("textSize", 1);
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}
