package com.example.weather.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.weather.concretepage.ConcretePageContract;
import com.example.weather.data.db.TianqiCity;
import com.example.weather.data.db.TianqiLeader;
import com.example.weather.data.db.TianqiProvince;
import com.example.weather.gson.bingpic.BingPic;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TasksRepository {
    private static TasksRepository tasksRepository = new TasksRepository();
    private ConcretePageContract.Presenter mConcretePagePresenter;

    private TasksRepository() {
    }

    public void setPresenter(ConcretePageContract.Presenter presenter) {
        this.mConcretePagePresenter = presenter;
    }

    public static TasksRepository getInstance() {
        return tasksRepository;
    }

    public SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public List<TianqiProvince> getProvinceList() {
        return DataSupport.findAll(TianqiProvince.class);
    }

    public List<TianqiLeader> getLeaderList(TianqiProvince province) {
        return DataSupport.where("provinceZh = ?",
                String.valueOf(province.getProvinceZh())).find(TianqiLeader.class);
    }

    public List<TianqiCity> getCityList(TianqiLeader leader) {
        return DataSupport.where("leaderZh = ?",
                String.valueOf(leader.getLeaderZh())).find(TianqiCity.class);
    }

    public void refreshBingPicUrl() {
        String url = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";

        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPicString = response.body().string();
                BingPic bingPic = Utility.handleBingPicResponse(bingPicString);
                String headPic = "https://cn.bing.com/";
                mConcretePagePresenter.setBingPicUrl(headPic + bingPic.images.get(0).url);
                mConcretePagePresenter.loadBingPic();
            }
        });
    }

}