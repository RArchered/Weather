package com.example.weather.db;

import org.litepal.crud.DataSupport;

public class TianqiProvince extends DataSupport {
    int id;
    String provinceZh;

    public String getProvinceZh() {
        return provinceZh;
    }

    public void setProvinceZh(String provinceZh) {
        this.provinceZh = provinceZh;
    }
}
