package com.example.weather.concretepage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weather.data.TasksRepository;
import com.example.weather.gson.weather.Weather;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ConcretePagePresenter implements ConcretePageContract.Presenter {
    ConcretePageContract.View mConcretePageView;
    TasksRepository mTasksRepository;

    private String bingPicUrl;

    public ConcretePagePresenter(TasksRepository tasksRepository,
                                 ConcretePageContract.View conCretePageView) {
        mConcretePageView = conCretePageView;
        mTasksRepository = tasksRepository;
        mConcretePageView.setPresenter(this);
        mTasksRepository.setPresenter(this);

    }

    public SharedPreferences getDefaultSharedPreferences(Context context) {
        return mTasksRepository.getDefaultSharedPreferences(context);
    }


    public void requestWeather(String cityId) {
        String weatherUrl = "https://tianqiapi.com/api?version=v1" +
                "&appid=89131264&appsecret=nSwK7fA7" +
                "&cityid=" + cityId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Activity activity = mConcretePageView.getActivityForView();
                activity.runOnUiThread(() -> {
                    Toast.makeText(mConcretePageView.getActivityForView(), "获取天气信息失败",
                            Toast.LENGTH_SHORT).show();
                    mConcretePageView.getSwipeRefresh().setRefreshing(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                //Log.d("TAG", "onResponse: "+responseText);
                //use response.body().string() not response.body().toString()......
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //define weather as final, this makes it can be captured by lambda
                final Activity activity = mConcretePageView.getActivityForView();
                activity.runOnUiThread(() -> {
                    if (weather != null) {
                        SharedPreferences.Editor editor = getDefaultSharedPreferences(activity)
                                .edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        //you must set mWeatheId because you will change city in fragment.
                        mConcretePageView.setmCityId(weather.cityid);
                        mConcretePageView.showWeatherInfo(weather);
                    } else {
                        Toast.makeText(activity, "获取天气信息失败!",
                                Toast.LENGTH_SHORT).show();
                    }
                    mConcretePageView.getSwipeRefresh().setRefreshing(false);
                });
            }
        });
        requestBingPic();//refresh bing pic when request weather info.
    }

    public void requestBingPic() {
        mTasksRepository.refreshBingPicUrl();
    }

    public void loadBingPic() {
        final Activity activity = mConcretePageView.getActivityForView();
        SharedPreferences.Editor editor = getDefaultSharedPreferences(activity).edit();
        editor.putString("bing_pic", bingPicUrl);
        editor.apply();
        activity.runOnUiThread(() -> {
            Glide.with(activity).load(bingPicUrl).into(mConcretePageView.getBingPicImg());
        });
    }

    public void setBingPicUrl(String bingPicUrl) {
        this.bingPicUrl = bingPicUrl;
    }

}
