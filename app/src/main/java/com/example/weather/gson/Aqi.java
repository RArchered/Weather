package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Aqi {
    @SerializedName("city")
    public AqiCity aqiCity;
    //the field in response JSON is "city", not aqiCity,
    //so use SerializedName

    public class AqiCity {
        public String aqi;
        public String pm25;
    }

}
