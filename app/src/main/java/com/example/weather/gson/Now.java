package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public MoreInfo moreInfo;

    public class MoreInfo {
        @SerializedName("txt")
        public String info;
    }
}
