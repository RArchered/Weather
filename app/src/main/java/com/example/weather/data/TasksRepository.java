package com.example.weather.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.weather.data.db.TianqiCity;
import com.example.weather.data.db.TianqiLeader;
import com.example.weather.data.db.TianqiProvince;

import org.litepal.crud.DataSupport;

import java.util.List;

public class TasksRepository {
    private static TasksRepository tasksRepository = new TasksRepository();

    private TasksRepository() {
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
}