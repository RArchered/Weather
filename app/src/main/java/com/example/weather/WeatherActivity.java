package com.example.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.weather.gson.Forecast;
import com.example.weather.gson.Weather;
import com.example.weather.service.AutoUpdateService;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public SwipeRefreshLayout swipeRefresh;

    private long mExitTime = System.currentTimeMillis() - 2000;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout layoutForecast;
    private ImageView bingPicImg;
    private String mCityId;
    private Button navButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //initialize all controls
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        layoutForecast = (LinearLayout) findViewById(R.id.layout_forecast);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            //use cache directly
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mCityId = weather.cityid;
            showWeatherInfo(weather);
        } else {
            mCityId = getIntent().getStringExtra("city_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mCityId);
        }
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
        swipeRefresh.setOnRefreshListener(() -> {
            //update weather based on current mWeatherId,
            //you should update the value in method "requestWeather"
            //because you will call this method in fragment.
            requestWeather(mCityId);
        });
        navButton.setOnClickListener((v) -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    @Override
    public void onBackPressed() {
        ChooseAreaFragment chooseAreaFragment = (ChooseAreaFragment)
                getSupportFragmentManager().findFragmentById(R.id.choose_area_fragment);
        if (drawerLayout.isOpen()
            && chooseAreaFragment.getCurrentLevel() != ChooseAreaFragment.LEVEL_PROVINCE) {
            chooseAreaFragment.onBackPressed();
        } else if (drawerLayout.isOpen()
            && chooseAreaFragment.getCurrentLevel() == ChooseAreaFragment.LEVEL_PROVINCE) {
            drawerLayout.closeDrawers();
        } else if (!drawerLayout.isOpen()) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Toast.makeText(this, "再按返回键将退出应用", Toast.LENGTH_SHORT)
                        .show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

    public void requestWeather(final String cityId) {
        String weatherUrl = "https://tianqiapi.com/api?version=v1" +
                "&appid=89131264&appsecret=nSwK7fA7" +
                "&cityid=" + cityId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(WeatherActivity.this, "获取天气信息失败",
                            Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                //Log.d("TAG", "onResponse: "+responseText);
                //use response.body().string() not response.body().toString()......
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //define weather as final, this makes it can be captured by lambda
                runOnUiThread(() -> {
                    if (weather != null) {
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(WeatherActivity.this).
                                edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        //you must set mWeatheId because you will change city in fragment.
                        mCityId = weather.cityid;
                        showWeatherInfo(weather);
                    } else {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败!",
                                Toast.LENGTH_SHORT).show();
                    }
                    swipeRefresh.setRefreshing(false);
                });
            }
        });
        loadBingPic();//refresh bing pic when request weather info.
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.city;
        String updateTime = weather.update_time;
        String degree = weather.forecastList.get(0).tem;
        String weatherInfo = weather.forecastList.get(0).wea;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        layoutForecast.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    layoutForecast, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.day);
            infoText.setText(forecast.wea);
            maxText.setText(forecast.tem1);
            minText.setText(forecast.tem2);
            layoutForecast.addView(view);
        }
        weatherLayout.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(() -> {
                    Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                });
            }
        });
    }
}
