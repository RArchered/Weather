package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String cityid;
    public String update_time;
    public String city;
    @SerializedName("data")
    public List<Forecast> forecastList;
}
