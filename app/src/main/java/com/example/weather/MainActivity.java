package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long mExitTime = System.currentTimeMillis() - 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("TAG", "Hello, git!");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("weather", null) != null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        ChooseAreaFragment chooseAreaFragment = (ChooseAreaFragment)
                getSupportFragmentManager().findFragmentById(R.id.choose_area_fragment);
        if (chooseAreaFragment.getCurrentLevel() != ChooseAreaFragment.LEVEL_PROVINCE) {
            chooseAreaFragment.onBackPressed();
        } else if (chooseAreaFragment.getCurrentLevel()
                == ChooseAreaFragment.LEVEL_PROVINCE) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Toast.makeText(this, "再按返回键将退出应用", Toast.LENGTH_SHORT)
                        .show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }
}