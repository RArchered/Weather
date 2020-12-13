package com.example.weather.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.weather.R;
import com.example.weather.gson.Weather;
import com.example.weather.util.Utility;

public class WeatherAppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.appwidget_weather);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String weatherString = prefs.getString("weather", null);
            if (weatherString != null) {
                //use cache directly
                Weather weather = Utility.handleWeatherResponse(weatherString);
                remoteViews.setTextViewText(R.id.degree_text,
                        weather.forecastList.get(0).tem);
                remoteViews.setTextViewText(R.id.weather_info_text,
                        weather.forecastList.get(0).wea);
                remoteViews.setTextViewText(R.id.city_text,
                        weather.city);
            }
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

}
