package com.example.weather.concretepage;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.weather.R;
import com.example.weather.appwidget.WeatherAppWidget;
import com.example.weather.data.TasksRepository;
import com.example.weather.gson.Forecast;
import com.example.weather.gson.Weather;
import com.example.weather.service.AutoUpdateService;
import com.example.weather.startpage.StartPageFragment;
import com.example.weather.startpage.StartPagePresenter;
import com.example.weather.util.Utility;

public class ConcretePageActivity extends AppCompatActivity
        implements ConcretePageContract.View {

    private ConcretePageContract.Presenter mConcretePagePresenter;
    private StartPagePresenter mStartPagePresenter;

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
        setContentView(R.layout.concrete_page_activity);
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

        //create two presenter, one for fragment, one for this activity.
        new ConcretePagePresenter(TasksRepository.getInstance(), this);
        mStartPagePresenter = new StartPagePresenter(TasksRepository.getInstance(),
                (StartPageFragment) (getSupportFragmentManager()
                        .findFragmentById(R.id.start_page_fragment)));

        SharedPreferences prefs = mConcretePagePresenter.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            //use cache directly
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mCityId = weather.cityid;
            showWeatherInfo(weather);
        } else {
            mCityId = getIntent().getStringExtra("city_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            mConcretePagePresenter.requestWeather(mCityId);
        }
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            mConcretePagePresenter.loadBingPic();
        }
        swipeRefresh.setOnRefreshListener(() -> {
            //update weather based on current mWeatherId,
            //you should update the value in method "requestWeather"
            //because you will call this method in fragment.
            mConcretePagePresenter.requestWeather(mCityId);
        });
        navButton.setOnClickListener((v) -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });
    }

    @Override
    public void onBackPressed() {
        StartPageFragment startPageFragment = (StartPageFragment)
                getSupportFragmentManager().findFragmentById(R.id.start_page_fragment);
        if (drawerLayout.isOpen()
                && mStartPagePresenter.getCurrentLevel() != StartPagePresenter.LEVEL_PROVINCE) {
            mStartPagePresenter.onBackPressed();
        } else if (drawerLayout.isOpen()
                && mStartPagePresenter.getCurrentLevel() == StartPagePresenter.LEVEL_PROVINCE) {
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

    public void showWeatherInfo(Weather weather) {
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
        //update widget when refresh
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(),
                R.layout.appwidget_weather);
        remoteViews.setTextViewText(R.id.degree_text,
                weather.forecastList.get(0).tem);
        remoteViews.setTextViewText(R.id.weather_info_text,
                weather.forecastList.get(0).wea);
        remoteViews.setTextViewText(R.id.city_text,
                weather.city);
        ComponentName thisWidget = new ComponentName(this, WeatherAppWidget.class);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    public void requestWeather(final String cityId) {
        mConcretePagePresenter.requestWeather(cityId);
    }

    @Override
    public void setPresenter(ConcretePageContract.Presenter presenter) {
        mConcretePagePresenter = presenter;
    }

    @Override
    public Activity getActivityForView() {
        return this;
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public SwipeRefreshLayout getSwipeRefresh() {
        return swipeRefresh;
    }

    public ScrollView getWeatherLayout() {
        return weatherLayout;
    }

    public TextView getTitleCity() {
        return titleCity;
    }

    public TextView getTitleUpdateTime() {
        return titleUpdateTime;
    }

    public TextView getDegreeText() {
        return degreeText;
    }

    public TextView getWeatherInfoText() {
        return weatherInfoText;
    }

    public LinearLayout getLayoutForecast() {
        return layoutForecast;
    }

    public ImageView getBingPicImg() {
        return bingPicImg;
    }

    public String getmCityId() {
        return mCityId;
    }

    public Button getNavButton() {
        return navButton;
    }

    public void setmCityId(String mCityId) {
        this.mCityId = mCityId;
    }
}
