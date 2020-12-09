package com.example.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    @SerializedName("conf")
    public Confort confort;

    @SerializedName("sport")
    public Sport sport;

    @SerializedName("cw")
    public CarWash carWash;

    public class Confort {
        public String txt;
    }

    public class Sport {
        public String txt;
    }

    public class CarWash {
        public String txt;
    }
}
