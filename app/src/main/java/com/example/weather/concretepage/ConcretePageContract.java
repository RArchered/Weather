package com.example.weather.concretepage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weather.BasePresenter;
import com.example.weather.BaseView;
import com.example.weather.gson.weather.Weather;

public interface ConcretePageContract {
    interface View extends BaseView<Presenter> {
        public Activity getActivityForView();
        public void requestWeather(final String cityId);
        public void showWeatherInfo(Weather weather);

        public DrawerLayout getDrawerLayout();
        public SwipeRefreshLayout getSwipeRefresh();
        public ScrollView getWeatherLayout();
        public TextView getTitleCity();
        public TextView getTitleUpdateTime();
        public TextView getDegreeText();
        public TextView getWeatherInfoText();
        public LinearLayout getLayoutForecast();
        public ImageView getBingPicImg();
        public String getmCityId();
        public Button getNavButton();

        public void setmCityId(String mCityId);
    }

    interface Presenter extends BasePresenter {
        public SharedPreferences getDefaultSharedPreferences(Context context);
        public void requestWeather(final String cityId);
        public void loadBingPic();
        public void setBingPicUrl(String bingPicUrl);
    }
}
