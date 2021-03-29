package com.example.weather.data.db;

import org.litepal.crud.DataSupport;

public class TianqiCity extends DataSupport {
    int id;
    String cityId;
    String cityZh;
    String leaderZh;

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityZh() {
        return cityZh;
    }

    public void setCityZh(String cityZh) {
        this.cityZh = cityZh;
    }

    public String getLeaderZh() {
        return leaderZh;
    }

    public void setLeaderZh(String leaderZh) {
        this.leaderZh = leaderZh;
    }
}
