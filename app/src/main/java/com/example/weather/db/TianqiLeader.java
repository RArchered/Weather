package com.example.weather.db;

import org.litepal.crud.DataSupport;

public class TianqiLeader extends DataSupport {
    int id;
    String leaderZh;
    String provinceZh;

    public String getLeaderZh() {
        return leaderZh;
    }

    public void setLeaderZh(String leaderZh) {
        this.leaderZh = leaderZh;
    }

    public String getProvinceZh() {
        return provinceZh;
    }

    public void setProvinceZh(String provinceZh) {
        this.provinceZh = provinceZh;
    }
}
