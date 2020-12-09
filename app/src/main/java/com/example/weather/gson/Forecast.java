package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public MoreInfo moreInfo;

    public class Temperature {
        public String max;
        public String min;
    }

    public class MoreInfo {
        @SerializedName("txt_d")
        public String info;
    }
}
