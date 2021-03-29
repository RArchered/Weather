package com.example.weather.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.example.weather.data.db.TianqiCity;
import com.example.weather.data.db.TianqiLeader;
import com.example.weather.data.db.TianqiProvince;
import com.example.weather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Utility {

    public static Weather handleWeatherResponse(String response) {
        try {
            return new Gson().fromJson(response, Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTianqiCityJson(String filename, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(assetManager.open(filename)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static boolean handleTianqiCity(Context context) {
        String tiqianCity = getTianqiCityJson("tianqi_city.json", context);
        if (!TextUtils.isEmpty(tiqianCity)) {
            try {
                JSONArray allCitys = new JSONArray(tiqianCity);
                String provinceZh_old = "";
                String leaderZh_old = "";
                String cityZh_old = "";
                String cityId_old = "";
                String leaderZh_new = "";
                String cityZh_new = "";
                String cityId_new = "";
                String provinceZh_new = "";
                for (int i = 0; i < allCitys.length(); i++) {
                    JSONObject cityObject = allCitys.getJSONObject(i);
                    provinceZh_old = provinceZh_new;
                    leaderZh_old = leaderZh_new;
                    cityZh_old = cityZh_new;
                    cityId_old = cityId_new;
                    provinceZh_new = cityObject.getString("provinceZh");
                    leaderZh_new = cityObject.getString("leaderZh");
                    cityZh_new = cityObject.getString("cityZh");
                    cityId_new = cityObject.getString("id");
                    if (!provinceZh_new.equals(provinceZh_old)) {
                        saveProvince(provinceZh_new);
                        saveLeader(provinceZh_new, leaderZh_new);
                        saveCity(leaderZh_new, cityZh_new, cityId_new);
                    } else if (!leaderZh_new.equals(leaderZh_old)) {
                        //chongqing, tianjing, shanghai should be considered separately
                        //to avoid insert same leader more than one time,
                        //example: cq cq xs, cq jlp jlp, cq cq sz.
                        if (leaderZh_new.equals("重庆")
                            || leaderZh_new.equals("天津")
                            || leaderZh_new.equals("上海")) {
                            List<TianqiLeader> leaders = DataSupport.where(
                                    "leaderZh = ?", leaderZh_new)
                                    .find(TianqiLeader.class);
                            if (leaders.isEmpty()) {
                                saveLeader(provinceZh_new, leaderZh_new);
                            }
                        } else {
                            saveLeader(provinceZh_new, leaderZh_new);
                            saveCity(leaderZh_new, cityZh_new, cityId_new);
                        }
                    } else if (!cityId_new.equals(cityId_old)) {
                        saveCity(leaderZh_new, cityZh_new, cityId_new);
                    }
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    return false;
    }

    private static void saveProvince(String provinceZh_new) {
        //save province
        TianqiProvince province = new TianqiProvince();
        province.setProvinceZh(provinceZh_new);
        province.save();
    }

    private static void saveLeader(String provinceZh_new, String leaderZh_new ) {
        //save leader
        TianqiLeader leader = new TianqiLeader();
        leader.setProvinceZh(provinceZh_new);
        leader.setLeaderZh(leaderZh_new);
        leader.save();
    }

    private static void saveCity(String leaderZh_new, String cityZh_new, String cityId_new) {
        //save city
        TianqiCity city = new TianqiCity();
        city.setLeaderZh(leaderZh_new);
        city.setCityZh(cityZh_new);
        city.setCityId(cityId_new);
        city.save();
    }
}
